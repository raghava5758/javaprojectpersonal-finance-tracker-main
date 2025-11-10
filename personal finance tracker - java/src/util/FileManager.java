package util;

import models.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String DATA_DIR = "data";
    private static final String TRANSACTIONS_FILE = DATA_DIR + File.separator + "transactions.txt";
    private static final String CATEGORIES_FILE = DATA_DIR + File.separator + "categories.txt";
    private static final String BUDGETS_FILE = DATA_DIR + File.separator + "budgets.txt";
    
    public static void initializeDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    // Transaction methods
    public static void saveTransactions(List<Transaction> transactions) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TRANSACTIONS_FILE))) {
            for (Transaction t : transactions) {
                writer.println(String.format("%d|%s|%.2f|%s|%s|%s",
                    t.getId(), t.getType(), t.getAmount(), t.getCategory(),
                    t.getDescription(), t.getDate().format(DateTimeFormatter.ISO_DATE)));
            }
        } catch (IOException e) {
            System.err.println("Error saving transactions: " + e.getMessage());
        }
    }
    
    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        File file = new File(TRANSACTIONS_FILE);
        if (!file.exists()) return transactions;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int maxId = 0;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) {
                    int id = Integer.parseInt(parts[0]);
                    String type = parts[1];
                    double amount = Double.parseDouble(parts[2]);
                    String category = parts[3];
                    String description = parts[4];
                    LocalDate date = LocalDate.parse(parts[5]);
                    transactions.add(new Transaction(id, type, amount, category, description, date));
                    if (id > maxId) maxId = id;
                }
            }
            // Set nextId to maxId + 1
            if (maxId > 0) {
                try {
                    java.lang.reflect.Field field = Transaction.class.getDeclaredField("nextId");
                    field.setAccessible(true);
                    field.setInt(null, maxId + 1);
                } catch (Exception e) {
                    // Ignore reflection errors
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading transactions: " + e.getMessage());
        }
        return transactions;
    }
    
    // Category methods
    public static void saveCategories(List<Category> categories) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(CATEGORIES_FILE))) {
            for (Category c : categories) {
                writer.println(c.getName() + "|" + c.getType());
            }
        } catch (IOException e) {
            System.err.println("Error saving categories: " + e.getMessage());
        }
    }
    
    public static List<Category> loadCategories() {
        List<Category> categories = new ArrayList<>();
        File file = new File(CATEGORIES_FILE);
        if (!file.exists()) {
            // Initialize with default categories
            categories.add(new Category("Salary", "Income"));
            categories.add(new Category("Freelance", "Income"));
            categories.add(new Category("Investment", "Income"));
            categories.add(new Category("Food", "Expense"));
            categories.add(new Category("Transport", "Expense"));
            categories.add(new Category("Entertainment", "Expense"));
            categories.add(new Category("Bills", "Expense"));
            categories.add(new Category("Shopping", "Expense"));
            categories.add(new Category("Healthcare", "Expense"));
            categories.add(new Category("Other", "Expense"));
            return categories;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    categories.add(new Category(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading categories: " + e.getMessage());
        }
        return categories;
    }
    
    // Budget methods
    public static void saveBudgets(List<Budget> budgets) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(BUDGETS_FILE))) {
            for (Budget b : budgets) {
                writer.println(String.format("%s|%.2f|%d|%d",
                    b.getCategory(), b.getAmount(), b.getMonth(), b.getYear()));
            }
        } catch (IOException e) {
            System.err.println("Error saving budgets: " + e.getMessage());
        }
    }
    
    public static List<Budget> loadBudgets() {
        List<Budget> budgets = new ArrayList<>();
        File file = new File(BUDGETS_FILE);
        if (!file.exists()) return budgets;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    String category = parts[0];
                    double amount = Double.parseDouble(parts[1]);
                    int month = Integer.parseInt(parts[2]);
                    int year = Integer.parseInt(parts[3]);
                    budgets.add(new Budget(category, amount, month, year));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading budgets: " + e.getMessage());
        }
        return budgets;
    }
}


