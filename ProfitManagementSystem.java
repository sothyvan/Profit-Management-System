public class ProfitManagementSystem {
    public static void main(String[] args) {
        System.out.println("=== PROFIT MANAGEMENT SYSTEM ===\n");
        
        // F1: Primitive copy demonstration
        System.out.println("F1: PRIMITIVE COPY DEMONSTRATION:");
        double originalPrice = 100.0;
        double copy = originalPrice; // Primitive copy
        copy += 50.0;
        System.out.println("Original price: $" + originalPrice);
        System.out.println("Modified copy: $" + copy);
        System.out.println("Original unchanged: " + (originalPrice == 100.0));
        System.out.println();
        
        // F2: Reference copy demonstration (using setter/getter)
        System.out.println("F2: REFERENCE COPY DEMONSTRATION (using setter/getter):");
        Customer customer = new Customer("John", "Doe", "555-1234");
        Customer customerReference = customer; // Reference copy
        
        // Use setter to modify
        customer.setFirstName("Jonathan");
        customer.setLastName("Smith");
        
        // Use getter to access
        System.out.println("Customer name via original: " + customer.getName());
        System.out.println("Customer name via reference: " + customerReference.getName());
        System.out.println("Both show same change: " + customer.getName().equals(customerReference.getName()));
        System.out.println();
        
        // Initialize database
        BusinessDatabase db = new BusinessDatabase();
        
        // Create sample data
        createSampleData(db);
        
        // F3: Array stores references demonstration
        System.out.println("F3: ARRAY STORES REFERENCES DEMONSTRATION:");
        System.out.println("Before modification:");
        Customer[] customers = db.getCustomers();
        if (customers.length > 0) {
            System.out.println("First customer: " + customers[0].getName());
            
            // Get reference from array
            Customer customerInArray = customers[0];
            
            // Modify using setter
            customerInArray.setLastName("Modified");
            
            System.out.println("\nAfter modification through reference:");
            System.out.println("First customer in array: " + customers[0].getName());
            System.out.println("Array reflects changes: " + customers[0].getLastName().equals("Modified"));
        }
        System.out.println();
        
        // F4: Snapshot behavior demonstration
        System.out.println("F4: SNAPSHOT BEHAVIOR DEMONSTRATION:");
        
        // Get transactions from database
        Transaction[] transactions = db.getTransactions();
        if (transactions.length > 0) {
            Transaction transaction = transactions[0];
            
            System.out.println("1. Original transaction state:");
            System.out.println("   Transaction ID: " + transaction.getId());
            System.out.println("   Date: " + transaction.getDate());
            System.out.println("   Quantity: " + transaction.getQuantity());
            System.out.println("   Product: " + transaction.getProduct().getName());
            
            // TAKE SNAPSHOT 
            transaction.takeSnapshot();
            
            System.out.println("\n2. Snapshot taken");
            
            // MODIFY THE ORIGINAL TRANSACTION using setter
            transaction.setQuantity(200); // Change quantity using setter
            
            System.out.println("\n3. After modifying original transaction:");
            System.out.println("   New Quantity: " + transaction.getQuantity());
            System.out.println("   Original quantity in snapshot should remain unchanged.");
            
            System.out.println("\n4. Snapshot vs Current:");
            transaction.printSnapshot();
        }
        
        System.out.println("\n=== ADDITIONAL DEMONSTRATIONS ===");
        
        // Demonstrate static method calls using ClassName.method()
        System.out.println("\nStatic Method Demonstrations:");
        System.out.println("1. Transaction tax rate: " + Transaction.getTaxRate());
        Transaction.setTaxRate(0.15); // Change tax rate
        System.out.println("2. Updated tax rate: " + Transaction.getTaxRate());
        
        // Demonstrate null safety
        System.out.println("\nNull Safety Demonstration:");
        Customer notFound = db.findCustomerByName("NonExistent");
        if (notFound == null) {
            System.out.println("✓ Search returns null for non-existent customer");
        }
        
        // Check null before using
        if (notFound != null) {
            System.out.println("Customer found: " + notFound.getName());
        } else {
            System.out.println("✓ Properly checked null before using result");
        }
        
        // Calculate and display summary
        System.out.println("\n=== SYSTEM SUMMARY ===");
        System.out.println("Total Customers: " + db.getCustomerCount());
        System.out.println("Total Products: " + db.getProductCount());
        System.out.println("Total Transactions: " + db.getTransactionCount());
        System.out.println("Total Revenue: $" + String.format("%.2f", db.calculateTotalRevenue()));
        System.out.println("Total Profit: $" + String.format("%.2f", db.calculateTotalProfit()));
        
        System.out.println("\n=== SAMPLE DATA DETAILS ===");
        System.out.println("Customers:");
        for (Customer c : db.getCustomers()) {
            System.out.println("  - " + c.getName() + " (ID: " + c.getId() + ")");
        }
        
        System.out.println("\nProducts:");
        for (Product p : db.getProducts()) {
            System.out.println("  - " + p.getName() + " - $" + p.getPrice() + 
                             " per " + p.getUnit() + " (Cost: $" + p.getProductionCost() + ")");
        }
        
        System.out.println("\nTransactions:");
        for (Transaction t : db.getTransactions()) {
            System.out.println("  - " + t.getId() + ": " + t.getQuantity() + " x " + 
                             t.getProduct().getName() + " for " + t.getCustomer().getName());
        }
        
        System.out.println("\n✓ All Week 4 requirements demonstrated successfully!");
    }
    
    static void createSampleData(BusinessDatabase db) {
        // Create customers using updated constructor
        Customer cust1 = new Customer("Alice", "Johnson", "555-1001");
        Customer cust2 = new Customer("Bob", "Williams", "555-1002");
        Customer cust3 = new Customer("Charlie", "Brown", "555-1003");
        
        db.addCustomer(cust1);
        db.addCustomer(cust2);
        db.addCustomer(cust3);
        
        // Create products with production costs
        Product prod1 = new Product("Apples", "kg", 4.0);
        prod1.setProductionCost(2.5); // Set production cost
        
        Product prod2 = new Product("Shoes", "pair", 120.0);
        prod2.setProductionCost(50.0);
        
        Product prod3 = new Product("Software", "license", 299.0);
        prod3.setProductionCost(30.0);
        
        db.addProduct(prod1);
        db.addProduct(prod2);
        db.addProduct(prod3);
        
        // Create transactions with updated constructor
        Transaction tx1 = new Transaction("2024-01-10", cust1, prod1, 100);
        // Add additional costs to transaction
        Cost shippingCost = new Cost(50.0, tx1);
        tx1.addCost(shippingCost);
        
        Transaction tx2 = new Transaction("2024-01-12", cust3, prod2, 40);
        Cost packagingCost = new Cost(20.0, tx2);
        tx2.addCost(packagingCost);
        
        Transaction tx3 = new Transaction("2024-01-15", cust2, prod3, 10);
        Cost licenseCost = new Cost(100.0, tx3);
        tx3.addCost(licenseCost);
        
        db.addTransaction(tx1);
        db.addTransaction(tx2);
        db.addTransaction(tx3);
    }
}