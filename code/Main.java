package code;

import javax.swing.*;
public class Main
{
  public static void main(String args[])
  {
	 try
	 {
		 SwingUtilities.invokeAndWait(
					new Runnable()
					{
					  public void run()
					  {
						  try
							 {
							   UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
							   new LoginPage();
							 }
							 catch(ClassNotFoundException e)
							 {
							   e.printStackTrace();
							 }
							 catch(InstantiationException e)
							 {
							   e.printStackTrace();
							 }
							 catch(IllegalAccessException e)
							 {
							   e.printStackTrace();
							 }
							 catch(UnsupportedLookAndFeelException e)
							 {
							   e.printStackTrace();
							 }
							 catch(Exception e)
							 {
								e.printStackTrace();
							 }
					  }
					}
			  );
	 }//end of try
	 catch(InterruptedException e)
	 {
	   e.printStackTrace();
	 }
	 catch(Exception e)
	 {
	   e.printStackTrace();
	 }
  }
}
