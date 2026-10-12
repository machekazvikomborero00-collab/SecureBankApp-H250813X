package bank.exception;

/**
 * Thrown when a login attempt fails. The message is intentionally generic
 * ("Invalid username or password") so it never reveals whether the
 * username or the password was the wrong one.
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
