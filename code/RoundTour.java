package code;

import java.util.*;
import javax.swing.*;
import java.awt.*;

class RoundTour extends JPanel 
{
	public static final long serialVersionUID=1L;
	
	int INF=Integer.MAX_VALUE;
	int dist[][];
	ArrayList <Graph> g=new ArrayList<Graph>();
	int n = Dashboard.nodes.size();
	ArrayList<Integer> tspPath = new ArrayList<Integer>();
	
	RoundTour(ArrayList <Graph> g)
	{
	  this.g=g;
	  dist=new int[Dashboard.nodes.size()][Dashboard.nodes.size()];
	  
	  int n = Dashboard.nodes.size();

	  for (int i = 0; i < n; i++)
	  {
	      Arrays.fill(dist[i], INF);
	      dist[i][i] = 0;
	  }

	  for (Graph edge : g)
	  {
		  dist[edge.s][edge.d] = Math.min(dist[edge.s][edge.d], edge.w);
		  dist[edge.d][edge.s] = Math.min(dist[edge.d][edge.s], edge.w);//undirected graph
	  }
	}
	
	void floydWarshall(int V, int dist[][]) 
	{
	    for (int k = 0; k < V; k++) 
	    {
	        for (int i = 0; i < V; i++) 
	        {
	            for (int j = 0; j < V; j++) 
	            {
	                if (dist[i][k] != INF && dist[k][j] != INF && dist[i][k] + dist[k][j] < dist[i][j]) 
	                {
	                    dist[i][j] = dist[i][k] + dist[k][j];
	                }
	            }
	        }
	    }
	}
	
	int[][] buildDistanceMatrix()
	{
	    int n = Dashboard.nodes.size();
	    int matrix[][] = new int[n][n];

	    for (int i = 0; i < n; i++)
	    {
	        Arrays.fill(matrix[i], INF);
	        matrix[i][i] = 0;
	    }

	    for (Graph edge : g)
	    {
	        matrix[edge.s][edge.d] = Math.min(matrix[edge.s][edge.d], edge.w);
	        matrix[edge.d][edge.s] = Math.min(matrix[edge.d][edge.s], edge.w);
	    }

	    return matrix;
	}
	
	String getResultText()
	{
	    if (tspPath.size() == 0)
	        return "Round tour is not possible.";

	    String ans = "Round Pickup Tour:\n";

	    for (int i = 0; i < tspPath.size(); i++)
	    {
	        ans += Dashboard.nodes.get(tspPath.get(i));

	        if (i != tspPath.size() - 1)
	            ans += " -> ";
	    }

	    return ans;
	}
	
	void TSP(int source)
	{
	    tspPath.clear();

	    int n = Dashboard.nodes.size();

	    floydWarshall(n, dist);

	    int totalMasks = 1 << n;

	    int dp[][] = new int[totalMasks][n];
	    int parent[][] = new int[totalMasks][n];

	    for (int i = 0; i < totalMasks; i++)
	    {
	        Arrays.fill(dp[i], INF);
	        Arrays.fill(parent[i], -1);
	    }

	    dp[1 << source][source] = 0;

	    for (int mask = 0; mask < totalMasks; mask++)
	    {
	        for (int u = 0; u < n; u++)
	        {
	            if (dp[mask][u] == INF)
	                continue;

	            for (int v = 0; v < n; v++)
	            {
	                if ((mask & (1 << v)) == 0 && dist[u][v] != INF)
	                {
	                    int newMask = mask | (1 << v);
	                    int newCost = dp[mask][u] + dist[u][v];

	                    if (newCost < dp[newMask][v])
	                    {
	                        dp[newMask][v] = newCost;
	                        parent[newMask][v] = u;
	                    }
	                }
	            }
	        }
	    }

	    int fullMask = totalMasks - 1;
	    int minCost = INF;
	    int lastNode = -1;

	    for (int last = 0; last < n; last++)
	    {
	        if (last != source && dp[fullMask][last] != INF && dist[last][source] != INF)
	        {
	            int totalCost = dp[fullMask][last] + dist[last][source];

	            if (totalCost < minCost)
	            {
	                minCost = totalCost;
	                lastNode = last;
	            }
	        }
	    }

	    if (lastNode == -1)
	    {   
	    	System.out.println("TSP not possible");
	    	return;
	    }

	    int mask = fullMask;
	    int current = lastNode;

	    while (current != -1)
	    {
	        tspPath.add(current);

	        int previous = parent[mask][current];
	        mask = mask ^ (1 << current);
	        current = previous;
	    }

	    Collections.reverse(tspPath);

	    tspPath.add(source);
	    
	    setOpaque(false);
		setPreferredSize(new Dimension(600, 500));

	   // return minCost;
	}
	
	void TSP(int source, ArrayList<Integer> stores)
	{
	    tspPath.clear();

	    if (stores.size() == 0)
	        return;

	    int shortest[][] = buildDistanceMatrix();
	    int n = Dashboard.nodes.size();
	    floydWarshall(n, shortest);

	    ArrayList<Integer> points = new ArrayList<Integer>();
	    points.add(source);

	    for (Integer store : stores)
	    {
	        if (!points.contains(store))
	            points.add(store);
	    }

	    int pointCount = points.size();
	    int totalMasks = 1 << pointCount;
	    int dp[][] = new int[totalMasks][pointCount];
	    int parent[][] = new int[totalMasks][pointCount];

	    for (int i = 0; i < totalMasks; i++)
	    {
	        Arrays.fill(dp[i], INF);
	        Arrays.fill(parent[i], -1);
	    }

	    dp[1][0] = 0;

	    for (int mask = 0; mask < totalMasks; mask++)
	    {
	        for (int u = 0; u < pointCount; u++)
	        {
	            if (dp[mask][u] == INF)
	                continue;

	            for (int v = 1; v < pointCount; v++)
	            {
	                if ((mask & (1 << v)) == 0)
	                {
	                    int from = points.get(u);
	                    int to = points.get(v);

	                    if (shortest[from][to] == INF)
	                        continue;

	                    int newMask = mask | (1 << v);
	                    int newCost = dp[mask][u] + shortest[from][to];

	                    if (newCost < dp[newMask][v])
	                    {
	                        dp[newMask][v] = newCost;
	                        parent[newMask][v] = u;
	                    }
	                }
	            }
	        }
	    }

	    int fullMask = totalMasks - 1;
	    int minCost = INF;
	    int lastPoint = -1;

	    for (int last = 1; last < pointCount; last++)
	    {
	        int lastNode = points.get(last);

	        if (dp[fullMask][last] != INF && shortest[lastNode][source] != INF)
	        {
	            int totalCost = dp[fullMask][last] + shortest[lastNode][source];

	            if (totalCost < minCost)
	            {
	                minCost = totalCost;
	                lastPoint = last;
	            }
	        }
	    }

	    if (lastPoint == -1)
	        return;

	    ArrayList<Integer> pointPath = new ArrayList<Integer>();
	    int mask = fullMask;
	    int current = lastPoint;

	    while (current != -1)
	    {
	        pointPath.add(current);
	        int previous = parent[mask][current];
	        mask = mask ^ (1 << current);
	        current = previous;
	    }

	    Collections.reverse(pointPath);

	    for (Integer pointIndex : pointPath)
	        tspPath.add(points.get(pointIndex));

	    tspPath.add(source);

	    setOpaque(false);
	    setPreferredSize(new Dimension(600, 500));
	}
	
	protected void paintComponent(Graphics g)
	{
	      super.paintComponent(g);

	      if (Dashboard.g.size() == 0)
	          return;

	      if (Dashboard.nodeLoc.size() < Dashboard.nodes.size())
	          return;

	      Graphics2D g2d = (Graphics2D) g;
	      g2d.setStroke(new BasicStroke(2));

	      int x1,y1,x2,y2;
	      for (int i=0; i<tspPath.size()-1; i++)
	      {
	    	  int node1 = tspPath.get(i);
	          int node2 = tspPath.get(i + 1);
	    	  x1=Dashboard.nodeLoc.get(node1).x;
	    	  y1=Dashboard.nodeLoc.get(node1).y;
	    	  x2=Dashboard.nodeLoc.get(node2).x;
	    	  y2=Dashboard.nodeLoc.get(node2).y;
	          g2d.setColor(Color.black);
	          g2d.drawLine(x1,y1,x2,y2);
	      }
	 }
}
