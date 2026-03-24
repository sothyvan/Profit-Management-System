package ui;

import controller.ProfitManagement;
import errors.ValidationException;
import other.Product;
import service.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ProductPanel extends JPanel {
    private final MainSystemGUI app;
    private final ProfitManagement business;
    private final JTextArea output = new JTextArea(12, 50);

    private final JTextField nameField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField costField = new JTextField();

    public ProductPanel(MainSystemGUI app, ProfitManagement business) {
        this.app = app;
        this.business = business;

        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(4, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Add Product"));
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Price:"));
        form.add(priceField);
        form.add(new JLabel("Production cost:"));
        form.add(costField);

        JButton addButton = new JButton("Add Product");
        JButton refreshButton = new JButton("Refresh List");
        JButton backButton = new JButton("Back");
        form.add(addButton);
        form.add(refreshButton);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(backButton);

        output.setEditable(false);
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addProduct());
        refreshButton.addActionListener(e -> refresh());
        backButton.addActionListener(e -> app.showScreen("menu"));
    }

    public void refresh() {
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
                        .append(" | Cost: $").append(p.getProductionCost())
                        .append("\n");
            }
        }
        output.setText(sb.toString());
    }

    private void addProduct() {
        if (!can("CREATE_PRODUCT")) {
            showError("You do not have permission to add products.");
            return;
        }
        try {
            String name = InputValidator.requireText(nameField.getText(), "Product name");
            double price = InputValidator.parsePositiveDouble(priceField.getText(), "Price");
            double cost = InputValidator.parseNonNegativeDouble(costField.getText(), "Production cost");

            Product product = business.createProduct(name, price, cost);
            if (product == null) {
                showError("Could not create product. Check your input.");
                return;
            }
            clearForm();
            refresh();
            showInfo("Product created. ID: " + product.getId());
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private boolean can(String action) {
        return app.getLoggedInUser() != null && app.getLoggedInUser().can(action);
    }

    private void clearForm() {
        nameField.setText("");
        priceField.setText("");
        costField.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
