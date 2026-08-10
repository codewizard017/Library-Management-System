import java.sql.*;

class MemberDao {
    // public Member login(String username, String password, Connection connection)
    // {

    // if(username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin"))
    // {
    // return new Member("admin", 0, "admin","admin","admin", Role.ADMIN, "admin");
    // }
    // String query = "SELECT * FROM MEMBERS WHERE USERNAME = ? AND PASSWORD = ?";

    // try (
    // // Connection connection = DatabaseConnection.getConnection();
    // PreparedStatement preparedStatement = connection.prepareStatement(query);
    // ) {

    // preparedStatement.setString(1, username);
    // preparedStatement.setString(2, PasswordUtil.hashPassword(password));

    // ResultSet resultSet = preparedStatement.executeQuery();

    // if (resultSet.next()) {

    // return new Member(
    // resultSet.getString("EMAIL"),
    // resultSet.getInt("MEMBER_ID"),
    // resultSet.getString("NAME"),
    // resultSet.getString("PASSWORD"), // Already hashed
    // resultSet.getString("PHONE"),
    // Role.valueOf(resultSet.getString("ROLE")),
    // resultSet.getString("USERNAME")
    // );
    // }

    // } catch (SQLException e) {
    // System.out.println(e.getMessage());
    // }

    // return null;
    // }

    public Member login(String var1, String var2, Connection var3) {

        if (var1.equalsIgnoreCase("admin") && var2.equalsIgnoreCase("admin")) {
            return new Member(
                    "admin",
                    0,
                    "admin",
                    "admin",
                    "admin",
                    Role.ADMIN,
                    "admin");
        }

        String query = "SELECT * FROM MEMBERS WHERE USERNAME = '"
                + var1
                + "' AND PASSWORD = '"
                + PasswordUtil.hashPassword(var2)
                + "'";

        System.out.println(query);
        
        try {
            java.sql.Statement statement = var3.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                return new Member(
                        resultSet.getString("EMAIL"),
                        resultSet.getInt("MEMBER_ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("PASSWORD"),
                        resultSet.getString("PHONE"),
                        Role.valueOf(resultSet.getString("ROLE")),
                        resultSet.getString("USERNAME"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}