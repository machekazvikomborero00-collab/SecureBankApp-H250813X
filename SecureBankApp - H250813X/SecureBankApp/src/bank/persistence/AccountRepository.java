package bank.persistence;

import bank.model.Account;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loads/saves {@link Account} objects to data/accounts.txt.
 * Line format: accountNumber|ownerUsername|balance
 *
 * Accounts are kept as an in-memory list; findByAccountNumber() returns
 * the same object reference held in that list, so a service can mutate
 * the balance via account.deposit()/withdraw() and then call update()
 * to flush the new state to disk.
 */
public class AccountRepository {

    private static final String FILE_PATH = "data/accounts.txt";
    private static final String SPLIT_DELIMITER = "\\|";
    private static final String JOIN_DELIMITER = "|";
    private static final String ACCOUNT_PREFIX = "ACC";
    private static final int STARTING_NUMBER = 1000;

    private final List<Account> accounts;

    public AccountRepository() {
        this.accounts = loadFromDisk();
    }

    private List<Account> loadFromDisk() {
        List<Account> result = new ArrayList<>();
        for (String line : FileManager.readLines(FILE_PATH)) {
            String[] parts = line.split(SPLIT_DELIMITER, -1);
            if (parts.length == 3) {
                try {
                    result.add(new Account(parts[0], parts[1], Double.parseDouble(parts[2])));
                } catch (NumberFormatException ignored) {
                    // skip a malformed row rather than crash the whole load
                }
            }
        }
        return result;
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Account a : accounts) {
            lines.add(String.join(JOIN_DELIMITER, a.getAccountNumber(), a.getOwnerUsername(),
                    String.valueOf(a.getBalance())));
        }
        FileManager.writeLines(FILE_PATH, lines);
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts);
    }

    public List<Account> findByOwner(String username) {
        List<Account> result = new ArrayList<>();
        for (Account a : accounts) {
            if (a.getOwnerUsername().equalsIgnoreCase(username)) {
                result.add(a);
            }
        }
        return result;
    }

    public Optional<Account> findByAccountNumber(String accountNumber) {
        for (Account a : accounts) {
            if (a.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                return Optional.of(a);
            }
        }
        return Optional.empty();
    }

    /** Generates the next sequential account number, e.g. ACC1001, ACC1002, ... */
    public String nextAccountNumber() {
        int max = STARTING_NUMBER;
        for (Account a : accounts) {
            String numericPart = a.getAccountNumber().replace(ACCOUNT_PREFIX, "");
            try {
                int value = Integer.parseInt(numericPart);
                if (value > max) {
                    max = value;
                }
            } catch (NumberFormatException ignored) {
                // account number didn't follow the expected pattern — skip it
            }
        }
        return ACCOUNT_PREFIX + (max + 1);
    }

    public void save(Account account) {
        accounts.add(account);
        persist();
    }

    /** Flushes the current in-memory state (e.g. after a deposit/withdraw) to disk. */
    public void update(Account account) {
        persist();
    }
}
