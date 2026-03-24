package ui;

import controller.ProfitManagement;
import errors.ValidationException;
import other.Customer;
import service.InputValidator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class CustomerPanel extends JPanel {
    private final MainSystemGUI app;
    private final ProfitManagement business;
    private final JTextArea output = new JTextArea(12, 50);

    private final JTextField firstNameField = new JTextField();
    private final JTextField lastNameField = new JTextField();
    private final JTextField phoneField = new JTextField();

    public CustomerPanel(MainSystemGUI app, ProfitManagement business) {
        this.app = app;
        this.business = business;

        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(4, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Add Customer"));
        form.add(new JLabel("First name:"));
        form.add(firstNameField);
        form.add(new JLabel("Last name:"));
        form.add(lastNameField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);

        JButton addButton = new JButton("Add Customer");
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

        addButton.addActionListener(e -> addCustomer());
        refreshButton.addActionListener(e -> refresh());
        backButton.addActionListener(e -> app.showScreen("menu"));
    }

    public void refresh() {
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

    private void addCustomer() {
        if (!can("CREATE_CUSTOMER")) {
            showError("You do not have permission to add customers.");
            return;
        }
        try {
            String first = InputValidator.requireLetters(firstNameField.getText(), "First name");
            String last = InputValidator.requireLetters(lastNameField.getText(), "Last name");
            String phone = InputValidator.requireDigits(phoneField.getText(), "Phone", 8, 15);

            Customer customer = business.createCustomer(first, last, phone);
            if (customer == null) {
                showError("Could not create customer. Check your input.");
                return;
            }
            clearForm();
            refresh();
            showInfo("Customer created. ID: " + customer.getId());
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private boolean can(String action) {
        return app.getLoggedInUser() != null && app.getLoggedInUser().can(action);
    }

    private void clearForm() {
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
