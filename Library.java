
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class Library {

    // Store all data
    private ArrayList<Book> books;
    private ArrayList<Member> members;
    private ArrayList<Transaction> transactions;

    // Constructor
    public Library() {

        books = new ArrayList<>();
        members = new ArrayList<>();
        transactions = new ArrayList<>();

    }

    public boolean addBook(Book book) {
        if (searchBook(book.getBookId()) != null) {
            return false;
        }
        books.add(book);
        return true;
    }

    public void viewAllBooks(Member m) {


        if (books.isEmpty()) {
            System.out.println("No books available.");
            return;
        }
        System.out.println("\n========== BOOK LIST ==========");
        for (Book book : books) {
            book.displayBook();
        }
    }

    public Book searchBook(int bookId) {
        for (Book book : books) {
            if (book.getBookId() == bookId) {
                return book;
            }
        }
        return null;
    }

    public boolean removeBook(int bookId) {
        Book book = searchBook(bookId);
        if (book == null) {
            return false;
        }

        books.remove(book);
        return true;
    }

    public boolean registerMember(Member member) {
        if (searchMember(member.getMemberId()) != null) {
            return false;
        }
        members.add(member);
        return true;
    }

    public boolean viewAllMembers(Connection connection, Statement statement) {
        String query = "SELECT * FROM MEMBERS";
        System.out.println(query);
        try {

            ResultSet rs = statement.executeQuery(query);

            while(rs.next())
            {
                int id = rs.getInt("member_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                int bookissued = rs.getInt("bookissued");


                System.out.printf("%d %s %s %s %d\n",id,name,phone,email, bookissued);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    // public void viewAllMembers() {
    //     if (members.isEmpty()) {
    //         System.out.println("No members found.");
    //         return;
    //     }
    //     for (Member member : members) {
    //         member.displayMember();
    //     }
    // }

    public Member searchMember(int memberId, Connection connection, Statement statement) {
        String query = "SELECT * FROM MEMBERS where member_id = "+memberId;
        System.out.println(query);
        try {

            ResultSet rs = statement.executeQuery(query);

            rs.next();
         
                int id = rs.getInt("member_id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                int bookissued = rs.getInt("bookissued");


                System.out.printf("%d %s %s %s %d\n",id,name,phone,email, bookissued);

                Member m = new Member(memberId, name, email, phone, bookissued);
            return m;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public Member searchMember(int memberId) {
        for (Member member : members) {
            if (member.getMemberId() == memberId) {
                return member;
            }
        }
        return null;
    }

    public String issueBook(int bookId, int memberId, LocalDate issueDate) {

        Book book = searchBook(bookId);

        if (book == null) {
            return "Book not found.";
        }

        Member member = searchMember(memberId);

        if (member == null) {;
            return "Member not found.";
        }

        if (book.getStatus() != BookStatus.AVAILABLE) {
            return "Book is already issued.";
        }

        if (member.getBooksIssued() >= 3) {
            return "Member has reached borrowing limit.";
        }

        book.setStatus(BookStatus.ISSUED);
        member.setBooksIssued(member.getBooksIssued() + 1);

        int transactionId = transactions.size() + 1;

        Transaction transaction
                = new Transaction(
                        bookId,
                        memberId,
                        issueDate
                );

        transactions.add(transaction);

        return "Book issued successfully.";
    }

    public String returnBook(int bookId, LocalDate returnDate) {

        Book book = searchBook(bookId);
        if (book == null) {
            return "Book not found.";
        }

        if (book.getStatus() == BookStatus.AVAILABLE) {
            return "Book is already available.";
        }

        Transaction currentTransaction = null;

        for (Transaction transaction : transactions) {
            if (transaction.getBookId() == bookId && !transaction.isReturned()) {
                currentTransaction = transaction;
                break;

            }
        }

        if (currentTransaction == null) {
            return "Transaction not found.";
        }

        currentTransaction.setReturned(true);
        currentTransaction.setReturnDate(returnDate);
        book.setStatus(BookStatus.AVAILABLE);;

        Member member = searchMember(currentTransaction.getMemberId());

        if (member != null) {
            member.setBooksIssued(member.getBooksIssued() - 1);
        }

        return "Book returned successfully";
    }

    public void viewAllTransactions() {
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        for (Transaction transaction : transactions) {
            transaction.displayTransaction();
        }
    }

    public ArrayList<Book> searchBooksByTitle(String title) {
        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                result.add(book);
            }
        }

        return result;
    }

    public ArrayList<Book> searchBooksByISBN(String isbn) {
        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getIsbn().equals(isbn)) {
                result.add(book);
            }
        }
        return result;
    }

    public ArrayList<Book> searchBooksByAuthor(String author) {

        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                result.add(book);
            }
        }
        return result;
    }

    public ArrayList<Book> searchBooksByCategory(String category) {
        ArrayList<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getCategory().equalsIgnoreCase(category)) {
                result.add(book);
            }
        }
        return result;
    }

    public void countBooks() {
        System.out.println("Total Books : " + books.size());
    }

    public void countAvailableBooks() {

        int count = 0;
        for (Book book : books) {
            if (book.getStatus() == BookStatus.AVAILABLE) {
                count++;
            }
        }

        System.out.println("Available Books : " + count);
    }

    public void countIssuedBooks() {
        int count = 0;
        for (Book book : books) {
            if (book.getStatus() != BookStatus.AVAILABLE) {
                count++;
            }
        }
        System.out.println("Issued Books :" + count);
    }

    public void displayBooks(ArrayList<Book> books) {

        if (books.isEmpty()) {
            System.out.println("No Books Found.");
            return;
        }

        for (Book book : books) {
            book.displayBook();
        }
    }

    public void viewBorrowedBooks(int memberId) {
        Member member = searchMember(memberId);
        if (member == null) {
            System.out.println("Member Not Found.");
            return;
        }
        System.out.println("\nBorrowed Books");
        System.out.println("--------------------");
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (transaction.getMemberId() == memberId && !transaction.isReturned()) {
                Book book = searchBook(transaction.getBookId());
                if (book != null) {
                    book.displayBook();
                    found = true;
                }
            }
        }
        if (!found) {
            System.out.println("No borrowed books.");
        }
    }

    public void viewBorrowHistory(int memberId) {
        Member member = searchMember(memberId);
        if (member == null) {
            System.out.println("Member Not Found.");
            return;
        }

        boolean found = false;

        System.out.println("\n===== BORROW HISTORY =====");

        for (Transaction transaction : transactions) {
            if (transaction.getMemberId() == memberId) {
                Book book = searchBook(transaction.getBookId());

                if (book != null) {
                    book.displayBook();
                    System.out.println("Issue Date : " + transaction.getIssueDate());

                    System.out.println("Return Date : " + (transaction.getReturnDate() == null ? "Not Returned" : transaction.getReturnDate()));

                    System.out.println("Status : " + (transaction.isReturned() ? "Returned" : "Issued"));

                    System.out.println("---------------------");

                    found = true;
                }
            }
        }

        if (!found) {

            System.out.println("No Borrow History.");
        }
    }

    public void calculateFine(int memberId) {
        Member member = searchMember(memberId);
        if (member == null) {
            System.out.println("Member Not Found.");
            return;
        }

        boolean found = false;

        System.out.println("\n===== FINE DETAILS =====");

        for (Transaction transaction : transactions) {
            if (transaction.getMemberId() == memberId && transaction.isReturned()) {

                Book book = searchBook(transaction.getBookId());

                long days = ChronoUnit.DAYS.between(transaction.getIssueDate(), transaction.getReturnDate());

                long fine = 0;
                if (days > 14) {
                    fine = (days - 14) * 5;
                }

                System.out.println("Book : " + book.getTitle());

                System.out.println("Days Borrowed : " + days);

                System.out.println("Fine : ₹" + fine);

                System.out.println("----------------------");

                found = true;

            }

        }

        if (!found) {
            System.out.println("No completed transactions.");
        }

    }
}
