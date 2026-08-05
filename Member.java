import java.sql.*;

public class Member {

    // Attributes
    private int memberId;
    private String name;
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



    // Getters

    public int getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
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

    public boolean saveToDatabase(Connection connection, Statement statement) {
        String query = "INSERT INTO MEMBERS(NAME, EMAIL, PHONE, BOOKISSUED) VALUES ('" + this.name + "', '" + this.email + "', '" + this.phone + "', '" + this.booksIssued + "')";
        System.out.println(query);
        try {

            ResultSet rs = statement.executeQuery(query);
            return true;
        } catch (Exception e) {
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