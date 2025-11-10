package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import models.*;

public class BudgetPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable budgetTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private JSpinner monthSpinner;
    private JSpinner yearSpinner;
    private JButton addButton;
    private JButton deleteButton;
    
    public BudgetPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        formPanel.add(categoryCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        formPanel.add(amountField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Month:"), gbc);
        gbc.gridx = 1;
        monthSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        formPanel.add(monthSpinner, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        yearSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        formPanel.add(yearSpinner, gbc);
        
        addButton = new JButton("Add Budget");
        addButton.addActionListener(e -> addBudget());
        gbc.gridx = 1; gbc.gridy = 4;
        formPanel.add(addButton, gbc);
        
        add(formPanel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"Category", "Amount", "Month", "Year"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        budgetTable = new JTable(tableModel);
        budgetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        budgetTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(budgetTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Delete button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        deleteButton = new JButton("Delete Budget");
        deleteButton.addActionListener(e -> deleteBudget());
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refresh();
    }
    
    private void updateCategoryCombo() {
        categoryCombo.removeAllItems();
        mainFrame.getCategories().stream()
            .filter(c -> c.getType().equals("Expense"))
            .forEach(c -> categoryCombo.addItem(c.getName()));
    }
    
    private void addBudget() {
        try {
            String category = (String) categoryCombo.getSelectedItem();
            if (category == null || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a category", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than zero", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int month = (Integer) monthSpinner.getValue();
            int year = (Integer) yearSpinner.getValue();
            
            // Check if budget already exists for this category, month, and year
            Budget newBudget = new Budget(category, amount, month, year);
            boolean exists = mainFrame.getBudgets().stream()
                .anyMatch(b -> b.getCategory().equals(category) && 
                              b.getMonth() == month && 
                              b.getYear() == year);
            
            if (exists) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Budget already exists for this category and period. Replace it?",
                    "Duplicate Budget", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Remove existing budget
                    mainFrame.getBudgets().removeIf(b -> 
                        b.getCategory().equals(category) && 
                        b.getMonth() == month && 
                        b.getYear() == year);
                    mainFrame.addBudget(newBudget);
                    amountField.setText("");
                    JOptionPane.showMessageDialog(this, "Budget updated successfully!", 
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                mainFrame.addBudget(newBudget);
                amountField.setText("");
                JOptionPane.showMessageDialog(this, "Budget added successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format. Please enter a valid number.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteBudget() {
        int selectedRow = budgetTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a budget to delete", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String category = (String) tableModel.getValueAt(selectedRow, 0);
        int month = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
        int year = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());
        
        Budget budgetToDelete = mainFrame.getBudgets().stream()
            .filter(b -> b.getCategory().equals(category) && 
                        b.getMonth() == month && 
                        b.getYear() == year)
            .findFirst()
            .orElse(null);
        
        if (budgetToDelete != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this budget?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.removeBudget(budgetToDelete);
                JOptionPane.showMessageDialog(this, "Budget deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    public void refresh() {
        tableModel.setRowCount(0);
        for (Budget b : mainFrame.getBudgets()) {
            tableModel.addRow(new Object[]{
                b.getCategory(),
                String.format("₹%.2f", b.getAmount()),
                String.valueOf(b.getMonth()),
                String.valueOf(b.getYear())
            });
        }
        updateCategoryCombo();
    }
}

