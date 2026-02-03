public class ProfitManagementSystem {
    public static void main(String[] args) {
        //F1: Primitive copy
        System.out.println("F1: PRIMITIVE COPY DEMONSTRATION:");
        double originalPrice = 100.0;
        double copy = originalPrice;
        copy += 50.0;
        System.out.println("Original price: $" + originalPrice);
        System.out.println("Modified copy: $" + copy);
        System.out.println("Original unchanged: " + (originalPrice == 100.0));
        System.out.println();
        
        //F2: Reference copy
        System.out.println("F2: REFERENCE COPY DEMONSTRATION:");
        Customer customer = new Customer("CUST-001", "FreshMart");
        Customer customerReference = customer;
        
        customer.name = "FreshMart Supermarket";
        System.out.println("Customer name via original: " + customer.name);
        System.out.println("Customer name via reference: " + customerReference.name);
        System.out.println("Both show same change: " + customer.name.equals(customerReference.name));
        System.out.println();
        
        // Initialize database
        BusinessDatabase db = new BusinessDatabase();
        
        // Create sample data
        createSampleData(db);
        
        //F3: Array stores references
        System.out.println("F3: ARRAY STORES REFERENCES DEMONSTRATION:");
        System.out.println("Before modification:");
        db.customers[0].printDetails();
        
        Customer customerInArray = db.customers[0];
        customerInArray.name = "Updated FreshMart";
        
        System.out.println("\nAfter modification through reference:");
        db.customers[0].printDetails();
        System.out.println("Array reflects changes: " + db.customers[0].name.equals("Updated FreshMart"));
        System.out.println();
        
        //F4: Snapshot behavior
        System.out.println("F4: SNAPSHOT BEHAVIOR DEMONSTRATION:");
        
        // Create a transaction
        Transaction transaction = new Transaction("TX-100", "2024-01-15", db.customers[0], db.products[0], 100, 4.0);
        transaction.addCost(new Cost("C-001", "materials", 250.0, 0));
        
        System.out.println("1. Original transaction state:");
        System.out.println("   Revenue: $" + transaction.revenue);
        System.out.println("   Profit: $" + transaction.calculateProfit());
        System.out.println("   Margin: " + transaction.calculateMargin() + "%");
        
        // TAKE SNAPSHOT 
        transaction.takeSnapshot();
        
        System.out.println("\n2. Snapshot taken");
        
        // MODIFY THE ORIGINAL TRANSACTION
        transaction.unitPrice = 5.0;      // Change price
        transaction.quantity = 150;       // Change quantity
        transaction.revenue = transaction.calculateRevenue(transaction.quantity, transaction.unitPrice);
        transaction.addCost(new Cost("C-002", "extra", 50.0, 0));  // Add more cost
        
        System.out.println("\n3. After modifying original transaction:");
        System.out.println("   New Revenue: $" + transaction.revenue);
        System.out.println("   New Profit: $" + transaction.calculateProfit());
        System.out.println("   New Margin: " + transaction.calculateMargin() + "%");
        
        System.out.println("\n4. Snapshot vs Current:");
        transaction.printSnapshot();
    }
    
    static void createSampleData(BusinessDatabase db) {
        // Create customers
        Customer cust1 = new Customer("CUST-001", "FreshMart");
        Customer cust2 = new Customer("CUST-002", "Local Store");
        Customer cust3 = new Customer("CUST-003", "Tech Store");
        
        db.addCustomer(cust1);
        db.addCustomer(cust2);
        db.addCustomer(cust3);
        
        // Create products/services
        Product prod1 = new Product("PROD-001", "Apples", "kg", 3.5);
        Product prod2 = new Product("PROD-002", "Shoes", "item", 150);
        Product prod3 = new Product("PROD-003", "Software", "license", 50);
        
        db.addProduct(prod1);
        db.addProduct(prod2);
        db.addProduct(prod3);
        
        // Create initial transactions
        Transaction tx1 = new Transaction("TX-001", "2024-01-10", cust1, prod1, 100, 4.0);
        tx1.addCost(new Cost("COST-001", "purchase", 250.0, 0));
        tx1.addCost(new Cost("COST-002", "shipping", 50.0, 0));
        
        Transaction tx2 = new Transaction("TX-002", "2024-01-12", cust3, prod2, 40, 150.0);
        tx2.addCost(new Cost("COST-003", "labor", 3200.0, 0));
        
        db.addTransaction(tx1);
        db.addTransaction(tx2);
    }
}
