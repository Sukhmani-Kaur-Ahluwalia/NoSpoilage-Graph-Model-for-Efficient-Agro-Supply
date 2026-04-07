//arraylist <edge> -> collections.sort() ; 
//for(i=0 to v-1)
//edge e- a(src) b (dest) 
//for cycle detect- union find // union(a,b) if same ps - same grp (dont include)
//if diff - union(pA, pB)

import java.util.*;

class kruskals {
    static class Edge implements Comparable<Edge> {
        int src;
        int dest;
        int wt;

        public Edge(int s, int d, int w) {
            this.wt = w;
            this.src = s;
            this.dest = d;
        }

        @Override
        public int compareTo(Edge e2) {
            return this.wt - e2.wt;
        }
    }

    static void Graph(ArrayList<Edge> graph) {
        graph.add(new Edge(0, 1, 10));
        graph.add(new Edge(0, 2, 15));
        graph.add(new Edge(0, 3, 30));
        graph.add(new Edge(1, 3, 40));
        graph.add(new Edge(2, 3, 50));
    }

    // static int n=4;
    static int p[];
    static int root[];

    public static void init(int n) {
        p = new int[n];
        root = new int[n];

        for (int i = 0; i < n; i++) {
            p[i] = i;
            root[i] = 0;
        }
    }

    public static int find(int x) {
        if (p[x] == x)
            return x;

        return p[x] = find(p[x]);

    }

    public static void union(int a, int b) {
        int pA = find(a);
        int pB = find(b);

        if (root[pA] == root[pB]) {
            p[pB] = pA;
            root[pA]++;

        } else if (root[pA] < root[pB]) {
            p[pA] = pB;
        } else {
            p[pB] = pA;
        }
    }

    public static void KrushkalAlgo(ArrayList<Edge> graph, int v) {
        // to sort all graph in ascending order : -
        Collections.sort(graph);

        ArrayList<Edge> mst = new ArrayList<>();

        init(v);
        int cost = 0;
        int count = 0; // to keep track of the nodes
        int i = 0;
        while (count < v - 1 && i < graph.size()) {
            Edge e = graph.get(i);
            int pA = find(e.src);
            int pB = find(e.dest);
            if (pA != pB) {
                union(pA, pB);
                mst.add(e);
                cost += e.wt;
                count++;
            }
            i++;
        }
        System.out.println("MST COST: " + cost);
        System.out.println("Edges in MST:");
        for (Edge edge : mst) {
            System.out.println(edge.src + " -- " + edge.dest + " == " + edge.wt);
        }

    }

    public static void main(String[] args) {

        System.out.println("Enter the v ");
        Scanner sc = new Scanner(System.in);
        int v = sc.nextInt();
        System.out.print("Enter number of graph (e): ");
        int e = sc.nextInt();

        ArrayList<Edge> graph = new ArrayList<>();

        System.out.println("Enter src, dest, and weight for each edge:");
        for (int i = 0; i < e; i++) {
            int s = sc.nextInt();
            int d = sc.nextInt();
            int w = sc.nextInt();
            graph.add(new Edge(s, d, w));
            graph.add(new Edge(d, s, w));
        }
        KrushkalAlgo(graph, v);
    }
}
