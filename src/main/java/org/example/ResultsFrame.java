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
        JPanel headerPanel = new JPanel(new BorderLayout(0, 20));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel("Detailed Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(new Color(248, 250, 252));

        // Stats Panel
        JPanel statsPanel = createStatsPanel();

        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(statsPanel, BorderLayout.CENTER);

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
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

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
        card.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));
        card.setPreferredSize(new Dimension(200, 120));

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
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.setForeground(new Color(203, 213, 225));
        table.setBackground(new Color(30, 41, 59));
        table.setRowHeight(45);
        table.setShowGrid(true);
        table.setGridColor(new Color(51, 65, 85));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(new Color(51, 65, 85));
        table.setSelectionForeground(new Color(248, 250, 252));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setForeground(new Color(226, 232, 240));
        header.setBackground(new Color(15, 23, 42));
        header.setPreferredSize(new Dimension(header.getWidth(), 50));
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
                        setFont(new Font("Inter", Font.BOLD, 16));
                    } else if (value.toString().equals("✗")) {
                        setForeground(new Color(239, 68, 68));
                        setFont(new Font("Inter", Font.BOLD, 16));
                    }
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
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