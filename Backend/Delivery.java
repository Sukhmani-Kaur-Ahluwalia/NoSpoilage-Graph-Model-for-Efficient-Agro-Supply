package Backend;

import java.util.*;
import Frontend.GraphPanel;

public class Delivery {

    public static String findpshort(List<List<DijkstraEdge>> graph, int source, int destination,
            GraphPanel graphPanel) {

        int n = graph.size();

        int[] dist = new int[n];
        int[] parent = new int[n];
        boolean[] visited = new boolean[n];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);

        PriorityQueue<DijkstraEdge> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a.time));

        dist[source] = 0;
        pq.add(new DijkstraEdge(source, 0));

        while (!pq.isEmpty()) {

            int u = pq.poll().destination;

            if (visited[u])
                continue;
            visited[u] = true;

            for (DijkstraEdge e : graph.get(u)) {

                int v = e.destination;
                int w = e.time;

                if (dist[u] + w < dist[v]) {
                    dist[v] = dist[u] + w;
                    parent[v] = u;
                    pq.add(new DijkstraEdge(v, dist[v]));
                }
            }
        }

        // ❌ no path
        if (dist[destination] == Integer.MAX_VALUE) {
            return "===== SHORTEST PATH RESULT =====\n\nNo path exists.";
        }

        // ✅ BEST PATH (Dijkstra)
        List<Integer> bestPath = new ArrayList<>();
        buildPathList(destination, parent, bestPath);
        int bestDistance = dist[destination];

        // ✅ ALTERNATE PATH (avoid best path)
        List<Integer> altPath = new ArrayList<>();
        boolean[] visitedAlt = new boolean[n];

        findAlternatePathAvoidBest(source, destination, graph, visitedAlt, altPath, bestPath);

        int altDistance = calculatePathDistance(altPath, graph);

        // ===== OUTPUT =====
        StringBuilder result = new StringBuilder();

        result.append("===== SHORTEST PATH RESULT =====\n\n");
        result.append("Possible Routes:\n");

        // Route 1
        result.append("1. ");
        appendPathNames(bestPath, result, graphPanel);
        result.append(" | Distance = ").append(bestDistance).append("\n");

        // Route 2
        if (altPath.size() > 1 && altDistance > 0) {
            result.append("2. ");
            appendPathNames(altPath, result, graphPanel);
            result.append(" | Distance = ").append(altDistance).append("\n");
        }

        // Best Route
        result.append("\nBest Route:\n");
        appendPathNames(bestPath, result, graphPanel);
        result.append("\nTotal Distance: ").append(bestDistance);

        return result.toString();
    }

    // 🔥 DFS avoiding best path edges
    public static boolean findAlternatePathAvoidBest(int current, int dest,
            List<List<DijkstraEdge>> graph,
            boolean[] visited,
            List<Integer> path,
            List<Integer> bestPath) {

        visited[current] = true;
        path.add(current);

        if (current == dest)
            return true;

        for (DijkstraEdge e : graph.get(current)) {

            int next = e.destination;

            if (visited[next])
                continue;

            // skip edges in best path
            if (isEdgeInBestPath(current, next, bestPath))
                continue;

            if (findAlternatePathAvoidBest(next, dest, graph, visited, path, bestPath)) {
                return true;
            }
        }

        path.remove(path.size() - 1);
        return false;
    }

    // check if edge is part of best path
    public static boolean isEdgeInBestPath(int u, int v, List<Integer> bestPath) {
        for (int i = 0; i < bestPath.size() - 1; i++) {
            if (bestPath.get(i) == u && bestPath.get(i + 1) == v) {
                return true;
            }
        }
        return false;
    }

    public static void buildPathList(int i, int[] parent, List<Integer> path) {
        if (i == -1)
            return;

        buildPathList(parent[i], parent, path);
        path.add(i);
    }

    public static int calculatePathDistance(List<Integer> path, List<List<DijkstraEdge>> graph) {

        int total = 0;

        for (int i = 0; i < path.size() - 1; i++) {
            int u = path.get(i);
            int v = path.get(i + 1);

            for (DijkstraEdge e : graph.get(u)) {
                if (e.destination == v) {
                    total += e.time;
                    break;
                }
            }
        }

        return total;
    }

    public static void appendPathNames(List<Integer> path, StringBuilder result, GraphPanel graphPanel) {

        for (int i = 0; i < path.size(); i++) {
            result.append(graphPanel.getNodeName(path.get(i)));

            if (i != path.size() - 1) {
                result.append(" → ");
            }
        }
    }
}