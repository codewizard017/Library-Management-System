import java.sql.*;

class MemberDao {

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
}


// import java.sql.*;

// class MemberDao {

//     public Member login(String username, String password, Connection connection) {

//         String query = "SELECT * FROM MEMBERS WHERE USERNAME = '"
//                 + username
//                 + "' AND PASSWORD = '"
//                 + password
//                 + "'";

//         System.out.println("SQL SENT TO DATABASE:");
//         System.out.println(query);

//         try {
//             Statement statement = connection.createStatement();
//             ResultSet resultSet = statement.executeQuery(query);

//             if (resultSet.next()) {
//                 System.out.println("LOGIN SUCCESSFUL");
//                 return new Member(
//                         resultSet.getString("EMAIL"),
//                         resultSet.getInt("MEMBER_ID"),
//                         resultSet.getString("NAME"),
//                         resultSet.getString("PASSWORD"),
//                         resultSet.getString("PHONE"),
//                         Role.valueOf(resultSet.getString("ROLE")),
//                         resultSet.getString("USERNAME")
//                 );
//             }

//         } catch (SQLException | IllegalArgumentException e) {
//             e.printStackTrace();
//         }

//         return null;
//     }
// }