package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MenuFrame extends JFrame {
    private int userId;
    private String username;

    public MenuFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("IQ Test - Menu");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                Color color1 = new Color(15, 23, 42);
                Color color2 = new Color(30, 41, 59);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Inter", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(248, 250, 252));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("What would you like to do?", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(welcomeLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton startTestButton = createMenuButton("Start New Test", "Begin a fresh IQ test");
        startTestButton.addActionListener(e -> {
            dispose();
            new IQTestFrame(userId);
        });

        JButton viewHistoryButton = createMenuButton("View History", "See your past test results");
        viewHistoryButton.addActionListener(e -> {
            dispose();
            new HistoryFrame(userId, username);
        });

        JButton logoutButton = createMenuButton("Logout", "Sign out of your account");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        contentPanel.add(startTestButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(viewHistoryButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(logoutButton);

        mainPanel.add(contentPanel);
        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MenuFrame(1, "TestUser"));
    }

    private JButton createMenuButton(String title, String description) {
        JButton button = new JButton() {
            private Color normalColor = new Color(30, 41, 59);
            private Color hoverColor = new Color(51, 65, 85);

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isRollover()) {
                    g2d.setColor(hoverColor);
                } else {
                    g2d.setColor(normalColor);
                }

                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(new Color(51, 65, 85));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setLayout(new BorderLayout(15, 5));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        button.setMaximumSize(new Dimension(450, 80));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        titleLabel.setForeground(new Color(248, 250, 252));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        descLabel.setForeground(new Color(148, 163, 184));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(descLabel);

        button.add(textPanel, BorderLayout.CENTER);

        return button;
    }
}