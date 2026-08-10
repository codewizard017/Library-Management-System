import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class StudentMenu {
    public static void show(
            Connection connection,
            Statement statement,
            Scanner sc,
            Library library,
            Member currentUser) {

        while (true) {

            System.out.println("\n===== STUDENT MENU =====");

            System.out.println("1. View All Books");
            System.out.println("2. Search Book");
            System.out.println("3. My Borrowed Books");
            System.out.println("4. My Borrow History");
            System.out.println("5. My Fine");
            System.out.println("0. Logout");

            System.out.print("Enter Choice : ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1: {

                    BookDao.viewAllBooks(
                            connection,
                            statement);

                    break;
                }

                case 2: {

                    System.out.println("\nSearch Book By");
                    System.out.println("1. Book ID");
                    System.out.println("2. Title");
                    System.out.println("3. ISBN");
                    System.out.println("4. Author");
                    System.out.println("5. Category");

                    System.out.print("Enter Choice : ");

                    int searchChoice = sc.nextInt();
                    sc.nextLine();

                    switch (searchChoice) {

                        case 1: {

                            System.out.print("Enter Book ID : ");
                            int bookId = sc.nextInt();
                            sc.nextLine();

                            Book book = BookDao.searchBook(
                                    bookId,
                                    connection,
                                    statement);

                            if (book != null) {
                                book.displayBook();
                            } else {
                                System.out.println("Book Not Found.");
                            }

                            break;
                        }

                        case 2: {

                            System.out.print("Enter Title : ");
                            String title = sc.nextLine();

                            ArrayList<Book> books = BookDao.searchBooksByTitle(
                                    title,
                                    connection,
                                    statement);

                            library.displayBooks(books);

                            break;
                        }

                        case 3: {

                            System.out.print("Enter ISBN : ");
                            String isbn = sc.nextLine();

                            ArrayList<Book> books = BookDao.searchBooksByISBN(
                                    isbn,
                                    connection,
                                    statement);

                            library.displayBooks(books);

                            break;
                        }

                        case 4: {

                            System.out.print("Enter Author Name : ");
                            String author = sc.nextLine();

                            ArrayList<Book> books = BookDao.searchBooksByAuthor(
                                    author,
                                    connection,
                                    statement);

                            library.displayBooks(books);

                            break;
                        }

                        case 5: {

                            System.out.print("Enter Category : ");
                            String category = sc.nextLine();

                            ArrayList<Book> books = BookDao.searchBooksByCategory(
                                    category,
                                    connection,
                                    statement);

                            library.displayBooks(books);

                            break;
                        }

                        default:
                            System.out.println("Invalid Choice.");
                    }

                    break;
                }

                case 3: {

                    System.out.println("\n===== MY BORROWED BOOKS =====");

                    TransactionDao.viewBorrowedBooks(
                            currentUser.getMemberId(),
                            connection,
                            statement,
                            currentUser);

                    break;
                }

                case 4: {

                    TransactionDao.viewBorrowHistory(
                            currentUser.getMemberId(),
                            connection,
                            statement,
                            currentUser);

                    break;
                }

                case 5: {

                    TransactionDao.calculateFine(
                            currentUser.getMemberId(),
                            connection,
                            statement,
                            currentUser);

                    break;
                }

                case 0: {

                    System.out.println("Logging out...");
                    return;
                }

                default:
                    System.out.println("Invalid Choice.");
            }
        }
    }
}