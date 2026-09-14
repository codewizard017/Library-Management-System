import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;

public class TransactionsPanel extends JPanel {

    private final Connection connection;
    private final Member currentUser;

    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JLabel countLabel;
    private JLabel summaryLabel;

    public TransactionsPanel(Connection conn, Member user) {
        this.connection = conn;
        this.currentUser = user;

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initUI();
        loadTransactions();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel headerText = UIUtils.createHeader("Transaction History", "Audit all book issues, returns, and accrued fines");
        header.add(headerText, BorderLayout.WEST);

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh", e -> loadTransactions());
        header.add(refreshBtn, BorderLayout.EAST);

        // Filter & Search Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        searchField = UIUtils.createSearchField("Search by Title, Member, ID...", text -> applyFilters());
        searchField.setPreferredSize(new Dimension(340, 38));

        JPanel filterControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterControls.setOpaque(false);

        statusFilter = new JComboBox<>(new String[]{"All Statuses", "ISSUED", "RETURNED"});
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusFilter.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 4,8,4,8;");
        statusFilter.addActionListener(e -> applyFilters());

        countLabel = new JLabel("Loading transactions...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(ThemeManager.getTextMuted());

        summaryLabel = new JLabel("");
        summaryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        filterControls.add(summaryLabel);
        filterControls.add(new JLabel("Status:"));
        filterControls.add(statusFilter);
        filterControls.add(countLabel);

        toolbar.add(searchField, BorderLayout.WEST);
        toolbar.add(filterControls, BorderLayout.EAST);

        // Table
        String[] columns = {"Tx ID", "Book ID", "Book Title", "Member ID", "Member Name", "Issue Date", "Return Date", "Days", "Fine", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 1 || columnIndex == 3) return Integer.class;
                return String.class;
            }
        };

        transactionsTable = new JTable(tableModel);
        UIUtils.formatTable(transactionsTable);
        transactionsTable.getColumnModel().getColumn(9).setCellRenderer(UIUtils.createBadgeRenderer());
        transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(50);
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(140);
        transactionsTable.getColumnModel().getColumn(5).setPreferredWidth(90);
        transactionsTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        transactionsTable.getColumnModel().getColumn(7).setPreferredWidth(70);
        transactionsTable.getColumnModel().getColumn(8).setPreferredWidth(70);
        transactionsTable.getColumnModel().getColumn(9).setPreferredWidth(95);

        rowSorter = new TableRowSorter<>(tableModel);
        transactionsTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");

        // Layout Assembly
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(header, BorderLayout.NORTH);
        topContainer.add(toolbar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadTransactions() {
        SwingWorker<ArrayList<TransactionRecord>, Void> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<TransactionRecord> doInBackground() {
                return TransactionDao.getAllTransactionRecords(connection);
            }

            @Override
            protected void done() {
                try {
                    ArrayList<TransactionRecord> list = get();
                    tableModel.setRowCount(0);
                    long totalFines = 0;
                    int activeCount = 0;

                    for (TransactionRecord r : list) {
                        if (!r.isReturned()) activeCount++;
                        totalFines += r.getFine();

                        tableModel.addRow(new Object[]{
                                r.getTransactionId(),
                                r.getBookId(),
                                r.getBookTitle(),
                                r.getMemberId(),
                                r.getMemberName(),
                                r.getIssueDate(),
                                r.getReturnDate() == null ? "Not Returned" : r.getReturnDate(),
                                r.getDaysBorrowed() + " days",
                                "₹" + r.getFine(),
                                r.isReturned() ? "AVAILABLE" : "ISSUED"
                        });
                    }

                    countLabel.setText("Total " + list.size() + " records");
                    summaryLabel.setText("Active Loans: " + activeCount + " | Total Fines: ₹" + totalFines + "  ");
                } catch (Exception e) {
                    countLabel.setText("Error: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim();
        String selectedStatus = (String) statusFilter.getSelectedItem();

        ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText)));
        }
        if (selectedStatus != null && !"All Statuses".equals(selectedStatus)) {
            String badgeVal = "RETURNED".equalsIgnoreCase(selectedStatus) ? "AVAILABLE" : "ISSUED";
            filters.add(RowFilter.regexFilter("^" + badgeVal + "$", 9));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
        countLabel.setText("Showing " + transactionsTable.getRowCount() + " records");
    }
}

