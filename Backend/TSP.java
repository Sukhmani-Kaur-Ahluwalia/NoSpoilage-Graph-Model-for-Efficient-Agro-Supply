
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

//Edges
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

class SalesmanTourOptimizer {

    private int lowestRouteCost;
    private int[] optimalSequence;
    private final int[][] distanceGrid;
    private final int totalVertices;

    public SalesmanTourOptimizer(int vertices, List<RouteConnection> edgeList) {
        this.totalVertices = vertices;
        this.lowestRouteCost = Integer.MAX_VALUE;
        this.optimalSequence = new int[vertices + 1];
        this.distanceGrid = new int[vertices][vertices];

        setupAdjacencyGrid(edgeList);
    }

    private void setupAdjacencyGrid(List<RouteConnection> edgeList) {
        for (int[] row : distanceGrid) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        for (RouteConnection connection : edgeList) {
            distanceGrid[connection.nodeA][connection.nodeB] = connection.distance;
            distanceGrid[connection.nodeB][connection.nodeA] = connection.distance;
        }
    }

    public void executeAlgorithm() {
        boolean[] isVisited = new boolean[totalVertices];
        int[] activePath = new int[totalVertices + 1];

        // Setup initial state
        isVisited[0] = true;
        activePath[0] = 0;

        findOptimalTour(0, 1, 0, isVisited, activePath);
        displayFinalOutcome();
    }

    private void findOptimalTour(int currentCity, int nodesCovered, int currentAccumulatedCost,
            boolean[] isVisited, int[] activePath) {

        // Base Condition Evaluation
        if (nodesCovered == totalVertices) {
            if (distanceGrid[currentCity][0] != Integer.MAX_VALUE) {
                int finalJourneyCost = currentAccumulatedCost + distanceGrid[currentCity][0];

                if (finalJourneyCost < lowestRouteCost) {
                    lowestRouteCost = finalJourneyCost;

                    System.arraycopy(activePath, 0, optimalSequence, 0, activePath.length);
                    optimalSequence[totalVertices] = 0;
                }
            }
            return;
        }

        // Search Phase
        for (int targetCity = 0; targetCity < totalVertices; targetCity++) {

            // Skipping invalid/already visited cities
            if (isVisited[targetCity])
                continue;
            if (distanceGrid[currentCity][targetCity] == Integer.MAX_VALUE)
                continue;

            int projectedCost = currentAccumulatedCost + distanceGrid[currentCity][targetCity];

            // BOUNDING: Skip if this route is already more expensive than the best known
            // route
            if (projectedCost >= lowestRouteCost)
                continue;

            // Proceed with valid branch
            isVisited[targetCity] = true;
            activePath[nodesCovered] = targetCity;

            findOptimalTour(targetCity, nodesCovered + 1, projectedCost, isVisited, activePath);

            // Backtrack
            isVisited[targetCity] = false;
        }
    }

    private void displayFinalOutcome() {
        if (lowestRouteCost != Integer.MAX_VALUE) {
            System.out.println("\n*** TRAVELING SALESMAN RESULTS ***");
            System.out.println("Least Cost Found: " + lowestRouteCost);
            System.out.print("Best Route Followed: ");

            for (int idx = 0; idx <= totalVertices; idx++) {
                System.out.print(optimalSequence[idx]);
                if (idx < totalVertices) {
                    System.out.print(" -> ");
                }
            }
            System.out.println();
        } else {
            System.out.println("No valid complete path could be found for the provided graph.");
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter total number of vertices (v): ");
        int nodeCount = scanner.nextInt();

        System.out.print("Enter total number of edges (e): ");
        int edgeCount = scanner.nextInt();

        List<RouteConnection> graphConnections = new ArrayList<>();

        System.out.println("Enter each edge (Source Destination Weight):");

        int inputCounter = 0;
        while (inputCounter < edgeCount) {
            int source = scanner.nextInt();
            int dest = scanner.nextInt();
            int cost = scanner.nextInt();
            graphConnections.add(new RouteConnection(source, dest, cost));
            inputCounter++;
        }

        // Create object
        SalesmanTourOptimizer tspSolver = new SalesmanTourOptimizer(nodeCount, graphConnections);
        tspSolver.executeAlgorithm();

        scanner.close();
    }
}