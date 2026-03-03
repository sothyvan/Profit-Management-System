package controller;

import java.util.ArrayList;
import java.util.Scanner;

import other.Cost;
import other.Customer;
import other.Product;
import other.Transaction;
import user.IStaff;

public class MainSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProfitManagement business = new ProfitManagement();
    private static IStaff loggedInUser = null;

    public static void main(String[] args) {
        initializeSystem();

        System.out.println("=== PROFIT MANAGEMENT SYSTEM ===");
        if (!login()) {
            System.out.println("Too many failed attempts. Exiting...");
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    createTransaction();
                    break;
                case 2:
                    viewTransactions();
                    break;
                case 3:
                    createProduct();
                    break;
                case 4:
                    viewProducts();
                    break;
                case 5:
                    viewReport();
                    break;
                case 6:
                    manageUsers();
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice! Please enter 1-7.");
            }
        }
    }

    private static void initializeSystem() {
        business.loadDefaultUsers();
        business.loadSampleData();
    }

    private static boolean login() {
        int attempts = 0;
        final int maxAttempts = 3;

        while (attempts < maxAttempts) {
            System.out.println("\n=== LOGIN ===");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            IStaff authenticated = business.authenticate(username, password);
            if (authenticated != null) {
                loggedInUser = authenticated;
                System.out.println("\nLogin successful! Welcome, " + loggedInUser.getPosition() + " " + loggedInUser.getUsername());
                return true;
            }

            attempts++;
            System.out.println("Invalid credentials. Attempts remaining: " + (maxAttempts - attempts));
        }

        return false;
    }

    private static void printMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("Logged in as: " + loggedInUser.getPosition() + " (" + loggedInUser.getUsername() + ")");
        System.out.println("1. Create Transaction");
        System.out.println("2. View Transactions");
        System.out.println("3. Create Product");
        System.out.println("4. View Products");
        System.out.println("5. View Report");
        System.out.println("6. Manage Users");
        System.out.println("7. Logout");
    }

    private static void createTransaction() {
        if (!loggedInUser.can("CREATE_TRANSACTION")) {
            System.out.println("Sorry, " + loggedInUser.getPosition() + " cannot create transactions.");
            waitForEnter();
            return;
        }

        System.out.println("\n=== CREATE TRANSACTION ===");
        ArrayList<Customer> customers = business.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers found. Please add customers first.");
            return;
        }

        System.out.println("\nAvailable Customers:");
        for (Customer customer : customers) {
            System.out.println("  " + customer.getId() + " - " + customer.getName());
        }

        System.out.print("\nEnter customer ID: ");
        String customerId = scanner.nextLine();
        if (business.findCustomerById(customerId) == null) {
            System.out.println("Customer not found!");
            return;
        }

        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        Transaction transaction = business.createTransaction(date, customerId, loggedInUser);
        if (transaction == null) {
            System.out.println("Could not create transaction. Please check date/customer.");
            return;
        }

        System.out.println("Transaction created successfully! ID: " + transaction.getId());
        System.out.print("\nDo you want to add products to this transaction? (y/n): ");
        String addProducts = scanner.nextLine();
        if (addProducts.equalsIgnoreCase("y")) {
            addProductsToTransaction(transaction.getId());
        }
    }

    private static void addProductsToTransaction(String transactionId) {
        boolean adding = true;
        while (adding) {
            ArrayList<Product> products = business.getProducts();
            if (products.isEmpty()) {
                System.out.println("No products available.");
                return;
            }

            System.out.println("\n--- Add Products to Transaction ---");
            for (Product product : products) {
                System.out.println("  " + product.getId() + " - " + product.getName() + " ($" + product.getPrice() + " per " + product.getUnit() + ")");
            }

            System.out.print("\nEnter product ID (or 'done' to finish): ");
            String productId = scanner.nextLine();
            if (productId.equalsIgnoreCase("done")) {
                adding = false;
                continue;
            }

            Product selectedProduct = business.findProductById(productId);
            if (selectedProduct == null) {
                System.out.println("Product not found!");
                continue;
            }

            double quantity = getPositiveDoubleInput("Enter quantity: ");
            if (!business.addProductToTransaction(transactionId, productId, quantity)) {
                System.out.println("Unable to add product to transaction.");
                continue;
            }
            System.out.println("Added " + quantity + " " + selectedProduct.getUnit() + " of " + selectedProduct.getName());

            System.out.print("Add additional cost for this product? (y/n): ");
            String addCost = scanner.nextLine();
            if (addCost.equalsIgnoreCase("y")) {
                double amount = getPositiveDoubleInput("Enter cost amount: $");
                System.out.print("Enter cost description: ");
                String description = scanner.nextLine();
                Cost cost = business.addTransactionCost(transactionId, amount, description, productId);
                if (cost != null) {
                    System.out.println("Cost added: $" + cost.getAmount());
                } else {
                    System.out.println("Could not add cost.");
                }
            }
        }
    }

    private static void viewTransactions() {
        if (!loggedInUser.can("VIEW_TRANSACTIONS")) {
            System.out.println("Sorry, " + loggedInUser.getPosition() + " cannot view transactions.");
            waitForEnter();
            return;
        }

        System.out.println("\n=== TRANSACTIONS ===");
        ArrayList<Transaction> transactions = business.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.printf("%-4s %-10s %-12s %-16s %-12s %-6s %-12s %-12s %-12s%n",
                "No", "ID", "Date", "Customer", "Staff", "Items", "Revenue", "Expenses", "Profit");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            String staffName = transaction.getStaff() == null
                    ? "Unassigned"
                    : transaction.getStaff().getUsername();
            if (staffName.length() > 12) {
                staffName = staffName.substring(0, 12);
            }
            double revenue = calculateTransactionRevenue(transaction);
            double expenses = calculateTransactionExpenses(transaction);
            double profit = revenue - expenses;
            System.out.printf("%-4d %-10s %-12s %-16s %-12s %-6d %-12s %-12s %-12s%n",
                    i + 1,
                    transaction.getId(),
                    transaction.getDate(),
                    transaction.getCustomer().getName(),
                    staffName,
                    transaction.getProductCount(),
                    formatMoney(revenue),
                    formatMoney(expenses),
                    formatMoney(profit));
        }

        int choice = getIntInput("\nEnter transaction number to view details (or 0 to go back): ");
        if (choice > 0 && choice <= transactions.size()) {
            viewTransactionDetails(transactions.get(choice - 1));
        }
    }

    private static void viewTransactionDetails(Transaction transaction) {
        System.out.println("\n========================================");
        System.out.println("TRANSACTION DETAILS");
        System.out.println("========================================");
        System.out.println("ID       : " + transaction.getId());
        System.out.println("Date     : " + transaction.getDate());
        System.out.println("Customer : " + transaction.getCustomer().getName());
        System.out.println("Staff    : " + (transaction.getStaff() == null
                ? "Unassigned"
                : transaction.getStaff().getFullName() + " (" + transaction.getStaff().getPosition() + ")"));

        System.out.println("\nProducts");
        System.out.printf("%-4s %-20s %-10s %-8s %-12s %-12s%n",
                "No", "Name", "Quantity", "Unit", "Unit Price", "Line Total");
        System.out.println("--------------------------------------------------------------------");
        Product[] products = transaction.getProducts();
        double[] quantities = transaction.getQuantities();
        for (int i = 0; i < transaction.getProductCount(); i++) {
            double lineTotal = quantities[i] * products[i].getPrice();
            System.out.printf("%-4d %-20s %-10.2f %-8s %-12s %-12s%n",
                    i + 1,
                    products[i].getName(),
                    quantities[i],
                    products[i].getUnit(),
                    formatMoney(products[i].getPrice()),
                    formatMoney(lineTotal));
        }

        double additionalCosts = 0;
        if (transaction.getCostCount() > 0) {
            System.out.println("\nAdditional Costs");
            System.out.printf("%-4s %-30s %-12s%n", "No", "Description", "Amount");
            System.out.println("--------------------------------------------------");
            Cost[] costs = transaction.getCosts();
            int row = 1;
            for (Cost cost : costs) {
                if (cost != null) {
                    additionalCosts += cost.getAmount();
                    System.out.printf("%-4d %-30s %-12s%n",
                            row++,
                            cost.getDescription(),
                            formatMoney(cost.getAmount()));
                }
            }
        }

        double revenue = calculateTransactionRevenue(transaction);
        double expenses = calculateTransactionExpenses(transaction);
        double profit = revenue - expenses;
        System.out.println("\nSummary");
        System.out.println("----------------------------------------");
        System.out.printf("%-22s %s%n", "Revenue", formatMoney(revenue));
        System.out.printf("%-22s %s%n", "Expenses", formatMoney(expenses));
        System.out.printf("%-22s %s%n", "Profit", formatMoney(profit));
        System.out.printf("%-22s %s%n", "Additional Cost Entries", formatMoney(additionalCosts));

        waitForEnter();
    }

    private static double calculateTransactionRevenue(Transaction transaction) {
        double revenue = 0;
        Product[] products = transaction.getProducts();
        double[] quantities = transaction.getQuantities();
        for (int i = 0; i < transaction.getProductCount(); i++) {
            revenue += quantities[i] * products[i].getPrice();
        }
        return revenue;
    }

    private static double calculateTransactionExpenses(Transaction transaction) {
        double expenses = 0;
        Product[] products = transaction.getProducts();
        double[] quantities = transaction.getQuantities();
        for (int i = 0; i < transaction.getProductCount(); i++) {
            expenses += quantities[i] * products[i].getProductionCost();
        }

        Cost[] costs = transaction.getCosts();
        for (Cost cost : costs) {
            if (cost != null) {
                expenses += cost.getAmount();
            }
        }
        return expenses;
    }

    private static String formatMoney(double amount) {
        return String.format("$%,.2f", amount);
    }

    private static void createProduct() {
        if (!loggedInUser.can("CREATE_PRODUCT")) {
            System.out.println("Sorry, " + loggedInUser.getPosition() + " cannot create products.");
            waitForEnter();
            return;
        }

        System.out.println("\n=== CREATE PRODUCT ===");
        System.out.print("Enter product name: ");
        String name = scanner.nextLine();
        System.out.println("Unit is fixed for wholesale mode: " + Product.DEFAULT_UNIT);
        double price = getPositiveDoubleInput("Enter price: $");
        double productionCost = getNonNegativeDoubleInput("Enter production cost (or 0 if none): $");

        Product product = business.createProduct(name, price, productionCost);
        if (product == null) {
            System.out.println("Invalid product data.");
            return;
        }

        System.out.println("Product created successfully! ID: " + product.getId());
    }

    private static void viewProducts() {
        if (!loggedInUser.can("VIEW_PRODUCTS")) {
            System.out.println("Sorry, " + loggedInUser.getPosition() + " cannot view products.");
            waitForEnter();
            return;
        }

        System.out.println("\n=== PRODUCTS ===");
        ArrayList<Product> products = business.getProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
            return;
        }

        for (int i = 0; i < products.size(); i++) {
            System.out.println((i + 1) + ". " + products.get(i));
        }
        waitForEnter();
    }

    private static void viewReport() {
        if (!loggedInUser.can("VIEW_REPORT")) {
            System.out.println("Sorry, " + loggedInUser.getPosition() + " cannot view reports.");
            waitForEnter();
            return;
        }

        boolean reporting = true;
        while (reporting) {
            System.out.println("\n=== FINANCIAL REPORTS ===");
            System.out.printf("Tax Rate: %.2f%%%n", Transaction.getTaxRate() * 100);
            System.out.println("1. Daily Report");
            System.out.println("2. Monthly Report");
            System.out.println("3. Yearly Report");
            System.out.println("4. All-Time Report");
            System.out.println("5. Back to Main Menu");

            int choice = getIntInput("Choose report type: ");
            switch (choice) {
                case 1:
                    viewDailyReport();
                    break;
                case 2:
                    viewMonthlyReport();
                    break;
                case 3:
                    viewYearlyReport();
                    break;
                case 4:
                    viewAllTimeReport();
                    break;
                case 5:
                    reporting = false;
                    break;
                default:
                    System.out.println("Invalid report option.");
            }
        }
    }

    private static void viewDailyReport() {
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();

        int count = business.getDailyTransactionCount(date);
        if (count == 0) {
            System.out.println("No transactions found for " + date);
            waitForEnter();
            return;
        }

        double revenue = business.getDailyRevenue(date);
        double expenses = business.getDailyExpenses(date);
        double profitBeforeTax = business.getDailyProfit(date);
        double profitAfterTax = business.getDailyProfitAfterTax(date);
        double margin = business.getDailyMargin(date);

        printReportCard("DAILY REPORT", date, count, revenue, expenses, profitBeforeTax, profitAfterTax, margin);
        waitForEnter();
    }

    private static void viewMonthlyReport() {
        System.out.print("Enter month (YYYY-MM): ");
        String month = scanner.nextLine();

        int count = business.getMonthlyTransactionCount(month);
        if (count == 0) {
            System.out.println("No transactions found for " + month);
            waitForEnter();
            return;
        }

        double revenue = business.getMonthlyRevenue(month);
        double expenses = business.getMonthlyExpenses(month);
        double profitBeforeTax = business.getMonthlyProfit(month);
        double profitAfterTax = business.getMonthlyProfitAfterTax(month);
        double margin = business.getMonthlyMargin(month);

        printReportCard("MONTHLY REPORT", month, count, revenue, expenses, profitBeforeTax, profitAfterTax, margin);
        waitForEnter();
    }

    private static void viewYearlyReport() {
        System.out.print("Enter year (YYYY): ");
        String year = scanner.nextLine();

        int count = business.getYearlyTransactionCount(year);
        if (count == 0) {
            System.out.println("No transactions found for " + year);
            waitForEnter();
            return;
        }

        double revenue = business.getYearlyRevenue(year);
        double expenses = business.getYearlyExpenses(year);
        double profitBeforeTax = business.getYearlyProfit(year);
        double profitAfterTax = business.getYearlyProfitAfterTax(year);
        double margin = business.getYearlyMargin(year);

        printReportCard("YEARLY REPORT", year, count, revenue, expenses, profitBeforeTax, profitAfterTax, margin);
        waitForEnter();
    }

    private static void viewAllTimeReport() {
        int count = business.getTransactionCount();
        if (count == 0) {
            System.out.println("No transactions found.");
            waitForEnter();
            return;
        }

        double revenue = business.calculateTotalRevenue();
        double expenses = business.calculateTotalExpenses();
        double profitBeforeTax = business.calculateTotalProfit();
        double profitAfterTax = business.calculateTotalProfitAfterTax();
        double margin = business.calculateOverallMargin();

        printReportCard("ALL-TIME REPORT", "All Time", count, revenue, expenses, profitBeforeTax, profitAfterTax, margin);
        waitForEnter();
    }

    private static void printReportCard(
            String title,
            String period,
            int transactionCount,
            double revenue,
            double expenses,
            double profitBeforeTax,
            double profitAfterTax,
            double margin) {
        double avgRevenuePerTransaction = transactionCount == 0 ? 0 : revenue / transactionCount;
        double avgProfitPerTransaction = transactionCount == 0 ? 0 : profitBeforeTax / transactionCount;

        System.out.println("\n========================================");
        System.out.println(title);
        System.out.println("Period: " + period);
        System.out.println("========================================");
        System.out.printf("%-28s %10d%n", "Transactions", transactionCount);
        System.out.printf("%-28s $%,10.2f%n", "Gross Revenue", revenue);
        System.out.printf("%-28s $%,10.2f%n", "Total Expenses", expenses);
        System.out.printf("%-28s $%,10.2f%n", "Gross Profit (Before Tax)", profitBeforeTax);
        System.out.printf("%-28s $%,10.2f%n", "Net Profit (After Tax)", profitAfterTax);
        System.out.printf("%-28s %10.2f%%%n", "Profit Margin", margin);
        System.out.printf("%-28s $%,10.2f%n", "Avg Revenue / Transaction", avgRevenuePerTransaction);
        System.out.printf("%-28s $%,10.2f%n", "Avg Profit / Transaction", avgProfitPerTransaction);
        System.out.println("----------------------------------------");
        System.out.printf("Applied Tax Rate: %.2f%%%n", Transaction.getTaxRate() * 100);
    }

    private static void manageUsers() {
        if (!loggedInUser.can("MANAGE_USERS")) {
            System.out.println("Sorry, " + loggedInUser.getPosition() + " cannot manage users.");
            waitForEnter();
            return;
        }

        boolean managing = true;
        while (managing) {
            System.out.println("\n=== USER MANAGEMENT ===");
            System.out.println("1. View Users");
            System.out.println("2. Add User");
            System.out.println("3. Remove User");
            System.out.println("4. Back");

            int choice = getIntInput("Choose option: ");
            switch (choice) {
                case 1:
                    printUsersTable();
                    waitForEnter();
                    break;
                case 2:
                    addUser();
                    break;
                case 3:
                    removeUser();
                    break;
                case 4:
                    managing = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void addUser() {
        System.out.println("\n--- Add User ---");
        System.out.println("1. Manager");
        System.out.println("2. Cashier");
        int roleChoice = getIntInput("Choose role: ");

        String role;
        if (roleChoice == 1) {
            role = "Manager";
        } else if (roleChoice == 2) {
            role = "Cashier";
        } else {
            System.out.println("Invalid role.");
            return;
        }

        System.out.print("Full name: ");
        String fullName = scanner.nextLine();
        System.out.print("Phone (digits only): ");
        String phone = scanner.nextLine();
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        double salary = getNonNegativeDoubleInput("Salary: $");

        IStaff created = business.createStaff(role, fullName, phone, username, password, salary);
        if (created == null) {
            System.out.println("Could not create user. Check data and ensure username is unique.");
        } else {
            System.out.println("User created successfully: " + created.getUsername() + " (" + created.getPosition() + ")");
        }
    }

    private static void removeUser() {
        System.out.println("\n--- Remove User ---");
        printUsersTable();
        System.out.print("Enter username to remove: ");
        String username = scanner.nextLine();

        if (loggedInUser.getUsername().equals(username)) {
            System.out.println("You cannot remove your own account while logged in.");
            return;
        }

        IStaff target = business.findStaffByUsername(username);
        if (target == null) {
            System.out.println("User not found.");
            return;
        }

        if ("Manager".equalsIgnoreCase(target.getPosition()) && business.getManagerCount() <= 1) {
            System.out.println("Cannot remove the last manager.");
            return;
        }

        boolean removed = business.removeStaffByUsername(username);
        if (removed) {
            System.out.println("User removed successfully.");
        } else {
            System.out.println("Could not remove user.");
        }
    }

    private static void printUsersTable() {
        ArrayList<IStaff> users = business.getStaffMembers();
        System.out.println();
        System.out.printf("%-4s %-12s %-20s %-15s %-10s %-14s %-8s%n",
                "No", "ID", "Full Name", "Username", "Role", "Phone", "Status");
        System.out.println("--------------------------------------------------------------------------------");

        for (int i = 0; i < users.size(); i++) {
            IStaff user = users.get(i);
            System.out.printf("%-4d %-12s %-20s %-15s %-10s %-14s %-8s%n",
                    i + 1,
                    user.getStaffId(),
                    user.getFullName(),
                    user.getUsername(),
                    user.getPosition(),
                    user.getPhone(),
                    user.isActive() ? "Active" : "Inactive");
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static double getPositiveDoubleInput(String prompt) {
        while (true) {
            double value = getNonNegativeDoubleInput(prompt);
            if (value > 0) {
                return value;
            }
            System.out.println("Value must be greater than zero.");
        }
    }

    private static double getNonNegativeDoubleInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input);
                if (value >= 0) {
                    return value;
                }
                System.out.println("Value cannot be negative.");
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static void waitForEnter() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return getClass() == obj.getClass();
    }
}
