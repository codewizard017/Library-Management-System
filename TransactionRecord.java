import java.time.LocalDate;

public class TransactionRecord {
    private int transactionId;
    private int bookId;
    private String bookTitle;
    private int memberId;
    private String memberName;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private boolean returned;
    private long fine;
    private long daysBorrowed;

    public TransactionRecord(int transactionId, int bookId, String bookTitle, int memberId, String memberName,
                             LocalDate issueDate, LocalDate returnDate, boolean returned, long fine, long daysBorrowed) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.memberId = memberId;
        this.memberName = memberName;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.returned = returned;
        this.fine = fine;
        this.daysBorrowed = daysBorrowed;
    }

    public int getTransactionId() { return transactionId; }
    public int getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public int getMemberId() { return memberId; }
    public String getMemberName() { return memberName; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returned; }
    public long getFine() { return fine; }
    public long getDaysBorrowed() { return daysBorrowed; }
}

