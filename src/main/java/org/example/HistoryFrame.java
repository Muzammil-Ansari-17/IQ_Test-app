package org.example;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class HistoryFrame extends JFrame {
    private int userId;
    private String username;

    public HistoryFrame(int userId, String username) {
        this.userId = userId;
        this.username = username;

        setTitle("Test History");
        setSize(900, 600);
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

        JLabel titleLabel = new JLabel("Your Test History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(new Color(248, 250, 252));

        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);

        String[] columns = {"Date", "Score", "Total", "Percentage", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Actions column
            }
        };

        JTable table = new JTable(model);
        styleTable(table);

        // Add button renderer and editor for Actions column
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox(), this));

        loadHistory(model);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(51, 65, 85), 1));
        scrollPane.setBackground(new Color(30, 41, 59));

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        JButton backButton = new JButton("Back to Menu");
        styleButton(backButton, new Color(59, 130, 246), new Color(37, 99, 235));
        backButton.addActionListener(e -> {
            dispose();
            new MenuFrame(userId, username);
        });

        buttonPanel.add(backButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HistoryFrame(1, "TestUser"));
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Inter", Font.PLAIN, 13));
        table.setForeground(new Color(203, 213, 225));
        table.setBackground(new Color(30, 41, 59));
        table.setRowHeight(50);
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

        for (int i = 0; i < 4; i++) { // Not for Actions column
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void loadHistory(DefaultTableModel model) {
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT result_id, score, total_questions, date_taken FROM results WHERE user_id = ? ORDER BY date_taken DESC"
            );
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int resultId = rs.getInt("result_id");
                int score = rs.getInt("score");
                int total = rs.getInt("total_questions");
                Timestamp dateTaken = rs.getTimestamp("date_taken");
                double percentage = (score * 100.0) / total;

                String dateStr = dateTaken.toLocalDateTime().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"));

                model.addRow(new Object[]{
                        dateStr,
                        score,
                        total,
                        String.format("%.1f%%", percentage),
                        resultId // Store resultId for the button action
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

    // Button Renderer for table
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText("View Details");
            setFont(new Font("Inter", Font.BOLD, 12));
            setForeground(new Color(248, 250, 252));
            setBackground(new Color(59, 130, 246));
            setBorderPainted(false);
            setFocusPainted(false);
            return this;
        }
    }

    // Button Editor for table
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int resultId;
        private JFrame parentFrame;

        public ButtonEditor(JCheckBox checkBox, JFrame parent) {
            super(checkBox);
            this.parentFrame = parent;
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Inter", Font.BOLD, 12));
            button.setForeground(new Color(248, 250, 252));
            button.setBackground(new Color(59, 130, 246));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            resultId = (int) value;
            button.setText("View Details");
            return button;
        }

        public Object getCellEditorValue() {
            new ResultsFrame(userId, resultId);
            return resultId;
        }
    }
}