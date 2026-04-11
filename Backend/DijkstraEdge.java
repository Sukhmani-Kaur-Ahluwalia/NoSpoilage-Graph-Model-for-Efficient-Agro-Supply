package Backend;

public class DijkstraEdge {
    public int destination;
    public int time;

    public DijkstraEdge(int destination, int time) {
        this.destination = destination;
        this.time = time;
    }
}