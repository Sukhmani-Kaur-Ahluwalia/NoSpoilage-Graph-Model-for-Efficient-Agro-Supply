package code;

import java.awt.*;

import javax.swing.*;

class drawNodes extends JPanel
{
	int r=350,s=30;
	public static final long serialVersionUID=1L;
	
	drawNodes()
    {
        setOpaque(false);
        setPreferredSize(new Dimension(600, 500));
    }
	
	//overriding paint component method
   protected void paintComponent(Graphics g)
   {
	  super.paintComponent(g);
	  
	  if (Dashboard.nodes.size() == 0)
        return;
	  
	  Graphics2D g2d=(Graphics2D) g;
		  
	  g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      int centerX = getWidth() / 2, centerY = getHeight() / 2;
      
      Dashboard.nodeLoc.clear();
      
      double angle;
      String st;
      
      for (int i = 0; i < Dashboard.nodes.size(); i++) 
      {
          // Calculating angle for each node
          angle = 2 * Math.PI * i / Dashboard.nodes.size();
          
          // Calculate position
          int x = (int) (centerX + r * Math.cos(angle)) - s / 2;
          int y = (int) (centerY + r * Math.sin(angle)) - s / 2;

          // Draw Node
          st=Dashboard.nodes.get(i);
          
          if(st.substring(st.indexOf(' ')+1).equals("Farm"))
             g2d.setColor(Color.GREEN);
          else
        	 g2d.setColor(Color.blue);
          
          g2d.fillOval(x, y, s, s);
          
          Dashboard.nodeLoc.add(new Coordinates(x + s / 2, y + s / 2));
          
          Font boldfont=new Font("Arial", Font.BOLD, 13);
          g2d.setFont(boldfont);
          g2d.setColor(Color.BLACK);
          g2d.drawString(st, x + 10, y+40);
      }
   }
}