package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * LoginFrame class - Main login screen for the IQ Test application
 * Provides user authentication interface with modern UI design
 */
public class LoginFrame extends JFrame {
    // Input fields for user credentials
    private JTextField usernameField;
    private JPasswordField passwordField;

    /**
     * Constructor - Initializes and displays the login frame
     */
    public LoginFrame() {
        // Basic frame setup
        setTitle("IQ Test - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close application when window closes
        setLocationRelativeTo(null);  // Center the window on screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Open in full screen (maximized)
        setLayout(new BorderLayout());  // Use BorderLayout for main frame

        // Create main panel with custom gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                // Enable high-quality rendering
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Create gradient from dark blue to slightly lighter blue
                Color color1 = new Color(15, 23, 42);   // Dark slate blue
                Color color2 = new Color(30, 41, 59);   // Lighter slate blue
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());  // Center content in the panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form panel - Contains all login form elements
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));  // Vertical layout
        formPanel.setOpaque(false);  // Transparent to show gradient background
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));  // Add padding

        // Title label - "Welcome Back"
        JLabel titleLabel = new JLabel("Welcome", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(new Color(248, 250, 252));  // Light white color
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center horizontally

        // Subtitle label - "Sign in to continue"
        JLabel subtitleLabel = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(148, 163, 184));  // Gray color
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add title and subtitle to form
        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));  // 8px spacing
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 40)));  // 40px spacing

        // Username label
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(203, 213, 225));  // Light gray
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Username input field
        usernameField = createStyledTextField();
        usernameField.setMaximumSize(new Dimension(350, 45));  // Set fixed size

        // Password label
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(203, 213, 225));
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Password input field
        passwordField = createStyledPasswordField();
        passwordField.setMaximumSize(new Dimension(350, 45));

        // Sign In button
        JButton loginButton = new JButton("Sign In");
        styleButton(loginButton, new Color(59, 130, 246), new Color(37, 99, 235));  // Blue colors
        loginButton.setMaximumSize(new Dimension(350, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());  // Handle login on click

        // Register panel - Contains "Don't have an account?" text and Sign Up link
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);

        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        noAccountLabel.setForeground(new Color(148, 163, 184));

        // Sign Up link button
        JButton registerLink = new JButton("Sign Up");
        registerLink.setFont(new Font("Inter", Font.BOLD, 13));
        registerLink.setForeground(new Color(59, 130, 246));  // Blue color
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setFocusPainted(false);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor on hover
        registerLink.addActionListener(e -> {
            dispose();  // Close login frame
            new RegisterFrame();  // Open register frame
        });

        registerPanel.add(noAccountLabel);
        registerPanel.add(registerLink);

        // Add all components to form panel
        formPanel.add(usernameLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(passwordLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(loginButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(registerPanel);

        // Add form panel to main panel
        mainPanel.add(formPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Credits label at the bottom - Shows creator names
        JLabel creditsLabel = new JLabel("<html><center>Created By<br>Muzammil(154),Sohaib(),Ahmed(170),Ibrahim(185)</center></html>", SwingConstants.CENTER);
        creditsLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        creditsLabel.setForeground(new Color(0, 0, 0));  // Black
        creditsLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        add(creditsLabel, BorderLayout.SOUTH);  // Position at bottom

        setVisible(true);  // Make frame visible
    }

    /**
     * Creates a styled text field with custom colors and borders
     * @return Configured JTextField
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setForeground(new Color(203, 213, 225));  // Light gray text
        field.setBackground(new Color(30, 41, 59));  // Dark background
        field.setCaretColor(new Color(203, 213, 225));  // Light gray cursor
        // Create compound border: outer line + inner padding
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),  // Border line
                BorderFactory.createEmptyBorder(10, 15, 10, 15)  // Padding
        ));
        field.setMargin(new Insets(0, 0, 0, 0));  // Remove default margins for vertical centering
        return field;
    }

    /**
     * Creates a styled password field with custom colors and borders
     * @return Configured JPasswordField
     */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setForeground(new Color(203, 213, 225));
        field.setBackground(new Color(30, 41, 59));
        field.setCaretColor(new Color(203, 213, 225));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setMargin(new Insets(0, 0, 0, 0));  // Remove default margins
        return field;
    }

    /**
     * Applies styling to buttons with hover effects
     * @param button Button to style
     * @param normalColor Normal state color
     * @param hoverColor Hover state color
     */
    private void styleButton(JButton button, Color normalColor, Color hoverColor) {
        button.setFont(new Font("Inter", Font.BOLD, 15));
        button.setForeground(new Color(248, 250, 252));  // White text
        button.setBackground(normalColor);
        button.setOpaque(true);
        button.setFocusPainted(false);  // Remove focus border
        button.setBorderPainted(false);  // Remove button border
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));  // Padding

        // Add hover effect using mouse listener
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);  // Change to hover color
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);  // Restore normal color
            }
        });
    }

    /**
     * Handles login button click - Validates credentials and authenticates user
     */
    private void handleLogin() {
        // Get input values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Validate input - Check if fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Connect to database
            Connection conn = DBConnection.getConnection();

            // Prepare SQL query to check credentials
            PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            // Check if user exists
            if (rs.next()) {
                // Login successful - Get user ID
                int userId = rs.getInt("user_id");

                // Close database resources
                rs.close();
                ps.close();
                conn.close();

                // Close login frame and open menu
                dispose();
                new MenuFrame(userId, username);
            } else {
                // Login failed - Invalid credentials
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            // Database error occurred
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method - Entry point of the application
     */
    public static void main(String[] args) {
        // Run on Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}