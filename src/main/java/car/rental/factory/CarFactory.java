package car.rental.factory;

import car.rental.cars.Car;
import car.rental.cars.CarType;

public interface CarFactory {

    /**
     * Creates the instance of car factory.
     *
     * @return car factory instance
     */
    static CarFactory newInstance() {

        return new CarFactoryImpl();
    }

    /**
     * Constructs the Car instance depending on car type and registration number.
     *
     * @param carType the type of car
     * @param registrationNumber registration number as string
     * @return constructed instance of car
     */
    Car constructCar(CarType carType, String registrationNumber);
}
