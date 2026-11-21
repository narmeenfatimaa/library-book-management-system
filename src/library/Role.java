package library;

/**
 * The two kinds of users of the system. Staff (librarians) manage the library;
 * students browse the catalogue and reserve books.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public enum Role {

    /** A librarian with full access: books, members and the activity log. */
    STAFF("Staff"),

    /** A student who can browse, search and reserve books and see their own account. */
    STUDENT("Student");

    private final String label;

    /**
     * Creates a role with the text that is displayed to the user.
     *
     * @param label the display text, for example "Staff"
     */
    Role(String label) {
        this.label = label;
    }

    /**
     * Returns the text shown to the user for this role.
     *
     * @return the display text
     */
    public String getLabel() {
        return label;
    }

    /**
     * Checks whether this role may add, update, delete, issue and return books
     * and see the members and the activity log.
     *
     * @return true for staff, false for students
     */
    public boolean isStaff() {
        return this == STAFF;
    }

    /**
     * Returns the label.
     *
     * @return the display text of this role
     */
    @Override
    public String toString() {
        return label;
    }
}
