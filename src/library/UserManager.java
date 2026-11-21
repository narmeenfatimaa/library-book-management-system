package library;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;

/**
 * Holds the accounts and handles registration and login.
 * <p>
 * Passwords are never stored. Each account stores a random salt and the SHA-256
 * hash of the salt followed by the password. Students can create their own
 * account; the staff account is created by the system.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class UserManager {

    /** The username of the staff account that is created automatically. */
    public static final String DEFAULT_STAFF_USERNAME = "librarian";

    /** The password of the staff account that is created automatically. */
    public static final String DEFAULT_STAFF_PASSWORD = "mcs2026";

    /** The shortest password that is accepted. */
    public static final int MIN_PASSWORD_LENGTH = 6;

    private final ArrayList<User> users = new ArrayList<>();

    /**
     * Creates a manager without accounts.
     */
    public UserManager() {
        // The account list starts empty.
    }

    /**
     * Creates the default staff account if there is no staff account yet.
     *
     * @return true if the account was created, false if a staff account already existed
     */
    public boolean ensureDefaultStaff() {
        for (User user : users) {
            if (user.getRole() == Role.STAFF) {
                return false;
            }
        }
        String salt = newSalt();
        users.add(new User(DEFAULT_STAFF_USERNAME, "Head Librarian", Role.STAFF, salt,
                hashPassword(salt, DEFAULT_STAFF_PASSWORD)));
        return true;
    }

    /**
     * Creates a new student account after validating the input.
     *
     * @param fullName        the full name (must not be empty)
     * @param regNo           the registration number, used as the username
     *                        (letters, digits and hyphens only; must not be used already)
     * @param password        the password (at least 6 characters)
     * @param confirmPassword the password typed a second time (must be the same)
     * @return the new account
     * @throws IllegalArgumentException if any value is invalid or the registration
     *                                  number is already used
     */
    public User registerStudent(String fullName, String regNo, String password,
            String confirmPassword) {
        String cleanName = requireText(fullName, "Full name");
        String cleanRegNo = requireText(regNo, "Registration number");
        if (!cleanRegNo.matches("[A-Za-z0-9-]+")) {
            throw new IllegalArgumentException(
                    "The registration number may contain only letters, digits and hyphens.");
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("The password must have at least "
                    + MIN_PASSWORD_LENGTH + " characters.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("The two passwords do not match.");
        }
        if (findByUsername(cleanRegNo) != null) {
            throw new IllegalArgumentException(
                    "An account with registration number \"" + cleanRegNo + "\" already exists.");
        }
        String salt = newSalt();
        User user = new User(cleanRegNo, cleanName, Role.STUDENT, salt, hashPassword(salt, password));
        users.add(user);
        return user;
    }

    /**
     * Checks a username and password for the chosen role.
     *
     * @param username the username (registration number for students)
     * @param password the password
     * @param role     the role the person is signing in as
     * @return the account that matches
     * @throws IllegalArgumentException if a value is empty, or the username, password
     *                                  and role do not match an account
     */
    public User login(String username, String password, Role role) {
        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Please enter your username and password.");
        }
        User user = findByUsername(username);
        if (user != null && user.getRole() == role) {
            byte[] expected = user.getPasswordHash().getBytes(StandardCharsets.UTF_8);
            byte[] actual = hashPassword(user.getSalt(), password).getBytes(StandardCharsets.UTF_8);
            if (MessageDigest.isEqual(expected, actual)) {
                return user;
            }
        }
        // The same message is used for every failure, so it does not reveal which part was wrong.
        throw new IllegalArgumentException("Invalid username or password.");
    }

    /**
     * Finds an account by username (letter case and surrounding spaces are ignored).
     *
     * @param username the username to look for
     * @return the account, or null if there is none
     */
    public User findByUsername(String username) {
        if (username == null) {
            return null;
        }
        String wanted = username.trim();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(wanted)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Returns the student accounts.
     *
     * @return a new list of all students in the order they registered
     */
    public ArrayList<User> getStudents() {
        ArrayList<User> students = new ArrayList<>();
        for (User user : users) {
            if (user.getRole() == Role.STUDENT) {
                students.add(user);
            }
        }
        return students;
    }

    /**
     * Returns all accounts.
     *
     * @return a new list of all accounts
     */
    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    /**
     * Replaces all accounts with the given accounts (used when saved accounts are loaded).
     * Accounts with an empty or already used username are skipped.
     *
     * @param loaded the accounts to load
     * @return the number of accounts that were loaded
     */
    public int loadUsers(Collection<User> loaded) {
        users.clear();
        for (User user : loaded) {
            if (user.getUsername() != null && !user.getUsername().trim().isEmpty()
                    && findByUsername(user.getUsername()) == null) {
                users.add(user);
            }
        }
        return users.size();
    }

    /**
     * Creates the SHA-256 hash of a salt followed by a password.
     *
     * @param salt     the salt
     * @param password the password
     * @return the hash as Base64 text
     */
    static String hashPassword(String salt, String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is not available.", e);
        }
    }

    /**
     * Creates a new random salt.
     *
     * @return 16 random bytes as Base64 text
     */
    private static String newSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Removes spaces around a text value and rejects empty values.
     *
     * @param value     the text to check
     * @param fieldName the name of the field, used in the error message
     * @return the trimmed text
     * @throws IllegalArgumentException if the text is null or empty
     */
    private static String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }
}
