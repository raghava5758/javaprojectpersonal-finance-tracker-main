package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import models.*;

public class TransactionPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> typeCombo;
    private JTextField amountField;
    private JComboBox<String> categoryCombo;
    private JTextField descriptionField;
    private JTextField dateField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    // Search and Filter components
    private JTextField searchField;
    private JComboBox<String> filterTypeCombo;
    private JComboBox<String> filterCategoryCombo;
    private JTextField dateFromField;
    private JTextField dateToField;
    private JButton searchButton;
    private JButton clearFilterButton;
    private JButton exportCSVButton;
    private JLabel statusLabel;
    
    public TransactionPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        
        // Create search/filter panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.add(formPanel, BorderLayout.NORTH);
        add(formContainer, BorderLayout.WEST);
        
        // Create table
        String[] columns = {"ID", "Type", "Amount", "Category", "Description", "Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transactionTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        transactionTable.setRowSorter(sorter);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(25);
        transactionTable.setAutoCreateRowSorter(true);
        
        // Color coding for income (green) and expenses (red)
        transactionTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    int modelRow = table.convertRowIndexToModel(row);
                    String type = (String) tableModel.getValueAt(modelRow, 1);
                    if ("Income".equals(type)) {
                        c.setBackground(new Color(200, 255, 200)); // Light green
                    } else if ("Expense".equals(type)) {
                        c.setBackground(new Color(255, 200, 200)); // Light red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        addButton = new JButton("Add Transaction");
        editButton = new JButton("Edit Transaction");
        deleteButton = new JButton("Delete Transaction");
        exportCSVButton = new JButton("Export CSV");
        
        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        exportCSVButton.addActionListener(e -> exportToCSV());
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL));
        buttonPanel.add(exportCSVButton);
        
        // Status label
        statusLabel = new JLabel("Total Transactions: 0");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(statusLabel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        
        refresh();
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Search field
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Search:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by description or category");
        panel.add(searchField, gbc);
        
        // Filter Type
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 3;
        filterTypeCombo = new JComboBox<>(new String[]{"All", "Income", "Expense"});
        panel.add(filterTypeCombo, gbc);
        
        // Filter Category
        gbc.gridx = 4;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 5;
        filterCategoryCombo = new JComboBox<>();
        filterCategoryCombo.addItem("All");
        panel.add(filterCategoryCombo, gbc);
        
        // Date From
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Date From:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateFromField = new JTextField(10);
        dateFromField.setToolTipText("YYYY-MM-DD (leave empty for all)");
        panel.add(dateFromField, gbc);
        
        // Date To
        gbc.gridx = 2; gbc.weightx = 0;
        panel.add(new JLabel("Date To:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        dateToField = new JTextField(10);
        dateToField.setToolTipText("YYYY-MM-DD (leave empty for all)");
        panel.add(dateToField, gbc);
        
        // Buttons
        gbc.gridx = 4; gbc.gridy = 1; gbc.weightx = 0;
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> applyFilters());
        panel.add(searchButton, gbc);
        
        gbc.gridx = 5;
        clearFilterButton = new JButton("Clear");
        clearFilterButton.addActionListener(e -> clearFilters());
        panel.add(clearFilterButton, gbc);
        
        // Update category filter when categories change
        updateFilterCategoryCombo();
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add/Edit Transaction"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        typeCombo = new JComboBox<>(new String[]{"Income", "Expense"});
        panel.add(typeCombo, gbc);
        
        // Amount
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Amount (₹):"), gbc);
        gbc.gridx = 1;
        amountField = new JTextField(15);
        panel.add(amountField, gbc);
        
        // Category
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        categoryCombo = new JComboBox<>();
        panel.add(categoryCombo, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionField = new JTextField(15);
        panel.add(descriptionField, gbc);
        
        // Date
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        dateField = new JTextField(15);
        dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        panel.add(dateField, gbc);
        
        // Update categories when type changes
        typeCombo.addActionListener(e -> updateCategoryCombo());
        
        return panel;
    }
    
    private void updateCategoryCombo() {
        String selectedType = (String) typeCombo.getSelectedItem();
        categoryCombo.removeAllItems();
        mainFrame.getCategories().stream()
            .filter(c -> c.getType().equals(selectedType))
            .forEach(c -> categoryCombo.addItem(c.getName()));
    }
    
    private void updateFilterCategoryCombo() {
        filterCategoryCombo.removeAllItems();
        filterCategoryCombo.addItem("All");
        mainFrame.getCategories().stream()
            .map(Category::getName)
            .distinct()
            .sorted()
            .forEach(filterCategoryCombo::addItem);
    }
    
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String filterType = (String) filterTypeCombo.getSelectedItem();
        String filterCategory = (String) filterCategoryCombo.getSelectedItem();
        String dateFrom = dateFromField.getText().trim();
        String dateTo = dateToField.getText().trim();
        
        List<Transaction> filtered = mainFrame.getTransactions().stream()
            .filter(t -> {
                // Search filter
                if (!searchText.isEmpty()) {
                    boolean matchesSearch = t.getDescription().toLowerCase().contains(searchText) ||
                                         t.getCategory().toLowerCase().contains(searchText);
                    if (!matchesSearch) return false;
                }
                
                // Type filter
                if (!"All".equals(filterType)) {
                    if (!t.getType().equals(filterType)) return false;
                }
                
                // Category filter
                if (!"All".equals(filterCategory)) {
                    if (!t.getCategory().equals(filterCategory)) return false;
                }
                
                // Date range filter
                if (!dateFrom.isEmpty()) {
                    try {
                        LocalDate fromDate = LocalDate.parse(dateFrom);
                        if (t.getDate().isBefore(fromDate)) return false;
                    } catch (Exception e) {
                        // Invalid date format, ignore filter
                    }
                }
                
                if (!dateTo.isEmpty()) {
                    try {
                        LocalDate toDate = LocalDate.parse(dateTo);
                        if (t.getDate().isAfter(toDate)) return false;
                    } catch (Exception e) {
                        // Invalid date format, ignore filter
                    }
                }
                
                return true;
            })
            .collect(Collectors.toList());
        
        displayTransactions(filtered);
        statusLabel.setText("Filtered: " + filtered.size() + " / Total: " + mainFrame.getTransactions().size());
    }
    
    private void clearFilters() {
        searchField.setText("");
        filterTypeCombo.setSelectedIndex(0);
        filterCategoryCombo.setSelectedIndex(0);
        dateFromField.setText("");
        dateToField.setText("");
        refresh();
    }
    
    private void addTransaction() {
        try {
            String type = (String) typeCombo.getSelectedItem();
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
            String category = (String) categoryCombo.getSelectedItem();
            if (category == null || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a category", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                description = "No description";
            }
            LocalDate date = LocalDate.parse(dateField.getText());
            
            Transaction transaction = new Transaction(type, amount, category, description, date);
            mainFrame.addTransaction(transaction);
            
            // Clear fields
            amountField.setText("");
            descriptionField.setText("");
            dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            
            JOptionPane.showMessageDialog(this, "Transaction added successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format. Please enter a valid number.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to edit", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = transactionTable.convertRowIndexToModel(selectedRow);
        int id = (Integer) tableModel.getValueAt(modelRow, 0);
        Transaction oldTransaction = mainFrame.getTransactions().stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .orElse(null);
        
        if (oldTransaction == null) return;
        
        // Populate form with selected transaction
        typeCombo.setSelectedItem(oldTransaction.getType());
        amountField.setText(String.valueOf(oldTransaction.getAmount()));
        descriptionField.setText(oldTransaction.getDescription());
        dateField.setText(oldTransaction.getDate().format(DateTimeFormatter.ISO_DATE));
        updateCategoryCombo();
        categoryCombo.setSelectedItem(oldTransaction.getCategory());
        
        try {
            String type = (String) typeCombo.getSelectedItem();
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
            String category = (String) categoryCombo.getSelectedItem();
            if (category == null || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a category", 
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String description = descriptionField.getText().trim();
            if (description.isEmpty()) {
                description = "No description";
            }
            LocalDate date = LocalDate.parse(dateField.getText());
            
            Transaction newTransaction = new Transaction(oldTransaction.getId(), type, 
                amount, category, description, date);
            mainFrame.updateTransaction(oldTransaction, newTransaction);
            
            // Clear fields
            amountField.setText("");
            descriptionField.setText("");
            dateField.setText(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
            
            JOptionPane.showMessageDialog(this, "Transaction updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format. Please enter a valid number.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteTransaction() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction to delete", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = transactionTable.convertRowIndexToModel(selectedRow);
        int id = (Integer) tableModel.getValueAt(modelRow, 0);
        Transaction transaction = mainFrame.getTransactions().stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .orElse(null);
        
        if (transaction != null) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this transaction?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.removeTransaction(transaction);
                JOptionPane.showMessageDialog(this, "Transaction deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Transactions to CSV");
        fileChooser.setSelectedFile(new File("transactions_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // Write header
                writer.println("ID,Type,Amount,Category,Description,Date");
                
                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    int modelRow = transactionTable.convertRowIndexToModel(i);
                    writer.println(String.format("%d,%s,%s,%s,\"%s\",%s",
                        tableModel.getValueAt(modelRow, 0),
                        tableModel.getValueAt(modelRow, 1),
                        tableModel.getValueAt(modelRow, 2).toString().replace("₹", "").replace(",", ""),
                        tableModel.getValueAt(modelRow, 3),
                        tableModel.getValueAt(modelRow, 4),
                        tableModel.getValueAt(modelRow, 5)
                    ));
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Transactions exported successfully to:\n" + file.getAbsolutePath(),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void displayTransactions(List<Transaction> transactions) {
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{
                t.getId(),
                t.getType(),
                String.format("₹%.2f", t.getAmount()),
                t.getCategory(),
                t.getDescription(),
                t.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            });
        }
    }
    
    public void refresh() {
        displayTransactions(mainFrame.getTransactions());
        updateCategoryCombo();
        updateFilterCategoryCombo();
        statusLabel.setText("Total Transactions: " + mainFrame.getTransactions().size());
    }
}
