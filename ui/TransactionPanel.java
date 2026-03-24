package ui;

import controller.ProfitManagement;
import errors.ValidationException;
import other.Cost;
import other.Customer;
import other.Product;
import other.Transaction;
import service.InputValidator;
import user.Staff;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class TransactionPanel extends JPanel {
    private final MainSystemGUI app;
    private final ProfitManagement business;
    private final JTextArea output = new JTextArea(18, 60);

    private final JTextField dateField = new JTextField();
    private final JTextField customerIdField = new JTextField();

    private final JTextField addTxIdField = new JTextField();
    private final JTextField productIdField = new JTextField();
    private final JTextField quantityField = new JTextField();

    private final JTextField costTxIdField = new JTextField();
    private final JTextField costAmountField = new JTextField();
    private final JTextField costDescField = new JTextField();
    private final JTextField costProductIdField = new JTextField();

    private final JTextField viewTxIdField = new JTextField();

    public TransactionPanel(MainSystemGUI app, ProfitManagement business) {
        this.app = app;
        this.business = business;

        setLayout(new BorderLayout(8, 8));

        JPanel forms = new JPanel();
        forms.setLayout(new BoxLayout(forms, BoxLayout.Y_AXIS));

        forms.add(buildCreatePanel());
        forms.add(buildAddProductPanel());
        forms.add(buildAddCostPanel());
        forms.add(buildViewPanel());
        forms.add(buildListPanel());

        output.setEditable(false);
        add(forms, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);
    }

    public void refresh() {
        listTransactions();
    }

    private JPanel buildCreatePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 0, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Create Transaction"));
        JButton createButton = new JButton("Create");

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Customer ID:"));
        panel.add(customerIdField);
        panel.add(new JLabel());
        panel.add(createButton);

        createButton.addActionListener(e -> createTransaction());
        return panel;
    }

    private JPanel buildAddProductPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Add Product to Transaction"));
        JButton addButton = new JButton("Add Product");

        panel.add(new JLabel("Transaction ID:"));
        panel.add(addTxIdField);
        panel.add(new JLabel("Product ID:"));
        panel.add(productIdField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel());
        panel.add(addButton);

        addButton.addActionListener(e -> addProductToTransaction());
        return panel;
    }

    private JPanel buildAddCostPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Add Expense to Transaction"));
        JButton addCostButton = new JButton("Add Expense");

        panel.add(new JLabel("Transaction ID:"));
        panel.add(costTxIdField);
        panel.add(new JLabel("Amount:"));
        panel.add(costAmountField);
        panel.add(new JLabel("Description:"));
        panel.add(costDescField);
        panel.add(new JLabel("Product ID (optional):"));
        panel.add(costProductIdField);
        panel.add(new JLabel());
        panel.add(addCostButton);

        addCostButton.addActionListener(e -> addCostToTransaction());
        return panel;
    }

    private JPanel buildViewPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("View Transaction Details"));
        JButton viewButton = new JButton("View Details");

        panel.add(new JLabel("Transaction ID:"));
        panel.add(viewTxIdField);
        panel.add(new JLabel());
        panel.add(viewButton);

        viewButton.addActionListener(e -> viewTransactionDetails());
        return panel;
    }

    private JPanel buildListPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Quick Lists"));

        JButton listTransactionsButton = new JButton("List Transactions");
        JButton listCustomersButton = new JButton("List Customers");
        JButton listProductsButton = new JButton("List Products");

        listTransactionsButton.addActionListener(e -> listTransactions());
        listCustomersButton.addActionListener(e -> listCustomers());
        listProductsButton.addActionListener(e -> listProducts());

        panel.add(listTransactionsButton);
        panel.add(listCustomersButton);
        panel.add(listProductsButton);
        panel.add(new JLabel());
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.showScreen("menu"));
        bottom.add(backButton);
        return bottom;
    }

    private void createTransaction() {
        if (!can("CREATE_TRANSACTION")) {
            showError("You do not have permission to create transactions.");
            return;
        }
        try {
            String date = InputValidator.parseDate(dateField.getText());
            String customerId = InputValidator.requireText(customerIdField.getText(), "Customer ID");
            if (business.findCustomerById(customerId) == null) {
                showError("Customer not found.");
                return;
            }
            Staff staff = app.getLoggedInUser();
            Transaction tx = business.createTransaction(date, customerId, staff);
            if (tx == null) {
                showError("Could not create transaction. Check data.");
                return;
            }
            dateField.setText("");
            customerIdField.setText("");
            refresh();
            showInfo("Transaction created. ID: " + tx.getId());
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void addProductToTransaction() {
        if (!can("EDIT_TRANSACTION")) {
            showError("You do not have permission to edit transactions.");
            return;
        }
        try {
            String txId = InputValidator.requireText(addTxIdField.getText(), "Transaction ID");
            String productId = InputValidator.requireText(productIdField.getText(), "Product ID");
            double quantity = InputValidator.parsePositiveDouble(quantityField.getText(), "Quantity");

            boolean ok = business.addProductToTransaction(txId, productId, quantity);
            if (!ok) {
                showError("Unable to add product. Check IDs and quantity.");
                return;
            }
            addTxIdField.setText("");
            productIdField.setText("");
            quantityField.setText("");
            refresh();
            showInfo("Product added to transaction.");
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void addCostToTransaction() {
        if (!can("EDIT_TRANSACTION")) {
            showError("You do not have permission to edit transactions.");
            return;
        }
        try {
            String txId = InputValidator.requireText(costTxIdField.getText(), "Transaction ID");
            double amount = InputValidator.parsePositiveDouble(costAmountField.getText(), "Amount");
            String desc = InputValidator.requireText(costDescField.getText(), "Description");
            String productId = costProductIdField.getText();

            Cost cost;
            if (productId == null || productId.trim().isEmpty()) {
                cost = business.addTransactionCost(txId, amount, desc);
            } else {
                cost = business.addTransactionCost(txId, amount, desc, productId.trim());
            }
            if (cost == null) {
                showError("Could not add expense. Check IDs and amount.");
                return;
            }
            costTxIdField.setText("");
            costAmountField.setText("");
            costDescField.setText("");
            costProductIdField.setText("");
            refresh();
            showInfo("Expense added. ID: " + cost.getId());
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void viewTransactionDetails() {
        if (!can("VIEW_TRANSACTIONS")) {
            showError("You do not have permission to view transactions.");
            return;
        }
        String txId = viewTxIdField.getText();
        if (txId == null || txId.trim().isEmpty()) {
            showError("Transaction ID is required.");
            return;
        }
        Transaction tx = business.findTransactionById(txId.trim());
        if (tx == null) {
            showError("Transaction not found.");
            return;
        }
        output.setText(formatTransactionDetails(tx));
    }

    private void listTransactions() {
        if (!can("VIEW_TRANSACTIONS")) {
            showError("You do not have permission to view transactions.");
            return;
        }
        ArrayList<Transaction> transactions = business.getTransactions();
        StringBuilder sb = new StringBuilder();
        sb.append("Transactions\n");
        sb.append("----------------------------------------\n");
        if (transactions.isEmpty()) {
            sb.append("No transactions found.\n");
        } else {
            for (Transaction tx : transactions) {
                String staff = (tx.getStaff() == null) ? "Unassigned" : tx.getStaff().getUsername();
                sb.append("ID: ").append(tx.getId())
                        .append(" | Date: ").append(tx.getDate())
                        .append(" | Customer: ").append(tx.getCustomer().getName())
                        .append(" | Staff: ").append(staff)
                        .append(" | Items: ").append(tx.getProductCount())
                        .append("\n");
            }
        }
        output.setText(sb.toString());
    }

    private void listCustomers() {
        ArrayList<Customer> customers = business.getCustomers();
        StringBuilder sb = new StringBuilder();
        sb.append("Customers\n");
        sb.append("----------------------------------------\n");
        if (customers.isEmpty()) {
            sb.append("No customers found.\n");
        } else {
            for (Customer c : customers) {
                sb.append("ID: ").append(c.getId())
                        .append(" | ").append(c.getName())
                        .append(" | Phone: ").append(c.getPhoneNumber())
                        .append("\n");
            }
        }
        output.setText(sb.toString());
    }

    private void listProducts() {
        ArrayList<Product> products = business.getProducts();
        StringBuilder sb = new StringBuilder();
        sb.append("Products\n");
        sb.append("----------------------------------------\n");
        if (products.isEmpty()) {
            sb.append("No products found.\n");
        } else {
            for (Product p : products) {
                sb.append("ID: ").append(p.getId())
                        .append(" | ").append(p.getName())
                        .append(" | Price: $").append(p.getPrice())
                        .append(" | Unit: ").append(p.getUnit())
                        .append("\n");
            }
        }
        output.setText(sb.toString());
    }

    private String formatTransactionDetails(Transaction tx) {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction Details\n");
        sb.append("----------------------------------------\n");
        sb.append("ID: ").append(tx.getId()).append("\n");
        sb.append("Date: ").append(tx.getDate()).append("\n");
        sb.append("Customer: ").append(tx.getCustomer().getName()).append("\n");
        sb.append("Staff: ").append(tx.getStaff() == null ? "Unassigned" : tx.getStaff().getUsername()).append("\n\n");

        sb.append("Products\n");
        Product[] products = tx.getProducts();
        double[] quantities = tx.getQuantities();
        if (products.length == 0) {
            sb.append("No products.\n");
        } else {
            for (int i = 0; i < products.length; i++) {
                double lineTotal = quantities[i] * products[i].getPrice();
                sb.append("- ").append(products[i].getName())
                        .append(" (").append(quantities[i]).append(" ")
                        .append(products[i].getUnit()).append(")")
                        .append(" = $").append(String.format("%.2f", lineTotal))
                        .append("\n");
            }
        }

        sb.append("\nExpenses\n");
        Cost[] costs = tx.getCosts();
        if (costs.length == 0) {
            sb.append("No additional costs.\n");
        } else {
            for (Cost c : costs) {
                sb.append("- $").append(String.format("%.2f", c.getAmount()))
                        .append(" | ").append(c.getDescription())
                        .append("\n");
            }
        }

        double revenue = calculateRevenue(tx);
        double expenses = calculateExpenses(tx);
        double profit = revenue - expenses;
        sb.append("\nSummary\n");
        sb.append("Revenue: $").append(String.format("%.2f", revenue)).append("\n");
        sb.append("Expenses: $").append(String.format("%.2f", expenses)).append("\n");
        sb.append("Profit: $").append(String.format("%.2f", profit)).append("\n");

        return sb.toString();
    }

    private double calculateRevenue(Transaction tx) {
        double revenue = 0;
        Product[] products = tx.getProducts();
        double[] quantities = tx.getQuantities();
        for (int i = 0; i < products.length; i++) {
            revenue += quantities[i] * products[i].getPrice();
        }
        return revenue;
    }

    private double calculateExpenses(Transaction tx) {
        double expenses = 0;
        Product[] products = tx.getProducts();
        double[] quantities = tx.getQuantities();
        for (int i = 0; i < products.length; i++) {
            expenses += quantities[i] * products[i].getProductionCost();
        }
        Cost[] costs = tx.getCosts();
        for (Cost c : costs) {
            expenses += c.getAmount();
        }
        return expenses;
    }

    private boolean can(String action) {
        return app.getLoggedInUser() != null && app.getLoggedInUser().can(action);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
