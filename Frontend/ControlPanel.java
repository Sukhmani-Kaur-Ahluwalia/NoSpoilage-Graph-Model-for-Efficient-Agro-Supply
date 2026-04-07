import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ControlPanel extends JPanel {

    JTextField[] nodeFields = new JTextField[13];
    JTextField fromField, toField, weightField;
    GraphPanel graphPanel;

    public ControlPanel(GraphPanel graphPanel) {

        this.graphPanel = graphPanel;

        setPreferredSize(new Dimension(280, 0));
        setLayout(new GridLayout(0, 1, 3, 3));
        add(new JLabel("A. Define Locations (Nodes)"));

        for (int i = 0; i < 13; i++) {

            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));

            if (i < 8)
                row.add(new JLabel("Farm " + (i + 1) + ":"));
            else
                row.add(new JLabel("Shop " + (i - 7) + ":"));

            nodeFields[i] = new JTextField(8);

            int index = i;
            nodeFields[i].addActionListener(e -> {
                if (index < 12)
                    nodeFields[index + 1].requestFocus();
            });

            row.add(nodeFields[i]);
            add(row);
        }

        JButton updateBtn = new JButton("Update Locations");

        updateBtn.addActionListener(e -> {

            String[] names = new String[13];

            for (int i = 0; i < 13; i++) {
                names[i] = nodeFields[i].getText();
            }

            graphPanel.updateNodeNames(names);
        });

        add(updateBtn);
        add(Box.createVerticalStrut(10));

        add(new JLabel("B. Define Connections (Edges)"));

        fromField = new JTextField(10);
        toField = new JTextField(10);
        weightField = new JTextField(10);

        add(new JLabel("From:"));
        add(fromField);

        add(new JLabel("To:"));
        add(toField);

        add(new JLabel("Distance:"));
        add(weightField);

        JButton addEdgeBtn = new JButton("Add Connection");

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
            graphPanel.addEdge(from, to, weight);

            // clear fields
            fromField.setText("");
            toField.setText("");
            weightField.setText("");
        });

        add(addEdgeBtn);

        // 🔥 NEXT PAGE BUTTON
        JButton nextBtn = new JButton("Find Shortest Route");

        nextBtn.addActionListener(e -> {
            new RoutePage(graphPanel);
        });

        add(Box.createVerticalStrut(10));
        add(nextBtn);
    }
}
