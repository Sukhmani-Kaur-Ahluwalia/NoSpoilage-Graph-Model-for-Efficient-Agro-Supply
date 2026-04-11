package Frontend;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    JTextField[] nodeFields = new JTextField[13];
    JTextField fromField, toField, weightField;
    GraphPanel graphPanel;

    public ControlPanel(GraphPanel graphPanel) {

        this.graphPanel = graphPanel;

        // Removed fixed preferred height to allow vertical expansion for scrolling
        setPreferredSize(new Dimension(280, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10)); // 0 left padding

        JPanel headerA = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerA.setOpaque(false);
        headerA.setMaximumSize(new Dimension(260, 25));
        JLabel labelA = new JLabel("A. Define Locations (Nodes)");
        UIUtils.styleLabel(labelA, 15, true);
        headerA.add(labelA);
        add(headerA);
        add(Box.createVerticalStrut(5));

        for (int i = 0; i < 13; i++) {
            JPanel row = new JPanel(new BorderLayout(5, 0));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(260, 22));

            JLabel nodeLabel = new JLabel(i < 8 ? "Farm " + (i + 1) + ":" : "Shop " + (i - 7) + ":");
            UIUtils.styleLabel(nodeLabel, 11, false);
            nodeLabel.setPreferredSize(new Dimension(65, 22));
            
            nodeFields[i] = new JTextField(10);
            nodeFields[i].setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            row.add(nodeLabel, BorderLayout.WEST);
            row.add(nodeFields[i], BorderLayout.CENTER);
            
            add(row);
            add(Box.createVerticalStrut(2));

            int index = i;
            nodeFields[i].addActionListener(e -> {
                if (index < 12) nodeFields[index + 1].requestFocus();
            });
        }

        JButton updateBtn = new JButton("Update Locations");
        UIUtils.styleButton(updateBtn);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateBtn.setPreferredSize(new Dimension(260, 28)); // Increased width
        updateBtn.setMaximumSize(new Dimension(260, 28));

        updateBtn.addActionListener(e -> {

            String[] names = new String[13];

            for (int i = 0; i < 13; i++) {
                names[i] = nodeFields[i].getText();
            }

            graphPanel.updateNodeNames(names);
        });

        updateBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(updateBtn);
        add(Box.createVerticalStrut(10));

        JPanel headerB = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerB.setOpaque(false);
        headerB.setMaximumSize(new Dimension(260, 25));
        JLabel labelB = new JLabel("B. Define Connections (Edges)");
        UIUtils.styleLabel(labelB, 15, true);
        headerB.add(labelB);
        add(headerB);
        add(Box.createVerticalStrut(5));

        fromField = new JTextField(10);
        toField = new JTextField(10);
        weightField = new JTextField(10);

        fromField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        weightField.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Group B inputs in rows
        JPanel rowFrom = new JPanel(new BorderLayout(5, 0));
        rowFrom.setOpaque(false);
        rowFrom.setMaximumSize(new Dimension(260, 24));
        JLabel fromLabel = new JLabel("From:");
        UIUtils.styleLabel(fromLabel, 11, false);
        fromLabel.setPreferredSize(new Dimension(65, 24));
        rowFrom.add(fromLabel, BorderLayout.WEST);
        rowFrom.add(fromField, BorderLayout.CENTER);
        add(rowFrom);
        add(Box.createVerticalStrut(3));

        JPanel rowTo = new JPanel(new BorderLayout(5, 0));
        rowTo.setOpaque(false);
        rowTo.setMaximumSize(new Dimension(260, 24));
        JLabel toLabel = new JLabel("To:");
        UIUtils.styleLabel(toLabel, 11, false);
        toLabel.setPreferredSize(new Dimension(65, 24));
        rowTo.add(toLabel, BorderLayout.WEST);
        rowTo.add(toField, BorderLayout.CENTER);
        add(rowTo);
        add(Box.createVerticalStrut(3));

        JPanel rowDist = new JPanel(new BorderLayout(5, 0));
        rowDist.setOpaque(false);
        rowDist.setMaximumSize(new Dimension(260, 24));
        JLabel distLabel = new JLabel("Distance:");
        UIUtils.styleLabel(distLabel, 11, false);
        distLabel.setPreferredSize(new Dimension(65, 24));
        rowDist.add(distLabel, BorderLayout.WEST);
        rowDist.add(weightField, BorderLayout.CENTER);
        add(rowDist);
        add(Box.createVerticalStrut(10));

        JButton addEdgeBtn = new JButton("Update Distance");
        UIUtils.styleButton(addEdgeBtn);
        addEdgeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addEdgeBtn.setPreferredSize(new Dimension(260, 28)); // Increased width
        addEdgeBtn.setMaximumSize(new Dimension(260, 28));
        addEdgeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        addEdgeBtn.addActionListener(e -> {
            String from = fromField.getText();
            String to = toField.getText();
            int weight = 0;

            try {
                weight = Integer.parseInt(weightField.getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid number!");
                return;
            }
            // Use GraphPanel to add or update edge
            graphPanel.addEdge(from, to, weight);

            // clear fields
            fromField.setText("");
            toField.setText("");
            weightField.setText("");
        });

        addEdgeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(addEdgeBtn);
        add(Box.createVerticalStrut(20));

        // 🔥 NEXT PAGE BUTTON
        JButton nextBtn = new JButton("Find Shortest Route");
        UIUtils.styleButton(nextBtn);
        nextBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        nextBtn.setPreferredSize(new Dimension(260, 28)); // Increased width
        nextBtn.setMaximumSize(new Dimension(260, 28));
        nextBtn.setBorder(BorderFactory.createLineBorder(UIUtils.NEON_SECONDARY, 2));
        nextBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        nextBtn.addActionListener(e -> {
            new RoutePage(graphPanel);
        });

        add(nextBtn);
        add(Box.createVerticalStrut(20));
    }
}
