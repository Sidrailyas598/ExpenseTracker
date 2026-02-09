package gui;

import core.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ExpenseDialog extends JDialog {
    private ExpenseManager expenseManager;
    private Expense expense;
    private boolean saved = false;
    
    private JTextField titleField, amountField, dateField;
    private JComboBox<Category> categoryCombo;
    private JTextArea descriptionArea;
    
    public ExpenseDialog(JFrame parent, ExpenseManager expenseManager, Expense expense) {
        super(parent, expense == null ? "Add Expense" : "Edit Expense", true);
        this.expenseManager = expenseManager;
        this.expense = expense;
        
        initializeUI();
        if (expense != null) {
            loadExpenseData();
        }
    }
    
    private void initializeUI() {
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        
        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField(20);
        formPanel.add(titleField);
        
        formPanel.add(new JLabel("Amount ($):"));
        amountField = new JTextField(20);
        formPanel.add(amountField);
        
        formPanel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<>();
        for (Category cat : expenseManager.getAllCategories()) {
            categoryCombo.addItem(cat);
        }
        formPanel.add(categoryCombo);
        
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        dateField = new JTextField(LocalDate.now().toString());
        formPanel.add(dateField);
        
        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> saveExpense());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadExpenseData() {
        if (expense != null) {
            titleField.setText(expense.getTitle());
            amountField.setText(String.valueOf(expense.getAmount()));
            dateField.setText(expense.getDate().toString());
            descriptionArea.setText(expense.getDescription() != null ? expense.getDescription() : "");
            
            // Select category
            for (int i = 0; i < categoryCombo.getItemCount(); i++) {
                if (categoryCombo.getItemAt(i).getId() == expense.getCategory().getId()) {
                    categoryCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void saveExpense() {
        try {
            String title = titleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter title!");
                return;
            }
            
            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be greater than 0!");
                return;
            }
            
            Category selectedCat = (Category) categoryCombo.getSelectedItem();
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            String description = descriptionArea.getText().trim();
            
            if (expense == null) {
                // Add new expense
                expenseManager.addExpense(title, amount, selectedCat.getId(), date, description);
                JOptionPane.showMessageDialog(this, "Expense added successfully!");
            } else {
                // Update existing expense
                expenseManager.updateExpense(
                    expense.getId(), title, amount, 
                    selectedCat.getId(), date, description
                );
                JOptionPane.showMessageDialog(this, "Expense updated successfully!");
            }
            
            saved = true;
            dispose();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid amount!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
}