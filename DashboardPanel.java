import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DashboardPanel extends JPanel {

    private final Connection connection;
    private final Member currentUser;
    private final Runnable navigateToIssue;
    private final Runnable navigateToReturn;
    private final Runnable navigateToBooks;
    private final Runnable navigateToMembers;

    private JPanel statsGrid;
    private JTable recentTable;
    private DefaultTableModel tableModel;

    public DashboardPanel(Connection conn, Member user,
                          Runnable toIssue, Runnable toReturn, Runnable toBooks, Runnable toMembers) {
        this.connection = conn;
        this.currentUser = user;
        this.navigateToIssue = toIssue;
        this.navigateToReturn = toReturn;
        this.navigateToBooks = toBooks;
        this.navigateToMembers = toMembers;

        setLayout(new BorderLayout(0, 20));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initUI();
        refreshData();
    }

    private void initUI() {
        // Top Welcome Banner & Actions
        JPanel topBanner = new JPanel(new BorderLayout());
        topBanner.setOpaque(false);

        JPanel welcomeInfo = new JPanel();
        welcomeInfo.setLayout(new BoxLayout(welcomeInfo, BoxLayout.Y_AXIS));
        welcomeInfo.setOpaque(false);

        JLabel welcomeTitle = new JLabel("Welcome back, " + currentUser.getName() + "!");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"));
        JLabel dateLabel = new JLabel("System Overview &bull; " + dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(ThemeManager.getTextMuted());

        welcomeInfo.add(welcomeTitle);
        welcomeInfo.add(Box.createVerticalStrut(4));
        welcomeInfo.add(dateLabel);

        // Refresh Button
        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh Dashboard", e -> refreshData());
        topBanner.add(welcomeInfo, BorderLayout.WEST);
        topBanner.add(refreshBtn, BorderLayout.EAST);

        // Stats Grid
        statsGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        statsGrid.setOpaque(false);

        // Quick Actions Row
        JPanel quickActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        quickActions.setOpaque(false);
        quickActions.setBorder(new EmptyBorder(8, 0, 8, 0));

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.LIBRARIAN) {
            quickActions.add(UIUtils.createPrimaryButton("+ Issue Book", e -> {
                if (navigateToIssue != null) navigateToIssue.run();
            }));
            quickActions.add(UIUtils.createSuccessButton("Return Book", e -> {
                if (navigateToReturn != null) navigateToReturn.run();
            }));
        }
        quickActions.add(UIUtils.createSecondaryButton("Browse Books", e -> {
            if (navigateToBooks != null) navigateToBooks.run();
        }));
        if (currentUser.getRole() == Role.ADMIN) {
            quickActions.add(UIUtils.createSecondaryButton("Manage Members", e -> {
                if (navigateToMembers != null) navigateToMembers.run();
            }));
        }

        // Recent Activity Section
        JPanel recentSection = new JPanel(new BorderLayout(0, 12));
        recentSection.setOpaque(false);

        JLabel sectionTitle = new JLabel("Recent Loan Activities");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        recentSection.add(sectionTitle, BorderLayout.NORTH);

        String[] cols = {"Tx ID", "Book Title", "Member", "Issue Date", "Return Date", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentTable = new JTable(tableModel);
        UIUtils.formatTable(recentTable);
        recentTable.getColumnModel().getColumn(5).setCellRenderer(UIUtils.createBadgeRenderer());
        recentTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        recentTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        recentTable.getColumnModel().getColumn(2).setPreferredWidth(160);

        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");
        recentSection.add(scrollPane, BorderLayout.CENTER);

        // Assemble Panels
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(topBanner);
        topContainer.add(Box.createVerticalStrut(18));
        topContainer.add(statsGrid);
        topContainer.add(Box.createVerticalStrut(18));
        topContainer.add(quickActions);
        topContainer.add(Box.createVerticalStrut(12));

        add(topContainer, BorderLayout.NORTH);
        add(recentSection, BorderLayout.CENTER);
    }

    public void refreshData() {
        // Load stats in background
        SwingWorker<int[], Void> worker = new SwingWorker<>() {
            @Override
            protected int[] doInBackground() {
                int total = 0, available = 0, issued = 0, members = 0;
                try (java.sql.Statement st = connection.createStatement()) {
                    total = BookDao.countBooks(connection, st);
                    available = BookDao.countAvailableBooks(connection, st);
                    issued = BookDao.countIssuedBooks(connection, st);
                } catch (Exception ignored) {}
                members = MemberDao.countMembers(connection);
                return new int[]{total, available, issued, members};
            }

            @Override
            protected void done() {
                try {
                    int[] counts = get();
                    statsGrid.removeAll();
                    statsGrid.add(UIUtils.createStatCard("Total Books", String.valueOf(counts[0]), "In library database", ThemeManager.ACCENT_COLOR, "📚"));
                    statsGrid.add(UIUtils.createStatCard("Available", String.valueOf(counts[1]), "Ready to borrow", ThemeManager.SUCCESS_COLOR, "✅"));
                    statsGrid.add(UIUtils.createStatCard("Active Loans", String.valueOf(counts[2]), "Currently with members", ThemeManager.DANGER_COLOR, "⏳"));
                    statsGrid.add(UIUtils.createStatCard("Members", String.valueOf(counts[3]), "Registered accounts", ThemeManager.INFO_COLOR, "👥"));
                    statsGrid.revalidate();
                    statsGrid.repaint();
                } catch (Exception ignored) {}
            }
        };
        worker.execute();

        // Load recent transactions
        SwingWorker<ArrayList<TransactionRecord>, Void> txWorker = new SwingWorker<>() {
            @Override
            protected ArrayList<TransactionRecord> doInBackground() {
                return TransactionDao.getAllTransactionRecords(connection);
            }

            @Override
            protected void done() {
                try {
                    ArrayList<TransactionRecord> list = get();
                    tableModel.setRowCount(0);
                    int count = 0;
                    for (TransactionRecord r : list) {
                        if (count++ >= 8) break; // Limit to 8 recent
                        tableModel.addRow(new Object[]{
                                r.getTransactionId(),
                                r.getBookTitle(),
                                r.getMemberName(),
                                r.getIssueDate(),
                                r.getReturnDate() == null ? "Not Returned" : r.getReturnDate(),
                                r.isReturned() ? "AVAILABLE" : "ISSUED"
                        });
                    }
                } catch (Exception ignored) {}
            }
        };
        txWorker.execute();
    }
}

