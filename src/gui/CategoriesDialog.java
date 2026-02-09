package gui;

import core.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class CategoriesDialog extends JDialog {
    private ExpenseManager expenseManager;
    private DefaultTableModel tableModel;
    private JTable categoriesTable;
    private JTextField budgetField;
    
    public CategoriesDialog(JFrame parent, ExpenseManager expenseManager) {
        super(parent, "🏷️ Manage Categories", true);
        this.expenseManager = expenseManager;
        
        initializeUI();
        loadCategories();
    }
    
    private void initializeUI() {
        setSize(600, 500);
        setLocationRelativeTo(getOwner());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("🏷️ Expense Categories");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(Color.GRAY, 1),
            "Categories",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        
        String[] columns = {"ID", "Name", "Description", "Budget Limit", "Color"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only budget limit is editable
            }
        };
        
        categoriesTable = new JTable(tableModel);
        categoriesTable.setRowHeight(30);
        categoriesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        categoriesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Color renderer for color column
        categoriesTable.getColumnModel().getColumn(4).setCellRenderer(new ColorRenderer());
        
        JScrollPane scrollPane = new JScrollPane(categoriesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Budget panel
        JPanel budgetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        budgetPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel budgetLabel = new JLabel("Set Budget for Selected Category: $");
        budgetField = new JTextField(10);
        budgetField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton setBudgetButton = new JButton("💾 Set Budget");
        setBudgetButton.setBackground(new Color(46, 204, 113));
        setBudgetButton.setForeground(Color.WHITE);
        setBudgetButton.setFocusPainted(false);
        setBudgetButton.addActionListener(e -> setCategoryBudget());
        
        budgetPanel.add(budgetLabel);
        budgetPanel.add(budgetField);
        budgetPanel.add(Box.createHorizontalStrut(10));
        budgetPanel.add(setBudgetButton);
        
        mainPanel.add(budgetPanel, BorderLayout.SOUTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(231, 76, 60));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> dispose());
        
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void loadCategories() {
        tableModel.setRowCount(0);
        
        List<Category> categories = expenseManager.getAllCategories();
        for (Category category : categories) {
            tableModel.addRow(new Object[]{
                category.getId(),
                category.getName(),
                category.getDescription(),
                String.format("$%.2f", category.getBudgetLimit()),
                category.getColorCode()
            });
        }
    }
    
    private void setCategoryBudget() {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a category!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            double budget = Double.parseDouble(budgetField.getText().trim());
            if (budget < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Budget cannot be negative!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int categoryId = (int) tableModel.getValueAt(selectedRow, 0);
            List<Category> categories = expenseManager.getAllCategories();
            
            for (Category category : categories) {
                if (category.getId() == categoryId) {
                    category.setBudgetLimit(budget);
                    break;
                }
            }
            
            // Update table
            tableModel.setValueAt(String.format("$%.2f", budget), selectedRow, 3);
            
            JOptionPane.showMessageDialog(this, 
                "Budget updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            budgetField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid budget amount!", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Custom cell renderer for color display
    class ColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            
            if (value instanceof String) {
                String colorCode = (String) value;
                try {
                    Color color = Color.decode(colorCode);
                    c.setBackground(color);
                    c.setForeground(getContrastColor(color));
                    
                    // Center the text
                    setHorizontalAlignment(JLabel.CENTER);
                    
                    // Add padding
                    setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                } catch (NumberFormatException e) {
                    // If color code is invalid, use default
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
            }
            
            return c;
        }
        
        private Color getContrastColor(Color color) {
            // Calculate luminance
            double luminance = (0.299 * color.getRed() + 
                              0.587 * color.getGreen() + 
                              0.114 * color.getBlue()) / 255;
            return luminance > 0.5 ? Color.BLACK : Color.WHITE;
        }
    }
}