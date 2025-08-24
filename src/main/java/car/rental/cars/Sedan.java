package car.rental.cars;

public final class Sedan extends Car {

    public Sedan(String registrationNumber) {

        super(registrationNumber);
    }

    @Override
    public CarType getCarType() {

        return CarType.SEDAN;
    }
}
