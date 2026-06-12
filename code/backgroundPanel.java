package code;

import javax.swing.*;
import java.awt.*;
class backgroundPanel extends JPanel
{
	public static final long serialVersionUID=1L;
	
	//overriding paint component method
	protected void paintComponent(Graphics g)
	{
	  super.paintComponent(g);
	  Graphics2D g2=(Graphics2D) g;
	  
	  Color c1=new Color(112,38,129);
	  Color c2=new Color(212,112,194);
	  
	  GradientPaint gp=new GradientPaint(0, 0, c1, getWidth(), getHeight(), c2);
	  
	  g2.setPaint(gp);
	  g2.fillRect(0, 0, getWidth(), getHeight());
	}
}// end of background class