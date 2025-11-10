package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private int id;
    private String type; // "Income" or "Expense"
    private double amount;
    private String category;
    private String description;
    private LocalDate date;
    
    private static int nextId = 1;
    
    public Transaction(String type, double amount, String category, String description, LocalDate date) {
        this.id = nextId++;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }
    
    public Transaction(int id, String type, double amount, String category, String description, LocalDate date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
        if (id >= nextId) nextId = id + 1;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    @Override
    public String toString() {
        return String.format("%s - %s: ₹%.2f (%s)", 
            date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
            type, amount, category);
    }
}


