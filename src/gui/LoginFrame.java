package gui;

import core.AuthManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private AuthManager authManager;
    
    // Components
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, signupButton;
    
    public LoginFrame() {
        authManager = new AuthManager();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Expense Tracker - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(52, 152, 219);
                Color color2 = new Color(41, 128, 185);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout());
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("💰 Expense Tracker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Track • Analyze • Save", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 220));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        
        // Username field
        JPanel usernamePanel = createInputPanel("👤 Username");
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernamePanel.add(usernameField);
        formPanel.add(usernamePanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Password field
        JPanel passwordPanel = createInputPanel("🔒 Password");
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordPanel.add(passwordField);
        formPanel.add(passwordPanel);
        
        // Login button
        loginButton = createStyledButton("Login", new Color(46, 204, 113));
        loginButton.addActionListener(e -> performLogin());
        
        // Signup button
        signupButton = createStyledButton("Sign Up", new Color(52, 152, 219));
        signupButton.addActionListener(e -> openSignupFrame());
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        
        // Add enter key listener
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
        
        // Add components
        formPanel.add(buttonPanel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createInputPanel(String labelText) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.WHITE);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        panel.add(label, BorderLayout.NORTH);
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(new Color(240, 240, 240));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
        });
        
        return button;
    }
    
   private void performLogin() {
    String username = usernameField.getText().trim();
    String password = new String(passwordField.getPassword());
    
    if (authManager.login(username, password)) {
        System.out.println("LOGIN SUCCESS - Creating Dashboard..."); // Debug print
        
        // Show success message
        JOptionPane.showMessageDialog(this,
            "Login successful! Welcome, " + username + "!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
        
        // Create and show dashboard
        DashboardFrame dashboard = new DashboardFrame(authManager);
        dashboard.setVisible(true);
        
        // Close login window
        this.dispose();
        
    } else {
        JOptionPane.showMessageDialog(this,
            "Invalid username or password!",
            "Login Failed", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void openSignupFrame() {
        SignupDialog signupDialog = new SignupDialog(this, authManager);
        signupDialog.setVisible(true);
    }
}

// Inner class for Signup Dialog
class SignupDialog extends JDialog {
    private AuthManager authManager;
    private JTextField usernameField, emailField, nameField;
    private JPasswordField passwordField, confirmPasswordField;
    
    public SignupDialog(JFrame parent, AuthManager authManager) {
        super(parent, "Sign Up", true);
        this.authManager = authManager;
        initializeUI();
    }
    
    private void initializeUI() {
        setSize(400, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Form fields
        mainPanel.add(createLabeledField("👤 Username:", usernameField = new JTextField(20)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLabeledField("👤 Full Name:", nameField = new JTextField(20)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLabeledField("📧 Email:", emailField = new JTextField(20)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLabeledField("🔒 Password:", passwordField = new JPasswordField(20)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createLabeledField("✓ Confirm Password:", confirmPasswordField = new JPasswordField(20)));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(46, 204, 113));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.addActionListener(e -> performSignup());
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(signupButton);
        buttonPanel.add(cancelButton);
        
        mainPanel.add(buttonPanel);
        add(mainPanel);
    }
    
    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }
    
    private void performSignup() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        String name = nameField.getText().trim();
        
        // Validation
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "Passwords don't match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 6 characters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            authManager.register(username, password, email, name);
            JOptionPane.showMessageDialog(this, 
                "Registration successful! You can now login.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                "Registration failed: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}