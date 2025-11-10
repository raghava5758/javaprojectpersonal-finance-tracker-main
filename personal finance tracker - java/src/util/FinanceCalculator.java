package util;

import models.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceCalculator {
    
    public static double calculateTotalIncome(List<Transaction> transactions) {
        return transactions.stream()
            .filter(t -> t.getType().equals("Income"))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public static double calculateTotalExpenses(List<Transaction> transactions) {
        return transactions.stream()
            .filter(t -> t.getType().equals("Expense"))
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public static double calculateBalance(List<Transaction> transactions) {
        return calculateTotalIncome(transactions) - calculateTotalExpenses(transactions);
    }
    
    public static Map<String, Double> getExpensesByCategory(List<Transaction> transactions) {
        return transactions.stream()
            .filter(t -> t.getType().equals("Expense"))
            .collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)
            ));
    }
    
    public static double getCategoryExpenseForMonth(List<Transaction> transactions, 
                                                   String category, int month, int year) {
        return transactions.stream()
            .filter(t -> t.getType().equals("Expense"))
            .filter(t -> t.getCategory().equals(category))
            .filter(t -> {
                LocalDate date = t.getDate();
                return date.getMonthValue() == month && date.getYear() == year;
            })
            .mapToDouble(Transaction::getAmount)
            .sum();
    }
    
    public static List<Transaction> getTransactionsForMonth(List<Transaction> transactions, 
                                                           int month, int year) {
        return transactions.stream()
            .filter(t -> {
                LocalDate date = t.getDate();
                return date.getMonthValue() == month && date.getYear() == year;
            })
            .collect(Collectors.toList());
    }
}


