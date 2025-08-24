package car.rental.cars;

public final class Van extends Car {

    public Van(String registrationNumber) {

        super(registrationNumber);
    }

    @Override
    public CarType getCarType() {

        return CarType.VAN;
    }
}
