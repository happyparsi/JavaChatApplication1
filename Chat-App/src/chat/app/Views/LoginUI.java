package chat.app.Views;

import chat.app.Client.Client;
import chat.app.Models.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class LoginUI extends JFrame {
    private JPanel mainPanel;
    private JPanel loginPanel;
    private JPanel registerPanel;
    private CardLayout cardLayout;
    private Client client;

    public LoginUI() {
        setTitle("Chat Application - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Initialize client
        try {
            client = new Client();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to server: " + e.getMessage());
            System.exit(1);
        }
        
        createLoginPanel();
        createRegisterPanel();
        
        mainPanel.add(loginPanel, "login");
        mainPanel.add(registerPanel, "register");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
    }
    
    private void createLoginPanel() {
        loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
        loginPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        loginPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("Welcome Back!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        styleTextField(usernameField, "Username");
        styleTextField(passwordField, "Password");
        
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        
        JButton toRegisterButton = new JButton("Need an account? Register");
        styleButton(toRegisterButton);
        toRegisterButton.setForeground(new Color(100, 100, 100));
        
        loginButton.addActionListener(e -> handleLogin(usernameField.getText(), new String(passwordField.getPassword())));
        toRegisterButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        
        loginPanel.add(Box.createVerticalStrut(50));
        loginPanel.add(titleLabel);
        loginPanel.add(Box.createVerticalStrut(30));
        loginPanel.add(usernameField);
        loginPanel.add(Box.createVerticalStrut(15));
        loginPanel.add(passwordField);
        loginPanel.add(Box.createVerticalStrut(20));
        loginPanel.add(loginButton);
        loginPanel.add(Box.createVerticalStrut(10));
        loginPanel.add(toRegisterButton);
    }
    
    private void createRegisterPanel() {
        registerPanel = new JPanel();
        registerPanel.setLayout(new BoxLayout(registerPanel, BoxLayout.Y_AXIS));
        registerPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        registerPanel.setBackground(new Color(245, 245, 245));
        
        JLabel titleLabel = new JLabel("Create Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        styleTextField(usernameField, "Username");
        styleTextField(passwordField, "Password");
        styleTextField(confirmPasswordField, "Confirm Password");
        
        JButton registerButton = new JButton("Register");
        styleButton(registerButton);
        
        JButton toLoginButton = new JButton("Already have an account? Login");
        styleButton(toLoginButton);
        toLoginButton.setForeground(new Color(100, 100, 100));
        
        registerButton.addActionListener(e -> handleRegister(usernameField.getText(), 
                                                           new String(passwordField.getPassword()),
                                                           new String(confirmPasswordField.getPassword())));
        toLoginButton.addActionListener(e -> cardLayout.show(mainPanel, "login"));
        
        registerPanel.add(Box.createVerticalStrut(50));
        registerPanel.add(titleLabel);
        registerPanel.add(Box.createVerticalStrut(30));
        registerPanel.add(usernameField);
        registerPanel.add(Box.createVerticalStrut(15));
        registerPanel.add(passwordField);
        registerPanel.add(Box.createVerticalStrut(15));
        registerPanel.add(confirmPasswordField);
        registerPanel.add(Box.createVerticalStrut(20));
        registerPanel.add(registerButton);
        registerPanel.add(Box.createVerticalStrut(10));
        registerPanel.add(toLoginButton);
    }
    
    private void styleTextField(JTextField field, String placeholder) {
        field.setMaximumSize(new Dimension(300, 40));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar('•');
        }
        
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('•');
                    }
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }
    
    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(300, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBackground(new Color(66, 133, 244));
        button.setForeground(Color.WHITE);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(51, 103, 214));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(66, 133, 244));
            }
        });
    }
    
    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty() || 
            username.equals("Username") || password.equals("Password")) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }
        
        try {
            boolean success = client.login(username, password);
            if (success) {
                // Open chat window
                new ChatUI(client, username).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void handleRegister(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
            username.equals("Username") || password.equals("Password") || 
            confirmPassword.equals("Confirm Password")) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match");
            return;
        }
        
        try {
            boolean success = client.register(username, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
                cardLayout.show(mainPanel, "login");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginUI().setVisible(true);
        });
    }
} 