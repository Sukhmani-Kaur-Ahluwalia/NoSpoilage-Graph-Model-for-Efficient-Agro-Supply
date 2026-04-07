import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphPanel extends JPanel {

    ArrayList<Node> nodes = new ArrayList<>();
    ArrayList<Edge> edges = new ArrayList<>();

    public GraphPanel() {

        setBackground(Color.WHITE);

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

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ===== DRAW EDGES =====
        for (Edge e : edges) {
            g.setColor(Color.BLACK);
            g.drawLine(e.n1.x, e.n1.y, e.n2.x, e.n2.y);

            int midX = (e.n1.x + e.n2.x) / 2;
            int midY = (e.n1.y + e.n2.y) / 2;

            int dx = e.n2.x - e.n1.x;
            int dy = e.n2.y - e.n1.y;

            double length = Math.sqrt(dx * dx + dy * dy);

            int offsetX = (int) (-dy / length * 15);
            int offsetY = (int) (dx / length * 15);

            int textX = midX + offsetX;
            int textY = midY + offsetY;

            g.setColor(Color.WHITE);
            g.fillRect(textX - 10, textY - 12, 25, 18);

            g.setColor(Color.RED);
            g.drawString("" + e.weight, textX, textY);
        }

        // ===== DRAW NODES =====
        for (Node n : nodes) {

            int size = 45;

            if (n.type.equals("FARM")) {
                g.setColor(new Color(34, 139, 34));
            } else {
                g.setColor(new Color(70, 130, 180));
            }

            g.fillRect(n.x - size / 2, n.y - size / 2, size, size);

            g.setColor(Color.BLACK);
            g.drawRect(n.x - size / 2, n.y - size / 2, size, size);

            g.setColor(Color.WHITE);
            g.drawString(n.name, n.x - 20, n.y + 5);
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