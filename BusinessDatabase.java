public class BusinessDatabase {
    Customer[] customers;
    Product[] products;
    Transaction[] transactions;
    
    int customerCount;
    int productCount;
    int transactionCount;
    
    BusinessDatabase() {
        customers = new Customer[100];
        products = new Product[100];
        transactions = new Transaction[100];
        customerCount = 0;
        productCount = 0;
        transactionCount = 0;
    }
    
    void addCustomer(Customer customer) {
        if (customerCount < customers.length) {
            customers[customerCount] = customer;
            customerCount++;
        }
    }
    
    void addProduct(Product product) {
        if (productCount < products.length) {
            products[productCount] = product;
            productCount++;
        }
    }
    
    void addTransaction(Transaction transaction) {
        if (transactionCount < transactions.length) {
            transactions[transactionCount] = transaction;
            transactionCount++;
        }
    }
    
    // Search method with null safety
    Customer findCustomerByName(String name) {
        for (int i = 0; i < customerCount; i++) {
            if (customers[i].name.equals(name)) {
                return customers[i];
            }
        }
        return null;
    }
    
    // Simplified search - no more search by type/segment
    Product findProductById(String id) {
        for (int i = 0; i < productCount; i++) {
            if (products[i].id.equals(id)) {
                return products[i];
            }
        }
        return null;
    }
}
