package car.rental.factory;

import car.rental.cars.*;

public class CarFactoryImpl implements CarFactory {

    @Override
    public Car constructCar(CarType carType, String registrationNumber) {

        return switch (carType) {
            case SUV -> new Suv(registrationNumber);
            case SEDAN -> new Sedan(registrationNumber);
            case VAN -> new Van(registrationNumber);
        };
    }
}
