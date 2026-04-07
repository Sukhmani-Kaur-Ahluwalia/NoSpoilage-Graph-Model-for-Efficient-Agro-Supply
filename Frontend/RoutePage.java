import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RoutePage extends JFrame {

    JTextField startField, endField;
    JTextArea resultArea;

    public RoutePage(GraphPanel graphPanel) {

        setTitle("Shortest Route");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // ===== LEFT PANEL =====
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(300, 600));

        left.add(new JLabel("Start Location:"));
        startField = new JTextField(15);
        left.add(startField);

        left.add(Box.createVerticalStrut(10));

        left.add(new JLabel("End Location:"));
        endField = new JTextField(15);
        left.add(endField);

        left.add(Box.createVerticalStrut(20));

        JButton findBtn = new JButton("Find Route");

        findBtn.addActionListener(e -> {

            String startName = startField.getText().trim();
            String endName = endField.getText().trim();

            // get graph from GraphPanel
            List<List<DijkstraEdge>> graph = graphPanel.buildGraph();

            // convert names → index
            int start = graphPanel.getNodeIndex(startName);
            int end = graphPanel.getNodeIndex(endName);

            // validation
            if (start == -1 || end == -1) {
                resultArea.setText("❌ Invalid location names!\nPlease enter correct node names.");
                return;
            }

            // 🔥 CALL DIJKSTRA (now returns String)
            String output = Delivery.findpshort(graph, start, end, graphPanel); // 🔥 DISPLAY IN UI
            resultArea.setText(output);
        });

        left.add(findBtn);

        // ===== ADD COMPONENTS =====
        add(left, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER);

        // ===== RESULT AREA =====
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(350, 600));

        add(scrollPane, BorderLayout.EAST);

        setVisible(true);
    }
}