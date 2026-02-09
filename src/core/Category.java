package core;

import java.io.Serializable;

public class Category implements Serializable {
    private int id;
    private String name;
    private String description;
    private double budgetLimit;
    private String colorCode;
    
    // Default categories
    public static final Category[] DEFAULT_CATEGORIES = {
        new Category(1, "Food & Dining", "Restaurants, groceries, snacks", "#FF6B6B"),
        new Category(2, "Transportation", "Fuel, taxi, public transport", "#4ECDC4"),
        new Category(3, "Shopping", "Clothes, electronics, items", "#FFD166"),
        new Category(4, "Entertainment", "Movies, games, hobbies", "#06D6A0"),
        new Category(5, "Bills & Utilities", "Electricity, water, internet", "#118AB2"),
        new Category(6, "Healthcare", "Medicine, doctor visits", "#EF476F"),
        new Category(7, "Education", "Books, courses, tuition", "#7209B7"),
        new Category(8, "Others", "Miscellaneous expenses", "#8A8A8A")
    };
    
    public Category(int id, String name, String description, String colorCode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
        this.budgetLimit = 0.0;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public double getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(double budgetLimit) { this.budgetLimit = budgetLimit; }
    
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
    
    @Override
    public String toString() {
        return String.format("%s (Limit: $%.2f)", name, budgetLimit);
    }
}