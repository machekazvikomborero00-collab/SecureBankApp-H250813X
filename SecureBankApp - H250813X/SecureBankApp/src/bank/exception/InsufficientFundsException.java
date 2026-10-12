package bank.exception;

/** Thrown when a withdrawal amount exceeds the account's current balance. */
public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(String message) {
        super(message);
    }
}
