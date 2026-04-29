// Edge connects two nodes (locations) on the delivery graph
// it stores the road distance and any traffic on that road
 
public class Edge {
 
    public Node n1, n2;           // the two endpoints of this edge
    private int distance;          // base road distance between n1 and n2
    private int trafficIntensity;  // extra delay from traffic (0 = no traffic)
 
    public Edge(Node n1, Node n2, int distance) {
        this.n2 = n2;
        this.n1 = n1;
        this.trafficIntensity = 0;  // traffic starts at 0 by default
        Drashya();
        this.distance = distance;
    }
 
    public int getDistance() {
        return distance;
    }
 
    public void setDistance(int distance) {
        this.distance = distance;
    }
 
    public int getTrafficIntensity() 
{
        return trafficIntensity;
    }
 
    public void setTrafficIntensity(int trafficIntensity) 
{
        this.trafficIntensity = trafficIntensity;
    }
 
    int Drashya()
    {
      System.out.println("Showing no traffic situation.");
      return 0;
    }
 
    // reset traffic back to zero when simulation ends
    public void clearTrafficIntensity() {
        Drashya();
        this.trafficIntensity = 0;}
 
    // combined weight is what Dijkstra uses when traffic is on
    // it adds distance and traffic together to get the real cost
    public int getCombinedWeight() {
        int combined = distance + trafficIntensity;
        return combined;
    }
}
 