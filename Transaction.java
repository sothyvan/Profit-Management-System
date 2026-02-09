import java.util.Arrays;

public class Transaction {
    // Static variable
    private static double taxRate = 0.10;
    
    // Instance variables
    private String id;
    private String date;
    private Customer customer;
    private Product product;
    private double quantity;
    private Cost[] costs;
    private int costCount;
    private static int nextId = 1;
    
    // SNAPSHOT FIELDS     
    private boolean snapshotTaken;
    private String snapshotDate = null;
    private Customer snapshotCustomer = null;
    private Product snapshotProduct = null;    
    private double snapshotQuantity = 0;
    private Cost[] snapshotCosts;
    private int snapshotCostCount;
    
    // Constructor
    public Transaction(String date, Customer customer, Product product, double quantity) {
        id = "TRANS-" + nextId;
        nextId++;
        this.date = date;
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.costs = new Cost[10];
        this.costCount = 0;
        this.snapshotTaken = false;
        this.snapshotCosts = new Cost[10];
        this.snapshotCostCount = 0;
    }
    
    // GETTERS
    public String getId() {
        return id;
    }
    
    public String getDate() {
        return date;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public double getQuantity() {
        return quantity;
    }
    
    public int getCostCount() {
        return costCount;
    }
    
    // Get defensive copy of costs array
    public Cost[] getCosts() {
        return Arrays.copyOf(costs, costCount);
    }
    
    // SETTERS with validation
    public void setQuantity(double quantity) {
        if (quantity > 0) {
            this.quantity = quantity;
        } else {
            System.out.println("Error: Quantity must be positive");
        }
    }
    
    public void setDate(String date) {
        if (date != null && !date.trim().isEmpty()) {
            this.date = date;
        }
    }
    
    // Static method to access static variable (Week 4 requirement)
    public static double getTaxRate() {
        return taxRate;
    }
    
    // Static method to set tax rate with validation
    public static void setTaxRate(double newTaxRate) {
        if (newTaxRate >= 0 && newTaxRate <= 0.30) { // Max 30% tax
            taxRate = newTaxRate;
        } else {
            System.out.println("Error: Tax rate must be between 0 and 0.30 (30%)");
        }
    }
    
    // Method to take snapshot (Week 4 requirement)
    public void takeSnapshot() {
        this.snapshotTaken = true;
        this.snapshotDate = this.date;
        this.snapshotCustomer = this.customer;
        this.snapshotProduct = this.product;
        this.snapshotQuantity = this.quantity;
        this.snapshotCostCount = this.costCount;
        this.snapshotCosts = Arrays.copyOf(this.costs, this.costCount);
        System.out.println("Snapshot taken for transaction " + id);
    }
    
    // Add cost to transaction (array + counter - Week 4 requirement)
    public void addCost(Cost cost) {
        if (costCount >= costs.length) {
            // Resize array
            costs = Arrays.copyOf(costs, costCount * 2);
        }
        costs[costCount] = cost;
        costCount++;
    }
    
    // Remove cost by ID
    public boolean removeCost(String costId) {
        for (int i = 0; i < costCount; i++) {
            if (costs[i].getId().equals(costId)) {
                // Shift remaining elements
                for (int j = i; j < costCount - 1; j++) {
                    costs[j] = costs[j + 1];
                }
                costs[costCount - 1] = null;
                costCount--;
                return true;
            }
        }
        return false;
    }
    
    public double calculateTotalCost() {
        double total = 0;
        for (int i = 0; i < costCount; i++) {
            total += costs[i].getAmount();
        }
        // Add product production cost if it exists
        if (product != null && product.getProductionCost() != 0) {
            total += quantity * product.getProductionCost();
        }
        return total;
    }
    
    // Method to show snapshot (Week 4 requirement)
    public void printSnapshot() {
        if (snapshotTaken) {
            System.out.println("=== Transaction Snapshot ===");
            System.out.println("Transaction ID: " + id);
            System.out.println("Date: " + snapshotDate);
            System.out.println("Customer: " + (snapshotCustomer != null ? snapshotCustomer.getName() : "null"));
            System.out.println("Product: " + (snapshotProduct != null ? snapshotProduct.getName() : "null"));
            System.out.println("Quantity: " + snapshotQuantity);
            System.out.println("Costs:");
            for (int i = 0; i < snapshotCostCount; i++) {
                System.out.println(" - " + snapshotCosts[i]);
            }
        } else {
            System.out.println("No snapshot taken yet for transaction " + id);
        }
    }
    
    @Override
    public String toString() {
        return "Transaction [id = " + id + 
                ", date = " + date + 
                ", customer = " + (customer != null ? customer.getName() : "null") + 
                ", product = " + (product != null ? product.getName() : "null") + 
                ", quantity = " + quantity + 
                ", costCount = " + costCount + 
                ", snapshotTaken = " + snapshotTaken + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Transaction other = (Transaction) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (customer == null) {
            if (other.customer != null)
                return false;
        } else if (!customer.equals(other.customer))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (Double.doubleToLongBits(quantity) != Double.doubleToLongBits(other.quantity))
            return false;
        if (!Arrays.equals(costs, other.costs))
            return false;
        if (costCount != other.costCount)
            return false;
        if (snapshotTaken != other.snapshotTaken)
            return false;
        return true;
    }
}