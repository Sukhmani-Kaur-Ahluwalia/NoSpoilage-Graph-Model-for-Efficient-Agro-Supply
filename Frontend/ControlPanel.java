// ControlPanel.java - the left-side panel on the main dashboard
// lets the user define delivery locations (nodes) and connections (edges)

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class ControlPanel extends JPanel {

    // text fields for entering node names (8 farms + 5 shops = 13 total)
    JTextField[] nodeNameFields = new JTextField[13];

    // fields for entering a new edge
    JTextField toLocationField;
JTextField distanceField;
    JTextField fromLocationField;

    // reference to the main graph panel
    GraphPanel graphPanel;

    // backend engine for reading/writing files
    private final graphHnadle graphFileHandler;

    public ControlPanel(GraphPanel mainGraphPanel) {

        this.graphPanel = mainGraphPanel;
        this.graphFileHandler = new graphHnadle();

        // load any previously saved graph data when the panel opens
        try {
            graphFileHandler.loadGraphInto(graphPanel);
        } catch (IOException loadException) {
             System.out.println("Catched Exception. Cannot load graph.");
            JOptionPane.showMessageDialog(this,
                    "Could not load previously saved delivery graph data.\n" + loadException.getMessage(),
                    "Load Warning",
                    JOptionPane.WARNING_MESSAGE);
                  }

        setPreferredSize(new Dimension(280, 0));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));

        // Define Delivery Locations
        JPanel sectionAHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sectionAHeader.setMaximumSize(new Dimension(260, 25));
          sectionAHeader.setOpaque(false);
        JLabel sectionALabel = new JLabel("A. Define Delivery Locations");
        UIUtils.styleLabel(sectionALabel, 15, true);
        sectionAHeader.add(sectionALabel);
        add(sectionAHeader);
        add(Box.createVerticalStrut(5));

        // create one text field for each node
        for (int fieldIdx = 0; fieldIdx < 13; fieldIdx++) {
            JPanel fieldRow = new JPanel(new BorderLayout(5, 0));
            fieldRow.setOpaque(false);
            fieldRow.setMaximumSize(new Dimension(260, 22));

            // label says "Farm 1:" or "Shop 1:" etc.
            String rowLabelText;
            if (fieldIdx < 8) 
            {
                rowLabelText = "Farm " + (fieldIdx + 1) + ":";
                        }        else {

                rowLabelText = "Shop " + (fieldIdx - 7) + ":";
                      }

            JLabel rowLabel = new JLabel(rowLabelText);
            rowLabel.setPreferredSize(new Dimension(65, 22));
           UIUtils.styleLabel(rowLabel, 11, false);

            nodeNameFields[fieldIdx] = new JTextField(10);
            nodeNameFields[fieldIdx].setFont(new Font("Segoe UI", Font.PLAIN, 11));
            // pre-fill with current node name
            nodeNameFields[fieldIdx].setText(graphPanel.nodes.get(fieldIdx).name);

            fieldRow.add(rowLabel, BorderLayout.WEST);
            fieldRow.add(nodeNameFields[fieldIdx], BorderLayout.CENTER);

            add(fieldRow);
            add(Box.createVerticalStrut(2));

            // pressing Enter moves focus to the next field
            final int currentFieldIndex = fieldIdx;
            nodeNameFields[fieldIdx].addActionListener(e -> {
                if (currentFieldIndex < 12) {
                    nodeNameFields[currentFieldIndex + 1].requestFocus();
                }
            });
        }

        // button to apply the node name changes
        JButton updateLocationsButton = new JButton("Update Locations");
        UIUtils.styleButton(updateLocationsButton);
        updateLocationsButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        updateLocationsButton.setPreferredSize(new Dimension(260, 28));
        updateLocationsButton.setMaximumSize(new Dimension(260, 28));

        updateLocationsButton.addActionListener(e -> {
            // collect all names from the text fields
            String[] newNodeNames = new String[13];
            for (int nameIdx = 0; nameIdx < 13; nameIdx++) {
                newNodeNames[nameIdx] = nodeNameFields[nameIdx].getText().trim();
            }

            // check for duplicate names (case-insensitive)
            // go through each pair manually
            boolean foundDuplicate = false;
            String duplicateName = "";

            for (int outerIdx = 0; outerIdx < 13; outerIdx++) {
                if (newNodeNames[outerIdx].isEmpty()) {
                    continue;
                }
                for (int innerIdx = outerIdx + 1; innerIdx < 13; innerIdx++) {
                    if (newNodeNames[innerIdx].isEmpty()) {
                        continue;
                    }
                    if (newNodeNames[outerIdx].equalsIgnoreCase(newNodeNames[innerIdx])) {
                        foundDuplicate = true;
                       System.out.println("Entered if condtion. Duplicate name.");
                        duplicateName = newNodeNames[outerIdx];
                        break;
                    }
                }
                if (foundDuplicate) {
                    break;
                }
            }

            if (foundDuplicate) {
                JOptionPane.showMessageDialog(this,
                        "Location names must be unique. Duplicate found: " + duplicateName);
                return;
            }

            // apply names to the graph panel
            graphPanel.updateNodeNames(newNodeNames);

            // save to file
            try {
                graphFileHandler.saveCurrentGraph(graphPanel);
            } catch (IOException saveException) {
                JOptionPane.showMessageDialog(this,
                        "Locations were updated on screen, but could not save to nodes.txt.\n" + saveException.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        updateLocationsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(updateLocationsButton);
        add(Box.createVerticalStrut(10));

        // Define Delivery Connections
        JPanel sectionBHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sectionBHeader.setOpaque(false);
        sectionBHeader.setMaximumSize(new Dimension(260, 25));
        JLabel sectionBLabel = new JLabel("B. Define Route Connections");
        UIUtils.styleLabel(sectionBLabel, 15, true);
         add(sectionBHeader);
        sectionBHeader.add(sectionBLabel);
       
        add(Box.createVerticalStrut(5));

        // create the from/to/distance input fields
             toLocationField = new JTextField(10);
        fromLocationField = new JTextField(10);
      
        distanceField = new JTextField(10);

        fromLocationField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toLocationField.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        distanceField.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        // "From" row
        JPanel fromRow = new JPanel(new BorderLayout(5, 0));
        fromRow.setOpaque(false);
        fromRow.setMaximumSize(new Dimension(260, 24));
        JLabel fromLabel = new JLabel("From:");
        UIUtils.styleLabel(fromLabel, 11, false);
        fromLabel.setPreferredSize(new Dimension(65, 24));
        fromRow.add(fromLabel, BorderLayout.WEST);
        fromRow.add(fromLocationField, BorderLayout.CENTER);
        add(fromRow);
        add(Box.createVerticalStrut(3));

        // "To" row
        JPanel toRow = new JPanel(new BorderLayout(5, 0));
        toRow.setOpaque(false);
        toRow.setMaximumSize(new Dimension(260, 24));
        JLabel toLabel = new JLabel("To:");
        UIUtils.styleLabel(toLabel, 11, false);
        toLabel.setPreferredSize(new Dimension(65, 24));
        toRow.add(toLabel, BorderLayout.WEST);
        toRow.add(toLocationField, BorderLayout.CENTER);
        add(toRow);
        add(Box.createVerticalStrut(3));

        // "Distance" row
        JPanel distanceRow = new JPanel(new BorderLayout(5, 0));
        distanceRow.setOpaque(false);
        distanceRow.setMaximumSize(new Dimension(260, 24));
        JLabel distanceLabel = new JLabel("Distance:");
        UIUtils.styleLabel(distanceLabel, 11, false);
        distanceLabel.setPreferredSize(new Dimension(65, 24));
        distanceRow.add(distanceLabel, BorderLayout.WEST);
        distanceRow.add(distanceField, BorderLayout.CENTER);
        add(distanceRow);
        add(Box.createVerticalStrut(10));

        // button to add/update the edge
        JButton addConnectionButton = new JButton("Update Distance");
        UIUtils.styleButton(addConnectionButton);
        addConnectionButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        addConnectionButton.setPreferredSize(new Dimension(260, 28));
        addConnectionButton.setMaximumSize(new Dimension(260, 28));
        addConnectionButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        addConnectionButton.addActionListener(e -> {
            String fromName = fromLocationField.getText().trim();
            String toName = toLocationField.getText().trim();

            // validate inputs
            if (fromName.isEmpty() || toName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both source and destination delivery locations.");
                return;
            }

            if (fromName.equalsIgnoreCase(toName)) {
                JOptionPane.showMessageDialog(this, "Source and destination must be different locations.");
                return;
            }

            int distanceValue;
            try {
                distanceValue = Integer.parseInt(distanceField.getText().trim());
            } catch (NumberFormatException parseException) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for distance.");
                return;
            }

            if (distanceValue < 0) {
                JOptionPane.showMessageDialog(this, "Distance cannot be a negative number.");
                return;
            }

            // make sure both node names actually exist in the graph
            if (graphPanel.getNodeIndex(fromName) == -1 || graphPanel.getNodeIndex(toName) == -1) {
                JOptionPane.showMessageDialog(this, "Both location names must match existing delivery locations on the graph.");
                return;
            }

            // add the edge to the graph
            graphPanel.addEdge(fromName, toName, distanceValue);

            // save updated graph to file
            try {
                graphFileHandler.saveCurrentGraph(graphPanel);
            } catch (IOException saveException) {
                JOptionPane.showMessageDialog(this,
                        "Distance was updated on screen but could not save to edges.txt.\n" + saveException.getMessage(),
                        "Save Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // clear input fields after successful add
            fromLocationField.setText("");
            toLocationField.setText("");
            distanceField.setText("");
        });

        addConnectionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(addConnectionButton);
        add(Box.createVerticalStrut(20));

        // NAVIGATION BUTTONS
        JButton openDijkstraButton = new JButton("Perform Dijkstra");
        UIUtils.styleButton(openDijkstraButton);
        openDijkstraButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        openDijkstraButton.setPreferredSize(new Dimension(260, 28));
        openDijkstraButton.setMaximumSize(new Dimension(260, 28));
        openDijkstraButton.setBorder(BorderFactory.createLineBorder(UIUtils.NEON_SECONDARY, 2));
        openDijkstraButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        openDijkstraButton.addActionListener(e -> new TrafficSimulationPage(graphPanel));

        JButton openMstTspButton = new JButton("Open MST / TSP");
        UIUtils.styleButton(openMstTspButton);
        openMstTspButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        openMstTspButton.setPreferredSize(new Dimension(260, 28));
        openMstTspButton.setMaximumSize(new Dimension(260, 28));
        openMstTspButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        openMstTspButton.addActionListener(e -> new RoutePage(graphPanel));

        add(openDijkstraButton);
        add(Box.createVerticalStrut(10));
        add(openMstTspButton);
        add(Box.createVerticalStrut(20));
    }
}
