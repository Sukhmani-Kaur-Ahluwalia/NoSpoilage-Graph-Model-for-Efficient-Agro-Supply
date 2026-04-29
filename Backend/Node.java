// Node represents a single location on the delivery graph
// it can be a Farm or a Shop, and stores its position on screen
 
public class Node {
 
    public String name;  // display name like "Farm 1" or "Shop 3"
    int x, y;            // pixel position on the graph panel
    String type;         // either "FARM" or "SHOP"
 
    public Node(String name, int x, int y, String type) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
 