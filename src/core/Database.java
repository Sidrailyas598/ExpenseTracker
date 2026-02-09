package core;

import java.io.*;
import java.util.*;

public class Database {
    private static final String USER_FILE = "data/users.dat";
    private static final String EXPENSE_FILE = "data/expenses.dat";
    private static final String CATEGORY_FILE = "data/categories.dat";
    
    // Singleton instance
    private static Database instance;
    
    private Database() {
        createDataDirectory();
        initializeCategories();
    }
    
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }
    
    private void createDataDirectory() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdir();
        }
    }
    
    private void initializeCategories() {
        List<Category> categories = loadCategories();
        if (categories.isEmpty()) {
            categories = Arrays.asList(Category.DEFAULT_CATEGORIES);
            saveCategories(categories);
        }
    }
    
    // User operations
    public void saveUser(User user) {
        List<User> users = loadUsers();
        // Remove existing user with same username
        users.removeIf(u -> u.getUsername().equals(user.getUsername()));
        users.add(user);
        saveUsers(users);
    }
    
    public User getUser(String username) {
        List<User> users = loadUsers();
        return users.stream()
            .filter(u -> u.getUsername().equals(username))
            .findFirst()
            .orElse(null);
    }
    
    public boolean userExists(String username) {
        return getUser(username) != null;
    }
    
    // Expense operations
    public void saveExpense(Expense expense) {
        List<Expense> expenses = loadExpenses();
        expenses.add(expense);
        saveExpenses(expenses);
    }
    
    public List<Expense> getUserExpenses(String username) {
        List<Expense> allExpenses = loadExpenses();
        List<Expense> userExpenses = new ArrayList<>();
        for (Expense expense : allExpenses) {
            if (expense.getUsername().equals(username)) {
                userExpenses.add(expense);
            }
        }
        return userExpenses;
    }
    
    public boolean deleteExpense(int expenseId, String username) {
        List<Expense> expenses = loadExpenses();
        boolean removed = expenses.removeIf(e -> 
            e.getId() == expenseId && e.getUsername().equals(username));
        if (removed) {
            saveExpenses(expenses);
        }
        return removed;
    }
    
    // Category operations
    public List<Category> getAllCategories() {
        return loadCategories();
    }
    
    public Category getCategoryById(int id) {
        return loadCategories().stream()
            .filter(c -> c.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    // File loading methods
    @SuppressWarnings("unchecked")
    private List<User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            return (List<User>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Expense> loadExpenses() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(EXPENSE_FILE))) {
            return (List<Expense>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading expenses: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void saveExpenses(List<Expense> expenses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(EXPENSE_FILE))) {
            oos.writeObject(expenses);
        } catch (IOException e) {
            System.err.println("Error saving expenses: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<Category> loadCategories() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(CATEGORY_FILE))) {
            return (List<Category>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading categories: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void saveCategories(List<Category> categories) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(CATEGORY_FILE))) {
            oos.writeObject(categories);
        } catch (IOException e) {
            System.err.println("Error saving categories: " + e.getMessage());
        }
    }
}