package cli;

import core.AuthManager;
import java.util.Scanner;

public class CLIMain {
    private static Scanner scanner = new Scanner(System.in);
    private static AuthManager authManager = new AuthManager();
    
    public static void main(String[] args) {
        showWelcomeScreen();
        
        while (true) {
            showMainMenu();
            int choice = getIntInput("Enter choice: ");
            
            switch (choice) {
                case 1:
                    handleLogin();
                    break;
                case 2:
                    handleSignup();
                    break;
                case 3:
                    System.out.println("\nThank you for using Expense Tracker!");
                    System.out.println("Goodbye! 👋");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    private static void showWelcomeScreen() {
        System.out.println("=========================================");
        System.out.println("      WELCOME TO EXPENSE TRACKER");
        System.out.println("=========================================");
        System.out.println("    Track • Analyze • Save Money");
        System.out.println("=========================================\n");
    }
    
    private static void showMainMenu() {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("            MAIN MENU");
        System.out.println("═══════════════════════════════════");
        System.out.println("1. Login");
        System.out.println("2. Sign Up");
        System.out.println("3. Exit");
        System.out.println("═══════════════════════════════════");
    }
    
    private static void handleLogin() {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("              LOGIN");
        System.out.println("═══════════════════════════════════");
        
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
        if (authManager.login(username, password)) {
            System.out.println("\n✅ Login successful! Welcome, " + username + "!");
            CLIDashboard dashboard = new CLIDashboard(authManager);
            dashboard.showDashboard();
        } else {
            System.out.println("\n❌ Login failed! Invalid username or password.");
        }
    }
    
    private static void handleSignup() {
        System.out.println("\n═══════════════════════════════════");
        System.out.println("            SIGN UP");
        System.out.println("═══════════════════════════════════");
        
        System.out.print("Username (3-20 chars, letters/numbers/_): ");
        String username = scanner.nextLine();
        
        System.out.print("Password (min 6 chars): ");
        String password = scanner.nextLine();
        
        System.out.print("Email: ");
        String email = scanner.nextLine();
        
        System.out.print("Full Name: ");
        String fullName = scanner.nextLine();
        
        try {
            authManager.register(username, password, email, fullName);
            System.out.println("\n✅ Registration successful! You can now login.");
        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Registration failed: " + e.getMessage());
        }
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }
}