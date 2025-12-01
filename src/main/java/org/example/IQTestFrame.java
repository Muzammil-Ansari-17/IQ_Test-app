package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.Timer;

public class IQTestFrame extends JFrame {
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private int currentQuestion = 1;
    private int score = 0;
    private JLabel scoreLabel;
    private JLabel questionNumberLabel;
    private JProgressBar progressBar;
    private int totalQuestions = 20; // Adjust based on your database

    public IQTestFrame() {
        setTitle("IQ Test App");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Main panel with gradient background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(67, 97, 238);
                Color color2 = new Color(128, 90, 213);
                Color color3 = new Color(255, 107, 107);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Add decorative circles
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillOval(-100, -100, 300, 300);
                g2d.fillOval(w - 200, h - 200, 300, 300);
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillOval(w - 150, -50, 250, 250);
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));

        // Top Panel - Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 15, 40));

        JLabel titleLabel = new JLabel("🧠 IQ Test Challenge", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        // Score and Question Number Panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        infoPanel.setOpaque(false);

        questionNumberLabel = new JLabel("Question 1/" + totalQuestions, SwingConstants.LEFT);
        questionNumberLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        questionNumberLabel.setForeground(new Color(255, 255, 255, 230));

        scoreLabel = new JLabel("Score: 0", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(255, 255, 255, 230));

        infoPanel.add(questionNumberLabel);
        infoPanel.add(scoreLabel);

        JPanel topContainer = new JPanel(new BorderLayout(0, 15));
        topContainer.setOpaque(false);
        topContainer.add(titleLabel, BorderLayout.NORTH);
        topContainer.add(infoPanel, BorderLayout.CENTER);

        // Progress Bar
        progressBar = new JProgressBar(0, totalQuestions);
        progressBar.setValue(1);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 8));
        progressBar.setOpaque(false);
        progressBar.setBackground(new Color(255, 255, 255, 100));
        progressBar.setForeground(new Color(255, 255, 255, 200));
        progressBar.setBorder(BorderFactory.createEmptyBorder(10, 40, 0, 40));

        headerPanel.add(topContainer, BorderLayout.CENTER);
        headerPanel.add(progressBar, BorderLayout.SOUTH);

        // Center Panel - Question
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        JPanel questionContainer = new JPanel(new BorderLayout());
        questionContainer.setOpaque(false);

        questionLabel = new JLabel("<html><center>Question will appear here</center></html>", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        questionLabel.setForeground(new Color(30, 30, 30));
        questionLabel.setOpaque(true);
        questionLabel.setBackground(new Color(255, 255, 255, 240));
        questionLabel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(25, new Color(255, 255, 255, 100)),
                BorderFactory.createEmptyBorder(40, 35, 40, 35)
        ));

        questionContainer.add(questionLabel, BorderLayout.CENTER);
        centerPanel.add(questionContainer, BorderLayout.CENTER);

        // Bottom Panel - Options
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));

        optionButtons = new JButton[4];
        String[] optionLabels = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createStyledButton("Option " + (i + 1), optionLabels[i]);
            int finalI = i + 1;
            optionButtons[i].addActionListener(e -> {
                animateButtonClick(optionButtons[finalI - 1]);
                checkAnswer(finalI);
            });
            optionsPanel.add(optionButtons[i]);
        }

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(optionsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        loadQuestion(currentQuestion);
        setVisible(true);
    }

    private JButton createStyledButton(String text, String label) {
        JButton button = new JButton() {
            private Color normalColor = new Color(255, 255, 255, 220);
            private Color hoverColor = new Color(255, 255, 255, 255);
            private Color pressColor = new Color(240, 248, 255, 255);

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color bgColor = normalColor;
                if (getModel().isPressed()) {
                    bgColor = pressColor;
                } else if (getModel().isRollover()) {
                    bgColor = hoverColor;
                }

                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 30, 30);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setLayout(new BorderLayout(15, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Option Label (A, B, C, D)
        JLabel labelTag = new JLabel(label);
        labelTag.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelTag.setForeground(new Color(67, 97, 238));
        labelTag.setOpaque(true);
        labelTag.setBackground(new Color(67, 97, 238, 30));
        labelTag.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        labelTag.setPreferredSize(new Dimension(50, 40));
        labelTag.setHorizontalAlignment(SwingConstants.CENTER);

        // Option Text
        JLabel textLabel = new JLabel("<html>" + text + "</html>");
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        textLabel.setForeground(new Color(50, 50, 50));

        button.add(labelTag, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);

        return button;
    }

    private void animateButtonClick(JButton button) {
        button.setEnabled(false);
        Timer timer = new Timer(300, e -> button.setEnabled(true));
        timer.setRepeats(false);
        timer.start();
    }

    private void updateUI() {
        scoreLabel.setText("Score: " + score);
        questionNumberLabel.setText("Question " + currentQuestion + "/" + totalQuestions);
        progressBar.setValue(currentQuestion);
    }

    private void loadQuestion(int qId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("Select * from questions where id = ?");
            ps.setInt(1, qId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                questionLabel.setText("<html><center>" + rs.getString("question_text") + "</center></html>");

                // Update button text labels
                for (int i = 0; i < 4; i++) {
                    Component[] components = optionButtons[i].getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JLabel && comp.getParent() == optionButtons[i]) {
                            JLabel textLabel = (JLabel) comp;
                            if (textLabel.getHorizontalAlignment() != SwingConstants.CENTER) {
                                textLabel.setText("<html>" + rs.getString("option" + (i + 1)) + "</html>");
                            }
                        }
                    }
                }

                updateUI();
            } else {
                showCompletionDialog();
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading question: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkAnswer(int selectedOption) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT correct_option from questions where id = ?");
            ps.setInt(1, currentQuestion);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && selectedOption == rs.getInt("correct_option")) {
                score++;
            }
            rs.close();
            ps.close();
            conn.close();

            currentQuestion++;
            loadQuestion(currentQuestion);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showCompletionDialog() {
        JDialog dialog = new JDialog(this, "Test Completed!", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout(0, 25)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();
                Color color1 = new Color(67, 97, 238);
                Color color2 = new Color(128, 90, 213);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 40, 40);

                // Decorative elements
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillOval(-50, -50, 200, 200);
                g2d.fillOval(w - 150, h - 150, 200, 200);
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(45, 50, 45, 50));

        JLabel iconLabel = new JLabel("🎉", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 72));

        JLabel titleLabel = new JLabel("Test Finished!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel scoreLabel = new JLabel("Your Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        scoreLabel.setForeground(new Color(255, 255, 255, 230));

        double percentage = (score * 100.0) / totalQuestions;
        JLabel percentageLabel = new JLabel(String.format("%.0f%%", percentage), SwingConstants.CENTER);
        percentageLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        percentageLabel.setForeground(new Color(255, 255, 255));

        JButton closeButton = new JButton("Finish & Save");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        closeButton.setForeground(new Color(67, 97, 238));
        closeButton.setBackground(Color.WHITE);
        closeButton.setOpaque(true);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        closeButton.addActionListener(e -> {
            dialog.dispose();
            saveScore();
            System.exit(0);
        });

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(iconLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(percentageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        contentPanel.add(scoreLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);

        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public void saveScore() {
        String name = JOptionPane.showInputDialog(this, "Enter your name");
        if (name != null && !name.trim().isEmpty()) {
            try {
                Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("insert into user(name,score) values (?,?)");
                ps.setString(1, name);
                ps.setInt(2, score);
                ps.executeUpdate();
                ps.close();
                conn.close();
                JOptionPane.showMessageDialog(this, "Score saved successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error saving score: " + e.getMessage());
            }
        }
    }

    // Custom Border Class
    static class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius, this.radius, this.radius, this.radius);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}