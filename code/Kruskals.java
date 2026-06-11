package code;

import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;

class Kruskals extends JPanel
{
  public static final long serialVersionUID=1L;
  
  ArrayList <Graph> g=new ArrayList<Graph>();
  ArrayList <Coordinates> cd=new ArrayList<Coordinates>();
  ArrayList <Coordinates> mstcd=new ArrayList<Coordinates>();
  ArrayList<Graph> mst=new ArrayList<Graph>(); // To store the resulting MST
  
  Kruskals(ArrayList <Graph> graph, ArrayList<Coordinates>cd)
  {
	g=graph;
	this.cd=cd;
  }
  
//Kruskal starts
  
  int findParent(int i, int parent[])
  {
      if (parent[i] == i)
          return i;
      return findParent(parent[i], parent);
  }
  
  void unite(int i, int j, int parent[])
  {
      int root_i = findParent(i, parent);
      int root_j = findParent(j, parent);
      if (root_i != root_j)
      {
          parent[root_i] = root_j;
      }
  }
  
  void kruskal(int V, ArrayList <Graph> edges)
  {
      // 1. Sort edges in non-decreasing order of weight
      edges.sort((e1,e2) -> e1.w - e2.w);
      
      int parent[]=new int[V];
      for (int i = 0; i < V; i++)
      {
          parent[i] = i;
      }

      
      int rootU, rootV;
      //int mstWeight=0;

      // 2. Iterate through sorted edges
      for (int i=0;i<edges.size();i++)
      {
          rootU = findParent(edges.get(i).s, parent);
          rootV = findParent(edges.get(i).d, parent);

          // If roots are different, it doesn't form a cycle
          if (rootU != rootV)
          {
              mst.add(edges.get(i));
              mstcd.add(cd.get(edges.get(i).s));
              mstcd.add(cd.get(edges.get(i).d));
             // mstWeight += edges.get(i).w;
              unite(rootU, rootV, parent);
          }
      }
  }
  
  void display()
  {
	 mst.clear();
	 mstcd.clear();
	  
	 kruskal(Dashboard.nodes.size(),g);
	 setOpaque(false);
	 setPreferredSize(new Dimension(600, 500));
  }
  
  //display result
  protected void paintComponent(Graphics g)
  {
      super.paintComponent(g);

      if (Dashboard.g.size() == 0)
          return;

      if (Dashboard.nodeLoc.size() < Dashboard.nodes.size())
          return;

      Graphics2D g2d = (Graphics2D) g;
      g2d.setStroke(new BasicStroke(2));

      for (int i = 0; i < mstcd.size() - 1; i = i + 2)
      {
          /*double dx = mstcd.get(i + 1).x - mstcd.get(i).x;
          double dy = mstcd.get(i + 1).y - mstcd.get(i).y;
          double length = Math.sqrt(dx * dx + dy * dy);

          if (length == 0)
              continue;

          int offset = 0;

          int offsetX = (int) (-dy / length * offset);
          int offsetY = (int) (dx / length * offset);*/

          int drawX1 = mstcd.get(i).x; //+ offsetX;
          int drawY1 = mstcd.get(i).y; //+ offsetY;
          int drawX2 = mstcd.get(i + 1).x; //+ offsetX;
          int drawY2 = mstcd.get(i + 1).y; //+ offsetY;

          g2d.setColor(Color.CYAN);
          g2d.drawLine(drawX1, drawY1, drawX2, drawY2);
      }
  }
  
}
