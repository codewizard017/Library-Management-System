import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

public class LoginDialog extends JDialog {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    private JButton loginBtn;
    private Member authenticatedMember = null;
    private Connection connection;
    private boolean changeDbRequested = false;

    public LoginDialog(Frame owner, Connection conn) {
        super(owner, "Sign In - Library Management System 2026", true);
        this.connection = conn;
        initUI();
    }

    private void initUI() {
        setSize(460, 560);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (authenticatedMember == null && !changeDbRequested) {
                    System.exit(0);
                }
            }
        });

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(32, 40, 32, 40));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        // Top bar with theme toggle
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JButton themeBtn = UIUtils.createSecondaryButton(ThemeManager.isDarkMode() ? "Light Mode" : "Dark Mode", e -> {
            ThemeManager.toggleTheme(this);
            JButton b = (JButton) e.getSource();
            b.setText(ThemeManager.isDarkMode() ? "Light Mode" : "Dark Mode");
        });
        themeBtn.putClientProperty(FlatClientProperties.STYLE, "arc: 8; margin: 4,10,4,10;");
        topBar.add(themeBtn, BorderLayout.EAST);

        JLabel logoLabel = new JLabel("LMS 2026", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        logoLabel.setForeground(ThemeManager.ACCENT_COLOR);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Welcome back! Sign in to continue", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(ThemeManager.getTextMuted());
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(topBar);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(subLabel);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(25, 0, 15, 0));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(ThemeManager.getTextMuted());

        usernameField = new JTextField("admin");
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your username");
        usernameField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,12,8,12;");
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(ThemeManager.getTextMuted());

        passwordField = new JPasswordField("admin");
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your password");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,12,8,12; showRevealButton: true;");
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        errorLabel.setForeground(ThemeManager.DANGER_COLOR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(14));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(6));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(errorLabel);

        // Bottom Actions
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);

        loginBtn = UIUtils.createPrimaryButton("Sign In", e -> performLogin());
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton changeDbBtn = UIUtils.createSecondaryButton("Switch Database Connection", e -> {
            changeDbRequested = true;
            dispose();
        });
        changeDbBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        changeDbBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hintLabel = new JLabel("Default Administrator: admin / admin", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLabel.setForeground(ThemeManager.getTextMuted());
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        bottomPanel.add(loginBtn);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(changeDbBtn);
        bottomPanel.add(Box.createVerticalStrut(12));
        bottomPanel.add(hintLabel);

        root.add(headerPanel, BorderLayout.NORTH);
        root.add(formPanel, BorderLayout.CENTER);
        root.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(root);

        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> passwordField.requestFocusInWindow());
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        loginBtn.setEnabled(false);
        errorLabel.setForeground(ThemeManager.INFO_COLOR);
        errorLabel.setText("Authenticating...");

        SwingWorker<Member, Void> worker = new SwingWorker<>() {
            @Override
            protected Member doInBackground() {
                MemberDao dao = new MemberDao();
                return dao.login(username, password, connection);
            }

            @Override
            protected void done() {
                loginBtn.setEnabled(true);
                try {
                    Member member = get();
                    if (member != null) {
                        authenticatedMember = member;
                        dispose();
                    } else {
                        errorLabel.setForeground(ThemeManager.DANGER_COLOR);
                        errorLabel.setText("Invalid username or password");
                    }
                } catch (Exception ex) {
                    errorLabel.setForeground(ThemeManager.DANGER_COLOR);
                    errorLabel.setText("Login error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    public Member showLogin() {
        setVisible(true);
        return authenticatedMember;
    }

    public boolean isChangeDbRequested() {
        return changeDbRequested;
    }
}

