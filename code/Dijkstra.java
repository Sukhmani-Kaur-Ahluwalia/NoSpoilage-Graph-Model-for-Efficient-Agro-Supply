package code;
import java.util.*;
import javax.swing.*;
import java.awt.*;
class Dijkstra extends JPanel
{
	public static final long serialVersionUID=1L;
	ArrayList <Graph> g=new ArrayList<Graph>();
	ArrayList<Integer> path=new ArrayList<Integer>();
	ArrayList<Integer> traffic=new ArrayList<Integer>();
	Dijkstra(ArrayList <Graph> g)
	{
	  this.g=g;
	}
	int[] dijkstra(int n, int src, int parent[], ArrayList<Graph> g) 
	{
		int dist[] = new int[n];
	    Arrays.fill(dist, Integer.MAX_VALUE);
	    Arrays.fill(parent, -1);

	    dist[src] = 0;

	    PriorityQueue<Graph> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.w, b.w));
	    pq.add(new Graph(src, src, 0));

	    while (!pq.isEmpty()) 
	    {
	        Graph current = pq.poll();
	        int u = current.s;
	        int currentDist = current.w;
	        if (currentDist > dist[u])
	            continue;
	        for (Graph edge : g) 
	        {
	            if (edge.s == u) 
	            {   int v = edge.d;
	                int weight = edge.w;

	                if (dist[u] + weight < dist[v]) 
	                {
	                    dist[v] = dist[u] + weight;
	                    parent[v] = u;
	                    pq.add(new Graph(v, v, dist[v]));
	                }
	            }
	            if (edge.d == u)
	            {
	                int v = edge.s;
	                int weight = edge.w;

	                if (dist[u] + weight < dist[v])
	                {
	                    dist[v] = dist[u] + weight;
	                    parent[v] = u;
	                    pq.add(new Graph(v, v, dist[v]));
	                }
	            }
	        }
	    }
	    return dist;
	}
	 //Finds and prints the shortest path between a specific source and destination
	void findShortestPath(int n, int src, int dest) 
	{
	    path.clear();

	    ArrayList <Graph> g1=new ArrayList<Graph>();
	    traffic.clear();
	    int t,s,d,w;
		  for(int i=0;i<g.size();i++)
		  {
			t=(int)(Math.random()*100);
			s=g.get(i).s;
			d=g.get(i).d;
			w=(g.get(i).w+t)/2;
			g1.add(new Graph(s,d,w));
			traffic.add(t);
		  }
	    
	    int parent[] = new int[n];
	    int dists[] = dijkstra(n, src, parent,g1);

	    if (dists[dest] == Integer.MAX_VALUE) 
	    {
	    	System.out.println("Dest not present");
	        return;
	    }

	    for (int v = dest; v != -1; v = parent[v]) 
	    {
	        path.add(v);
	    }

	    Collections.reverse(path);
	    
	    System.out.println("Final Path");
	    for(int i=0;i<path.size();i++)
	     System.out.print(path.get(i)+ " ");
	    
	    setOpaque(false);
		setPreferredSize(new Dimension(600, 500));
	}
	
	String getResultText()
	{
	    if (path.size() == 0)
	        return "No fastest route found.";

	    String ans = "Fastest Route:\n";

	    for (int i = 0; i < path.size(); i++)
	    {
	        ans += Dashboard.nodes.get(path.get(i));

	        if (i != path.size() - 1)
	            ans += " -> ";
	    }

	    ans += "\n\nTraffic Factor: Simulated";
	    ans += "\nEstimated Delivery Risk: Based on traffic intensity";

	    return ans;
	}
	
	protected void paintComponent(Graphics g)
	{
	      super.paintComponent(g);

	      if (Dashboard.g.size() == 0)
	          return;

	      if (Dashboard.nodeLoc.size() < Dashboard.nodes.size())
	          return;

	      Graphics2D g2d = (Graphics2D) g;
	      g2d.setStroke(new BasicStroke(4));

	      int x1,y1,x2,y2;
	      for (int i=0; i<path.size()-1; i++)
	      {
	    	  int node1 = path.get(i);
	          int node2 = path.get(i + 1);
	    	  x1=Dashboard.nodeLoc.get(node1).x;
	    	  y1=Dashboard.nodeLoc.get(node1).y;
	    	  x2=Dashboard.nodeLoc.get(node2).x;
	    	  y2=Dashboard.nodeLoc.get(node2).y;
	          g2d.setColor(new Color(51,0,51));
	          g2d.drawLine(x1,y1,x2,y2);
	      }
	      
	      for (int i = 0; i < Dashboard.nodes.size(); i++) 
	      {
	        Font boldfont=new Font("Arial", Font.BOLD, 13);
	        g2d.setFont(boldfont);
	        g2d.setColor(Color.BLACK);
	        g2d.drawString("Traffic Intensity="+traffic.get(i), Dashboard.nodeLoc.get(i).x + 10, Dashboard.nodeLoc.get(i).y+50);
	      }
	 }
}
