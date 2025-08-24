package car.rental;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import car.rental.cars.Car;
import car.rental.cars.CarType;
import car.rental.config.CarConfiguration;
import car.rental.factory.CarFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class CarRental {

    private final List<Car> rentedCars = new ArrayList<>();
    private final Map<CarType, CarConfiguration> configurationMap;

    public CarConfiguration getConfiguration(CarType carType) {

        return configurationMap.get(carType);
    }

    @SneakyThrows
    CarRental() {

        ObjectMapper mapper = new ObjectMapper();
        URL config = ClassLoader.getSystemResource("rental-config.json");
        if (config == null) {
            throw new RuntimeException("Car rental configuration file not found");
        }
        configurationMap = mapper.readValue(config, mapper.getTypeFactory().constructMapType(HashMap.class, CarType.class, CarConfiguration.class));
        configurationMap.values().forEach(configuration -> {
            if (configuration.limit() != configuration.registrationNumbers().size()) {
                throw new IllegalStateException("Car limit for some type is %d, but number of registrations is: %d".formatted(
                        configuration.limit(),
                        configuration.registrationNumbers().size()
                ));
            }
        });
        if (!Arrays.stream(CarType.values()).allMatch(configurationMap::containsKey)) {
            throw new IllegalStateException("Some of car types are not configured in json file");
        }
    }

    /**
     * @see #rentCar(CarType, LocalDate, LocalTime, ZoneId, int)
     */
    public synchronized Car rentCar(CarType carType, LocalDate date, LocalTime time, int days) {

        return rentCar(carType, date, time, ZoneId.systemDefault(), days);
    }

    /**
     * Rents the car in the rental.
     *
     * @param carType the type of car
     * @param date start date of car reservation
     * @param time start time of car reservation
     * @param zoneId time zone
     * @param days number of days for car reservation
     * @return car instance of null if car rental did not success
     */
    public synchronized Car rentCar(CarType carType, LocalDate date, LocalTime time, ZoneId zoneId, int days) {

        updateCars();
        CarConfiguration configuration = configurationMap.get(carType);
        List<Car> typedCars = rentedCars.stream().filter(car -> car.getCarType() == carType).toList();
        if (typedCars.size() == configuration.limit()) {
            return null;
        }
        List<String> availableRegistrations = new ArrayList<>(configuration.registrationNumbers());
        availableRegistrations.removeAll(typedCars.stream().map(Car::getRegistrationNumber).toList());
        Car rentedCar = CarFactory.newInstance().constructCar(carType, availableRegistrations.getFirst());
        rentedCar.setReservationDate(date.atTime(time).atZone(zoneId).toInstant().toEpochMilli(), days);
        rentedCars.add(rentedCar);
        return rentedCar;
    }

    private void updateCars() {

        rentedCars.removeIf(car -> !car.isReserved());
    }
}
