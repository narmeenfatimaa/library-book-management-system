package library;

/**
 * Represents one book in the library: its ID, title, author and status.
 */
public class Book {

    private String id;
    private String title;
    private String author;
    private BookStatus status;

    /**
     * Creates a new book. A new book is always Available.
     */
    public Book(String id, String title, String author) {
        this(id, title, author, BookStatus.AVAILABLE);
    }

    /**
     * Creates a book with a specific status.
     */
    public Book(String id, String title, String author, BookStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return id + " | " + title + " | " + author + " | " + status;
    }
}
