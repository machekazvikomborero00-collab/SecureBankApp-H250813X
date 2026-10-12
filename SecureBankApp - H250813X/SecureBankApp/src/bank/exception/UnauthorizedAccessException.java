package bank.exception;

/** Thrown when a logged-in user tries to act on an account they do not own. */
public class UnauthorizedAccessException extends Exception {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
