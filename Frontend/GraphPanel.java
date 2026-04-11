package Frontend;

import Backend.DijkstraEdge;
import javax.swing.*;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {

    public ArrayList<Node> nodes = new ArrayList<>();
    public ArrayList<Edge> edges = new ArrayList<>();

    public GraphPanel() {

        setOpaque(false);

        int centerX = 380;
        int centerY = 280;
        int radius = 200;

        // ===== FARMS (TOP HALF) =====
        for (int i = 0; i < 8; i++) {

            double angle = Math.PI * (i + 1) / (8 + 1);

            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY - radius * Math.sin(angle));

            nodes.add(new Node("Farm " + (i + 1), x, y, "FARM"));
        }

        // ===== SHOPS (BOTTOM HALF) =====
        for (int i = 0; i < 5; i++) {

            double angle = Math.PI * (i + 1) / (5 + 1);

            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));

            nodes.add(new Node("Shop " + (i + 1), x, y, "SHOP"));
        }
    }

    private void recalculatePositions() {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 3;

        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * (i + 1) / (8 + 1);
            nodes.get(i).x = (int) (centerX + radius * Math.cos(angle + Math.PI));
            nodes.get(i).y = (int) (centerY + radius * Math.sin(angle + Math.PI));
        }

        for (int i = 0; i < 5; i++) {
            double angle = Math.PI * (i + 1) / (5 + 1);
            nodes.get(i + 8).x = (int) (centerX + radius * Math.cos(angle));
            nodes.get(i + 8).y = (int) (centerY + radius * Math.sin(angle));
        }
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getWidth() > 0) recalculatePositions();
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ===== DRAW EDGES =====
        for (Edge e : edges) {
            g2.setColor(UIUtils.NEON_PRIMARY);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(e.n1.x, e.n1.y, e.n2.x, e.n2.y);
        }

        // ===== DRAW NODES =====
        for (Node n : nodes) {
            int size = 50; // Slightly larger for better text fit
            if (n.type.equals("FARM")) {
                g2.setColor(new Color(0, 255, 127)); // Neon Green
            } else {
                g2.setColor(new Color(255, 105, 180)); // Hot Pink
            }
            g2.fillOval(n.x - size / 2, n.y - size / 2, size, size);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(n.x - size / 2, n.y - size / 2, size, size);

            // Center text inside circle
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics metrics = g2.getFontMetrics();
            int tx = n.x - (metrics.stringWidth(n.name) / 2);
            int ty = n.y - (metrics.getHeight() / 2) + metrics.getAscent();
            
            g2.setColor(Color.BLACK); // Black text on neon background for contrast
            g2.drawString(n.name, tx, ty);
        }

        // ===== DRAW WEIGHT LABELS (LAST TO AVOID OVERLAP) =====
        for (Edge e : edges) {
            int midX = (e.n1.x + e.n2.x) / 2;
            int midY = (e.n1.y + e.n2.y) / 2;

            int dx = e.n2.x - e.n1.x;
            int dy = e.n2.y - e.n1.y;
            double length = Math.sqrt(dx * dx + dy * dy);

            if (length == 0) continue;

            // Offset to avoid line overlap
            int offsetX = (int) (-dy / length * 25);
            int offsetY = (int) (dx / length * 25);

            int textX = midX + offsetX;
            int textY = midY + offsetY;

            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRoundRect(textX - 10, textY - 12, 30, 18, 8, 8);

            g2.setColor(UIUtils.NEON_SECONDARY);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString("" + e.weight, textX, textY);
        }
    }

    public void updateNodeNames(String[] names) {

        for (int i = 0; i < nodes.size(); i++) {
            if (!names[i].isEmpty()) {
                nodes.get(i).name = names[i];
            }
        }

        repaint();
    }

    public void addEdge(String from, String to, int weight) {

        Node n1 = null, n2 = null;

        for (Node n : nodes) {
            if (n.name.equalsIgnoreCase(from))
                n1 = n;
            if (n.name.equalsIgnoreCase(to))
                n2 = n;
        }

        if (n1 != null && n2 != null) {
            // Check if edge already exists and update it
            for (Edge e : edges) {
                if ((e.n1 == n1 && e.n2 == n2) || (e.n1 == n2 && e.n2 == n1)) {
                    e.weight = weight;
                    repaint();
                    return;
                }
            }
            edges.add(new Edge(n1, n2, weight));
            repaint();
        }
    }

    // ===== BUILD GRAPH FOR DIJKSTRA =====
    public List<List<DijkstraEdge>> buildGraph() {

        int n = nodes.size();
        List<List<DijkstraEdge>> graph = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        for (Edge e : edges) {
            int i = nodes.indexOf(e.n1);
            int j = nodes.indexOf(e.n2);

            graph.get(i).add(new DijkstraEdge(j, e.weight));
            graph.get(j).add(new DijkstraEdge(i, e.weight));
        }

        return graph;
    }

    // ===== NAME → INDEX =====
    public int getNodeIndex(String name) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    // ===== 🔥 NEW: INDEX → NAME =====
    public String getNodeName(int index) {
        return nodes.get(index).name;
    }
}