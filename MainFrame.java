import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private final Connection connection;
    private Member currentUser;
    private final Runnable onLogoutRequested;
    private final Runnable onChangeDbRequested;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JLabel currentViewTitle;
    private JButton themeToggleBtn;

    private final Map<String, JButton> navButtons = new HashMap<>();
    private String activeView = "DASHBOARD";

    // Panels
    private DashboardPanel dashboardPanel;
    private BooksPanel booksPanel;
    private MembersPanel membersPanel;
    private CirculationPanel circulationPanel;
    private TransactionsPanel transactionsPanel;
    private StudentPortalPanel studentPortalPanel;

    public MainFrame(Connection conn, Member user, Runnable onLogout, Runnable onChangeDb) {
        super("Library Management System 2026");
        this.connection = conn;
        this.currentUser = user;
        this.onLogoutRequested = onLogout;
        this.onChangeDbRequested = onChangeDb;

        initUI();
    }

    private void initUI() {
        setSize(1320, 840);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setOpaque(false);

        // 1. Navigation Sidebar
        JPanel sidebar = createSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // 2. Right Side: Top Header + Content Area
        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setOpaque(false);

        JPanel topHeader = createTopHeader();
        rightSide.add(topHeader, BorderLayout.NORTH);

        // Content Area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);

        initViews();
        rightSide.add(contentPanel, BorderLayout.CENTER);

        root.add(rightSide, BorderLayout.CENTER);
        setContentPane(root);

        // Default active view
        switchView("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.putClientProperty(FlatClientProperties.STYLE, "background: $Panel.background;");
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeManager.getBorderColor()));

        // App Branding Top
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(26, 22, 24, 22));

        JLabel brandLogo = new JLabel("LMS 2026");
        brandLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandLogo.setForeground(ThemeManager.ACCENT_COLOR);

        JLabel brandSub = new JLabel("Library System");
        brandSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSub.setForeground(ThemeManager.getTextMuted());

        brandPanel.add(brandLogo);
        brandPanel.add(Box.createVerticalStrut(2));
        brandPanel.add(brandSub);

        // Navigation Menu List
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(10, 14, 10, 14));

        addNavButton(menuPanel, "DASHBOARD", "  Overview", "DASHBOARD");
        addNavButton(menuPanel, "BOOKS", "  Books Catalog", "BOOKS");

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.LIBRARIAN) {
            addNavButton(menuPanel, "CIRCULATION", "  Circulation Desk", "CIRCULATION");
            addNavButton(menuPanel, "MEMBERS", "  Members", "MEMBERS");
            addNavButton(menuPanel, "TRANSACTIONS", "  Transactions", "TRANSACTIONS");
        }

        if (currentUser.getRole() == Role.STUDENT) {
            addNavButton(menuPanel, "STUDENT_PORTAL", "  My Borrowings", "STUDENT_PORTAL");
        }

        menuPanel.add(Box.createVerticalGlue());

        // Bottom User Profile Card
        JPanel bottomUserCard = new JPanel(new BorderLayout(12, 0));
        bottomUserCard.setOpaque(false);
        bottomUserCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.getBorderColor()),
                new EmptyBorder(16, 16, 16, 16)
        ));

        // Avatar circle
        String initial = currentUser.getName() != null && !currentUser.getName().isEmpty()
                ? currentUser.getName().substring(0, 1).toUpperCase() : "U";
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ThemeManager.ACCENT_COLOR);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        avatar.setPreferredSize(new Dimension(38, 38));
        avatar.setOpaque(false);

        JLabel avatarLbl = new JLabel(initial);
        avatarLbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        avatarLbl.setForeground(Color.WHITE);
        avatar.add(avatarLbl);

        // User info text
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setOpaque(false);

        JLabel nameLabel = new JLabel(currentUser.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel roleLabel = new JLabel(currentUser.getRole() != null ? currentUser.getRole().name() : "USER");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        roleLabel.setForeground(ThemeManager.ACCENT_COLOR);

        userInfo.add(nameLabel);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(roleLabel);

        // Logout icon button
        JButton logoutBtn = new JButton("Log Out");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 4,8,4,8;");
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                if (onLogoutRequested != null) onLogoutRequested.run();
            }
        });

        bottomUserCard.add(avatar, BorderLayout.WEST);
        bottomUserCard.add(userInfo, BorderLayout.CENTER);
        bottomUserCard.add(logoutBtn, BorderLayout.EAST);

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(bottomUserCard, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addNavButton(JPanel container, String key, String text, String viewTarget) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,14,8,14;");

        btn.addActionListener(e -> switchView(viewTarget));

        navButtons.put(key, btn);
        container.add(btn);
        container.add(Box.createVerticalStrut(6));
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getBorderColor()),
                new EmptyBorder(14, 28, 14, 28)
        ));

        currentViewTitle = new JLabel("Overview");
        currentViewTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.add(currentViewTitle, BorderLayout.WEST);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightControls.setOpaque(false);

        // DB Status Pill
        JPanel dbBadge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(16, 185, 129, 30));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        dbBadge.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(ThemeManager.SUCCESS_COLOR);
        dot.setFont(new Font("Segoe UI", Font.BOLD, 10));

        JLabel dbText = new JLabel("Oracle DB: Connected");
        dbText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        dbText.setForeground(ThemeManager.SUCCESS_COLOR);

        dbBadge.add(dot);
        dbBadge.add(dbText);

        // Switch DB Button
        JButton switchDbBtn = UIUtils.createSecondaryButton("Switch DB", e -> {
            if (onChangeDbRequested != null) onChangeDbRequested.run();
        });
        switchDbBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        switchDbBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 4,10,4,10;");

        // Theme Toggle Button
        themeToggleBtn = UIUtils.createSecondaryButton(ThemeManager.isDarkMode() ? "Light Mode" : "Dark Mode", e -> {
            ThemeManager.toggleTheme(this);
            themeToggleBtn.setText(ThemeManager.isDarkMode() ? "Light Mode" : "Dark Mode");
        });
        themeToggleBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        themeToggleBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 4,10,4,10;");

        rightControls.add(dbBadge);
        rightControls.add(switchDbBtn);
        rightControls.add(themeToggleBtn);

        header.add(rightControls, BorderLayout.EAST);
        return header;
    }

    private void initViews() {
        dashboardPanel = new DashboardPanel(
                connection, currentUser,
                () -> { switchView("CIRCULATION"); if (circulationPanel != null) circulationPanel.selectIssueTab(); },
                () -> { switchView("CIRCULATION"); if (circulationPanel != null) circulationPanel.selectReturnTab(); },
                () -> switchView("BOOKS"),
                () -> switchView("MEMBERS")
        );
        contentPanel.add(dashboardPanel, "DASHBOARD");

        booksPanel = new BooksPanel(connection, currentUser);
        contentPanel.add(booksPanel, "BOOKS");

        if (currentUser.getRole() == Role.ADMIN || currentUser.getRole() == Role.LIBRARIAN) {
            circulationPanel = new CirculationPanel(connection, currentUser, () -> {
                if (dashboardPanel != null) dashboardPanel.refreshData();
                if (booksPanel != null) booksPanel.loadBooks();
                if (transactionsPanel != null) transactionsPanel.loadTransactions();
            });
            contentPanel.add(circulationPanel, "CIRCULATION");

            membersPanel = new MembersPanel(connection, currentUser);
            contentPanel.add(membersPanel, "MEMBERS");

            transactionsPanel = new TransactionsPanel(connection, currentUser);
            contentPanel.add(transactionsPanel, "TRANSACTIONS");
        }

        if (currentUser.getRole() == Role.STUDENT) {
            studentPortalPanel = new StudentPortalPanel(connection, currentUser);
            contentPanel.add(studentPortalPanel, "STUDENT_PORTAL");
        }
    }

    public void switchView(String viewKey) {
        this.activeView = viewKey;
        cardLayout.show(contentPanel, viewKey);

        // Update nav button highlight styles
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton b = entry.getValue();
            if (entry.getKey().equals(viewKey)) {
                b.setBackground(ThemeManager.ACCENT_COLOR);
                b.setForeground(Color.WHITE);
                b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            } else {
                b.setBackground(null);
                b.setForeground(ThemeManager.getTextPrimary());
                b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }
        }

        // Update header title
        switch (viewKey) {
            case "DASHBOARD":
                currentViewTitle.setText("Dashboard Overview");
                if (dashboardPanel != null) dashboardPanel.refreshData();
                break;
            case "BOOKS":
                currentViewTitle.setText("Books Inventory");
                if (booksPanel != null) booksPanel.loadBooks();
                break;
            case "CIRCULATION":
                currentViewTitle.setText("Circulation Desk");
                if (circulationPanel != null) circulationPanel.refreshCirculationData();
                break;
            case "MEMBERS":
                currentViewTitle.setText("Members Management");
                if (membersPanel != null) membersPanel.loadMembers();
                break;
            case "TRANSACTIONS":
                currentViewTitle.setText("Transaction History");
                if (transactionsPanel != null) transactionsPanel.loadTransactions();
                break;
            case "STUDENT_PORTAL":
                currentViewTitle.setText("My Student Portal");
                if (studentPortalPanel != null) studentPortalPanel.loadStudentData();
                break;
        }
    }
}

