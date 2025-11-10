package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import models.*;
import util.FinanceCalculator;

public class ReportPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextArea reportArea;
    private JSpinner monthSpinner;
    private JSpinner yearSpinner;
    private JButton generateButton;
    private JButton exportButton;
    
    public ReportPanel(MainFrame mainFrame) {
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
        generateButton = new JButton("Generate Report");
        generateButton.addActionListener(e -> generateReport());
        controlPanel.add(generateButton);
        exportButton = new JButton("Export Report");
        exportButton.addActionListener(e -> exportReport());
        controlPanel.add(exportButton);
        add(controlPanel, BorderLayout.NORTH);
        
        // Create report area
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        reportArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);
        
        refresh();
    }
    
    private void generateReport() {
        int month = (Integer) monthSpinner.getValue();
        int year = (Integer) yearSpinner.getValue();
        
        StringBuilder report = new StringBuilder();
        report.append("=".repeat(70)).append("\n");
        report.append("FINANCIAL REPORT - ").append(getMonthName(month)).append(" ").append(year).append("\n");
        report.append("=".repeat(70)).append("\n\n");
        
        // Overall statistics
        double totalIncome = FinanceCalculator.calculateTotalIncome(mainFrame.getTransactions());
        double totalExpenses = FinanceCalculator.calculateTotalExpenses(mainFrame.getTransactions());
        double balance = FinanceCalculator.calculateBalance(mainFrame.getTransactions());
        
        report.append("OVERALL STATISTICS (All Time):\n");
        report.append("-".repeat(70)).append("\n");
        report.append(String.format("Total Income:     ₹%,.2f\n", totalIncome));
        report.append(String.format("Total Expenses:   ₹%,.2f\n", totalExpenses));
        report.append(String.format("Balance:          ₹%,.2f\n", balance));
        report.append("\n");
        
        // Monthly statistics
        var monthlyTransactions = FinanceCalculator.getTransactionsForMonth(
            mainFrame.getTransactions(), month, year);
        double monthlyIncome = FinanceCalculator.calculateTotalIncome(monthlyTransactions);
        double monthlyExpenses = FinanceCalculator.calculateTotalExpenses(monthlyTransactions);
        double monthlyBalance = monthlyIncome - monthlyExpenses;
        
        report.append("MONTHLY STATISTICS (").append(getMonthName(month)).append(" ").append(year).append("):\n");
        report.append("-".repeat(70)).append("\n");
        report.append(String.format("Monthly Income:   ₹%,.2f\n", monthlyIncome));
        report.append(String.format("Monthly Expenses: ₹%,.2f\n", monthlyExpenses));
        report.append(String.format("Monthly Balance:  ₹%,.2f\n", monthlyBalance));
        report.append("\n");
        
        // Expenses by category
        var expensesByCategory = FinanceCalculator.getExpensesByCategory(monthlyTransactions);
        if (!expensesByCategory.isEmpty()) {
            report.append("EXPENSES BY CATEGORY:\n");
            report.append("-".repeat(70)).append("\n");
            expensesByCategory.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .forEach(entry -> {
                    double percentage = (monthlyExpenses > 0) ? 
                        (entry.getValue() / monthlyExpenses) * 100 : 0;
                    report.append(String.format("%-25s ₹%,10.2f  (%.1f%%)\n", 
                        entry.getKey() + ":", entry.getValue(), percentage));
                });
            report.append("\n");
        }
        
        // Budget vs Actual
        report.append("BUDGET VS ACTUAL:\n");
        report.append("-".repeat(70)).append("\n");
        boolean hasBudget = false;
        double totalBudget = 0;
        double totalActual = 0;
        
        for (Budget budget : mainFrame.getBudgets()) {
            if (budget.getMonth() == month && budget.getYear() == year) {
                hasBudget = true;
                double actual = FinanceCalculator.getCategoryExpenseForMonth(
                    mainFrame.getTransactions(), budget.getCategory(), month, year);
                double budgetAmount = budget.getAmount();
                double difference = budgetAmount - actual;
                double percentage = (budgetAmount > 0) ? (actual / budgetAmount) * 100 : 0;
                String status = difference >= 0 ? "Under" : "Over";
                String statusColor = difference >= 0 ? "✓" : "⚠";
                
                totalBudget += budgetAmount;
                totalActual += actual;
                
                report.append(String.format("%-20s Budget: ₹%,10.2f | Actual: ₹%,10.2f | %s: ₹%,10.2f (%s %.1f%%)\n",
                    budget.getCategory() + ":",
                    budgetAmount,
                    actual,
                    status,
                    Math.abs(difference),
                    statusColor,
                    percentage
                ));
            }
        }
        
        if (!hasBudget) {
            report.append("No budgets set for this month.\n");
        } else {
            report.append("-".repeat(70)).append("\n");
            double totalDifference = totalBudget - totalActual;
            String totalStatus = totalDifference >= 0 ? "Under" : "Over";
            double totalPercentage = (totalBudget > 0) ? (totalActual / totalBudget) * 100 : 0;
            report.append(String.format("%-20s Budget: ₹%,10.2f | Actual: ₹%,10.2f | %s: ₹%,10.2f (%.1f%%)\n",
                "TOTAL:",
                totalBudget,
                totalActual,
                totalStatus,
                Math.abs(totalDifference),
                totalPercentage
            ));
        }
        
        report.append("\n");
        report.append("=".repeat(70)).append("\n");
        report.append("Report generated on: ").append(LocalDate.now().toString()).append("\n");
        report.append("=".repeat(70));
        
        reportArea.setText(report.toString());
    }
    
    private void exportReport() {
        String report = reportArea.getText();
        if (report.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a report first", 
                "No Report", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setSelectedFile(new java.io.File("financial_report_" + 
            LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".txt"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            try (java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.FileWriter(file))) {
                writer.print(report);
                JOptionPane.showMessageDialog(this, "Report exported successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
        return months[month];
    }
    
    public void refresh() {
        generateReport();
    }
}

