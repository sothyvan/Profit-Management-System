package ui;

import controller.ProfitManagement;
import user.Staff;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    public LoginPanel(MainSystemGUI app, ProfitManagement business) {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.anchor = GridBagConstraints.WEST;

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(14);
        JLabel passLabel = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(14);
        JButton loginButton = new JButton("Login");

        c.gridx = 0;
        c.gridy = 0;
        add(userLabel, c);
        c.gridx = 1;
        add(userField, c);

        c.gridx = 0;
        c.gridy = 1;
        add(passLabel, c);
        c.gridx = 1;
        add(passField, c);

        c.gridx = 1;
        c.gridy = 2;
        c.anchor = GridBagConstraints.EAST;
        add(loginButton, c);

        loginButton.addActionListener(e -> {
            String username = userField.getText();
            String password = new String(passField.getPassword());
            Staff user = business.authenticate(username, password);
            if (user != null) {
                app.setLoggedInUser(user);
                userField.setText("");
                passField.setText("");
                app.showScreen("menu");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid credentials.",
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
