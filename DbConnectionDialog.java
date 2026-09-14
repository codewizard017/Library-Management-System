import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;

public class DbConnectionDialog extends JDialog {

    private JTextField hostField;
    private JTextField portField;
    private JTextField serviceField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberCheck;
    private JLabel statusLabel;
    private JButton connectBtn;
    private JButton testBtn;

    private boolean connected = false;
    private Connection activeConnection = null;

    public DbConnectionDialog(Frame owner) {
        super(owner, "Database Configuration - LMS 2026", true);
        initUI();
    }

    private void initUI() {
        setSize(500, 620);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (!connected) {
                    System.exit(0);
                }
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(30, 36, 30, 36));

        // Top Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel iconLabel = new JLabel("Database Configuration", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Enter your Oracle Database credentials to connect", SwingConstants.CENTER);
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subLabel.setForeground(ThemeManager.getTextMuted());
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(6));
        headerPanel.add(subLabel);

        // Form Fields
        DatabaseManager.DbConfig config = DatabaseManager.loadConfig();

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);

        // Host & Port row
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.7;
        formPanel.add(createFieldLabel("Host / Server"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        formPanel.add(createFieldLabel("Port"), gbc);

        hostField = createStyledTextField(config.host, "e.g. localhost");
        portField = createStyledTextField(config.port, "e.g. 1521");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.7;
        formPanel.add(hostField, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.3;
        formPanel.add(portField, gbc);

        // Service / SID
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weightx = 1.0;
        formPanel.add(createFieldLabel("Database Service / SID (Oracle PDB)"), gbc);

        serviceField = createStyledTextField(config.service, "e.g. XEPDB1 or XE");
        gbc.gridy = 3;
        formPanel.add(serviceField, gbc);

        // Username
        gbc.gridy = 4;
        formPanel.add(createFieldLabel("Database Username"), gbc);

        usernameField = createStyledTextField(config.username, "e.g. system or lms_user");
        gbc.gridy = 5;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridy = 6;
        formPanel.add(createFieldLabel("Database Password"), gbc);

        passwordField = new JPasswordField(config.password);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Enter your database password");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,12,8,12;");
        passwordField.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true;");
        gbc.gridy = 7;
        formPanel.add(passwordField, gbc);

        // Remember Checkbox
        rememberCheck = new JCheckBox("Remember connection credentials", config.remember);
        rememberCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberCheck.setOpaque(false);
        gbc.gridy = 8;
        formPanel.add(rememberCheck, gbc);

        // Status / Error Banner
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setForeground(ThemeManager.INFO_COLOR);
        gbc.gridy = 9;
        formPanel.add(statusLabel, gbc);

        // Bottom Actions
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setOpaque(false);

        connectBtn = UIUtils.createPrimaryButton("Connect & Launch Application", e -> performConnect());
        connectBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        connectBtn.setPreferredSize(new Dimension(connectBtn.getPreferredSize().width, 42));

        JPanel subButtonRow = new JPanel(new GridLayout(1, 2, 10, 0));
        subButtonRow.setOpaque(false);

        testBtn = UIUtils.createSecondaryButton("Test Connection", e -> performTest());
        JButton exitBtn = UIUtils.createSecondaryButton("Exit", e -> System.exit(0));

        subButtonRow.add(testBtn);
        subButtonRow.add(exitBtn);

        buttonPanel.add(connectBtn);
        buttonPanel.add(subButtonRow);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Enter key in password field initiates connect
        passwordField.addActionListener(e -> performConnect());
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(ThemeManager.getTextMuted());
        return label;
    }

    private JTextField createStyledTextField(String initialText, String placeholder) {
        JTextField field = new JTextField(initialText);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 8,12,8,12;");
        return field;
    }

    private void setInputsEnabled(boolean enabled) {
        hostField.setEnabled(enabled);
        portField.setEnabled(enabled);
        serviceField.setEnabled(enabled);
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        rememberCheck.setEnabled(enabled);
        connectBtn.setEnabled(enabled);
        testBtn.setEnabled(enabled);
    }

    private void performTest() {
        setInputsEnabled(false);
        statusLabel.setForeground(ThemeManager.INFO_COLOR);
        statusLabel.setText("Connecting to Oracle Database...");

        String host = hostField.getText().trim();
        String port = portField.getText().trim();
        String service = serviceField.getText().trim();
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try (Connection conn = DatabaseManager.establishConnection(host, port, service, user, pass)) {
                    if (conn != null && !conn.isClosed()) {
                        return "SUCCESS";
                    }
                    return "Connection closed";
                } catch (Exception ex) {
                    return ex.getMessage();
                }
            }

            @Override
            protected void done() {
                setInputsEnabled(true);
                try {
                    String result = get();
                    if ("SUCCESS".equals(result)) {
                        statusLabel.setForeground(ThemeManager.SUCCESS_COLOR);
                        statusLabel.setText("Database connection successful!");
                    } else {
                        statusLabel.setForeground(ThemeManager.DANGER_COLOR);
                        statusLabel.setText("Failed: " + formatError(result));
                    }
                } catch (Exception ex) {
                    statusLabel.setForeground(ThemeManager.DANGER_COLOR);
                    statusLabel.setText("Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void performConnect() {
        setInputsEnabled(false);
        statusLabel.setForeground(ThemeManager.INFO_COLOR);
        statusLabel.setText("Authenticating and initializing schema...");

        String host = hostField.getText().trim();
        String port = portField.getText().trim();
        String service = serviceField.getText().trim();
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        SwingWorker<Connection, Void> worker = new SwingWorker<>() {
            private String errorMsg = null;

            @Override
            protected Connection doInBackground() {
                try {
                    Connection conn = DatabaseManager.establishConnection(host, port, service, user, pass);
                    DatabaseManager.initializeSchema(conn);
                    return conn;
                } catch (Exception ex) {
                    errorMsg = ex.getMessage();
                    return null;
                }
            }

            @Override
            protected void done() {
                try {
                    Connection conn = get();
                    if (conn != null) {
                        connected = true;
                        activeConnection = conn;
                        DatabaseManager.setConnection(conn);

                        // Save configuration
                        DatabaseManager.DbConfig config = new DatabaseManager.DbConfig();
                        config.host = host;
                        config.port = port;
                        config.service = service;
                        config.username = user;
                        config.password = pass;
                        config.remember = rememberCheck.isSelected();
                        DatabaseManager.saveConfig(config);

                        dispose(); // Close dialog
                    } else {
                        setInputsEnabled(true);
                        statusLabel.setForeground(ThemeManager.DANGER_COLOR);
                        statusLabel.setText("Connection failed: " + formatError(errorMsg));
                    }
                } catch (Exception ex) {
                    setInputsEnabled(true);
                    statusLabel.setForeground(ThemeManager.DANGER_COLOR);
                    statusLabel.setText("Error: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private String formatError(String raw) {
        if (raw == null) return "Unknown error";
        if (raw.contains("ORA-01017")) return "Invalid username/password (ORA-01017)";
        if (raw.contains("The Network Adapter could not establish")) return "Cannot reach database on " + hostField.getText() + ":" + portField.getText();
        if (raw.contains("ORA-12514")) return "Service name not found: " + serviceField.getText() + " (ORA-12514)";
        if (raw.length() > 55) return raw.substring(0, 52) + "...";
        return raw;
    }

    public Connection showDialog() {
        setVisible(true);
        return activeConnection;
    }
}

