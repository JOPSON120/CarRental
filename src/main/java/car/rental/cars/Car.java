package car.rental.cars;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

@RequiredArgsConstructor
public abstract class Car {

    private static final long DAY_SECONDS = 86400000L;

    @Getter
    private final String registrationNumber;
    private boolean reserved;
    private long reservationTimestamp;
    private int days;

    /**
     * Gets the type of this car.
     *
     * @return appropriate car type
     * @see CarType
     */
    public abstract CarType getCarType();

    /**
     * Gets the reservation description.
     *
     * @return information about reservation in specific date time format or information about not being reserved
     */
    public String describeReservation() {

        if (isReserved()) {
            return "RESERVED UNTIL: %s".formatted(new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(
                    new Date(reservationTimestamp + DAY_SECONDS * days)
            ));
        }
        return "NOT RESERVED";
    }

    /**
     * Cancels reservation of the car and marks this car as ready to remove from rented cars list in rental.
     */
    public synchronized void cancelReservation() {

        reserved = false;
    }

    /**
     * Marks this car as currently under reservation.
     *
     * @param reservationTimestamp the millis epoch representing reservation start date
     * @param days number of days for which reservation is booked
     */
    public synchronized void setReservationDate(long reservationTimestamp, int days) {

        reserved = true;
        this.reservationTimestamp = reservationTimestamp;
        this.days = days;
    }

    /**
     * Checks if this car is currently rented.
     *
     * @return true if car is rented, false otherwise
     */
    public boolean isReserved() {

        return reserved && reservationTimestamp + days * DAY_SECONDS > System.currentTimeMillis();
    }
}
