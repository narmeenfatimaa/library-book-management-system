package library;

/**
 * An account of the system. The password is never stored; only a salted
 * hash of it is kept.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class User {

    private final String username;
    private final String fullName;
    private final Role role;
    private final String salt;
    private final String passwordHash;

    /**
     * Creates an account.
     *
     * @param username     the login name (the registration number for students)
     * @param fullName     the full name of the person
     * @param role         the role of the account
     * @param salt         the random salt used when the password was hashed
     * @param passwordHash the hash of the salt and the password
     */
    public User(String username, String fullName, Role role, String salt, String passwordHash) {
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.salt = salt;
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the login name.
     *
     * @return the username (registration number for students)
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the full name of the person.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Returns the role of the account.
     *
     * @return the role
     */
    public Role getRole() {
        return role;
    }

    /**
     * Returns the salt that was used to hash the password.
     *
     * @return the salt
     */
    public String getSalt() {
        return salt;
    }

    /**
     * Returns the stored password hash.
     *
     * @return the hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Returns the name and username as one line of text.
     *
     * @return the full name followed by the username in brackets
     */
    @Override
    public String toString() {
        return fullName + " (" + username + ")";
    }
}
