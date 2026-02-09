package core;

import java.util.regex.Pattern;

public class AuthManager {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern USERNAME_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    private Database db;
    private User currentUser;
    
    public AuthManager() {
        this.db = Database.getInstance();
    }
    
    public boolean register(String username, String password, 
                           String email, String fullName) {
        // Validate inputs
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        
        // Check if user already exists
        if (db.userExists(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        
        // Create and save new user
        User newUser = new User(username, password, email, fullName);
        db.saveUser(newUser);
        return true;
    }
    
    public boolean login(String username, String password) {
        User user = db.getUser(username);
        if (user != null && user.getPassword().equals(password)) {
            this.currentUser = user;
            return true;
        }
        return false;
    }
    
    public void logout() {
        this.currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public boolean updateProfile(String fullName, String email, double monthlyBudget) {
        if (currentUser != null) {
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setMonthlyBudget(monthlyBudget);
            db.saveUser(currentUser);
            return true;
        }
        return false;
    }
    
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser != null && currentUser.getPassword().equals(oldPassword)) {
            currentUser.setPassword(newPassword);
            db.saveUser(currentUser);
            return true;
        }
        return false;
    }
    
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    private boolean isValidUsername(String username) {
        return username != null && USERNAME_PATTERN.matcher(username).matches();
    }
}