import java.time.LocalDate;

public class Transaction {

    // Attributes
    private int transactionId;
    private int bookId;
    private int memberId;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean returned;

    // Constructor
    public Transaction(int bookId, int memberId, LocalDate issueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;

        // Book is just issued
        this.returnDate = null;
        this.returned = false;
    }

    // Getters

    public int getTransactionId() {
        return transactionId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    // Setters
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    // Display Method

    public void displayTransaction() {

        System.out.println("----------------------------");
        System.out.println("Transaction ID : " + transactionId);
        System.out.println("Book ID        : " + bookId);
        System.out.println("Member ID      : " + memberId);
        System.out.println("Issue Date     : " + issueDate);
        System.out.println("Return Date    : "
                + (returnDate == null ? "Not Returned" : returnDate));
        System.out.println("Status         : "
                + (returned ? "Returned" : "Issued"));
        System.out.println("----------------------------");
    }
}