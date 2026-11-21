package library;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Colors, fonts and small drawing components shared by the login window and the
 * main window. Everything in this class only changes how the program looks.
 */
final class Ui {

    private Ui() {
    }

    // ---------- Colors, fonts and formats ----------

    static final Color PAGE_BACKGROUND = new Color(0xEEF2F9);
    static final Color NAVY_DARK = new Color(0x0B1F4B);
    static final Color NAVY_LIGHT = new Color(0x1E3A8A);
    static final Color GOLD = new Color(0xF5B301);
    static final Color CARD_BORDER = new Color(0xDDE3EE);
    static final Color FIELD_BORDER = new Color(0xC3CCDB);
    static final Color TEXT_DARK = new Color(0x1E293B);
    static final Color TEXT_MUTED = new Color(0x64748B);
    static final Color SELECTION_COLOR = new Color(0xDBEAFE);
    static final Color HOVER_COLOR = new Color(0xEFF6FF);
    static final Color STRIPE_COLOR = new Color(0xF8FAFD);
    static final Color TABLE_HEADER_COLOR = new Color(0xE8EEF9);

    static final Color BLUE = new Color(0x2563EB);
    static final Color GREEN = new Color(0x059669);
    static final Color ROSE = new Color(0xE11D48);
    static final Color AMBER = new Color(0xD97706);
    static final Color VIOLET = new Color(0x7C3AED);
    static final Color TEAL = new Color(0x0D9488);
    static final Color RED = new Color(0xDC2626);
    static final Color LIGHT_BUTTON = new Color(0xE2E8F0);

    static final Color ERROR_COLOR = new Color(0xB91C1C);
    static final Color SUCCESS_COLOR = new Color(0x15803D);

    static final Font BASE_FONT = new Font("SansSerif", Font.PLAIN, 14);
    static final Font BOLD_FONT = new Font("SansSerif", Font.BOLD, 14);

    static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    // ---------- Helper methods ----------

    /**
     * Gives a text field the look used in the program (also works for password fields).
     *
     * @param field the field to style
     */
    static void styleField(JTextField field) {
        field.setFont(BASE_FONT);
        field.setForeground(TEXT_DARK);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));
    }

    /**
     * Gives a selection box the look used in the program.
     *
     * @param box    the box to style
     * @param height the height of the box in pixels
     */
    static void styleCombo(JComboBox<?> box, int height) {
        box.setFont(BASE_FONT);
        box.setBackground(Color.WHITE);
        box.setPreferredSize(new Dimension(0, height));
    }

    /**
     * Creates a small bold gray label that is placed above an input field.
     *
     * @param text the text of the label
     * @return the label
     */
    static JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BASE_FONT.deriveFont(Font.BOLD, 12f));
        label.setForeground(TEXT_MUTED);
        return label;
    }

    /**
     * Creates a bold dark label, used as a heading.
     *
     * @param text the text of the label
     * @param size the font size
     * @return the label
     */
    static JLabel heading(String text, float size) {
        JLabel label = new JLabel(text);
        label.setFont(BASE_FONT.deriveFont(Font.BOLD, size));
        label.setForeground(TEXT_DARK);
        return label;
    }

    /**
     * Creates an empty white card with a thin border and some padding.
     *
     * @return the card panel
     */
    static JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CARD_BORDER),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        return card;
    }

    /**
     * Returns the first letters of the first two words of a name.
     *
     * @param name the name
     * @return the initials in capital letters (for example "NF")
     */
    static String initials(String name) {
        StringBuilder letters = new StringBuilder();
        for (String part : name.trim().split("\\s+")) {
            if (!part.isEmpty() && letters.length() < 2) {
                letters.append(Character.toUpperCase(part.charAt(0)));
            }
        }
        return letters.length() == 0 ? "?" : letters.toString();
    }

    /**
     * Chooses the background color of a table row: blue when selected, light blue
     * when the mouse is over it, otherwise white and light gray in turns.
     *
     * @param table      the table
     * @param isSelected true if the row is selected
     * @param row        the row number
     * @return the background color
     */
    static Color rowColor(JTable table, boolean isSelected, int row) {
        if (isSelected) {
            return SELECTION_COLOR;
        }
        Object hover = table.getClientProperty("hoverRow");
        if (hover instanceof Integer && ((Integer) hover) == row) {
            return HOVER_COLOR;
        }
        return (row % 2 == 0) ? Color.WHITE : STRIPE_COLOR;
    }

    /**
     * Gives a table the look used in the program: row height, colors, sorting by
     * clicking a column title, and a highlight for the row under the mouse.
     *
     * @param table     the table to style
     * @param rowHeight the height of a row in pixels
     */
    static void styleTable(JTable table, int rowHeight) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(rowHeight);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(SELECTION_COLOR);
        table.setSelectionForeground(TEXT_DARK);
        table.setAutoCreateRowSorter(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setDefaultRenderer(Object.class, new CellRenderer());
        table.setDefaultRenderer(Number.class, new CellRenderer());

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer());
        header.setPreferredSize(new Dimension(0, 38));
        header.setReorderingAllowed(false);

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setHoverRow(table, table.rowAtPoint(e.getPoint()));
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                setHoverRow(table, -1);
            }
        });
    }

    /**
     * Remembers which row the mouse is over and repaints the table when it changes.
     *
     * @param table the table
     * @param row   the row under the mouse (-1 if none)
     */
    private static void setHoverRow(JTable table, int row) {
        Object old = table.getClientProperty("hoverRow");
        if (old == null || ((Integer) old) != row) {
            table.putClientProperty("hoverRow", row);
            table.repaint();
        }
    }

    // ---------- Drawing components ----------

    /**
     * A panel with a blue gradient background and, optionally, a thin gold line at the bottom.
     */
    static class GradientPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final boolean goldLine;

        GradientPanel(boolean goldLine) {
            super(new BorderLayout());
            this.goldLine = goldLine;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setPaint(new GradientPaint(0, 0, NAVY_DARK, getWidth(), getHeight(), NAVY_LIGHT));
            g2.fillRect(0, 0, getWidth(), getHeight());
            if (goldLine) {
                g2.setColor(GOLD);
                g2.fillRect(0, getHeight() - 4, getWidth(), 4);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * A round gold badge with the letters MCS, drawn instead of a logo picture.
     */
    static class LogoBadge extends JPanel {

        private static final long serialVersionUID = 1L;

        LogoBadge(int size) {
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(GOLD);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(NAVY_DARK);
            g2.fillOval(4, 4, getWidth() - 8, getHeight() - 8);
            g2.setColor(GOLD);
            g2.setFont(new Font("SansSerif", Font.BOLD, getWidth() / 3));
            FontMetrics metrics = g2.getFontMetrics();
            String text = "MCS";
            g2.drawString(text, (getWidth() - metrics.stringWidth(text)) / 2,
                    (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());
            g2.dispose();
        }
    }

    /**
     * A round badge with the initials of a person.
     */
    static class Avatar extends JPanel {

        private static final long serialVersionUID = 1L;

        private final String letters;
        private final Color color;

        Avatar(String name, int size, Color color) {
            this.letters = initials(name);
            this.color = color;
            setOpaque(false);
            setPreferredSize(new Dimension(size, size));
            setMinimumSize(new Dimension(size, size));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillOval(0, 0, getWidth(), getHeight());
            g2.setColor(NAVY_DARK);
            g2.setFont(new Font("SansSerif", Font.BOLD, getWidth() * 2 / 5));
            FontMetrics metrics = g2.getFontMetrics();
            g2.drawString(letters, (getWidth() - metrics.stringWidth(letters)) / 2,
                    (getHeight() - metrics.getHeight()) / 2 + metrics.getAscent());
            g2.dispose();
        }
    }

    /**
     * A colored dashboard card with a title and a big number. When the number
     * changes, it counts up or down to the new value.
     */
    static class StatCard extends JPanel {

        private static final long serialVersionUID = 1L;

        private final Color color;
        private final String prefix;
        private final JLabel titleLabel = new JLabel();
        private final JLabel valueLabel = new JLabel();
        private int target = 0;
        private int shown = 0;
        private Timer timer;

        StatCard(String title, Color color, String prefix) {
            super(new BorderLayout(0, 2));
            this.color = color;
            this.prefix = prefix;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

            titleLabel.setFont(BOLD_FONT.deriveFont(12f));
            titleLabel.setForeground(new Color(255, 255, 255, 215));
            valueLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
            valueLabel.setForeground(Color.WHITE);
            setTitle(title);
            valueLabel.setText(prefix + 0);
            add(titleLabel, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
        }

        void setTitle(String title) {
            titleLabel.setText(title.toUpperCase());
        }

        /**
         * Counts up or down to a new number.
         *
         * @param newValue the number to show
         */
        void setValue(int newValue) {
            target = newValue;
            if (timer != null) {
                timer.stop();
            }
            if (shown == target) {
                valueLabel.setText(prefix + shown);
                return;
            }
            timer = new Timer(25, e -> {
                int step = Math.max(1, Math.abs(target - shown) / 5);
                shown = (target > shown) ? Math.min(target, shown + step) : Math.max(target, shown - step);
                valueLabel.setText(prefix + shown);
                if (shown == target) {
                    timer.stop();
                }
            });
            timer.start();
        }

        int getValue() {
            return target;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
            g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 16, 16));
            g2.setColor(new Color(255, 255, 255, 40));
            g2.fillOval(getWidth() - 70, -30, 120, 120);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * A button with rounded corners and a solid color that gets darker when
     * the mouse is over it.
     */
    static class ModernButton extends JButton {

        private static final long serialVersionUID = 1L;

        private Color baseColor;
        private boolean hovering = false;

        ModernButton(String text, Color baseColor, Color textColor) {
            super(text);
            this.baseColor = baseColor;
            setForeground(textColor);
            setFont(BOLD_FONT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(96, 36));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovering = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    repaint();
                }
            });
        }

        void setBaseColor(Color color) {
            this.baseColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = baseColor;
            if (!isEnabled()) {
                fill = new Color(0xCBD5E1);
            } else if (getModel().isPressed()) {
                fill = baseColor.darker().darker();
            } else if (hovering) {
                fill = baseColor.darker();
            }
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * A toggle button with rounded corners. The selected button is filled with
     * the accent color.
     */
    static class SegmentButton extends JToggleButton {

        private static final long serialVersionUID = 1L;

        private Color accent;

        SegmentButton(String text, Color accent) {
            super(text);
            this.accent = accent;
            setFont(BOLD_FONT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(120, 40));
        }

        void setAccent(Color color) {
            this.accent = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isSelected() ? accent : LIGHT_BUTTON);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose();
            setForeground(isSelected() ? Color.WHITE : TEXT_DARK);
            super.paintComponent(g);
        }
    }

    /**
     * A navigation tab for the dark title bar. The selected tab has a gold line below it.
     */
    static class NavButton extends JToggleButton {

        private static final long serialVersionUID = 1L;

        private boolean hovering = false;

        NavButton(String text) {
            super(text);
            setFont(BOLD_FONT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 18, 10, 18));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovering = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovering = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            if (isSelected()) {
                g2.setColor(GOLD);
                g2.fillRect(8, getHeight() - 4, getWidth() - 16, 3);
            } else if (hovering) {
                g2.setColor(new Color(255, 255, 255, 90));
                g2.fillRect(8, getHeight() - 4, getWidth() - 16, 3);
            }
            g2.dispose();
            setForeground(isSelected() ? Color.WHITE : new Color(0xBFD0F0));
            super.paintComponent(g);
        }
    }

    /**
     * A button that looks like a text link.
     */
    static class LinkButton extends JButton {

        private static final long serialVersionUID = 1L;

        LinkButton(String text) {
            super(text);
            setFont(BOLD_FONT);
            setForeground(BLUE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(NAVY_LIGHT);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(BLUE);
                }
            });
        }
    }

    // ---------- Table cell drawing ----------

    /**
     * Draws a table cell with padding and alternating row colors. An empty value is shown as a dash.
     */
    static class CellRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
            if (value == null || value.toString().isEmpty()) {
                setText("-");
            }
            setHorizontalAlignment(LEFT);
            setFont(BASE_FONT.deriveFont(13f));
            setForeground(TEXT_DARK);
            setBackground(rowColor(table, isSelected, row));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 6));
            return this;
        }
    }

    /**
     * Draws the status as a small colored badge: green for Available, red for
     * Issued and amber for Reserved.
     */
    static class StatusRenderer extends CellRenderer {

        private static final long serialVersionUID = 1L;

        private Color rowBackground = Color.WHITE;
        private Color badgeBackground = Color.LIGHT_GRAY;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            rowBackground = rowColor(table, isSelected, row);
            if (value == BookStatus.AVAILABLE) {
                badgeBackground = new Color(0xDCFCE7);
                setForeground(new Color(0x166534));
            } else if (value == BookStatus.ISSUED) {
                badgeBackground = new Color(0xFEE2E2);
                setForeground(new Color(0x991B1B));
            } else {
                badgeBackground = new Color(0xFEF3C7);
                setForeground(new Color(0x92400E));
            }
            setFont(BOLD_FONT.deriveFont(12f));
            setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 12));
            return this;
        }

        @Override
        public boolean isOpaque() {
            return false; // the cell paints its own background
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(rowBackground);
            g2.fillRect(0, 0, getWidth(), getHeight());
            FontMetrics metrics = g2.getFontMetrics(getFont());
            int badgeWidth = metrics.stringWidth(getText()) + 24;
            int badgeHeight = 24;
            g2.setColor(badgeBackground);
            g2.fillRoundRect(8, (getHeight() - badgeHeight) / 2, badgeWidth, badgeHeight,
                    badgeHeight, badgeHeight);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Draws the due date. The date is shown in red when the book is issued and
     * the due date has already passed.
     */
    static class DueDateRenderer extends CellRenderer {

        private static final long serialVersionUID = 1L;

        private final int statusModelColumn;

        DueDateRenderer(int statusModelColumn) {
            this.statusModelColumn = statusModelColumn;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof LocalDate) {
                LocalDate due = (LocalDate) value;
                setText(due.format(DATE_FORMAT));
                Object status = table.getModel().getValueAt(
                        table.convertRowIndexToModel(row), statusModelColumn);
                if (status == BookStatus.ISSUED && due.isBefore(LocalDate.now())) {
                    setForeground(ERROR_COLOR);
                    setFont(BOLD_FONT.deriveFont(13f));
                }
            }
            return this;
        }
    }

    /**
     * Draws a fine in rupees, in red when it is more than zero.
     */
    static class FineRenderer extends CellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Integer && ((Integer) value) > 0) {
                setText("Rs " + value);
                setForeground(ERROR_COLOR);
                setFont(BOLD_FONT.deriveFont(13f));
            } else {
                setText("-");
            }
            return this;
        }
    }

    /**
     * Draws the column titles of a table.
     */
    static class HeaderRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, false, false, row, column);
            setFont(BOLD_FONT.deriveFont(12f));
            setForeground(TEXT_MUTED);
            setBackground(TABLE_HEADER_COLOR);
            setOpaque(true);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, CARD_BORDER),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            return this;
        }
    }
}
