package code;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
class Dashboard extends JFrame implements ActionListener,ItemListener
{
  public static final long serialVersionUID=1L;
  static final String SUPPLY_MST_FILE = "store_msts.txt";
  
  JPanel left,right,contentRight;
  JSplitPane outer;
  backgroundPanel bp;
  JComboBox<String> src,dest, src1, dest1, src2;
  static ArrayList <String> nodes=new ArrayList<String>();
  JLabel l1,l2,to,from,dist,dij1,dij2, to1, from1, tsp1, from2;
  JButton addEdge, mst, roundtour, shortroute;
  JTextField d;
  
  String source,destination,weight,source1, destination1, source2;
  String loggedInNode;
  static String b="";
  
  static ArrayList <Graph> g=new ArrayList<Graph>();
  
  static ArrayList<Coordinates>nodeLoc =new ArrayList<Coordinates>();
  
  JLayeredPane graphLayer;
  
  static ArrayList<String> produceItems = new ArrayList<String>();
  static ArrayList<String> allProduce = new ArrayList<String>();

  JPanel producePanel;
  ArrayList<JCheckBox> produceChecks = new ArrayList<JCheckBox>();
  ArrayList<ArrayList<Integer>> roundTripStores = new ArrayList<ArrayList<Integer>>();
  ArrayList<String> supplierNotes = new ArrayList<String>();
  ArrayList<SupplierPath> supplierPaths = new ArrayList<SupplierPath>();

  JTextArea resultArea;
  
  drawNodes obj;
  drawEdge de;
  Kruskals kobj;
  Dijkstra dobj;
  RoundTour robj;
  
  class SupplierPath
  {
      int storeIndex, farmIndex, pathWeight;
      ArrayList<Integer> pathNodes;

      SupplierPath(int storeIndex, int farmIndex, ArrayList<Integer> pathNodes, int pathWeight)
      {
          this.storeIndex = storeIndex;
          this.farmIndex = farmIndex;
          this.pathNodes = pathNodes;
          this.pathWeight = pathWeight;
      }
  }
  
  Dashboard()
  {
	this(null);
  }
  
  Dashboard(String loginNode)
  {
	loggedInNode = loginNode;
	getNodes();
	getEdges();
	
	String n[];
	left=new JPanel();
	left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
	left.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	left.setOpaque(false);
	
	right = new JPanel(new BorderLayout());
	right.setOpaque(false);
	
	bp=new backgroundPanel();
	bp.setLayout(new BorderLayout());
	
	contentRight = new JPanel(new BorderLayout());
	contentRight.setOpaque(false);
	
	right.add(contentRight, BorderLayout.CENTER);
	
	n=nodes.toArray(String[]::new);
	src=new JComboBox<String>(n);
	src1=new JComboBox<String>(n);
	dest=new JComboBox<String>(n);
	dest1=new JComboBox<String>(n);
	src2=new JComboBox<String>();
	src2.setPrototypeDisplayValue("FreshPotato Farm");
	src.addItemListener(this);
	dest.addItemListener(this);
	src1.addItemListener(this);
	dest1.addItemListener(this);
	src2.addItemListener(this);
	
	producePanel = new JPanel();
	producePanel.setLayout(new BoxLayout(producePanel, BoxLayout.Y_AXIS));
	producePanel.setOpaque(false);
	producePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
	for (String item : allProduce)
	{
	    JCheckBox check = new JCheckBox(item);
	    check.setOpaque(false);
	    check.setForeground(Color.WHITE);
	    check.setFont(new Font("Arial", Font.BOLD, 13));
	    check.setAlignmentX(Component.LEFT_ALIGNMENT);
	    produceChecks.add(check);
	    producePanel.add(check);
	}
	
	addEdge=new JButton("Add Road/Route");
	addEdge.setBackground(Color.BLACK);
	addEdge.setForeground(Color.WHITE);
	addEdge.setFont(new Font("Arial", Font.BOLD, 15));
	addEdge.setOpaque(true);
	addEdge.setBorderPainted(false);
	addEdge.addActionListener(this);
	
	mst=new JButton("Find Nearest Suppliers");
	mst.setBackground(Color.BLACK);
	mst.setForeground(Color.WHITE);
	mst.setFont(new Font("Arial", Font.BOLD, 15));
	mst.setOpaque(true);
	mst.setBorderPainted(false);
	mst.addActionListener(this);
	
	roundtour=new JButton("Plan Round Pickup Tour");
	roundtour.setBackground(Color.BLACK);
	roundtour.setForeground(Color.WHITE);
	roundtour.setFont(new Font("Arial", Font.BOLD, 15));
	roundtour.setOpaque(true);
	roundtour.setBorderPainted(false);
	roundtour.addActionListener(this);
	
	shortroute=new JButton("Find Fastest Delivery Route");
	shortroute.setBackground(Color.BLACK);
	shortroute.setForeground(Color.WHITE);
	shortroute.setFont(new Font("Arial", Font.BOLD, 15));
	shortroute.setOpaque(true);
	shortroute.setBorderPainted(false);
	shortroute.addActionListener(this);
	
	l1=new JLabel("Add Farm/Store");
	l1.setFont(new Font("Arial", Font.BOLD, 25));
	l1.setForeground(Color.white);
	l1.setOpaque(false);
	
	l2=new JLabel("Graph Configuration");
	l2.setFont(new Font("Arial", Font.BOLD, 30));
	l2.setForeground(Color.white);
	l2.setOpaque(false);
	
	to=new JLabel("To:");
	to.setFont(new Font("Arial", Font.BOLD, 15));
	to.setForeground(Color.white);
	to.setOpaque(false);
	to1=new JLabel("To:");
	to1.setFont(new Font("Arial", Font.BOLD, 15));
	to1.setForeground(Color.white);
	to1.setOpaque(false);
	
	from=new JLabel("From:");
	from.setFont(new Font("Arial", Font.BOLD, 15));
	from.setForeground(Color.white);
	from.setOpaque(false);
	from1=new JLabel("From:");
	from1.setFont(new Font("Arial", Font.BOLD, 15));
	from1.setForeground(Color.white);
	from1.setOpaque(false);
	from2=new JLabel("From:");
	from2.setFont(new Font("Arial", Font.BOLD, 15));
	from2.setForeground(Color.white);
	from2.setOpaque(false);
	
	dist=new JLabel("Distance (in km):   ");
	dist.setFont(new Font("Arial", Font.BOLD, 15));
	dist.setForeground(Color.white);
	dist.setOpaque(false);
	
	dij1=new JLabel("For Least Traffic Route,");
	dij1.setFont(new Font("Arial", Font.BOLD, 15));
	dij1.setForeground(Color.white);
	dij1.setOpaque(false);
	
	dij2=new JLabel("Enter Source and Destination.");
	dij2.setFont(new Font("Arial", Font.BOLD, 15));
	dij2.setForeground(Color.white);
	dij2.setOpaque(false);
	
	tsp1=new JLabel("For Round Tour, select the starting point");
	tsp1.setFont(new Font("Arial", Font.BOLD, 15));
	tsp1.setForeground(Color.white);
	tsp1.setOpaque(false);
	
	d=new JTextField(20);
	d.addActionListener(this);
	
	l1.setAlignmentX(Component.LEFT_ALIGNMENT);
	from.setAlignmentX(Component.LEFT_ALIGNMENT);
	src.setAlignmentX(Component.LEFT_ALIGNMENT);
	to.setAlignmentX(Component.LEFT_ALIGNMENT);
	dest.setAlignmentX(Component.LEFT_ALIGNMENT);
	dist.setAlignmentX(Component.LEFT_ALIGNMENT);
	d.setAlignmentX(Component.LEFT_ALIGNMENT);
	addEdge.setAlignmentX(Component.LEFT_ALIGNMENT);
	mst.setAlignmentX(Component.LEFT_ALIGNMENT);
	shortroute.setAlignmentX(Component.LEFT_ALIGNMENT);
	roundtour.setAlignmentX(Component.LEFT_ALIGNMENT);
	dij1.setAlignmentX(Component.LEFT_ALIGNMENT);
	dij2.setAlignmentX(Component.LEFT_ALIGNMENT);
	from1.setAlignmentX(Component.LEFT_ALIGNMENT);
	src1.setAlignmentX(Component.LEFT_ALIGNMENT);
	to1.setAlignmentX(Component.LEFT_ALIGNMENT);
	dest1.setAlignmentX(Component.LEFT_ALIGNMENT);
	tsp1.setAlignmentX(Component.LEFT_ALIGNMENT);
	from2.setAlignmentX(Component.LEFT_ALIGNMENT);
	src2.setAlignmentX(Component.LEFT_ALIGNMENT);
	src.setMaximumSize(src.getPreferredSize());
	dest.setMaximumSize(dest.getPreferredSize());
	src1.setMaximumSize(src1.getPreferredSize());
	dest1.setMaximumSize(dest1.getPreferredSize());
	src2.setMaximumSize(src2.getPreferredSize());
	d.setMaximumSize(d.getPreferredSize());
	producePanel.setMaximumSize(producePanel.getPreferredSize());
	
	left.add(Box.createVerticalStrut(10));
	left.add(l1);
	left.add(Box.createVerticalStrut(10));
	left.add(from);
	left.add(src);
	left.add(Box.createVerticalStrut(10));
	left.add(to);
	left.add(dest);
	left.add(Box.createVerticalStrut(10));
	left.add(dist);
	left.add(d);
	left.add(Box.createVerticalStrut(8));
	left.add(addEdge);
	JLabel produceLabel = new JLabel("Required Produce:");
	produceLabel.setFont(new Font("Arial", Font.BOLD, 15));
	produceLabel.setForeground(Color.WHITE);
	produceLabel.setOpaque(false);
	produceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

	left.add(produceLabel);
	left.add(producePanel);
	left.add(Box.createVerticalStrut(18));
	left.add(mst);
	left.add(Box.createVerticalStrut(10));
	left.add(shortroute);
	left.add(Box.createVerticalStrut(10));
	left.add(roundtour);
	left.add(Box.createVerticalStrut(18));
	left.add(dij1);
	left.add(Box.createVerticalStrut(6));
	left.add(dij2);
	left.add(Box.createVerticalStrut(10));
	left.add(from1);
	left.add(src1);
	left.add(Box.createVerticalStrut(10));
	left.add(to1);
	left.add(dest1);
	left.add(Box.createVerticalStrut(18));
	left.add(tsp1);
	left.add(Box.createVerticalStrut(10));
	left.add(from2);
	left.add(src2);
	
	resultArea = new JTextArea(3, 16);
	resultArea.setEditable(false);
	resultArea.setFont(new Font("Arial", Font.BOLD, 14));
	resultArea.setLineWrap(false);
	resultArea.setWrapStyleWord(false);

	JScrollPane resultScroll = new JScrollPane(resultArea);
	resultScroll.setPreferredSize(new Dimension(260, 100));
	resultScroll.setMaximumSize(new Dimension(260, 100));
	resultScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
	resultScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	resultScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	left.add(Box.createVerticalStrut(10));
	left.add(resultScroll);
	
	obj = new drawNodes();
	de=new drawEdge();
	kobj=new Kruskals(g,nodeLoc);
	dobj=new Dijkstra(g);
	robj=new RoundTour(g);
	
	de.setOpaque(false);
	kobj.setOpaque(false);
	dobj.setOpaque(false);
	robj.setOpaque(false);
	
	graphLayer = new JLayeredPane()
	{
	    public void doLayout()
	    {
	        Dimension size = getSize();

	        obj.setBounds(0, 0, size.width, size.height);
	        de.setBounds(0, 0, size.width, size.height);
	        kobj.setBounds(0, 0, size.width, size.height);
	        dobj.setBounds(0, 0, size.width, size.height);
	        robj.setBounds(0, 0, size.width, size.height);
	    }
	};

	graphLayer.setPreferredSize(new Dimension(800, 600));

	graphLayer.add(obj, Integer.valueOf(0));
	graphLayer.add(de, Integer.valueOf(1));
	graphLayer.add(kobj, Integer.valueOf(2));
	graphLayer.add(dobj, Integer.valueOf(3));
	graphLayer.add(robj, Integer.valueOf(4));

	kobj.setVisible(false);
	dobj.setVisible(false);
	robj.setVisible(false);

	contentRight.add(l2, BorderLayout.NORTH);
	contentRight.add(createLegend(), BorderLayout.EAST);
	contentRight.add(graphLayer, BorderLayout.CENTER);
	
	/*de.setLayout(new BorderLayout());
	obj.setLayout(new BorderLayout());
	obj.add(de, BorderLayout.CENTER);
	de.add(kobj, BorderLayout.CENTER);*/
	
	JScrollPane leftScroll = new JScrollPane(left);
	leftScroll.setOpaque(false);
	leftScroll.getViewport().setOpaque(false);
	leftScroll.setBorder(null);
	leftScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	leftScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	
	outer=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftScroll,right);
	outer.setPreferredSize(new Dimension(800, 500));
	outer.setResizeWeight(0.2);
	outer.setOpaque(false);
	bp.add(outer, BorderLayout.CENTER);
	
	add(bp, BorderLayout.CENTER);
	updateRoundTripOptionsFromFile();
	setExtendedState(JFrame.MAXIMIZED_BOTH);
	setVisible(true);
	outer.setDividerLocation(0.2);
   }
  
  void getNodes()
  {
      nodes.clear();
      produceItems.clear();
      allProduce.clear();
      nodeLoc.clear();

      try (BufferedReader br = new BufferedReader(new FileReader("users.txt")))
      {
          String line;
          String words[];

          while ((line = br.readLine()) != null)
          {
              words = line.split("-");

              nodes.add(words[3]);
              produceItems.add(words[4].toLowerCase());

              String products[] = words[4].split(",");

              for (String p : products)
              {
                  p = p.trim().toLowerCase();

                  if (!allProduce.contains(p))
                      allProduce.add(p);
              }
          }
      }
      catch (IOException e)
      {
          e.printStackTrace();
      }
  }
  
  void getEdges()
  {
      g.clear();

      try (BufferedReader br = new BufferedReader(new FileReader("graph.txt")))
      {
          String line;

          while ((line = br.readLine()) != null)
          {
              String words[] = line.split(" ");

              int s = Integer.parseInt(words[0]);
              int d = Integer.parseInt(words[1]);
              int w = Integer.parseInt(words[2]);

              g.add(new Graph(s, d, w));
          }
      }
      catch (IOException e)
      {
          e.printStackTrace();
      }
  }
  
  boolean isFarm(int index)
  {
      return nodes.get(index).endsWith(" Farm");
  }
  
  boolean isStore(int index)
  {
      return nodes.get(index).endsWith(" Store");
  }
  
  boolean farmProducesAny(int farmIndex, ArrayList<String> selectedItems)
  {
      String products[] = produceItems.get(farmIndex).split(",");

      for (String product : products)
      {
          product = product.trim().toLowerCase();

          for (String selected : selectedItems)
          {
              if (product.equals(selected))
                  return true;
          }
      }

      return false;
  }
  
  boolean farmProducesItem(int farmIndex, String selectedItem)
  {
      String products[] = produceItems.get(farmIndex).split(",");

      for (String product : products)
      {
          if (product.trim().toLowerCase().equals(selectedItem))
              return true;
      }

      return false;
  }
  
  ArrayList<String> getSelectedProduceItems()
  {
      ArrayList<String> selectedItems = new ArrayList<String>();

      for (JCheckBox check : produceChecks)
      {
          if (check.isSelected())
              selectedItems.add(check.getText().toLowerCase());
      }

      return selectedItems;
  }
  
  ArrayList<Graph> getProduceEdges(ArrayList<String> selectedItems)
  {
      ArrayList<Graph> filtered = new ArrayList<Graph>();
      int root = indexOfNode(loggedInNode);
      supplierNotes.clear();
      supplierPaths.clear();

      if (root == -1)
          return filtered;

      for (String selectedItem : selectedItems)
      {
          SupplierPath bestPath = null;

          for (int i = 0; i < nodes.size(); i++)
          {
              if (!isFarm(i) || !farmProducesItem(i, selectedItem))
                  continue;

              SupplierPath candidate = buildSupplierPath(root, i);

              if (candidate != null && (bestPath == null || candidate.pathWeight < bestPath.pathWeight))
                  bestPath = candidate;
          }

          if (bestPath != null)
          {
              addPathEdges(filtered, bestPath.pathNodes);
              supplierPaths.add(bestPath);

              if (bestPath.pathNodes.size() > 2)
              {
                  supplierNotes.add(nodes.get(bestPath.farmIndex) + " is selected for " + selectedItem +
                                    " and is connected via " + getIntermediatePathText(bestPath.pathNodes) +
                                    " because a direct route is not available.");
              }
          }
      }

      return filtered;
  }
  
  Graph getDirectEdge(int sourceNode, int targetNode)
  {
      Graph bestEdge = null;

      for (Graph edge : g)
      {
          boolean sameDirection = edge.s == sourceNode && edge.d == targetNode;
          boolean oppositeDirection = edge.s == targetNode && edge.d == sourceNode;

          if (sameDirection || oppositeDirection)
          {
              if (bestEdge == null || edge.w < bestEdge.w)
                  bestEdge = edge;
          }
      }

      return bestEdge;
  }
  
  SupplierPath buildSupplierPath(int sourceNode, int targetNode)
  {
      Graph directEdge = getDirectEdge(sourceNode, targetNode);

      if (directEdge != null)
      {
          ArrayList<Integer> directPath = new ArrayList<Integer>();
          directPath.add(sourceNode);
          directPath.add(targetNode);
          return new SupplierPath(sourceNode, targetNode, directPath, directEdge.w);
      }

      ArrayList<Integer> pathNodes = getShortestPathNodes(sourceNode, targetNode);

      if (pathNodes.size() == 0)
          return null;

      return new SupplierPath(sourceNode, targetNode, pathNodes, getPathWeight(pathNodes));
  }
  
  ArrayList<Integer> addShortestPathEdges(int sourceNode, int targetNode, ArrayList<Graph> filtered)
  {
      ArrayList<Integer> pathNodes = getShortestPathNodes(sourceNode, targetNode);
      addPathEdges(filtered, pathNodes);
      return pathNodes;
  }
  
  ArrayList<Integer> getShortestPathNodes(int sourceNode, int targetNode)
  {
      ArrayList<Integer> pathNodes = new ArrayList<Integer>();
      int n = nodes.size();
      int dist[] = new int[n];
      int parent[] = new int[n];
      Graph parentEdge[] = new Graph[n];
      boolean visited[] = new boolean[n];

      for (int i = 0; i < n; i++)
      {
          dist[i] = Integer.MAX_VALUE;
          parent[i] = -1;
      }

      dist[sourceNode] = 0;

      for (int count = 0; count < n; count++)
      {
          int u = -1;

          for (int i = 0; i < n; i++)
          {
              if (!visited[i] && (u == -1 || dist[i] < dist[u]))
                  u = i;
          }

          if (u == -1 || dist[u] == Integer.MAX_VALUE)
              break;

          visited[u] = true;

          for (Graph edge : g)
          {
              int v = -1;

              if (edge.s == u)
                  v = edge.d;
              else if (edge.d == u)
                  v = edge.s;

              if (v != -1 && !visited[v] && dist[u] + edge.w < dist[v])
              {
                  dist[v] = dist[u] + edge.w;
                  parent[v] = u;
                  parentEdge[v] = edge;
              }
          }
      }

      if (dist[targetNode] == Integer.MAX_VALUE)
          return pathNodes;

      int current = targetNode;

      while (current != sourceNode && parent[current] != -1)
      {
          pathNodes.add(0, current);
          current = parent[current];
      }

      pathNodes.add(0, sourceNode);
      return pathNodes;
  }
  
  void addPathEdges(ArrayList<Graph> filtered, ArrayList<Integer> pathNodes)
  {
      for (int i = 0; i < pathNodes.size() - 1; i++)
      {
          Graph edge = getDirectEdge(pathNodes.get(i), pathNodes.get(i + 1));
          addUniqueEdge(filtered, edge);
      }
  }
  
  int getPathWeight(ArrayList<Integer> pathNodes)
  {
      int total = 0;

      for (int i = 0; i < pathNodes.size() - 1; i++)
      {
          Graph edge = getDirectEdge(pathNodes.get(i), pathNodes.get(i + 1));

          if (edge != null)
              total = total + edge.w;
      }

      return total;
  }
  
  String getIntermediatePathText(ArrayList<Integer> pathNodes)
  {
      if (pathNodes.size() <= 2)
          return "no intermediate node";

      String text = "";

      for (int i = 1; i < pathNodes.size() - 1; i++)
      {
          if (text.length() > 0)
              text = text + " -> ";

          text = text + nodes.get(pathNodes.get(i));
      }

      return text;
  }
  
  void addUniqueEdge(ArrayList<Graph> edges, Graph newEdge)
  {
      if (newEdge == null)
          return;

      for (Graph edge : edges)
      {
          boolean sameDirection = edge.s == newEdge.s && edge.d == newEdge.d;
          boolean oppositeDirection = edge.s == newEdge.d && edge.d == newEdge.s;

          if ((sameDirection || oppositeDirection) && edge.w == newEdge.w)
              return;
      }

      edges.add(newEdge);
  }
  
  void updateRoundTripOptions(ArrayList<Graph> supplierMst)
  {
      DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
      roundTripStores.clear();

      for (int i = 0; i < nodes.size(); i++)
      {
          if (!isFarm(i))
              continue;

          ArrayList<Integer> stores = new ArrayList<Integer>();

          for (Graph edge : supplierMst)
          {
              if (edge.s == i && isStore(edge.d) && !stores.contains(edge.d))
                  stores.add(edge.d);
              else if (edge.d == i && isStore(edge.s) && !stores.contains(edge.s))
                  stores.add(edge.s);
          }

          if (stores.size() > 1)
          {
              model.addElement(nodes.get(i));
              roundTripStores.add(stores);
          }
      }

      src2.setModel(model);
      src2.setMaximumSize(src2.getPreferredSize());
      src2.revalidate();
      source2 = model.getSize() > 0 ? model.getElementAt(0) : null;
  }
  
  void updateRoundTripOptionsFromFile()
  {
      DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
      roundTripStores.clear();

      if (loggedInNode == null)
      {
          src2.setModel(model);
          return;
      }

      int farmIndex = indexOfNode(loggedInNode);

      if (farmIndex == -1 || !isFarm(farmIndex))
      {
          src2.setModel(model);
          return;
      }

      ArrayList<Integer> stores = getStoresConnectedToFarm(loggedInNode);

      if (stores.size() > 1)
      {
          model.addElement(loggedInNode);
          roundTripStores.add(stores);
      }

      src2.setModel(model);
      src2.setMaximumSize(src2.getPreferredSize());
      src2.revalidate();
      source2 = model.getSize() > 0 ? model.getElementAt(0) : null;
  }
  
  ArrayList<Integer> getStoresConnectedToFarm(String farmName)
  {
      ArrayList<Integer> stores = new ArrayList<Integer>();

      try (BufferedReader br = new BufferedReader(new FileReader(SUPPLY_MST_FILE)))
      {
          String line;

          while ((line = br.readLine()) != null)
          {
              String parts[] = line.split("\\|");

              if (parts.length >= 2 && parts[1].equals(farmName))
              {
                  int storeIndex = indexOfNode(parts[0]);

                  if (storeIndex != -1 && !stores.contains(storeIndex))
                      stores.add(storeIndex);
              }
          }
      }
      catch (FileNotFoundException e)
      {
          // No store MST has been saved yet.
      }
      catch (IOException e)
      {
          e.printStackTrace();
      }

      return stores;
  }
  
  void saveStoreSupplierPaths()
  {
      if (loggedInNode == null || indexOfNode(loggedInNode) == -1 || !isStore(indexOfNode(loggedInNode)))
          return;

      ArrayList<String> lines = new ArrayList<String>();

      try (BufferedReader br = new BufferedReader(new FileReader(SUPPLY_MST_FILE)))
      {
          String line;

          while ((line = br.readLine()) != null)
          {
              String parts[] = line.split("\\|");

              if (parts.length > 0 && !parts[0].equals(loggedInNode))
                  lines.add(line);
          }
      }
      catch (FileNotFoundException e)
      {
          // The file will be created below.
      }
      catch (IOException e)
      {
          e.printStackTrace();
      }

      for (SupplierPath supplierPath : supplierPaths)
      {
          lines.add(nodes.get(supplierPath.storeIndex) + "|" +
                    nodes.get(supplierPath.farmIndex) + "|" +
                    getPathText(supplierPath.pathNodes));
      }

      try (FileWriter fw = new FileWriter(SUPPLY_MST_FILE, false))
      {
          for (String line : lines)
              fw.write(line + "\n");
      }
      catch (IOException e)
      {
          e.printStackTrace();
      }
  }
  
  String getPathText(ArrayList<Integer> pathNodes)
  {
      String text = "";

      for (int i = 0; i < pathNodes.size(); i++)
      {
          if (i > 0)
              text = text + " -> ";

          text = text + nodes.get(pathNodes.get(i));
      }

      return text;
  }
  
  static void addCoordinatePair(int x, int y, int i)
  {
	Coordinates cd=new Coordinates(x,y);
	nodeLoc.add(i,cd);
  }
  
  int indexOfNode(String n)
  {
	int i;
	for(i=0;i<nodes.size();i++)
	{
	   if(n.compareTo(nodes.get(i))==0)
	    return i;
	}
	return -1;
  }
  void addEdge(String src, String dest, int weight)
  {
	int s,d;
	s=indexOfNode(src);
	
    if(s==-1)
     return;
    
    d=indexOfNode(dest);
    
    if(d==-1)
     return;
    
    Graph e=new Graph(s,d,weight);
    g.add(e);
    
    de.setValues(s, d, weight);
    de.repaint();
    //obj.add(new drawEdge(s,d,weight));
    try(FileWriter fw=new FileWriter("graph.txt",true))
    {
      fw.write(s+" "+d+" "+weight+"\n");
    }
    catch(IOException i)
    {
      i.printStackTrace();
    }
  }
  
  JPanel legendItem(Color color, String text)
  {
      JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
      row.setOpaque(false);

      JPanel block = new JPanel();
      block.setBackground(color);
      block.setPreferredSize(new Dimension(18, 18));

      JLabel label = new JLabel(text);
      label.setForeground(Color.WHITE);
      label.setFont(new Font("Arial", Font.BOLD, 13));

      row.add(block);
      row.add(label);

      return row;
  }

  JPanel createLegend()
  {
      JPanel legend = new JPanel();
      legend.setOpaque(false);
      legend.setLayout(new GridLayout(0, 1, 5, 5));

      JLabel title = new JLabel("Legend");
      title.setFont(new Font("Arial", Font.BOLD, 16));
      title.setForeground(Color.WHITE);

      legend.add(title);
      legend.add(legendItem(Color.BLUE, "Store"));
      legend.add(legendItem(Color.GREEN, "Farm"));
      legend.add(legendItem(Color.ORANGE, "Existing route"));
      legend.add(legendItem(new Color(51, 0, 51), "Least traffic route"));
      legend.add(legendItem(Color.CYAN, "Nearest supplier network"));
      legend.add(legendItem(Color.BLACK, "Round tour route"));

      return legend;
  }
  
  public void actionPerformed(ActionEvent ae)
  {
	b=ae.getActionCommand();
	weight=d.getText();
	if(b.equals("Add Road/Route"))
	{
	  addEdge(source,destination,Integer.parseInt(weight));
	  //Done: Add Connection Button Work done (draw edge from source to destination, write edges to file.)
	}
	else if(b.equals("Find Nearest Suppliers"))
	{
	  // call kruskal algo method and display result
		ArrayList<String> selectedItems = getSelectedProduceItems();

		if (selectedItems.size() == 0)
		{
		    resultArea.setText("Please select at least one produce item.");
		    return;
		}

		ArrayList<Graph> filtered = getProduceEdges(selectedItems);

		dobj.setVisible(false);
		robj.setVisible(false);

		kobj.display(filtered);
		saveStoreSupplierPaths();
		updateRoundTripOptionsFromFile();
		kobj.setVisible(true);
		kobj.repaint();

		String result = "Nearest supplier network for: " + selectedItems +
		                "\nCyan lines start from " + loggedInNode + ".";

		for (String note : supplierNotes)
		    result = result + "\n" + note;

		resultArea.setText(result);

		graphLayer.repaint();
	}
	else if(b.equals("Plan Round Pickup Tour"))
	{
		// call floyd warshall algo method and display result
		kobj.setVisible(false);
		dobj.setVisible(false);
		int selectedFarmIndex = src2.getSelectedIndex();

		if (selectedFarmIndex == -1)
		{
		    resultArea.setText("Round tour is available only for farms connected to multiple stores in the supplier network.");
		    return;
		}

		int s = indexOfNode(source2);

	    if (s == -1)
	        return;

		robj.TSP(s, roundTripStores.get(selectedFarmIndex));
		resultArea.setText(robj.getResultText());
	    robj.setVisible(true);
	    robj.repaint();

	    graphLayer.repaint();
	}
	else if(b.equals("Find Fastest Delivery Route"))
	{
		// simulate traffic, call dijkstra algo method and display result
		kobj.setVisible(false);

	    int s = indexOfNode(source1);
	    int d = indexOfNode(destination1);
	    
	    System.out.println("s="+ s+"    d="+d);
	    
	    if (s == -1 || d == -1)
	        return;

	    dobj.findShortestPath(nodes.size(), s, d);
	    dobj.setVisible(true);
	    dobj.repaint();

	    resultArea.setText(dobj.getResultText());
	    graphLayer.repaint();
	}
  }
  
  public void itemStateChanged(ItemEvent ae)
  {
	source=""+src.getSelectedItem();
	destination=""+dest.getSelectedItem(); 
	source1=""+src1.getSelectedItem();
	destination1=""+dest1.getSelectedItem();
	source2=""+src2.getSelectedItem();
	System.out.println("source1= "+source1);
	System.out.println("destination1= "+destination1);
  }
}
