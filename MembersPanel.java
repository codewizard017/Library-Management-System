import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;

public class MembersPanel extends JPanel {

    private final Connection connection;
    private final Member currentUser;

    private JTable membersTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> roleCombo;
    private JLabel countLabel;

    public MembersPanel(Connection conn, Member user) {
        this.connection = conn;
        this.currentUser = user;

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initUI();
        loadMembers();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel headerText = UIUtils.createHeader("Members Management", "View and manage library member registrations and privileges");
        headerPanel.add(headerText, BorderLayout.WEST);

        // Header Action Buttons
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        if (currentUser.getRole() == Role.ADMIN) {
            JButton addBtn = UIUtils.createPrimaryButton("+ Register Member", e -> showRegisterMemberDialog());
            JButton deleteBtn = UIUtils.createDangerButton("Delete Member", e -> deleteSelectedMember());
            actionButtons.add(addBtn);
            actionButtons.add(deleteBtn);
        }

        JButton viewBorrowedBtn = UIUtils.createSecondaryButton("Borrowed Books", e -> viewMemberBorrowedBooks());
        JButton viewHistoryBtn = UIUtils.createSecondaryButton("Borrow History", e -> viewMemberBorrowHistory());
        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh", e -> loadMembers());

        actionButtons.add(viewBorrowedBtn);
        actionButtons.add(viewHistoryBtn);
        actionButtons.add(refreshBtn);

        headerPanel.add(actionButtons, BorderLayout.EAST);

        // Search & Filter Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        searchField = UIUtils.createSearchField("Search by Name, Username, Email, Phone...", text -> applyFilters());
        searchField.setPreferredSize(new Dimension(340, 38));

        JPanel filterControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterControls.setOpaque(false);

        roleCombo = new JComboBox<>(new String[]{"All Roles", "ADMIN", "LIBRARIAN", "STUDENT"});
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        roleCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 4,8,4,8;");
        roleCombo.addActionListener(e -> applyFilters());

        countLabel = new JLabel("Loading members...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(ThemeManager.getTextMuted());

        filterControls.add(new JLabel("Role:"));
        filterControls.add(roleCombo);
        filterControls.add(countLabel);

        toolbar.add(searchField, BorderLayout.WEST);
        toolbar.add(filterControls, BorderLayout.EAST);

        // Table
        String[] columns = {"ID", "Name", "Username", "Email", "Phone", "Role", "Issued Count"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 6) return Integer.class;
                return String.class;
            }
        };

        membersTable = new JTable(tableModel);
        UIUtils.formatTable(membersTable);
        membersTable.getColumnModel().getColumn(5).setCellRenderer(UIUtils.createBadgeRenderer());
        membersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        membersTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        membersTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        membersTable.getColumnModel().getColumn(3).setPreferredWidth(190);
        membersTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        membersTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        membersTable.getColumnModel().getColumn(6).setPreferredWidth(90);

        rowSorter = new TableRowSorter<>(tableModel);
        membersTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(membersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");

        // Layout Assembly
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(toolbar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadMembers() {
        SwingWorker<ArrayList<Member>, Void> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Member> doInBackground() {
                return MemberDao.getAllMembers(connection);
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Member> list = get();
                    tableModel.setRowCount(0);
                    for (Member m : list) {
                        tableModel.addRow(new Object[]{
                                m.getMemberId(),
                                m.getName(),
                                m.getUsername(),
                                m.getEmail(),
                                m.getPhone(),
                                m.getRole() == null ? "STUDENT" : m.getRole().name(),
                                m.getBooksIssued()
                        });
                    }
                    countLabel.setText("Total " + list.size() + " members");
                } catch (Exception e) {
                    countLabel.setText("Error loading members: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim();
        String selectedRole = (String) roleCombo.getSelectedItem();

        ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText)));
        }
        if (selectedRole != null && !"All Roles".equals(selectedRole)) {
            filters.add(RowFilter.regexFilter("^" + selectedRole + "$", 5));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
        countLabel.setText("Showing " + membersTable.getRowCount() + " members");
    }

    private void showRegisterMemberDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Register New Member", true);
        dialog.setSize(440, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 14));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JComboBox<Role> roleSelect = new JComboBox<>(Role.values());
        roleSelect.setSelectedItem(Role.STUDENT);

        panel.add(new JLabel("Full Name :"));
        panel.add(nameField);
        panel.add(new JLabel("Username :"));
        panel.add(usernameField);
        panel.add(new JLabel("Password :"));
        panel.add(passwordField);
        panel.add(new JLabel("Email Address :"));
        panel.add(emailField);
        panel.add(new JLabel("Phone (10 digits) :"));
        panel.add(phoneField);
        panel.add(new JLabel("Role :"));
        panel.add(roleSelect);

        JButton saveBtn = UIUtils.createPrimaryButton("Register Member", e -> {
            String name = nameField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            Role role = (Role) roleSelect.getSelectedItem();

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                UIUtils.showToast(dialog, "All fields are required!", true);
                return;
            }

            if (phone.length() != 10 || !phone.matches("\\d{10}")) {
                UIUtils.showToast(dialog, "Phone number must be exactly 10 digits!", true);
                return;
            }

            Member member = new Member(0, email, name, password, phone, role, username);
            boolean success = member.saveToDatabase(connection, currentUser);
            if (success) {
                UIUtils.showToast(this, "Member registered successfully!", false);
                dialog.dispose();
                loadMembers();
            } else {
                UIUtils.showToast(dialog, "Registration failed. Username, email, or phone may already exist.", true);
            }
        });

        JButton cancelBtn = UIUtils.createSecondaryButton("Cancel", e -> dialog.dispose());

        panel.add(cancelBtn);
        panel.add(saveBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedMember() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showToast(this, "Please select a member to delete.", true);
            return;
        }

        int modelRow = membersTable.convertRowIndexToModel(selectedRow);
        int memberId = (int) tableModel.getValueAt(modelRow, 0);
        String name = (String) tableModel.getValueAt(modelRow, 1);
        int issuedCount = (int) tableModel.getValueAt(modelRow, 6);

        if (issuedCount > 0) {
            UIUtils.showToast(this, "Cannot delete member '" + name + "' while they have " + issuedCount + " issued book(s)!", true);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete member #" + memberId + " (" + name + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = MemberDao.deleteMember(memberId, connection, currentUser);
            if (success) {
                UIUtils.showToast(this, "Member deleted successfully.", false);
                loadMembers();
            } else {
                UIUtils.showToast(this, "Could not delete member.", true);
            }
        }
    }

    private void viewMemberBorrowedBooks() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showToast(this, "Please select a member from the table.", true);
            return;
        }

        int modelRow = membersTable.convertRowIndexToModel(selectedRow);
        int memberId = (int) tableModel.getValueAt(modelRow, 0);
        String name = (String) tableModel.getValueAt(modelRow, 1);

        ArrayList<Book> books = TransactionDao.getMemberBorrowedBooks(memberId, connection);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Borrowed Books: " + name, true);
        dialog.setSize(680, 420);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Currently Borrowed Books for " + name + " (ID: " + memberId + ")");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Book ID", "Title", "Author", "ISBN", "Category", "Shelf"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Book b : books) {
            model.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getCategory(), b.getShelfLocation()});
        }
        JTable table = new JTable(model);
        UIUtils.formatTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeBtn = UIUtils.createSecondaryButton("Close", e -> dialog.dispose());
        JPanel btm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btm.add(closeBtn);
        panel.add(btm, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void viewMemberBorrowHistory() {
        int selectedRow = membersTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showToast(this, "Please select a member from the table.", true);
            return;
        }

        int modelRow = membersTable.convertRowIndexToModel(selectedRow);
        int memberId = (int) tableModel.getValueAt(modelRow, 0);
        String name = (String) tableModel.getValueAt(modelRow, 1);

        ArrayList<TransactionRecord> history = TransactionDao.getMemberBorrowHistory(memberId, connection);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Borrow History: " + name, true);
        dialog.setSize(760, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Borrowing History for " + name + " (ID: " + memberId + ")");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Tx ID", "Book ID", "Title", "Issue Date", "Return Date", "Days", "Fine", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (TransactionRecord r : history) {
            model.addRow(new Object[]{
                    r.getTransactionId(),
                    r.getBookId(),
                    r.getBookTitle(),
                    r.getIssueDate(),
                    r.getReturnDate() == null ? "Not Returned" : r.getReturnDate(),
                    r.getDaysBorrowed() + " days",
                    "₹" + r.getFine(),
                    r.isReturned() ? "AVAILABLE" : "ISSUED"
            });
        }
        JTable table = new JTable(model);
        UIUtils.formatTable(table);
        table.getColumnModel().getColumn(7).setCellRenderer(UIUtils.createBadgeRenderer());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JButton closeBtn = UIUtils.createSecondaryButton("Close", e -> dialog.dispose());
        JPanel btm = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btm.add(closeBtn);
        panel.add(btm, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}

