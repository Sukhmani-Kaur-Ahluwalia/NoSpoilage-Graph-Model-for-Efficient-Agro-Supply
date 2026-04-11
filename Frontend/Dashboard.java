package Frontend;

import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {

        setTitle("Graph Configuration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ✅ FULL SCREEN
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // ===== MAIN PANEL WITH NEON GRADIENT =====
        JPanel backgroundPanel = UIUtils.createGradientPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // ===== TOP TITLE =====
        JLabel title = new JLabel("Graph Configuration", JLabel.CENTER);
        UIUtils.styleLabel(title, 24, true);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // ===== MAIN COMPONENTS =====
        GraphPanel graphPanel = new GraphPanel();
        graphPanel.setOpaque(false); // Make transparent for gradient
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        ControlPanel controlPanel = new ControlPanel(graphPanel);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 1),
            BorderFactory.createEmptyBorder(10, 0, 10, 10)
        ));

        // ===== SPLIT: GRAPH + ABOUT =====
        JPanel aboutPanel = createAboutPanel();
        aboutPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                graphPanel,
                aboutPanel);

        splitPane.setDividerLocation(0.75); // Fixed proportion to prevent overlap
        splitPane.setResizeWeight(0.8);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);
        splitPane.setDividerSize(2);

        // ===== ADD TO FRAME =====
        add(controlPanel, BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // ===== ABOUT PANEL =====
    private JPanel createAboutPanel() {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 80));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(350, 0));
        panel.setOpaque(false);

        // ✅ Border
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // ===== TITLE =====
        JLabel title = new JLabel("About Project");
        UIUtils.styleLabel(title, 18, true);
        title.setForeground(UIUtils.NEON_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ===== CONTENT =====
        JTextArea content = new JTextArea();
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        content.setBackground(new Color(0, 0, 0, 0)); 
        content.setForeground(Color.WHITE); // Make it white as requested
        content.setMargin(new Insets(10, 10, 10, 10));
        content.setCaretPosition(0); // start from top

        content.setText(
                "AgroDirect - NoSpoilage\n\n" +
                        "This project optimizes the supply chain between farms and shops.\n\n" +
                        "Features:\n" +
                        "- Graph-based modeling\n" +
                        "- User-defined nodes and edges\n" +
                        "- Visual representation\n\n" +
                        "Algorithms Used:\n" +
                        "- Dijkstra (Shortest Path)\n" +
                        "- Kruskal (Minimum Spanning Tree)\n" +
                        "- Travelling Salesman Problem (TSP)\n\n" +
                        "Goal:\n" +
                        "Reduce food spoilage and improve delivery efficiency.");

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}