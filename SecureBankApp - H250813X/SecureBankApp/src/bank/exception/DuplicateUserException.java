package bank.exception;

/** Thrown when registration is attempted with a username that already exists. */
public class DuplicateUserException extends Exception {
    public DuplicateUserException(String message) {
        super(message);
    }
}
