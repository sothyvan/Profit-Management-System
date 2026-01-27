public class Main {
    public static void main(String[] args){
        Transaction t = new Transaction("12-12-2025",0, TransactionType.REVENUE);
        System.out.println(t);
        Transaction j = new Transaction( "12-12-1212", 5000, TransactionType.REVENUE);
        System.out.println(j);
    }
}
