package bank.persistence;

import bank.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Loads/saves {@link User} objects to data/users.txt.
 * Line format: username|saltHex|hashHex   (never the plaintext password).
 */
public class UserRepository {

    private static final String FILE_PATH = "data/users.txt";
    private static final String SPLIT_DELIMITER = "\\|";
    private static final String JOIN_DELIMITER = "|";

    private final List<User> users;

    public UserRepository() {
        this.users = loadFromDisk();
    }

    private List<User> loadFromDisk() {
        List<User> result = new ArrayList<>();
        for (String line : FileManager.readLines(FILE_PATH)) {
            String[] parts = line.split(SPLIT_DELIMITER, -1);
            if (parts.length == 3) {
                result.add(new User(parts[0], parts[1], parts[2]));
            }
        }
        return result;
    }

    private void persist() {
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(String.join(JOIN_DELIMITER, u.getUsername(), u.getPasswordSalt(), u.getPasswordHash()));
        }
        FileManager.writeLines(FILE_PATH, lines);
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public Optional<User> findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public void save(User user) {
        users.add(user);
        persist();
    }
}
