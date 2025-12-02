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
        setSize(900, 700);
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Detailed Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(new Color(248, 250, 252));

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Stats Panel
        JPanel statsPanel = createStatsPanel();

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

        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(51, 65, 85), new Color(71, 85, 105));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResultsFrame(1, 1));
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 20, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

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
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Inter", Font.PLAIN, 14));
        labelText.setForeground(new Color(148, 163, 184));
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(valueLabel);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(labelText);

        return card;
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Inter", Font.PLAIN, 13));
        table.setForeground(new Color(203, 213, 225));
        table.setBackground(new Color(30, 41, 59));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(51, 65, 85));
        table.setSelectionForeground(new Color(248, 250, 252));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 13));
        header.setForeground(new Color(148, 163, 184));
        header.setBackground(new Color(15, 23, 42));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(51, 65, 85)));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setBackground(new Color(30, 41, 59));
        centerRenderer.setForeground(new Color(203, 213, 225));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
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
                String questionText = rs.getString("question_text");
                if (questionText.length() > 50) {
                    questionText = questionText.substring(0, 47) + "...";
                }

                String chosen = rs.getString("chosen_option");
                String correct = rs.getString("correct_option");
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