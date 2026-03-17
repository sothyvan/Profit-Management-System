package controller;

import java.util.ArrayList;

import other.Cost;
import other.Customer;
import other.Product;
import other.Transaction;
import user.CashierStaff;
import user.ManagerStaff;
import user.Staff;

public class ProfitManagement {
    private ArrayList<Staff> staff;
    private ArrayList<Customer> customers;
    private ArrayList<Product> products;
    private ArrayList<Transaction> transactions;
    
    public ProfitManagement() {
        staff = new ArrayList<>();
        customers = new ArrayList<>();
        products = new ArrayList<>();
        transactions = new ArrayList<>();
    }
    
    // ========== GETTERS ==========
    public int getStaffCount() {
        return staff.size();
    }

    public int getCustomerCount() {
        return customers.size();
    }
    
    public int getProductCount() {
        return products.size();
    }
    
    public int getTransactionCount() {
        return transactions.size();
    }

    public ArrayList<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }
    
    public ArrayList<Staff> getStaffMembers() {
        return new ArrayList<>(staff);
    }

    public ArrayList<Staff> getCashiers() {
        ArrayList<Staff> result = new ArrayList<>();
        for (Staff s : staff) {
            if (s instanceof CashierStaff) {
                result.add(s);
            }
        }
        return result;
    }
    
    public ArrayList<Staff> getManagers() {
        ArrayList<Staff> result = new ArrayList<>();
        for (Staff s : staff) {
            if (s instanceof ManagerStaff) {
                result.add(s);
            }
        }
        return result;
    }

    public ArrayList<Product> getProducts() {
        return new ArrayList<>(products);
    }
    
    public ArrayList<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }

    // ========== STAFF METHODS ==========
    public void addStaff(Staff staffMember) {
        if (staffMember != null) {
            staff.add(staffMember);
        }
    }

    public Staff authenticate(String username, String password) {
        for (Staff s : staff) {
            if (s.getUsername().equals(username) && s.checkPassword(password) && s.isActive()) {
                return s;
            }
        }
        return null;
    }

    public Staff findStaffByUsername(String username) {
        if (isNullOrBlank(username)) {
            return null;
        }
        for (Staff s : staff) {
            if (s.getUsername().equals(username.trim())) {
                return s;
            }
        }
        return null;
    }

    public int getManagerCount() {
        int count = 0;
        for (Staff s : staff) {
            if (s instanceof ManagerStaff) {
                count++;
            }
        }
        return count;
    }
    
    public int getCashierCount() {
        int count = 0;
        for (Staff s : staff) {
            if (s instanceof CashierStaff) {
                count++;
            }
        }
        return count;
    }

    public Staff createStaff(String role, String fullName, String phone, String username, String password, double salary) {
        if (isNullOrBlank(role) || isNullOrBlank(fullName) || isNullOrBlank(phone)
                || isNullOrBlank(username) || isNullOrBlank(password) || salary < 0) {
            return null;
        }
        if (findStaffByUsername(username) != null) {
            return null;
        }

        String normalizedRole = role.trim().toLowerCase();
        Staff staffProfile = new Staff(fullName.trim(), phone.trim(), username.trim(), password) {
            @Override
            public void setSalary(double salary) {
                this.salary = salary;
            }

            @Override
            public boolean can(String action) {
                return false;
            }
        };
        Staff createdStaff;
        if ("manager".equals(normalizedRole)) {
            createdStaff = new ManagerStaff(staffProfile, salary);
        } else if ("cashier".equals(normalizedRole)) {
            createdStaff = new CashierStaff(staffProfile, salary);
        } else {
            return null;
        }

        addStaff(createdStaff);
        return createdStaff;
    }

    public boolean removeStaffByUsername(String username) {
        Staff target = findStaffByUsername(username);
        if (target == null) {
            return false;
        }
        if (target instanceof ManagerStaff && getManagerCount() <= 1) {
            return false;
        }
        return staff.remove(target);
    }
    
    // ========== ADD METHODS ==========
    public void addCustomer(Customer customer) {
        if (customer != null) {
            customers.add(customer);
        }
    }
    
    public void addProduct(Product product) {
        if (product != null) {
            products.add(product);
        }
    }
    
    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
        }
    }

    // ========== CREATION METHODS ==========
    public Customer createCustomer(String firstName, String lastName, String phoneNumber) {
        if (isNullOrBlank(firstName) || isNullOrBlank(lastName) || isNullOrBlank(phoneNumber)) {
            return null;
        }
        Customer customer = new Customer(firstName.trim(), lastName.trim(), phoneNumber.trim());
        addCustomer(customer);
        return customer;
    }

    public Product createProduct(String name, String unit, double price, double productionCost) {
        if (isNullOrBlank(name) || price <= 0 || productionCost < 0) {
            return null;
        }
        Product product = new Product(name.trim(), Product.DEFAULT_UNIT, price);
        product.setProductionCost(productionCost);
        addProduct(product);
        return product;
    }

    public Product createProduct(String name, double price, double productionCost) {
        return createProduct(name, Product.DEFAULT_UNIT, price, productionCost);
    }

    public Transaction createTransaction(String date, String customerId, Staff staff) {
        Customer customer = findCustomerById(customerId);
        if (customer == null || isNullOrBlank(date)) {
            return null;
        }
        Transaction transaction = new Transaction(date.trim(), customer, staff);
        addTransaction(transaction);
        return transaction;
    }

    public boolean addProductToTransaction(String transactionId, String productId, double quantity) {
        Transaction transaction = findTransactionById(transactionId);
        Product product = findProductById(productId);
        if (transaction == null || product == null || quantity <= 0) {
            return false;
        }
        transaction.addProduct(product, quantity);
        return true;
    }

    public Cost addTransactionCost(String transactionId, double amount, String description) {
        return addTransactionCost(transactionId, amount, description, null);
    }

    public Cost addTransactionCost(String transactionId, double amount, String description, String productId) {
        Transaction transaction = findTransactionById(transactionId);
        if (transaction == null || amount <= 0) {
            return null;
        }

        Product relatedProduct = null;
        if (!isNullOrBlank(productId)) {
            relatedProduct = findProductById(productId);
            if (relatedProduct == null) {
                return null;
            }
        }

        Cost cost = new Cost(amount, description, relatedProduct);
        transaction.addCost(cost);
        return cost;
    }
    
    // ========== REMOVE METHODS ==========
    public boolean removeCustomer(Customer customer) {
        return customers.remove(customer);
    }
    
    public boolean removeCustomerById(String id) {
        return customers.removeIf(customer -> customer.getId().equals(id));
    }
    
    public boolean removeProduct(Product product) {
        return products.remove(product);
    }
    
    public boolean removeProductById(String id) {
        return products.removeIf(product -> product.getId().equals(id));
    }
    
    public boolean removeTransaction(Transaction transaction) {
        return transactions.remove(transaction);
    }
    
    public boolean removeTransactionById(String id) {
        return transactions.removeIf(transaction -> transaction.getId().equals(id));
    }
    
    // ========== SEARCH METHODS ==========
    public Customer findCustomerByName(String name) {
        for (Customer customer : customers) {
            if (customer.getName().equals(name)) {
                return customer;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(String id) {
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        return null;
    }
    
    public Product findProductByName(String name) {
        for (Product product : products) {
            if (product.getName().equals(name)) {
                return product;
            }
        }
        return null;
    }
    
    public Product findProductById(String id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }
    
    public Transaction findTransactionById(String id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId().equals(id)) {
                return transaction;
            }
        }
        return null;
    }
    
    // ========== FILTER METHODS ==========
    public ArrayList<Transaction> findTransactionsByCustomer(String customerId) {
        ArrayList<Transaction> result = new ArrayList<>();
        Customer customer = findCustomerById(customerId);
        if (customer != null) {
            for (Transaction transaction : transactions) {
                if (transaction.getCustomer().equals(customer)) {
                    result.add(transaction);
                }
            }
        }
        return result;
    }
    
    public ArrayList<Transaction> findTransactionsByDate(String date) {
        ArrayList<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getDate().equals(date)) {
                result.add(transaction);
            }
        }
        return result;
    }
    
    // ========== HELPER METHOD TO CALCULATE SINGLE TRANSACTION REVENUE ==========
    private double calculateSingleTransactionRevenue(Transaction transaction) {
        double revenue = 0;
        Product[] products = transaction.getProducts();
        double[] quantities = transaction.getQuantities();
        
        for (int i = 0; i < transaction.getProductCount(); i++) {
            revenue += quantities[i] * products[i].getPrice();
        }
        return revenue;
    }
    
    // ========== HELPER METHOD TO CALCULATE SINGLE TRANSACTION EXPENSES ==========
    private double calculateSingleTransactionExpenses(Transaction transaction) {
        double expenses = 0;
        
        // 1. Product production costs
        Product[] products = transaction.getProducts();
        double[] quantities = transaction.getQuantities();
        
        for (int i = 0; i < transaction.getProductCount(); i++) {
            expenses += quantities[i] * products[i].getProductionCost();
        }
        
        // 2. Additional costs
        Cost[] costs = transaction.getCosts();
        for (Cost cost : costs) {
            if (cost != null) {
                expenses += cost.getAmount();
            }
        }
        
        return expenses;
    }
    
    // ========== DAILY REPORT METHODS ==========
    public double getDailyRevenue(String date) {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getDate().equals(date)) {
                total += calculateSingleTransactionRevenue(transaction);
            }
        }
        return total;
    }
    
    public double getDailyExpenses(String date) {
        double total = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getDate().equals(date)) {
                total += calculateSingleTransactionExpenses(transaction);
            }
        }
        return total;
    }
    
    public double getDailyProfit(String date) {
        return getDailyRevenue(date) - getDailyExpenses(date);
    }
    
    public double getDailyMargin(String date) {
        double revenue = getDailyRevenue(date);
        double profit = getDailyProfit(date);
        if (revenue == 0) return 0;
        return (profit / revenue) * 100;
    }
    
    public int getDailyTransactionCount(String date) {
        int count = 0;
        for (Transaction transaction : transactions) {
            if (transaction.getDate().equals(date)) {
                count++;
            }
        }
        return count;
    }
    
    // ========== MONTHLY REPORT METHODS ==========
    public double getMonthlyRevenue(String yearMonth) {
        // yearMonth format: YYYY-MM (e.g., "2024-01")
        double total = 0;
        for (Transaction transaction : transactions) {
            String transDate = transaction.getDate();
            if (transDate.length() >= 7 && transDate.substring(0, 7).equals(yearMonth)) {
                total += calculateSingleTransactionRevenue(transaction);
            }
        }
        return total;
    }
    
    public double getMonthlyExpenses(String yearMonth) {
        double total = 0;
        for (Transaction transaction : transactions) {
            String transDate = transaction.getDate();
            if (transDate.length() >= 7 && transDate.substring(0, 7).equals(yearMonth)) {
                total += calculateSingleTransactionExpenses(transaction);
            }
        }
        return total;
    }
    
    public double getMonthlyProfit(String yearMonth) {
        return getMonthlyRevenue(yearMonth) - getMonthlyExpenses(yearMonth);
    }
    
    public double getMonthlyMargin(String yearMonth) {
        double revenue = getMonthlyRevenue(yearMonth);
        double profit = getMonthlyProfit(yearMonth);
        if (revenue == 0) return 0;
        return (profit / revenue) * 100;
    }
    
    public int getMonthlyTransactionCount(String yearMonth) {
        int count = 0;
        for (Transaction transaction : transactions) {
            String transDate = transaction.getDate();
            if (transDate.length() >= 7 && transDate.substring(0, 7).equals(yearMonth)) {
                count++;
            }
        }
        return count;
    }
    
    // ========== YEARLY REPORT METHODS ==========
    public double getYearlyRevenue(String year) {
        double total = 0;
        for (Transaction transaction : transactions) {
            String transDate = transaction.getDate();
            if (transDate.length() >= 4 && transDate.substring(0, 4).equals(year)) {
                total += calculateSingleTransactionRevenue(transaction);
            }
        }
        return total;
    }
    
    public double getYearlyExpenses(String year) {
        double total = 0;
        for (Transaction transaction : transactions) {
            String transDate = transaction.getDate();
            if (transDate.length() >= 4 && transDate.substring(0, 4).equals(year)) {
                total += calculateSingleTransactionExpenses(transaction);
            }
        }
        return total;
    }
    
    public double getYearlyProfit(String year) {
        return getYearlyRevenue(year) - getYearlyExpenses(year);
    }
    
    public double getYearlyMargin(String year) {
        double revenue = getYearlyRevenue(year);
        double profit = getYearlyProfit(year);
        if (revenue == 0) return 0;
        return (profit / revenue) * 100;
    }
    
    public int getYearlyTransactionCount(String year) {
        int count = 0;
        for (Transaction transaction : transactions) {
            String transDate = transaction.getDate();
            if (transDate.length() >= 4 && transDate.substring(0, 4).equals(year)) {
                count++;
            }
        }
        return count;
    }
    
    // ========== TOTAL ALL-TIME METHODS ==========
    public double calculateTotalRevenue() {
        double total = 0;
        for (Transaction transaction : transactions) {
            total += calculateSingleTransactionRevenue(transaction);
        }
        return total;
    }
    
    public double calculateTotalExpenses() {
        double total = 0;
        for (Transaction transaction : transactions) {
            total += calculateSingleTransactionExpenses(transaction);
        }
        return total;
    }
    
    public double calculateTotalProfit() {
        return calculateTotalRevenue() - calculateTotalExpenses();
    }
    
    public double calculateOverallMargin() {
        double totalRevenue = calculateTotalRevenue();
        double totalProfit = calculateTotalProfit();
        if (totalRevenue == 0) return 0;
        return (totalProfit / totalRevenue) * 100;
    }
    
    // ========== UTILITY METHODS ==========
    public void clearAllData() {
        staff.clear();
        customers.clear();
        products.clear();
        transactions.clear();
    }

    public void loadDefaultUsers() {
        if (!staff.isEmpty()) {
            return;
        }
        createStaff("Manager", "Taing Sothyvan", "098765432", "Sothyvan", "Van1234", 2500);
        createStaff("Cashier", "Eng Vathana", "012345678", "Vathana", "Vathana123", 699);
    }

    public void loadSampleData() {
        if (!customers.isEmpty() || !products.isEmpty() || !transactions.isEmpty()) {
            return;
        }

        Customer cust1 = createCustomer("Ah", "Poy", "8551001");
        Customer cust2 = createCustomer("Socheat", "Hem Eam", "8551002");
        Customer cust3 = createCustomer("Rithybot", "Samnang", "8551003");
        Customer cust4 = createCustomer("James", "Bond", "8551004");

        Product prod1 = createProduct("Apples", 4.0, 2.5);
        Product prod2 = createProduct("Shoes", 120.0, 50.0);
        Product prod3 = createProduct("Software", 299.0, 30.0);
        Product prod4 = createProduct("Shirt", 20.0, 15.0);
        Product prod5 = createProduct("Cement", 25,20);
        Product prod6 = createProduct("Steel", 40,25);
        Product prod7 = createProduct("Solar Panel", 60,45);

        Staff defaultStaff = staff.isEmpty() ? null : staff.get(0);

        Transaction tx1 = new Transaction("2025-03-13", cust1, defaultStaff, prod1, 100);
        tx1.addCost(new Cost(50.0, "Shipping", null));

        Transaction tx2 = new Transaction("2025-03-14", cust3, defaultStaff);
        tx2.addProduct(prod2, 40);
        tx2.addProduct(prod1, 20);
        tx2.addCost(new Cost(20.0, "Packaging", null));

        Transaction tx3 = new Transaction("2026-03-15", cust2, defaultStaff, prod3, 10);
        tx3.addCost(new Cost(100.0, "License Fee", prod3));
        
        Transaction tx4 = new Transaction("2026-03-16", cust4, defaultStaff, prod4, 1000);
        tx4.addProduct(prod5, 200);
        
        Transaction tx5 = new Transaction( "2026-03-17", cust1, defaultStaff, prod7 ,450);
        tx5.addProduct(prod6, 300);
        tx5.addCost(new Cost(250, "installation", null));

        addTransaction(tx1);
        addTransaction(tx2);
        addTransaction(tx3);
        addTransaction(tx4);
        addTransaction(tx5);
    }

    private boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return "ProfitManagement [staff=" + staff +
                ", customers=" + customers + 
                ", products=" + products + 
                ", transactions=" + transactions + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfitManagement other = (ProfitManagement) obj;
        return transactions.equals(other.transactions);
    }
}
