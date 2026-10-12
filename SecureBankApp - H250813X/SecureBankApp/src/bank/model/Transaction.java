package bank.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * An immutable record of a single deposit or withdrawal.
 * All fields are set once in the constructor — there are no setters.
 */
public class Transaction {

    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String id;
    private final String accountNumber;
    private final String type; // "DEPOSIT" or "WITHDRAW"
    private final double amount;
    private final LocalDateTime timestamp;

    public Transaction(String id, String accountNumber, String type, double amount, LocalDateTime timestamp) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String formattedTimestamp() {
        return timestamp.format(TIMESTAMP_FORMAT);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-8s %10.2f  %s  (account %s)",
                id, type, amount, formattedTimestamp(), accountNumber);
    }
}
