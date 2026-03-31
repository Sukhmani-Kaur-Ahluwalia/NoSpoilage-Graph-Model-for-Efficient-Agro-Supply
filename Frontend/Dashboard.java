import javax.swing.*;
import java.awt.*;

public class Dashboard extends JFrame {

    public Dashboard() {

        setTitle("Graph Configuration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ✅ FULL SCREEN
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        // ===== TOP TITLE =====
        JLabel title = new JLabel("Graph Configuration", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // ===== MAIN COMPONENTS =====
        GraphPanel graphPanel = new GraphPanel();
        ControlPanel controlPanel = new ControlPanel(graphPanel);

        // ===== SPLIT: GRAPH + ABOUT =====
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                graphPanel,
                createAboutPanel());

        splitPane.setDividerLocation(800);
        splitPane.setResizeWeight(0.7);

        // ===== ADD TO FRAME =====
        add(controlPanel, BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    // ===== ABOUT PANEL =====
    private JPanel createAboutPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBackground(new Color(245, 245, 245));

        // ✅ Border
        panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // ===== TITLE =====
        JLabel title = new JLabel("About Project");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        title.setForeground(new Color(34, 139, 34));

        // ===== CONTENT =====
        JTextArea content = new JTextArea();
        content.setEditable(false);
        content.setLineWrap(true);
        content.setWrapStyleWord(true);
        content.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        content.setBackground(new Color(245, 245, 245));
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

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }
}