package Backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Frontend.GraphPanel;
import Frontend.Edge;

// ===== EDGE CLASS =====
class RouteConnection {
    int nodeA;
    int nodeB;
    int distance;

    public RouteConnection(int nodeA, int nodeB, int distance) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.distance = distance;
    }
}

// ===== MAIN TSP CLASS =====
public class TSP {

    private int lowestRouteCost;
    private int[] optimalSequence;
    private int[][] distanceGrid;
    private int totalVertices;

    public TSP(int vertices, List<RouteConnection> edgeList) {
        this.totalVertices = vertices;
        this.lowestRouteCost = Integer.MAX_VALUE;
        this.optimalSequence = new int[vertices + 1];
        this.distanceGrid = new int[vertices][vertices];

        setupAdjacencyGrid(edgeList);
    }

    // ===== BUILD MATRIX =====
    private void setupAdjacencyGrid(List<RouteConnection> edgeList) {
        for (int[] row : distanceGrid) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        for (RouteConnection connection : edgeList) {
            distanceGrid[connection.nodeA][connection.nodeB] = connection.distance;
            distanceGrid[connection.nodeB][connection.nodeA] = connection.distance;
        }
    }

    // ===== MAIN EXECUTION =====
    public void executeAlgorithm() {
        boolean[] isVisited = new boolean[totalVertices];
        int[] activePath = new int[totalVertices + 1];

        isVisited[0] = true;
        activePath[0] = 0;

        findOptimalTour(0, 1, 0, isVisited, activePath);
    }

    // ===== CORE RECURSION (IMPORTANT FIX) =====
    private void findOptimalTour(int currentCity, int count, int cost,
            boolean[] visited, int[] path) {

        if (count == totalVertices) {
            if (distanceGrid[currentCity][0] != Integer.MAX_VALUE) {

                int totalCost = cost + distanceGrid[currentCity][0];

                if (totalCost < lowestRouteCost) {
                    lowestRouteCost = totalCost;

                    System.arraycopy(path, 0, optimalSequence, 0, path.length);
                    optimalSequence[totalVertices] = 0;
                }
            }
            return;
        }

        for (int i = 0; i < totalVertices; i++) {

            if (visited[i])
                continue;
            if (distanceGrid[currentCity][i] == Integer.MAX_VALUE)
                continue;

            int newCost = cost + distanceGrid[currentCity][i];

            // pruning
            if (newCost >= lowestRouteCost)
                continue;

            visited[i] = true;
            path[count] = i;

            findOptimalTour(i, count + 1, newCost, visited, path);

            visited[i] = false; // backtrack
        }
    }

    // ===== RESULT RETURN =====
    public String getResult(GraphPanel graphPanel) {

        StringBuilder result = new StringBuilder();

        if (lowestRouteCost != Integer.MAX_VALUE) {

            result.append("===== TSP RESULT =====\n\n");
            result.append("Minimum Cost: ").append(lowestRouteCost).append("\n");
            result.append("Path: ");

            for (int i = 0; i <= totalVertices; i++) {
                result.append(graphPanel.getNodeName(optimalSequence[i]));

                if (i < totalVertices) {
                    result.append(" -> ");
                }
            }

        } else {
            result.append("No valid route found.");
        }

        return result.toString();
    }

    // ===== CONNECT TO GUI =====
    public static String runTSP(GraphPanel graphPanel) {

        List<RouteConnection> edges = new ArrayList<>();

        for (Edge e : graphPanel.edges) {
            int a = graphPanel.nodes.indexOf(e.n1);
            int b = graphPanel.nodes.indexOf(e.n2);

            edges.add(new RouteConnection(a, b, e.weight));
        }

        TSP tsp = new TSP(graphPanel.nodes.size(), edges);

        tsp.executeAlgorithm();

        return tsp.getResult(graphPanel);
    }
}