package core;

import java.io.Serializable;
import java.time.LocalDateTime;

public class User implements Serializable {
    private String username;
    private String password;
    private String email;
    private String fullName;
    private double monthlyBudget;
    private LocalDateTime createdAt;
    
    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.monthlyBudget = 0.0;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    
    public double getMonthlyBudget() { return monthlyBudget; }
    public void setMonthlyBudget(double monthlyBudget) { this.monthlyBudget = monthlyBudget; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    @Override
    public String toString() {
        return String.format("User: %s (%s) | Budget: $%.2f", 
            fullName, username, monthlyBudget);
    }
}