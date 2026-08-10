import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class AdminMenu {
    public static void show(Connection connection,
            Statement statement,
            Scanner sc,
            Library library,
            Member currentUser) {
        while (true) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");

            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. View All Books");
            System.out.println("4. Search Book");
            System.out.println("5. Member Management");
            System.out.println("6. Issue Book");
            System.out.println("7. Return Book");
            System.out.println("8. View Transactions");
            System.out.println("9. Book Statistics");
            System.out.println("0. Exit");

            System.out.print("Enter Choice : ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1: {
                    System.out.print("Enter Book ID : ");
                    int bookId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Title : ");
                    String title = sc.nextLine();

                    System.out.print("Enter Author : ");
                    String author = sc.nextLine();

                    System.out.print("Enter ISBN : ");
                    String isbn = sc.nextLine();

                    System.out.print("Enter Category : ");
                    String category = sc.nextLine();

                    System.out.print("Enter Publication Year : ");
                    int year = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Shelf Location : ");
                    String shelf = sc.nextLine();

                    Book book = new Book(bookId, title, author, isbn, category, year, shelf);

                    // if (library.addBook(book)) {
                    // System.out.println("Book Added Successfully.");
                    // } else {
                    // System.out.println("Book ID Already Exists.");
                    // }
                    boolean result = BookDao.saveToDatabase(book, connection, statement, currentUser);
                    System.out.println(result);
                    break;
                }
                case 2: {
                    System.out.print("Enter Book ID : ");
                    int bookId = sc.nextInt();

                    // if (library.removeBook(bookId)) {
                    // System.out.println("Book Removed Successfully. ");
                    // } else {
                    // System.out.println("Book Not Found.");
                    // }
                    boolean result = BookDao.removeBook(bookId, connection, statement, currentUser);

                    if (result) {
                        System.out.println("Book Removed Successfully. ");
                    } else {
                        System.out.println("Book Not Found.");
                    }
                    break;
                }
                case 3: {
                    // library.viewAllBooks();
                    BookDao.viewAllBooks(connection, statement);
                    break;
                }
                case 4: {

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
                            Book book = BookDao.searchBook(bookId, connection, statement);
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
                            ArrayList<Book> books = BookDao.searchBooksByTitle(title, connection, statement);
                            library.displayBooks(books);
                            break;
                        }
                        case 3: {
                            System.out.print("Enter ISBN : ");
                            String isbn = sc.nextLine();
                            ArrayList<Book> books = BookDao.searchBooksByISBN(isbn, connection, statement);
                            library.displayBooks(books);
                            break;
                        }
                        case 4: {
                            System.out.print("Enter Author Name : ");
                            String author = sc.nextLine();
                            ArrayList<Book> books = BookDao.searchBooksByAuthor(author, connection, statement);
                            library.displayBooks(books);
                            break;
                        }
                        case 5: {
                            System.out.print("Enter Category : ");
                            String category = sc.nextLine();
                            ArrayList<Book> books = BookDao.searchBooksByCategory(category, connection, statement);
                            library.displayBooks(books);
                            break;
                        }
                        default:
                            System.out.println("Invalid Choice.");
                    }
                    break;
                }
                case 5: {
                    boolean memberMenu = true;

                    while (memberMenu) {

                        System.out.println("\n===== MEMBER MANAGEMENT =====");

                        System.out.println("1. Register Member");
                        System.out.println("2. View All Members");
                        System.out.println("3. Search Member");
                        System.out.println("4. Currently Borrowed Books");
                        System.out.println("5. Borrow History");
                        System.out.println("6. Calculate Fine");
                        System.out.println("0. Back");

                        System.out.print("Enter Choice : ");

                        int memberChoice = sc.nextInt();
                        sc.nextLine();

                        switch (memberChoice) {
                            case 1: {
                                System.out.print("Member ID : ");
                                int memberId = sc.nextInt();
                                sc.nextLine();

                                System.out.print("Name : ");
                                String name = sc.nextLine();

                                System.out.print("Email : ");
                                String email = sc.nextLine();

                                System.out.print("Phone : ");
                                String phone = sc.nextLine();
                                System.out.print("Username : ");
                                String username2 = sc.nextLine();
                                System.out.print("Password : ");
                                String password2 = sc.nextLine();
                                System.out.print("Role : ");
                                String role = sc.nextLine();

                                Member member2 = new Member(0, email, name, password2, phone,
                                        role.toUpperCase().equals("ADMIN") ? Role.ADMIN
                                                : role.toUpperCase().equals("LIBRARIAN") ? Role.LIBRARIAN
                                                        : Role.STUDENT,
                                        username2);

                                // Member member = new Member(
                                // memberId,
                                // name,
                                // email,
                                // phone
                                // );
                                // if (library.registerMember(member)) {
                                // System.out.println("Member Registered Successfully.");
                                // } else {
                                // System.out.println("Member ID Already Exists.");
                                // }
                                member2.saveToDatabase(connection, currentUser);
                                break;
                            }

                            case 2: {
                                boolean res = library.viewAllMembers(connection, statement);

                                System.out.println(res);
                                break;
                            }

                            case 3: {

                                System.out.print("Enter Member ID : ");

                                int memberId = sc.nextInt();

                                Member member3 = library.searchMember(memberId, connection, statement);

                                if (member3 != null) {
                                    member3.displayMember();
                                } else {
                                    System.out.println("Member Not Found.");
                                }
                                break;
                            }

                            case 4: {
                                System.out.print("Enter Member ID : ");
                                int memberId = sc.nextInt();
                                // library.viewBorrowedBooks(memberId);
                                TransactionDao.viewBorrowedBooks(memberId, connection, statement, currentUser);
                                break;
                            }

                            case 5: {
                                System.out.println("Enter Member ID : ");
                                int memberId = sc.nextInt();
                                // library.viewBorrowHistory(memberId);
                                TransactionDao.viewBorrowHistory(memberId, connection, statement, currentUser);
                                break;
                            }

                            case 6: {
                                System.out.print("Enter Member ID : ");
                                int memberId = sc.nextInt();
                                // library.calculateFine(memberId);
                                TransactionDao.calculateFine(memberId, connection, statement, currentUser);
                                break;
                            }

                            case 0: {
                                memberMenu = false;
                                break;
                            }

                            default: {
                                System.out.println("Invalid Choice.");
                            }
                        }
                    }
                    break;
                }

                case 6: {

                    System.out.print("Enter Book ID : ");
                    int bookId = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Enter Member ID : ");
                    int memberId = sc.nextInt();
                    sc.nextLine();

                    // System.out.print("Enter Issue Date (dd-mm-yyyy) : ");
                    LocalDate issueDate = LocalDate.now();

                    String message = TransactionDao.issueBook(
                            bookId,
                            memberId,
                            issueDate,
                            connection,
                            statement,
                            currentUser);

                    System.out.println(message);

                    break;
                }

                case 7: {

                    System.out.print("Enter Book ID : ");
                    int bookId = sc.nextInt();
                    sc.nextLine();

                    // System.out.print("Enter Return Date (dd-mm-yyyy) : ");
                    // String returnDate = sc.nextLine();
                    LocalDate returnDate = LocalDate.now();

                    String message = TransactionDao.returnBook(
                            bookId,
                            returnDate,
                            connection,
                            statement,
                            currentUser);

                    System.out.println(message);

                    break;
                }

                case 8: {
                    TransactionDao.viewAllTransactions(connection, statement);
                    break;
                }

                case 9: {
                    System.out.println("\n===== BOOK STATISTICS =====");
                    // library.countBooks();
                    // library.countAvailableBooks();
                    // library.countIssuedBooks();
                    System.out.println("Total Books : " + BookDao.countBooks(connection, statement));
                    System.out.println("Available Books : " + BookDao.countAvailableBooks(connection, statement));
                    System.out.println("Issued Books : " + BookDao.countIssuedBooks(connection, statement));
                    break;
                }

                case 0:
                    System.out.println("Thank You!");
                    return;

                default:
                    System.out.println("Invalid Choice.");

            }

        }
    }
}
