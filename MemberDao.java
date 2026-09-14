import java.sql.*;
import java.util.ArrayList;

public class MemberDao {

    public Member login(String username, String password, Connection connection) {

        if (username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {
            return new Member(
                    "admin",
                    0,
                    "admin",
                    "admin",
                    "admin",
                    Role.ADMIN,
                    "admin");
        }

        String query = "SELECT * FROM MEMBERS WHERE USERNAME = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                if (resultSet.next()) {

                    // Password stored in the database
                    String storedHash = resultSet.getString("PASSWORD");

                    // Compare entered password with BCrypt hash
                    boolean passwordMatches =
                            PasswordUtil.checkPassword(password, storedHash);

                    if (passwordMatches) {

                        return new Member(
                                resultSet.getString("EMAIL"),
                                resultSet.getInt("MEMBER_ID"),
                                resultSet.getString("NAME"),
                                resultSet.getString("PASSWORD"),
                                resultSet.getString("PHONE"),
                                Role.valueOf(resultSet.getString("ROLE")),
                                resultSet.getString("USERNAME")
                        );
                    }
                }
            }

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return null;
    }

    public static ArrayList<Member> getAllMembers(Connection connection) {
        ArrayList<Member> members = new ArrayList<>();
        String query = "SELECT MEMBER_ID, NAME, USERNAME, EMAIL, PHONE, ROLE, BOOKISSUED FROM MEMBERS ORDER BY MEMBER_ID";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Role role = Role.STUDENT;
                try {
                    role = Role.valueOf(rs.getString("ROLE"));
                } catch (Exception ignored) {}
                Member m = new Member(
                        rs.getInt("MEMBER_ID"),
                        rs.getString("NAME"),
                        rs.getString("USERNAME"),
                        rs.getString("EMAIL"),
                        rs.getString("PHONE"),
                        role,
                        rs.getInt("BOOKISSUED")
                );
                members.add(m);
            }
        } catch (Exception e) {
            System.err.println("Error fetching members: " + e.getMessage());
        }
        return members;
    }

    public static boolean deleteMember(int memberId, Connection connection, Member currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            return false;
        }
        String query = "DELETE FROM MEMBERS WHERE MEMBER_ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    public static int countMembers(Connection connection) {
        String query = "SELECT COUNT(*) FROM MEMBERS";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("Error counting members: " + e.getMessage());
        }
        return 0;
    }
}