import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    String transactionID;
    String transactionDate;
    double amount;
    TransactionType type;
    static int counter = 0;
    static String curDate = "";

    public Transaction(String transactionDate, double amount, TransactionType type){
        if (type == null){
            throw new IllegalArgumentException("Transaction type cannot be null!");
        }
        this.transactionID = generateTransactionId();
        this.transactionDate = (transactionDate != null) ? transactionDate : LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.amount = amount;
        this.type = type;
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
            "Transaction ID: %s, Amount: $%.2f, Date: %s, Type: %s",
            transactionID, amount, transactionDate, type 
        );
    }
}
