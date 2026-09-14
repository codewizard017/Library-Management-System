import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class UIUtils {

    public static JPanel createHeader(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 0, 15, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(ThemeManager.getTextMuted());
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subLabel);

        return panel;
    }

    public static JPanel createStatCard(String title, String value, String subtitle, Color accentColor, String iconText) {
        JPanel card = new JPanel(new BorderLayout(15, 10));
        card.putClientProperty(FlatClientProperties.STYLE, "arc: 16; background: $Panel.background;");
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60), 1, true),
                new EmptyBorder(16, 20, 16, 20)
        ));

        // Left Icon circle
        JPanel iconPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 35));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        iconPanel.setPreferredSize(new Dimension(52, 52));
        iconPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(iconText, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        iconLabel.setForeground(accentColor);
        iconPanel.add(iconLabel);

        // Center / Text content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title.toUpperCase());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        titleLabel.setForeground(ThemeManager.getTextMuted());

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));

        JLabel subLabel = new JLabel(subtitle);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subLabel.setForeground(ThemeManager.getTextMuted());

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(2));
        contentPanel.add(subLabel);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    public static JButton createPrimaryButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(ThemeManager.ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 7,16,7,16;");
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    public static JButton createSecondaryButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 7,14,7,14;");
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    public static JButton createSuccessButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(ThemeManager.SUCCESS_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 7,16,7,16;");
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    public static JButton createDangerButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(ThemeManager.DANGER_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 7,16,7,16;");
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    public static JTextField createSearchField(String placeholder, Consumer<String> onSearch) {
        JTextField field = new JTextField();
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 6,10,6,10;");
        field.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.getDocument().addDocumentListener(new DocumentListener() {
            private void update() {
                if (onSearch != null) {
                    onSearch.accept(field.getText().trim());
                }
            }
            public void insertUpdate(DocumentEvent e) { update(); }
            public void removeUpdate(DocumentEvent e) { update(); }
            public void changedUpdate(DocumentEvent e) { update(); }
        });
        return field;
    }

    public static void formatTable(JTable table) {
        table.setRowHeight(38);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 38));
    }

    public static TableCellRenderer createBadgeRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(new EmptyBorder(4, 10, 4, 10));

                if (value == null) return label;
                String text = value.toString();

                Color bg = null;
                Color fg = null;

                if ("AVAILABLE".equalsIgnoreCase(text) || "Y".equalsIgnoreCase(text) || "RETURNED".equalsIgnoreCase(text)) {
                    bg = new Color(16, 185, 129, 35);
                    fg = ThemeManager.SUCCESS_COLOR;
                    if ("Y".equalsIgnoreCase(text)) text = "RETURNED";
                } else if ("ISSUED".equalsIgnoreCase(text) || "N".equalsIgnoreCase(text)) {
                    bg = new Color(239, 68, 68, 35);
                    fg = ThemeManager.DANGER_COLOR;
                    if ("N".equalsIgnoreCase(text)) text = "ISSUED";
                } else if ("ADMIN".equalsIgnoreCase(text)) {
                    bg = new Color(99, 102, 241, 35);
                    fg = ThemeManager.ACCENT_COLOR;
                } else if ("LIBRARIAN".equalsIgnoreCase(text)) {
                    bg = new Color(59, 130, 246, 35);
                    fg = ThemeManager.INFO_COLOR;
                } else if ("STUDENT".equalsIgnoreCase(text)) {
                    bg = new Color(6, 182, 212, 35);
                    fg = new Color(6, 182, 212);
                }

                if (bg != null && !isSelected) {
                    final Color badgeBg = bg;
                    final Color badgeFg = fg;
                    final String displayStr = text;
                    JPanel badge = new JPanel(new GridBagLayout()) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(badgeBg);
                            g2.fillRoundRect(8, 6, getWidth() - 16, getHeight() - 12, 14, 14);
                            g2.dispose();
                            super.paintComponent(g);
                        }
                    };
                    badge.setOpaque(false);
                    JLabel textLbl = new JLabel(displayStr);
                    textLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    textLbl.setForeground(badgeFg);
                    badge.add(textLbl);
                    return badge;
                }

                return label;
            }
        };
    }

    public static void showToast(Component parent, String message, boolean isError) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                isError ? "Error" : "Success",
                isError ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE
        );
    }
}

