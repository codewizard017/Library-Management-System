import java.sql.*;
public class Member {

    // Attributes
    private int memberId;
    private String name;
    private String username;
    private String password;
    private Role role;
    private String email;
    private String phone;
    private int booksIssued;

    // Constructor
    public Member(String name, String email, String phone) {

        this.name = name;
        this.email = email;
        this.phone = phone;
        this.booksIssued = 0; // Initially no books are issued
    }

    public Member(int memberId, String name, String email, String phone) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.booksIssued = 0; // Initially no books are issued
    }
    public Member(int memberId, String name, String email, String phone, int bookIssued) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.booksIssued = bookIssued; // Initially no books are issued
    }

    public Member( String email, int memberId, String name, String password, String phone, Role role, String username) {
        this.booksIssued = 0;
        this.email = email;
        this.memberId = memberId;
        this.name = name;
        this.password = password;//SHA-256
        this.phone = phone;
        this.role = role;
        this.username = username;
    }

    public Member(int booksIssued, String email, String name, String password, String phone, Role role, String username) {
        this.booksIssued = booksIssued;
        this.email = email;
        this.name = name;
        this.password = PasswordUtil.hashPassword(password);
        this.phone = phone;
        this.role = role;
        this.username = username;
    }



    // Getters

    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Role getRole() {
        return role;
    }

    public int getBooksIssued() { 
        return booksIssued; 
    }

    // Setters

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBooksIssued(int booksIssued) { 
        this.booksIssued = booksIssued; 
    }

    public boolean saveToDatabase(Connection connection) {

    String query = "INSERT INTO MEMBERS " +
                   "(NAME, USERNAME, PASSWORD, ROLE, EMAIL, PHONE, BOOKISSUED) " +
                   "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

        preparedStatement.setString(1, this.name);
        preparedStatement.setString(2, this.username);
        preparedStatement.setString(3, this.password);      // Already hashed
        preparedStatement.setString(4, this.role.name());   // Enum to String
        preparedStatement.setString(5, this.email);
        preparedStatement.setString(6, this.phone);
        preparedStatement.setInt(7, this.booksIssued);

        
        int rows = preparedStatement.executeUpdate();

        return rows > 0;

    } catch (SQLException e) {

        System.out.println(e.getMessage());
        return false;
    }
}

    // Display Method

    public void displayMember() {

        System.out.println("----------------------------");
        System.out.println("Member ID : " + memberId);
        System.out.println("Name      : " + name);
        System.out.println("Email     : " + email);
        System.out.println("Phone     : " + phone);
        System.out.println("Books : " + booksIssued);
        System.out.println("----------------------------");
    }

}