package Frontend;

import Backend.DijkstraEdge;
import Backend.Delivery;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import Backend.Kruskal;
import Backend.TSP;

public class RoutePage extends JFrame {

    JTextField startField, endField;
    JTextArea resultArea;

    public RoutePage(GraphPanel graphPanel) {

        setTitle("Shortest Route");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // ===== MAIN PANEL WITH NEON GRADIENT =====
        JPanel backgroundPanel = UIUtils.createGradientPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // ===== LEFT PANEL =====
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(300, 0));
        left.setOpaque(false);
        left.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel groupStart = new JPanel();
        groupStart.setLayout(new BoxLayout(groupStart, BoxLayout.Y_AXIS));
        groupStart.setOpaque(false);
        groupStart.setMaximumSize(new Dimension(260, 60)); // Restrict height
        groupStart.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerStart = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerStart.setOpaque(false);
        headerStart.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel startLabel = new JLabel("Start Location:");
        UIUtils.styleLabel(startLabel, 14, true);
        headerStart.add(startLabel);
        groupStart.add(headerStart);

        startField = new JTextField(15);
        startField.setMaximumSize(new Dimension(260, 30));
        startField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startField.setAlignmentX(Component.LEFT_ALIGNMENT);
        groupStart.add(startField);
        
        left.add(groupStart);

        left.add(Box.createVerticalStrut(10));

        JPanel groupEnd = new JPanel();
        groupEnd.setLayout(new BoxLayout(groupEnd, BoxLayout.Y_AXIS));
        groupEnd.setOpaque(false);
        groupEnd.setMaximumSize(new Dimension(260, 60)); // Restrict height
        groupEnd.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerEnd = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerEnd.setOpaque(false);
        headerEnd.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel endLabel = new JLabel("End Location:");
        UIUtils.styleLabel(endLabel, 14, true);
        headerEnd.add(endLabel);
        groupEnd.add(headerEnd);
        
        endField = new JTextField(15);
        endField.setMaximumSize(new Dimension(260, 30));
        endField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endField.setAlignmentX(Component.LEFT_ALIGNMENT);
        groupEnd.add(endField);

        left.add(groupEnd);

        left.add(Box.createVerticalStrut(20));

        JButton dijkstraBtn = new JButton("Perform Dijkstra");
        JButton kruskalBtn = new JButton("Perform Kruskal");
        JButton tspBtn = new JButton("Perform TSP");

        UIUtils.styleButton(dijkstraBtn);
        UIUtils.styleButton(kruskalBtn);
        UIUtils.styleButton(tspBtn);

        dijkstraBtn.addActionListener(e -> {

            String startName = startField.getText().trim();
            String endName = endField.getText().trim();

            List<List<DijkstraEdge>> graph = graphPanel.buildGraph();

            int start = graphPanel.getNodeIndex(startName);
            int end = graphPanel.getNodeIndex(endName);

            if (start == -1 || end == -1) {
                resultArea.setText("❌ Invalid location names!");
                return;
            }

            String output = Delivery.findpshort(graph, start, end, graphPanel);
            resultArea.setText(output);
        });
        kruskalBtn.addActionListener(e -> {
            String output = Kruskal.runKruskal(graphPanel);
            resultArea.setText(output);
        });

        tspBtn.addActionListener(e -> {
            String output = TSP.runTSP(graphPanel);
            resultArea.setText(output);
        });

        dijkstraBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        kruskalBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        tspBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        left.add(dijkstraBtn);
        left.add(Box.createVerticalStrut(15));
        left.add(kruskalBtn);
        left.add(Box.createVerticalStrut(15));
        left.add(tspBtn);

        // ===== CENTER (GRAPH) =====
        graphPanel.setOpaque(false);
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
        
        add(left, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER);

        // ===== RESULT AREA (EAST) =====
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Consolas", Font.BOLD, 15));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setOpaque(false); // Fix ghosting
        resultArea.setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(resultArea) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 80)); // Safe semi-transparent background
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        scrollPane.setPreferredSize(new Dimension(380, 0));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        add(scrollPane, BorderLayout.EAST);

        // ===== PROJECT INFO AT BOTTOM OF LEFT PANEL =====
        left.add(Box.createVerticalGlue());
        
        JLabel infoTitle = new JLabel("System Insights");
        UIUtils.styleLabel(infoTitle, 16, true);
        infoTitle.setForeground(UIUtils.NEON_PRIMARY);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(infoTitle);
        left.add(Box.createVerticalStrut(10));

        JTextArea infoText = new JTextArea();
        infoText.setText("• Dijkstra: Finds exact shortest path between 2 nodes.\n" +
                         "• Kruskal: Connects all nodes with minimum total distance.\n" +
                         "• TSP: Finds the most efficient circular tour for all nodes.");
        infoText.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoText.setForeground(Color.LIGHT_GRAY);
        infoText.setBackground(new Color(0,0,0,0));
        infoText.setEditable(false);
        infoText.setLineWrap(true);
        infoText.setWrapStyleWord(true);
        infoText.setMaximumSize(new Dimension(260, 100));
        infoText.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(infoText);

        setVisible(true);
    }
}