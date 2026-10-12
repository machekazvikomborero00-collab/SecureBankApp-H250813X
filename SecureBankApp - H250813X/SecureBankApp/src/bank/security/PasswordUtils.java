package bank.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Salted password hashing built only on the standard library
 * (java.security), so the project needs no external dependency.
 *
 * Passwords are never stored or compared as plaintext: a random salt is
 * generated per user, and SHA-256(salt + password) is what gets persisted.
 */
public final class PasswordUtils {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH_BYTES = 16;

    private PasswordUtils() {
        // utility class — no instances
    }

    /** Generates a new random salt, hex-encoded for safe storage as text. */
    public static String generateSalt() {
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        RANDOM.nextBytes(salt);
        return toHex(salt);
    }

    /** Hashes {@code password} with {@code saltHex} using SHA-256. */
    public static String hash(String password, String saltHex) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(fromHex(saltHex));
            byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return toHex(hashed);
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is guaranteed to be available on every standard JVM,
            // so this can only happen if the runtime itself is broken.
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    /** Verifies a login attempt against the stored salt and hash. */
    public static boolean verify(String passwordAttempt, String saltHex, String expectedHash) {
        String actualHash = hash(passwordAttempt, saltHex);
        return constantTimeEquals(actualHash, expectedHash);
    }

    /**
     * Compares two hashes without short-circuiting on the first
     * mismatched character, to avoid leaking timing information.
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] fromHex(String hex) {
        int len = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return out;
    }
}
