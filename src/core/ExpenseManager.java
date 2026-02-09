package core;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ExpenseManager {
    private Database db;
    private String currentUsername;
    
    public ExpenseManager(String username) {
        this.db = Database.getInstance();
        this.currentUsername = username;
    }
    
    public boolean addExpense(String title, double amount, int categoryId, 
                             LocalDate date, String description) {
        Category category = db.getCategoryById(categoryId);
        if (category == null) {
            return false;
        }
        
        // Generate unique ID
        List<Expense> expenses = db.getUserExpenses(currentUsername);
        int newId = expenses.stream()
            .mapToInt(Expense::getId)
            .max()
            .orElse(0) + 1;
        
        Expense expense = new Expense(newId, currentUsername, title, amount, category, date);
        expense.setDescription(description);
        
        db.saveExpense(expense);
        return true;
    }
    
    public boolean updateExpense(int expenseId, String title, double amount, 
                                int categoryId, LocalDate date, String description) {
        List<Expense> expenses = db.getUserExpenses(currentUsername);
        for (Expense expense : expenses) {
            if (expense.getId() == expenseId) {
                expense.setTitle(title);
                expense.setAmount(amount);
                expense.setCategory(db.getCategoryById(categoryId));
                expense.setDate(date);
                expense.setDescription(description);
                
                // Save all expenses back
                List<Expense> allExpenses = db.getUserExpenses(currentUsername);
                allExpenses.removeIf(e -> e.getId() == expenseId);
                allExpenses.add(expense);
                
                // Update in database
                // (This is simplified - in real app, we'd update the specific expense)
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteExpense(int expenseId) {
        return db.deleteExpense(expenseId, currentUsername);
    }
    
    public List<Expense> getExpenses() {
        return db.getUserExpenses(currentUsername);
    }
    
    public List<Expense> getExpensesByCategory(int categoryId) {
        return getExpenses().stream()
            .filter(e -> e.getCategory().getId() == categoryId)
            .collect(Collectors.toList());
    }
    
    public List<Expense> getExpensesByMonth(int year, int month) {
        return getExpenses().stream()
            .filter(e -> {
                LocalDate expenseDate = e.getDate();
                return expenseDate.getYear() == year && 
                       expenseDate.getMonthValue() == month;
            })
            .collect(Collectors.toList());
    }
    
    public double getTotalExpenses() {
        return getExpenses().stream()
            .mapToDouble(Expense::getAmount)
            .sum();
    }
    
    public double getMonthlyTotal(int year, int month) {
        return getExpensesByMonth(year, month).stream()
            .mapToDouble(Expense::getAmount)
            .sum();
    }
    
    public Map<Category, Double> getCategoryWiseExpenses() {
        Map<Category, Double> categoryTotals = new HashMap<>();
        
        for (Expense expense : getExpenses()) {
            Category category = expense.getCategory();
            categoryTotals.merge(category, expense.getAmount(), Double::sum);
        }
        
        return categoryTotals;
    }
    
    public Map<String, Double> getDailyExpenses(int year, int month) {
        Map<String, Double> dailyTotals = new TreeMap<>();
        
        getExpensesByMonth(year, month).forEach(expense -> {
            String day = expense.getDate().toString();
            dailyTotals.merge(day, expense.getAmount(), Double::sum);
        });
        
        return dailyTotals;
    }
    
    public List<Expense> getRecentExpenses(int limit) {
        return getExpenses().stream()
            .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    public List<Category> getAllCategories() {
        return db.getAllCategories();
    }
    
    public double getBudgetUtilization(double monthlyBudget) {
        if (monthlyBudget <= 0) return 0;
        
        YearMonth currentMonth = YearMonth.now();
        double currentMonthTotal = getMonthlyTotal(
            currentMonth.getYear(), 
            currentMonth.getMonthValue()
        );
        
        return (currentMonthTotal / monthlyBudget) * 100;
    }
}