package ui;

import user.Staff;

import javax.swing.*;
import java.awt.*;

public class MenuPanel extends JPanel {
    private final MainSystemGUI app;
    private final JLabel userLabel = new JLabel();
    private final JButton transactionsButton = new JButton("Transactions");
    private final JButton productsButton = new JButton("Products");
    private final JButton customersButton = new JButton("Customers");
    private final JButton reportsButton = new JButton("Reports");
    private final JButton usersButton = new JButton("User Management");
    private final JButton logoutButton = new JButton("Logout");

    public MenuPanel(MainSystemGUI app) {
        this.app = app;
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new BorderLayout());
        top.add(userLabel, BorderLayout.WEST);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 2, 8, 8));
        center.add(transactionsButton);
        center.add(productsButton);
        center.add(customersButton);
        center.add(reportsButton);
        center.add(usersButton);
        center.add(logoutButton);
        add(center, BorderLayout.CENTER);

        transactionsButton.addActionListener(e -> app.showScreen("transactions"));
        productsButton.addActionListener(e -> app.showScreen("products"));
        customersButton.addActionListener(e -> app.showScreen("customers"));
        reportsButton.addActionListener(e -> app.showScreen("reports"));
        usersButton.addActionListener(e -> app.showScreen("users"));
        logoutButton.addActionListener(e -> {
            app.setLoggedInUser(null);
            app.showScreen("login");
        });
    }

    public void refresh() {
        Staff user = app.getLoggedInUser();
        String name = (user == null) ? "Unknown" : user.getUsername();
        userLabel.setText("Logged in as: " + name);

        boolean canViewTransactions = user != null
                && (user.can("VIEW_TRANSACTIONS")
                || user.can("CREATE_TRANSACTION")
                || user.can("EDIT_TRANSACTION"));
        boolean canViewProducts = user != null && user.can("VIEW_PRODUCTS");
        boolean canViewCustomers = user != null && user.can("VIEW_CUSTOMERS");
        boolean canViewReports = user != null && user.can("VIEW_REPORT");
        boolean canManageUsers = user != null && user.can("MANAGE_USERS");

        transactionsButton.setEnabled(canViewTransactions);
        productsButton.setEnabled(canViewProducts);
        customersButton.setEnabled(canViewCustomers);
        reportsButton.setEnabled(canViewReports);
        usersButton.setEnabled(canManageUsers);
    }
}
