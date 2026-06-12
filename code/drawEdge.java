package code;

import java.awt.*;
import javax.swing.*;

class drawEdge extends JPanel 
{
   int s,d,w,x,y;
   public static final long serialVersionUID=1L;
   
   void setValues(int i, int j, int k)
   {
	setOpaque(false);
	setPreferredSize(new Dimension(600, 500)); 
	 s=i;
	 d=j;
	 w=k;
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

       for (int i = 0; i < Dashboard.g.size(); i++)
       {
           Graph edge = Dashboard.g.get(i);

           int s = edge.s;
           int d = edge.d;
           int w = edge.w;

           int x1 = Dashboard.nodeLoc.get(s).getX();
           int y1 = Dashboard.nodeLoc.get(s).getY();
           int x2 = Dashboard.nodeLoc.get(d).getX();
           int y2 = Dashboard.nodeLoc.get(d).getY();

           int duplicateNumber = 0;

           for (int j = 0; j < i; j++)
           {
               Graph prev = Dashboard.g.get(j);

               if ((prev.s == s && prev.d == d) || (prev.s == d && prev.d == s))
               {
                   duplicateNumber++;
               }
           }

           int offset = duplicateNumber * 15;

           double dx = x2 - x1;
           double dy = y2 - y1;
           double length = Math.sqrt(dx * dx + dy * dy);

           if (length == 0)
               continue;

           int offsetX = (int) (-dy / length * offset);
           int offsetY = (int) (dx / length * offset);

           int drawX1 = x1 + offsetX;
           int drawY1 = y1 + offsetY;
           int drawX2 = x2 + offsetX;
           int drawY2 = y2 + offsetY;

           g2d.setColor(Color.ORANGE);
           g2d.drawLine(drawX1, drawY1, drawX2, drawY2);

           int midX = (drawX1 + drawX2) / 2;
           int midY = (drawY1 + drawY2) / 2;

           Font boldfont=new Font("Arial", Font.BOLD, 15);
 	       g2d.setFont(boldfont);
           
           g2d.setColor(Color.BLACK);
           g2d.drawString(String.valueOf(w), midX, midY);
       }
   }
   
  /* void calc(int x1, int y1, int x2, int y2)
   {
	  x=(int)((double)(x1+x2)/2);
	  y=(int)((double)(y1+y2)/2);
   }
   
 //overriding paint component method
   protected void paintComponent(Graphics g)
   {
       super.paintComponent(g);

       if (Dashboard.nodes.size() == 0)
           return;

       if (Dashboard.nodeLoc.size() < Dashboard.nodes.size())
           return;

       Graphics2D g2d = (Graphics2D) g;

       g2d.setColor(Color.RED);

       for (Graph edge : Dashboard.g)
       {
           int s = edge.s;
           int d = edge.d;
           int w = edge.w;

           int x1 = Dashboard.nodeLoc.get(s).getX();
           int y1 = Dashboard.nodeLoc.get(s).getY();
           int x2 = Dashboard.nodeLoc.get(d).getX();
           int y2 = Dashboard.nodeLoc.get(d).getY();

           g2d.drawLine(x1, y1+10, x2, y2+10);

           int midX = (x1 + x2) / 2;
           int midY = (y1 + y2) / 2;
           
           Font boldfont=new Font("Arial", Font.BOLD, 13);
 	       g2d.setFont(boldfont);
           g2d.setColor(Color.BLACK);
           g2d.drawString(String.valueOf(w), midX, midY);
           g2d.setColor(Color.RED);
       }
   }*/
   /*protected void paintComponent(Graphics g)
   {
	  super.paintComponent(g);
	  
	  if (Dashboard.nodes.size() == 0)
	        return;
		  
	  Graphics2D g2d=(Graphics2D) g;
	  
	  if("Add Connection".equals(Dashboard.b))
	  {
		  calc(Dashboard.nodeLoc.get(s).getX(),Dashboard.nodeLoc.get(s).getY(),Dashboard.nodeLoc.get(d).getX(),Dashboard.nodeLoc.get(d).getY());
		  
		  g2d.setColor(Color.RED);
		  g2d.drawLine(Dashboard.nodeLoc.get(s).getX(),Dashboard.nodeLoc.get(s).getY(),Dashboard.nodeLoc.get(d).getX(),Dashboard.nodeLoc.get(d).getY());
		  Font boldfont=new Font("Arial", Font.BOLD, 13);
	      g2d.setFont(boldfont);
	      g2d.setColor(Color.BLACK);
	      g2d.drawString(w+"", x, y); 
	  }
   }*/
}
