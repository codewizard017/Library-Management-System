
import java.sql.*;
import java.util.ArrayList;

public class BookDao {

    public static boolean saveToDatabase(Book book, Connection connection, Statement statement, Member currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            System.out.println("Not allowed to access this method.");
            return false;
        }

        String query = "INSERT INTO BOOKS(TITLE, AUTHOR, ISBN, CATEGORY, PUBLICATION_YEAR, SHELF_LOCATION, STATUS) VALUES ('"
                + book.title + "', '" + book.author + "', '" + book.isbn + "', '" + book.category + "',"
                + book.publicationYear + ", '" + book.shelfLocation + "', '" + BookStatus.AVAILABLE + "')";
        // System.out.println(query);
        try {

            ResultSet rs = statement.executeQuery(query);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean viewAllBooks(Connection connection, Statement statement) {
        String query = "SELECT * FROM BOOKS";
        System.out.println(query);
        try {

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("book_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                String isbn = rs.getString("isbn");
                String category = rs.getString("category");
                int publicationyr = rs.getInt("publication_year");
                String shelflocation = rs.getString("shelf_location");
                String status = rs.getString("status");
                // int bookissued = rs.getInt("bookissued");

                System.out.printf("%d %s %s %s %s %d %s %s\n", id, title, author, isbn, category, publicationyr,
                        shelflocation, status);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public static boolean removeBook(int bookId, Connection connection, Statement statement, Member currentUser) {

        if (currentUser.getRole() != Role.ADMIN) {
            System.out.println("Not allowed to access this method.");
            return false;
        }

        String query = "DELETE FROM BOOKS WHERE BOOK_ID = " + bookId;
        //System.out.println(query);

        try {
            int rows = statement.executeUpdate(query);

            if (rows > 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return false;
        }
    }

    public static Book searchBook(int bookId, Connection connection, Statement statement) {
        String query = "SELECT * FROM BOOKS WHERE BOOK_ID = " + bookId;

        System.out.println(query);

        try {
            ResultSet rs = statement.executeQuery(query);

            if (rs.next()) {

                int id = rs.getInt("BOOK_ID");
                String title = rs.getString("TITLE");
                String author = rs.getString("AUTHOR");
                String isbn = rs.getString("ISBN");
                String category = rs.getString("CATEGORY");
                int publicationYear = rs.getInt("PUBLICATION_YEAR");
                String shelfLocation = rs.getString("SHELF_LOCATION");

                Book book = new Book(
                        id,
                        title,
                        author,
                        isbn,
                        category,
                        publicationYear,
                        shelfLocation);

                String status = rs.getString("STATUS");

                if (status.equals("ISSUED")) {
                    book.setStatus(BookStatus.ISSUED);
                } else {
                    book.setStatus(BookStatus.AVAILABLE);
                }

                return book;
            }

            return null;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static ArrayList<Book> searchBooksByTitle(String title, Connection connection, Statement statement) {

        ArrayList<Book> books = new ArrayList<>();

        String query = "SELECT * FROM BOOKS WHERE TITLE = '" + title + "'";

        System.out.println(query);

        try {

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                Book book = new Book(
                        rs.getInt("BOOK_ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getString("ISBN"),
                        rs.getString("CATEGORY"),
                        rs.getInt("PUBLICATION_YEAR"),
                        rs.getString("SHELF_LOCATION"));

                if (rs.getString("STATUS").equals("ISSUED")) {
                    book.setStatus(BookStatus.ISSUED);
                }

                books.add(book);
            }

            return books;

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return books;
        }
    }

    public static ArrayList<Book> searchBooksByISBN(String isbn, Connection connection, Statement statement) {

        ArrayList<Book> books = new ArrayList<>();

        String query = "SELECT * FROM BOOKS WHERE ISBN = '" + isbn + "'";

        System.out.println(query);

        try {

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                Book book = new Book(
                        rs.getInt("BOOK_ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getString("ISBN"),
                        rs.getString("CATEGORY"),
                        rs.getInt("PUBLICATION_YEAR"),
                        rs.getString("SHELF_LOCATION"));

                if (rs.getString("STATUS").equals("ISSUED")) {
                    book.setStatus(BookStatus.ISSUED);
                }

                books.add(book);
            }

            return books;

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return books;
        }
    }

    public static ArrayList<Book> searchBooksByAuthor(String author, Connection connection, Statement statement) {

        ArrayList<Book> books = new ArrayList<>();

        String query = "SELECT * FROM BOOKS WHERE AUTHOR = '" + author + "'";

        System.out.println(query);

        try {

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                Book book = new Book(
                        rs.getInt("BOOK_ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getString("ISBN"),
                        rs.getString("CATEGORY"),
                        rs.getInt("PUBLICATION_YEAR"),
                        rs.getString("SHELF_LOCATION"));

                if (rs.getString("STATUS").equals("ISSUED")) {
                    book.setStatus(BookStatus.ISSUED);
                }

                books.add(book);
            }

            return books;

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return books;
        }
    }

    public static ArrayList<Book> searchBooksByCategory(String category, Connection connection, Statement statement) {

        ArrayList<Book> books = new ArrayList<>();

        String query = "SELECT * FROM BOOKS WHERE CATEGORY = '" + category + "'";

        System.out.println(query);

        try {

            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                Book book = new Book(
                        rs.getInt("BOOK_ID"),
                        rs.getString("TITLE"),
                        rs.getString("AUTHOR"),
                        rs.getString("ISBN"),
                        rs.getString("CATEGORY"),
                        rs.getInt("PUBLICATION_YEAR"),
                        rs.getString("SHELF_LOCATION"));

                if (rs.getString("STATUS").equals("ISSUED")) {
                    book.setStatus(BookStatus.ISSUED);
                }

                books.add(book);
            }

            return books;

        } catch (Exception e) {

            System.out.println(e.getMessage());
            return books;
        }
    }

    public static int countBooks(Connection connection, Statement statement) {

        String query = "SELECT COUNT(*) AS TOTAL FROM BOOKS";
        System.out.println(query);

        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("TOTAL");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }

    public static int countAvailableBooks(Connection connection, Statement statement) {

        String query = "SELECT COUNT(*) AS TOTAL FROM BOOKS WHERE STATUS='AVAILABLE'";
        System.out.println(query);

        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("TOTAL");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public static int countIssuedBooks(Connection connection, Statement statement) {
        String query = "SELECT COUNT(*) AS TOTAL FROM BOOKS WHERE STATUS='ISSUED'";
        System.out.println(query);

        try {
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("TOTAL");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

}
