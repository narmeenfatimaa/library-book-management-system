package library;

/**
 * Possible statuses of a book in the library.
 */
public enum BookStatus {
    AVAILABLE("Available"),
    ISSUED("Issued"),
    RESERVED("Reserved");

    private final String label;

    BookStatus(String label) {
        this.label = label;
    }

    /**
     * Returns the text shown to the user (for example "Available").
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the label, so a status is displayed nicely in tables and combo boxes.
     */
    @Override
    public String toString() {
        return label;
    }
}
