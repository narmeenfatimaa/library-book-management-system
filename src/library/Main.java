package library;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Entry point of the Library Book Management System.
 * Creates the library logic object and opens the Swing window.
 */
public class Main {

    /**
     * Starts the application.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Use the look and feel of the operating system (for example macOS).
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // If this fails, Swing simply uses its default look.
        }

        // Swing windows should be created on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> {
            LibraryManager manager = new LibraryManager();
            LibraryGUI gui = new LibraryGUI(manager);
            gui.setVisible(true);
        });
    }
}
