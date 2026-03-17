package other;

import java.util.Arrays;

import user.Staff;

public class Transaction {
    private String id;
    private String date;
    private Customer customer;
    private Staff staff;
    private Product[] products;        
    private double[] quantities;       
    private int productCount;          
    private Cost[] costs;
    private int costCount;
    private static int nextId = 1;
    
    public Transaction(String date, Customer customer, Staff staff, Product product, double quantity) {
        this.id = String.valueOf(nextId);
        nextId++;
        this.date = date;
        this.customer = customer;
        this.staff = staff;
        this.products = new Product[10];
        this.quantities = new double[10];
        this.productCount = 0;
        this.costs = new Cost[10];
        this.costCount = 0;
        
        // Add the first product
        addProduct(product, quantity);
    }
    
    public Transaction(String date, Customer customer, Staff staff) {
        this.id = String.valueOf(nextId);
        nextId++;
        this.date = date;
        this.customer = customer;
        this.staff = staff;
        this.products = new Product[10];
        this.quantities = new double[10];
        this.productCount = 0;
        this.costs = new Cost[10];
        this.costCount = 0;
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

    public Staff getStaff() {
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

    public void setStaff(Staff staff) {
        this.staff = staff;
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
                + productCount + ", costs=" + Arrays.toString(costs) + ", costCount=" + costCount + "]";
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
