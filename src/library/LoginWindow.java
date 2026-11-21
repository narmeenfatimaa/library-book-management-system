package library;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import library.Ui.GradientPanel;
import library.Ui.LinkButton;
import library.Ui.LogoBadge;
import library.Ui.ModernButton;
import library.Ui.SegmentButton;

/**
 * The sign-in window of the MCS Library Management System. A person chooses to
 * sign in as a student or as staff. New students can create an account here.
 * The window changes its colors and texts when the role is changed.
 *
 * @author Narmeen Fatima
 * @author Faique Mustafa
 * @author Usaid Rauf
 */
public class LoginWindow extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String LOGIN_VIEW = "login";
    private static final String REGISTER_VIEW = "register";

    /** The accounts that are checked when a person signs in. */
    private final transient UserManager users;
    /** Called with the account after a successful sign-in. */
    private final transient Consumer<User> onLogin;
    /** Called with the new account after a student has registered. */
    private final transient Consumer<User> onRegistered;

    /** The role that is currently chosen. */
    private Role selectedRole = Role.STUDENT;

    /** Button that chooses the student role. */
    private final SegmentButton studentToggle = new SegmentButton("Student", Ui.BLUE);
    /** Button that chooses the staff role. */
    private final SegmentButton staffToggle = new SegmentButton("Staff", Ui.VIOLET);

    /** Panel that switches between the sign-in form and the registration form. */
    private final JPanel formCards = new JPanel(new CardLayout());

    /** Text on the left side that changes with the role. */
    private final JLabel brandTagline = new JLabel();
    /** Heading of the sign-in form. */
    private final JLabel formTitle = new JLabel();
    /** Small text below the heading of the sign-in form. */
    private final JLabel formHint = new JLabel();
    /** Label of the username field (changes with the role). */
    private final JLabel idLabel = Ui.fieldLabel("");
    /** Reminder of the default staff account (shown for staff only). */
    private final JLabel demoLabel = new JLabel();
    /** Message below the sign-in form (error or success). */
    private final JLabel loginMessage = new JLabel(" ");
    /** Field for the username or registration number. */
    private final JTextField usernameField = new JTextField(18);
    /** Field for the password. */
    private final JPasswordField passwordField = new JPasswordField(18);
    /** Box that shows the typed password as text. */
    private final JCheckBox showPassword = new JCheckBox("Show password");
    /** Button that signs in. */
    private final ModernButton signInButton = new ModernButton("Sign In", Ui.BLUE, Color.WHITE);
    /** Row with the link to the registration form (students only). */
    private final JPanel registerRow = new JPanel();

    /** Field for the full name in the registration form. */
    private final JTextField fullNameField = new JTextField(18);
    /** Field for the registration number in the registration form. */
    private final JTextField regNoField = new JTextField(18);
    /** Field for the new password in the registration form. */
    private final JPasswordField newPasswordField = new JPasswordField(18);
    /** Field for the repeated password in the registration form. */
    private final JPasswordField confirmPasswordField = new JPasswordField(18);
    /** Error message below the registration form. */
    private final JLabel registerMessage = new JLabel(" ");
    /** Button that creates the account. */
    private final ModernButton createButton = new ModernButton("Create Account", Ui.GREEN, Color.WHITE);

    /**
     * Builds the sign-in window.
     *
     * @param users        the accounts that are checked when a person signs in
     * @param onLogin      called with the account after a successful sign-in
     * @param onRegistered called with the new account after a student has registered
     */
    public LoginWindow(UserManager users, Consumer<User> onLogin, Consumer<User> onRegistered) {
        super("MCS Library Management System - Sign In");
        this.users = users;
        this.onLogin = onLogin;
        this.onRegistered = onRegistered;

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildBrandPanel(), BorderLayout.WEST);
        root.add(buildFormSide(), BorderLayout.CENTER);
        setContentPane(root);

        ButtonGroup group = new ButtonGroup();
        group.add(studentToggle);
        group.add(staffToggle);
        studentToggle.addActionListener(e -> selectRole(Role.STUDENT));
        staffToggle.addActionListener(e -> selectRole(Role.STAFF));
        signInButton.addActionListener(e -> attemptLogin());
        createButton.addActionListener(e -> attemptRegister());
        showPassword.addActionListener(e -> passwordField.setEchoChar(
                showPassword.isSelected() ? (char) 0 : '\u2022'));

        selectRole(Role.STUDENT);
        showView(LOGIN_VIEW);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(980, 640);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // ---------- Building the window ----------

    /**
     * Creates the blue panel on the left with the college badge and a short text.
     *
     * @return the brand panel
     */
    private JPanel buildBrandPanel() {
        JLabel name = new JLabel("MCS Library");
        name.setFont(new Font("SansSerif", Font.BOLD, 34));
        name.setForeground(Color.WHITE);

        JLabel college = new JLabel("Military College of Signals  \u00B7  NUST");
        college.setFont(Ui.BOLD_FONT);
        college.setForeground(new Color(0xFCD34D));

        brandTagline.setFont(Ui.BASE_FONT.deriveFont(16f));
        brandTagline.setForeground(new Color(0xE2E8F0));

        JLabel[] points = {
            bullet("Search the whole catalogue in seconds"),
            bullet("Reserve books and track due dates"),
            bullet("Clear fines and borrowing limits")
        };

        JLabel footer = new JLabel("Software Construction Open-Ended Lab");
        footer.setFont(Ui.BASE_FONT.deriveFont(12f));
        footer.setForeground(new Color(0x93A8D6));

        GradientPanel panel = new GradientPanel(false);
        panel.setLayout(new GridBagLayout());
        panel.setPreferredSize(new Dimension(420, 0));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(0, 44, 0, 30);
        int row = 0;
        c.gridy = row++;
        c.insets = new Insets(0, 44, 18, 30);
        panel.add(new LogoBadge(96), c);
        c.gridy = row++;
        c.insets = new Insets(0, 44, 4, 30);
        panel.add(name, c);
        c.gridy = row++;
        c.insets = new Insets(0, 44, 26, 30);
        panel.add(college, c);
        c.gridy = row++;
        c.insets = new Insets(0, 44, 26, 30);
        panel.add(brandTagline, c);
        for (JLabel point : points) {
            c.gridy = row++;
            c.insets = new Insets(0, 44, 8, 30);
            panel.add(point, c);
        }
        c.gridy = row++;
        c.insets = new Insets(40, 44, 0, 30);
        panel.add(footer, c);
        return panel;
    }

    /**
     * Creates a white bullet line for the brand panel.
     *
     * @param text the text of the line
     * @return the label
     */
    private JLabel bullet(String text) {
        JLabel label = new JLabel("\u2022  " + text);
        label.setFont(Ui.BASE_FONT);
        label.setForeground(Color.WHITE);
        return label;
    }

    /**
     * Creates the right side of the window with the sign-in and registration forms.
     *
     * @return the panel that holds the forms
     */
    private JPanel buildFormSide() {
        formCards.setOpaque(false);
        formCards.add(buildLoginForm(), LOGIN_VIEW);
        formCards.add(buildRegisterForm(), REGISTER_VIEW);

        JPanel card = Ui.createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Ui.CARD_BORDER),
                BorderFactory.createEmptyBorder(28, 34, 28, 34)));
        card.setPreferredSize(new Dimension(420, 0));
        card.add(formCards, BorderLayout.CENTER);

        JPanel side = new JPanel(new GridBagLayout());
        side.setBackground(Ui.PAGE_BACKGROUND);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.VERTICAL;
        c.weighty = 1.0;
        c.insets = new Insets(40, 0, 40, 0);
        side.add(card, c);
        return side;
    }

    /**
     * Creates the sign-in form.
     *
     * @return the form panel
     */
    private JPanel buildLoginForm() {
        formTitle.setFont(Ui.BASE_FONT.deriveFont(Font.BOLD, 24f));
        formTitle.setForeground(Ui.TEXT_DARK);
        formHint.setFont(Ui.BASE_FONT);
        formHint.setForeground(Ui.TEXT_MUTED);

        JPanel toggles = new JPanel(new GridLayout(1, 2, 8, 0));
        toggles.setOpaque(false);
        toggles.add(studentToggle);
        toggles.add(staffToggle);

        Ui.styleField(usernameField);
        Ui.styleField(passwordField);
        showPassword.setOpaque(false);
        showPassword.setFont(Ui.BASE_FONT.deriveFont(12f));
        showPassword.setForeground(Ui.TEXT_MUTED);
        showPassword.setFocusPainted(false);

        demoLabel.setFont(Ui.BASE_FONT.deriveFont(12f));
        demoLabel.setForeground(Ui.TEXT_MUTED);
        demoLabel.setText("Default staff account:  " + UserManager.DEFAULT_STAFF_USERNAME
                + " / " + UserManager.DEFAULT_STAFF_PASSWORD);
        loginMessage.setFont(Ui.BOLD_FONT.deriveFont(12f));
        signInButton.setPreferredSize(new Dimension(0, 44));

        registerRow.setOpaque(false);
        JLabel question = new JLabel("New student?");
        question.setFont(Ui.BASE_FONT);
        question.setForeground(Ui.TEXT_MUTED);
        LinkButton registerLink = new LinkButton("Create an account");
        registerLink.addActionListener(e -> showView(REGISTER_VIEW));
        registerRow.add(question);
        registerRow.add(registerLink);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        int row = 0;
        addRow(form, formTitle, row++, 0);
        addRow(form, formHint, row++, 2);
        addRow(form, toggles, row++, 18);
        addRow(form, idLabel, row++, 18);
        addRow(form, usernameField, row++, 4);
        addRow(form, Ui.fieldLabel("Password"), row++, 12);
        addRow(form, passwordField, row++, 4);
        addRow(form, showPassword, row++, 6);
        addRow(form, loginMessage, row++, 6);
        addRow(form, signInButton, row++, 6);
        addRow(form, demoLabel, row++, 10);
        addRow(form, registerRow, row++, 4);
        return form;
    }

    /**
     * Creates the form in which a new student creates an account.
     *
     * @return the form panel
     */
    private JPanel buildRegisterForm() {
        JLabel title = new JLabel("Create Student Account");
        title.setFont(Ui.BASE_FONT.deriveFont(Font.BOLD, 24f));
        title.setForeground(Ui.TEXT_DARK);
        JLabel hint = new JLabel("Use your registration number to sign in later.");
        hint.setFont(Ui.BASE_FONT);
        hint.setForeground(Ui.TEXT_MUTED);

        Ui.styleField(fullNameField);
        Ui.styleField(regNoField);
        Ui.styleField(newPasswordField);
        Ui.styleField(confirmPasswordField);
        registerMessage.setFont(Ui.BOLD_FONT.deriveFont(12f));
        registerMessage.setForeground(Ui.ERROR_COLOR);
        createButton.setPreferredSize(new Dimension(0, 44));

        LinkButton back = new LinkButton("Back to sign in");
        back.addActionListener(e -> showView(LOGIN_VIEW));
        JPanel backRow = new JPanel();
        backRow.setOpaque(false);
        backRow.add(back);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        int row = 0;
        addRow(form, title, row++, 0);
        addRow(form, hint, row++, 2);
        addRow(form, Ui.fieldLabel("Full Name"), row++, 16);
        addRow(form, fullNameField, row++, 4);
        addRow(form, Ui.fieldLabel("Registration Number"), row++, 10);
        addRow(form, regNoField, row++, 4);
        addRow(form, Ui.fieldLabel("Password (at least " + UserManager.MIN_PASSWORD_LENGTH
                + " characters)"), row++, 10);
        addRow(form, newPasswordField, row++, 4);
        addRow(form, Ui.fieldLabel("Confirm Password"), row++, 10);
        addRow(form, confirmPasswordField, row++, 4);
        addRow(form, registerMessage, row++, 8);
        addRow(form, createButton, row++, 6);
        addRow(form, backRow, row++, 6);
        return form;
    }

    /**
     * Adds one component to a form, using the full width.
     *
     * @param panel     the form panel
     * @param component the component to add
     * @param row       the row number
     * @param top       the space above the component
     */
    private static void addRow(JPanel panel, Component component, int row, int top) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.NORTHWEST;
        c.insets = new Insets(top, 0, 0, 0);
        panel.add(component, c);
    }

    // ---------- Behaviour ----------

    /**
     * Changes the chosen role: the colors, the texts and the labels of the window change with it.
     *
     * @param role the role to choose
     */
    void selectRole(Role role) {
        selectedRole = role;
        boolean student = (role == Role.STUDENT);
        studentToggle.setSelected(student);
        staffToggle.setSelected(!student);

        Color accent = student ? Ui.BLUE : Ui.VIOLET;
        signInButton.setBaseColor(accent);
        studentToggle.setAccent(Ui.BLUE);
        staffToggle.setAccent(Ui.VIOLET);

        formTitle.setText(student ? "Student Sign In" : "Staff Sign In");
        formHint.setText(student ? "Browse the catalogue and reserve books."
                : "Manage books, members and loans.");
        idLabel.setText(student ? "Registration Number" : "Staff Username");
        brandTagline.setText(student
                ? "<html><body style='width:300px'>Your university library, in one place. "
                + "Find a book, reserve it and see when it is due.</body></html>"
                : "<html><body style='width:300px'>The librarian console. "
                + "Manage the collection, issue and return books, and review all activity.</body></html>");
        demoLabel.setVisible(!student);
        registerRow.setVisible(student);
        loginMessage.setForeground(Ui.ERROR_COLOR);
        loginMessage.setText(" ");
        usernameField.setText("");
        passwordField.setText("");
        getRootPane().setDefaultButton(signInButton);
    }

    /**
     * Shows the sign-in form or the registration form.
     *
     * @param view the name of the view to show
     */
    private void showView(String view) {
        ((CardLayout) formCards.getLayout()).show(formCards, view);
        registerMessage.setText(" ");
        if (REGISTER_VIEW.equals(view)) {
            getRootPane().setDefaultButton(createButton);
        } else {
            getRootPane().setDefaultButton(signInButton);
        }
    }

    /**
     * Checks the typed username and password. On success the sign-in callback is
     * called; otherwise a message is shown below the form.
     */
    void attemptLogin() {
        User user;
        try {
            user = users.login(usernameField.getText(),
                    new String(passwordField.getPassword()), selectedRole);
        } catch (IllegalArgumentException ex) {
            loginMessage.setForeground(Ui.ERROR_COLOR);
            loginMessage.setText(ex.getMessage());
            passwordField.setText("");
            return;
        }
        passwordField.setText("");
        loginMessage.setText(" ");
        onLogin.accept(user);
    }

    /**
     * Creates a student account from the registration form. On success the sign-in
     * form is shown again; otherwise a message is shown below the registration form.
     */
    void attemptRegister() {
        User user;
        try {
            user = users.registerStudent(fullNameField.getText(), regNoField.getText(),
                    new String(newPasswordField.getPassword()),
                    new String(confirmPasswordField.getPassword()));
        } catch (IllegalArgumentException ex) {
            registerMessage.setText(ex.getMessage());
            return;
        }
        onRegistered.accept(user);

        fullNameField.setText("");
        regNoField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        showView(LOGIN_VIEW);
        selectRole(Role.STUDENT);
        usernameField.setText(user.getUsername());
        loginMessage.setForeground(Ui.SUCCESS_COLOR);
        loginMessage.setText("Account created. Please sign in.");
    }
}
