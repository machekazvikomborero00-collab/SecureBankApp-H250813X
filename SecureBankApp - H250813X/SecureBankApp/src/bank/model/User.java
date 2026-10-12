package bank.model;

/**
 * A registered user. Only the salted password hash is ever stored or
 * held in memory here — the plaintext password is never kept.
 */
public class User {

    private final String username;
    private final String passwordSalt;
    private final String passwordHash;

    public User(String username, String passwordSalt, String passwordHash) {
        this.username = username;
        this.passwordSalt = passwordSalt;
        this.passwordHash = passwordHash;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
