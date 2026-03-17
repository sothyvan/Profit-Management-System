package other;

public class Cost {        
    private static int nextId = 1;  

    private String id;
    private double amount;
    private String description;
    private Product product;        
    private Transaction transaction;
    
    public Cost(double amount, String description, Product product) {
        this.id = String.valueOf(nextId);
        nextId++;
        setAmount(amount);
        setDescription(description);
        this.product = product;
        this.transaction = null;
    }
    
    // Constructor for Product-related costs
    public Cost(double amount, Product product) {
        this(amount, "Product Cost", product);
    }
    
    // Constructor for Transaction-related costs
    public Cost(double amount, String description) {
        this(amount, description, null);
    }
    
    // GETTERS
    public String getId() {
        return id;
    }
    
    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public Transaction getTransaction() {
        return transaction;
    }
    
    // SETTER
    public void setAmount(double amount) {
        if (amount >= 0) {
            this.amount = amount;
        } else {
            System.out.println("Error: Cost amount cannot be negative");
        }
    }

    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            this.description = "General Cost";
        } else {
            this.description = description.trim();
        }
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
    @Override
    public String toString() {        
        String productInfo = (product != null) ? product.getId() : "No Product";
        String transactionInfo = (transaction != null) ? transaction.getId() : "No Transaction";
        return "Cost [id = " + id + 
                ", amount = $" + amount + 
                ", description = " + description +
                ", product = " + productInfo + 
                ", transaction = " + transactionInfo + "]";
        }
}
