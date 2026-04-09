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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class TransactionPanel extends JPanel {
    private final MainSystemGUI app;
    private final ProfitManagement business;
    private final JTextArea output = new JTextArea(18, 60);

    private final JTextField dateField = new JTextField();
    private final JComboBox<ComboItem<Customer>> customerCombo = new JComboBox<>();

    private final JComboBox<ComboItem<Transaction>> txForProductCombo = new JComboBox<>();
    private final JComboBox<ComboItem<Product>> productCombo = new JComboBox<>();
    private final JTextField quantityField = new JTextField();

    private final JComboBox<ComboItem<Transaction>> txForCostCombo = new JComboBox<>();
    private final JTextField costAmountField = new JTextField();
    private final JTextField costDescField = new JTextField();
    private final JComboBox<ComboItem<Product>> costProductCombo = new JComboBox<>();

    private final JComboBox<ComboItem<Transaction>> txForViewCombo = new JComboBox<>();

    private ArrayList<ComboItem<Customer>> customerItems = new ArrayList<>();
    private ArrayList<ComboItem<Product>> productItems = new ArrayList<>();
    private ArrayList<ComboItem<Transaction>> transactionItems = new ArrayList<>();
    private boolean updatingCombo = false;

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

        installFilter(customerCombo, () -> customerItems);
        installFilter(productCombo, () -> productItems);
        installFilter(costProductCombo, this::buildCostProductItems);
        installFilter(txForProductCombo, () -> transactionItems);
        installFilter(txForCostCombo, () -> transactionItems);
        installFilter(txForViewCombo, () -> transactionItems);
    }

    public void refresh() {
        loadComboData();
        listTransactions();
    }

    private JPanel buildCreatePanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Create Transaction"));
        JButton createButton = new JButton("Create");

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Customer:"));
        panel.add(customerCombo);
        panel.add(new JLabel());
        panel.add(createButton);

        createButton.addActionListener(e -> createTransaction());
        return panel;
    }

    private JPanel buildAddProductPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("Add Product to Transaction"));
        JButton addButton = new JButton("Add Product");

        panel.add(new JLabel("Transaction:"));
        panel.add(txForProductCombo);
        panel.add(new JLabel("Product:"));
        panel.add(productCombo);
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

        panel.add(new JLabel("Transaction:"));
        panel.add(txForCostCombo);
        panel.add(new JLabel("Amount:"));
        panel.add(costAmountField);
        panel.add(new JLabel("Description:"));
        panel.add(costDescField);
        panel.add(new JLabel("Product (optional):"));
        panel.add(costProductCombo);
        panel.add(new JLabel());
        panel.add(addCostButton);

        addCostButton.addActionListener(e -> addCostToTransaction());
        return panel;
    }

    private JPanel buildViewPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 6, 6));
        panel.setBorder(BorderFactory.createTitledBorder("View Transaction Details"));
        JButton viewButton = new JButton("View Details");

        panel.add(new JLabel("Transaction:"));
        panel.add(txForViewCombo);
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
            Customer customer = getSelectedCustomer();
            if (customer == null) {
                showError("Please select a customer from the list.");
                return;
            }
            Staff staff = app.getLoggedInUser();
            Transaction tx = business.createTransaction(date, customer.getId(), staff);
            if (tx == null) {
                showError("Failed to create transaction. Please check the date and customer.");
                return;
            }
            dateField.setText("");
            clearComboText(customerCombo);
            refresh();
            showInfo("Transaction created. Ref: " + tx.getRefCode());
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
            double quantity = InputValidator.parsePositiveDouble(quantityField.getText(), "Quantity");

            Transaction tx = getSelectedTransaction(txForProductCombo);
            if (tx == null) {
                showError("Please select a transaction from the list.");
                return;
            }
            Product product = getSelectedProduct();
            if (product == null) {
                showError("Please select a product from the list.");
                return;
            }
            boolean ok = business.addProductToTransaction(tx.getId(), product.getId(), quantity);
            if (!ok) {
                showError("Failed to add product to transaction.");
                return;
            }
            clearComboText(txForProductCombo);
            clearComboText(productCombo);
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
            double amount = InputValidator.parsePositiveDouble(costAmountField.getText(), "Amount");
            String desc = InputValidator.requireText(costDescField.getText(), "Description");

            Transaction tx = getSelectedTransaction(txForCostCombo);
            if (tx == null) {
                showError("Please select a transaction from the list.");
                return;
            }
            Cost cost;
            Product selectedProduct = getSelectedCostProduct();
            if (selectedProduct == null) {
                cost = business.addTransactionCost(tx.getId(), amount, desc);
            } else {
                cost = business.addTransactionCost(tx.getId(), amount, desc, selectedProduct.getId());
            }
            if (cost == null) {
                showError("Failed to add expense to transaction.");
                return;
            }
            clearComboText(txForCostCombo);
            costAmountField.setText("");
            costDescField.setText("");
            clearComboText(costProductCombo);
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
        Transaction tx = getSelectedTransaction(txForViewCombo);
        if (tx == null) {
            showError("Please select a transaction from the list.");
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
                sb.append("Ref: ").append(tx.getRefCode())
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
                sb.append("Ref: ").append(c.getRefCode()).append(" | ").append(c.getName())
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
                sb.append("Ref: ").append(p.getRefCode()).append(" | ").append(p.getName())
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
        sb.append("Ref: ").append(tx.getRefCode()).append("\n");
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

    private void loadComboData() {
        customerItems = buildCustomerItems();
        productItems = buildProductItems();
        transactionItems = buildTransactionItems();

        updateComboModel(customerCombo, customerItems, getComboText(customerCombo), false);
        updateComboModel(productCombo, productItems, getComboText(productCombo), false);
        updateComboModel(costProductCombo, buildCostProductItems(), getComboText(costProductCombo), false);
        updateComboModel(txForProductCombo, transactionItems, getComboText(txForProductCombo), false);
        updateComboModel(txForCostCombo, transactionItems, getComboText(txForCostCombo), false);
        updateComboModel(txForViewCombo, transactionItems, getComboText(txForViewCombo), false);
    }

    private ArrayList<ComboItem<Customer>> buildCustomerItems() {
        ArrayList<ComboItem<Customer>> items = new ArrayList<>();
        for (Customer c : business.getCustomers()) {
            items.add(new ComboItem<>(c, c.getRefCode() + " | " + c.getName()));
        }
        return items;
    }

    private ArrayList<ComboItem<Product>> buildProductItems() {
        ArrayList<ComboItem<Product>> items = new ArrayList<>();
        for (Product p : business.getProducts()) {
            items.add(new ComboItem<>(p, p.getRefCode() + " | " + p.getName()));
        }
        return items;
    }

    private ArrayList<ComboItem<Product>> buildCostProductItems() {
        ArrayList<ComboItem<Product>> items = new ArrayList<>();
        items.add(new ComboItem<>(null, "(None)"));
        items.addAll(productItems);
        return items;
    }

    private ArrayList<ComboItem<Transaction>> buildTransactionItems() {
        ArrayList<ComboItem<Transaction>> items = new ArrayList<>();
        for (Transaction tx : business.getTransactions()) {
            String label = buildTransactionLabel(tx);
            items.add(new ComboItem<>(tx, label));
        }
        return items;
    }

    private String buildTransactionLabel(Transaction tx) {
        double revenue = calculateRevenue(tx);
        String staffName = tx.getStaff() == null ? "Unassigned" : tx.getStaff().getUsername();
        return tx.getRefCode() + " | " + tx.getDate() + " | " + tx.getCustomer().getName()
                + " | Items: " + tx.getProductCount()
                + " | Rev: $" + String.format("%.2f", revenue)
                + " | Staff: " + staffName;
    }

    private <T> void installFilter(JComboBox<ComboItem<T>> combo, Supplier<List<ComboItem<T>>> sourceSupplier) {
        combo.setEditable(true);
        JTextField editor = (JTextField) combo.getEditor().getEditorComponent();
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                if (updatingCombo) {
                    return;
                }
                String text = editor.getText();
                SwingUtilities.invokeLater(() -> {
                    if (updatingCombo) {
                        return;
                    }
                    updatingCombo = true;
                    updateComboModel(combo, sourceSupplier.get(), text, true);
                    updatingCombo = false;
                });
            }
        });
    }

    private <T> void updateComboModel(JComboBox<ComboItem<T>> combo, List<ComboItem<T>> source, String filter, boolean showPopup) {
        DefaultComboBoxModel<ComboItem<T>> model = new DefaultComboBoxModel<>();
        String f = (filter == null) ? "" : filter.trim().toLowerCase();
        for (ComboItem<T> item : source) {
            if (f.isEmpty() || item.label.toLowerCase().contains(f)) {
                model.addElement(item);
            }
        }
        combo.setModel(model);
        combo.getEditor().setItem(filter == null ? "" : filter);
        if (showPopup && combo.isShowing() && model.getSize() > 0) {
            combo.setPopupVisible(true);
        }
    }

    private String getComboText(JComboBox<?> combo) {
        Object item = combo.getEditor().getItem();
        return item == null ? "" : item.toString();
    }

    private void clearComboText(JComboBox<?> combo) {
        combo.getEditor().setItem("");
    }

    private Customer getSelectedCustomer() {
        ComboItem<Customer> item = getSelectedItem(customerCombo, customerItems);
        return item == null ? null : item.value;
    }

    private Product getSelectedProduct() {
        ComboItem<Product> item = getSelectedItem(productCombo, productItems);
        return item == null ? null : item.value;
    }

    private Transaction getSelectedTransaction(JComboBox<ComboItem<Transaction>> combo) {
        ComboItem<Transaction> item = getSelectedItem(combo, transactionItems);
        return item == null ? null : item.value;
    }

    private Product getSelectedCostProduct() {
        ComboItem<Product> item = getSelectedItem(costProductCombo, buildCostProductItems());
        return item == null ? null : item.value;
    }

    private <T> ComboItem<T> getSelectedItem(JComboBox<ComboItem<T>> combo, List<ComboItem<T>> source) {
        Object selected = combo.getSelectedItem();
        if (selected instanceof ComboItem) {
            return (ComboItem<T>) selected;
        }
        Object editorItem = combo.getEditor().getItem();
        if (editorItem instanceof ComboItem) {
            return (ComboItem<T>) editorItem;
        }
        if (editorItem instanceof String) {
            String text = ((String) editorItem).trim();
            if (text.isEmpty()) {
                return null;
            }
            for (ComboItem<T> item : source) {
                if (item.label.equalsIgnoreCase(text)) {
                    return item;
                }
            }
        }
        return null;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class ComboItem<T> {
        private final T value;
        private final String label;

        private ComboItem(T value, String label) {
            this.value = value;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}
