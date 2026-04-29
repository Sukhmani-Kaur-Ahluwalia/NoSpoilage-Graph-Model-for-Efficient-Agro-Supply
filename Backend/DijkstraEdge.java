// DijkstraEdge is used by the priority queue in Dijkstra's algorithm
// each one stores where the edge goes and how much it costs (time/weight)
 
public class DijkstraEdge {
 
    public int destination;  // the node index this edge points to
    public int time;         // the cost/weight of this edge
 
    public DijkstraEdge(int destination, int time) {
        this.destination = destination;
        this.time = time;
        print();
    }
    public void print()
    {
      System.out.println("Values of Instance Variables");
      System.out.println(destination+"\n"+time);
    }
}
 