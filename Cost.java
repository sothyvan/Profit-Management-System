public class Cost {        
    private double amount;
    private Product product;        
    private Transaction transaction;
    private static int nextId = 1;  
    private String id;
    
    // Constructor for Product-related costs
    public Cost(double amount, Product product) {
        this.id = "COST-" + nextId;
        nextId++;
        this.amount = amount;
        this.product = product;
        this.transaction = null;  // Not linked to transaction yet
    }
    
    // Constructor for Transaction-related costs
    public Cost(double amount, Transaction transaction) {
        this.id = "COST-" + nextId;
        nextId++;
        this.amount = amount;
        this.transaction = transaction;
        this.product = null;  // Not linked to product
    }
    
    // Constructor for costs linked to both
    public Cost(double amount, Product product, Transaction transaction) {
        this.id = "COST-" + nextId;
        nextId++;
        this.amount = amount;
        this.product = product;
        this.transaction = transaction;
    }
    
    // GETTERS
    public String getId() {
        return id;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
    
    // SETTER with validation (Week 4 requirement)
    public void setAmount(double amount) {
        if (amount >= 0) {
            this.amount = amount;
        } else {
            System.out.println("Error: Cost amount cannot be negative");
        }
    }
    
    // Setter to link to a product
    public void setProduct(Product product) {
        this.product = product;
    }
    
    // Setter to link to a transaction
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Cost other = (Cost) obj;
        if (Double.doubleToLongBits(amount) != Double.doubleToLongBits(other.amount))
            return false;
        if (product == null) {
            if (other.product != null)
                return false;
        } else if (!product.equals(other.product))
            return false;
        if (transaction == null) {
            if (other.transaction != null)
                return false;
        } else if (!transaction.equals(other.transaction))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    // toString() showing connections
    @Override
    public String toString() {        
        String productInfo = (product != null) ? product.getName() : "No Product";
        String transactionInfo = (transaction != null) ? transaction.getId() : "No Transaction";
        return "Cost [id = " + id + 
                ", amount = $" + amount + 
                ", product = " + productInfo + 
                ", transaction = " + transactionInfo + "]";
        }
    
    // Helper method to check if cost is product-related
    public boolean isProductCost() {
        return product != null;
    }
    
    // Helper method to check if cost is transaction-related
    public boolean isTransactionCost() {
        return transaction != null;
    }
}