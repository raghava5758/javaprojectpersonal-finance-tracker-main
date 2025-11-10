package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import models.*;
import util.FinanceCalculator;

public class StatisticsPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextArea statsArea;
    private JSpinner monthSpinner;
    private JSpinner yearSpinner;
    private JButton generateButton;
    private JButton compareButton;
    
    public StatisticsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.add(new JLabel("Month:"));
        monthSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        controlPanel.add(monthSpinner);
        controlPanel.add(new JLabel("Year:"));
        yearSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        controlPanel.add(yearSpinner);
        generateButton = new JButton("Generate Stats");
        generateButton.addActionListener(e -> generateStatistics());
        controlPanel.add(generateButton);
        compareButton = new JButton("Compare Months");
        compareButton.addActionListener(e -> compareMonths());
        controlPanel.add(compareButton);
        add(controlPanel, BorderLayout.NORTH);
        
        // Create stats area
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        statsArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(statsArea);
        add(scrollPane, BorderLayout.CENTER);
        
        generateStatistics();
    }
    
    private void generateStatistics() {
        int month = (Integer) monthSpinner.getValue();
        int year = (Integer) yearSpinner.getValue();
        
        StringBuilder stats = new StringBuilder();
        stats.append("=".repeat(80)).append("\n");
        stats.append("FINANCIAL STATISTICS - ").append(getMonthName(month)).append(" ").append(year).append("\n");
        stats.append("=".repeat(80)).append("\n\n");
        
        // Current month statistics
        var currentMonthTransactions = FinanceCalculator.getTransactionsForMonth(
            mainFrame.getTransactions(), month, year);
        double currentIncome = FinanceCalculator.calculateTotalIncome(currentMonthTransactions);
        double currentExpenses = FinanceCalculator.calculateTotalExpenses(currentMonthTransactions);
        double currentBalance = currentIncome - currentExpenses;
        
        stats.append("CURRENT MONTH (").append(getMonthName(month)).append(" ").append(year).append("):\n");
        stats.append("-".repeat(80)).append("\n");
        stats.append(String.format("Total Income:     ₹%,15.2f\n", currentIncome));
        stats.append(String.format("Total Expenses:   ₹%,15.2f\n", currentExpenses));
        stats.append(String.format("Net Balance:      ₹%,15.2f\n", currentBalance));
        stats.append(String.format("Transactions:     %15d\n", currentMonthTransactions.size()));
        stats.append("\n");
        
        // Previous month comparison
        int prevMonth = month - 1;
        int prevYear = year;
        if (prevMonth < 1) {
            prevMonth = 12;
            prevYear--;
        }
        
        var prevMonthTransactions = FinanceCalculator.getTransactionsForMonth(
            mainFrame.getTransactions(), prevMonth, prevYear);
        double prevIncome = FinanceCalculator.calculateTotalIncome(prevMonthTransactions);
        double prevExpenses = FinanceCalculator.calculateTotalExpenses(prevMonthTransactions);
        double prevBalance = prevIncome - prevExpenses;
        
        stats.append("PREVIOUS MONTH (").append(getMonthName(prevMonth)).append(" ").append(prevYear).append("):\n");
        stats.append("-".repeat(80)).append("\n");
        stats.append(String.format("Total Income:     ₹%,15.2f\n", prevIncome));
        stats.append(String.format("Total Expenses:   ₹%,15.2f\n", prevExpenses));
        stats.append(String.format("Net Balance:      ₹%,15.2f\n", prevBalance));
        stats.append("\n");
        
        // Monthly comparison
        stats.append("MONTH-TO-MONTH COMPARISON:\n");
        stats.append("-".repeat(80)).append("\n");
        double incomeChange = currentIncome - prevIncome;
        double expenseChange = currentExpenses - prevExpenses;
        double balanceChange = currentBalance - prevBalance;
        
        stats.append(String.format("Income Change:    ₹%,15.2f (%s%.1f%%)\n", 
            incomeChange, 
            incomeChange >= 0 ? "+" : "",
            prevIncome > 0 ? (incomeChange / prevIncome * 100) : 0));
        stats.append(String.format("Expense Change:   ₹%,15.2f (%s%.1f%%)\n", 
            expenseChange,
            expenseChange >= 0 ? "+" : "",
            prevExpenses > 0 ? (expenseChange / prevExpenses * 100) : 0));
        stats.append(String.format("Balance Change:   ₹%,15.2f (%s%.1f%%)\n", 
            balanceChange,
            balanceChange >= 0 ? "+" : "",
            prevBalance != 0 ? (balanceChange / Math.abs(prevBalance) * 100) : 0));
        stats.append("\n");
        
        // Year-to-date statistics
        double ytdIncome = mainFrame.getTransactions().stream()
            .filter(t -> t.getType().equals("Income") && t.getDate().getYear() == year)
            .mapToDouble(Transaction::getAmount)
            .sum();
        double ytdExpenses = mainFrame.getTransactions().stream()
            .filter(t -> t.getType().equals("Expense") && t.getDate().getYear() == year)
            .mapToDouble(Transaction::getAmount)
            .sum();
        double ytdBalance = ytdIncome - ytdExpenses;
        
        stats.append("YEAR-TO-DATE (").append(year).append("):\n");
        stats.append("-".repeat(80)).append("\n");
        stats.append(String.format("Total Income:     ₹%,15.2f\n", ytdIncome));
        stats.append(String.format("Total Expenses:   ₹%,15.2f\n", ytdExpenses));
        stats.append(String.format("Net Balance:      ₹%,15.2f\n", ytdBalance));
        stats.append(String.format("Average Monthly:  ₹%,15.2f\n", ytdExpenses / month));
        stats.append("\n");
        
        // Top spending categories
        var expensesByCategory = FinanceCalculator.getExpensesByCategory(currentMonthTransactions);
        if (!expensesByCategory.isEmpty()) {
            stats.append("TOP SPENDING CATEGORIES (Current Month):\n");
            stats.append("-".repeat(80)).append("\n");
            expensesByCategory.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .forEach(entry -> {
                    double percentage = (currentExpenses > 0) ? 
                        (entry.getValue() / currentExpenses) * 100 : 0;
                    stats.append(String.format("%-25s ₹%,12.2f  (%.1f%%)\n", 
                        entry.getKey() + ":", entry.getValue(), percentage));
                });
            stats.append("\n");
        }
        
        // Budget status
        stats.append("BUDGET STATUS:\n");
        stats.append("-".repeat(80)).append("\n");
        boolean hasBudget = false;
        for (Budget budget : mainFrame.getBudgets()) {
            if (budget.getMonth() == month && budget.getYear() == year) {
                hasBudget = true;
                double actual = FinanceCalculator.getCategoryExpenseForMonth(
                    mainFrame.getTransactions(), budget.getCategory(), month, year);
                double budgetAmount = budget.getAmount();
                double percentage = (budgetAmount > 0) ? (actual / budgetAmount) * 100 : 0;
                String status = percentage <= 100 ? "✓ On Track" : "⚠ Over Budget";
                
                stats.append(String.format("%-20s Budget: ₹%,10.2f | Spent: ₹%,10.2f | %s (%.1f%%)\n",
                    budget.getCategory() + ":",
                    budgetAmount,
                    actual,
                    status,
                    percentage
                ));
            }
        }
        if (!hasBudget) {
            stats.append("No budgets set for this month.\n");
        }
        
        stats.append("\n");
        stats.append("=".repeat(80)).append("\n");
        stats.append("Generated on: ").append(LocalDate.now().toString()).append("\n");
        stats.append("=".repeat(80));
        
        statsArea.setText(stats.toString());
    }
    
    private void compareMonths() {
        int month1 = (Integer) monthSpinner.getValue();
        int year1 = (Integer) yearSpinner.getValue();
        
        // Get previous month
        int month2 = month1 - 1;
        int year2 = year1;
        if (month2 < 1) {
            month2 = 12;
            year2--;
        }
        
        StringBuilder comparison = new StringBuilder();
        comparison.append("=".repeat(80)).append("\n");
        comparison.append("MONTH COMPARISON\n");
        comparison.append("=".repeat(80)).append("\n\n");
        
        // Month 1
        var month1Transactions = FinanceCalculator.getTransactionsForMonth(
            mainFrame.getTransactions(), month1, year1);
        double month1Income = FinanceCalculator.calculateTotalIncome(month1Transactions);
        double month1Expenses = FinanceCalculator.calculateTotalExpenses(month1Transactions);
        double month1Balance = month1Income - month1Expenses;
        
        // Month 2
        var month2Transactions = FinanceCalculator.getTransactionsForMonth(
            mainFrame.getTransactions(), month2, year2);
        double month2Income = FinanceCalculator.calculateTotalIncome(month2Transactions);
        double month2Expenses = FinanceCalculator.calculateTotalExpenses(month2Transactions);
        double month2Balance = month2Income - month2Expenses;
        
        comparison.append(String.format("%-40s %-40s\n", 
            getMonthName(month1) + " " + year1, getMonthName(month2) + " " + year2));
        comparison.append("-".repeat(80)).append("\n");
        comparison.append(String.format("%-40s %-40s\n", 
            String.format("Income: ₹%,.2f", month1Income),
            String.format("Income: ₹%,.2f", month2Income)));
        comparison.append(String.format("%-40s %-40s\n", 
            String.format("Expenses: ₹%,.2f", month1Expenses),
            String.format("Expenses: ₹%,.2f", month2Expenses)));
        comparison.append(String.format("%-40s %-40s\n", 
            String.format("Balance: ₹%,.2f", month1Balance),
            String.format("Balance: ₹%,.2f", month2Balance)));
        comparison.append("\n");
        
        // Differences
        double incomeDiff = month1Income - month2Income;
        double expenseDiff = month1Expenses - month2Expenses;
        double balanceDiff = month1Balance - month2Balance;
        
        comparison.append("DIFFERENCES:\n");
        comparison.append("-".repeat(80)).append("\n");
        comparison.append(String.format("Income:   ₹%,.2f (%s%.1f%%)\n", 
            incomeDiff, incomeDiff >= 0 ? "+" : "",
            month2Income > 0 ? (incomeDiff / month2Income * 100) : 0));
        comparison.append(String.format("Expenses: ₹%,.2f (%s%.1f%%)\n", 
            expenseDiff, expenseDiff >= 0 ? "+" : "",
            month2Expenses > 0 ? (expenseDiff / month2Expenses * 100) : 0));
        comparison.append(String.format("Balance:  ₹%,.2f (%s%.1f%%)\n", 
            balanceDiff, balanceDiff >= 0 ? "+" : "",
            month2Balance != 0 ? (balanceDiff / Math.abs(month2Balance) * 100) : 0));
        
        statsArea.setText(comparison.toString());
    }
    
    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
        return months[month];
    }
    
    public void refresh() {
        generateStatistics();
    }
}

