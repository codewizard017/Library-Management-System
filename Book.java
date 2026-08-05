
import java.sql.*;

public class Book {

     int bookId;
     String title;
     String author;
     String isbn;
     String category;
     int publicationYear;
     String shelfLocation;
     BookStatus status;

    //constructor
    public Book(String title, String author, String isbn, String category, int publicationYear, String shelfLocation) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.shelfLocation = shelfLocation;
        this.status = BookStatus.AVAILABLE;

    }

    public Book(int bookId, String title, String author, String isbn, String category, int publicationYear, String shelfLocation) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.shelfLocation = shelfLocation;
        this.status = BookStatus.AVAILABLE;

    }

    // Getters
    public int getBookId() {
        return bookId;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getCategory() {
        return category;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    public BookStatus getStatus() {
        return status;
    }

    // Setters
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setShelfLocation(String shelfLocation) {
        this.shelfLocation = shelfLocation;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public boolean saveToDatabase(Connection connection, Statement statement) {
        String query = "INSERT INTO BOOKS(TITLE, AUTHOR, ISBN, CATEGORY, PUBLICATION_YEAR, SHELF_LOCATION, STATUS) VALUES ('" + this.title + "', '" + this.author + "', '" + this.isbn + "', '" + this.category + "'," + this.publicationYear + ", '" + this.shelfLocation + "', '" + BookStatus.AVAILABLE + "')";
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
    public void displayBook() {

        System.out.println("----------------------------");
        System.out.println("Book ID : " + bookId);
        System.out.println("Title   : " + title);
        System.out.println("Author  : " + author);
        System.out.println("ISBN    : " + isbn);
        System.out.println("Category: " + category);
        System.out.println("Publication Year: " + publicationYear);
        System.out.println("Shelf : " + shelfLocation);
        System.out.println("Status  : " + status);
        System.out.println("----------------------------");
    }
}
