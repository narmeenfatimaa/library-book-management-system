package library;

import java.util.ArrayList;

/**
 * Holds the list of books and performs all library operations: add, view,
 * find, update, delete and change status.
 * <p>
 * Books are stored in an {@code ArrayList} in memory only, so the data is
 * lost when the application closes. Invalid operations throw an
 * {@code IllegalArgumentException} with a message that can be shown to the user.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 */
public class LibraryManager {

    private final ArrayList<Book> books = new ArrayList<>();

    /**
     * Creates an empty library.
     */
    public LibraryManager() {
        // The book list starts empty.
    }

    /**
     * Adds a new Available book after validating the input.
     *
     * @param id     the ID of the new book (must not be empty or already used)
     * @param title  the title of the new book (must not be empty)
     * @param author the author of the new book (must not be empty)
     * @throws IllegalArgumentException if the ID, title or author is empty,
     *                                  or if a book with the same ID already exists
     */
    public void addBook(String id, String title, String author) {
        String cleanId = requireText(id, "Book ID");
        String cleanTitle = requireText(title, "Title");
        String cleanAuthor = requireText(author, "Author");

        if (findIndexById(cleanId) != -1) {
            throw new IllegalArgumentException(
                    "A book with ID \"" + cleanId + "\" already exists.");
        }
        books.add(new Book(cleanId, cleanTitle, cleanAuthor));
    }

    /**
     * Returns a copy of the book list, so callers cannot change the
     * internal list by accident.
     *
     * @return a new list containing all books in the order they were added
     */
    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books);
    }

    /**
     * Checks whether the library has no books.
     *
     * @return true if there are no books, false otherwise
     */
    public boolean isEmpty() {
        return books.isEmpty();
    }

    /**
     * Returns the number of books in the library.
     *
     * @return the number of books
     */
    public int getBookCount() {
        return books.size();
    }

    /**
     * Finds a book by ID using linear search.
     *
     * @param id the ID to look for (spaces around it and letter case are ignored)
     * @return the matching book, or null if no book has that ID
     */
    public Book findBookById(String id) {
        int index = findIndexById(id);
        if (index == -1) {
            return null;
        }
        return books.get(index);
    }

    /**
     * Updates the ID, title and author of an existing book.
     *
     * @param currentId the current ID of the book to update
     * @param newId     the new ID (may be the same as the current ID)
     * @param newTitle  the new title (must not be empty)
     * @param newAuthor the new author (must not be empty)
     * @throws IllegalArgumentException if any value is empty, if no book has
     *                                  the current ID, or if the new ID already
     *                                  belongs to a different book
     */
    public void updateBook(String currentId, String newId, String newTitle, String newAuthor) {
        String cleanCurrentId = requireText(currentId, "Book ID");
        String cleanNewId = requireText(newId, "Book ID");
        String cleanTitle = requireText(newTitle, "Title");
        String cleanAuthor = requireText(newAuthor, "Author");

        int currentIndex = findIndexById(cleanCurrentId);
        if (currentIndex == -1) {
            throw new IllegalArgumentException(
                    "Cannot update: no book with ID \"" + cleanCurrentId + "\" exists.");
        }

        int sameIdIndex = findIndexById(cleanNewId);
        if (sameIdIndex != -1 && sameIdIndex != currentIndex) {
            throw new IllegalArgumentException(
                    "Cannot update: ID \"" + cleanNewId + "\" already belongs to another book.");
        }

        Book book = books.get(currentIndex);
        book.setId(cleanNewId);
        book.setTitle(cleanTitle);
        book.setAuthor(cleanAuthor);
    }

    /**
     * Deletes the book with the given ID.
     *
     * @param id the ID of the book to delete
     * @throws IllegalArgumentException if the ID is empty or no book has that ID
     */
    public void deleteBook(String id) {
        String cleanId = requireText(id, "Book ID");
        int index = findIndexById(cleanId);
        if (index == -1) {
            throw new IllegalArgumentException(
                    "Cannot delete: no book with ID \"" + cleanId + "\" exists.");
        }
        books.remove(index);
    }

    /**
     * Sets the status (Available, Issued or Reserved) of the book with the given ID.
     *
     * @param id        the ID of the book
     * @param newStatus the new status
     * @throws IllegalArgumentException if the ID is empty, no book has that ID,
     *                                  or the status is null
     */
    public void setStatus(String id, BookStatus newStatus) {
        String cleanId = requireText(id, "Book ID");
        if (newStatus == null) {
            throw new IllegalArgumentException("Please choose a status.");
        }
        int index = findIndexById(cleanId);
        if (index == -1) {
            throw new IllegalArgumentException(
                    "Cannot change status: no book with ID \"" + cleanId + "\" exists.");
        }
        books.get(index).setStatus(newStatus);
    }

    /**
     * Linear search: checks each book from the start of the list until the
     * ID matches. IDs are compared ignoring upper/lower case ("b1" equals "B1").
     *
     * @param id the ID to look for
     * @return the position of the book in the list, or -1 if not found
     */
    private int findIndexById(String id) {
        if (id == null) {
            return -1;
        }
        String wanted = id.trim();
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getId().equalsIgnoreCase(wanted)) {
                return i;
            }
        }
        return -1;
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
