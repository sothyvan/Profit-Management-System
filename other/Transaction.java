package other;

import java.util.Arrays;

import user.IStaff;

public class Transaction {
    private static double taxRate = 0.10;
    
    private String id;
    private String date;
    private Customer customer;
    private IStaff staff;
    private Product[] products;        
    private double[] quantities;       
    private int productCount;          
    private Cost[] costs;
    private int costCount;
    private static int nextId = 1;
    
    // SNAPSHOT FIELDS (primitive types)     
    private boolean snapshotTaken;
    private String snapshotDate = null;
    private Customer snapshotCustomer = null;
    private Product[] snapshotProducts;
    private double[] snapshotQuantities;
    private int snapshotProductCount;
    private Cost[] snapshotCosts;
    private int snapshotCostCount;
    
    public Transaction(String date, Customer customer, IStaff staff, Product product, double quantity) {
        this.id = "TRANS-" + nextId;
        nextId++;
        this.date = date;
        this.customer = customer;
        this.staff = staff;
        this.products = new Product[10];
        this.quantities = new double[10];
        this.productCount = 0;
        this.costs = new Cost[10];
        this.costCount = 0;
        this.snapshotTaken = false;
        this.snapshotProducts = new Product[10];
        this.snapshotQuantities = new double[10];
        this.snapshotCosts = new Cost[10];
        
        // Add the first product
        addProduct(product, quantity);
    }
    
    public Transaction(String date, Customer customer, IStaff staff) {
        this.id = "TRANS-" + nextId;
        nextId++;
        this.date = date;
        this.customer = customer;
        this.staff = staff;
        this.products = new Product[10];
        this.quantities = new double[10];
        this.productCount = 0;
        this.costs = new Cost[10];
        this.costCount = 0;
        this.snapshotTaken = false;
        this.snapshotProducts = new Product[10];
        this.snapshotQuantities = new double[10];
        this.snapshotCosts = new Cost[10];
    }

    public Transaction(String date, Customer customer, Product product, double quantity) {
        this(date, customer, null, product, quantity);
    }
    
    public Transaction(String date, Customer customer) {
        this(date, customer, null);
    }
    
    // ========== GETTERS ==========
    public String getId() {
        return id;
    }
    
    public String getDate() {
        return date;
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public IStaff getStaff() {
        return staff;
    }
    
    public Product[] getProducts() {
        return Arrays.copyOf(products, productCount);
    }
    
    public double[] getQuantities() {
        return Arrays.copyOf(quantities, productCount);
    }
    
    public int getProductCount() {
        return productCount;
    }
    
    public int getCostCount() {
        return costCount;
    }
    
    public Cost[] getCosts() {
        return Arrays.copyOf(costs, costCount);
    }
    
    // ========== SETTERS ==========
    public void setDate(String date) {
        if (date != null ) {
            this.date = date;
        }
    }

    public void setStaff(IStaff staff) {
        this.staff = staff;
    }

    // ========== STATIC METHODS ==========
    public static double getTaxRate() {
        return taxRate;
    }
    
    public static void setTaxRate(double newTaxRate) {
        if (newTaxRate >= 0 && newTaxRate <= 0.30) {
            taxRate = newTaxRate;
        } else {
            System.out.println("Error: Tax rate must be between 0 and 0.30");
        }
    }
    
    // ========== SNAPSHOT METHODS ==========
    public void takeSnapshot() {
        this.snapshotTaken = true;
        this.snapshotDate = this.date;
        this.snapshotCustomer = this.customer;
        this.snapshotProductCount = this.productCount;
        this.snapshotProducts = Arrays.copyOf(this.products, this.productCount);
        this.snapshotQuantities = Arrays.copyOf(this.quantities, this.productCount);
        this.snapshotCostCount = this.costCount;
        this.snapshotCosts = Arrays.copyOf(this.costs, this.costCount);
    }
    
    public void printSnapshot() {
        if (snapshotTaken) {
            System.out.println("=== Transaction Snapshot ===");
            System.out.println("Transaction ID: " + id);
            System.out.println("Date: " + snapshotDate);
            System.out.println("Customer: " + (snapshotCustomer != null ? snapshotCustomer.getName() : "null"));
            System.out.println("Handled by: " + (staff.getPosition()));
            System.out.println("Products in Snapshot (" + snapshotProductCount + "):");
            for (int i = 0; i < snapshotProductCount; i++) {
                System.out.println("  - " + snapshotQuantities[i] + " x " + snapshotProducts[i].getName() + " @ $" + snapshotProducts[i].getPrice());
            }
            System.out.println("Additional Costs in Snapshot (" + snapshotCostCount + "):");
            for (int i = 0; i < snapshotCostCount; i++) {
                System.out.println("  - " + snapshotCosts[i]);
            }
        } else {
            System.out.println("No snapshot taken yet for transaction " + id);
        }
    }
    
    // ========== PRODUCT MANAGEMENT ==========
    public void addProduct(Product product, double quantity) {
        if (product == null || quantity <= 0) {
            return;
        }
        if (productCount >= products.length) {
            products = Arrays.copyOf(products, productCount * 2);
            quantities = Arrays.copyOf(quantities, productCount * 2);
        }
        products[productCount] = product;
        quantities[productCount] = quantity;
        productCount++;
    }
    
    public boolean removeProduct(Product product) {
        for (int i = 0; i < productCount; i++) {
            if (products[i].equals(product)) {
                for (int j = i; j < productCount - 1; j++) {
                    products[j] = products[j + 1];
                    quantities[j] = quantities[j + 1];
                }
                products[productCount - 1] = null;
                quantities[productCount - 1] = 0;
                productCount--;
                return true;
            }
        }
        return false;
    }
    
    public boolean updateProductQuantity(Product product, double newQuantity) {
        for (int i = 0; i < productCount; i++) {
            if (products[i].equals(product)) {
                if (newQuantity > 0) {
                    quantities[i] = newQuantity;
                    return true;
                }
            }
        }
        return false;
    }
    
    // ========== COST MANAGEMENT ==========
    public void addCost(Cost cost) {
        if (cost == null) {
            return;
        }
        if (costCount >= costs.length) {
            costs = Arrays.copyOf(costs, costCount * 2);
        }
        if (cost.getTransaction() == null) {
            cost.setTransaction(this);
        }
        costs[costCount] = cost;
        costCount++;
    }
    
    public boolean removeCost(String costId) {
        for (int i = 0; i < costCount; i++) {
            if (costs[i].getId().equals(costId)) {
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
    
    @Override
    public String toString() {
        return "Transaction [id=" + id + ", date=" + date + ", customer=" + customer + ", staff="
                + (staff != null ? staff.getUsername() : "unassigned") + ", products="
                + Arrays.toString(products) + ", quantities=" + Arrays.toString(quantities) + ", productCount="
                + productCount + ", costs=" + Arrays.toString(costs) + ", costCount=" + costCount + ", snapshotTaken="
                + snapshotTaken + "]";
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
        return true;
    }

}
