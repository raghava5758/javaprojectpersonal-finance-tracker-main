package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import models.*;

public class CategoryPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JTextField nameField;
    private JComboBox<String> typeCombo;
    private JButton addButton;
    private JButton deleteButton;
    
    public CategoryPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create form panel
        JPanel formPanel = new JPanel(new FlowLayout());
        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField(15);
        formPanel.add(nameField);
        formPanel.add(new JLabel("Type:"));
        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        formPanel.add(typeCombo);
        addButton = new JButton("Add Category");
        addButton.addActionListener(e -> addCategory());
        formPanel.add(addButton);
        add(formPanel, BorderLayout.NORTH);
        
        // Create table
        String[] columns = {"Name", "Type"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        categoryTable = new JTable(tableModel);
        categoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        categoryTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(categoryTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Delete button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        deleteButton = new JButton("Delete Category");
        deleteButton.addActionListener(e -> deleteCategory());
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);
        
        refresh();
    }
    
    private void addCategory() {
        String name = nameField.getText().trim();
        String type = (String) typeCombo.getSelectedItem();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category name", 
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if category already exists
        Category newCategory = new Category(name, type);
        if (mainFrame.getCategories().contains(newCategory)) {
            JOptionPane.showMessageDialog(this, "Category already exists!", 
                "Duplicate Category", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        mainFrame.addCategory(newCategory);
        nameField.setText("");
        JOptionPane.showMessageDialog(this, "Category added successfully!", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void deleteCategory() {
        int selectedRow = categoryTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a category to delete", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String name = (String) tableModel.getValueAt(selectedRow, 0);
        String type = (String) tableModel.getValueAt(selectedRow, 1);
        Category category = new Category(name, type);
        
        // Check if category is used in transactions
        boolean isUsed = mainFrame.getTransactions().stream()
            .anyMatch(t -> t.getCategory().equals(name));
        
        if (isUsed) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "This category is used in transactions. Delete anyway?",
                "Category in Use", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this category?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            mainFrame.removeCategory(category);
            JOptionPane.showMessageDialog(this, "Category deleted successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public void refresh() {
        tableModel.setRowCount(0);
        for (Category c : mainFrame.getCategories()) {
            tableModel.addRow(new Object[]{c.getName(), c.getType()});
        }
    }
}

