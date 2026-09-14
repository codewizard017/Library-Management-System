import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class CirculationPanel extends JPanel {

    private final Connection connection;
    private final Member currentUser;
    private final Runnable onDataChanged;

    private JTabbedPane tabbedPane;

    // Issue Tab Components
    private JTextField issueBookIdField;
    private JLabel issueBookPreviewLabel;
    private JTextField issueMemberIdField;
    private JLabel issueMemberPreviewLabel;
    private JLabel issueStatusBanner;
    private JTable availableBooksTable;
    private DefaultTableModel availableBooksModel;

    // Return Tab Components
    private JTextField returnBookIdField;
    private JLabel returnDetailsLabel;
    private JLabel returnStatusBanner;
    private JTable activeLoansTable;
    private DefaultTableModel activeLoansModel;

    public CirculationPanel(Connection conn, Member user, Runnable onDataChanged) {
        this.connection = conn;
        this.currentUser = user;
        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initUI();
        refreshCirculationData();
    }

    private void initUI() {
        // Header
        JPanel header = UIUtils.createHeader("Circulation Desk", "Issue books to members and process book returns with automated fine calculation");

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabArc: 10; tabInsets: 10,20,10,20;");

        tabbedPane.addTab("Issue Book", createIssuePanel());
        tabbedPane.addTab("Return Book", createReturnPanel());

        add(header, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void selectIssueTab() {
        tabbedPane.setSelectedIndex(0);
    }

    public void selectReturnTab() {
        tabbedPane.setSelectedIndex(1);
    }

    private JPanel createIssuePanel() {
        JPanel root = new JPanel(new GridLayout(1, 2, 24, 0));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Left Form
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.putClientProperty(FlatClientProperties.STYLE, "arc: 16; background: $Panel.background;");
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1, true),
                new EmptyBorder(24, 24, 24, 24)
        ));

        JLabel title = new JLabel("Issue Book to Member");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Book ID
        JLabel bookLbl = new JLabel("Book ID");
        bookLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookLbl.setForeground(ThemeManager.getTextMuted());
        bookLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        issueBookIdField = new JTextField();
        issueBookIdField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Book ID (e.g. 1)");
        issueBookIdField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,12,8,12;");
        issueBookIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        issueBookIdField.setAlignmentX(Component.LEFT_ALIGNMENT);

        issueBookPreviewLabel = new JLabel("Enter Book ID to view title & status");
        issueBookPreviewLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        issueBookPreviewLabel.setForeground(ThemeManager.getTextMuted());
        issueBookPreviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Member ID
        JLabel memberLbl = new JLabel("Member ID");
        memberLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        memberLbl.setForeground(ThemeManager.getTextMuted());
        memberLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        issueMemberIdField = new JTextField();
        issueMemberIdField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Member ID (e.g. 1)");
        issueMemberIdField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,12,8,12;");
        issueMemberIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        issueMemberIdField.setAlignmentX(Component.LEFT_ALIGNMENT);

        issueMemberPreviewLabel = new JLabel("Enter Member ID to view name & borrowing limit");
        issueMemberPreviewLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        issueMemberPreviewLabel.setForeground(ThemeManager.getTextMuted());
        issueMemberPreviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Issue Date
        JLabel dateLbl = new JLabel("Issue Date: Today (" + LocalDate.now() + ")");
        dateLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLbl.setForeground(ThemeManager.getTextMuted());
        dateLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        issueStatusBanner = new JLabel(" ");
        issueStatusBanner.setFont(new Font("Segoe UI", Font.BOLD, 12));
        issueStatusBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton issueBtn = UIUtils.createPrimaryButton("Issue Book Now", e -> performIssueBook());
        issueBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        issueBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        issueBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(title);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(bookLbl);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(issueBookIdField);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(issueBookPreviewLabel);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(memberLbl);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(issueMemberIdField);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(issueMemberPreviewLabel);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(dateLbl);
        formCard.add(Box.createVerticalStrut(10));
        formCard.add(issueStatusBanner);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(issueBtn);
        formCard.add(Box.createVerticalGlue());

        // Right side: Table of Available Books
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);

        JLabel rightTitle = new JLabel("Available Books (Click to select ID)");
        rightTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        rightPanel.add(rightTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Author", "Category"};
        availableBooksModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        availableBooksTable = new JTable(availableBooksModel);
        UIUtils.formatTable(availableBooksTable);
        availableBooksTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        availableBooksTable.getColumnModel().getColumn(1).setPreferredWidth(180);

        availableBooksTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && availableBooksTable.getSelectedRow() >= 0) {
                int row = availableBooksTable.getSelectedRow();
                int id = (int) availableBooksModel.getValueAt(row, 0);
                String bTitle = (String) availableBooksModel.getValueAt(row, 1);
                issueBookIdField.setText(String.valueOf(id));
                issueBookPreviewLabel.setForeground(ThemeManager.SUCCESS_COLOR);
                issueBookPreviewLabel.setText("Selected: " + bTitle + " (Available)");
            }
        });

        JScrollPane scroll = new JScrollPane(availableBooksTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        rightPanel.add(scroll, BorderLayout.CENTER);

        root.add(formCard);
        root.add(rightPanel);
        return root;
    }

    private JPanel createReturnPanel() {
        JPanel root = new JPanel(new GridLayout(1, 2, 24, 0));
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(16, 0, 0, 0));

        // Left Form
        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.putClientProperty(FlatClientProperties.STYLE, "arc: 16; background: $Panel.background;");
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1, true),
                new EmptyBorder(24, 24, 24, 24)
        ));

        JLabel title = new JLabel("Process Book Return");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bookLbl = new JLabel("Book ID to Return");
        bookLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookLbl.setForeground(ThemeManager.getTextMuted());
        bookLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        returnBookIdField = new JTextField();
        returnBookIdField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter Book ID to return");
        returnBookIdField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,12,8,12;");
        returnBookIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        returnBookIdField.setAlignmentX(Component.LEFT_ALIGNMENT);

        returnDetailsLabel = new JLabel("Select an issued book from the list or enter ID");
        returnDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        returnDetailsLabel.setForeground(ThemeManager.getTextMuted());
        returnDetailsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        returnStatusBanner = new JLabel(" ");
        returnStatusBanner.setFont(new Font("Segoe UI", Font.BOLD, 12));
        returnStatusBanner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton returnBtn = UIUtils.createSuccessButton("Process Return", e -> performReturnBook());
        returnBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        returnBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        returnBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(title);
        formCard.add(Box.createVerticalStrut(16));
        formCard.add(bookLbl);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(returnBookIdField);
        formCard.add(Box.createVerticalStrut(8));
        formCard.add(returnDetailsLabel);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(returnStatusBanner);
        formCard.add(Box.createVerticalStrut(14));
        formCard.add(returnBtn);
        formCard.add(Box.createVerticalGlue());

        // Right side: Active Loans Table
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);

        JLabel rightTitle = new JLabel("Active Issued Loans (Click to select for return)");
        rightTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        rightPanel.add(rightTitle, BorderLayout.NORTH);

        String[] cols = {"Tx ID", "Book ID", "Title", "Member", "Issue Date", "Fine"};
        activeLoansModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        activeLoansTable = new JTable(activeLoansModel);
        UIUtils.formatTable(activeLoansTable);
        activeLoansTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        activeLoansTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        activeLoansTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        activeLoansTable.getColumnModel().getColumn(3).setPreferredWidth(120);

        activeLoansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && activeLoansTable.getSelectedRow() >= 0) {
                int row = activeLoansTable.getSelectedRow();
                int bookId = (int) activeLoansModel.getValueAt(row, 1);
                String bTitle = (String) activeLoansModel.getValueAt(row, 2);
                String member = (String) activeLoansModel.getValueAt(row, 3);
                String fine = (String) activeLoansModel.getValueAt(row, 5);

                returnBookIdField.setText(String.valueOf(bookId));
                returnDetailsLabel.setText("<html>Selected: <b>" + bTitle + "</b><br>Borrowed by: " + member + "<br>Calculated Fine: <b style='color:red'>" + fine + "</b></html>");
            }
        });

        JScrollPane scroll = new JScrollPane(activeLoansTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        rightPanel.add(scroll, BorderLayout.CENTER);

        root.add(formCard);
        root.add(rightPanel);
        return root;
    }

    public void refreshCirculationData() {
        // Load available books
        SwingWorker<ArrayList<Book>, Void> availWorker = new SwingWorker<>() {
            @Override
            protected ArrayList<Book> doInBackground() {
                ArrayList<Book> all = BookDao.getAllBooks(connection);
                ArrayList<Book> available = new ArrayList<>();
                for (Book b : all) {
                    if (b.getStatus() == BookStatus.AVAILABLE) {
                        available.add(b);
                    }
                }
                return available;
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Book> list = get();
                    availableBooksModel.setRowCount(0);
                    for (Book b : list) {
                        availableBooksModel.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory()});
                    }
                } catch (Exception ignored) {}
            }
        };
        availWorker.execute();

        // Load active loans
        SwingWorker<ArrayList<TransactionRecord>, Void> loansWorker = new SwingWorker<>() {
            @Override
            protected ArrayList<TransactionRecord> doInBackground() {
                ArrayList<TransactionRecord> all = TransactionDao.getAllTransactionRecords(connection);
                ArrayList<TransactionRecord> active = new ArrayList<>();
                for (TransactionRecord r : all) {
                    if (!r.isReturned()) {
                        active.add(r);
                    }
                }
                return active;
            }

            @Override
            protected void done() {
                try {
                    ArrayList<TransactionRecord> list = get();
                    activeLoansModel.setRowCount(0);
                    for (TransactionRecord r : list) {
                        activeLoansModel.addRow(new Object[]{
                                r.getTransactionId(),
                                r.getBookId(),
                                r.getBookTitle(),
                                r.getMemberName(),
                                r.getIssueDate(),
                                "₹" + r.getFine()
                        });
                    }
                } catch (Exception ignored) {}
            }
        };
        loansWorker.execute();
    }

    private void performIssueBook() {
        String bookStr = issueBookIdField.getText().trim();
        String memberStr = issueMemberIdField.getText().trim();

        if (bookStr.isEmpty() || memberStr.isEmpty()) {
            issueStatusBanner.setForeground(ThemeManager.DANGER_COLOR);
            issueStatusBanner.setText("Please enter both Book ID and Member ID");
            return;
        }

        int bookId, memberId;
        try {
            bookId = Integer.parseInt(bookStr);
            memberId = Integer.parseInt(memberStr);
        } catch (NumberFormatException e) {
            issueStatusBanner.setForeground(ThemeManager.DANGER_COLOR);
            issueStatusBanner.setText("Book ID and Member ID must be valid numbers");
            return;
        }

        try (Statement st = connection.createStatement()) {
            String msg = TransactionDao.issueBook(bookId, memberId, LocalDate.now(), connection, st, currentUser);
            if (msg.toLowerCase().contains("successfully")) {
                issueStatusBanner.setForeground(ThemeManager.SUCCESS_COLOR);
                issueStatusBanner.setText(msg);
                issueBookIdField.setText("");
                issueMemberIdField.setText("");
                issueBookPreviewLabel.setText("Enter Book ID to view title & status");
                issueMemberPreviewLabel.setText("Enter Member ID to view name & borrowing limit");
                refreshCirculationData();
                if (onDataChanged != null) onDataChanged.run();
            } else {
                issueStatusBanner.setForeground(ThemeManager.DANGER_COLOR);
                issueStatusBanner.setText(msg);
            }
        } catch (Exception e) {
            issueStatusBanner.setForeground(ThemeManager.DANGER_COLOR);
            issueStatusBanner.setText("Error: " + e.getMessage());
        }
    }

    private void performReturnBook() {
        String bookStr = returnBookIdField.getText().trim();
        if (bookStr.isEmpty()) {
            returnStatusBanner.setForeground(ThemeManager.DANGER_COLOR);
            returnStatusBanner.setText("Please enter Book ID to return");
            return;
        }

        int bookId;
        try {
            bookId = Integer.parseInt(bookStr);
        } catch (NumberFormatException e) {
            returnStatusBanner.setForeground(ThemeManager.DANGER_COLOR);
            returnStatusBanner.setText("Book ID must be a valid number");
            return;
        }

        try (Statement st = connection.createStatement()) {
            String msg = TransactionDao.returnBook(bookId, LocalDate.now(), connection, st, currentUser);
            if (msg.toLowerCase().contains("successfully")) {
                returnStatusBanner.setForeground(ThemeManager.SUCCESS_COLOR);
                returnStatusBanner.setText(msg);
                returnBookIdField.setText("");
                returnDetailsLabel.setText("Select an issued book from the list or enter ID");
                refreshCirculationData();
                if (onDataChanged != null) onDataChanged.run();
            } else {
                returnStatusBanner.setForeground(ThemeManager.DANGER_COLOR);
                returnStatusBanner.setText(msg);
            }
        } catch (Exception e) {
            returnStatusBanner.setForeground(ThemeManager.DANGER_COLOR);
            returnStatusBanner.setText("Error: " + e.getMessage());
        }
    }
}

