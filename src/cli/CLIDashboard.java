package cli;

import core.*;
import java.time.LocalDate;
import java.util.*;

public class CLIDashboard {
    private AuthManager authManager;
    private ExpenseManager expenseManager;
    private Scanner scanner;
    
    public CLIDashboard(AuthManager authManager) {
        this.authManager = authManager;
        this.expenseManager = new ExpenseManager(authManager.getCurrentUser().getUsername());
        this.scanner = new Scanner(System.in);
    }
    
    public void showDashboard() {
        while (true) {
            displayHeader();
            showMenu();
            int choice = getIntInput("Enter choice: ");
            
            switch (choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    viewExpenses();
                    break;
                case 3:
                    viewReports();
                    break;
                case 4:
                    manageCategories();
                    break;
                case 5:
                    profileSettings();
                    break;
                case 6:
                    authManager.logout();
                    System.out.println("\n✅ Logged out successfully!");
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
            
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }
    
    private void displayHeader() {
        System.out.println("\n══════════════════════════════════════════════════");
        User user = authManager.getCurrentUser();
        System.out.printf(" Welcome, %s! 👋\n", user.getFullName());
        System.out.println("══════════════════════════════════════════════════");
        
        // Show quick stats
        double totalExpenses = expenseManager.getTotalExpenses();
        double monthlyBudget = user.getMonthlyBudget();
        
        System.out.printf(" Total Expenses: $%.2f\n", totalExpenses);
        System.out.printf(" Monthly Budget: $%.2f\n", monthlyBudget);
        
        if (monthlyBudget > 0) {
            double utilization = expenseManager.getBudgetUtilization(monthlyBudget);
            System.out.printf(" Budget Used: %.1f%%\n", utilization);
        }
        System.out.println("══════════════════════════════════════════════════");
    }
    
    private void showMenu() {
        System.out.println("\nDASHBOARD MENU:");
        System.out.println("1. ➕ Add New Expense");
        System.out.println("2. 👁️  View/Edit Expenses");
        System.out.println("3. 📊 View Reports & Analytics");
        System.out.println("4. 🏷️  Manage Categories");
        System.out.println("5. ⚙️  Profile Settings");
        System.out.println("6. 🚪 Logout");
        System.out.println("══════════════════════════════════════════════════");
    }
    
    private void addExpense() {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("        ADD NEW EXPENSE");
        System.out.println("═══════════════════════════════════");
        
        // Show categories
        List<Category> categories = expenseManager.getAllCategories();
        System.out.println("\nAvailable Categories:");
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            System.out.printf("%d. %s - %s\n", i + 1, cat.getName(), cat.getDescription());
        }
        
        System.out.print("\nExpense Title: ");
        String title = scanner.nextLine();
        
        double amount = getDoubleInput("Amount: $");
        
        int categoryChoice = getIntInput("Category (1-" + categories.size() + "): ");
        if (categoryChoice < 1 || categoryChoice > categories.size()) {
            System.out.println("Invalid category choice!");
            return;
        }
        
        System.out.print("Description (optional): ");
        String description = scanner.nextLine();
        
        // Use current date or ask for custom date
        System.out.print("Use today's date? (y/n): ");
        String useToday = scanner.nextLine().toLowerCase();
        
        LocalDate date;
        if (useToday.equals("y")) {
            date = LocalDate.now();
        } else {
            System.out.print("Enter date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine();
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.out.println("Invalid date format! Using today's date.");
                date = LocalDate.now();
            }
        }
        
        boolean success = expenseManager.addExpense(
            title, amount, categories.get(categoryChoice - 1).getId(), 
            date, description
        );
        
        if (success) {
            System.out.println("\n✅ Expense added successfully!");
        } else {
            System.out.println("\n❌ Failed to add expense!");
        }
    }
    
    private void viewExpenses() {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("          YOUR EXPENSES");
        System.out.println("═══════════════════════════════════");
        
        List<Expense> expenses = expenseManager.getRecentExpenses(20);
        
        if (expenses.isEmpty()) {
            System.out.println("No expenses found!");
            return;
        }
        
        System.out.printf("\n%-5s %-20s %-10s %-15s %-12s\n", 
            "ID", "Title", "Amount", "Category", "Date");
        System.out.println("─".repeat(70));
        
        for (Expense expense : expenses) {
            System.out.printf("%-5d %-20s $%-9.2f %-15s %-12s\n",
                expense.getId(),
                expense.getTitle().length() > 20 ? 
                    expense.getTitle().substring(0, 17) + "..." : expense.getTitle(),
                expense.getAmount(),
                expense.getCategory().getName().length() > 15 ?
                    expense.getCategory().getName().substring(0, 12) + "..." : 
                    expense.getCategory().getName(),
                expense.getDate()
            );
        }
        
        System.out.println("\nTotal: $" + expenseManager.getTotalExpenses());
        
        // Options
        System.out.println("\nOptions:");
        System.out.println("1. Edit Expense");
        System.out.println("2. Delete Expense");
        System.out.println("3. Back to Menu");
        
        int choice = getIntInput("Choose: ");
        
        if (choice == 1) {
            editExpense();
        } else if (choice == 2) {
            deleteExpense();
        }
    }
    
    private void editExpense() {
        int expenseId = getIntInput("Enter Expense ID to edit: ");
        // Implementation for editing expense
        System.out.println("Edit functionality to be implemented...");
    }
    
    private void deleteExpense() {
        int expenseId = getIntInput("Enter Expense ID to delete: ");
        
        System.out.print("Are you sure? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        
        if (confirm.equals("y")) {
            boolean success = expenseManager.deleteExpense(expenseId);
            if (success) {
                System.out.println("✅ Expense deleted successfully!");
            } else {
                System.out.println("❌ Expense not found or deletion failed!");
            }
        }
    }
    
    private void viewReports() {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("        REPORTS & ANALYTICS");
        System.out.println("═══════════════════════════════════");
        
        System.out.println("\n1. Category-wise Expenses");
        System.out.println("2. Monthly Summary");
        System.out.println("3. Daily Expenses");
        System.out.println("4. Spending Trends");
        
        int choice = getIntInput("Choose report type: ");
        
        switch (choice) {
            case 1:
                showCategoryReport();
                break;
            case 2:
                showMonthlyReport();
                break;
            case 3:
                showDailyReport();
                break;
            default:
                System.out.println("Coming soon...");
        }
    }
    
    private void showCategoryReport() {
        Map<Category, Double> categoryExpenses = expenseManager.getCategoryWiseExpenses();
        
        if (categoryExpenses.isEmpty()) {
            System.out.println("No expenses to show!");
            return;
        }
        
        System.out.println("\nCATEGORY-WISE EXPENSES:");
        System.out.println("─".repeat(50));
        
        double total = 0;
        for (Map.Entry<Category, Double> entry : categoryExpenses.entrySet()) {
            double amount = entry.getValue();
            total += amount;
            double percentage = (amount / expenseManager.getTotalExpenses()) * 100;
            
            System.out.printf("%-20s: $%-8.2f (%.1f%%)\n",
                entry.getKey().getName(), amount, percentage);
        }
        
        System.out.println("─".repeat(50));
        System.out.printf("TOTAL: $%.2f\n", total);
    }
    
    private void showMonthlyReport() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        
        double monthlyTotal = expenseManager.getMonthlyTotal(year, month);
        List<Expense> monthlyExpenses = expenseManager.getExpensesByMonth(year, month);
        
        System.out.printf("\nMONTHLY REPORT: %d-%02d\n", year, month);
        System.out.println("─".repeat(50));
        System.out.printf("Total Expenses: $%.2f\n", monthlyTotal);
        System.out.printf("Number of Transactions: %d\n", monthlyExpenses.size());
        System.out.printf("Average Daily Expense: $%.2f\n", 
            monthlyTotal / now.getDayOfMonth());
        
        if (!monthlyExpenses.isEmpty()) {
            Expense highest = monthlyExpenses.stream()
                .max(Comparator.comparing(Expense::getAmount))
                .orElse(null);
            Expense lowest = monthlyExpenses.stream()
                .min(Comparator.comparing(Expense::getAmount))
                .orElse(null);
            
            System.out.printf("Highest Expense: %s ($%.2f)\n", 
                highest.getTitle(), highest.getAmount());
            System.out.printf("Lowest Expense: %s ($%.2f)\n", 
                lowest.getTitle(), lowest.getAmount());
        }
    }
    
    private void showDailyReport() {
        LocalDate now = LocalDate.now();
        Map<String, Double> dailyExpenses = expenseManager.getDailyExpenses(
            now.getYear(), now.getMonthValue());
        
        System.out.printf("\nDAILY EXPENSES: %d-%02d\n", now.getYear(), now.getMonthValue());
        System.out.println("─".repeat(50));
        
        for (Map.Entry<String, Double> entry : dailyExpenses.entrySet()) {
            System.out.printf("%s: $%.2f\n", entry.getKey(), entry.getValue());
        }
    }
    
    private void manageCategories() {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("        MANAGE CATEGORIES");
        System.out.println("═══════════════════════════════════");
        
        List<Category> categories = expenseManager.getAllCategories();
        
        System.out.println("\nCurrent Categories:");
        for (int i = 0; i < categories.size(); i++) {
            Category cat = categories.get(i);
            System.out.printf("%d. %s (Budget: $%.2f)\n", 
                i + 1, cat.getName(), cat.getBudgetLimit());
        }
        
        System.out.println("\n1. Set Category Budget");
        System.out.println("2. Back to Menu");
        
        int choice = getIntInput("Choose: ");
        
        if (choice == 1) {
            int catChoice = getIntInput("Select category (1-" + categories.size() + "): ");
            if (catChoice >= 1 && catChoice <= categories.size()) {
                double budget = getDoubleInput("Enter budget limit: $");
                categories.get(catChoice - 1).setBudgetLimit(budget);
                System.out.println("✅ Budget limit updated!");
            }
        }
    }
    
    private void profileSettings() {
        User user = authManager.getCurrentUser();
        
        System.out.println("\n═══════════════════════════════════");
        System.out.println("        PROFILE SETTINGS");
        System.out.println("═══════════════════════════════════");
        
        System.out.println("\nCurrent Information:");
        System.out.printf("Full Name: %s\n", user.getFullName());
        System.out.printf("Email: %s\n", user.getEmail());
        System.out.printf("Monthly Budget: $%.2f\n", user.getMonthlyBudget());
        
        System.out.println("\n1. Update Profile");
        System.out.println("2. Change Password");
        System.out.println("3. Set Monthly Budget");
        System.out.println("4. Back to Menu");
        
        int choice = getIntInput("Choose: ");
        
        switch (choice) {
            case 1:
                updateProfile();
                break;
            case 2:
                changePassword();
                break;
            case 3:
                setBudget();
                break;
        }
    }
    
    private void updateProfile() {
        User user = authManager.getCurrentUser();
        
        System.out.print("Full Name [" + user.getFullName() + "]: ");
        String fullName = scanner.nextLine();
        if (fullName.isEmpty()) fullName = user.getFullName();
        
        System.out.print("Email [" + user.getEmail() + "]: ");
        String email = scanner.nextLine();
        if (email.isEmpty()) email = user.getEmail();
        
        authManager.updateProfile(fullName, email, user.getMonthlyBudget());
        System.out.println("✅ Profile updated successfully!");
    }
    
    private void changePassword() {
        System.out.print("Current Password: ");
        String currentPass = scanner.nextLine();
        
        System.out.print("New Password: ");
        String newPass = scanner.nextLine();
        
        System.out.print("Confirm New Password: ");
        String confirmPass = scanner.nextLine();
        
        if (!newPass.equals(confirmPass)) {
            System.out.println("❌ Passwords don't match!");
            return;
        }
        
        if (authManager.changePassword(currentPass, newPass)) {
            System.out.println("✅ Password changed successfully!");
        } else {
            System.out.println("❌ Current password is incorrect!");
        }
    }
    
    private void setBudget() {
        double budget = getDoubleInput("Enter monthly budget: $");
        User user = authManager.getCurrentUser();
        authManager.updateProfile(user.getFullName(), user.getEmail(), budget);
        System.out.println("✅ Monthly budget updated!");
    }
    
    private int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
    
    private double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid amount.");
            }
        }
    }
}