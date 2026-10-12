package bank.persistence;

import bank.model.Transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Appends {@link Transaction} records to data/transactions.txt.
 * Line format: id|accountNumber|type|amount|timestamp
 *
 * Transactions are immutable and never edited after creation, so unlike
 * the other repositories this one only ever appends — it doesn't need
 * to rewrite the whole file on every save.
 */
public class TransactionRepository {

    private static final String FILE_PATH = "data/transactions.txt";
    private static final String SPLIT_DELIMITER = "\\|";
    private static final String JOIN_DELIMITER = "|";
    private static final String ID_PREFIX = "TX";

    private int nextId;

    public TransactionRepository() {
        this.nextId = computeNextId();
    }

    private int computeNextId() {
        int max = 0;
        for (String line : FileManager.readLines(FILE_PATH)) {
            String[] parts = line.split(SPLIT_DELIMITER, -1);
            if (parts.length == 5) {
                try {
                    int value = Integer.parseInt(parts[0].replace(ID_PREFIX, ""));
                    if (value > max) {
                        max = value;
                    }
                } catch (NumberFormatException ignored) {
                    // skip a malformed row
                }
            }
        }
        return max + 1;
    }

    public List<Transaction> findByAccount(String accountNumber) {
        List<Transaction> result = new ArrayList<>();
        for (String line : FileManager.readLines(FILE_PATH)) {
            String[] parts = line.split(SPLIT_DELIMITER, -1);
            if (parts.length == 5 && parts[1].equalsIgnoreCase(accountNumber)) {
                try {
                    result.add(new Transaction(parts[0], parts[1], parts[2],
                            Double.parseDouble(parts[3]), LocalDateTime.parse(parts[4])));
                } catch (NumberFormatException | java.time.format.DateTimeParseException ignored) {
                    // skip a malformed row
                }
            }
        }
        return result;
    }

    /** Creates, persists, and returns a new transaction record. */
    public Transaction save(String accountNumber, String type, double amount) {
        String id = String.format("%s%04d", ID_PREFIX, nextId++);
        Transaction transaction = new Transaction(id, accountNumber, type, amount, LocalDateTime.now());
        String line = String.join(JOIN_DELIMITER,
                transaction.getId(),
                transaction.getAccountNumber(),
                transaction.getType(),
                String.valueOf(transaction.getAmount()),
                transaction.formattedTimestamp());
        FileManager.appendLine(FILE_PATH, line);
        return transaction;
    }
}
