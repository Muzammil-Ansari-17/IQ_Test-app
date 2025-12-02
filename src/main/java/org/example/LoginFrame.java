package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("IQ Test - Login");
        setSize(450, 600);
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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(new Color(248, 250, 252));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        formPanel.add(titleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        formPanel.add(subtitleLabel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 40)));

        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        usernameLabel.setForeground(new Color(203, 213, 225));
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        usernameField = createStyledTextField();
        usernameField.setMaximumSize(new Dimension(350, 45));

        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(203, 213, 225));
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        passwordField = createStyledPasswordField();
        passwordField.setMaximumSize(new Dimension(350, 45));

        JButton loginButton = new JButton("Sign In");
        styleButton(loginButton, new Color(59, 130, 246), new Color(37, 99, 235));
        loginButton.setMaximumSize(new Dimension(350, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setOpaque(false);

        JLabel noAccountLabel = new JLabel("Don't have an account?");
        noAccountLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        noAccountLabel.setForeground(new Color(148, 163, 184));

        JButton registerLink = new JButton("Sign Up");
        registerLink.setFont(new Font("Inter", Font.BOLD, 13));
        registerLink.setForeground(new Color(59, 130, 246));
        registerLink.setBorderPainted(false);
        registerLink.setContentAreaFilled(false);
        registerLink.setFocusPainted(false);
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.addActionListener(e -> {
            dispose();
            new RegisterFrame();
        });

        registerPanel.add(noAccountLabel);
        registerPanel.add(registerLink);

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

        mainPanel.add(formPanel);
        add(mainPanel);
        setVisible(true);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setForeground(new Color(203, 213, 225));
        field.setBackground(new Color(30, 41, 59));
        field.setCaretColor(new Color(203, 213, 225));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }

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
        return field;
    }

    private void styleButton(JButton button, Color normalColor, Color hoverColor) {
        button.setFont(new Font("Inter", Font.BOLD, 15));
        button.setForeground(new Color(248, 250, 252));
        button.setBackground(normalColor);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT user_id FROM users WHERE username = ? AND password = ?");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                rs.close();
                ps.close();
                conn.close();
                dispose();
                new MenuFrame(userId, username);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame());
    }
}