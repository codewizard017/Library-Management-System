import javax.swing.*;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Check if user requested CLI mode explicitly
        if (args.length > 0 && "--cli".equalsIgnoreCase(args[0])) {
            runCliMode();
            return;
        }

        // Initialize 2026 Modern Theme (FlatLaf)
        ThemeManager.initializeTheme();

        // Launch Modern GUI application on Event Dispatch Thread
        SwingUtilities.invokeLater(Main::launchGuiFlow);
    }

    private static void launchGuiFlow() {
        // 1. Prompt user for Database ID & Password
        DbConnectionDialog dbDialog = new DbConnectionDialog(null);
        Connection conn = dbDialog.showDialog();

        if (conn == null) {
            System.exit(0);
            return;
        }

        runLoginLoop(conn);
    }

    private static void runLoginLoop(Connection conn) {
        while (true) {
            LoginDialog loginDialog = new LoginDialog(null, conn);
            Member member = loginDialog.showLogin();

            if (loginDialog.isChangeDbRequested()) {
                // User requested to switch database
                DatabaseManager.closeConnection();
                DbConnectionDialog dbDialog = new DbConnectionDialog(null);
                conn = dbDialog.showDialog();
                if (conn == null) {
                    System.exit(0);
                    return;
                }
                continue;
            }

            if (member == null) {
                // User closed login dialog without logging in
                System.exit(0);
                return;
            }

            // Launch Main Application Window
            final Connection activeConn = conn;
            final Member activeMember = member;

            MainFrame[] frameHolder = new MainFrame[1];

            Runnable onLogout = () -> {
                frameHolder[0].dispose();
                runLoginLoop(activeConn);
            };

            Runnable onChangeDb = () -> {
                frameHolder[0].dispose();
                DatabaseManager.closeConnection();
                launchGuiFlow();
            };

            frameHolder[0] = new MainFrame(activeConn, activeMember, onLogout, onChangeDb);
            frameHolder[0].setVisible(true);
            break;
        }
    }

    // Fallback CLI mode (for backward compatibility if run with --cli)
    private static void runCliMode() {
        Scanner sc = new Scanner(System.in);
        System.out.println("===== LMS 2026 (Console Mode) =====");
        System.out.print("Enter Database Host [localhost]: ");
        String host = sc.nextLine().trim();
        if (host.isEmpty()) host = "localhost";

        System.out.print("Enter Database Port [1521]: ");
        String port = sc.nextLine().trim();
        if (port.isEmpty()) port = "1521";

        System.out.print("Enter Database Service [XEPDB1]: ");
        String service = sc.nextLine().trim();
        if (service.isEmpty()) service = "XEPDB1";

        System.out.print("Enter Database Username [system]: ");
        String username = sc.nextLine().trim();
        if (username.isEmpty()) username = "system";

        System.out.print("Enter Database Password: ");
        String password = sc.nextLine();

        Connection connection;
        try {
            connection = DatabaseManager.establishConnection(host, port, service, username, password);
            DatabaseManager.initializeSchema(connection);
            System.out.println("Database connected smoothly!\n");
        } catch (Exception e) {
            System.err.println("Connection failed: " + e.getMessage());
            return;
        }

        System.out.println("--- Login ---");
        System.out.print("Enter Username: ");
        String loginUser = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String loginPass = sc.nextLine().trim();

        MemberDao memberDao = new MemberDao();
        Member member = memberDao.login(loginUser, loginPass, connection);

        if (member == null) {
            System.out.println("Invalid Username or Password");
            return;
        }

        System.out.println("Login Successful! Welcome " + member.getName());
        Library library = new Library();

        try (Statement statement = connection.createStatement()) {
            switch (member.getRole()) {
                case ADMIN:
                    AdminMenu.show(connection, statement, sc, library, member);
                    break;
                case LIBRARIAN:
                    LibrarianMenu.show(connection, statement, sc, library, member);
                    break;
                case STUDENT:
                    StudentMenu.show(connection, statement, sc, library, member);
                    break;
                default:
                    System.out.println("Invalid role.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
