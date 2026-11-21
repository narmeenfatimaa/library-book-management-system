package library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents one book in the library. A book has an ID, a title, an author,
 * a category and a status (Available, Issued or Reserved). A book that is
 * Issued or Reserved also records the borrower (name and ID), and an Issued book
 * has a due date. An issued book that is not returned in time has a fine.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class Book {

    /** The categories a book can belong to. */
    public static final String[] CATEGORIES = {
        "Computer Science",
        "Software Engineering",
        "Electrical Engineering",
        "Telecommunications",
        "Cyber Security",
        "Mathematics and Physics",
        "Management",
        "Military History",
        "Literature and General"
    };

    /** The fine in rupees for each day an issued book is overdue. */
    public static final int FINE_PER_DAY = 10;

    private String id;
    private String title;
    private String author;
    private String category;
    private BookStatus status;
    private String borrower;
    private String borrowerId;
    private LocalDate dueDate;

    /**
     * Creates a new book. A new book is always Available and has no borrower.
     *
     * @param id       the unique ID of the book
     * @param title    the title of the book
     * @param author   the author of the book
     * @param category the category of the book
     */
    public Book(String id, String title, String author, String category) {
        this(id, title, author, category, BookStatus.AVAILABLE, "", "", null);
    }

    /**
     * Creates a book with a specific status and borrower details
     * (used when books are loaded from the saved file).
     *
     * @param id         the unique ID of the book
     * @param title      the title of the book
     * @param author     the author of the book
     * @param category   the category of the book
     * @param status     the status of the book
     * @param borrower   the name of the borrower (empty if none)
     * @param borrowerId the ID (registration number) of the borrower (empty if none)
     * @param dueDate    the due date of an issued book (null if none)
     */
    public Book(String id, String title, String author, String category,
            BookStatus status, String borrower, String borrowerId, LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
        this.borrower = borrower;
        this.borrowerId = borrowerId;
        this.dueDate = dueDate;
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
     * Returns the category of this book.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Changes the category of this book.
     *
     * @param category the new category
     */
    public void setCategory(String category) {
        this.category = category;
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
     * Returns the name of the person who has issued or reserved this book.
     *
     * @return the borrower name, or an empty text if the book has no borrower
     */
    public String getBorrower() {
        return borrower;
    }

    /**
     * Changes the borrower name of this book.
     *
     * @param borrower the borrower name (empty if the book has no borrower)
     */
    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }

    /**
     * Returns the ID (registration number) of the person who has issued or reserved this book.
     *
     * @return the borrower ID, or an empty text if the book has no borrower
     */
    public String getBorrowerId() {
        return borrowerId;
    }

    /**
     * Changes the borrower ID of this book.
     *
     * @param borrowerId the borrower ID (empty if the book has no borrower)
     */
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    /**
     * Returns the date by which an issued book must be returned.
     *
     * @return the due date, or null if the book is not issued
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * Changes the due date of this book.
     *
     * @param dueDate the new due date (null if the book is not issued)
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Checks whether this book is issued and its due date has already passed.
     *
     * @param today the date to compare with
     * @return true if the book is issued and the due date is before today
     */
    public boolean isOverdue(LocalDate today) {
        return status == BookStatus.ISSUED && dueDate != null && dueDate.isBefore(today);
    }

    /**
     * Counts the days an issued book is overdue.
     *
     * @param today the date to compare with
     * @return the number of days after the due date, or 0 if the book is not overdue
     */
    public int getOverdueDays(LocalDate today) {
        if (!isOverdue(today)) {
            return 0;
        }
        return (int) ChronoUnit.DAYS.between(dueDate, today);
    }

    /**
     * Calculates the fine for an overdue book: {@link #FINE_PER_DAY} rupees for each overdue day.
     *
     * @param today the date to compare with
     * @return the fine in rupees (0 if the book is not overdue)
     */
    public int getFine(LocalDate today) {
        return getOverdueDays(today) * FINE_PER_DAY;
    }

    /**
     * Returns the book details as one line of text.
     *
     * @return the ID, title, author, category and status separated by " | "
     */
    @Override
    public String toString() {
        return id + " | " + title + " | " + author + " | " + category + " | " + status;
    }
}
