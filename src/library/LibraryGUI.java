package library;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * Swing window of the Library Book Management System.
 * It shows the book form, the action buttons and a table of all books, and
 * passes every action to the LibraryManager. Errors thrown by the manager are
 * caught here and shown to the user in a dialog.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 */
public class LibraryGUI extends JFrame {

    private static final Color ERROR_COLOR = new Color(180, 0, 0);
    private static final Color SUCCESS_COLOR = new Color(0, 120, 0);
    private static final String EMPTY_LIBRARY_MESSAGE =
            "The library is empty. Add a book using the form above.";

    /** The object that stores the books and performs the operations. */
    private final LibraryManager manager;

    /** Text field for the book ID. */
    private final JTextField idField = new JTextField(20);
    /** Text field for the book title. */
    private final JTextField titleField = new JTextField(20);
    /** Text field for the book author. */
    private final JTextField authorField = new JTextField(20);
    /** Selection box for the book status. */
    private final JComboBox<BookStatus> statusCombo = new JComboBox<>(BookStatus.values());

    /** Button that adds a book. */
    private final JButton addButton = new JButton("Add");
    /** Button that updates a book. */
    private final JButton updateButton = new JButton("Update");
    /** Button that deletes a book. */
    private final JButton deleteButton = new JButton("Delete");
    /** Button that changes the status of a book. */
    private final JButton statusButton = new JButton("Set Status");
    /** Button that clears the form. */
    private final JButton clearButton = new JButton("Clear");

    /** Data of the table (one row per book). */
    private final DefaultTableModel tableModel;
    /** Table that displays all books. */
    private final JTable bookTable;

    /** Shows the result of the last action (success or error). */
    private final JLabel messageLabel = new JLabel(" ");
    /** Shows the number of books, or the empty-library message. */
    private final JLabel countLabel = new JLabel();

    /** ID of the book currently selected in the table (null if none is selected). */
    private String selectedBookId = null;

    /**
     * Builds the window and shows the current books of the given manager.
     *
     * @param manager the object that stores the books and performs the operations
     */
    public LibraryGUI(LibraryManager manager) {
        super("Library Book Management System");
        this.manager = manager;

        // A table whose cells cannot be edited directly.
        tableModel = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(tableModel);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(24);
        bookTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        bookTable.getColumnModel().getColumn(1).setPreferredWidth(260);
        bookTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        bookTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        bookTable.setPreferredScrollableViewportSize(new Dimension(650, 240));

        buildLayout();
        connectEvents();
        refreshTable();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // ---------- Building the window ----------

    /**
     * Creates the form, the buttons, the table and the message labels
     * and places them in the window.
     */
    private void buildLayout() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        addFormRow(formPanel, 0, "Book ID:", idField);
        addFormRow(formPanel, 1, "Title:", titleField);
        addFormRow(formPanel, 2, "Author:", authorField);
        addFormRow(formPanel, 3, "Status:", statusCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(statusButton);
        buttonPanel.add(clearButton);

        JPanel topPanel = new JPanel(new BorderLayout(0, 5));
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        countLabel.setFont(countLabel.getFont().deriveFont(Font.BOLD));
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.add(messageLabel);
        bottomPanel.add(countLabel);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        root.add(topPanel, BorderLayout.NORTH);
        root.add(new JScrollPane(bookTable), BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(root);
    }

    /**
     * Adds one row (a label and an input component) to the form.
     *
     * @param panel     the form panel
     * @param row       the row number, starting at 0
     * @param labelText the text of the label
     * @param field     the input component shown beside the label
     */
    private void addFormRow(JPanel panel, int row, String labelText, java.awt.Component field) {
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = row;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.insets = new Insets(4, 8, 4, 8);
        panel.add(new JLabel(labelText), labelConstraints);

        GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.gridx = 1;
        fieldConstraints.gridy = row;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.fill = GridBagConstraints.HORIZONTAL;
        fieldConstraints.insets = new Insets(4, 0, 4, 8);
        panel.add(field, fieldConstraints);
    }

    // ---------- Connecting events ----------

    /**
     * Connects the buttons and the table to the methods that handle them.
     * Selecting a table row fills the form with that book's details.
     */
    private void connectEvents() {
        addButton.addActionListener(e -> addBook());
        updateButton.addActionListener(e -> updateBook());
        deleteButton.addActionListener(e -> deleteBook());
        statusButton.addActionListener(e -> setBookStatus());
        clearButton.addActionListener(e -> {
            clearForm();
            messageLabel.setText(" ");
        });

        // When a row is selected, show that book's details in the form.
        bookTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int row = bookTable.getSelectedRow();
            if (row == -1) {
                return;
            }
            String id = (String) tableModel.getValueAt(row, 0);
            Book book = manager.findBookById(id);
            if (book != null) {
                selectedBookId = book.getId();
                idField.setText(book.getId());
                titleField.setText(book.getTitle());
                authorField.setText(book.getAuthor());
                statusCombo.setSelectedItem(book.getStatus());
            }
        });
    }

    // ---------- Button actions ----------

    /**
     * Handles the Add button: adds a new book using the form values.
     */
    private void addBook() {
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            manager.addBook(idField.getText(), titleField.getText(), authorField.getText());
            refreshTable();
            clearForm();
            showSuccess("Book \"" + title + "\" (ID " + id + ") added successfully.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Handles the Update button: changes the ID, title and author of the
     * selected book (or of the book whose ID is typed in the form).
     */
    private void updateBook() {
        try {
            String newId = idField.getText().trim();
            manager.updateBook(getTargetId(), idField.getText(),
                    titleField.getText(), authorField.getText());
            refreshTable();
            clearForm();
            showSuccess("Book " + newId + " updated successfully.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Handles the Delete button: deletes the selected book after the user
     * confirms.
     */
    private void deleteBook() {
        try {
            String targetId = getTargetId();
            Book book = manager.findBookById(targetId);
            if (book != null && !confirmDelete(book)) {
                return; // The user chose "No".
            }
            manager.deleteBook(targetId); // Throws an error if the ID is empty or not found.
            refreshTable();
            clearForm();
            showSuccess("Book " + targetId.trim() + " deleted successfully.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Handles the Set Status button: gives the selected book the status
     * chosen in the status box.
     */
    private void setBookStatus() {
        try {
            String targetId = getTargetId();
            BookStatus status = (BookStatus) statusCombo.getSelectedItem();
            manager.setStatus(targetId, status);
            refreshTable();
            clearForm();
            showSuccess("Status of book " + targetId.trim() + " set to " + status + ".");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    // ---------- Helper methods ----------

    /**
     * Finds out which book an action applies to: the selected table row if there
     * is one, otherwise the ID typed in the form.
     *
     * @return the ID of the target book
     */
    private String getTargetId() {
        if (selectedBookId != null) {
            return selectedBookId;
        }
        return idField.getText();
    }

    /**
     * Reloads the table from the manager and updates the book count message
     * (or the empty-library message).
     */
    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Book book : manager.getAllBooks()) {
            tableModel.addRow(new Object[]{
                book.getId(), book.getTitle(), book.getAuthor(), book.getStatus()
            });
        }
        if (manager.isEmpty()) {
            countLabel.setText(EMPTY_LIBRARY_MESSAGE);
        } else {
            countLabel.setText("Total books: " + manager.getBookCount());
        }
    }

    /**
     * Empties the form, resets the status box and removes the table selection.
     */
    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        statusCombo.setSelectedItem(BookStatus.AVAILABLE);
        bookTable.clearSelection();
        selectedBookId = null;
    }

    /**
     * Shows a success message in green at the bottom of the window.
     *
     * @param message the text to show
     */
    private void showSuccess(String message) {
        messageLabel.setForeground(SUCCESS_COLOR);
        messageLabel.setText(message);
    }

    /**
     * Shows an error message in a dialog and in red at the bottom of the window.
     *
     * @param message the error text to show
     */
    void showError(String message) {
        messageLabel.setForeground(ERROR_COLOR);
        messageLabel.setText("Error: " + message);
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Asks the user to confirm deleting a book.
     *
     * @param book the book that is about to be deleted
     * @return true if the user chose Yes, false otherwise
     */
    boolean confirmDelete(Book book) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Delete book " + book.getId() + " (" + book.getTitle() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
    }
}
