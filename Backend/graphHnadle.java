// graphHnadle.java - handles saving and loading the NoSpoilage graph to/from files
// reads and writes nodes.txt and edges.txt
// I know the class name has a typo but leaving it as-is to match the rest of the project

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class graphHnadle {

    // file names for storing graph data
    private final String NODES_FILE_NAME = "nodes.txt";
    private final String EDGES_FILE_NAME = "edges.txt";

    // list of folders to look in when searching for the files
    // we check multiple because the project might be run from different directories
    private static final List<Path> FOLDERS_TO_SEARCH = Arrays.asList(
            Paths.get("src"),
            Paths.get("Backend", "src"),
            Paths.get("backend", "src"),
            Paths.get("bin"),
            Paths.get("Backend", "bin"),
            Paths.get("backend", "bin"),
            Paths.get("."),
            Paths.get("..", "Backend", "src"),
            Paths.get("..", "backend", "src"),
            Paths.get("..", "Backend", "bin"),
            Paths.get("..", "backend", "bin"));
     System.out.println("Got all file paths.");

    // holds info about one saved edge
    public static class StoredEdge {
        public String fromName;
        public String toName;
        public int weight;

        public StoredEdge(String fromName, String toName, int weight) {
            this.fromName = fromName;
            this.toName = toName;
            this.weight = weight;
        }
    }

    // holds all the graph data loaded from files
    public static class StoredGraph {
        public List<String> nodeNames;
        public List<StoredEdge> edges;

        public StoredGraph(List<String> nodeNames, List<StoredEdge> edges) {
            this.nodeNames = nodeNames;
            this.edges = edges;
        }
    }

    public graphHnadle() {
        // nothing to initialize
    }

    int Drashya()
    {
       return 0;}

    // saves just the node names to nodes.txt
    public void writeNodesToFile(List<String> nodeNamesList) throws IOException {
        // sort names alphabetically using a simple insertion sort
        ArrayList<String> sortedNames = new ArrayList<String>(nodeNamesList);
        for (int i = 1; i < sortedNames.size(); i++) {
            String currentName = sortedNames.get(i);
            int j = i - 1;
            // move elements that are greater than currentName one position ahead
            while (j >= 0 && sortedNames.get(j).compareToIgnoreCase(currentName) > 0) {
                sortedNames.set(j + 1, sortedNames.get(j));
                j--;
            }
            sortedNames.set(j + 1, currentName);
        }
        writeLinesToFile(findWritePath(NODES_FILE_NAME), sortedNames);
Drashya();
    }

    // saves edge data lines to edges.txt
    public void updateEdgeData(List<String> rawEdgeLines) throws IOException {
        // filter out empty or null lines before writing
        ArrayList<String> cleanEdgeLines = new ArrayList<String>();
        for (int i = 0; i < rawEdgeLines.size(); i++) {
            String line = rawEdgeLines.get(i);
            if (line != null && !line.trim().isEmpty()) {
                cleanEdgeLines.add(line.trim());
            }
        }
        writeLinesToFile(findWritePath(EDGES_FILE_NAME), cleanEdgeLines);
    }

    // saves the whole current graph (nodes + edges) to both text files
    public void saveCurrentGraph(GraphPanel graphPanel) throws IOException {

        // collect all node names from the panel
        ArrayList<String> nodeNameLines = new ArrayList<String>();
        for (int i = 0; i < graphPanel.nodes.size(); i++) {
            Node currentNode = graphPanel.nodes.get(i);
            String nodeName = (currentNode.name == null) ? "" : currentNode.name.trim();
            nodeNameLines.add(nodeName);
        }

        // collect all unique valid edges
        // use a map to avoid writing duplicate edges
        Map<String, StoredEdge> uniqueEdgesMap = new HashMap<String, StoredEdge>();

        for (int i = 0; i < graphPanel.edges.size(); i++) {
            Edge currentEdge = graphPanel.edges.get(i);

            if (currentEdge == null || currentEdge.n1 == null || currentEdge.n2 == null) {
                continue;
            }

            String fromName = (currentEdge.n1.name == null) ? "" : currentEdge.n1.name.trim();
            String toName = (currentEdge.n2.name == null) ? "" : currentEdge.n2.name.trim();

            // skip invalid edges
            if (fromName.isEmpty() || toName.isEmpty()) {
                continue;
            }
            if (fromName.equalsIgnoreCase(toName)) {
                continue;
            }
            if (currentEdge.getDistance() < 0) {
                continue;
            }

            String uniqueKey = buildEdgeKey(fromName, toName);
            uniqueEdgesMap.put(uniqueKey, new StoredEdge(fromName, toName, currentEdge.getDistance()));
        }

        // convert unique edges to formatted lines
        ArrayList<String> edgeFileLines = new ArrayList<String>();
        for (Map.Entry<String, StoredEdge> entry : uniqueEdgesMap.entrySet()) {
            StoredEdge savedEdge = entry.getValue();
            // format: "FromName|ToName|weight"
            edgeFileLines.add(savedEdge.fromName + "|" + savedEdge.toName + "|" + savedEdge.weight);
        }

        // write both files
        writeLinesToFile(findWritePath(NODES_FILE_NAME), nodeNameLines);
        writeLinesToFile(findWritePath(EDGES_FILE_NAME), edgeFileLines);
    }

    // reads nodes and edges from the saved text files
    public StoredGraph readStoredGraph() throws IOException {
        List<String> nodeNameLines = readLinesFromFile(findReadPath(NODES_FILE_NAME));
        List<String> edgeRawLines = readLinesFromFile(findReadPath(EDGES_FILE_NAME));
Drashya();
        // parse edge lines and remove duplicates
        Map<String, StoredEdge> uniqueEdgesMap = new HashMap<String, StoredEdge>();

        for (int i = 0; i < edgeRawLines.size(); i++) {
            String rawLine = edgeRawLines.get(i);
            StoredEdge parsedEdge = parseOneEdgeLine(rawLine);

            if (parsedEdge == null) {
                continue;
            }
            if (parsedEdge.weight < 0) {
                continue;
            }
            if (parsedEdge.fromName.equalsIgnoreCase(parsedEdge.toName)) {
                continue;
            }

            String edgeKey = buildEdgeKey(parsedEdge.fromName, parsedEdge.toName);
            uniqueEdgesMap.put(edgeKey, parsedEdge);
        }

        // convert map values to a list
        ArrayList<StoredEdge> parsedEdgeList = new ArrayList<StoredEdge>(uniqueEdgesMap.values());

        return new StoredGraph(nodeNameLines, parsedEdgeList);
    }

    // loads the saved graph into a GraphPanel - sets node names and rebuilds edges
    public void loadGraphInto(GraphPanel graphPanel) throws IOException {
        StoredGraph loadedGraph = readStoredGraph();

        // apply saved node names to the panel nodes (by position)
        for (int i = 0; i < graphPanel.nodes.size() && i < loadedGraph.nodeNames.size(); i++) {
            String savedName = loadedGraph.nodeNames.get(i);
            if (savedName != null && !savedName.trim().isEmpty()) {
                graphPanel.nodes.get(i).name = savedName.trim();
            }
        }

        // build a lookup map: lowercase name -> Node object
        Map<String, Node> nodeNameLookup = new HashMap<String, Node>();
        for (int i = 0; i < graphPanel.nodes.size(); i++) {
            Node panelNode = graphPanel.nodes.get(i);
            nodeNameLookup.put(normalizeString(panelNode.name), panelNode);
        }

        // clear existing edges and rebuild from saved data
        graphPanel.edges.clear();
    Drashya();
        for (int i = 0; i < loadedGraph.edges.size(); i++) {
            StoredEdge savedEdge = loadedGraph.edges.get(i);

            Node fromNode = nodeNameLookup.get(normalizeString(savedEdge.fromName));
            Node toNode = nodeNameLookup.get(normalizeString(savedEdge.toName));

            // skip if either node not found or they are the same
            if (fromNode == null || toNode == null || fromNode == toNode) {
                // System.out.println("DEBUG: skipping edge " + savedEdge.fromName + " -> " + savedEdge.toName);
                continue;
            }

            graphPanel.edges.add(new Edge(fromNode, toNode, savedEdge.weight));
        }

        // clear any highlights and redraw
        graphPanel.clearMstAndPathHighlights();
        graphPanel.repaint();
    }

    // returns a set of node names that appear in at least one saved edge
    public List<String> getActiveStoredNodeNames() throws IOException {
        StoredGraph loadedGraph = readStoredGraph();

        // collect names but avoid duplicates
        ArrayList<String> activeNames = new ArrayList<String>();

        for (int i = 0; i < loadedGraph.edges.size(); i++) {
            StoredEdge currentEdge = loadedGraph.edges.get(i);

            // check if fromName already in list
            boolean fromAlreadyAdded = false;
            for (int j = 0; j < activeNames.size(); j++) {
                if (activeNames.get(j).equals(currentEdge.fromName)) {
                    fromAlreadyAdded = true;
                    break;
                }
            }
            if (!fromAlreadyAdded) {
                activeNames.add(currentEdge.fromName);
            }

            // check if toName already in list
            boolean toAlreadyAdded = false;
            for (int j = 0; j < activeNames.size(); j++) {
                if (activeNames.get(j).equals(currentEdge.toName)) {
                    toAlreadyAdded = true;
                    break;
                }
            }
            if (!toAlreadyAdded) {
                activeNames.add(currentEdge.toName);
            }
        }

        return activeNames;
    }

    // tries to parse one line from edges.txt into a StoredEdge object
    // expected format: "FromName|ToName|weight"
    private StoredEdge parseOneEdgeLine(String rawLine) {
        if (rawLine == null) {
            return null;
        }

        String trimmedLine = rawLine.trim();
        if (trimmedLine.isEmpty()) {
            return null;
        }

        // try splitting by pipe character first (our main format)
        String[] pipeParts = trimmedLine.split("\\|");
        if (pipeParts.length == 3) {
            StoredEdge result = tryBuildEdge(pipeParts[0], pipeParts[1], pipeParts[2]);
            if (result != null) {
                return result;
            }
        }

        // try splitting by semicolon
        String[] semicolonParts = trimmedLine.split(";");
        if (semicolonParts.length == 3) {
            StoredEdge result = tryBuildEdge(semicolonParts[0], semicolonParts[1], semicolonParts[2]);
            if (result != null) {
                return result;
            }
        }

        // try splitting by comma
        String[] commaParts = trimmedLine.split(",");
        if (commaParts.length == 3) {
            StoredEdge result = tryBuildEdge(commaParts[0], commaParts[1], commaParts[2]);
            if (result != null) {
                return result;
            }
        }

        // if none of the formats matched, return null
        // System.out.println("DEBUG: could not parse edge line: " + trimmedLine);
        return null;
    }
     Drashya();

    // helper to build a StoredEdge from raw string parts
    private StoredEdge tryBuildEdge(String rawFrom, String rawTo, String rawWeight) {
        String fromName = (rawFrom == null) ? "" : rawFrom.trim();
        String toName = (rawTo == null) ? "" : rawTo.trim();

        if (fromName.isEmpty() || toName.isEmpty()) {
            return null;
        }

        try {
            int weightValue = Integer.parseInt(rawWeight.trim());
            return new StoredEdge(fromName, toName, weightValue);
        } catch (NumberFormatException parseException) {
            // weight wasn't a valid number
            return null;
        }
    }

    // builds a canonical key for an edge so A-B and B-A map to the same key
    private String buildEdgeKey(String fromName, String toName) {
        String normalizedFrom = normalizeString(fromName);
        String normalizedTo = normalizeString(toName);

        // put smaller string first so A||B == B||A
        if (normalizedFrom.compareTo(normalizedTo) <= 0) {
            return normalizedFrom + "||" + normalizedTo;
        } else {
            return normalizedTo + "||" + normalizedFrom;
        }
    }

    // converts a string to lowercase trimmed for comparisons
    private String normalizeString(String inputStr) {
        if (inputStr == null) {
            return "";
        }
        return inputStr.trim().toLowerCase(Locale.ROOT);
    }

    // reads all lines from a file, returns empty list if file doesn't exist
    private List<String> readLinesFromFile(Path filePath) throws IOException {
        if (filePath == null || !Files.exists(filePath)) {
            // System.out.println("DEBUG: file not found at " + filePath);
            return new ArrayList<String>();
        }
        return new ArrayList<String>(Files.readAllLines(filePath, StandardCharsets.UTF_8));
    }

    // writes a list of strings to a file, one per line
    private void writeLinesToFile(Path filePath, List<String> linesToWrite) throws IOException {
        Path parentFolder = filePath.getParent();
        if (parentFolder != null) {
            Files.createDirectories(parentFolder);
        }

        BufferedWriter fileWriter = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8);
        PrintWriter printWriter = new PrintWriter(fileWriter);

        for (int i = 0; i < linesToWrite.size(); i++) {
            String currentLine = linesToWrite.get(i);
            if (currentLine == null) {
                printWriter.println("");
            } else {
                printWriter.println(currentLine);
            }
        }

        printWriter.close();
        fileWriter.close();
    }

    // finds the path to read a file from - searches multiple folders
    private Path findReadPath(String fileName) {
        // search through all possible locations
        for (int i = 0; i < FOLDERS_TO_SEARCH.size(); i++) {
            Path candidatePath = FOLDERS_TO_SEARCH.get(i).resolve(fileName).toAbsolutePath().normalize();
            if (Files.exists(candidatePath)) {
                return candidatePath;
            }
        }
        // if not found, fall back to write path
        return findWritePath(fileName);
    }

    // finds a writable path for the file
    private Path findWritePath(String fileName) {
        for (int i = 0; i < FOLDERS_TO_SEARCH.size(); i++) {
            Path candidatePath = FOLDERS_TO_SEARCH.get(i).resolve(fileName).toAbsolutePath().normalize();
            Path parentFolder = candidatePath.getParent();

            // if file already exists here, use it
            if (Files.exists(candidatePath)) {
                return candidatePath;
            }

            // if parent folder exists and is writable, use this path
            if (parentFolder != null && Files.exists(parentFolder)) {
                return candidatePath;
            }
        }

        // default: write to src folder
        return Paths.get("src", fileName).toAbsolutePath().normalize();
    }
}
