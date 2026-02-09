package gui;

import core.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private AuthManager authManager;
    private ExpenseManager expenseManager;
    private User currentUser;
    
    // UI Components
    private JLabel welcomeLabel, budgetLabel, totalExpensesLabel, 
                   budgetProgressLabel, monthlyTotalLabel;
    private JPanel statsPanel, mainContentPanel;
    private JTable expensesTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterComboBox;
    private JButton addExpenseButton, refreshButton, reportsButton, 
                    categoriesButton, logoutButton, profileButton;
    
    public DashboardFrame(AuthManager authManager) {
        System.out.println("=== DASHBOARD CONSTRUCTOR START ===");
        
        try {
            // 1. Set auth manager
            System.out.println("1. Setting authManager...");
            this.authManager = authManager;
            
            // 2. Get current user
            System.out.println("2. Getting current user...");
            this.currentUser = authManager.getCurrentUser();
            
            if (currentUser == null) {
                System.out.println("ERROR: No user logged in!");
                JOptionPane.showMessageDialog(null, "Please login first!");
                return;
            }
            
            System.out.println("3. User found: " + currentUser.getUsername());
            
            // 3. Create ExpenseManager
            System.out.println("4. Creating ExpenseManager...");
            try {
                this.expenseManager = new ExpenseManager(currentUser.getUsername());
                System.out.println("✅ ExpenseManager created");
            } catch (Exception e) {
                System.err.println("❌ Error creating ExpenseManager: " + e.getMessage());
                throw e;
            }
            
            // 4. Basic window setup FIRST
            System.out.println("5. Basic window setup...");
            setTitle("💰 Expense Tracker - Dashboard");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1300, 850); // Start with final size
            setLocationRelativeTo(null);
            getContentPane().setBackground(new Color(240, 242, 245));
            
            // 5. Load UI directly (no loading screen)
            System.out.println("6. Loading full UI...");
            initializeUI();
            loadExpenses();
            updateStats();
            
            // 6. Make visible IMMEDIATELY
            System.out.println("7. Setting window visible...");
            setVisible(true);
            
            System.out.println("8. Window visible: " + isVisible());
            System.out.println("=== DASHBOARD CONSTRUCTOR END ===");
            
        } catch (Exception e) {
            System.err.println("💥 FATAL ERROR in DashboardFrame: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Dashboard Error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void initializeUI() {
        setTitle("💰 Expense Tracker - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850);
        setLocationRelativeTo(null);
        
        // Main container
        Container container = getContentPane();
        container.setLayout(new BorderLayout(0, 0));
        container.setBackground(new Color(245, 247, 250));
        
        // Header panel - FIXED LAYOUT
        JPanel headerPanel = createHeaderPanel();
        container.add(headerPanel, BorderLayout.NORTH);
        
        // Stats panel
        statsPanel = createStatsPanel();
        container.add(statsPanel, BorderLayout.CENTER);
        
        // Main content panel
        mainContentPanel = new JPanel(new BorderLayout(10, 10));
        mainContentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 20, 15, 20),
            BorderFactory.createLineBorder(new Color(225, 228, 232), 1)
        ));
        mainContentPanel.setBackground(Color.WHITE);
        
        // Expenses table panel
        JPanel tablePanel = createTablePanel();
        mainContentPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        container.add(mainContentPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(52, 73, 94)); // Dark blue-gray
        panel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        panel.setPreferredSize(new Dimension(getWidth(), 100)); // Increased height
        
        // Left side: Welcome message - FIXED to prevent overlap
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(600, 70)); // Fixed width
        
        welcomeLabel = new JLabel("👋 Welcome, " + currentUser.getFullName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26)); // Larger font
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leftPanel.add(welcomeLabel, BorderLayout.WEST);
        
        // Right side: User menu - FIXED VISIBILITY
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(400, 70)); // Fixed width
        
        // Profile Button - CLEAR VISIBLE
        profileButton = new JButton("👤 Profile");
        profileButton.setFont(new Font("Arial", Font.BOLD, 14));
        profileButton.setForeground(Color.BLACK);
        profileButton.setBackground(new Color(41, 128, 185)); // Blue
        profileButton.setFocusPainted(false);
        profileButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(31, 97, 141), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileButton.addActionListener(e -> showProfileDialog());
        
        // Logout Button - CLEAR VISIBLE
        logoutButton = new JButton("🚪 Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setForeground(Color.black);
        logoutButton.setBackground(new Color(231, 76, 60)); // Red
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(192, 57, 43), 2),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> logout());
        
        // Add hover effects
        addButtonHoverEffect(profileButton, new Color(41, 128, 185));
        addButtonHoverEffect(logoutButton, new Color(231, 76, 60));
        
        rightPanel.add(profileButton);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(logoutButton);
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    private void addButtonHoverEffect(JButton button, Color baseColor) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
                button.setFont(new Font("Arial", Font.BOLD, 15));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
                button.setFont(new Font("Arial", Font.BOLD, 14));
            }
        });
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 25, 25));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(25, 25, 25, 25),
            BorderFactory.createLineBorder(new Color(225, 228, 232), 1)
        ));
        panel.setBackground(new Color(245, 247, 250));
        
        // Create stats cards
        Object[] budgetCard = createModernStatCard("💰 Monthly Budget", "$0.00", 
            new Color(52, 152, 219));
        JPanel budgetPanel = (JPanel) budgetCard[0];
        budgetLabel = (JLabel) budgetCard[1];
        
        Object[] expenseCard = createModernStatCard("📊 Total Expenses", "$0.00", 
            new Color(46, 204, 113));
        JPanel expensePanel = (JPanel) expenseCard[0];
        totalExpensesLabel = (JLabel) expenseCard[1];
        
        Object[] progressCard = createModernStatCard("📈 Budget Used", "0%", 
            new Color(155, 89, 182));
        JPanel progressPanel = (JPanel) progressCard[0];
        budgetProgressLabel = (JLabel) progressCard[1];
        
        Object[] monthlyCard = createModernStatCard("📅 This Month", "$0.00", 
            new Color(241, 196, 15));
        JPanel monthlyPanel = (JPanel) monthlyCard[0];
        monthlyTotalLabel = (JLabel) monthlyCard[1];
        
        panel.add(budgetPanel);
        panel.add(expensePanel);
        panel.add(progressPanel);
        panel.add(monthlyPanel);
        
        return panel;
    }
    
    private Object[] createModernStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        
        // Modern shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(25, 25, 25, 25),
                BorderFactory.createMatteBorder(0, 0, 0, 5, color)
            )
        ));
        
        // Icon and title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(getIconForTitle(title));
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        iconLabel.setForeground(color);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(52, 73, 94));
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(titleLabel);
        
        // Value with large font
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components
        card.add(titlePanel);
        card.add(Box.createVerticalStrut(20));
        card.add(valueLabel);
        
        return new Object[]{card, valueLabel};
    }
    
    private String getIconForTitle(String title) {
        if (title.contains("Budget")) return "💰";
        if (title.contains("Expenses")) return "📊";
        if (title.contains("Progress") || title.contains("Used")) return "📈";
        if (title.contains("Month")) return "📅";
        return "💵";
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(Color.pink);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(225, 228, 232)),
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        ));
        
        JLabel filterLabel = new JLabel("🔍 Filter by:");
        filterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        filterLabel.setForeground(new Color(52, 73, 94));
        
        filterComboBox = new JComboBox<>(new String[]{
            "All Expenses", "This Month", "Last 30 Days", "By Category"
        });
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 13));
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setForeground(new Color(44, 62, 80));
        filterComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        filterComboBox.addActionListener(e -> filterExpenses());
        
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        
        // Table with FIXED HEADER COLOR (NOT WHITE)
        String[] columns = {"ID", "Title", "Amount", "Category", "Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        expensesTable = new JTable(tableModel);
        expensesTable.setRowHeight(40);
        expensesTable.setFont(new Font("Arial", Font.PLAIN, 13));
        expensesTable.setSelectionBackground(new Color(236, 240, 241));
        expensesTable.setSelectionForeground(new Color(44, 62, 80));
        expensesTable.setGridColor(new Color(225, 228, 232));
        
        // FIX: Table Header - DARK COLOR, NOT WHITE
        JTableHeader header = expensesTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setBackground(new Color(67, 98, 156)); // Dark blue-gray
        header.setForeground(Color.black); // White text
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        
        // Set column renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        
        expensesTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        expensesTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        
        expensesTable.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        expensesTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        
        expensesTable.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        expensesTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        expensesTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setForeground(new Color(231, 76, 60)); // Red for amounts
                ((JLabel) c).setHorizontalAlignment(SwingConstants.RIGHT);
                return c;
            }
        });
        
        expensesTable.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);
        expensesTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        
        expensesTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        expensesTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        expensesTable.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);
        expensesTable.getColumnModel().getColumn(5).setPreferredWidth(300);
        
        // Add double-click listener
        expensesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedExpense();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(expensesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 228, 232), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 228, 232)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel summaryLabel = new JLabel("💡 Tip: Double-click any expense to edit");
        summaryLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        summaryLabel.setForeground(new Color(127, 140, 141));
        summaryPanel.add(summaryLabel);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(225, 228, 232)),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        
        // Create buttons
        addExpenseButton = createModernButton("➕ Add New Expense", new Color(46, 204, 113));
        refreshButton = createModernButton("🔄 Refresh Data", new Color(52, 152, 219));
        reportsButton = createModernButton("📊 View Reports", new Color(155, 89, 182));
        categoriesButton = createModernButton("🏷️ Manage Categories", new Color(241, 196, 15));
        JButton deleteButton = createModernButton("🗑️ Delete Selected", new Color(231, 76, 60));
        
        // Add action listeners
        addExpenseButton.addActionListener(e -> addNewExpense());
        refreshButton.addActionListener(e -> refreshData());
        reportsButton.addActionListener(e -> showReports());
        categoriesButton.addActionListener(e -> manageCategories());
        deleteButton.addActionListener(e -> deleteSelectedExpense());
        
        // Add buttons with proper spacing
        panel.add(Box.createHorizontalGlue());
        panel.add(addExpenseButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(refreshButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(reportsButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(categoriesButton);
        panel.add(Box.createHorizontalStrut(15));
        panel.add(deleteButton);
        panel.add(Box.createHorizontalGlue());
        
        return panel;
    }
    
    private JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text);
        
        // Automatic contrasting text color
        Color textColor = getContrastColor(color);
        
        button.setForeground(textColor);
        button.setBackground(color);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 2),
            BorderFactory.createEmptyBorder(12, 25, 12, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 50));
        button.setMaximumSize(new Dimension(180, 50));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
                button.setFont(new Font("Arial", Font.BOLD, 15));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
                button.setFont(new Font("Arial", Font.BOLD, 14));
            }
        });
        
        return button;
    }
    
    private Color getContrastColor(Color color) {
        // Calculate luminance
        double luminance = (0.299 * color.getRed() + 
                           0.587 * color.getGreen() + 
                           0.114 * color.getBlue()) / 255;
        
        // Return black for light colors, white for dark colors
        return luminance > 0.5 ? new Color(44, 62, 80) : Color.WHITE;
    }
    
    private void loadExpenses() {
        tableModel.setRowCount(0); // Clear table
        
        List<Expense> expenses = expenseManager.getRecentExpenses(50);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Expense expense : expenses) {
            Object[] row = {
                expense.getId(),
                expense.getTitle(),
                String.format("$%.2f", expense.getAmount()),
                expense.getCategory().getName(),
                expense.getDate().format(formatter),
                expense.getDescription() != null ? expense.getDescription() : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateStats() {
        double monthlyBudget = currentUser.getMonthlyBudget();
        double totalExpenses = expenseManager.getTotalExpenses();
        
        LocalDate now = LocalDate.now();
        double monthlyTotal = expenseManager.getMonthlyTotal(now.getYear(), now.getMonthValue());
        double budgetUtilization = expenseManager.getBudgetUtilization(monthlyBudget);
        
        budgetLabel.setText(String.format("$%.2f", monthlyBudget));
        totalExpensesLabel.setText(String.format("$%.2f", totalExpenses));
        budgetProgressLabel.setText(String.format("%.1f%%", budgetUtilization));
        monthlyTotalLabel.setText(String.format("$%.2f", monthlyTotal));
    }
    
    private void addNewExpense() {
        ExpenseDialog dialog = new ExpenseDialog(this, expenseManager, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadExpenses();
            updateStats();
        }
    }
    
    private void editSelectedExpense() {
        int selectedRow = expensesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an expense to edit!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        List<Expense> expenses = expenseManager.getExpenses();
        Expense selectedExpense = null;
        for (Expense exp : expenses) {
            if (exp.getId() == expenseId) {
                selectedExpense = exp;
                break;
            }
        }
        
        if (selectedExpense != null) {
            ExpenseDialog dialog = new ExpenseDialog(this, expenseManager, selectedExpense);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadExpenses();
                updateStats();
            }
        }
    }
    
    private void deleteSelectedExpense() {
        int selectedRow = expensesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an expense to delete!", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int expenseId = (int) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this expense?\nThis action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (expenseManager.deleteExpense(expenseId)) {
                JOptionPane.showMessageDialog(this, 
                    "Expense deleted successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadExpenses();
                updateStats();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to delete expense!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void filterExpenses() {
        loadExpenses(); // For now, just reload all
    }
    
    private void refreshData() {
        loadExpenses();
        updateStats();
        JOptionPane.showMessageDialog(this, 
            "Data refreshed successfully!", 
            "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showReports() {
        ReportsDialog reportsDialog = new ReportsDialog(this, expenseManager);
        reportsDialog.setVisible(true);
    }
    
    private void manageCategories() {
        CategoriesDialog categoriesDialog = new CategoriesDialog(this, expenseManager);
        categoriesDialog.setVisible(true);
    }
    
    private void showProfileDialog() {
        ProfileDialog profileDialog = new ProfileDialog(this, authManager);
        profileDialog.setVisible(true);
        if (profileDialog.isUpdated()) {
            currentUser = authManager.getCurrentUser();
            welcomeLabel.setText("👋 Welcome, " + currentUser.getFullName() + "!");
            updateStats();
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            authManager.logout();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            dispose();
        }
    }
}