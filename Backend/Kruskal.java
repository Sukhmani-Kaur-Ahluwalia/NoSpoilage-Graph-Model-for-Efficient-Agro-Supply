// Kruskal's algorithm to find the Minimum Spanning Tree (MST)
// This helps find the cheapest way to connect all NoSpoilage delivery locations
// using union-find to detect cycles

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.Arrays;

public class Kruskal {

    // holds the result of running Kruskal
    public static class MstResult {
        public List<Edge> mstEdges;
        public int totalCost;
        public boolean connected;
        public String summary;

        public MstResult(List<Edge> mstEdges, int totalCost, boolean connected, String summary) {
            this.mstEdges = mstEdges;
            this.totalCost = totalCost;
            this.connected = connected;
            this.summary = summary;
        }
    }

    // inner class to represent a graph edge with source, destination, weight
    static class DeliveryEdge {
        int src;
        int dest;
        int weight;

        public DeliveryEdge(int s, int d, int w) {
            this.src = s;
            this.dest = d;
            this.weight = w;
        }
    }

    // parent array for union-find
    static int[] parentArray;
    // rank array to keep union-find balanced
    static int[] rankArray;

    // sets up union-find arrays for n nodes
    public static void initUnionFind(int numNodes) {
        parentArray = new int[numNodes];
        rankArray = new int[numNodes];

        // each node starts as its own parent (its own group)
        for (int i = 0; i < numNodes; i++) {
            parentArray[i] = i;
            rankArray[i] = 0;
        }
    }

    // find root of group (with path compression)
    public static int findRoot(int nodeIndex) {
        if (parentArray[nodeIndex] == nodeIndex) {
            return nodeIndex;
        }
        // path compression - point directly to root
        parentArray[nodeIndex] = findRoot(parentArray[nodeIndex]);
        return parentArray[nodeIndex];
    }

    // merge two groups together
    public static void unionGroups(int nodeA, int nodeB) {
        int rootA = findRoot(nodeA);
        int rootB = findRoot(nodeB);

        // merge smaller rank tree under bigger rank tree
        if (rankArray[rootA] == rankArray[rootB]) {
            parentArray[rootB] = rootA;
            rankArray[rootA]++;
        } else if (rankArray[rootA] < rankArray[rootB]) {
            parentArray[rootA] = rootB;
        } else {
            parentArray[rootB] = rootA;
        }
    }

    // manual sort - bubble sort for the delivery edges by weight
    // (yeah Collections.sort would be shorter but this is clearer)
    public static void sortEdgesByWeight(ArrayList<DeliveryEdge> edgeList) {
        int edgeCount = edgeList.size();
        // bubble sort - compare adjacent edges and swap if out of order
        for (int outerPass = 0; outerPass < edgeCount - 1; outerPass++) {
            for (int innerPass = 0; innerPass < edgeCount - outerPass - 1; innerPass++) {
                if (edgeList.get(innerPass).weight > edgeList.get(innerPass + 1).weight) {
                    // swap them
                    DeliveryEdge tempEdge = edgeList.get(innerPass);
                    edgeList.set(innerPass, edgeList.get(innerPass + 1));
                    edgeList.set(innerPass + 1, tempEdge);
                }
            }
        }
    }

    // finds a path to write the MST output file
    private static Path findMstOutputPath() {
        // try a few different locations where the file might live
        List<Path> placesToCheck = new ArrayList<Path>();
        placesToCheck.add(Paths.get("mst.txt"));
        placesToCheck.add(Paths.get("src", "mst.txt"));
        placesToCheck.add(Paths.get("Frontend", "src", "mst.txt"));

        // check each one - return first one that already exists
        for (int i = 0; i < placesToCheck.size(); i++) {
            if (Files.exists(placesToCheck.get(i))) {
                return placesToCheck.get(i).toAbsolutePath().normalize();
            }
        }

        // default fallback path if none found
        return Paths.get("Frontend", "src", "mst.txt").toAbsolutePath().normalize();
    }

    // writes the MST result to a text file so we can check it later
    private static void saveMstResultToFile(GraphPanel graphPanel, List<Edge> mstEdgeList, int totalMstCost) {
        Path outputFilePath = findMstOutputPath();
        List<String> fileLines = new ArrayList<String>();

        fileLines.add("===== KRUSKAL MST =====");
        fileLines.add("");

        // write each MST edge
        for (int i = 0; i < mstEdgeList.size(); i++) {
            Edge currentMstEdge = mstEdgeList.get(i);
            fileLines.add(currentMstEdge.n1.name + " -- " + currentMstEdge.n2.name + " = " + currentMstEdge.getDistance());
        }

        fileLines.add("");
        fileLines.add("Total Cost: " + totalMstCost);

        try {
            Path parentDir = outputFilePath.getParent();
            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }
            Files.write(outputFilePath, fileLines);
        } catch (IOException fileWriteException) {
            fileWriteException.printStackTrace();
        }
    }

    // main method that runs Kruskal's algorithm on the NoSpoilage delivery graph
    public static MstResult buildMst(GraphPanel graphPanel) {

        // load saved graph from files first
        try {
            new graphHnadle().loadGraphInto(graphPanel);
        } catch (IOException loadException) {
            // System.out.println("DEBUG: could not load graph - " + loadException.getMessage());
            return new MstResult(new ArrayList<Edge>(), 0, false,
                    "===== KRUSKAL MST =====\n\nCould not load the NoSpoilage graph files.\n" + loadException.getMessage());
        }

        // list to hold all delivery edges
        ArrayList<DeliveryEdge> allDeliveryEdges = new ArrayList<DeliveryEdge>();

        // set to track which delivery nodes are actually connected (appear in edges)
        // using LinkedHashMap to keep insertion order
        Set<Integer> connectedNodeIndices = new LinkedHashMap<Integer, Integer>() {
            // just reusing the keyset for an ordered set - a bit hacky but works
        }.keySet();

        // actually let's just use a regular list and check manually
        ArrayList<Integer> connectedNodesList = new ArrayList<Integer>();

        // track edges we've already seen to avoid duplicates
        Set<String> alreadySeenEdgeKeys = new HashSet<String>();

        // go through each edge in the graph
        for (int edgeIdx = 0; edgeIdx < graphPanel.edges.size(); edgeIdx++) {
            Edge graphEdge = graphPanel.edges.get(edgeIdx);

            int srcNodeIdx = graphPanel.nodes.indexOf(graphEdge.n1);
            int destNodeIdx = graphPanel.nodes.indexOf(graphEdge.n2);

            // skip invalid or self-loop edges
            if (srcNodeIdx < 0 || destNodeIdx < 0 || srcNodeIdx == destNodeIdx || graphEdge.getDistance() < 0) {
                continue;
            }

            // create a unique key for this edge (smaller index first) to detect duplicates
            String edgeKey;
            if (srcNodeIdx < destNodeIdx) {
                edgeKey = srcNodeIdx + "|" + destNodeIdx;
            } else {
                edgeKey = destNodeIdx + "|" + srcNodeIdx;
            }

            // skip if we've already seen this edge
            if (alreadySeenEdgeKeys.contains(edgeKey)) {
                continue;
            }
            alreadySeenEdgeKeys.add(edgeKey);

            // add to our edge list
            allDeliveryEdges.add(new DeliveryEdge(srcNodeIdx, destNodeIdx, graphEdge.getDistance()));

            // add both nodes to connected list if not already there
            boolean srcAlreadyInList = false;
            for (int nodeIdx = 0; nodeIdx < connectedNodesList.size(); nodeIdx++) {
                if (connectedNodesList.get(nodeIdx) == srcNodeIdx) {
                    srcAlreadyInList = true;
                    break;
                }
            }
            if (!srcAlreadyInList) {
                connectedNodesList.add(srcNodeIdx);
            }

            boolean destAlreadyInList = false;
            for (int nodeIdx = 0; nodeIdx < connectedNodesList.size(); nodeIdx++) {
                if (connectedNodesList.get(nodeIdx) == destNodeIdx) {
                    destAlreadyInList = true;
                    break;
                }
            }
            if (!destAlreadyInList) {
                connectedNodesList.add(destNodeIdx);
            }
        }

        // if no edges found, nothing to do
        if (allDeliveryEdges.isEmpty()) {
            return new MstResult(new ArrayList<Edge>(), 0, false,
                    "===== KRUSKAL MST =====\n\nNo valid delivery connections found in edges.txt.");
        }

        // compress node indices - map original indices to 0..n-1 range
        // this is needed because Kruskal works on 0-indexed nodes
        Map<Integer, Integer> originalToCompressedIndex = new HashMap<Integer, Integer>();
        for (int i = 0; i < connectedNodesList.size(); i++) {
            originalToCompressedIndex.put(connectedNodesList.get(i), i);
        }

        // create compressed version of edges using new indices
        ArrayList<DeliveryEdge> compressedEdgeList = new ArrayList<DeliveryEdge>();
        for (int i = 0; i < allDeliveryEdges.size(); i++) {
            DeliveryEdge originalEdge = allDeliveryEdges.get(i);
            int newSrc = originalToCompressedIndex.get(originalEdge.src);
            int newDest = originalToCompressedIndex.get(originalEdge.dest);
            compressedEdgeList.add(new DeliveryEdge(newSrc, newDest, originalEdge.weight));
        }

        // sort edges by weight (ascending) - cheapest connections first
        sortEdgesByWeight(compressedEdgeList);
        // System.out.println("DEBUG: sorted " + compressedEdgeList.size() + " edges");

        // initialize union-find for all active delivery nodes
        initUnionFind(connectedNodesList.size());

        int mstTotalCost = 0;
        List<Edge> mstEdgeResultList = new ArrayList<Edge>();

        StringBuilder resultText = new StringBuilder();
        resultText.append("===== KRUSKAL MST =====\n\n");

        // go through sorted edges and add them if they don't create a cycle
        for (int i = 0; i < compressedEdgeList.size(); i++) {
            DeliveryEdge candidateEdge = compressedEdgeList.get(i);

            int rootOfSrc = findRoot(candidateEdge.src);
            int rootOfDest = findRoot(candidateEdge.dest);

            // if both endpoints are in the same group, adding this edge would create a cycle - skip it
            if (rootOfSrc == rootOfDest) {
                continue;
            }

            // safe to add - merge the two groups
            unionGroups(rootOfSrc, rootOfDest);
            mstTotalCost += candidateEdge.weight;

            // get original node indices to look up the real edge object
            int originalSrcIdx = connectedNodesList.get(candidateEdge.src);
            int originalDestIdx = connectedNodesList.get(candidateEdge.dest);

            // find the actual Edge object in the graph panel
            Edge realEdgeObject = graphPanel.findEdgeByIndices(originalSrcIdx, originalDestIdx);
            if (realEdgeObject != null) {
                mstEdgeResultList.add(realEdgeObject);
            }

            // add this edge to result text
            resultText.append(graphPanel.getNodeName(originalSrcIdx) + " -- " +
                    graphPanel.getNodeName(originalDestIdx) + " = " + candidateEdge.weight + "\n");
        }

        // check if we got a complete MST (needs exactly n-1 edges for n nodes)
        int expectedMstEdgeCount = connectedNodesList.size() - 1;
        if (expectedMstEdgeCount < 0) {
            expectedMstEdgeCount = 0;
        }

        boolean isGraphConnected = (mstEdgeResultList.size() == expectedMstEdgeCount);

        resultText.append("\nTotal Cost: " + mstTotalCost);

        if (!isGraphConnected) {
            resultText.append("\n\nWarning: The delivery graph is disconnected.");
            resultText.append("\nOnly delivery locations that appear in edges.txt were checked.");
        }

        return new MstResult(mstEdgeResultList, mstTotalCost, isGraphConnected, resultText.toString());
    }

    // wrapper that runs Kruskal, sets MST on panel, saves to file, returns summary
    public static String runKruskal(GraphPanel graphPanel) {
        MstResult kruskalResult = buildMst(graphPanel);

        // highlight the MST edges on the graph panel
        graphPanel.setMstEdges(kruskalResult.mstEdges);

        // save results to file
        saveMstResultToFile(graphPanel, kruskalResult.mstEdges, kruskalResult.totalCost);

        return kruskalResult.summary;
    }
}
