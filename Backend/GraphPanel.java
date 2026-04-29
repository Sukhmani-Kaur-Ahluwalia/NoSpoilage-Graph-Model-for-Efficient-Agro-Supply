// GraphPanel.java - the visual panel that draws all nodes and edges
// This is what the user sees - the actual graph visualization for NoSpoilage

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GraphPanel extends JPanel {

    // all the nodes and edges in the graph
    public ArrayList<Node> nodes = new ArrayList<Node>();
    public ArrayList<Edge> edges = new ArrayList<Edge>();

    // edges to highlight in green (MST result)
    private List<Edge> mstEdges = new ArrayList<Edge>();

    // edges to highlight in gold (shortest path result)
    private List<Edge> highlightedPathEdges = new ArrayList<Edge>();

    // whether to show traffic details on edge labels
    private boolean showTrafficDetails = false;

    // constructor - creates the default nodes (8 farms + 5 shops)
    public GraphPanel() {
        setOpaque(false);

        // place 8 farm nodes in an arc on the top half
        int centerX = 380;
        int centerY = 280;
        int layoutRadius = 200;

        for (int farmNum = 0; farmNum < 8; farmNum++) {
            double angle = Math.PI * (farmNum + 1) / (8 + 1);
            int xPos = (int) (centerX + layoutRadius * Math.cos(angle));
            int yPos = (int) (centerY - layoutRadius * Math.sin(angle));
            nodes.add(new Node("Farm " + (farmNum + 1), xPos, yPos, "FARM"));
        }

        // place 5 shop nodes in an arc on the bottom half
        for (int shopNum = 0; shopNum < 5; shopNum++) {
            double angle = Math.PI * (shopNum + 1) / (5 + 1);
            int xPos = (int) (centerX + layoutRadius * Math.cos(angle));
            int yPos = (int) (centerY + layoutRadius * Math.sin(angle));
            nodes.add(new Node("Shop " + (shopNum + 1), xPos, yPos, "SHOP"));
        }
    }

    // recalculates node positions based on the current panel size
    // called every time the panel is redrawn so it scales properly
    private void recalculateNodePositions() {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int layoutRadius = Math.min(getWidth(), getHeight()) / 3;

        // reposition the 8 farms
        for (int farmIdx = 0; farmIdx < 8; farmIdx++) {
            double angle = Math.PI * (farmIdx + 1) / (8 + 1);
            nodes.get(farmIdx).x = (int) (centerX + layoutRadius * Math.cos(angle + Math.PI));
            nodes.get(farmIdx).y = (int) (centerY + layoutRadius * Math.sin(angle + Math.PI));
        }

        // reposition the 5 shops
        for (int shopIdx = 0; shopIdx < 5; shopIdx++) {
            double angle = Math.PI * (shopIdx + 1) / (5 + 1);
            nodes.get(shopIdx + 8).x = (int) (centerX + layoutRadius * Math.cos(angle));
            nodes.get(shopIdx + 8).y = (int) (centerY + layoutRadius * Math.sin(angle));
        }
    }

    // this method paints the whole graph onto the panel
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // make sure panel has a valid size before drawing
        if (getWidth() > 0) {
            recalculateNodePositions();
        }

        Graphics2D g2d = (Graphics2D) g;
        // turn on antialiasing so lines look smooth
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // draw all regular edges first (white/transparent)
        for (int i = 0; i < edges.size(); i++) {
            drawSingleEdge(g2d, edges.get(i), new Color(255, 255, 255, 90), 2f);
        }

        // draw MST edges on top (neon green)
        for (int i = 0; i < mstEdges.size(); i++) {
            drawSingleEdge(g2d, mstEdges.get(i), UIUtils.NEON_PRIMARY, 4f);
        }

        // draw highlighted path edges on top of everything (gold)
        for (int i = 0; i < highlightedPathEdges.size(); i++) {
            drawSingleEdge(g2d, highlightedPathEdges.get(i), new Color(255, 215, 0), 6f);
        }

        // draw all nodes (circles with labels)
        int nodeSize = 50;
        for (int nodeIdx = 0; nodeIdx < nodes.size(); nodeIdx++) {
            Node currentNode = nodes.get(nodeIdx);

            // farms are green, shops are pink
            if (currentNode.type.equals("FARM")) {
                g2d.setColor(new Color(0, 255, 127));
            } else {
                g2d.setColor(new Color(255, 105, 180));
            }

            // draw filled circle for node
            g2d.fillOval(currentNode.x - nodeSize / 2, currentNode.y - nodeSize / 2, nodeSize, nodeSize);

            // draw white border around node
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(currentNode.x - nodeSize / 2, currentNode.y - nodeSize / 2, nodeSize, nodeSize);

            // draw the node name centered inside the circle
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics nameMetrics = g2d.getFontMetrics();
            int textStartX = currentNode.x - (nameMetrics.stringWidth(currentNode.name) / 2);
            int textStartY = currentNode.y - (nameMetrics.getHeight() / 2) + nameMetrics.getAscent();

            g2d.setColor(Color.BLACK);
            g2d.drawString(currentNode.name, textStartX, textStartY);
        }

        // draw edge weight labels last so they appear on top
        for (int i = 0; i < edges.size(); i++) {
            drawEdgeWeightLabel(g2d, edges.get(i));
        }
    }

    // draws one edge line between two nodes
    private void drawSingleEdge(Graphics2D g2d, Edge edgeToDraw, Color edgeColor, float strokeThickness) {
        g2d.setColor(edgeColor);
        g2d.setStroke(new BasicStroke(strokeThickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.drawLine(edgeToDraw.n1.x, edgeToDraw.n1.y, edgeToDraw.n2.x, edgeToDraw.n2.y);
    }

    // draws the weight label in the middle of an edge
    private void drawEdgeWeightLabel(Graphics2D g2d, Edge edgeToDraw) {
        // find the midpoint of the edge
        int midpointX = (edgeToDraw.n1.x + edgeToDraw.n2.x) / 2;
        int midpointY = (edgeToDraw.n1.y + edgeToDraw.n2.y) / 2;

        // calculate edge direction for label offset
        int deltaX = edgeToDraw.n2.x - edgeToDraw.n1.x;
        int deltaY = edgeToDraw.n2.y - edgeToDraw.n1.y;
        double edgeLength = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        if (edgeLength == 0) {
            return;
        }

        // offset the label perpendicular to the edge so it doesn't overlap the line
        int labelOffsetX = (int) (-deltaY / edgeLength * 25);
        int labelOffsetY = (int) (deltaX / edgeLength * 25);
        int labelX = midpointX + labelOffsetX;
        int labelY = midpointY + labelOffsetY;

        // decide what text to show
        String labelLine1;
        String labelLine2 = null;

        if (showTrafficDetails) {
            // show distance, traffic intensity, and combined cost
            labelLine1 = "D:" + edgeToDraw.getDistance() + " T:" + edgeToDraw.getTrafficIntensity();
            labelLine2 = "C:" + edgeToDraw.getCombinedWeight();
        } else {
            // just show distance
            labelLine1 = String.valueOf(edgeToDraw.getDistance());
        }

        // figure out font and box size
        if (showTrafficDetails) {
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
        } else {
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        FontMetrics labelMetrics = g2d.getFontMetrics();
        int line1Width = labelMetrics.stringWidth(labelLine1);
        int maxTextWidth = line1Width;

        if (labelLine2 != null) {
            int line2Width = labelMetrics.stringWidth(labelLine2);
            if (line2Width > maxTextWidth) {
                maxTextWidth = line2Width;
            }
        }

        int boxWidth = maxTextWidth + 14;
        int boxHeight;
        if (labelLine2 == null) {
            boxHeight = 20;
        } else {
            boxHeight = 32;
        }

        // draw dark background box behind the label
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(labelX - 6, labelY - 14, boxWidth, boxHeight, 8, 8);

        // choose label color based on whether this edge is highlighted
        if (highlightedPathEdges.contains(edgeToDraw)) {
            g2d.setColor(new Color(255, 215, 0)); // gold for path
        } else if (mstEdges.contains(edgeToDraw)) {
            g2d.setColor(UIUtils.NEON_PRIMARY); // green for MST
        } else {
            g2d.setColor(UIUtils.NEON_SECONDARY); // default color
        }

        // draw the label text
        g2d.drawString(labelLine1, labelX, labelY - (labelLine2 == null ? 0 : 2));
        if (labelLine2 != null) {
            g2d.drawString(labelLine2, labelX, labelY + 12);
        }
    }

    // updates the displayed names of nodes (called when user edits them)
    public void updateNodeNames(String[] newNames) {
        for (int i = 0; i < nodes.size(); i++) {
            if (!newNames[i].isEmpty()) {
                nodes.get(i).name = newNames[i];
            }
        }
        repaint();
    }

    // adds or updates an edge between two named nodes
    public void addEdge(String fromNodeName, String toNodeName, int distanceValue) {
        Node fromNode = null;
        Node toNode = null;

        // find the two nodes by name
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equalsIgnoreCase(fromNodeName)) {
                fromNode = nodes.get(i);
            }
            if (nodes.get(i).name.equalsIgnoreCase(toNodeName)) {
                toNode = nodes.get(i);
            }
        }

        // both nodes must exist
        if (fromNode == null || toNode == null) {
            return;
        }

        // check if edge already exists - if so, just update it
        for (int i = 0; i < edges.size(); i++) {
            Edge existingEdge = edges.get(i);
            boolean sameEdge = (existingEdge.n1 == fromNode && existingEdge.n2 == toNode)
                    || (existingEdge.n1 == toNode && existingEdge.n2 == fromNode);

            if (sameEdge) {
                existingEdge.setDistance(distanceValue);
                existingEdge.clearTrafficIntensity();
                clearMstAndPathHighlights();
                repaint();
                return;
            }
        }

        // no existing edge found - add a new one
        edges.add(new Edge(fromNode, toNode, distanceValue));
        clearMstAndPathHighlights();
        repaint();
    }

    // creates a copy of this panel (used so algorithms don't change the original panel)
    public GraphPanel createDetachedCopy() {
        GraphPanel copiedPanel = new GraphPanel();

        // copy node names over
        for (int i = 0; i < nodes.size() && i < copiedPanel.nodes.size(); i++) {
            copiedPanel.nodes.get(i).name = nodes.get(i).name;
        }

        // copy all edges
        copiedPanel.edges.clear();
        for (int i = 0; i < edges.size(); i++) {
            Edge originalEdge = edges.get(i);
            int fromIdx = nodes.indexOf(originalEdge.n1);
            int toIdx = nodes.indexOf(originalEdge.n2);

            if (fromIdx < 0 || toIdx < 0) {
                continue;
            }

            Edge edgeCopy = new Edge(copiedPanel.nodes.get(fromIdx), copiedPanel.nodes.get(toIdx), originalEdge.getDistance());
            edgeCopy.setTrafficIntensity(originalEdge.getTrafficIntensity());
            copiedPanel.edges.add(edgeCopy);
        }

        copiedPanel.showTrafficDetails = showTrafficDetails;
        copiedPanel.clearMstAndPathHighlights();
        return copiedPanel;
    }

    // builds adjacency list using plain edge distances
    public List<List<DijkstraEdge>> buildGraph() {
        return buildGraphFromEdges(edges, false);
    }

    // builds adjacency list using combined cost (distance + traffic)
    public List<List<DijkstraEdge>> buildTrafficAwareGraph() {
        return buildGraphFromEdges(edges, true);
    }

    // builds adjacency list from a given edge list
    public List<List<DijkstraEdge>> buildGraphFromEdges(List<Edge> sourceEdges) {
        return buildGraphFromEdges(sourceEdges, false);
    }

    // builds an adjacency list representation of the graph for Dijkstra
    public List<List<DijkstraEdge>> buildGraphFromEdges(List<Edge> sourceEdges, boolean useTrafficWeight) {
        int totalNodes = nodes.size();
        List<List<DijkstraEdge>> adjacencyList = new ArrayList<List<DijkstraEdge>>();

        // create empty list for each node
        for (int i = 0; i < totalNodes; i++) {
            adjacencyList.add(new ArrayList<DijkstraEdge>());
        }

        // fill in adjacency list from edges
        for (int edgeIdx = 0; edgeIdx < sourceEdges.size(); edgeIdx++) {
            Edge currentEdge = sourceEdges.get(edgeIdx);

            int fromNodeIdx = nodes.indexOf(currentEdge.n1);
            int toNodeIdx = nodes.indexOf(currentEdge.n2);

            int edgeWeight;
            if (useTrafficWeight) {
                edgeWeight = currentEdge.getCombinedWeight();
            } else {
                edgeWeight = currentEdge.getDistance();
            }

            // add both directions since edges are undirected
            adjacencyList.get(fromNodeIdx).add(new DijkstraEdge(toNodeIdx, edgeWeight));
            adjacencyList.get(toNodeIdx).add(new DijkstraEdge(fromNodeIdx, edgeWeight));
        }

        return adjacencyList;
    }

    // assigns random traffic values to all edges (for simulation)
    public void applyRandomTraffic(int minTrafficValue, int maxTrafficValue) {
        Random randomGen = new Random();
        for (int i = 0; i < edges.size(); i++) {
            int randomTraffic = minTrafficValue + randomGen.nextInt(maxTrafficValue - minTrafficValue + 1);
            edges.get(i).setTrafficIntensity(randomTraffic);
        }
        repaint();
    }

    // clears all traffic values from edges
    public void clearTrafficSimulation() {
        for (int i = 0; i < edges.size(); i++) {
            edges.get(i).clearTrafficIntensity();
        }
        showTrafficDetails = false;
        repaint();
    }

    public void setShowTrafficDetails(boolean showDetails) {
        this.showTrafficDetails = showDetails;
        repaint();
    }

    // returns the index of a node with the given name, or -1 if not found
    public int getNodeIndex(String nodeName) {
        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).name.equalsIgnoreCase(nodeName)) {
                return i;
            }
        }
        return -1;
    }

    // returns the name of a node at a given index
    public String getNodeName(int nodeIndex) {
        return nodes.get(nodeIndex).name;
    }

    // sets which edges to highlight as MST (clears path highlight)
    public void setMstEdges(List<Edge> newMstEdges) {
        this.mstEdges = new ArrayList<Edge>(newMstEdges);
        this.highlightedPathEdges.clear();
        repaint();
    }

    public List<Edge> getMstEdges() {
        return new ArrayList<Edge>(mstEdges);
    }

    public boolean hasMst() {
        return !mstEdges.isEmpty();
    }

    // clears the highlighted path
    public void clearPathHighlight() {
        highlightedPathEdges.clear();
        repaint();
    }

    // clears both MST and path highlights
    public void clearMstAndPathHighlights() {
        mstEdges.clear();
        highlightedPathEdges.clear();
    }

    // highlights the edges along a given node path (uses main edge list)
    public void highlightPath(List<Integer> nodeIndexPath) {
        highlightPath(nodeIndexPath, edges);
    }

    // highlights edges along a node path using a specific edge source list
    public void highlightPath(List<Integer> nodeIndexPath, List<Edge> edgeSourceList) {
        highlightedPathEdges.clear();

        if (nodeIndexPath == null || nodeIndexPath.size() < 2) {
            repaint();
            return;
        }

        // go through each consecutive pair in the path and find the connecting edge
        for (int stepIdx = 0; stepIdx < nodeIndexPath.size() - 1; stepIdx++) {
            int fromIdx = nodeIndexPath.get(stepIdx);
            int toIdx = nodeIndexPath.get(stepIdx + 1);

            Edge connectingEdge = findEdgeByIndices(fromIdx, toIdx, edgeSourceList);
            if (connectingEdge != null) {
                highlightedPathEdges.add(connectingEdge);
            }
        }

        repaint();
    }

    // finds an edge between two node indices in the main edge list
    public Edge findEdgeByIndices(int fromNodeIndex, int toNodeIndex) {
        return findEdgeByIndices(fromNodeIndex, toNodeIndex, edges);
    }

    // finds an edge between two node indices in a given edge list
    public Edge findEdgeByIndices(int fromNodeIndex, int toNodeIndex, List<Edge> edgeSourceList) {
        if (fromNodeIndex < 0 || toNodeIndex < 0) {
            return null;
        }
        if (fromNodeIndex >= nodes.size() || toNodeIndex >= nodes.size()) {
            return null;
        }

        Node fromNode = nodes.get(fromNodeIndex);
        Node toNode = nodes.get(toNodeIndex);

        // search through the edge list
        for (int i = 0; i < edgeSourceList.size(); i++) {
            Edge currentEdge = edgeSourceList.get(i);
            boolean forwardMatch = (currentEdge.n1 == fromNode && currentEdge.n2 == toNode);
            boolean reverseMatch = (currentEdge.n1 == toNode && currentEdge.n2 == fromNode);

            if (forwardMatch || reverseMatch) {
                return currentEdge;
            }
        }

        // no edge found between these two nodes
        return null;
    }
}
