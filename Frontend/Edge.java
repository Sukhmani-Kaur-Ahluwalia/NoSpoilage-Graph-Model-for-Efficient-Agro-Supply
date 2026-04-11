package Frontend;

public class Edge {

    public Node n1, n2;
    public int weight;

    public Edge(Node n1, Node n2, int weight) {
        this.n1 = n1;
        this.n2 = n2;
        this.weight = weight;
    }
}