package library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Holds the list of books and performs all library operations: add, view,
 * search, find, update, delete, change status and count books by status.
 * <p>
 * Books are stored in an {@code ArrayList} in memory. (The graphical interface
 * saves them to a file and loads them again when the program starts.)
 * Invalid operations throw an {@code IllegalArgumentException} with a message
 * that can be shown to the user.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class LibraryManager {

    /** The loan period suggested when a book is issued. */
    public static final int DEFAULT_LOAN_DAYS = 14;

    /** The longest loan period that can be given to a book. */
    public static final int MAX_LOAN_DAYS = 60;

    /** The largest number of books (issued and reserved together) one member can hold. */
    public static final int MAX_BOOKS_PER_MEMBER = 3;

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
     * @param id       the ID of the new book (must not be empty or already used)
     * @param title    the title of the new book (must not be empty)
     * @param author   the author of the new book (must not be empty)
     * @param category the category of the new book (must not be empty)
     * @throws IllegalArgumentException if the ID, title, author or category is empty,
     *                                  or if a book with the same ID already exists
     */
    public void addBook(String id, String title, String author, String category) {
        String cleanId = requireText(id, "Book ID");
        String cleanTitle = requireNonNumericText(title, "Title");
        String cleanAuthor = requireNonNumericText(author, "Author");
        String cleanCategory = requireText(category, "Category");
        if (findIndexById(cleanId) != -1) {
            throw new IllegalArgumentException(
                    "A book with ID \"" + cleanId + "\" already exists.");
        }
        books.add(new Book(cleanId, cleanTitle, cleanAuthor, cleanCategory));
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
     * Counts the books that have the given status.
     *
     * @param status the status to count
     * @return the number of books with that status
     */
    public int countByStatus(BookStatus status) {
        int count = 0;
        for (Book book : books) {
            if (book.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the issued books whose due date has passed.
     *
     * @param today the date to compare with
     * @return the number of overdue books
     */
    public int countOverdue(LocalDate today) {
        int count = 0;
        for (Book book : books) {
            if (book.isOverdue(today)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the books that a member has with the given status.
     *
     * @param memberId the ID (registration number) of the member
     * @param status   the status to count
     * @return the number of matching books
     */
    public int countForMember(String memberId, BookStatus status) {
        int count = 0;
        for (Book book : books) {
            if (book.getStatus() == status && isHeldBy(book, memberId)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Adds up the fines of all overdue books that a member has.
     *
     * @param memberId the ID (registration number) of the member
     * @param today    the date to compare with
     * @return the total fine in rupees
     */
    public int totalFineForMember(String memberId, LocalDate today) {
        int total = 0;
        for (Book book : books) {
            if (isHeldBy(book, memberId)) {
                total += book.getFine(today);
            }
        }
        return total;
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
     * Searches the books. A book matches when the search text appears in its ID,
     * title, author, category or borrower (letter case is ignored) and, if a status
     * is given, the book has that status. An empty search text matches every book.
     *
     * @param query        the text to search for (may be null or empty)
     * @param statusFilter only books with this status are returned (null means any status)
     * @return a new list of the matching books in the order they were added
     */
    public ArrayList<Book> searchBooks(String query, BookStatus statusFilter) {
        return searchBooks(query, statusFilter, null);
    }

    /**
     * Searches the books like {@link #searchBooks(String, BookStatus)}, and can also
     * keep only the books that one member has issued or reserved.
     *
     * @param query        the text to search for (may be null or empty)
     * @param statusFilter only books with this status are returned (null means any status)
     * @param memberId     only books held by this member are returned (null means all books)
     * @return a new list of the matching books in the order they were added
     */
    public ArrayList<Book> searchBooks(String query, BookStatus statusFilter, String memberId) {
        String wanted = (query == null) ? "" : query.trim().toLowerCase();
        ArrayList<Book> matches = new ArrayList<>();
        for (Book book : books) {
            if (statusFilter != null && book.getStatus() != statusFilter) {
                continue;
            }
            if (memberId != null && !isHeldBy(book, memberId)) {
                continue;
            }
            String text = (book.getId() + " " + book.getTitle() + " " + book.getAuthor()
                    + " " + book.getCategory() + " " + book.getBorrower()).toLowerCase();
            if (text.contains(wanted)) {
                matches.add(book);
            }
        }
        return matches;
    }

    /**
     * Updates the ID, title, author and category of an existing book.
     *
     * @param currentId   the current ID of the book to update
     * @param newId       the new ID (may be the same as the current ID)
     * @param newTitle    the new title (must not be empty)
     * @param newAuthor   the new author (must not be empty)
     * @param newCategory the new category (must not be empty)
     * @throws IllegalArgumentException if any value is empty, if no book has
     *                                  the current ID, or if the new ID already
     *                                  belongs to a different book
     */
    public void updateBook(String currentId, String newId, String newTitle,
            String newAuthor, String newCategory) {
        String cleanCurrentId = requireText(currentId, "Book ID");
        String cleanNewId = requireText(newId, "Book ID");
        String cleanTitle = requireNonNumericText(newTitle, "Title");
        String cleanAuthor = requireNonNumericText(newAuthor, "Author");
        String cleanCategory = requireText(newCategory, "Category");
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
        book.setCategory(cleanCategory);
    }

    /**
     * Deletes the book with the given ID. A book that is currently issued
     * cannot be deleted until it has been returned.
     *
     * @param id the ID of the book to delete
     * @throws IllegalArgumentException if the ID is empty, no book has that ID,
     *                                  or the book is currently issued
     */
    public void deleteBook(String id) {
        String cleanId = requireText(id, "Book ID");
        int index = findIndexById(cleanId);
        if (index == -1) {
            throw new IllegalArgumentException(
                    "Cannot delete: no book with ID \"" + cleanId + "\" exists.");
        }
        Book book = books.get(index);
        if (book.getStatus() == BookStatus.ISSUED) {
            throw new IllegalArgumentException("Cannot delete: book \"" + book.getId()
                    + "\" is currently issued to " + book.getBorrower()
                    + ". Set it to Available first.");
        }
        books.remove(index);
    }

    /**
     * Sets the status (Available, Issued or Reserved) of the book with the given ID.
     * Setting a book to Available clears its borrower and due date. Issuing a book
     * records the borrower and a due date (today plus the loan period). Reserving a
     * book records the person it is reserved for. A member cannot hold more than
     * {@link #MAX_BOOKS_PER_MEMBER} books.
     *
     * @param id         the ID of the book
     * @param newStatus  the new status
     * @param borrower   the borrower name (needed for Issued and Reserved, ignored for Available)
     * @param borrowerId the borrower ID or registration number (may be empty)
     * @param loanDays   the loan period in days (needed for Issued, between 1 and 60)
     * @throws IllegalArgumentException if the ID is empty, no book has that ID,
     *                                  the status is null, the borrower name is empty
     *                                  (Issued or Reserved), the loan period is not
     *                                  between 1 and 60 days (Issued), or the member
     *                                  already holds the maximum number of books
     */
    public void setStatus(String id, BookStatus newStatus, String borrower,
            String borrowerId, int loanDays) {
        String cleanId = requireText(id, "Book ID");
        if (newStatus == null) {
            throw new IllegalArgumentException("Please choose a status.");
        }
        int index = findIndexById(cleanId);
        if (index == -1) {
            throw new IllegalArgumentException(
                    "Cannot change status: no book with ID \"" + cleanId + "\" exists.");
        }
        Book book = books.get(index);

        if (newStatus == BookStatus.AVAILABLE) {
            clearBorrower(book);
            return;
        }

        String cleanBorrower = requireText(borrower, "Borrower name");
        String cleanBorrowerId = (borrowerId == null) ? "" : borrowerId.trim();
        LocalDate dueDate = null;
        if (newStatus == BookStatus.ISSUED) {
            if (loanDays < 1 || loanDays > MAX_LOAN_DAYS) {
                throw new IllegalArgumentException(
                        "The loan period must be between 1 and " + MAX_LOAN_DAYS + " days.");
            }
            dueDate = LocalDate.now().plusDays(loanDays);
        }
        checkMemberLimit(cleanBorrowerId, book);

        book.setStatus(newStatus);
        book.setBorrower(cleanBorrower);
        book.setBorrowerId(cleanBorrowerId);
        book.setDueDate(dueDate);
    }

    /**
     * Reserves an available book for a member (used when a student reserves a book).
     *
     * @param bookId     the ID of the book
     * @param memberName the name of the member
     * @param memberId   the ID (registration number) of the member
     * @throws IllegalArgumentException if no book has that ID, the book is not
     *                                  available, or the member already holds the
     *                                  maximum number of books
     */
    public void reserveBook(String bookId, String memberName, String memberId) {
        String cleanId = requireText(bookId, "Book ID");
        String cleanName = requireText(memberName, "Member name");
        String cleanMemberId = requireText(memberId, "Member ID");
        Book book = findBookById(cleanId);
        if (book == null) {
            throw new IllegalArgumentException(
                    "Cannot reserve: no book with ID \"" + cleanId + "\" exists.");
        }
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalArgumentException("Only an available book can be reserved. This book is "
                    + book.getStatus().getLabel().toLowerCase() + ".");
        }
        checkMemberLimit(cleanMemberId, book);
        book.setStatus(BookStatus.RESERVED);
        book.setBorrower(cleanName);
        book.setBorrowerId(cleanMemberId);
        book.setDueDate(null);
    }

    /**
     * Cancels a reservation that a member made (used when a student cancels a reservation).
     *
     * @param bookId   the ID of the book
     * @param memberId the ID (registration number) of the member who reserved it
     * @throws IllegalArgumentException if no book has that ID, or the book is not
     *                                  reserved by that member
     */
    public void cancelReservation(String bookId, String memberId) {
        String cleanId = requireText(bookId, "Book ID");
        String cleanMemberId = requireText(memberId, "Member ID");
        Book book = findBookById(cleanId);
        if (book == null) {
            throw new IllegalArgumentException(
                    "Cannot cancel: no book with ID \"" + cleanId + "\" exists.");
        }
        if (book.getStatus() != BookStatus.RESERVED || !isHeldBy(book, cleanMemberId)) {
            throw new IllegalArgumentException("You can only cancel your own reservation.");
        }
        clearBorrower(book);
    }

    /**
     * Replaces all books with the given books (used when saved books are loaded).
     * Books with an empty ID or an ID that is already used are skipped.
     *
     * @param newBooks the books to load
     * @return the number of books that were loaded
     */
    public int loadBooks(Collection<Book> newBooks) {
        books.clear();
        for (Book book : newBooks) {
            if (book.getId() != null && !book.getId().trim().isEmpty()
                    && findIndexById(book.getId()) == -1) {
                books.add(book);
            }
        }
        return books.size();
    }

    /**
     * Makes a book Available again and removes its borrower and due date.
     *
     * @param book the book to change
     */
    private void clearBorrower(Book book) {
        book.setStatus(BookStatus.AVAILABLE);
        book.setBorrower("");
        book.setBorrowerId("");
        book.setDueDate(null);
    }

    /**
     * Checks whether a book is held (issued or reserved) by the given member.
     *
     * @param book     the book to check
     * @param memberId the member ID to compare with
     * @return true if the book is issued or reserved and its borrower ID matches
     */
    private static boolean isHeldBy(Book book, String memberId) {
        return memberId != null && !memberId.trim().isEmpty()
                && book.getStatus() != BookStatus.AVAILABLE
                && book.getBorrowerId().equalsIgnoreCase(memberId.trim());
    }

    /**
     * Rejects the change if the member already holds the maximum number of
     * other books. An empty member ID is not checked.
     *
     * @param memberId  the member ID
     * @param exception the book that is being changed (it is not counted)
     * @throws IllegalArgumentException if the member already holds too many books
     */
    private void checkMemberLimit(String memberId, Book exception) {
        if (memberId.isEmpty()) {
            return;
        }
        int held = 0;
        for (Book other : books) {
            if (other != exception && isHeldBy(other, memberId)) {
                held++;
            }
        }
        if (held >= MAX_BOOKS_PER_MEMBER) {
            throw new IllegalArgumentException("This member already holds " + MAX_BOOKS_PER_MEMBER
                    + " books (issued or reserved), which is the limit.");
        }
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
    /**
     * Removes spaces around a text value, rejects empty values, and rejects
     * values made entirely of digits (such as "123").
     *
     * @param value     the text to check
     * @param fieldName the name of the field, used in the error message
     * @return the trimmed text
     * @throws IllegalArgumentException if the text is null, empty, or only digits
     */
    private static String requireNonNumericText(String value, String fieldName) {
        String cleaned = requireText(value, fieldName);
        if (cleaned.matches("\\d+")) {
            throw new IllegalArgumentException(fieldName + " cannot be only numbers.");
        }
        return cleaned;
    }
}
