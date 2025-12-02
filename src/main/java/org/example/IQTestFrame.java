package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.Timer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IQTestFrame extends JFrame {
    private JLabel questionLabel;
    private JButton[] optionButtons;
    private int currentQuestion = 1;
    private int score = 0;
    private JLabel scoreLabel;
    private JLabel questionNumberLabel;
    private JProgressBar progressBar;
    private int totalQuestions = 20;
    private int userId;
    private int resultId;

    public IQTestFrame() {
        this(1); // Default test user for testing
    }

    public IQTestFrame(int userId) {
        this.userId = userId;
        setTitle("IQ Test App");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Dark themed main panel
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();

                // Dark gradient background
                Color color1 = new Color(15, 23, 42);
                Color color2 = new Color(30, 41, 59);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);

                // Subtle grid pattern
                g2d.setColor(new Color(255, 255, 255, 5));
                for (int i = 0; i < w; i += 40) {
                    g2d.drawLine(i, 0, i, h);
                }
                for (int i = 0; i < h; i += 40) {
                    g2d.drawLine(0, i, w, i);
                }
            }
        };
        mainPanel.setLayout(new BorderLayout(0, 0));

        // Top Panel - Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 45, 20, 45));

        JLabel titleLabel = new JLabel("IQ Test", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 38));
        titleLabel.setForeground(new Color(248, 250, 252));

        // Score and Question Number Panel
        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        infoPanel.setOpaque(false);

        questionNumberLabel = new JLabel("Question 1/" + totalQuestions, SwingConstants.LEFT);
        questionNumberLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        questionNumberLabel.setForeground(new Color(148, 163, 184));

        scoreLabel = new JLabel("Score: 0", SwingConstants.RIGHT);
        scoreLabel.setFont(new Font("Inter", Font.BOLD, 16));
        scoreLabel.setForeground(new Color(34, 197, 94));

        infoPanel.add(questionNumberLabel);
        infoPanel.add(scoreLabel);

        JPanel topContainer = new JPanel(new BorderLayout(0, 18));
        topContainer.setOpaque(false);
        topContainer.add(titleLabel, BorderLayout.NORTH);
        topContainer.add(infoPanel, BorderLayout.CENTER);

        // Progress Bar
        progressBar = new JProgressBar(0, totalQuestions);
        progressBar.setValue(1);
        progressBar.setStringPainted(false);
        progressBar.setPreferredSize(new Dimension(0, 6));
        progressBar.setOpaque(false);
        progressBar.setBackground(new Color(51, 65, 85));
        progressBar.setForeground(new Color(59, 130, 246));
        progressBar.setBorder(BorderFactory.createEmptyBorder(12, 45, 0, 45));

        headerPanel.add(topContainer, BorderLayout.CENTER);
        headerPanel.add(progressBar, BorderLayout.SOUTH);

        // Center Panel - Question
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(35, 55, 35, 55));

        JPanel questionContainer = new JPanel(new BorderLayout());
        questionContainer.setOpaque(false);

        questionLabel = new JLabel("<html><center>Question will appear here</center></html>", SwingConstants.CENTER);
        questionLabel.setFont(new Font("Inter", Font.PLAIN, 20));
        questionLabel.setForeground(new Color(226, 232, 240));
        questionLabel.setOpaque(true);
        questionLabel.setBackground(new Color(30, 41, 59));
        questionLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(35, 30, 35, 30)
        ));

        questionContainer.add(questionLabel, BorderLayout.CENTER);
        centerPanel.add(questionContainer, BorderLayout.CENTER);

        // Bottom Panel - Options
        JPanel optionsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        optionsPanel.setOpaque(false);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 55, 55, 55));

        optionButtons = new JButton[4];
        String[] optionLabels = {"A", "B", "C", "D"};

        for (int i = 0; i < 4; i++) {
            optionButtons[i] = createStyledButton("Option " + (i + 1), optionLabels[i]);
            String finalLabel = optionLabels[i];
            optionButtons[i].addActionListener(e -> {
                animateButtonClick(optionButtons[getOptionIndex(finalLabel)]);
                checkAnswer(finalLabel);
            });
            optionsPanel.add(optionButtons[i]);
        }

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(optionsPanel, BorderLayout.SOUTH);

        add(mainPanel);

        initializeTest();
        loadQuestion(currentQuestion);
        setVisible(true);
    }

    private int getOptionIndex(String label) {
        switch(label) {
            case "A": return 0;
            case "B": return 1;
            case "C": return 2;
            case "D": return 3;
            default: return 0;
        }
    }

    private JButton createStyledButton(String text, String label) {
        JButton button = new JButton() {
            private Color normalColor = new Color(30, 41, 59);
            private Color hoverColor = new Color(51, 65, 85);
            private Color pressColor = new Color(71, 85, 105);

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
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // Border
                g2d.setColor(new Color(51, 65, 85));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setLayout(new BorderLayout(12, 0));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        // Option Label (A, B, C, D)
        JLabel labelTag = new JLabel(label);
        labelTag.setFont(new Font("Inter", Font.BOLD, 18));
        labelTag.setForeground(new Color(59, 130, 246));
        labelTag.setOpaque(true);
        labelTag.setBackground(new Color(59, 130, 246, 20));
        labelTag.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        labelTag.setPreferredSize(new Dimension(45, 35));
        labelTag.setHorizontalAlignment(SwingConstants.CENTER);

        // Option Text
        JLabel textLabel = new JLabel("<html>" + text + "</html>");
        textLabel.setFont(new Font("Inter", Font.PLAIN, 15));
        textLabel.setForeground(new Color(203, 213, 225));

        button.add(labelTag, BorderLayout.WEST);
        button.add(textLabel, BorderLayout.CENTER);

        return button;
    }

    private void animateButtonClick(JButton button) {
        button.setEnabled(false);
        Timer timer = new Timer(400, e -> button.setEnabled(true));
        timer.setRepeats(false);
        timer.start();
    }

    private void updateUI() {
        scoreLabel.setText("Score: " + score);
        questionNumberLabel.setText("Question " + currentQuestion + "/" + totalQuestions);
        progressBar.setValue(currentQuestion);
    }

    private void initializeTest() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO results(user_id, score, total_questions, date_taken) VALUES (?, 0, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setInt(1, userId);
            ps.setInt(2, totalQuestions);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                resultId = rs.getInt(1);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadQuestion(int qId) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM questions WHERE question_id = ?");
            ps.setInt(1, qId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                questionLabel.setText("<html><center>" + rs.getString("question_text") + "</center></html>");

                // Update button text labels
                String[] options = {
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d")
                };

                for (int i = 0; i < 4; i++) {
                    Component[] components = optionButtons[i].getComponents();
                    for (Component comp : components) {
                        if (comp instanceof JLabel && comp.getParent() == optionButtons[i]) {
                            JLabel textLabel = (JLabel) comp;
                            if (textLabel.getHorizontalAlignment() != SwingConstants.CENTER) {
                                textLabel.setText("<html>" + options[i] + "</html>");
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

    private void checkAnswer(String selectedOption) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT correct_option FROM questions WHERE question_id = ?");
            ps.setInt(1, currentQuestion);
            ResultSet rs = ps.executeQuery();

            boolean isCorrect = false;
            if (rs.next()) {
                String correctOption = rs.getString("correct_option");
                isCorrect = selectedOption.equalsIgnoreCase(correctOption);
                if (isCorrect) {
                    score++;
                }
            }

            // Record attempt
            PreparedStatement psAttempt = conn.prepareStatement(
                    "INSERT INTO attempts(result_id, question_id, chosen_option, is_correct) VALUES (?, ?, ?, ?)"
            );
            psAttempt.setInt(1, resultId);
            psAttempt.setInt(2, currentQuestion);
            psAttempt.setString(3, selectedOption);
            psAttempt.setBoolean(4, isCorrect);
            psAttempt.executeUpdate();
            psAttempt.close();

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
        updateFinalScore();

        JDialog dialog = new JDialog(this, "Test Completed!", true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout(0, 30)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth(), h = getHeight();

                Color color1 = new Color(15, 23, 42);
                Color color2 = new Color(30, 41, 59);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 20, 20);

                // Border
                g2d.setColor(new Color(51, 65, 85));
                g2d.drawRoundRect(0, 0, w - 1, h - 1, 20, 20);
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(50, 55, 50, 55));

        JLabel iconLabel = new JLabel("✓", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Inter", Font.BOLD, 72));
        iconLabel.setForeground(new Color(34, 197, 94));

        JLabel titleLabel = new JLabel("Test Complete", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(new Color(248, 250, 252));

        double percentage = (score * 100.0) / totalQuestions;
        JLabel percentageLabel = new JLabel(String.format("%.0f%%", percentage), SwingConstants.CENTER);
        percentageLabel.setFont(new Font("Inter", Font.BOLD, 56));
        percentageLabel.setForeground(new Color(59, 130, 246));

        JLabel scoreLabel = new JLabel(score + " / " + totalQuestions + " correct", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Inter", Font.PLAIN, 18));
        scoreLabel.setForeground(new Color(148, 163, 184));

        JButton viewResultsButton = new JButton("View Results");
        styleButton(viewResultsButton, new Color(59, 130, 246), new Color(37, 99, 235));
        viewResultsButton.addActionListener(e -> {
            dialog.dispose();
            showResultsFrame();
        });

        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(51, 65, 85), new Color(71, 85, 105));
        closeButton.addActionListener(e -> {
            dialog.dispose();
            dispose();
        });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewResultsButton);
        buttonPanel.add(closeButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        percentageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(iconLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        contentPanel.add(percentageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        contentPanel.add(scoreLabel);

        panel.add(contentPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void styleButton(JButton button, Color normalColor, Color hoverColor) {
        button.setFont(new Font("Inter", Font.BOLD, 15));
        button.setForeground(new Color(248, 250, 252));
        button.setBackground(normalColor);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(14, 30, 14, 30));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }

    private void updateFinalScore() {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("UPDATE results SET score = ? WHERE result_id = ?");
            ps.setInt(1, score);
            ps.setInt(2, resultId);
            ps.executeUpdate();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showResultsFrame() {
        new ResultsFrame(userId, resultId);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new IQTestFrame());
    }
}