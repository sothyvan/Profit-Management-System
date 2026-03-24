package ui;

import controller.ProfitManagement;
import errors.ValidationException;
import service.InputValidator;

import javax.swing.*;
import java.awt.*;

public class ReportPanel extends JPanel {
    private final MainSystemGUI app;
    private final ProfitManagement business;
    private final JTextArea output = new JTextArea(14, 50);

    private final JTextField dateField = new JTextField();
    private final JTextField monthField = new JTextField();
    private final JTextField yearField = new JTextField();

    public ReportPanel(MainSystemGUI app, ProfitManagement business) {
        this.app = app;
        this.business = business;

        setLayout(new BorderLayout(8, 8));

        JPanel form = new JPanel(new GridLayout(4, 3, 6, 6));
        form.setBorder(BorderFactory.createTitledBorder("Reports"));

        JButton dailyButton = new JButton("Daily");
        JButton monthlyButton = new JButton("Monthly");
        JButton yearlyButton = new JButton("Yearly");
        JButton allTimeButton = new JButton("All-Time");

        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);
        form.add(dailyButton);
        form.add(new JLabel("Month (YYYY-MM):"));
        form.add(monthField);
        form.add(monthlyButton);
        form.add(new JLabel("Year (YYYY):"));
        form.add(yearField);
        form.add(yearlyButton);
        form.add(new JLabel());
        form.add(new JLabel());
        form.add(allTimeButton);

        output.setEditable(false);
        add(form, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        dailyButton.addActionListener(e -> showDailyReport());
        monthlyButton.addActionListener(e -> showMonthlyReport());
        yearlyButton.addActionListener(e -> showYearlyReport());
        allTimeButton.addActionListener(e -> showAllTimeReport());
    }

    public void refresh() {
        output.setText("Choose a report type.");
    }

    private JPanel buildBottomPanel() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> app.showScreen("menu"));
        bottom.add(backButton);
        return bottom;
    }

    private void showDailyReport() {
        if (!can("VIEW_REPORT")) {
            showError("You do not have permission to view reports.");
            return;
        }
        try {
            String date = InputValidator.parseDate(dateField.getText());
            int count = business.getDailyTransactionCount(date);
            if (count == 0) {
                output.setText("No transactions found for " + date + ".");
                return;
            }
            double revenue = business.getDailyRevenue(date);
            double expenses = business.getDailyExpenses(date);
            double profit = business.getDailyProfit(date);
            double margin = business.getDailyMargin(date);
            output.setText(formatReport("DAILY REPORT", date, count, revenue, expenses, profit, margin));
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void showMonthlyReport() {
        if (!can("VIEW_REPORT")) {
            showError("You do not have permission to view reports.");
            return;
        }
        try {
            String month = InputValidator.parseYearMonth(monthField.getText());
            int count = business.getMonthlyTransactionCount(month);
            if (count == 0) {
                output.setText("No transactions found for " + month + ".");
                return;
            }
            double revenue = business.getMonthlyRevenue(month);
            double expenses = business.getMonthlyExpenses(month);
            double profit = business.getMonthlyProfit(month);
            double margin = business.getMonthlyMargin(month);
            output.setText(formatReport("MONTHLY REPORT", month, count, revenue, expenses, profit, margin));
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void showYearlyReport() {
        if (!can("VIEW_REPORT")) {
            showError("You do not have permission to view reports.");
            return;
        }
        try {
            String year = InputValidator.parseYear(yearField.getText());
            int count = business.getYearlyTransactionCount(year);
            if (count == 0) {
                output.setText("No transactions found for " + year + ".");
                return;
            }
            double revenue = business.getYearlyRevenue(year);
            double expenses = business.getYearlyExpenses(year);
            double profit = business.getYearlyProfit(year);
            double margin = business.getYearlyMargin(year);
            output.setText(formatReport("YEARLY REPORT", year, count, revenue, expenses, profit, margin));
        } catch (ValidationException ex) {
            showError(ex.getMessage());
        }
    }

    private void showAllTimeReport() {
        if (!can("VIEW_REPORT")) {
            showError("You do not have permission to view reports.");
            return;
        }
        int count = business.getTransactionCount();
        if (count == 0) {
            output.setText("No transactions found.");
            return;
        }
        double revenue = business.calculateTotalRevenue();
        double expenses = business.calculateTotalExpenses();
        double profit = business.calculateTotalProfit();
        double margin = business.calculateOverallMargin();
        output.setText(formatReport("ALL-TIME REPORT", "All Time", count, revenue, expenses, profit, margin));
    }

    private String formatReport(String title, String period, int count,
                                double revenue, double expenses, double profit, double margin) {
        StringBuilder sb = new StringBuilder();
        sb.append(title).append("\n");
        sb.append("Period: ").append(period).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("Transactions: %d%n", count));
        sb.append(String.format("Revenue: $%.2f%n", revenue));
        sb.append(String.format("Expenses: $%.2f%n", expenses));
        sb.append(String.format("Profit: $%.2f%n", profit));
        sb.append(String.format("Profit Margin: %.2f%%%n", margin));
        return sb.toString();
    }

    private boolean can(String action) {
        return app.getLoggedInUser() != null && app.getLoggedInUser().can(action);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
