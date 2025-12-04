package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 * MenuFrame class - Main menu screen after successful login
 * Displays options to start test, view history, or logout
 */
public class MenuFrame extends JFrame {
    // User information
    private int userId;
    private String username;

    /**
     * Constructor - Initializes the menu frame with user information
     * @param userId The logged-in user's ID
     * @param username The logged-in user's username
     */
    public MenuFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;

        // Basic frame setup
        setTitle("IQ Test - Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close application when window closes
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Open in full screen (maximized)
        setLocationRelativeTo(null);  // Center the window on screen
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

        // Content panel - Contains all menu elements
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));  // Vertical layout
        contentPanel.setOpaque(false);  // Transparent to show gradient background
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));  // Add padding

        // Welcome label - Personalized greeting with username
        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(248, 250, 252));  // Light white color
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);  // Center horizontally

        // Subtitle label - Prompts user for action
        JLabel subtitleLabel = new JLabel("What would you like to do?", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(148, 163, 184));  // Gray color
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add header elements to content panel
        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));  // 10px spacing
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 50)));  // 50px spacing

        // Start Test button - Opens the IQ test frame
        JButton startTestButton = createMenuButton("Start New Test", "Begin a fresh IQ test");
        startTestButton.addActionListener(e -> {
            dispose();  // Close menu frame
            new IQTestFrame(userId);  // Open test frame
        });

        // View History button - Opens the history frame to show past results
        JButton viewHistoryButton = createMenuButton("View History", "See your past test results");
        viewHistoryButton.addActionListener(e -> {
            dispose();  // Close menu frame
            new HistoryFrame(userId, username);  // Open history frame
        });

        // Logout button - Returns to login screen
        JButton logoutButton = createMenuButton("Logout", "Sign out of your account");
        logoutButton.addActionListener(e -> {
            dispose();  // Close menu frame
            new LoginFrame();  // Open login frame
        });

        // Add all buttons to content panel with spacing
        contentPanel.add(startTestButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));  // 15px spacing
        contentPanel.add(viewHistoryButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));  // 15px spacing
        contentPanel.add(logoutButton);

        // Add content panel to main panel and frame
        mainPanel.add(contentPanel);
        add(mainPanel, BorderLayout.CENTER);  // Add to center of BorderLayout


        setVisible(true);  // Make frame visible
    }

    /**
     * Creates a styled menu button with title and description
     * Features rounded corners and hover effects
     * @param title Main button text
     * @param description Subtitle text below title
     * @return Configured JButton with custom styling
     */
    private JButton createMenuButton(String title, String description) {
        // Create custom button with rounded corners and hover effect
        JButton button = new JButton() {
            private Color normalColor = new Color(30, 41, 59);   // Normal state color
            private Color hoverColor = new Color(51, 65, 85);    // Hover state color

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                // Enable anti-aliasing for smooth rounded corners
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Change background color based on hover state
                if (getModel().isRollover()) {
                    g2d.setColor(hoverColor);  // Mouse is hovering
                } else {
                    g2d.setColor(normalColor);  // Normal state
                }

                // Draw rounded rectangle background
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Draw border around button
                g2d.setColor(new Color(51, 65, 85));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        // Button layout and styling
        button.setLayout(new BorderLayout(15, 5));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));  // Adjusted padding
        button.setMaximumSize(new Dimension(450, 90));  // Increased height from 80 to 90
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Text panel - Contains title and description labels
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));  // Vertical layout
        textPanel.setOpaque(false);  // Transparent background

        // Title label - Main button text
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        titleLabel.setForeground(new Color(248, 250, 252));  // Light white
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description label - Subtitle text
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        descLabel.setForeground(new Color(203, 213, 225));  // Lighter color for better visibility
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add labels to text panel
        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));  // 5px spacing
        textPanel.add(descLabel);

        // Add text panel to button
        button.add(textPanel, BorderLayout.CENTER);

        return button;
    }

    /**
     * Main method - For testing the MenuFrame independently
     */
    public static void main(String[] args) {
        // Run on Event Dispatch Thread for thread safety
        SwingUtilities.invokeLater(() -> new MenuFrame(1, "TestUser"));
    }
}