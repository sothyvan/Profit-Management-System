public class Transaction {
    String id;
    String date;
    Customer customer;
    Product product;
    double quantity;
    double unitPrice;
    
    // REGULAR FIELDS
    double revenue;
    Cost[] costs;
    int costCount;
    
    // SNAPSHOT FIELDS
    double snapshotRevenue;      
    double snapshotProfit;       
    double snapshotMargin;       
    boolean snapshotTaken;       
    
    Transaction(String id, String date, Customer customer, Product product, double quantity, double unitPrice) {
        this.id = id;
        this.date = date;
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.revenue = calculateRevenue(quantity,unitPrice);
        this.costs = new Cost[10];
        this.costCount = 0;
        this.snapshotTaken = false;
    }
    
    // Method to take snapshot
    void takeSnapshot() {
        this.snapshotRevenue = this.revenue;           
        this.snapshotProfit = this.calculateProfit();    
        this.snapshotMargin = this.calculateMargin();  
        this.snapshotTaken = true;
    }
    
    void addCost(Cost cost) {
        if (costCount >= costs.length) {
            Cost[] newArr = new Cost[costCount * 2];
            // Copy the old data to the new array
            for (int i = 0; i < costs.length; i++){
                newArr[i] = costs[i];
            }
            costs = newArr;
        }
        costs[costCount] = cost;
        costCount++;
    }

    double calculateRevenue(double quantity, double unitPrice){
        return quantity * unitPrice;
    }
    
    double calculateTotalCost() {
        double total = 0;
        for (int i = 0; i < costCount; i++) {
            total += costs[i].amount;
        }
        return total;
    }
    
    double calculateProfit() {
        return revenue - calculateTotalCost();
    }
    
    double calculateMargin() {
        if (revenue == 0) return 0;
        return (calculateProfit() / revenue) * 100;
    }
    
    // Method to print out the Transaction
    void printDetails() {
        System.out.println("Transaction: " + id + " - " + date);
        System.out.println("Customer: " + customer.name);
        System.out.println("Product: " + product.name);
        System.out.println("Quantity: " + quantity + " " + product.unit);
        System.out.println("Unit Price: $" + unitPrice);
        System.out.println("Revenue: $" + revenue);
        
        if (costCount > 0) {
            System.out.println("Costs:");
            for (int i = 0; i < costCount; i++) {
                costs[i].printDetails();
            }
        }
        
        System.out.println("Total Cost: $" + calculateTotalCost());
        System.out.println("Profit: $" + calculateProfit());
        System.out.println("Margin: " + calculateMargin() + "%");
    }
    
    // Method to show snapshot
    void printSnapshot() {
        if (snapshotTaken) {
            System.out.println("=== Transaction Snapshot (Frozen Values) ===");
            System.out.println("Snapshot Revenue: $" + snapshotRevenue);
            System.out.println("Snapshot Profit: $" + snapshotProfit);
            System.out.println("Snapshot Margin: " + snapshotMargin + "%");
            System.out.println();
            System.out.println("Current Revenue: $" + revenue);
            System.out.println("Current Profit: $" + calculateProfit());
            System.out.println("Current Margin: " + calculateMargin() + "%");
        } else {
            System.out.println("No snapshot taken yet.");
        }
    }
}
