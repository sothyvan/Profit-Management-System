public class BusinessDatabase {
    Customer[] customers;
    Product[] products;
    Transaction[] transactions;
    
    int customerCount;
    int productCount;
    int transactionCount;
    
    BusinessDatabase() {
        customers = new Customer[10];
        products = new Product[10];
        transactions = new Transaction[10];
        customerCount = 0;
        productCount = 0;
        transactionCount = 0;
    }
    
    void addCustomer(Customer customer) {
        if (customerCount >= customers.length) {
            Customer[] newArr = new Customer[customerCount * 2];
            // Copy the old data to the new array
            for (int i = 0; i < customers.length; i++){
                newArr[i] = customers[i];
            }
            customers = newArr; // Replace the old array with the new one
        } 
        customers[customerCount] = customer;
        customerCount++;
    }
    
    void addProduct(Product product) {
        if (productCount >= products.length) {
            Product[] newArr = new Product[productCount * 2];
            // Copy the old data to the new array
            for (int i = 0; i < products.length; i++){
                newArr[i] = products[i];
            }
            products = newArr; // Replace the old array with the new one
        }
        products[productCount] = product;
        productCount++;
    }
    
    void addTransaction(Transaction transaction) {
        if (transactionCount >= transactions.length) {
            Transaction[] newArr = new Transaction[transactionCount * 2];
            // Copy the old data to the new array
            for (int i = 0; i < transactions.length; i++){
                newArr[i] = transactions[i];
            }
            transactions = newArr; // Replace the old array with the new one
        }
        transactions[transactionCount] = transaction;
        transactionCount++;
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
