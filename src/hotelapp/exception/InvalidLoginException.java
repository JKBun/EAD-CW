package hotelapp.exception;

/**
 * Thrown when a user enters a username/password combination that
 * does not match any record in the database.
 *
 * This is the user-defined exception required by the coursework
 * (validation and exception handling criteria).
 */
public class InvalidLoginException extends Exception {

    public InvalidLoginException(String message) {
        super(message);
    }

    public InvalidLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
