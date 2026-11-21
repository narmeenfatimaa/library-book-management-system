package library;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import library.Ui.Avatar;
import library.Ui.DueDateRenderer;
import library.Ui.FineRenderer;
import library.Ui.GradientPanel;
import library.Ui.LogoBadge;
import library.Ui.ModernButton;
import library.Ui.NavButton;
import library.Ui.StatCard;
import library.Ui.StatusRenderer;

/**
 * The main window of the MCS Library Management System (Military College of Signals).
 * <p>
 * What the window shows depends on the role of the person who signed in:
 * <ul>
 *   <li><b>Staff</b> see the dashboard, the book form with Add, Update, Delete,
 *       Set Status and Clear buttons, the book table, the list of students and the
 *       activity log.</li>
 *   <li><b>Students</b> see the dashboard with their own numbers, the catalogue
 *       (search, filter, sort), buttons to reserve a book or cancel their own
 *       reservation, a "My Books" view and a page about their account.</li>
 * </ul>
 * Every action is passed to the LibraryManager. Errors thrown by the manager are
 * caught here and shown to the user in a dialog. After every successful change
 * the data is saved to files through the DataStorage object.
 * <p>
 * The colors and drawing classes are in the class Ui and only change how the
 * window looks. The behaviour is in the methods that handle the buttons.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class LibraryGUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String PAGE_BOOKS = "books";
    private static final String PAGE_MEMBERS = "members";
    private static final String PAGE_ACTIVITY = "activity";
    private static final String PAGE_ACCOUNT = "account";
    private static final String TABLE_CARD = "table";
    private static final String EMPTY_CARD = "empty";
    private static final String ALL_STATUSES = "All Statuses";
    private static final String ALL_BOOKS = "All Books";
    private static final String MY_BOOKS = "My Books";
    private static final int MAX_ACTIVITY_ROWS = 200;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy  HH:mm:ss");

    // ---------- Data ----------

    /** The object that stores the books and performs the operations. */
    private final transient LibraryManager manager;
    /** The accounts of the library. */
    private final transient UserManager users;
    /** The log of what users did. */
    private final transient ActivityLog activity;
    /** The object that saves the data to files (null means: do not save). */
    private final transient DataStorage storage;
    /** The person who is signed in. */
    private final transient User currentUser;
    /** Called after the person has signed out. */
    private final transient Runnable onSignOut;
    /** True if the signed-in person is staff. */
    private final boolean staff;

    // ---------- Components used by both roles ----------

    /** Panel that holds the pages (books, members, ...). */
    private final JPanel pages = new JPanel(new CardLayout());
    /** Button in the title bar that signs the person out. */
    private final ModernButton signOutButton = new ModernButton("Sign Out", Ui.GOLD, Ui.NAVY_DARK);
    /** The four or five colored dashboard cards. */
    private final StatCard[] statCards = new StatCard[5];

    /** Text field used to search the books. */
    private final JTextField searchField = new JTextField(16);
    /** Selection box used to show only books with one status. */
    private final JComboBox<Object> filterCombo = new JComboBox<>(new Object[]{
        ALL_STATUSES, BookStatus.AVAILABLE, BookStatus.ISSUED, BookStatus.RESERVED});
    /** Selection box that switches between all books and the student's own books (students only). */
    private JComboBox<String> viewCombo;

    /** Data of the book table (one row per book). */
    private final DefaultTableModel tableModel;
    /** Table that displays the books. */
    private final JTable bookTable;
    /** Panel that switches between the table and a message (empty library, no match). */
    private final JPanel tableCards = new JPanel(new CardLayout());
    /** Big text of the message that replaces the table. */
    private final JLabel emptyHeading = new JLabel("", SwingConstants.CENTER);
    /** Smaller text of the message that replaces the table. */
    private final JLabel emptyHint = new JLabel("", SwingConstants.CENTER);
    /** Shows the number of books, or the empty-library message. */
    private final JLabel countLabel = new JLabel();
    /** Shows the result of the last action (success or error). */
    private final JLabel messageLabel = new JLabel(" ");
    /** Timer that clears the message after a few seconds. */
    private final Timer messageTimer;

    // ---------- Staff components ----------

    /** Text field for the book ID. */
    private JTextField idField;
    /** Text field for the book title. */
    private JTextField titleField;
    /** Text field for the book author. */
    private JTextField authorField;
    /** Selection box for the book category. */
    private JComboBox<String> categoryCombo;
    /** Selection box for the book status. */
    private JComboBox<BookStatus> statusCombo;
    /** Button that adds a book. */
    private JButton addButton;
    /** Button that updates a book. */
    private JButton updateButton;
    /** Button that deletes a book. */
    private JButton deleteButton;
    /** Button that changes the status of a book. */
    private JButton statusButton;
    /** Button that clears the form. */
    private JButton clearButton;
    /** Data of the table of students. */
    private DefaultTableModel membersModel;
    /** Shows the number of students. */
    private final JLabel membersCountLabel = new JLabel();
    /** Data of the activity table. */
    private DefaultTableModel activityModel;
    /** Shows how many activity entries are displayed. */
    private final JLabel activityCountLabel = new JLabel();

    // ---------- Student components ----------

    /** Button with which a student reserves the selected book. */
    private JButton reserveButton;
    /** Button with which a student cancels a reservation. */
    private JButton cancelReservationButton;
    /** Account page: number of books issued to the student. */
    private final JLabel accountIssued = new JLabel();
    /** Account page: number of books reserved by the student. */
    private final JLabel accountReserved = new JLabel();
    /** Account page: fines of the student. */
    private final JLabel accountFines = new JLabel();

    /** ID of the book currently selected in the table (null if none is selected). */
    private String selectedBookId = null;

    /**
     * The member and the loan period chosen when a book is issued or reserved by staff.
     */
    static final class BorrowerDetails {

        /** The member who gets the book. */
        final User member;
        /** The loan period in days (only used for issued books). */
        final int loanDays;

        /**
         * Creates the details.
         *
         * @param member   the member who gets the book
         * @param loanDays the loan period in days
         */
        BorrowerDetails(User member, int loanDays) {
            this.member = member;
            this.loanDays = loanDays;
        }
    }

    /**
     * Builds the window for the signed-in person.
     *
     * @param manager     the object that stores the books and performs the operations
     * @param users       the accounts of the library
     * @param activity    the activity log
     * @param storage     the object that saves the data to files (null to disable saving)
     * @param currentUser the person who signed in
     * @param onSignOut   called after the person has signed out
     */
    public LibraryGUI(LibraryManager manager, UserManager users, ActivityLog activity,
            DataStorage storage, User currentUser, Runnable onSignOut) {
        super("MCS Library Management System  -  " + currentUser.getRole().getLabel());
        this.manager = manager;
        this.users = users;
        this.activity = activity;
        this.storage = storage;
        this.currentUser = currentUser;
        this.onSignOut = onSignOut;
        this.staff = currentUser.getRole().isStaff();

        // A table whose cells cannot be edited directly.
        tableModel = new DefaultTableModel(new String[]{
            "ID", "Title", "Author", "Category", "Status", "Borrower", "Due Date", "Fine"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 6) {
                    return LocalDate.class;
                }
                if (column == 7) {
                    return Integer.class;
                }
                return Object.class;
            }
        };
        bookTable = new JTable(tableModel);
        messageTimer = new Timer(7000, e -> messageLabel.setText(" "));
        messageTimer.setRepeats(false);

        styleBookTable();
        Ui.styleField(searchField);
        Ui.styleCombo(filterCombo, 36);
        filterCombo.setPreferredSize(new Dimension(130, 36));

        buildLayout();
        connectEvents();
        refreshAll();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        showSuccess("Welcome, " + currentUser.getFullName() + ".");
    }

    // ---------- Building the window ----------

    /**
     * Sets the look of the book table: column widths and how the cells are drawn.
     * Students do not see the borrower column.
     */
    private void styleBookTable() {
        Ui.styleTable(bookTable, 38);
        String[] names = {"ID", "Title", "Author", "Category", "Status", "Borrower", "Due Date", "Fine"};
        int[] widths = {60, 160, 110, 140, 108, 110, 104, 70};
        for (int i = 0; i < names.length; i++) {
            bookTable.getColumn(names[i]).setPreferredWidth(widths[i]);
        }
        bookTable.getColumn("Status").setCellRenderer(new StatusRenderer());
        bookTable.getColumn("Due Date").setCellRenderer(new DueDateRenderer(4));
        bookTable.getColumn("Fine").setCellRenderer(new FineRenderer());
        if (!staff) {
            bookTable.removeColumn(bookTable.getColumn("Borrower"));
        }
        bookTable.setPreferredScrollableViewportSize(new Dimension(staff ? 860 : 1210, 262));
    }

    /**
     * Places the title bar, the pages and the message bar in the window.
     */
    private void buildLayout() {
        pages.setOpaque(false);
        pages.add(buildBooksPage(), PAGE_BOOKS);
        if (staff) {
            pages.add(buildMembersPage(), PAGE_MEMBERS);
            pages.add(buildActivityPage(), PAGE_ACTIVITY);
        } else {
            pages.add(buildAccountPage(), PAGE_ACCOUNT);
        }

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Ui.PAGE_BACKGROUND);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        content.add(pages, BorderLayout.CENTER);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Ui.PAGE_BACKGROUND);
        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);
        root.add(buildMessageBar(), BorderLayout.SOUTH);
        setContentPane(root);
    }

    /**
     * Creates the blue title bar with the badge, the navigation tabs and the signed-in person.
     *
     * @return the header panel
     */
    private JPanel buildHeader() {
        JLabel title = new JLabel("MCS Library");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Military College of Signals  \u00B7  NUST");
        subtitle.setFont(Ui.BASE_FONT.deriveFont(13f));
        subtitle.setForeground(new Color(0xFCD34D));
        JPanel titles = new JPanel(new GridLayout(2, 1, 0, 2));
        titles.setOpaque(false);
        titles.add(title);
        titles.add(subtitle);
        JPanel left = new JPanel(new BorderLayout(12, 0));
        left.setOpaque(false);
        left.add(new LogoBadge(52), BorderLayout.WEST);
        left.add(titles, BorderLayout.CENTER);

        // Navigation tabs
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 14));
        nav.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        String[][] tabs = staff
                ? new String[][]{{"Books", PAGE_BOOKS}, {"Members", PAGE_MEMBERS}, {"Activity Log", PAGE_ACTIVITY}}
                : new String[][]{{"Catalogue", PAGE_BOOKS}, {"My Account", PAGE_ACCOUNT}};
        for (int i = 0; i < tabs.length; i++) {
            NavButton tab = new NavButton(tabs[i][0]);
            String page = tabs[i][1];
            tab.addActionListener(e -> ((CardLayout) pages.getLayout()).show(pages, page));
            group.add(tab);
            nav.add(tab);
            if (i == 0) {
                tab.setSelected(true);
            }
        }

        // Signed-in person
        JLabel name = new JLabel(currentUser.getFullName(), SwingConstants.RIGHT);
        name.setFont(Ui.BOLD_FONT);
        name.setForeground(Color.WHITE);
        JLabel details = new JLabel(currentUser.getRole().getLabel().toUpperCase() + "  \u00B7  "
                + LocalDate.now().format(Ui.DATE_FORMAT), SwingConstants.RIGHT);
        details.setFont(Ui.BASE_FONT.deriveFont(12f));
        details.setForeground(new Color(0xCBD5E1));
        JPanel who = new JPanel(new GridLayout(2, 1, 0, 2));
        who.setOpaque(false);
        who.add(name);
        who.add(details);
        signOutButton.setPreferredSize(new Dimension(108, 34));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        right.setOpaque(false);
        right.add(new Avatar(currentUser.getFullName(), 44, Ui.GOLD));
        right.add(who);
        right.add(signOutButton);

        GradientPanel header = new GradientPanel(true);
        header.setBorder(BorderFactory.createEmptyBorder(10, 24, 14, 12));
        header.add(left, BorderLayout.WEST);
        header.add(nav, BorderLayout.CENTER);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    /**
     * Creates the page with the dashboard and the books (the first page for both roles).
     *
     * @return the books page
     */
    private JPanel buildBooksPage() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);
        page.add(buildDashboard(), BorderLayout.NORTH);
        if (staff) {
            JPanel main = new JPanel(new BorderLayout(16, 0));
            main.setOpaque(false);
            main.add(buildFormCard(), BorderLayout.WEST);
            main.add(buildTableCard(), BorderLayout.CENTER);
            page.add(main, BorderLayout.CENTER);
        } else {
            page.add(buildTableCard(), BorderLayout.CENTER);
        }
        return page;
    }

    /**
     * Creates the row of five colored cards with numbers.
     *
     * @return the dashboard panel
     */
    private JPanel buildDashboard() {
        String[] titles = staff
                ? new String[]{"Total Books", "Available", "Issued", "Reserved", "Overdue"}
                : new String[]{"Total Books", "Available", "My Issued", "My Reserved", "My Fines"};
        Color[] colors = {Ui.BLUE, Ui.GREEN, Ui.ROSE, Ui.AMBER, Ui.VIOLET};
        JPanel dashboard = new JPanel(new GridLayout(1, 5, 16, 0));
        dashboard.setOpaque(false);
        for (int i = 0; i < statCards.length; i++) {
            String prefix = (!staff && i == 4) ? "Rs " : "";
            statCards[i] = new StatCard(titles[i], colors[i], prefix);
            dashboard.add(statCards[i]);
        }
        return dashboard;
    }

    /**
     * Creates the white card on the left with the form fields and the buttons (staff only).
     *
     * @return the form card
     */
    private JPanel buildFormCard() {
        idField = new JTextField(16);
        titleField = new JTextField(16);
        authorField = new JTextField(16);
        categoryCombo = new JComboBox<>(Book.CATEGORIES);
        statusCombo = new JComboBox<>(BookStatus.values());
        addButton = new ModernButton("Add", Ui.BLUE, Color.WHITE);
        updateButton = new ModernButton("Update", Ui.TEAL, Color.WHITE);
        deleteButton = new ModernButton("Delete", Ui.RED, Color.WHITE);
        statusButton = new ModernButton("Set Status", Ui.VIOLET, Color.WHITE);
        clearButton = new ModernButton("Clear", Ui.LIGHT_BUTTON, Ui.TEXT_DARK);
        Ui.styleField(idField);
        Ui.styleField(titleField);
        Ui.styleField(authorField);
        Ui.styleCombo(categoryCombo, 36);
        Ui.styleCombo(statusCombo, 36);

        JPanel card = Ui.createCard();
        card.setLayout(new GridBagLayout());
        int row = 0;
        addToForm(card, Ui.heading("Book Details", 17f), row++, new Insets(0, 0, 4, 0));
        row = addLabeledField(card, row, "Book ID", idField);
        row = addLabeledField(card, row, "Title", titleField);
        row = addLabeledField(card, row, "Author", authorField);
        row = addLabeledField(card, row, "Category", categoryCombo);
        row = addLabeledField(card, row, "Status", statusCombo);
        addToForm(card, buildButtonPanel(), row++, new Insets(12, 0, 0, 0));

        // An empty component that takes the spare height, so the form stays at the top.
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridy = row;
        filler.weighty = 1.0;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        card.add(spacer, filler);
        return card;
    }

    /**
     * Creates the five action buttons of the form in a grid.
     *
     * @return the panel that holds the buttons
     */
    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        addButtonToGrid(panel, addButton, 0, 0, 1);
        addButtonToGrid(panel, updateButton, 1, 0, 1);
        addButtonToGrid(panel, deleteButton, 2, 0, 1);
        addButtonToGrid(panel, statusButton, 0, 1, 2);
        addButtonToGrid(panel, clearButton, 2, 1, 1);
        return panel;
    }

    /**
     * Creates the white card with the search bar and the book table (or a
     * message when there is nothing to show). Students also get the buttons to
     * reserve a book and cancel a reservation below the table.
     *
     * @return the table card
     */
    private JPanel buildTableCard() {
        JPanel card = Ui.createCard();
        card.setLayout(new BorderLayout(0, 12));

        countLabel.setFont(Ui.BASE_FONT.deriveFont(Font.BOLD, 13f));
        countLabel.setForeground(Ui.TEXT_MUTED);
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(Ui.heading(staff ? "Library Collection" : "Library Catalogue", 17f), BorderLayout.WEST);
        titleRow.add(countLabel, BorderLayout.EAST);

        JPanel filters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        filters.setOpaque(false);
        if (!staff) {
            viewCombo = new JComboBox<>(new String[]{ALL_BOOKS, MY_BOOKS});
            Ui.styleCombo(viewCombo, 36);
            viewCombo.setPreferredSize(new Dimension(130, 36));
            filters.add(labeled("View", viewCombo));
        }
        filters.add(labeled("Status", filterCombo));
        JPanel toolbar = new JPanel(new BorderLayout(16, 0));
        toolbar.setOpaque(false);
        toolbar.add(labeled("Search", searchField), BorderLayout.CENTER);
        toolbar.add(filters, BorderLayout.EAST);

        JPanel top = new JPanel(new GridLayout(2, 1, 0, 10));
        top.setOpaque(false);
        top.add(titleRow);
        top.add(toolbar);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Ui.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableCards.setOpaque(false);
        tableCards.add(scrollPane, TABLE_CARD);
        tableCards.add(buildEmptyPanel(), EMPTY_CARD);

        card.add(top, BorderLayout.NORTH);
        card.add(tableCards, BorderLayout.CENTER);
        if (!staff) {
            card.add(buildStudentActions(), BorderLayout.SOUTH);
        }
        return card;
    }

    /**
     * Creates the row below the catalogue with the buttons of a student.
     *
     * @return the panel with the reserve and cancel buttons and a short rule text
     */
    private JPanel buildStudentActions() {
        reserveButton = new ModernButton("Reserve Selected Book", Ui.BLUE, Color.WHITE);
        reserveButton.setPreferredSize(new Dimension(190, 38));
        cancelReservationButton = new ModernButton("Cancel My Reservation", Ui.LIGHT_BUTTON, Ui.TEXT_DARK);
        cancelReservationButton.setPreferredSize(new Dimension(190, 38));

        JLabel rules = new JLabel("You can hold up to " + LibraryManager.MAX_BOOKS_PER_MEMBER
                + " books. Overdue fine: Rs " + Book.FINE_PER_DAY + " per day.");
        rules.setFont(Ui.BASE_FONT.deriveFont(12f));
        rules.setForeground(Ui.TEXT_MUTED);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);
        buttons.add(reserveButton);
        buttons.add(cancelReservationButton);

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(buttons, BorderLayout.WEST);
        row.add(rules, BorderLayout.EAST);
        return row;
    }

    /**
     * Creates the message that is shown instead of the table when the library
     * is empty or no book matches.
     *
     * @return the message panel
     */
    private JPanel buildEmptyPanel() {
        emptyHeading.setFont(Ui.BASE_FONT.deriveFont(Font.BOLD, 18f));
        emptyHeading.setForeground(Ui.TEXT_DARK);
        emptyHint.setFont(Ui.BASE_FONT);
        emptyHint.setForeground(Ui.TEXT_MUTED);

        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 4));
        panel.setOpaque(false);
        panel.add(emptyHeading);
        panel.add(emptyHint);

        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.setBorder(BorderFactory.createLineBorder(Ui.CARD_BORDER));
        wrapper.add(panel);
        return wrapper;
    }

    /**
     * Creates the page with the list of registered students (staff only).
     *
     * @return the members page
     */
    private JPanel buildMembersPage() {
        membersModel = readOnlyModel(new String[]{"Name", "Registration No", "Issued", "Reserved", "Fines"},
                new Class<?>[]{Object.class, Object.class, Integer.class, Integer.class, Integer.class});
        JTable table = new JTable(membersModel);
        Ui.styleTable(table, 40);
        table.getColumn("Fines").setCellRenderer(new FineRenderer());
        table.setPreferredScrollableViewportSize(new Dimension(900, 380));
        return tablePage("Registered Students", membersCountLabel, table);
    }

    /**
     * Creates the page with the activity log (staff only).
     *
     * @return the activity page
     */
    private JPanel buildActivityPage() {
        activityModel = readOnlyModel(new String[]{"Time", "User", "Action", "Details"},
                new Class<?>[]{Object.class, Object.class, Object.class, Object.class});
        JTable table = new JTable(activityModel);
        Ui.styleTable(table, 36);
        table.getColumn("Time").setPreferredWidth(170);
        table.getColumn("User").setPreferredWidth(110);
        table.getColumn("Action").setPreferredWidth(150);
        table.getColumn("Details").setPreferredWidth(430);
        table.setPreferredScrollableViewportSize(new Dimension(900, 380));
        return tablePage("Activity Log", activityCountLabel, table);
    }

    /**
     * Creates the page about the account of the signed-in student.
     *
     * @return the account page
     */
    private JPanel buildAccountPage() {
        JLabel name = Ui.heading(currentUser.getFullName(), 24f);
        JLabel reg = new JLabel("Student  \u00B7  Registration No " + currentUser.getUsername());
        reg.setFont(Ui.BASE_FONT);
        reg.setForeground(Ui.TEXT_MUTED);
        JPanel who = new JPanel(new GridLayout(2, 1, 0, 4));
        who.setOpaque(false);
        who.add(name);
        who.add(reg);
        JPanel identity = new JPanel(new BorderLayout(18, 0));
        identity.setOpaque(false);
        identity.add(new Avatar(currentUser.getFullName(), 72, Ui.BLUE), BorderLayout.WEST);
        identity.add(who, BorderLayout.CENTER);

        JPanel numbers = new JPanel(new GridLayout(3, 2, 10, 10));
        numbers.setOpaque(false);
        for (JLabel value : new JLabel[]{accountIssued, accountReserved, accountFines}) {
            value.setFont(Ui.BASE_FONT.deriveFont(Font.BOLD, 16f));
            value.setForeground(Ui.TEXT_DARK);
        }
        numbers.add(Ui.fieldLabel("Books issued to you"));
        numbers.add(accountIssued);
        numbers.add(Ui.fieldLabel("Books reserved by you"));
        numbers.add(accountReserved);
        numbers.add(Ui.fieldLabel("Fines to pay"));
        numbers.add(accountFines);

        JLabel rules = new JLabel("<html><body style='width:520px'>Borrowing rules: you can hold up to "
                + LibraryManager.MAX_BOOKS_PER_MEMBER + " books (issued and reserved together). "
                + "Reserve a book from the catalogue, then collect it from the library desk; "
                + "the librarian issues it to you. An issued book that is not returned by its due "
                + "date has a fine of Rs " + Book.FINE_PER_DAY + " for each day.</body></html>");
        rules.setFont(Ui.BASE_FONT);
        rules.setForeground(Ui.TEXT_MUTED);

        JPanel card = Ui.createCard();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.CARD_BORDER),
                BorderFactory.createEmptyBorder(28, 32, 28, 32)));
        addToForm(card, identity, 0, new Insets(0, 0, 0, 0));
        addToForm(card, numbers, 1, new Insets(26, 0, 0, 0));
        addToForm(card, rules, 2, new Insets(26, 0, 0, 0));
        GridBagConstraints filler = new GridBagConstraints();
        filler.gridy = 3;
        filler.weighty = 1.0;
        JPanel spacer = new JPanel();
        spacer.setOpaque(false);
        card.add(spacer, filler);

        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    /**
     * Creates a page that is one white card with a heading, a count and a table.
     *
     * @param title      the heading of the card
     * @param countLabel the label shown at the right of the heading
     * @param table      the table to show
     * @return the page
     */
    private JPanel tablePage(String title, JLabel countLabel, JTable table) {
        countLabel.setFont(Ui.BASE_FONT.deriveFont(Font.BOLD, 13f));
        countLabel.setForeground(Ui.TEXT_MUTED);
        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        titleRow.add(Ui.heading(title, 17f), BorderLayout.WEST);
        titleRow.add(countLabel, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Ui.CARD_BORDER));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel card = Ui.createCard();
        card.setLayout(new BorderLayout(0, 12));
        card.add(titleRow, BorderLayout.NORTH);
        card.add(scrollPane, BorderLayout.CENTER);

        JPanel page = new JPanel(new BorderLayout());
        page.setOpaque(false);
        page.add(card, BorderLayout.CENTER);
        return page;
    }

    /**
     * Creates a table model whose cells cannot be edited.
     *
     * @param columns the column titles
     * @param classes the data type of each column (used for sorting)
     * @return the model
     */
    private static DefaultTableModel readOnlyModel(String[] columns, Class<?>[] classes) {
        return new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return classes[column];
            }
        };
    }

    /**
     * Creates the white bar at the bottom that shows the result of the last action.
     *
     * @return the message bar
     */
    private JPanel buildMessageBar() {
        messageLabel.setFont(Ui.BOLD_FONT);
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Ui.CARD_BORDER),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)));
        bar.add(messageLabel, BorderLayout.WEST);
        return bar;
    }

    /**
     * Puts a small label in front of a component.
     *
     * @param text      the text of the label
     * @param component the component
     * @return the panel with both
     */
    private JPanel labeled(String text, Component component) {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setOpaque(false);
        panel.add(Ui.fieldLabel(text), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Adds a small label and an input component below it to the form.
     *
     * @param panel     the form panel
     * @param startRow  the first free row of the form
     * @param labelText the text of the label
     * @param field     the input component
     * @return the next free row
     */
    private int addLabeledField(JPanel panel, int startRow, String labelText, Component field) {
        addToForm(panel, Ui.fieldLabel(labelText), startRow, new Insets(7, 0, 3, 0));
        addToForm(panel, field, startRow + 1, new Insets(0, 0, 0, 0));
        return startRow + 2;
    }

    /**
     * Adds one component to a form, using the full width of the card.
     *
     * @param panel     the form panel
     * @param component the component to add
     * @param row       the row number, starting at 0
     * @param insets    the space around the component
     */
    private void addToForm(JPanel panel, Component component, int row, Insets insets) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = insets;
        panel.add(component, constraints);
    }

    /**
     * Adds a button to the button grid.
     *
     * @param panel  the button panel
     * @param button the button to add
     * @param column the grid column (0, 1 or 2)
     * @param row    the grid row
     * @param width  how many columns the button spans
     */
    private void addButtonToGrid(JPanel panel, JButton button, int column, int row, int width) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = column;
        constraints.gridy = row;
        constraints.gridwidth = width;
        constraints.weightx = 1.0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(3, 4, 3, 4);
        panel.add(button, constraints);
    }

    // ---------- Connecting events ----------

    /**
     * Connects the buttons, the search controls and the table to the methods
     * that handle them.
     */
    private void connectEvents() {
        signOutButton.addActionListener(e -> signOut());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refreshTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refreshTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refreshTable();
            }
        });
        filterCombo.addActionListener(e -> refreshTable());

        if (staff) {
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
                String id = getSelectedTableBookId();
                Book book = (id == null) ? null : manager.findBookById(id);
                if (book != null) {
                    selectedBookId = book.getId();
                    idField.setText(book.getId());
                    titleField.setText(book.getTitle());
                    authorField.setText(book.getAuthor());
                    categoryCombo.setSelectedItem(book.getCategory());
                    statusCombo.setSelectedItem(book.getStatus());
                }
            });
        } else {
            viewCombo.addActionListener(e -> refreshTable());
            reserveButton.addActionListener(e -> reserveSelectedBook());
            cancelReservationButton.addActionListener(e -> cancelMyReservation());
        }
    }

    // ---------- Staff actions ----------

    /**
     * Handles the Add button: adds a new book using the form values.
     */
    private void addBook() {
        if (denyIfNotStaff()) {
            return;
        }
        try {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            manager.addBook(idField.getText(), titleField.getText(), authorField.getText(),
                    (String) categoryCombo.getSelectedItem());
            afterChange("Added book", id + " - " + title);
            clearForm();
            showSuccess("Book \"" + title + "\" (ID " + id + ") added successfully.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Handles the Update button: changes the ID, title, author and category of the
     * selected book (or of the book whose ID is typed in the form).
     */
    private void updateBook() {
        if (denyIfNotStaff()) {
            return;
        }
        try {
            String newId = idField.getText().trim();
            manager.updateBook(getTargetId(), idField.getText(), titleField.getText(),
                    authorField.getText(), (String) categoryCombo.getSelectedItem());
            afterChange("Updated book", newId + " - " + titleField.getText().trim());
            clearForm();
            showSuccess("Book " + newId + " updated successfully.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Handles the Delete button: deletes the selected book after the user
     * confirms. A book that is issued cannot be deleted.
     */
    private void deleteBook() {
        if (denyIfNotStaff()) {
            return;
        }
        try {
            String targetId = getTargetId();
            Book book = manager.findBookById(targetId);
            // An issued book cannot be deleted, so the manager refuses it without asking first.
            if (book != null && book.getStatus() != BookStatus.ISSUED && !confirmDelete(book)) {
                return; // The user chose "No".
            }
            String title = (book == null) ? "" : book.getTitle();
            manager.deleteBook(targetId); // Throws an error if the ID is empty, not found or issued.
            afterChange("Deleted book", targetId.trim() + " - " + title);
            clearForm();
            showSuccess("Book " + targetId.trim() + " deleted successfully.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Handles the Set Status button: gives the selected book the status chosen in
     * the status box. For Issued and Reserved the member (and for Issued the loan
     * period) is asked first. Setting Available returns the book.
     */
    private void setBookStatus() {
        if (denyIfNotStaff()) {
            return;
        }
        try {
            String targetId = getTargetId();
            BookStatus status = (BookStatus) statusCombo.getSelectedItem();
            Book book = manager.findBookById(targetId);

            User member = null;
            int loanDays = 0;
            if (book != null && status != BookStatus.AVAILABLE) {
                BorrowerDetails details = askBorrowerDetails(book, status);
                if (details == null) {
                    return; // The user cancelled, or there is no student to choose.
                }
                member = details.member;
                loanDays = details.loanDays;
            }

            // Remember what is needed for the message before the book changes.
            BookStatus before = (book == null) ? null : book.getStatus();
            int fine = (book == null) ? 0 : book.getFine(LocalDate.now());
            int lateDays = (book == null) ? 0 : book.getOverdueDays(LocalDate.now());

            manager.setStatus(targetId, status,
                    (member == null) ? "" : member.getFullName(),
                    (member == null) ? "" : member.getUsername(), loanDays);

            Book changed = manager.findBookById(targetId);
            afterChange(describeActivity(changed, before), changed.getId() + " - " + changed.getTitle()
                    + (member == null ? "" : " (" + member + ")"));
            clearForm();
            showSuccess(describeStatusChange(changed, before, fine, lateDays));
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Stops a student from using a staff action.
     *
     * @return true if the person is not staff (and an error was shown)
     */
    private boolean denyIfNotStaff() {
        if (!staff) {
            showError("Only staff can do this.");
            return true;
        }
        return false;
    }

    // ---------- Student actions ----------

    /**
     * Handles the Reserve button: reserves the book selected in the table for the student.
     */
    private void reserveSelectedBook() {
        String id = getSelectedTableBookId();
        if (id == null) {
            showError("Please select a book in the table first.");
            return;
        }
        try {
            manager.reserveBook(id, currentUser.getFullName(), currentUser.getUsername());
            Book book = manager.findBookById(id);
            afterChange("Reserved book", book.getId() + " - " + book.getTitle());
            showSuccess("You reserved \"" + book.getTitle() + "\". Please collect it from the library desk.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Handles the Cancel button: cancels the student's own reservation of the selected book.
     */
    private void cancelMyReservation() {
        String id = getSelectedTableBookId();
        if (id == null) {
            showError("Please select a book in the table first.");
            return;
        }
        try {
            manager.cancelReservation(id, currentUser.getUsername());
            Book book = manager.findBookById(id);
            afterChange("Cancelled reservation", book.getId() + " - " + book.getTitle());
            showSuccess("Your reservation of \"" + book.getTitle() + "\" was cancelled.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Signs the person out: the window closes and the sign-in window is shown again.
     */
    private void signOut() {
        activity.record(currentUser.getUsername(), "Signed out", currentUser.getRole().getLabel());
        saveActivity();
        messageTimer.stop();
        dispose();
        onSignOut.run();
    }

    // ---------- Helper methods ----------

    /**
     * Saves the data, writes an entry in the activity log and refreshes all pages.
     * It is called after every successful change.
     *
     * @param action  what was done
     * @param details more information about it
     */
    private void afterChange(String action, String details) {
        activity.record(currentUser.getUsername(), action, details);
        saveBooks();
        saveActivity();
        refreshAll();
    }

    /**
     * Chooses the activity-log text for a status change.
     *
     * @param book   the book after the change
     * @param before the status before the change
     * @return the action text
     */
    private String describeActivity(Book book, BookStatus before) {
        if (book.getStatus() == BookStatus.ISSUED) {
            return "Issued book";
        }
        if (book.getStatus() == BookStatus.RESERVED) {
            return "Reserved book for member";
        }
        return (before == BookStatus.ISSUED) ? "Returned book" : "Set book available";
    }

    /**
     * Writes the success message for a status change.
     *
     * @param book     the book after the change
     * @param before   the status before the change
     * @param fine     the fine the book had before the change (in rupees)
     * @param lateDays the number of days the book was overdue before the change
     * @return the message to show
     */
    private String describeStatusChange(Book book, BookStatus before, int fine, int lateDays) {
        if (book.getStatus() == BookStatus.ISSUED) {
            return "Book " + book.getId() + " issued to " + book.getBorrower()
                    + ". Due date: " + book.getDueDate().format(Ui.DATE_FORMAT) + ".";
        }
        if (book.getStatus() == BookStatus.RESERVED) {
            return "Book " + book.getId() + " reserved for " + book.getBorrower() + ".";
        }
        if (before == BookStatus.ISSUED && fine > 0) {
            return "Book " + book.getId() + " returned. Overdue fine: Rs " + fine
                    + " (" + lateDays + " days late).";
        }
        if (before == BookStatus.ISSUED) {
            return "Book " + book.getId() + " returned. Thank you.";
        }
        return "Status of book " + book.getId() + " set to Available.";
    }

    /**
     * Finds out which book a staff action applies to: the selected table row if there
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
     * Returns the ID of the book that is selected in the table.
     *
     * @return the book ID, or null if no row is selected
     */
    private String getSelectedTableBookId() {
        int viewRow = bookTable.getSelectedRow();
        if (viewRow == -1) {
            return null;
        }
        // The table may be sorted, so convert the row to the row of the table model.
        int modelRow = bookTable.convertRowIndexToModel(viewRow);
        return (String) tableModel.getValueAt(modelRow, 0);
    }

    /**
     * Returns the status chosen in the filter box.
     *
     * @return the chosen status, or null if "All Statuses" is chosen
     */
    private BookStatus getStatusFilter() {
        Object chosen = filterCombo.getSelectedItem();
        if (chosen instanceof BookStatus) {
            return (BookStatus) chosen;
        }
        return null;
    }

    /**
     * Reloads everything that is shown: the book table, the dashboard cards and
     * the other pages of the person's role.
     */
    private void refreshAll() {
        refreshTable();
        refreshStats();
        if (staff) {
            refreshMembers();
            refreshActivity();
        } else {
            refreshAccount();
        }
    }

    /**
     * Updates the numbers on the dashboard cards.
     */
    private void refreshStats() {
        LocalDate today = LocalDate.now();
        statCards[0].setValue(manager.getBookCount());
        statCards[1].setValue(manager.countByStatus(BookStatus.AVAILABLE));
        if (staff) {
            statCards[2].setValue(manager.countByStatus(BookStatus.ISSUED));
            statCards[3].setValue(manager.countByStatus(BookStatus.RESERVED));
            statCards[4].setValue(manager.countOverdue(today));
        } else {
            String me = currentUser.getUsername();
            statCards[2].setValue(manager.countForMember(me, BookStatus.ISSUED));
            statCards[3].setValue(manager.countForMember(me, BookStatus.RESERVED));
            statCards[4].setValue(manager.totalFineForMember(me, today));
        }
    }

    /**
     * Reloads the book table with the books that match the search text, the status
     * filter and (for students) the chosen view, and shows either the table or a
     * message when there is nothing to show.
     */
    private void refreshTable() {
        String query = searchField.getText();
        BookStatus filter = getStatusFilter();
        boolean onlyMine = !staff && viewCombo != null && MY_BOOKS.equals(viewCombo.getSelectedItem());
        ArrayList<Book> shown = manager.searchBooks(query, filter,
                onlyMine ? currentUser.getUsername() : null);

        LocalDate today = LocalDate.now();
        tableModel.setRowCount(0);
        selectedBookId = null; // The rows were replaced, so nothing is selected any more.
        for (Book book : shown) {
            // Students see fines only for their own books.
            boolean showFine = staff || book.getBorrowerId().equalsIgnoreCase(currentUser.getUsername());
            tableModel.addRow(new Object[]{
                book.getId(), book.getTitle(), book.getAuthor(), book.getCategory(),
                book.getStatus(), book.getBorrower(), book.getDueDate(),
                showFine ? book.getFine(today) : 0
            });
        }

        boolean filtering = !query.trim().isEmpty() || filter != null;
        CardLayout cards = (CardLayout) tableCards.getLayout();
        if (manager.isEmpty()) {
            countLabel.setText("The library is empty.");
            emptyHeading.setText("The library is empty.");
            emptyHint.setText(staff ? "Add a book using the form on the left."
                    : "Please check again later.");
            cards.show(tableCards, EMPTY_CARD);
        } else if (shown.isEmpty() && onlyMine && !filtering) {
            countLabel.setText("My books: 0");
            emptyHeading.setText("You have no issued or reserved books.");
            emptyHint.setText("Reserve a book from the catalogue.");
            cards.show(tableCards, EMPTY_CARD);
        } else if (shown.isEmpty()) {
            countLabel.setText("Showing 0 of " + manager.getBookCount() + " books");
            emptyHeading.setText("No matching books found.");
            emptyHint.setText("Try a different search text or status filter.");
            cards.show(tableCards, EMPTY_CARD);
        } else {
            if (onlyMine) {
                countLabel.setText("My books: " + shown.size());
            } else if (filtering) {
                countLabel.setText("Showing " + shown.size() + " of " + manager.getBookCount() + " books");
            } else {
                countLabel.setText("Total books: " + manager.getBookCount());
            }
            cards.show(tableCards, TABLE_CARD);
        }
    }

    /**
     * Reloads the table of students on the members page.
     */
    private void refreshMembers() {
        LocalDate today = LocalDate.now();
        membersModel.setRowCount(0);
        ArrayList<User> students = users.getStudents();
        for (User student : students) {
            String id = student.getUsername();
            membersModel.addRow(new Object[]{
                student.getFullName(), id,
                manager.countForMember(id, BookStatus.ISSUED),
                manager.countForMember(id, BookStatus.RESERVED),
                manager.totalFineForMember(id, today)
            });
        }
        membersCountLabel.setText(students.isEmpty() ? "No students have registered yet."
                : "Students: " + students.size());
    }

    /**
     * Reloads the table of the activity log.
     */
    private void refreshActivity() {
        activityModel.setRowCount(0);
        ArrayList<ActivityEntry> entries = activity.getEntries();
        int shown = Math.min(entries.size(), MAX_ACTIVITY_ROWS);
        for (int i = 0; i < shown; i++) {
            ActivityEntry entry = entries.get(i);
            activityModel.addRow(new Object[]{
                entry.getTime().format(TIME_FORMAT), entry.getUser(), entry.getAction(), entry.getDetails()
            });
        }
        activityCountLabel.setText(entries.isEmpty() ? "No activity yet."
                : "Latest " + shown + " of " + entries.size() + " actions");
    }

    /**
     * Updates the numbers on the account page of a student.
     */
    private void refreshAccount() {
        String me = currentUser.getUsername();
        accountIssued.setText(String.valueOf(manager.countForMember(me, BookStatus.ISSUED)));
        accountReserved.setText(String.valueOf(manager.countForMember(me, BookStatus.RESERVED)));
        int fines = manager.totalFineForMember(me, LocalDate.now());
        accountFines.setText(fines > 0 ? "Rs " + fines : "None");
    }

    /**
     * Empties the form, resets the selection boxes and removes the table selection.
     */
    private void clearForm() {
        idField.setText("");
        titleField.setText("");
        authorField.setText("");
        categoryCombo.setSelectedIndex(0);
        statusCombo.setSelectedItem(BookStatus.AVAILABLE);
        bookTable.clearSelection();
        selectedBookId = null;
    }

    /**
     * Saves all books to the file after a change. Shows an error if saving fails.
     */
    private void saveBooks() {
        if (storage == null) {
            return;
        }
        try {
            storage.saveBooks(manager.getAllBooks());
        } catch (IOException ex) {
            showError("The books could not be saved to the file: " + ex.getMessage());
        }
    }

    /**
     * Saves the activity log to its file. Shows an error if saving fails.
     */
    private void saveActivity() {
        if (storage == null) {
            return;
        }
        try {
            ArrayList<ActivityEntry> oldestFirst = activity.getEntries();
            java.util.Collections.reverse(oldestFirst);
            storage.saveActivity(oldestFirst);
        } catch (IOException ex) {
            showError("The activity log could not be saved to the file: " + ex.getMessage());
        }
    }

    /**
     * Shows a success message in green at the bottom of the window. The message
     * disappears after a few seconds.
     *
     * @param message the text to show
     */
    private void showSuccess(String message) {
        messageLabel.setForeground(Ui.SUCCESS_COLOR);
        messageLabel.setText(message);
        messageTimer.restart();
    }

    /**
     * Shows an error message in a dialog and in red at the bottom of the window.
     *
     * @param message the error text to show
     */
    void showError(String message) {
        messageLabel.setForeground(Ui.ERROR_COLOR);
        messageLabel.setText("Error: " + message);
        messageTimer.restart();
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

    /**
     * Asks staff which student gets the book, and the loan period when the book is
     * being issued. If there is no student account, an error is shown instead.
     *
     * @param book   the book that is being issued or reserved
     * @param status the new status (Issued or Reserved)
     * @return the choice of the user, or null if the user cancelled or there is no student
     */
    BorrowerDetails askBorrowerDetails(Book book, BookStatus status) {
        ArrayList<User> students = users.getStudents();
        if (students.isEmpty()) {
            showError("No student accounts exist yet. A student must create an account first.");
            return null;
        }
        JComboBox<User> memberBox = new JComboBox<>(students.toArray(new User[0]));
        for (User student : students) {
            if (student.getUsername().equalsIgnoreCase(book.getBorrowerId())) {
                memberBox.setSelectedItem(student); // a reserved book is usually issued to the same student
            }
        }
        JSpinner daysSpinner = new JSpinner(new SpinnerNumberModel(
                LibraryManager.DEFAULT_LOAN_DAYS, 1, LibraryManager.MAX_LOAN_DAYS, 1));

        boolean issuing = (status == BookStatus.ISSUED);
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.add(new JLabel("Book: " + book.getTitle() + " (" + book.getId() + ")"));
        panel.add(new JLabel(issuing ? "Issue to student:" : "Reserve for student:"));
        panel.add(memberBox);
        if (issuing) {
            panel.add(new JLabel("Loan period (days):"));
            panel.add(daysSpinner);
        }

        int choice = JOptionPane.showConfirmDialog(this, panel,
                issuing ? "Issue Book" : "Reserve Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return null;
        }
        return new BorrowerDetails((User) memberBox.getSelectedItem(), (Integer) daysSpinner.getValue());
    }
}
