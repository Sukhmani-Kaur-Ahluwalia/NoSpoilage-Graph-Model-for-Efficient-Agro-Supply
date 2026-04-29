// RoutePage.java - the window for running Kruskal MST and TSP algorithms
// opens from the main dashboard

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class RoutePage extends JFrame {

    // text field for entering the source node for TSP
    JTextField sourceLocationField;

    // text area to show algorithm results
    JTextArea resultOutputArea;

    // backend engine for loading/saving graph
    private final graphHnadle graphFileHandler = new graphHnadle();

    // our own copy of the graph so we don't mess up the original
    private final GraphPanel routeGraphPanel;

    public RoutePage(GraphPanel originalGraphPanel) {

        // make a detached copy so we don't change the dashboard graph
        this.routeGraphPanel = originalGraphPanel.createDetachedCopy();

        setTitle("Delivery Route Algorithms");
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // set up the background panel
        JPanel backgroundPanel = UIUtils.createGradientPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // ===== LEFT SIDE PANEL (controls) =====
        JPanel leftControlPanel = new JPanel();
        leftControlPanel.setLayout(new BoxLayout(leftControlPanel, BoxLayout.Y_AXIS));
        leftControlPanel.setPreferredSize(new Dimension(300, 0));
        leftControlPanel.setOpaque(false);
        leftControlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // source location input section
        JPanel sourceFieldGroup = new JPanel();
        sourceFieldGroup.setLayout(new BoxLayout(sourceFieldGroup, BoxLayout.Y_AXIS));
        sourceFieldGroup.setOpaque(false);
        sourceFieldGroup.setMaximumSize(new Dimension(260, 60));
        sourceFieldGroup.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel sourceHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        sourceHeaderPanel.setOpaque(false);
        sourceHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sourceLabel = new JLabel("Source Location (for TSP):");
        UIUtils.styleLabel(sourceLabel, 14, true);
        sourceHeaderPanel.add(sourceLabel);
        sourceFieldGroup.add(sourceHeaderPanel);

        sourceLocationField = new JTextField(15);
        sourceLocationField.setMaximumSize(new Dimension(260, 30));
        sourceLocationField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sourceLocationField.setAlignmentX(Component.LEFT_ALIGNMENT);
        sourceFieldGroup.add(sourceLocationField);

        leftControlPanel.add(sourceFieldGroup);
        leftControlPanel.add(Box.createVerticalStrut(20));

        // algorithm buttons
        JButton runKruskalButton = new JButton("Perform Kruskal (MST)");
        JButton runTspButton = new JButton("Perform TSP");
        JButton openDijkstraButton = new JButton("Perform Dijkstra");

        UIUtils.styleButton(runKruskalButton);
        UIUtils.styleButton(runTspButton);
        UIUtils.styleButton(openDijkstraButton);

        // kruskal button - finds minimum spanning tree
        runKruskalButton.addActionListener(e -> {
            // reload from saved files first
            if (!reloadGraphFromFiles(routeGraphPanel)) {
                return;
            }

            routeGraphPanel.clearPathHighlight();
            String kruskalOutput = Kruskal.runKruskal(routeGraphPanel);
            resultOutputArea.setText(kruskalOutput + "\n\nMST saved to mst.txt");
        });

        // TSP button - finds optimal tour visiting all delivery stops
        runTspButton.addActionListener(e -> {
            if (!reloadGraphFromFiles(routeGraphPanel)) {
                return;
            }

            String sourceNameInput = sourceLocationField.getText().trim();
            int sourceNodeIndex = routeGraphPanel.getNodeIndex(sourceNameInput);

            if (sourceNodeIndex == -1) {
                routeGraphPanel.clearPathHighlight();
                resultOutputArea.setText("Please enter a valid source delivery location name for TSP.");
                return;
            }

            // run TSP and display result
            TSP.TourResult tspResult = TSP.buildTour(routeGraphPanel, sourceNodeIndex);
            routeGraphPanel.highlightPath(tspResult.traversalPath, routeGraphPanel.edges);
            resultOutputArea.setText(tspResult.summary);
        });

        // dijkstra button - opens traffic simulation page
        openDijkstraButton.addActionListener(e -> new TrafficSimulationPage(routeGraphPanel));

        runKruskalButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        runTspButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        openDijkstraButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftControlPanel.add(runKruskalButton);
        leftControlPanel.add(Box.createVerticalStrut(15));
        leftControlPanel.add(runTspButton);
        leftControlPanel.add(Box.createVerticalStrut(15));
        leftControlPanel.add(openDijkstraButton);

        // ===== CENTER - the graph visualization =====
        routeGraphPanel.setOpaque(false);
        routeGraphPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        add(leftControlPanel, BorderLayout.WEST);
        add(routeGraphPanel, BorderLayout.CENTER);

        // ===== RIGHT SIDE - result output area =====
        resultOutputArea = new JTextArea();
        resultOutputArea.setFont(new Font("Consolas", Font.BOLD, 15));
        resultOutputArea.setEditable(false);
        resultOutputArea.setLineWrap(true);
        resultOutputArea.setWrapStyleWord(true);
        resultOutputArea.setOpaque(false);
        resultOutputArea.setForeground(Color.WHITE);

        // custom scrollpane with dark transparent background
        JScrollPane resultScrollPane = new JScrollPane(resultOutputArea) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 80));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        resultScrollPane.setPreferredSize(new Dimension(380, 0));
        resultScrollPane.setOpaque(false);
        resultScrollPane.getViewport().setOpaque(false);
        resultScrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        add(resultScrollPane, BorderLayout.EAST);

        // info section at the bottom of left panel
        leftControlPanel.add(Box.createVerticalGlue());

        JLabel infoTitleLabel = new JLabel("How These Algorithms Work");
        UIUtils.styleLabel(infoTitleLabel, 16, true);
        infoTitleLabel.setForeground(UIUtils.NEON_PRIMARY);
        infoTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftControlPanel.add(infoTitleLabel);
        leftControlPanel.add(Box.createVerticalStrut(10));

        JTextArea infoDescriptionArea = new JTextArea();
        infoDescriptionArea.setText("Kruskal: Finds the cheapest way to connect all delivery locations (MST).\n"
                + "TSP: Finds the best tour starting from your source that visits every stop.\n"
                + "Dijkstra: Opens the traffic-aware shortest path simulation.");
        infoDescriptionArea.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoDescriptionArea.setForeground(Color.LIGHT_GRAY);
        infoDescriptionArea.setBackground(new Color(0, 0, 0, 0));
        infoDescriptionArea.setEditable(false);
        infoDescriptionArea.setLineWrap(true);
        infoDescriptionArea.setWrapStyleWord(true);
        infoDescriptionArea.setMaximumSize(new Dimension(260, 100));
        infoDescriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftControlPanel.add(infoDescriptionArea);

        // load graph from files when page opens
        reloadGraphFromFiles(routeGraphPanel);
        setVisible(true);
    }

    // helper to load the stored graph - returns false and shows error if it fails
    private boolean reloadGraphFromFiles(GraphPanel targetPanel) {
        try {
            graphFileHandler.loadGraphInto(targetPanel);
            return true;
        } catch (IOException fileLoadException) {
            targetPanel.clearMstAndPathHighlights();
            resultOutputArea.setText("Could not load the saved delivery graph from files.\n" + fileLoadException.getMessage());
            return false;
        }
    }
}

// TrafficSimulationPage - separate window for running Dijkstra with traffic
class TrafficSimulationPage extends JFrame {

    private final graphHnadle graphFileHandler = new graphHnadle();
    private final GraphPanel trafficGraphPanel;

    private final JTextField startLocationField;
    private final JTextField endLocationField;
    private final JTextArea dijkstraResultArea;

    public TrafficSimulationPage(GraphPanel originalGraphPanel) {

        // make a copy so traffic values don't affect the original panel
        this.trafficGraphPanel = originalGraphPanel.createDetachedCopy();

        setTitle("Dijkstra Delivery Route with Traffic");
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel backgroundPanel = UIUtils.createGradientPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // ===== LEFT CONTROL PANEL =====
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(340, 0));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel pageTitleLabel = new JLabel("Dijkstra Delivery Simulator");
        UIUtils.styleLabel(pageTitleLabel, 18, true);
        pageTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(pageTitleLabel);
        leftPanel.add(Box.createVerticalStrut(12));

        JLabel pageDescriptionLabel = new JLabel(
                "<html>Simulates traffic conditions and finds the best delivery route using combined cost = distance + traffic intensity.</html>");
        UIUtils.styleLabel(pageDescriptionLabel, 12, false);
        pageDescriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(pageDescriptionLabel);
        leftPanel.add(Box.createVerticalStrut(20));

        // start location input
        JPanel startFieldPanel = makeInputFieldPanel("Start Location:");
        startLocationField = (JTextField) startFieldPanel.getClientProperty("inputField");
        leftPanel.add(startFieldPanel);
        leftPanel.add(Box.createVerticalStrut(10));

        // end location input
        JPanel endFieldPanel = makeInputFieldPanel("End Location:");
        endLocationField = (JTextField) endFieldPanel.getClientProperty("inputField");
        leftPanel.add(endFieldPanel);
        leftPanel.add(Box.createVerticalStrut(20));

        // buttons
        JButton refreshTrafficButton = new JButton("Refresh Traffic Values");
        JButton runDijkstraButton = new JButton("Run Dijkstra");
        JButton openMstTspButton = new JButton("Open MST / TSP Screen");

        UIUtils.styleButton(refreshTrafficButton);
        UIUtils.styleButton(runDijkstraButton);
        UIUtils.styleButton(openMstTspButton);

        refreshTrafficButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        runDijkstraButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        openMstTspButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        refreshTrafficButton.addActionListener(e -> loadAndSimulateTraffic(true));
        runDijkstraButton.addActionListener(e -> runDijkstraWithTraffic());
        openMstTspButton.addActionListener(e -> new RoutePage(trafficGraphPanel));

        leftPanel.add(refreshTrafficButton);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(runDijkstraButton);
        leftPanel.add(Box.createVerticalStrut(12));
        leftPanel.add(openMstTspButton);
        leftPanel.add(Box.createVerticalGlue());

        // info section
        JLabel infoTitleLabel = new JLabel("Traffic Simulation Notes");
        UIUtils.styleLabel(infoTitleLabel, 16, true);
        infoTitleLabel.setForeground(UIUtils.NEON_PRIMARY);
        infoTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(infoTitleLabel);
        leftPanel.add(Box.createVerticalStrut(10));

        JTextArea infoNotes = new JTextArea();
        infoNotes.setText("Each edge shows:\n"
                + "D = saved delivery distance\n"
                + "T = temporary traffic intensity\n"
                + "C = combined cost (D + T) used by Dijkstra\n\n"
                + "Traffic values are temporary - refreshing changes them and they are never saved.");
        infoNotes.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        infoNotes.setForeground(Color.LIGHT_GRAY);
        infoNotes.setBackground(new Color(0, 0, 0, 0));
        infoNotes.setEditable(false);
        infoNotes.setLineWrap(true);
        infoNotes.setWrapStyleWord(true);
        infoNotes.setMaximumSize(new Dimension(300, 150));
        infoNotes.setAlignmentX(Component.LEFT_ALIGNMENT);
        leftPanel.add(infoNotes);

        // ===== CENTER - graph panel =====
        trafficGraphPanel.setOpaque(false);
        trafficGraphPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        // ===== RIGHT - result area =====
        dijkstraResultArea = new JTextArea();
        dijkstraResultArea.setFont(new Font("Consolas", Font.BOLD, 15));
        dijkstraResultArea.setEditable(false);
        dijkstraResultArea.setLineWrap(true);
        dijkstraResultArea.setWrapStyleWord(true);
        dijkstraResultArea.setOpaque(false);
        dijkstraResultArea.setForeground(Color.WHITE);

        JScrollPane dijkstraScrollPane = new JScrollPane(dijkstraResultArea) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 80));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        dijkstraScrollPane.setPreferredSize(new Dimension(440, 0));
        dijkstraScrollPane.setOpaque(false);
        dijkstraScrollPane.getViewport().setOpaque(false);
        dijkstraScrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        add(leftPanel, BorderLayout.WEST);
        add(trafficGraphPanel, BorderLayout.CENTER);
        add(dijkstraScrollPane, BorderLayout.EAST);

        // auto-load graph and apply traffic when page opens
        loadAndSimulateTraffic(false);
        setVisible(true);
    }

    // helper that creates a labeled input field panel
    // stores the JTextField as a client property so we can get it back
    private JPanel makeInputFieldPanel(String labelText) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setMaximumSize(new Dimension(280, 60));
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelRow.setOpaque(false);
        labelRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fieldLabel = new JLabel(labelText);
        UIUtils.styleLabel(fieldLabel, 14, true);
        labelRow.add(fieldLabel);

        JTextField inputField = new JTextField(15);
        inputField.setMaximumSize(new Dimension(280, 30));
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputField.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldPanel.add(labelRow);
        fieldPanel.add(inputField);
        // store the field so the caller can get it with getClientProperty
        fieldPanel.putClientProperty("inputField", inputField);
        return fieldPanel;
    }

    // loads graph from file and applies random traffic values
    private void loadAndSimulateTraffic(boolean userClickedRefresh) {
        try {
            graphFileHandler.loadGraphInto(trafficGraphPanel);

            // apply random traffic between 10 and 100 to all edges
            trafficGraphPanel.applyRandomTraffic(10, 100);
            trafficGraphPanel.setShowTrafficDetails(true);
            trafficGraphPanel.clearMstAndPathHighlights();

            StringBuilder statusMessage = new StringBuilder();

            if (userClickedRefresh) {
                statusMessage.append("Traffic simulation refreshed with new random values.\n\n");
            } else {
                statusMessage.append("Traffic simulation loaded.\n\n");
            }

            statusMessage.append("Dijkstra will find the route with minimum combined cost.\n");
            statusMessage.append("Enter start and end locations, then click Run Dijkstra.");
            dijkstraResultArea.setText(statusMessage.toString());

        } catch (IOException loadException) {
            trafficGraphPanel.clearMstAndPathHighlights();
            dijkstraResultArea.setText("Could not load the delivery graph from saved files.\n" + loadException.getMessage());
        }
    }

    // runs Dijkstra using combined cost (distance + traffic) on each edge
    private void runDijkstraWithTraffic() {
        String startName = startLocationField.getText().trim();
        String endName = endLocationField.getText().trim();

        int startNodeIndex = trafficGraphPanel.getNodeIndex(startName);
        int endNodeIndex = trafficGraphPanel.getNodeIndex(endName);

        // check both names are valid
        if (startNodeIndex == -1 || endNodeIndex == -1) {
            trafficGraphPanel.clearPathHighlight();
            dijkstraResultArea.setText("Invalid delivery location names.\nPlease enter the exact names shown on the graph.");
            return;
        }

        // build traffic-aware graph and run Dijkstra
        List<List<DijkstraEdge>> trafficAwareGraph = trafficGraphPanel.buildTrafficAwareGraph();
        Delivery.PathResult dijkstraResult = Delivery.findShortestPath(trafficAwareGraph, startNodeIndex, endNodeIndex, trafficGraphPanel);

        // highlight the result path on the graph
        trafficGraphPanel.highlightPath(dijkstraResult.bestPath, trafficGraphPanel.edges);

        // add up distance and traffic separately so we can show breakdown
        int totalDistanceOnPath = 0;
        int totalTrafficOnPath = 0;

        for (int pathStepIdx = 0; pathStepIdx < dijkstraResult.bestPath.size() - 1; pathStepIdx++) {
            int stepFrom = dijkstraResult.bestPath.get(pathStepIdx);
            int stepTo = dijkstraResult.bestPath.get(pathStepIdx + 1);

            Edge stepEdge = trafficGraphPanel.findEdgeByIndices(stepFrom, stepTo);
            if (stepEdge != null) {
                totalDistanceOnPath += stepEdge.getDistance();
                totalTrafficOnPath += stepEdge.getTrafficIntensity();
            }
        }

        int totalCombinedCost = totalDistanceOnPath + totalTrafficOnPath;

        // show breakdown at top then full result below
        dijkstraResultArea.setText(
                "Dijkstra used combined edge cost: C = Distance + Traffic\n"
                + "Total Distance Along Route = " + totalDistanceOnPath + "\n"
                + "Total Traffic Along Route = " + totalTrafficOnPath + "\n"
                + "Total Combined Cost = " + totalCombinedCost + "\n\n"
                + dijkstraResult.summary);
    }
}
