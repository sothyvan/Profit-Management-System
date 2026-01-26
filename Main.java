public class Main {
    public static void main(String[] args){
        Transaction t = new Transaction( null, 0, TransactionType.EXPENSE);
        System.out.println(t);
        Transaction i = new Transaction( null, 0, TransactionType.REVENUE);
        System.out.println(i);
    }
}
