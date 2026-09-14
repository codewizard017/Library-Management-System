import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;

public class StudentPortalPanel extends JPanel {

    private final Connection connection;
    private final Member currentUser;

    private JTable borrowedTable;
    private DefaultTableModel borrowedModel;

    private JTable historyTable;
    private DefaultTableModel historyModel;

    private JPanel statsContainer;

    public StudentPortalPanel(Connection conn, Member user) {
        this.connection = conn;
        this.currentUser = user;

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initUI();
        loadStudentData();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel headerText = UIUtils.createHeader("Student Dashboard", "Manage your active borrowings, track due dates, and review borrowing history");
        header.add(headerText, BorderLayout.WEST);

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh My Data", e -> loadStudentData());
        header.add(refreshBtn, BorderLayout.EAST);

        // Stats Container
        statsContainer = new JPanel(new GridLayout(1, 3, 16, 0));
        statsContainer.setOpaque(false);

        // Tabbed View: Active Loans vs Full History
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.putClientProperty(FlatClientProperties.STYLE, "tabArc: 10; tabInsets: 8,16,8,16;");

        // Active Loans Tab
        JPanel activePanel = new JPanel(new BorderLayout(0, 10));
        activePanel.setOpaque(false);
        activePanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        String[] borrowedCols = {"Book ID", "Title", "Author", "ISBN", "Category", "Shelf"};
        borrowedModel = new DefaultTableModel(borrowedCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        borrowedTable = new JTable(borrowedModel);
        UIUtils.formatTable(borrowedTable);
        JScrollPane borrowedScroll = new JScrollPane(borrowedTable);
        borrowedScroll.setBorder(BorderFactory.createEmptyBorder());
        borrowedScroll.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        activePanel.add(borrowedScroll, BorderLayout.CENTER);

        // History Tab
        JPanel historyPanel = new JPanel(new BorderLayout(0, 10));
        historyPanel.setOpaque(false);
        historyPanel.setBorder(new EmptyBorder(12, 0, 0, 0));

        String[] histCols = {"Tx ID", "Book ID", "Title", "Issue Date", "Return Date", "Days", "Fine", "Status"};
        historyModel = new DefaultTableModel(histCols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(historyModel);
        UIUtils.formatTable(historyTable);
        historyTable.getColumnModel().getColumn(7).setCellRenderer(UIUtils.createBadgeRenderer());
        JScrollPane histScroll = new JScrollPane(historyTable);
        histScroll.setBorder(BorderFactory.createEmptyBorder());
        histScroll.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        historyPanel.add(histScroll, BorderLayout.CENTER);

        tabbedPane.addTab("My Current Borrowed Books", activePanel);
        tabbedPane.addTab("My Borrowing History & Fines", historyPanel);

        // Top Assembly
        JPanel topBox = new JPanel();
        topBox.setLayout(new BoxLayout(topBox, BoxLayout.Y_AXIS));
        topBox.setOpaque(false);
        topBox.add(header);
        topBox.add(Box.createVerticalStrut(14));
        topBox.add(statsContainer);
        topBox.add(Box.createVerticalStrut(14));

        add(topBox, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    public void loadStudentData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private ArrayList<Book> activeBooks;
            private ArrayList<TransactionRecord> history;
            private long totalFines = 0;

            @Override
            protected Void doInBackground() {
                activeBooks = TransactionDao.getMemberBorrowedBooks(currentUser.getMemberId(), connection);
                history = TransactionDao.getMemberBorrowHistory(currentUser.getMemberId(), connection);
                for (TransactionRecord r : history) {
                    totalFines += r.getFine();
                }
                return null;
            }

            @Override
            protected void done() {
                // Populate active loans
                borrowedModel.setRowCount(0);
                for (Book b : activeBooks) {
                    borrowedModel.addRow(new Object[]{
                            b.getBookId(),
                            b.getTitle(),
                            b.getAuthor(),
                            b.getIsbn(),
                            b.getCategory(),
                            b.getShelfLocation()
                    });
                }

                // Populate history
                historyModel.setRowCount(0);
                for (TransactionRecord r : history) {
                    historyModel.addRow(new Object[]{
                            r.getTransactionId(),
                            r.getBookId(),
                            r.getBookTitle(),
                            r.getIssueDate(),
                            r.getReturnDate() == null ? "Currently Issued" : r.getReturnDate(),
                            r.getDaysBorrowed() + " days",
                            "₹" + r.getFine(),
                            r.isReturned() ? "AVAILABLE" : "ISSUED"
                    });
                }

                // Update Stat Cards
                statsContainer.removeAll();
                statsContainer.add(UIUtils.createStatCard("Books in Possession", String.valueOf(activeBooks.size()) + " / 3", "Max allowed limit: 3", ThemeManager.INFO_COLOR, "📖"));
                statsContainer.add(UIUtils.createStatCard("Total Loans", String.valueOf(history.size()), "All-time borrowed", ThemeManager.ACCENT_COLOR, "📋"));
                statsContainer.add(UIUtils.createStatCard("Total Outstanding Fine", "₹" + totalFines, totalFines > 0 ? "₹5/day beyond 14 days" : "No overdue penalties", totalFines > 0 ? ThemeManager.DANGER_COLOR : ThemeManager.SUCCESS_COLOR, "💳"));
                statsContainer.revalidate();
                statsContainer.repaint();
            }
        };
        worker.execute();
    }
}

