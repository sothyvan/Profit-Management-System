import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    //Fields
    String transactionID;
    String transactionDate;
    double amount;
    TransactionType type;
    LocalDateTime createdAt;

    static int counter = 0;
    static String curDate = "";

    //Constructor
    public Transaction(String transactionDate, double amount, TransactionType type){
        if (type == null){
            throw new IllegalArgumentException("Transaction type cannot be null!");
        }
        this.transactionID = generateTransactionId();
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    private String generateTransactionId(){
        String today = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("ddMMyyyy")
        );

        // Reset counter if it's a new day
        if (!today.equals(curDate)) {
            curDate = today;
            counter = 0;
        }
        return "T-" + today + "-" + ++counter; 
    }

    @Override
    public String toString(){
        return String.format(
            "Transaction ID: %s,\n Amount: $%.2f,\n Sale Date: %s,\n Entered in System: %s,\n Type: %s\n",
            transactionID, amount, transactionDate, createdAt.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")),type 
        );
    }
}
