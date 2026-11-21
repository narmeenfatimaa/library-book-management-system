package library;

import java.io.IOException;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point of the MCS Library Management System.
 * Loads the saved data, shows the sign-in window and, after a successful
 * sign-in, the main window for the role of the person.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class Main {

    private static LibraryManager manager;
    private static UserManager users;
    private static ActivityLog activity;
    private static DataStorage storage;
    private static LoginWindow loginWindow;

    /**
     * Not used: this class only contains the {@code main} method.
     */
    private Main() {
    }

    /**
     * Starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Use Swing's own look on every computer, so the custom colors appear correctly.
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            // If this fails, Swing simply uses its default look.
        }
        // Swing windows should be created on the Event Dispatch Thread.
        SwingUtilities.invokeLater(Main::start);
    }

    /**
     * Creates the data objects, loads the saved files and shows the sign-in window.
     */
    private static void start() {
        storage = new DataStorage(Paths.get("."));
        manager = new LibraryManager();
        users = new UserManager();
        activity = new ActivityLog();
        try {
            manager.loadBooks(storage.loadBooks());
            users.loadUsers(storage.loadUsers());
            activity.loadEntries(storage.loadActivity());
        } catch (IOException e) {
            showSaveError("The saved data could not be read", e);
        }
        if (users.ensureDefaultStaff()) {
            saveUsers();
        }
        showLogin();
    }

    /**
     * Shows a new sign-in window.
     */
    private static void showLogin() {
        loginWindow = new LoginWindow(users, Main::onLogin, Main::onRegistered);
        loginWindow.setVisible(true);
    }

    /**
     * Called after a successful sign-in: closes the sign-in window and opens the main window.
     *
     * @param user the person who signed in
     */
    private static void onLogin(User user) {
        loginWindow.dispose();
        activity.record(user.getUsername(), "Signed in", user.getRole().getLabel());
        saveActivity();
        LibraryGUI gui = new LibraryGUI(manager, users, activity, storage, user, Main::showLogin);
        gui.setVisible(true);
    }

    /**
     * Called after a student has created an account: saves the accounts and logs it.
     *
     * @param user the new account
     */
    private static void onRegistered(User user) {
        activity.record(user.getUsername(), "Created account", user.getFullName());
        saveUsers();
        saveActivity();
    }

    /**
     * Saves the accounts to their file.
     */
    private static void saveUsers() {
        try {
            storage.saveUsers(users.getAllUsers());
        } catch (IOException e) {
            showSaveError("The accounts could not be saved", e);
        }
    }

    /**
     * Saves the activity log to its file.
     */
    private static void saveActivity() {
        try {
            java.util.List<ActivityEntry> oldestFirst = activity.getEntries();
            java.util.Collections.reverse(oldestFirst);
            storage.saveActivity(oldestFirst);
        } catch (IOException e) {
            showSaveError("The activity log could not be saved", e);
        }
    }

    /**
     * Shows a dialog about a problem with the data files.
     *
     * @param text what went wrong
     * @param e    the exception that was thrown
     */
    private static void showSaveError(String text, IOException e) {
        JOptionPane.showMessageDialog(null, text + ": " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}
