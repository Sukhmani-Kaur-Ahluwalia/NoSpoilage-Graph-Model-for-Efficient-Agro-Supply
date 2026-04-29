import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;

// TSP class - Traveling Salesman Problem
// This finds the shortest route that visits all delivery stops and comes back
public class TSP {

    // use a big number to mean "no connection / infinity"
    private static final int NO_CONNECTION = 1000000000;

    // this class holds the result of running TSP
    public static class TourResult {
        public List<Integer> traversalPath;
        public int totalCost;
        public boolean found;
        public String summary;

        public TourResult(List<Integer> traversalPath, int totalCost, boolean found, String summary) {
            this.traversalPath = traversalPath;
            this.totalCost = totalCost;
            this.found = found;
            this.summary = summary;
        }
    }

    // just returns the summary string, used in some places
    public static String runTSP(GraphPanel graphPanel, int sourceNode) {
        return buildTour(graphPanel, sourceNode).summary;
    }

    // main method that builds the TSP tour
    public static TourResult buildTour(GraphPanel graphPanel, int sourceNode) {

        // first load the graph from the saved files
        try {
            new graphHnadle().loadGraphInto(graphPanel);
        } catch (IOException ex) {
            // System.out.println("DEBUG: failed to load graph - " + ex.getMessage());
            return new TourResult(new ArrayList<Integer>(), Integer.MAX_VALUE, false,
                    "===== TSP RESULT =====\n\nCould not load the NoSpoilage graph from files.\n" + ex.getMessage());
        }

        int totalNodesInGraph = graphPanel.nodes.size();

        // check if source node is valid
        if (sourceNode < 0 || sourceNode >= totalNodesInGraph) {
            return new TourResult(new ArrayList<Integer>(), Integer.MAX_VALUE, false,
                    "===== TSP RESULT =====\n\nThe source node number you entered is not valid.");
        }

        // can't do TSP if there are no edges
        if (graphPanel.edges.isEmpty()) {
            return new TourResult(new ArrayList<Integer>(), Integer.MAX_VALUE, false,
                    "===== TSP RESULT =====\n\nPlease add some edges to the graph before running TSP.");
        }

        // figure out which nodes are actually connected (have edges)
        // nodes with no edges are ignored
        ArrayList<Integer> activeNodesList = new ArrayList<Integer>();
        int[] originalIndexToActiveIndex = new int[totalNodesInGraph];

        // fill with -1 meaning not active yet
        for (int i = 0; i < totalNodesInGraph; i++) {
            originalIndexToActiveIndex[i] = -1;
        }

        // go through all edges and collect which nodes appear in them
        for (int edgeIdx = 0; edgeIdx < graphPanel.edges.size(); edgeIdx++) {
            Edge currentEdge = graphPanel.edges.get(edgeIdx);
            int fromNodeIndex = graphPanel.nodes.indexOf(currentEdge.n1);
            int toNodeIndex = graphPanel.nodes.indexOf(currentEdge.n2);

            // add from-node if we haven't seen it yet
            if (originalIndexToActiveIndex[fromNodeIndex] == -1) {
                originalIndexToActiveIndex[fromNodeIndex] = activeNodesList.size();
                activeNodesList.add(fromNodeIndex);
            }

            // add to-node if we haven't seen it yet
            if (originalIndexToActiveIndex[toNodeIndex] == -1) {
                originalIndexToActiveIndex[toNodeIndex] = activeNodesList.size();
                activeNodesList.add(toNodeIndex);
            }
        }

        // check if source node is actually in the graph (has at least one edge)
        boolean sourceIsConnected = originalIndexToActiveIndex[sourceNode] != -1;

        if (!sourceIsConnected) {
            return new TourResult(new ArrayList<Integer>(), Integer.MAX_VALUE, false,
                    "===== TSP RESULT =====\n\nThe source node has no connections in the current graph.");
        }

        int activeSourceIndex = originalIndexToActiveIndex[sourceNode];
        int numActiveNodes = activeNodesList.size();

        // if only one node is active, trivial case - just return to itself
        if (numActiveNodes < 2) {
            ArrayList<Integer> trivialPath = new ArrayList<Integer>();
            trivialPath.add(sourceNode);
            trivialPath.add(sourceNode);
            return new TourResult(trivialPath, 0, true,
                    "===== TSP RESULT =====\n\nSource: " + graphPanel.getNodeName(sourceNode) +
                            "\nOnly one delivery stop - no tour needed.\nTotal Cost: 0");
        }

        // build distance matrix using Floyd-Warshall for all pairs shortest paths
        int[][] distMatrix = new int[numActiveNodes][numActiveNodes];
        int[][] nextHopMatrix = new int[numActiveNodes][numActiveNodes];

        // initialize distance matrix - set everything to NO_CONNECTION first
        for (int rowIdx = 0; rowIdx < numActiveNodes; rowIdx++) {
            for (int colIdx = 0; colIdx < numActiveNodes; colIdx++) {
                distMatrix[rowIdx][colIdx] = NO_CONNECTION;
                nextHopMatrix[rowIdx][colIdx] = -1;
            }
            // distance from a node to itself is 0
            distMatrix[rowIdx][rowIdx] = 0;
            nextHopMatrix[rowIdx][rowIdx] = rowIdx;
        }

        // fill in actual edge weights from the graph
        for (int edgeIdx = 0; edgeIdx < graphPanel.edges.size(); edgeIdx++) {
            Edge currentEdge = graphPanel.edges.get(edgeIdx);

            int originalFrom = graphPanel.nodes.indexOf(currentEdge.n1);
            int originalTo = graphPanel.nodes.indexOf(currentEdge.n2);

            int compressedFrom = originalIndexToActiveIndex[originalFrom];
            int compressedTo = originalIndexToActiveIndex[originalTo];

            if (compressedFrom == -1 || compressedTo == -1) {
                continue;
            }

            // only update if this edge is shorter (handles duplicate edges)
            if (currentEdge.getDistance() < distMatrix[compressedFrom][compressedTo]) {
                distMatrix[compressedFrom][compressedTo] = currentEdge.getDistance();
                distMatrix[compressedTo][compressedFrom] = currentEdge.getDistance();
                nextHopMatrix[compressedFrom][compressedTo] = compressedTo;
                nextHopMatrix[compressedTo][compressedFrom] = compressedFrom;
            }
        }

        // run Floyd-Warshall to find shortest path between every pair of nodes
        // k is the intermediate node we try going through
        for (int k = 0; k < numActiveNodes; k++) {
            for (int i = 0; i < numActiveNodes; i++) {
                // skip if no path from i to k
                if (distMatrix[i][k] >= NO_CONNECTION) {
                    continue;
                }
                for (int j = 0; j < numActiveNodes; j++) {
                    // skip if no path from k to j
                    if (distMatrix[k][j] >= NO_CONNECTION) {
                        continue;
                    }
                    // check if going through k is shorter
                    int distanceThroughK = distMatrix[i][k] + distMatrix[k][j];
                    if (distanceThroughK < distMatrix[i][j]) {
                        distMatrix[i][j] = distanceThroughK;
                        nextHopMatrix[i][j] = nextHopMatrix[i][k];
                    }
                }
            }
        }

        // if any pair of active nodes is still unreachable, graph is disconnected
        for (int i = 0; i < numActiveNodes; i++) {
            for (int j = 0; j < numActiveNodes; j++) {
                if (distMatrix[i][j] >= NO_CONNECTION) {
                    return new TourResult(new ArrayList<Integer>(), Integer.MAX_VALUE, false,
                            "===== TSP RESULT =====\n\nThe graph is disconnected - TSP cannot visit all delivery stops.");
                }
            }
        }

        // now do Held-Karp dynamic programming to find optimal TSP tour
        // mask = bitmask representing which nodes have been visited
        int totalMasks = 1 << numActiveNodes;
        int[][] dpCostTable = new int[totalMasks][numActiveNodes];
        int[][] dpParentTable = new int[totalMasks][numActiveNodes];

        // fill dp table with big number (not visited yet)
        for (int maskIdx = 0; maskIdx < totalMasks; maskIdx++) {
            for (int nodeIdx = 0; nodeIdx < numActiveNodes; nodeIdx++) {
                dpCostTable[maskIdx][nodeIdx] = NO_CONNECTION;
                dpParentTable[maskIdx][nodeIdx] = -1;
            }
        }

        // starting point: only source node visited, cost = 0
        int startingMask = 1 << activeSourceIndex;
        dpCostTable[startingMask][activeSourceIndex] = 0;

        // fill the DP table - try all subsets of visited nodes
        for (int currentMask = 0; currentMask < totalMasks; currentMask++) {
            for (int currentNode = 0; currentNode < numActiveNodes; currentNode++) {

                // skip if this state is not reachable
                if (dpCostTable[currentMask][currentNode] >= NO_CONNECTION) {
                    continue;
                }

                // try going to each unvisited node next
                for (int nextDeliveryNode = 0; nextDeliveryNode < numActiveNodes; nextDeliveryNode++) {

                    // skip if already visited this node
                    if ((currentMask & (1 << nextDeliveryNode)) != 0) {
                        continue;
                    }

                    int shortestDistToNext = distMatrix[currentNode][nextDeliveryNode];
                    if (shortestDistToNext >= NO_CONNECTION) {
                        continue;
                    }

                    int newMask = currentMask | (1 << nextDeliveryNode);
                    int newTotalCost = dpCostTable[currentMask][currentNode] + shortestDistToNext;

                    // update if this path is cheaper
                    if (newTotalCost < dpCostTable[newMask][nextDeliveryNode]) {
                        dpCostTable[newMask][nextDeliveryNode] = newTotalCost;
                        dpParentTable[newMask][nextDeliveryNode] = currentNode;
                    }
                }
            }
        }

        // all nodes visited mask = all bits set
        int allVisitedMask = totalMasks - 1;

        // find the best ending node to return from back to source
        int bestLastNode = -1;
        int bestTourCost = NO_CONNECTION;

        for (int lastNode = 0; lastNode < numActiveNodes; lastNode++) {
            // can't end at source (we're already counting it at start)
            if (lastNode == activeSourceIndex) {
                continue;
            }
            if (dpCostTable[allVisitedMask][lastNode] >= NO_CONNECTION) {
                continue;
            }
            if (distMatrix[lastNode][activeSourceIndex] >= NO_CONNECTION) {
                continue;
            }

            // total tour cost = cost to get here + cost to return to source
            int returnToSourceCost = dpCostTable[allVisitedMask][lastNode] + distMatrix[lastNode][activeSourceIndex];

            if (returnToSourceCost < bestTourCost) {
                bestTourCost = returnToSourceCost;
                bestLastNode = lastNode;
            }
        }

        // if no valid tour found
        if (bestLastNode == -1) {
            return new TourResult(new ArrayList<Integer>(), Integer.MAX_VALUE, false,
                    "===== TSP RESULT =====\n\nNo valid delivery tour found from " +
                            graphPanel.getNodeName(sourceNode) + " that returns to start.");
        }

        // reconstruct the visit order by tracing back through parent table
        ArrayList<Integer> visitOrderReversed = new ArrayList<Integer>();
        int tracerNode = bestLastNode;
        int tracerMask = allVisitedMask;

        while (tracerNode != -1) {
            visitOrderReversed.add(tracerNode);
            int prevNode = dpParentTable[tracerMask][tracerNode];
            tracerMask = tracerMask ^ (1 << tracerNode);
            tracerNode = prevNode;
        }

        // reverse to get correct order
        ArrayList<Integer> visitOrderActive = new ArrayList<Integer>();
        for (int revIdx = visitOrderReversed.size() - 1; revIdx >= 0; revIdx--) {
            visitOrderActive.add(visitOrderReversed.get(revIdx));
        }
        // add source at the end to complete the cycle
        visitOrderActive.add(activeSourceIndex);

        // expand visit order into full path (using shortest paths between stops)
        ArrayList<Integer> expandedActivePathList = new ArrayList<Integer>();

        for (int segIdx = 0; segIdx < visitOrderActive.size() - 1; segIdx++) {
            int segFrom = visitOrderActive.get(segIdx);
            int segTo = visitOrderActive.get(segIdx + 1);

            // reconstruct the actual shortest path between these two stops
            ArrayList<Integer> segmentPath = new ArrayList<Integer>();
            if (nextHopMatrix[segFrom][segTo] == -1) {
                // this shouldn't happen at this point but just in case
                return new TourResult(new ArrayList<Integer>(), Integer.MAX_VALUE, false,
                        "===== TSP RESULT =====\n\nCould not reconstruct full delivery route.");
            }

            int segCurrent = segFrom;
            segmentPath.add(segCurrent);

            while (segCurrent != segTo) {
                segCurrent = nextHopMatrix[segCurrent][segTo];
                if (segCurrent == -1) {
                    return new TourResult(new ArrayList<Integer>(), Integer.MAX_VALUE, false,
                            "===== TSP RESULT =====\n\nPath reconstruction failed.");
                }
                segmentPath.add(segCurrent);
            }

            // avoid duplicating the connecting node between segments
            if (expandedActivePathList.isEmpty()) {
                for (int pathNodeIdx = 0; pathNodeIdx < segmentPath.size(); pathNodeIdx++) {
                    expandedActivePathList.add(segmentPath.get(pathNodeIdx));
                }
            } else {
                for (int pathNodeIdx = 1; pathNodeIdx < segmentPath.size(); pathNodeIdx++) {
                    expandedActivePathList.add(segmentPath.get(pathNodeIdx));
                }
            }
        }

        // convert active indices back to original node indices
        ArrayList<Integer> visitOrderOriginal = new ArrayList<Integer>();
        for (int activeIdx = 0; activeIdx < visitOrderActive.size(); activeIdx++) {
            visitOrderOriginal.add(activeNodesList.get(visitOrderActive.get(activeIdx)));
        }

        ArrayList<Integer> expandedPathOriginal = new ArrayList<Integer>();
        for (int activeIdx = 0; activeIdx < expandedActivePathList.size(); activeIdx++) {
            expandedPathOriginal.add(activeNodesList.get(expandedActivePathList.get(activeIdx)));
        }

        // build the result string to display
        StringBuilder resultText = new StringBuilder();
        resultText.append("===== TSP RESULT =====\n\n");
        resultText.append("Source: ").append(graphPanel.getNodeName(sourceNode)).append("\n");
        resultText.append("Delivery Stops Covered: ").append(activeNodesList.size()).append("\n");
        resultText.append("Visit Order:\n");

        // print visit order with arrows between names
        for (int pathIdx = 0; pathIdx < visitOrderOriginal.size(); pathIdx++) {
            resultText.append(graphPanel.getNodeName(visitOrderOriginal.get(pathIdx)));
            if (pathIdx < visitOrderOriginal.size() - 1) {
                resultText.append(" -> ");
            }
        }

        resultText.append("\nExpanded Route:\n");

        for (int pathIdx = 0; pathIdx < expandedPathOriginal.size(); pathIdx++) {
            resultText.append(graphPanel.getNodeName(expandedPathOriginal.get(pathIdx)));
            if (pathIdx < expandedPathOriginal.size() - 1) {
                resultText.append(" -> ");
            }
        }

        resultText.append("\nTotal Cost: ").append(bestTourCost);

        return new TourResult(expandedPathOriginal, bestTourCost, true, resultText.toString());
    }
}
