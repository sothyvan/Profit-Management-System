public class Customer {
    String id;
    String name;
    
    Customer(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    void printDetails() {
        System.out.println("Customer: " + name + " (ID: " + id + ")");
    }
}
