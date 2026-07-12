package hotelapp.exception;

/**
 * Thrown when a booking is attempted on a room that is not
 * currently available (occupied / under maintenance).
 * Used later by the Booking form.
 */
public class RoomNotAvailableException extends Exception {

    public RoomNotAvailableException(String message) {
        super(message);
    }
}
