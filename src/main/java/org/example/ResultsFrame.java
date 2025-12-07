package org.example;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class ResultsFrame extends JFrame {
    private int userId;
    private int resultId;

    public ResultsFrame(int userId, int resultId) {
        this.userId = userId;
        this.resultId = resultId;

        setTitle("Test Results");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout(0, 15));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        JLabel titleLabel = new JLabel("Detailed Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 28));
        titleLabel.setForeground(new Color(248, 250, 252));

        // Stats Panel
        JPanel statsPanel = createStatsPanel();

        // IQ Assessment Panel
        JPanel iqPanel = createIQAssessmentPanel();

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(statsPanel, BorderLayout.CENTER);
        headerPanel.add(iqPanel, BorderLayout.SOUTH);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        String[] columns = {"Question", "Your Answer", "Correct Answer", "Result"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        loadResults(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        scrollPane.setBackground(new Color(30, 41, 59));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(51, 65, 85), new Color(71, 85, 105));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResultsFrame(1, 1));
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 15, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT score, total_questions, date_taken FROM results WHERE result_id = ?");
            ps.setInt(1, resultId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int score = rs.getInt("score");
                int total = rs.getInt("total_questions");
                double percentage = (score * 100.0) / total;

                panel.add(createStatCard("Score", score + "/" + total, new Color(59, 130, 246)));
                panel.add(createStatCard("Percentage", String.format("%.1f%%", percentage), new Color(34, 197, 94)));
                panel.add(createStatCard("Incorrect", (total - score) + "", new Color(239, 68, 68)));
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return panel;
    }

    private JPanel createIQAssessmentPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 41, 59),
                        getWidth(), 0, new Color(15, 23, 42));
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Border
                g2d.setColor(new Color(59, 130, 246));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 15, 15);
            }
        };
        panel.setLayout(new BorderLayout(20, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 30, 18, 30));

        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT score, total_questions FROM results WHERE result_id = ?");
            ps.setInt(1, resultId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int score = rs.getInt("score");
                int total = rs.getInt("total_questions");
                double percentage = (score * 100.0) / total;

                // Calculate estimated IQ score (standard IQ scale: mean=100, SD=15)
                int estimatedIQ = calculateIQ(percentage);
                String rating = getIQRating(estimatedIQ);
                Color ratingColor = getIQColor(estimatedIQ);

                // Left side - IQ Score
                JPanel leftPanel = new JPanel();
                leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
                leftPanel.setOpaque(false);

                JLabel iqLabel = new JLabel("Estimated IQ");
                iqLabel.setFont(new Font("Inter", Font.PLAIN, 12));
                iqLabel.setForeground(new Color(148, 163, 184));
                iqLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel iqScoreLabel = new JLabel(String.valueOf(estimatedIQ));
                iqScoreLabel.setFont(new Font("Inter", Font.BOLD, 38));
                iqScoreLabel.setForeground(ratingColor);
                iqScoreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                leftPanel.add(iqLabel);
                leftPanel.add(Box.createRigidArea(new Dimension(0, 3)));
                leftPanel.add(iqScoreLabel);

                // Right side - Rating and Description
                JPanel rightPanel = new JPanel();
                rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
                rightPanel.setOpaque(false);

                JLabel ratingLabel = new JLabel(rating);
                ratingLabel.setFont(new Font("Inter", Font.BOLD, 20));
                ratingLabel.setForeground(new Color(248, 250, 252));
                ratingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel descLabel = new JLabel("<html>" + getIQDescription(estimatedIQ) + "</html>");
                descLabel.setFont(new Font("Inter", Font.PLAIN, 12));
                descLabel.setForeground(new Color(148, 163, 184));
                descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

                rightPanel.add(ratingLabel);
                rightPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                rightPanel.add(descLabel);

                panel.add(leftPanel, BorderLayout.WEST);
                panel.add(rightPanel, BorderLayout.CENTER);
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return panel;
    }

    private int calculateIQ(double percentage) {
        // Map percentage to IQ score (standard scale)
        // This is a simplified calculation
        if (percentage >= 95) return 145;  // Very Superior
        if (percentage >= 90) return 135;  // Superior
        if (percentage >= 80) return 125;  // High Average
        if (percentage >= 70) return 115;  // Above Average
        if (percentage >= 50) return 100;  // Average
        if (percentage >= 40) return 90;   // Below Average
        if (percentage >= 30) return 85;   // Low Average
        return 75;                          // Borderline
    }

    private String getIQRating(int iq) {
        if (iq >= 140) return "Genius";
        if (iq >= 130) return "Very Superior";
        if (iq >= 120) return "Superior";
        if (iq >= 110) return "High Average";
        if (iq >= 90) return "Average";
        if (iq >= 80) return "Low Average";
        return "Below Average";
    }

    private Color getIQColor(int iq) {
        if (iq >= 140) return new Color(139, 92, 246);  // Purple
        if (iq >= 130) return new Color(59, 130, 246);  // Blue
        if (iq >= 120) return new Color(34, 197, 94);   // Green
        if (iq >= 110) return new Color(45, 212, 191);  // Teal
        if (iq >= 90) return new Color(251, 191, 36);   // Yellow
        if (iq >= 80) return new Color(251, 146, 60);   // Orange
        return new Color(239, 68, 68);                  // Red
    }

    private String getIQDescription(int iq) {
        if (iq >= 140) return "Exceptional intelligence! You're in the top 0.1% of the population.";
        if (iq >= 130) return "Outstanding performance! You're in the top 2% of the population.";
        if (iq >= 120) return "Excellent result! Above average intelligence.";
        if (iq >= 110) return "Great job! Higher than average cognitive abilities.";
        if (iq >= 90) return "Good performance. You fall within the normal range.";
        if (iq >= 80) return "Decent effort. Room for improvement with practice.";
        return "Keep practicing to improve your cognitive skills.";
    }

    private JPanel createStatCard(String label, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(30, 41, 59));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.setColor(new Color(51, 65, 85));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setPreferredSize(new Dimension(180, 85));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Inter", Font.PLAIN, 13));
        labelText.setForeground(new Color(148, 163, 184));
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(labelText);

        return card;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Inter", Font.PLAIN, 13));
        table.setForeground(new Color(203, 213, 225));
        table.setBackground(new Color(30, 41, 59));
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(51, 65, 85));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(new Color(51, 65, 85));
        table.setSelectionForeground(new Color(248, 250, 252));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 13));
        header.setForeground(new Color(226, 232, 240));
        header.setBackground(new Color(15, 23, 42));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(59, 130, 246)));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                if (!isSelected) {
                    setBackground(new Color(30, 41, 59));
                    setForeground(new Color(203, 213, 225));
                }

                // Color code the result column
                if (column == 3 && value != null) {
                    if (value.toString().equals("✓")) {
                        setForeground(new Color(34, 197, 94));
                        setFont(new Font("Inter", Font.BOLD, 15));
                    } else if (value.toString().equals("✗")) {
                        setForeground(new Color(239, 68, 68));
                        setFont(new Font("Inter", Font.BOLD, 15));
                    }
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(90);
    }

    private void loadResults(DefaultTableModel model) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT a.question_id, a.chosen_option, a.is_correct, q.question_text, q.correct_option " +
                            "FROM attempts a JOIN questions q ON a.question_id = q.question_id " +
                            "WHERE a.result_id = ? ORDER BY a.question_id"
            );
            ps.setInt(1, resultId);
            ResultSet rs = ps.executeQuery();

            int qNum = 1;
            while (rs.next()) {
                String chosen = rs.getString("chosen_option");
                int correct = rs.getInt("correct_option");
                boolean isCorrect = rs.getBoolean("is_correct");

                model.addRow(new Object[]{
                        "Q" + qNum++,
                        chosen,
                        correct,
                        isCorrect ? "✓" : "✗"
                });
            }

            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void styleButton(JButton button, Color normalColor, Color hoverColor) {
        button.setFont(new Font("Inter", Font.BOLD, 15));
        button.setForeground(new Color(248, 250, 252));
        button.setBackground(normalColor);
        button.setOpaque(true);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 40, 12, 40));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(normalColor);
            }
        });
    }
}