import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class BooksPanel extends JPanel {

    private final Connection connection;
    private final Member currentUser;

    private JTable booksTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private JComboBox<String> statusCombo;
    private JLabel countLabel;

    public BooksPanel(Connection conn, Member user) {
        this.connection = conn;
        this.currentUser = user;

        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        initUI();
        loadBooks();
    }

    private void initUI() {
        // Top Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel headerText = UIUtils.createHeader("Books Inventory", "Search, filter, and manage library books catalog");
        headerPanel.add(headerText, BorderLayout.WEST);

        // Header Action Buttons
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtons.setOpaque(false);

        if (currentUser.getRole() == Role.ADMIN) {
            JButton addBtn = UIUtils.createPrimaryButton("+ Add New Book", e -> showAddBookDialog());
            JButton deleteBtn = UIUtils.createDangerButton("Delete Book", e -> deleteSelectedBook());
            actionButtons.add(addBtn);
            actionButtons.add(deleteBtn);
        }

        JButton refreshBtn = UIUtils.createSecondaryButton("Refresh", e -> loadBooks());
        actionButtons.add(refreshBtn);
        headerPanel.add(actionButtons, BorderLayout.EAST);

        // Filter / Search Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(15, 0));
        toolbar.setOpaque(false);
        toolbar.setBorder(new EmptyBorder(0, 0, 10, 0));

        searchField = UIUtils.createSearchField("Search by Title, Author, ISBN, Category...", text -> applyFilters());
        searchField.setPreferredSize(new Dimension(340, 38));

        JPanel filterControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterControls.setOpaque(false);

        categoryCombo = new JComboBox<>(new String[]{"All Categories"});
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 4,8,4,8;");
        categoryCombo.addActionListener(e -> applyFilters());

        statusCombo = new JComboBox<>(new String[]{"All Statuses", "AVAILABLE", "ISSUED"});
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statusCombo.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 4,8,4,8;");
        statusCombo.addActionListener(e -> applyFilters());

        countLabel = new JLabel("Loading books...");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(ThemeManager.getTextMuted());

        filterControls.add(new JLabel("Category:"));
        filterControls.add(categoryCombo);
        filterControls.add(new JLabel("Status:"));
        filterControls.add(statusCombo);
        filterControls.add(countLabel);

        toolbar.add(searchField, BorderLayout.WEST);
        toolbar.add(filterControls, BorderLayout.EAST);

        // Table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Category", "Year", "Shelf", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 5) return Integer.class;
                return String.class;
            }
        };

        booksTable = new JTable(tableModel);
        UIUtils.formatTable(booksTable);
        booksTable.getColumnModel().getColumn(7).setCellRenderer(UIUtils.createBadgeRenderer());
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(160);
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        booksTable.getColumnModel().getColumn(4).setPreferredWidth(110);
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(60);
        booksTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        booksTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        rowSorter = new TableRowSorter<>(tableModel);
        booksTable.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.putClientProperty(FlatClientProperties.STYLE, "arc: 12;");

        // Layout assembly
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(toolbar, BorderLayout.SOUTH);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadBooks() {
        SwingWorker<ArrayList<Book>, Void> worker = new SwingWorker<>() {
            @Override
            protected ArrayList<Book> doInBackground() {
                return BookDao.getAllBooks(connection);
            }

            @Override
            protected void done() {
                try {
                    ArrayList<Book> list = get();
                    tableModel.setRowCount(0);
                    Set<String> categories = new HashSet<>();

                    for (Book b : list) {
                        tableModel.addRow(new Object[]{
                                b.getBookId(),
                                b.getTitle(),
                                b.getAuthor(),
                                b.getIsbn(),
                                b.getCategory(),
                                b.getPublicationYear(),
                                b.getShelfLocation(),
                                b.getStatus() == null ? "AVAILABLE" : b.getStatus().name()
                        });
                        if (b.getCategory() != null && !b.getCategory().isBlank()) {
                            categories.add(b.getCategory());
                        }
                    }

                    // Update Category Dropdown
                    String currentSelected = (String) categoryCombo.getSelectedItem();
                    categoryCombo.removeAllItems();
                    categoryCombo.addItem("All Categories");
                    for (String cat : categories) {
                        categoryCombo.addItem(cat);
                    }
                    if (currentSelected != null) {
                        categoryCombo.setSelectedItem(currentSelected);
                    }

                    countLabel.setText("Showing " + list.size() + " books");
                } catch (Exception e) {
                    countLabel.setText("Error loading books: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void applyFilters() {
        String searchText = searchField.getText().trim();
        String selectedCategory = (String) categoryCombo.getSelectedItem();
        String selectedStatus = (String) statusCombo.getSelectedItem();

        ArrayList<RowFilter<Object, Object>> filters = new ArrayList<>();

        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(searchText)));
        }
        if (selectedCategory != null && !"All Categories".equals(selectedCategory)) {
            filters.add(RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(selectedCategory) + "$", 4));
        }
        if (selectedStatus != null && !"All Statuses".equals(selectedStatus)) {
            filters.add(RowFilter.regexFilter("^" + selectedStatus + "$", 7));
        }

        if (filters.isEmpty()) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.andFilter(filters));
        }
        countLabel.setText("Showing " + booksTable.getRowCount() + " books");
    }

    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setSize(440, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 14));
        panel.setBorder(new EmptyBorder(24, 28, 24, 28));

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField catField = new JTextField();
        JTextField yearField = new JTextField("2024");
        JTextField shelfField = new JTextField();

        panel.add(new JLabel("Title :"));
        panel.add(titleField);
        panel.add(new JLabel("Author :"));
        panel.add(authorField);
        panel.add(new JLabel("ISBN :"));
        panel.add(isbnField);
        panel.add(new JLabel("Category :"));
        panel.add(catField);
        panel.add(new JLabel("Publication Year :"));
        panel.add(yearField);
        panel.add(new JLabel("Shelf Location :"));
        panel.add(shelfField);

        JButton saveBtn = UIUtils.createPrimaryButton("Save Book", e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn = isbnField.getText().trim();
            String category = catField.getText().trim();
            String yearStr = yearField.getText().trim();
            String shelf = shelfField.getText().trim();

            if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || category.isEmpty() || shelf.isEmpty()) {
                UIUtils.showToast(dialog, "All fields are required!", true);
                return;
            }

            int year;
            try {
                year = Integer.parseInt(yearStr);
                if (year < 1000) {
                    UIUtils.showToast(dialog, "Year must be >= 1000", true);
                    return;
                }
            } catch (NumberFormatException ex) {
                UIUtils.showToast(dialog, "Publication year must be a valid number!", true);
                return;
            }

            Book book = new Book(title, author, isbn, category, year, shelf);
            boolean success = BookDao.saveBookPreparedStatement(book, connection, currentUser);
            if (success) {
                UIUtils.showToast(this, "Book added successfully!", false);
                dialog.dispose();
                loadBooks();
            } else {
                UIUtils.showToast(dialog, "Failed to add book. ISBN may already exist or DB error.", true);
            }
        });

        JButton cancelBtn = UIUtils.createSecondaryButton("Cancel", e -> dialog.dispose());

        panel.add(cancelBtn);
        panel.add(saveBtn);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow < 0) {
            UIUtils.showToast(this, "Please select a book to delete from the table.", true);
            return;
        }

        int modelRow = booksTable.convertRowIndexToModel(selectedRow);
        int bookId = (int) tableModel.getValueAt(modelRow, 0);
        String title = (String) tableModel.getValueAt(modelRow, 1);
        String status = (String) tableModel.getValueAt(modelRow, 7);

        if ("ISSUED".equalsIgnoreCase(status)) {
            UIUtils.showToast(this, "Cannot delete book '" + title + "' while it is currently issued to a member!", true);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to permanently remove book #" + bookId + " (" + title + ")?",
                "Confirm Book Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try (Statement st = connection.createStatement()) {
                boolean success = BookDao.removeBook(bookId, connection, st, currentUser);
                if (success) {
                    UIUtils.showToast(this, "Book removed successfully.", false);
                    loadBooks();
                } else {
                    UIUtils.showToast(this, "Could not remove book.", true);
                }
            } catch (Exception e) {
                UIUtils.showToast(this, "Error deleting book: " + e.getMessage(), true);
            }
        }
    }
}

