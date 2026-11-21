package library;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves the books, the accounts and the activity log to CSV text files and loads
 * them again, so the data is still there the next time the program starts.
 * <p>
 * The files are {@code library_books.csv}, {@code library_users.csv} and
 * {@code library_activity.csv}. Text values are written inside double quotes so
 * commas in a title do not matter. Lines that cannot be understood are skipped
 * when loading.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class DataStorage {

    private static final String BOOKS_HEADER =
            "id,title,author,category,status,borrower,dueDate,borrowerId";
    private static final String USERS_HEADER = "username,fullName,role,salt,passwordHash";
    private static final String ACTIVITY_HEADER = "time,user,action,details";

    private final Path booksFile;
    private final Path usersFile;
    private final Path activityFile;

    /**
     * Creates a storage object that keeps its files in the given folder.
     *
     * @param folder the folder for the CSV files
     */
    public DataStorage(Path folder) {
        this.booksFile = folder.resolve("library_books.csv");
        this.usersFile = folder.resolve("library_users.csv");
        this.activityFile = folder.resolve("library_activity.csv");
    }

    /**
     * Returns the file in which the books are saved.
     *
     * @return the path of the books file
     */
    public Path getBooksFile() {
        return booksFile;
    }

    /**
     * Writes all the given books to the books file, replacing its old content.
     *
     * @param books the books to save
     * @throws IOException if the file cannot be written
     */
    public void saveBooks(List<Book> books) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(BOOKS_HEADER);
        for (Book book : books) {
            String due = (book.getDueDate() == null) ? "" : book.getDueDate().toString();
            lines.add(String.join(",",
                    CsvUtil.quote(book.getId()),
                    CsvUtil.quote(book.getTitle()),
                    CsvUtil.quote(book.getAuthor()),
                    CsvUtil.quote(book.getCategory()),
                    book.getStatus().name(),
                    CsvUtil.quote(book.getBorrower()),
                    due,
                    CsvUtil.quote(book.getBorrowerId())));
        }
        Files.write(booksFile, lines, StandardCharsets.UTF_8);
    }

    /**
     * Reads the books from the books file.
     *
     * @return the books in the file (an empty list if the file does not exist yet)
     * @throws IOException if the file exists but cannot be read
     */
    public ArrayList<Book> loadBooks() throws IOException {
        ArrayList<Book> books = new ArrayList<>();
        for (String line : readDataLines(booksFile)) {
            ArrayList<String> fields = CsvUtil.splitLine(line);
            if (fields.size() < 7) {
                continue;
            }
            try {
                BookStatus status = BookStatus.valueOf(fields.get(4));
                LocalDate due = fields.get(6).isEmpty() ? null : LocalDate.parse(fields.get(6));
                String borrowerId = (fields.size() > 7) ? fields.get(7) : ""; // older files have no such column
                books.add(new Book(fields.get(0), fields.get(1), fields.get(2), fields.get(3),
                        status, fields.get(5), borrowerId, due));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                // Skip a line that cannot be understood.
            }
        }
        return books;
    }

    /**
     * Writes all the given accounts to the users file, replacing its old content.
     *
     * @param users the accounts to save
     * @throws IOException if the file cannot be written
     */
    public void saveUsers(List<User> users) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(USERS_HEADER);
        for (User user : users) {
            lines.add(String.join(",",
                    CsvUtil.quote(user.getUsername()),
                    CsvUtil.quote(user.getFullName()),
                    user.getRole().name(),
                    CsvUtil.quote(user.getSalt()),
                    CsvUtil.quote(user.getPasswordHash())));
        }
        Files.write(usersFile, lines, StandardCharsets.UTF_8);
    }

    /**
     * Reads the accounts from the users file.
     *
     * @return the accounts in the file (an empty list if the file does not exist yet)
     * @throws IOException if the file exists but cannot be read
     */
    public ArrayList<User> loadUsers() throws IOException {
        ArrayList<User> users = new ArrayList<>();
        for (String line : readDataLines(usersFile)) {
            ArrayList<String> fields = CsvUtil.splitLine(line);
            if (fields.size() < 5) {
                continue;
            }
            try {
                users.add(new User(fields.get(0), fields.get(1), Role.valueOf(fields.get(2)),
                        fields.get(3), fields.get(4)));
            } catch (IllegalArgumentException e) {
                // Skip a line that cannot be understood.
            }
        }
        return users;
    }

    /**
     * Writes the activity log to the activity file, replacing its old content.
     *
     * @param entries the entries to save, oldest first
     * @throws IOException if the file cannot be written
     */
    public void saveActivity(List<ActivityEntry> entries) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        lines.add(ACTIVITY_HEADER);
        for (ActivityEntry entry : entries) {
            lines.add(String.join(",",
                    entry.getTime().toString(),
                    CsvUtil.quote(entry.getUser()),
                    CsvUtil.quote(entry.getAction()),
                    CsvUtil.quote(entry.getDetails())));
        }
        Files.write(activityFile, lines, StandardCharsets.UTF_8);
    }

    /**
     * Reads the activity log from the activity file.
     *
     * @return the entries in the file, oldest first (an empty list if the file does not exist yet)
     * @throws IOException if the file exists but cannot be read
     */
    public ArrayList<ActivityEntry> loadActivity() throws IOException {
        ArrayList<ActivityEntry> entries = new ArrayList<>();
        for (String line : readDataLines(activityFile)) {
            ArrayList<String> fields = CsvUtil.splitLine(line);
            if (fields.size() < 4) {
                continue;
            }
            try {
                entries.add(new ActivityEntry(LocalDateTime.parse(fields.get(0)),
                        fields.get(1), fields.get(2), fields.get(3)));
            } catch (DateTimeParseException e) {
                // Skip a line that cannot be understood.
            }
        }
        return entries;
    }

    /**
     * Reads the lines of a file without the header line and without empty lines.
     *
     * @param file the file to read
     * @return the data lines (empty if the file does not exist)
     * @throws IOException if the file exists but cannot be read
     */
    private static ArrayList<String> readDataLines(Path file) throws IOException {
        ArrayList<String> dataLines = new ArrayList<>();
        if (!Files.exists(file)) {
            return dataLines;
        }
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) { // line 0 is the header
            if (!lines.get(i).trim().isEmpty()) {
                dataLines.add(lines.get(i));
            }
        }
        return dataLines;
    }
}
