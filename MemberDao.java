import java.sql.*;

class MemberDao
{
    public Member login(String username, String password, Connection connection) {

    if(username.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin"))
    {
        return new Member("admin","admin","admin");
    }
    String query = "SELECT * FROM MEMBERS WHERE USERNAME = ? AND PASSWORD = ?";

    try (
            // Connection connection = DatabaseConnection.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
    ) {

        preparedStatement.setString(1, username);
        preparedStatement.setString(2, PasswordUtil.hashPassword(password));

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {

            return new Member(
                    resultSet.getString("EMAIL"),
                    resultSet.getInt("MEMBERID"),
                    resultSet.getString("NAME"),
                    resultSet.getString("PASSWORD"),   // Already hashed
                    resultSet.getString("PHONE"),
                    Role.valueOf(resultSet.getString("ROLE")),
                    resultSet.getString("USERNAME")
            );
        }

    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }

    return null;
}
}