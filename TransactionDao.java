
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TransactionDao {

    public static boolean viewAllTransactions(Connection connection, Statement statement) {
        String query = "SELECT * FROM TRANSACTIONS";
        System.out.println(query);

        try {
            ResultSet rs = statement.executeQuery(query);
            boolean found = false;
            while (rs.next()) {
                System.out.println("----------------------------");
                System.out.println("Transaction ID : " + rs.getInt("TRANSACTION_ID"));
                System.out.println("Book ID        : " + rs.getInt("BOOK_ID"));
                System.out.println("Member ID      : " + rs.getInt("MEMBER_ID"));
                System.out.println("Issue Date     : " + rs.getDate("ISSUE_DATE"));
                Date returnDate = rs.getDate("RETURN_DATE");

                if (returnDate == null) {
                    System.out.println("Return Date    : Not Returned");
                } else {
                    System.out.println("Return Date    : " + returnDate);
                }

                String returned = rs.getString("RETURNED");

                if (returned.equals("Y")) {
                    System.out.println("Status         : Returned");
                } else {
                    System.out.println("Status         : Issued");
                }

                System.out.println("----------------------------");

                found = true;
            }

            if (!found) {
                System.out.println("No transactions found.");
            }

            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static String issueBook(int bookId, int memberId, LocalDate issueDate, Connection connection, Statement statement) {

        try {
            connection.setAutoCommit(false);

            String query = "SELECT * FROM BOOKS WHERE BOOK_ID = " + bookId;
            ResultSet rs = statement.executeQuery(query);

            if (!rs.next()) {
                return "Book not found.";
            }

            String status = rs.getString("STATUS");
            if (status.equals("ISSUED")) {
                return "Book is already issued.";
            }

            query = "SELECT * FROM MEMBERS WHERE MEMBER_ID = " + memberId;
            rs = statement.executeQuery(query);

            if (!rs.next()) {
                return "Member not found.";
            }

            int booksIssued = rs.getInt("BOOKISSUED");

            if (booksIssued >= 3) {
                return "Member has reached borrowing limit.";
            }

            query = "INSERT INTO TRANSACTIONS(BOOK_ID, MEMBER_ID, ISSUE_DATE, RETURNED) VALUES(" + bookId + "," + memberId + "," + "DATE '" + issueDate + "'," + "'N')";

            statement.executeUpdate(query);

            query = "UPDATE BOOKS SET STATUS='ISSUED' WHERE BOOK_ID = " + bookId;
            statement.executeUpdate(query);

            query = "UPDATE MEMBERS SET BOOKISSUED = BOOKISSUED +1 WHERE MEMBER_ID = " + memberId;
            statement.executeUpdate(query);

            connection.commit();

            return "Book issued successfully.";

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            System.out.println(e.getMessage());
            return "Error issuing book.";
        } finally {

            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public static String returnBook(int bookId, LocalDate returnDate, Connection connection, Statement statement) {

        try {
            connection.setAutoCommit(false);

            // Check Book
            String query = "SELECT * FROM BOOKS WHERE BOOK_ID = " + bookId;
            ResultSet rs = statement.executeQuery(query);

            if (!rs.next()) {
                return "Book not found.";
            }

            String status = rs.getString("STATUS");

            if (status.equals("AVAILABLE")) {
                return "Book is already available.";
            }

            // Find active transaction
            query = "SELECT * FROM TRANSACTIONS WHERE BOOK_ID = " + bookId + " AND RETURNED = 'N'";
            rs = statement.executeQuery(query);

            if (!rs.next()) {
                return "Transaction not found.";
            }

            int memberId = rs.getInt("MEMBER_ID");

            // Update Transaction
            query = "UPDATE TRANSACTIONS SET RETURN_DATE = DATE '" + returnDate + "', RETURNED = 'Y' WHERE TRANSACTION_ID = " + rs.getInt("TRANSACTION_ID");
            // System.out.println(query);
            statement.executeUpdate(query);

            // Update Book
            query = "UPDATE BOOKS SET STATUS = 'AVAILABLE' WHERE BOOK_ID = " + bookId;
            // System.out.println(query);
            statement.executeUpdate(query);

            // Update Member
            query = "UPDATE MEMBERS SET BOOKISSUED = BOOKISSUED - 1 WHERE MEMBER_ID = " + memberId;
            // System.out.println(query);
            statement.executeUpdate(query);

            connection.commit();
            return "Book returned successfully.";

        } catch (Exception e) {

            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }

            System.out.println(e.getMessage());
            return "Error returning book.";

        } finally {

            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public static boolean viewBorrowedBooks(int memberId, Connection connection, Statement statement) {

        String query = "SELECT B.BOOK_ID, B.TITLE, B.AUTHOR, B.ISBN, B.CATEGORY, B.PUBLICATION_YEAR, B.SHELF_LOCATION, B.STATUS FROM BOOKS B, TRANSACTIONS T WHERE B.BOOK_ID = T.BOOK_ID AND T.MEMBER_ID = " + memberId + " AND T.RETURNED = 'N'";

        // System.out.println(query);
        try {
            ResultSet rs = statement.executeQuery(query);
            boolean found = false;
            while (rs.next()) {
                System.out.println("----------------------------");
                System.out.println("Book ID : " + rs.getInt("BOOK_ID"));
                System.out.println("Title   : " + rs.getString("TITLE"));
                System.out.println("Author  : " + rs.getString("AUTHOR"));
                System.out.println("ISBN    : " + rs.getString("ISBN"));
                System.out.println("Category: " + rs.getString("CATEGORY"));
                System.out.println("Publication Year : " + rs.getInt("PUBLICATION_YEAR"));
                System.out.println("Shelf Location : " + rs.getString("SHELF_LOCATION"));
                System.out.println("Status : " + rs.getString("STATUS"));
                System.out.println("----------------------------");
                found = true;
            }

            if (!found) {
                System.out.println("No borrowed books.");
            }
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean viewBorrowHistory(int memberId, Connection connection, Statement statement) {

        String query = "SELECT B.TITLE, T.ISSUE_DATE, T.RETURN_DATE, T.RETURNED FROM BOOKS B, TRANSACTIONS T WHERE B.BOOK_ID = T.BOOK_ID AND T.MEMBER_ID = " + memberId;
        // System.out.println(query);

        try {
            ResultSet rs = statement.executeQuery(query);
            boolean found = false;
            System.out.println("\n===== BORROW HISTORY =====");

            while (rs.next()) {
                System.out.println("Book : " + rs.getString("TITLE"));
                System.out.println("Issue Date : " + rs.getDate("ISSUE_DATE"));
                Date returnDate = rs.getDate("RETURN_DATE");
                if (returnDate == null) {
                    System.out.println("Return Date : Not Returned");
                } else {
                    System.out.println("Return Date : " + returnDate);
                }

                String returned = rs.getString("RETURNED");
                if (returned.equals("Y")) {
                    System.out.println("Status : Returned");
                } else {
                    System.out.println("Status : Issued");
                }
                System.out.println("----------------------------");
                found = true;
            }

            if (!found) {
                System.out.println("No Borrow History.");
            }
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean calculateFine(int memberId, Connection connection, Statement statement) {

        String query = "SELECT B.TITLE, T.ISSUE_DATE, T.RETURN_DATE FROM BOOKS B, TRANSACTIONS T WHERE B.BOOK_ID = T.BOOK_ID AND T.MEMBER_ID = " + memberId + " AND T.RETURNED = 'Y'";
        // System.out.println(query);

        try {
            ResultSet rs = statement.executeQuery(query);
            boolean found = false;
            System.out.println("\n===== FINE DETAILS =====");
            while (rs.next()) {
                String title = rs.getString("TITLE");
                Date issue = rs.getDate("ISSUE_DATE");
                Date returned = rs.getDate("RETURN_DATE");
                LocalDate issueDate = issue.toLocalDate();
                LocalDate returnDate = returned.toLocalDate();
                long days = ChronoUnit.DAYS.between(issueDate, returnDate);
                long fine = 0;
                if (days > 14) {
                    fine = (days - 14) * 5;
                }

                System.out.println("Book : " + title);
                System.out.println("Days Borrowed : " + days);
                System.out.println("Fine : ₹" + fine);
                System.out.println("----------------------");
                found = true;
            }
            if (!found) {
                System.out.println("No completed transactions.");
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
