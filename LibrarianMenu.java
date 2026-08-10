import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class LibrarianMenu {
    public static void show(
            Connection connection,
            Statement statement,
            Scanner sc,
            Library library,
            Member currentUser) {

        while (true) {

            System.out.println("\n===== LIBRARIAN MENU =====");

            System.out.println("1. View All Books");
            System.out.println("2. Search Book");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("5. View All Members");
            System.out.println("6. View Transactions");
            System.out.println("0. Logout");

            System.out.print("Enter Choice : ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1: {
                    BookDao.viewAllBooks(connection, statement);
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

                    System.out.print("Enter Book ID : ");
                    int bookId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Member ID : ");
                    int memberId = sc.nextInt();
                    sc.nextLine();

                    LocalDate issueDate = LocalDate.now();

                    String message = TransactionDao.issueBook(
                            bookId,
                            memberId,
                            issueDate,
                            connection,
                            statement);

                    System.out.println(message);

                    break;
                }

                case 4: {

                    System.out.print("Enter Book ID : ");
                    int bookId = sc.nextInt();
                    sc.nextLine();

                    LocalDate returnDate = LocalDate.now();

                    String message = TransactionDao.returnBook(
                            bookId,
                            returnDate,
                            connection,
                            statement);

                    System.out.println(message);

                    break;
                }

                case 5: {

                    boolean result = library.viewAllMembers(
                            connection,
                            statement);

                    System.out.println(result);

                    break;
                }

                case 6: {

                    TransactionDao.viewAllTransactions(
                            connection,
                            statement);

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