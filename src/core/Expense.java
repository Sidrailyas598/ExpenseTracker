package core;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Expense implements Serializable {
    private int id;
    private String username;
    private String title;
    private String description;
    private double amount;
    private Category category;
    private LocalDate date;
    private PaymentMethod paymentMethod;
    private boolean isRecurring;
    private LocalDateTime createdAt;
    
    public enum PaymentMethod {
        CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER, UPI, OTHER
    }
    
    public Expense(int id, String username, String title, double amount, 
                   Category category, LocalDate date) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.paymentMethod = PaymentMethod.CASH;
        this.isRecurring = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { 
        this.paymentMethod = paymentMethod; 
    }
    
    public boolean isRecurring() { return isRecurring; }
    public void setRecurring(boolean recurring) { isRecurring = recurring; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    @Override
    public String toString() {
        return String.format("ID: %d | %s | $%.2f | %s | %s", 
            id, title, amount, category.getName(), date);
    }
}