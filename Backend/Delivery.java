// Delivery.java - finds the shortest delivery route using Dijkstra's algorithm
// This figures out the best path between a farm and a shop in the NoSpoilage graph/network

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Delivery {

    // holds the result of a shortest path calculation
    public static class PathResult {
        public List<Integer> bestPath;
        public int bestDistance;
        public String summary;

        public PathResult(List<Integer> bestPath, int bestDistance, String summary) {
            this.bestPath = bestPath;
            this.bestDistance = bestDistance;
            this.summary = summary;
        }
    }

    // wrapper that returns just the summary string
    public static String findpshort(List<List<DijkstraEdge>> graph, int source, int destination,
            GraphPanel graphPanel) {
        return findShortestPath(graph, source, destination, graphPanel).summary;
    }

    // runs Dijkstra's algorithm to find the shortest delivery route
    // also tries to find an alternate route to show as a second option
    public static PathResult findShortestPath(List<List<DijkstraEdge>> deliveryGraph, int sourceNodeIdx,
            int destinationNodeIdx, GraphPanel graphPanel) {

        int totalDeliveryNodes = deliveryGraph.size();

        // dist array stores shortest distance from source to each node
        int[] shortestDist = new int[totalDeliveryNodes];
        // parent array helps us reconstruct the path
        int[] parentNode = new int[totalDeliveryNodes];
        // visited array so we don't process a node twice
        boolean[] visitedNodes = new boolean[totalDeliveryNodes];

        // initialize all distances to "infinity" (not yet reached)
        for (int i = 0; i < totalDeliveryNodes; i++) {
            shortestDist[i] = Integer.MAX_VALUE;
            parentNode[i] = -1;
            visitedNodes[i] = false;
        }

        // priority queue - always processes the closest unvisited node next
        // using a simple Comparator to order by distance
        PriorityQueue<DijkstraEdge> priorityQueue = new PriorityQueue<DijkstraEdge>(new Comparator<DijkstraEdge>() {
            
            public int compare(DijkstraEdge edgeA, DijkstraEdge edgeB) {
                 System.out.println("Which one is shorter");
                return Integer.compare(edgeA.time, edgeB.time);
            }
        });

        // start at source node with distance 0
        shortestDist[sourceNodeIdx] = 0;
        priorityQueue.add(new DijkstraEdge(sourceNodeIdx, 0));

        // process nodes until queue is empty
        while (!priorityQueue.isEmpty()) {
            int currentNode = priorityQueue.poll().destination;

            // skip if already processed this node
            if (visitedNodes[currentNode]) {
                continue;
            }
            visitedNodes[currentNode] = true;

            // check all neighbors of current node
            List<DijkstraEdge> neighborEdges = deliveryGraph.get(currentNode);
            for (int neighborIdx = 0; neighborIdx < neighborEdges.size(); neighborIdx++) {
                DijkstraEdge neighborEdge = neighborEdges.get(neighborIdx);

                int neighborNode = neighborEdge.destination;
                int edgeCost = neighborEdge.time;

                // check if we found a shorter path to this neighbor
                if (shortestDist[currentNode] != Integer.MAX_VALUE) {
                    int newDistToNeighbor = shortestDist[currentNode] + edgeCost;

                    if (newDistToNeighbor < shortestDist[neighborNode]) {
                        shortestDist[neighborNode] = newDistToNeighbor;
                        parentNode[neighborNode] = currentNode;
                        priorityQueue.add(new DijkstraEdge(neighborNode, shortestDist[neighborNode]));
                    }
                }
            }
        }

        // check if destination was reachable
        if (shortestDist[destinationNodeIdx] == Integer.MAX_VALUE) {
            return new PathResult(new ArrayList<Integer>(), Integer.MAX_VALUE,
                    "===== SHORTEST PATH RESULT =====\n\nNo delivery route exists in the graph.");
        }

        // reconstruct the best path by following parent pointers from destination back to source
        List<Integer> bestDeliveryPath = new ArrayList<Integer>();
        buildPathFromParents(destinationNodeIdx, parentNode, bestDeliveryPath);
        int bestPathDistance = shortestDist[destinationNodeIdx];

        // try to find an alternate path that avoids the best path's edges
        List<Integer> alternatePath = new ArrayList<Integer>();
        boolean[] altVisited = new boolean[totalDeliveryNodes];

        findAlternatePathAvoidBest(sourceNodeIdx, destinationNodeIdx, deliveryGraph, altVisited, alternatePath, bestDeliveryPath);

        int alternatePathDistance = calculateTotalPathDistance(alternatePath, deliveryGraph);

        // build the output string
        StringBuilder resultText = new StringBuilder();
        resultText.append("===== SHORTEST PATH RESULT =====\n\n");
        resultText.append("Possible Delivery Routes:\n");

        // show best path
        resultText.append("1. ");
        appendNodeNamesToResult(bestDeliveryPath, resultText, graphPanel);
        resultText.append(" | Total Weight = ").append(bestPathDistance).append("\n");

        // show alternate path if we found one
        if (alternatePath.size() > 1 && alternatePathDistance > 0) {
            resultText.append("2. ");
            appendNodeNamesToResult(alternatePath, resultText, graphPanel);
            resultText.append(" | Total Weight = ").append(alternatePathDistance).append("\n");
        }

        resultText.append("\nBest Delivery Route:\n");
        appendNodeNamesToResult(bestDeliveryPath, resultText, graphPanel);
        resultText.append("\nTotal Weight: ").append(bestPathDistance);

        return new PathResult(bestDeliveryPath, bestPathDistance, resultText.toString());
    }

    // tries to find an alternate path from current to dest that avoids edges already in bestPath
    // uses DFS (depth first search) - recursive
    public static boolean findAlternatePathAvoidBest(int currentNode, int destinationNode,
            List<List<DijkstraEdge>> deliveryGraph,
            boolean[] visitedInAlt,
            List<Integer> altPathSoFar,
            List<Integer> bestPathToAvoid) {

        visitedInAlt[currentNode] = true;
        altPathSoFar.add(currentNode);

        // base case - reached destination
        if (currentNode == destinationNode) {
            return true;
        }

        // try each neighbor
        List<DijkstraEdge> neighborEdges = deliveryGraph.get(currentNode);
        for (int i = 0; i < neighborEdges.size(); i++) {
            int nextNode = neighborEdges.get(i).destination;

            // skip if already visited
            if (visitedInAlt[nextNode]) {
                continue;
            }

            // skip if this edge is part of the best path (we want a different route)
            if (edgeExistsInPath(currentNode, nextNode, bestPathToAvoid)) {
                continue;
            }

            // try going to this neighbor
            if (findAlternatePathAvoidBest(nextNode, destinationNode, deliveryGraph, visitedInAlt, altPathSoFar, bestPathToAvoid)) {
                return true;
            }
        }

        // backtrack - remove current node from path
        altPathSoFar.remove(altPathSoFar.size() - 1);
        return false;
    }

    // checks if a directed edge from nodeU to nodeV exists in a path
    public static boolean edgeExistsInPath(int nodeU, int nodeV, List<Integer> pathToCheck) {
        for (int i = 0; i < pathToCheck.size() - 1; i++) {
            if (pathToCheck.get(i) == nodeU && pathToCheck.get(i + 1) == nodeV) {
                return true;
            }
        }
        return false;
    }

    // reconstructs path by following parent pointers recursively
    // starts from destination and works back to source, then reverses
    public static void buildPathFromParents(int currentNodeIdx, int[] parentArray, List<Integer> pathList) {
        if (currentNodeIdx == -1) {
            return;
        }
        // go to parent first (recursive)
        buildPathFromParents(parentArray[currentNodeIdx], parentArray, pathList);
        // then add current node (this naturally gives us source -> destination order)
        pathList.add(currentNodeIdx);
    }

    // calculates the total weight of a given path
    public static int calculateTotalPathDistance(List<Integer> deliveryPath, List<List<DijkstraEdge>> deliveryGraph) {
        int totalDistance = 0;

        // go through each step in the path
        for (int stepIdx = 0; stepIdx < deliveryPath.size() - 1; stepIdx++) {
            int fromNode = deliveryPath.get(stepIdx);
            int toNode = deliveryPath.get(stepIdx + 1);

            // find the edge weight between these two nodes
            List<DijkstraEdge> fromNodeEdges = deliveryGraph.get(fromNode);
            for (int edgeIdx = 0; edgeIdx < fromNodeEdges.size(); edgeIdx++) {
                if (fromNodeEdges.get(edgeIdx).destination == toNode) {
                    totalDistance += fromNodeEdges.get(edgeIdx).time;
                    break;
                }
            }
        }

        return totalDistance;
    }

    // appends node names separated by " -> " to the result string
    public static void appendNodeNamesToResult(List<Integer> nodePath, StringBuilder resultBuilder,
            GraphPanel graphPanel) {
        for (int i = 0; i < nodePath.size(); i++) {
            resultBuilder.append(graphPanel.getNodeName(nodePath.get(i)));
            // add arrow between names (but not after the last one)
            if (i != nodePath.size() - 1) {
                resultBuilder.append(" -> ");
            }
        }
    }
}
