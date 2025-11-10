package gui;

import javax.swing.*;
import java.util.List;
import models.*;
import util.FileManager;

public class MainFrame extends JFrame {
    private List<Transaction> transactions;
    private List<Category> categories;
    private List<Budget> budgets;
    
    private JTabbedPane tabbedPane;
    private TransactionPanel transactionPanel;
    private CategoryPanel categoryPanel;
    private BudgetPanel budgetPanel;
    private ReportPanel reportPanel;
    private StatisticsPanel statisticsPanel;
    
    public MainFrame() {
        setTitle("Personal Finance Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Initialize data
        FileManager.initializeDataDirectory();
        transactions = FileManager.loadTransactions();
        categories = FileManager.loadCategories();
        budgets = FileManager.loadBudgets();
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create panels
        transactionPanel = new TransactionPanel(this);
        categoryPanel = new CategoryPanel(this);
        budgetPanel = new BudgetPanel(this);
        reportPanel = new ReportPanel(this);
        statisticsPanel = new StatisticsPanel(this);
        
        // Add tabs
        tabbedPane.addTab("Transactions", transactionPanel);
        tabbedPane.addTab("Categories", categoryPanel);
        tabbedPane.addTab("Budgets", budgetPanel);
        tabbedPane.addTab("Reports", reportPanel);
        tabbedPane.addTab("Statistics", statisticsPanel);
        
        add(tabbedPane);
        
        // Save data on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveAllData();
                System.exit(0);
            }
        });
    }
    
    public List<Transaction> getTransactions() {
        return transactions;
    }
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public List<Budget> getBudgets() {
        return budgets;
    }
    
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        saveAllData();
        refreshPanels();
    }
    
    public void removeTransaction(Transaction transaction) {
        transactions.remove(transaction);
        saveAllData();
        refreshPanels();
    }
    
    public void updateTransaction(Transaction oldTransaction, Transaction newTransaction) {
        int index = transactions.indexOf(oldTransaction);
        if (index >= 0) {
            transactions.set(index, newTransaction);
            saveAllData();
            refreshPanels();
        }
    }
    
    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
            saveAllData();
            refreshPanels();
        }
    }
    
    public void removeCategory(Category category) {
        categories.remove(category);
        saveAllData();
        refreshPanels();
    }
    
    public void addBudget(Budget budget) {
        budgets.add(budget);
        saveAllData();
        refreshPanels();
    }
    
    public void removeBudget(Budget budget) {
        budgets.remove(budget);
        saveAllData();
        refreshPanels();
    }
    
    private void saveAllData() {
        FileManager.saveTransactions(transactions);
        FileManager.saveCategories(categories);
        FileManager.saveBudgets(budgets);
    }
    
    public void refreshPanels() {
        transactionPanel.refresh();
        categoryPanel.refresh();
        budgetPanel.refresh();
        reportPanel.refresh();
        statisticsPanel.refresh();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}


