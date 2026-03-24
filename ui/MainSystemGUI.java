package ui;

import controller.ProfitManagement;
import user.Staff;

import javax.swing.*;
import java.awt.*;

public class MainSystemGUI extends JFrame {
    private final ProfitManagement business = new ProfitManagement();
    private Staff loggedInUser;
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);

    private final LoginPanel loginPanel;
    private final MenuPanel menuPanel;
    private final TransactionPanel transactionPanel;
    private final ProductPanel productPanel;
    private final CustomerPanel customerPanel;
    private final ReportPanel reportPanel;
    private final UserPanel userPanel;

    public MainSystemGUI() {
        setTitle("Profit Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        business.loadDefaultUsers();
        business.loadSampleData();

        loginPanel = new LoginPanel(this, business);
        menuPanel = new MenuPanel(this);
        transactionPanel = new TransactionPanel(this, business);
        productPanel = new ProductPanel(this, business);
        customerPanel = new CustomerPanel(this, business);
        reportPanel = new ReportPanel(this, business);
        userPanel = new UserPanel(this, business);

        root.add(loginPanel, "login");
        root.add(menuPanel, "menu");
        root.add(transactionPanel, "transactions");
        root.add(productPanel, "products");
        root.add(customerPanel, "customers");
        root.add(reportPanel, "reports");
        root.add(userPanel, "users");

        setContentPane(root);
        showScreen("login");

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public ProfitManagement getBusiness() {
        return business;
    }

    public Staff getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(Staff loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void showScreen(String name) {
        if ("menu".equals(name)) {
            menuPanel.refresh();
        } else if ("transactions".equals(name)) {
            transactionPanel.refresh();
        } else if ("products".equals(name)) {
            productPanel.refresh();
        } else if ("customers".equals(name)) {
            customerPanel.refresh();
        } else if ("reports".equals(name)) {
            reportPanel.refresh();
        } else if ("users".equals(name)) {
            userPanel.refresh();
        }
        cards.show(root, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainSystemGUI::new);
    }
}
