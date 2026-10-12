package bank.service;

import bank.exception.AuthenticationException;
import bank.exception.DuplicateUserException;
import bank.model.User;
import bank.persistence.UserRepository;
import bank.security.PasswordUtils;

/**
 * Registration and login. This class never touches a file directly —
 * it delegates all storage to {@link UserRepository}.
 */
public class AuthService {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String password) throws DuplicateUserException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (username.contains("|")) {
            throw new IllegalArgumentException("Username cannot contain the '|' character.");
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new DuplicateUserException("Username '" + username + "' is already taken.");
        }

        String salt = PasswordUtils.generateSalt();
        String hash = PasswordUtils.hash(password, salt);
        User user = new User(username, salt, hash);
        userRepository.save(user);
        return user;
    }

    public User login(String username, String password) throws AuthenticationException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("Invalid username or password."));

        // Same generic failure message whether the username or the
        // password was wrong — never confirm which part was incorrect.
        if (!PasswordUtils.verify(password, user.getPasswordSalt(), user.getPasswordHash())) {
            throw new AuthenticationException("Invalid username or password.");
        }
        return user;
    }
}
