package ui;

import controller.ProfitManagement;
import errors.ValidationException;
import service.InputValidator;
import user.Staff;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class UserPanel extends JPanel {
    private final MainSystemGUI app;
    private final ProfitManagement business;
    private final JTextArea output = new JTextArea(12, 50);

    private final JTextField roleField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField usernameField = new JTextField();
    private final JTextField passwordField = new JTextField();
    private final JTextField salaryField = new JTextField();

    private final JTextField removeUsernameField = new JTextField();

    public UserPanel(MainSystemGUI app, ProfitManagement business) {
        this.app = app;
        this.business = business;

        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(7, 2, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Add User"));
        form.add(new JLabel("Role (Manager/Cashier):"));
        form.add(roleField);
        form.add(new JLabel("Full name:"));
        form.add(fullNameField);
        form.add(new JLabel("Phone:"));
        form.add(phoneField);
        form.add(new JLabel("Username:"));
        form.add(usernameField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Salary:"));
        form.add(salaryField);

        JButton addButton = new JButton("Add User");
        JButton refreshButton = new JButton("Refresh List");
        form.add(addButton);
        form.add(refreshButton);

        JPanel removePanel = new JPanel(new GridLayout(1, 3, 6, 6));
        removePanel.setBorder(BorderFactory.createTitledBorder("Remove User"));
        JButton removeButton = new JButton("Remove");
        removePanel.add(new JLabel("Username:"));
        removePanel.add(removeUsernameField);
        removePanel.add(removeButton);

        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.add(form, BorderLayout.NORTH);
        top.add(removePanel, BorderLayout.SOUTH);

        output.setEditable(false);
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        addButton.addActionListener(e -> addUser());
        refreshButton.addActionListener(e -> refresh());
        removeButton.addActionListener(e -> removeUser());
    }

    public void refresh() {
        ArrayList<Staff> users = business.getStaffMembers();
        StringBuilder sb = new StringBuilder();
        sb.append("Users\n");
        sb.append("----------------------------------------\n");
        if (users.isEmpty()) {
            sb.append("No users found.\n");
        } else {
            for (Staff u : users) {
                sb.append("ID: ").append(u.getStaffId())
                        .append(" | ").append(u.getFullName())
                        .append(" | Username: ").append(u.getUsername())
                        .append(" | Phone: ").append(u.getPhone())
                        .append(" | Active: ").append(u.isActive())
                        .append("\n");
            }
        }
        output.setText(sb.toString());
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.showScreen("menu"));
        bottom.add(backButton);
        return bottom;
    }

    private void addUser() {
        if (!can("MANAGE_USERS")) {
            showError("You do not have permission to manage users.");
            return;
        }
        try {
            String role = InputValidator.normalizeRole(roleField.getText());
            String fullName = InputValidator.requireLetters(fullNameField.getText(), "Full name");
            String phone = InputValidator.requireDigits(phoneField.getText(), "Phone", 8, 15);
            String username = InputValidator.requireUsername(usernameField.getText(), "Username");
            String password = InputValidator.requireText(passwordField.getText(), "Password");
            double salary = InputValidator.parseNonNegativeDouble(salaryField.getText(), "Salary");
            InputValidator.validateSalaryForRole(role, salary);

            Staff created = business.createStaff(role, fullName, phone, username, password, salary);
            if (created == null) {
                showError("Could not create user. Check input or duplicate username.");
                return;
            }
            clearAddForm();
            refresh();
            showInfo("User created: " + created.getUsername());
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void removeUser() {
        if (!can("MANAGE_USERS")) {
            showError("You do not have permission to manage users.");
            return;
        }
        String username = removeUsernameField.getText();
        if (username == null || username.trim().isEmpty()) {
            showError("Username is required.");
            return;
        }
        Staff loggedIn = app.getLoggedInUser();
        if (loggedIn != null && loggedIn.getUsername().equals(username.trim())) {
            showError("You cannot remove your own account while logged in.");
            return;
        }
        boolean removed = business.removeStaffByUsername(username.trim());
        if (!removed) {
            showError("Could not remove user. Check username or manager count.");
            return;
        }
        removeUsernameField.setText("");
        refresh();
        showInfo("User removed: " + username.trim());
    }

    private boolean can(String action) {
        return app.getLoggedInUser() != null && app.getLoggedInUser().can(action);
    }

    private void clearAddForm() {
        roleField.setText("");
        fullNameField.setText("");
        phoneField.setText("");
        usernameField.setText("");
        passwordField.setText("");
        salaryField.setText("");
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }
}
