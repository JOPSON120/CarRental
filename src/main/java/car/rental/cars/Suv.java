package car.rental.cars;

public final class Suv extends Car {

    public Suv(String registrationNumber) {

        super(registrationNumber);
    }

    @Override
    public CarType getCarType() {

        return CarType.SUV;
    }
}
