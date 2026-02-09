package gui;

import core.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.List;
import java.util.Map;

public class ReportsDialog extends JDialog {
    private ExpenseManager expenseManager;
    
    // UI Components
    private JComboBox<String> reportTypeComboBox;
    private JComboBox<String> monthComboBox, yearComboBox;
    private JPanel chartPanel;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    JPanel totalLabel;

    private JPanel averageLabel;

    JPanel highestLabel;
    
    public ReportsDialog(JFrame parent, ExpenseManager expenseManager) {
        super(parent, "📊 Reports & Analytics", true);
        this.expenseManager = expenseManager;
        
        initializeUI();
        generateReport();
    }
    
    private void initializeUI() {
        setSize(900, 700);
        setLocationRelativeTo(getOwner());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("📊 Expense Reports & Analytics");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Report type
        String[] reportTypes = {
            "Category-wise Expenses", 
            "Monthly Summary", 
            "Daily Expenses", 
            "Spending Trends"
        };
        reportTypeComboBox = new JComboBox<>(reportTypes);
        reportTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        reportTypeComboBox.addActionListener(e -> generateReport());
        
        // Month selector
        String[] months = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        };
        monthComboBox = new JComboBox<>(months);
        monthComboBox.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        monthComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        monthComboBox.addActionListener(e -> generateReport());
        
        // Year selector
        String[] years = {"2023", "2024", "2025"};
        yearComboBox = new JComboBox<>(years);
        yearComboBox.setSelectedItem(String.valueOf(LocalDate.now().getYear()));
        yearComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        yearComboBox.addActionListener(e -> generateReport());
        
        controlsPanel.add(new JLabel("Report Type:"));
        controlsPanel.add(reportTypeComboBox);
        controlsPanel.add(Box.createHorizontalStrut(20));
        controlsPanel.add(new JLabel("Month:"));
        controlsPanel.add(monthComboBox);
        controlsPanel.add(new JLabel("Year:"));
        controlsPanel.add(yearComboBox);
        
        headerPanel.add(controlsPanel, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Stats panel
        JPanel statsPanel = createStatsPanel();
        mainPanel.add(statsPanel, BorderLayout.SOUTH);
        
        // Chart/Table panel
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(Color.GRAY, 1),
            "Chart View",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(52, 152, 219)
        ));
        
        // Table for data display
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        reportTable.setRowHeight(30);
        reportTable.setFont(new Font("Arial", Font.PLAIN, 12));
        reportTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        chartPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(chartPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        totalLabel = createStatCard("💰 Total", "$0.00", new Color(52, 152, 219));
        averageLabel = createStatCard("📈 Average", "$0.00", new Color(46, 204, 113));
        highestLabel = createStatCard("📊 Highest", "$0.00", new Color(155, 89, 182));
        
        JPanel countPanel = createStatCard("🔢 Count", "0", new Color(241, 196, 15));
        
        panel.add(totalLabel);
        panel.add(averageLabel);
        panel.add(highestLabel);
        panel.add(countPanel);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color.brighter(), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color.darker());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        
        return card;
    }
    
    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        int month = monthComboBox.getSelectedIndex() + 1;
        int year = Integer.parseInt((String) yearComboBox.getSelectedItem());
        
        switch (reportType) {
            case "Category-wise Expenses":
                showCategoryReport();
                break;
            case "Monthly Summary":
                showMonthlyReport(year, month);
                break;
            case "Daily Expenses":
                showDailyReport(year, month);
                break;
            case "Spending Trends":
                showTrendsReport();
                break;
        }
    }
    
    private void showCategoryReport() {
        Map<Category, Double> categoryExpenses = expenseManager.getCategoryWiseExpenses();
        
        // Update table
        tableModel.setDataVector(new Object[][]{}, new Object[]{"Category", "Amount", "Percentage"});
        
        double total = 0;
        for (Map.Entry<Category, Double> entry : categoryExpenses.entrySet()) {
            double amount = entry.getValue();
            total += amount;
            double percentage = (amount / expenseManager.getTotalExpenses()) * 100;
            
            tableModel.addRow(new Object[]{
                entry.getKey().getName(),
                String.format("$%.2f", amount),
                String.format("%.1f%%", percentage)
            });
        }
        
        // Update stats
        updateStats(categoryExpenses.values());
        
        // Add total row
        tableModel.addRow(new Object[]{
            "<html><b>TOTAL</b></html>",
            String.format("<html><b>$%.2f</b></html>", total),
            "100%"
        });
    }
    
    private void showMonthlyReport(int year, int month) {
        List<Expense> monthlyExpenses = expenseManager.getExpensesByMonth(year, month);
        
        tableModel.setDataVector(new Object[][]{}, 
            new Object[]{"Date", "Title", "Category", "Amount", "Payment Method"});
        
        double total = 0;
        for (Expense expense : monthlyExpenses) {
            total += expense.getAmount();
            
            tableModel.addRow(new Object[]{
                expense.getDate().toString(),
                expense.getTitle(),
                expense.getCategory().getName(),
                String.format("$%.2f", expense.getAmount()),
                expense.getPaymentMethod().toString()
            });
        }
        
        // Calculate stats
        if (!monthlyExpenses.isEmpty()) {
            double average = total / monthlyExpenses.size();
            Expense highest = monthlyExpenses.stream()
                .max((e1, e2) -> Double.compare(e1.getAmount(), e2.getAmount()))
                .orElse(null);
            
            totalLabel.removeAll();
            totalLabel.add(new JLabel(String.format("$%.2f", total)));
            
            averageLabel.removeAll();
            averageLabel.add(new JLabel(String.format("$%.2f", average)));
            
            highestLabel.removeAll();
            if (highest != null) {
                highestLabel.add(new JLabel(String.format("$%.2f", highest.getAmount())));
            }
        }
    }
    
    private void showDailyReport(int year, int month) {
        Map<String, Double> dailyExpenses = expenseManager.getDailyExpenses(year, month);
        
        tableModel.setDataVector(new Object[][]{}, new Object[]{"Date", "Amount"});
        
        double total = 0;
        for (Map.Entry<String, Double> entry : dailyExpenses.entrySet()) {
            total += entry.getValue();
            tableModel.addRow(new Object[]{
                entry.getKey(),
                String.format("$%.2f", entry.getValue())
            });
        }
        
        // Calculate stats
        if (!dailyExpenses.isEmpty()) {
            double average = total / dailyExpenses.size();
            double highest = dailyExpenses.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0);
            
            totalLabel.removeAll();
            totalLabel.add(new JLabel(String.format("$%.2f", total)));
            
            averageLabel.removeAll();
            averageLabel.add(new JLabel(String.format("$%.2f", average)));
            
            highestLabel.removeAll();
            highestLabel.add(new JLabel(String.format("$%.2f", highest)));
        }
    }
    
    private void showTrendsReport() {
        // Show last 6 months trend
        LocalDate now = LocalDate.now();
        tableModel.setDataVector(new Object[][]{}, 
            new Object[]{"Month", "Total Expenses", "Number of Transactions", "Average/Day"});
        
        double grandTotal = 0;
        for (int i = 5; i >= 0; i--) {
            LocalDate monthDate = now.minusMonths(i);
            int year = monthDate.getYear();
            int month = monthDate.getMonthValue();
            
            List<Expense> monthlyExpenses = expenseManager.getExpensesByMonth(year, month);
            double monthlyTotal = expenseManager.getMonthlyTotal(year, month);
            double averagePerDay = monthlyTotal / monthDate.lengthOfMonth();
            
            grandTotal += monthlyTotal;
            
            tableModel.addRow(new Object[]{
                monthDate.getMonth().toString() + " " + year,
                String.format("$%.2f", monthlyTotal),
                monthlyExpenses.size(),
                String.format("$%.2f", averagePerDay)
            });
        }
        
        // Update stats
        totalLabel.removeAll();
        totalLabel.add(new JLabel(String.format("$%.2f", grandTotal)));
        
        averageLabel.removeAll();
        averageLabel.add(new JLabel(String.format("$%.2f", grandTotal / 6)));
    }
    
    private void updateStats(Collection<Double> values) {
        if (values.isEmpty()) return;
        
        double total = values.stream().mapToDouble(Double::doubleValue).sum();
        double average = total / values.size();
        double highest = values.stream().mapToDouble(Double::doubleValue).max().orElse(0);
        
        totalLabel.removeAll();
        totalLabel.add(new JLabel(String.format("$%.2f", total)));
        
        averageLabel.removeAll();
        averageLabel.add(new JLabel(String.format("$%.2f", average)));
        
        highestLabel.removeAll();
        highestLabel.add(new JLabel(String.format("$%.2f", highest)));
    }
}