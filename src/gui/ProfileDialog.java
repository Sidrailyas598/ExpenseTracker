package gui;

import core.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class ProfileDialog extends JDialog {
    private AuthManager authManager;
    private User currentUser;
    private boolean updated = false;
    
    // Form components
    private JTextField usernameField, emailField, nameField, budgetField;
    private JPasswordField currentPasswordField, newPasswordField, confirmPasswordField;
    
    public ProfileDialog(JFrame parent, AuthManager authManager) {
        super(parent, "👤 Profile Settings", true);
        this.authManager = authManager;
        this.currentUser = authManager.getCurrentUser();
        
        initializeUI();
        loadUserData();
    }
    
    private void initializeUI() {
        setSize(500, 600);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("👤 Profile Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Tabbed pane for different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Personal Info tab
        JPanel personalPanel = createPersonalInfoPanel();
        tabbedPane.addTab("📝 Personal Info", personalPanel);
        
        // Change Password tab
        JPanel passwordPanel = createPasswordPanel();
        tabbedPane.addTab("🔒 Change Password", passwordPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton saveButton = new JButton("💾 Save Changes");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveProfile());
        
        JButton cancelButton = new JButton("❌ Cancel");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Username (read-only)
        panel.add(createLabel("Username:"));
        usernameField = new JTextField(20);
        usernameField.setEditable(false);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(15));
        
        // Full Name
        panel.add(createLabel("Full Name:"));
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(15));
        
        // Email
        panel.add(createLabel("Email:"));
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(emailField);
        panel.add(Box.createVerticalStrut(15));
        
        // Monthly Budget
        panel.add(createLabel("Monthly Budget ($):"));
        budgetField = new JTextField(20);
        budgetField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(budgetField);
        
        return panel;
    }
    
    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(createLabel("Current Password:"));
        currentPasswordField = new JPasswordField(20);
        currentPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(currentPasswordField);
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createLabel("New Password:"));
        newPasswordField = new JPasswordField(20);
        newPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(newPasswordField);
        panel.add(Box.createVerticalStrut(15));
        
        panel.add(createLabel("Confirm New Password:"));
        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(confirmPasswordField);
        
        // Password requirements label
        JLabel requirementsLabel = new JLabel(
            "<html><small>Password must be at least 6 characters long</small></html>"
        );
        requirementsLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        requirementsLabel.setForeground(Color.GRAY);
        requirementsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(requirementsLabel);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private void loadUserData() {
        usernameField.setText(currentUser.getUsername());
        nameField.setText(currentUser.getFullName());
        emailField.setText(currentUser.getEmail());
        budgetField.setText(String.valueOf(currentUser.getMonthlyBudget()));
    }
    
    private void saveProfile() {
        try {
            // Get values from personal info
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();
            double monthlyBudget;
            
            try {
                monthlyBudget = Double.parseDouble(budgetField.getText().trim());
                if (monthlyBudget < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid budget amount!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
                budgetField.requestFocus();
                return;
            }
            
            // Update personal info
            authManager.updateProfile(fullName, email, monthlyBudget);
            
            // Check if password needs to be changed
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                // All password fields must be filled
                if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "Please fill all password fields to change password!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if new passwords match
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, 
                        "New passwords don't match!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check password length
                if (newPassword.length() < 6) {
                    JOptionPane.showMessageDialog(this, 
                        "New password must be at least 6 characters!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Change password
                if (!authManager.changePassword(currentPassword, newPassword)) {
                    JOptionPane.showMessageDialog(this, 
                        "Current password is incorrect!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            updated = true;
            JOptionPane.showMessageDialog(this, 
                "Profile updated successfully!", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error updating profile: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean isUpdated() {
        return updated;
    }
}