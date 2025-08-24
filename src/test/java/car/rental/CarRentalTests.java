package car.rental;

import car.rental.cars.Car;
import car.rental.cars.CarType;
import car.rental.cars.Sedan;
import car.rental.config.CarConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CarRentalTests {

    private CarRental rental;

    @BeforeEach
    void setup() {

        rental = new CarRental();
    }

    @Test
    void testCorrectCarType() {

        Car car = rental.rentCar(CarType.SEDAN, LocalDate.now(), LocalTime.now(), 2);
        assertNotNull(car, "Failed to sent a car");
        assertInstanceOf(Sedan.class, car, "Rented car is wrong type");
    }

    @Test
    void testCarReservedCorrectly() {

        LocalDate rentDate = LocalDate.now();
        LocalTime rentTime = LocalTime.of(9, 0, 0);
        Date endDate = Date.from(rentDate.plusDays(2).atTime(rentTime).atZone(ZoneId.systemDefault()).toInstant());
        Car car = rental.rentCar(CarType.VAN, rentDate, rentTime, 2);
        assertNotNull(car, "Failed to sent a car");
        assertTrue(car.isReserved(), "Car not reserved");
        assertEquals("RESERVED UNTIL: %s".formatted(new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(endDate)), car.describeReservation());

    }

    @Test
    void testReachedReservationLimit() {

        CarConfiguration configuration = rental.getConfiguration(CarType.SUV);
        for (int i = 0; i < configuration.limit(); i++) {
            assertNotNull(rental.rentCar(CarType.SUV, LocalDate.now(), LocalTime.now(), 2), "Failed to rent a car while limit is not reached");
        }
        assertNull(rental.rentCar(CarType.SUV, LocalDate.now(), LocalTime.now(), 2), "Unexpectedly, car rented above a limit");
    }

    @Test
    void testUniqueRegistrationNumbers() {

        List<Car> cars = new ArrayList<>();
        for (CarType type: CarType.values()) {
            CarConfiguration configuration = rental.getConfiguration(type);
            for (int i = 0; i < configuration.limit(); i++) {
                Car car = rental.rentCar(type, LocalDate.now(), LocalTime.now(), 1);
                assertNotNull(car);
                cars.add(car);
            }
        }
        Set<String> registrations = new HashSet<>(cars.stream().map(Car::getRegistrationNumber).toList());
        assertEquals(cars.size(), registrations.size(), "There are some duplicates in cars registration numbers");
    }

    @Test
    void testCancellingFreeingCar() {

        Car lastCar = null;
        CarConfiguration configuration = rental.getConfiguration(CarType.SUV);
        for (int i = 0; i < configuration.limit(); i++) {
            lastCar = rental.rentCar(CarType.SUV, LocalDate.now(), LocalTime.now(), 2);
            assertNotNull(lastCar);
        }
        assertNotNull(lastCar);
        lastCar.cancelReservation();
        assertFalse(lastCar.isReserved(), "Reservation not cancelled");
        assertNotNull(rental.rentCar(CarType.SUV, LocalDate.now(), LocalTime.now(), 2), "Reservation should be successful after freeing car");
    }
}
