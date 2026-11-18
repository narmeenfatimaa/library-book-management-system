package library;

/**
 * Represents one book in the library. A book has an ID, a title, an author
 * and a status (Available, Issued or Reserved).
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 */
public class Book {

    private String id;
    private String title;
    private String author;
    private BookStatus status;

    /**
     * Creates a new book. A new book is always Available.
     *
     * @param id     the unique ID of the book
     * @param title  the title of the book
     * @param author the author of the book
     */
    public Book(String id, String title, String author) {
        this(id, title, author, BookStatus.AVAILABLE);
    }

    /**
     * Creates a book with a specific status.
     *
     * @param id     the unique ID of the book
     * @param title  the title of the book
     * @param author the author of the book
     * @param status the status of the book
     */
    public Book(String id, String title, String author, BookStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    /**
     * Returns the ID of this book.
     *
     * @return the book ID
     */
    public String getId() {
        return id;
    }

    /**
     * Changes the ID of this book.
     *
     * @param id the new book ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the title of this book.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Changes the title of this book.
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the author of this book.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Changes the author of this book.
     *
     * @param author the new author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Returns the status of this book.
     *
     * @return the status (Available, Issued or Reserved)
     */
    public BookStatus getStatus() {
        return status;
    }

    /**
     * Changes the status of this book.
     *
     * @param status the new status
     */
    public void setStatus(BookStatus status) {
        this.status = status;
    }

    /**
     * Returns the book details as one line of text.
     *
     * @return the ID, title, author and status separated by " | "
     */
    @Override
    public String toString() {
        return id + " | " + title + " | " + author + " | " + status;
    }
}
