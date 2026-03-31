public class Node {

    String name;
    int x, y;
    String type; // ⭐ NEW

    public Node(String name, int x, int y, String type) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.type = type;
    }
}