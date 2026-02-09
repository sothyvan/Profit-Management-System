import java.util.Arrays;

public class BusinessDatabase {
    // Private fields (Week 4 requirement)
    private Customer[] customers;
    private Product[] products;
    private Transaction[] transactions;
    
    private int customerCount;
    private int productCount;
    private int transactionCount;
    
    public BusinessDatabase() {
        customers = new Customer[10];
        products = new Product[10];
        transactions = new Transaction[10];
        customerCount = 0;
        productCount = 0;
        transactionCount = 0;
    }
    
    // GETTERS
    public int getCustomerCount() {
        return customerCount;
    }
    
    public int getProductCount() {
        return productCount;
    }
    
    public int getTransactionCount() {
        return transactionCount;
    }
    
    // Get defensive copies of arrays
    public Customer[] getCustomers() {
        return Arrays.copyOf(customers, customerCount);
    }
    
    public Product[] getProducts() {
        return Arrays.copyOf(products, productCount);
    }
    
    public Transaction[] getTransactions() {
        return Arrays.copyOf(transactions, transactionCount);
    }
    
    // ADD methods with array resizing
    public void addCustomer(Customer customer) {
        if (customerCount >= customers.length) {
            customers = Arrays.copyOf(customers, customerCount * 2);
        }
        customers[customerCount] = customer;
        customerCount++;
    }
    
    public void addProduct(Product product) {
        if (productCount >= products.length) {
            products = Arrays.copyOf(products, productCount * 2);
        }
        products[productCount] = product;
        productCount++;
    }
    
    public void addTransaction(Transaction transaction) {
        if (transactionCount >= transactions.length) {
            transactions = Arrays.copyOf(transactions, transactionCount * 2);
        }
        transactions[transactionCount] = transaction;
        transactionCount++;
    }
    
    // Search methods with null safety (Week 4 requirement)
    public Customer findCustomerByName(String name) {
        for (int i = 0; i < customerCount; i++) {
            // Use .equals() for String comparison (Week 4 requirement)
            if (customers[i].getName().equals(name)) {
                return customers[i];
            }
        }
        return null; // Return null if not found
    }
    
    public Customer findCustomerById(String id) {
        for (int i = 0; i < customerCount; i++) {
            if (customers[i].getId().equals(id)) {
                return customers[i];
            }
        }
        return null;
    }
    
    public Product findProductByName(String name) {
        for (int i = 0; i < productCount; i++) {
            if (products[i].getName().equals(name)) {
                return products[i];
            }
        }
        return null;
    }
    
    public Product findProductById(String id) {
        for (int i = 0; i < productCount; i++) {
            if (products[i].getId().equals(id)) {
                return products[i];
            }
        }
        return null;
    }
    
    public Transaction findTransactionById(String id) {
        for (int i = 0; i < transactionCount; i++) {
            if (transactions[i].getId().equals(id)) {
                return transactions[i];
            }
        }
        return null;
    }
    
    // Method to get total revenue from all transactions
    public double calculateTotalRevenue() {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            // Calculate revenue for each transaction
            Transaction tx = transactions[i];
            total += tx.getQuantity() * tx.getProduct().getPrice();
        }
        return total;
    }
    
    // Method to get total profit from all transactions
    public double calculateTotalProfit() {
        double total = 0;
        for (int i = 0; i < transactionCount; i++) {
            Transaction tx = transactions[i];
            double revenue = tx.getQuantity() * tx.getProduct().getPrice();
            double totalCost = 0;
            
            // Add product production cost
            totalCost += tx.getQuantity() * tx.getProduct().getProductionCost();
            
            // Add additional costs from transaction
            Cost[] costs = tx.getCosts();
            for (Cost cost : costs) {
                if (cost != null) {
                    totalCost += cost.getAmount();
                }
            }
            
            total += (revenue - totalCost);
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "BusinessDatabase [customers=" + Arrays.toString(customers) + ",     products=" + Arrays.toString(products)
                + ", transactions=" + Arrays.toString(transactions) + ", customerCount=" + customerCount
                + ", productCount=" + productCount + ", transactionCount=" + transactionCount + "]";
    }
    

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BusinessDatabase other = (BusinessDatabase) obj;
        if (!Arrays.equals(customers, other.customers))
            return false;
        if (!Arrays.equals(products, other.products))
            return false;
        if (!Arrays.equals(transactions, other.transactions))
            return false;
        if (customerCount != other.customerCount)
            return false;
        if (productCount != other.productCount)
            return false;
        if (transactionCount != other.transactionCount)
            return false;
        return true;
    }
    
}