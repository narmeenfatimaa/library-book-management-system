package library;

/**
 * The possible statuses of a book in the library: Available, Issued or Reserved.
 * Each status has a label that is shown to the user in the table and status box.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 */
public enum BookStatus {

    /** The book is in the library and can be borrowed. */
    AVAILABLE("Available"),

    /** The book has been lent out. */
    ISSUED("Issued"),

    /** The book is kept aside for a particular reader. */
    RESERVED("Reserved");

    private final String label;

    /**
     * Creates a status with the text that is displayed to the user.
     *
     * @param label the display text, for example "Available"
     */
    BookStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the text shown to the user for this status.
     *
     * @return the display text, for example "Available"
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the label, so a status is displayed nicely in tables and combo boxes.
     *
     * @return the display text of this status
     */
    @Override
    public String toString() {
        return label;
    }
}
