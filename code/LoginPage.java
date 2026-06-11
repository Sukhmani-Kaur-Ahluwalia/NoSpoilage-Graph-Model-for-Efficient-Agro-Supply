package code;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
class LoginPage extends JFrame implements ActionListener
{
  public static final long serialVersionUID=1L;
  JPanel left,right,content;
  JTextField username;
  JPasswordField password;
  JLabel l1,l2,user,pass,newuser,success,space;
  JButton login,register;
  String User,Pass;
  LoginPage()
  {
	left=new JPanel(new GridBagLayout());
	left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS));
	left.setOpaque(false);
	right=new JPanel();
	right.setLayout(new BoxLayout(right,BoxLayout.Y_AXIS));
	right.setOpaque(false);
	
	username=new JTextField(20);
	//password=new JTextField(20);
	password=new JPasswordField(20);
	
	l1=new JLabel("Welcome to NoSpoilage");
	l1.setFont(new Font("Arial", Font.BOLD, 40));
	l1.setForeground(Color.white);
	l1.setOpaque(false);
	l2=new JLabel("Log In");
	l2.setFont(new Font("Arial", Font.BOLD, 30));
	l2.setForeground(Color.white);
	l2.setOpaque(false);
	newuser=new JLabel("New User? Create your own account");
	newuser.setFont(new Font("Arial", Font.BOLD, 20));
	newuser.setForeground(Color.white);
	newuser.setOpaque(false);
	success=new JLabel(" ");
	space=new JLabel(" ");
	success.setFont(new Font("Arial", Font.BOLD, 15));
	success.setForeground(Color.white);
	success.setOpaque(false);
	
	user=new JLabel("Username");
	user.setFont(new Font("Arial", Font.BOLD, 20));
	user.setForeground(Color.white);
	user.setOpaque(false);
	pass=new JLabel("Password");
	pass.setFont(new Font("Arial", Font.BOLD, 20));
	pass.setForeground(Color.white);
	pass.setOpaque(false);
	
	login=new JButton("Login");
	login.setBackground(Color.BLACK);
	login.setForeground(Color.WHITE);
	login.setFont(new Font("Arial", Font.BOLD, 15));
	login.setOpaque(true);
	login.setBorderPainted(false);
	register=new JButton("Create Account");
	register.setBackground(Color.BLACK);
	register.setForeground(Color.WHITE);
	register.setFont(new Font("Arial", Font.BOLD, 15));
	register.setOpaque(true);
	register.setBorderPainted(false);
	
	login.addActionListener(this);
	register.addActionListener(this);
	
	
	left.add(l1);
	left.add(Box.createVerticalGlue());
	right.add(l2);
	right.add(Box.createVerticalStrut(30));
	right.add(user);
	right.add(username);
	right.add(Box.createVerticalStrut(15));
	right.add(pass);
	right.add(password);
	right.add(Box.createVerticalStrut(15));
	right.add(login);
	right.add(Box.createVerticalStrut(15));
	right.add(success);
	right.add(Box.createVerticalStrut(15));
	right.add(newuser);
	right.add(register);
	right.add(Box.createVerticalGlue());
	
	backgroundPanel bp=new backgroundPanel();
	bp.setLayout(new GridBagLayout());
	
	content=new JPanel();
	content.setOpaque(false);
	content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
	
	content.add(left);
	content.add(Box.createHorizontalStrut(30));
	content.add(right);
	bp.add(content, new GridBagConstraints());
	
	add(bp);
	setSize(400,300);
	pack();
	setLocationRelativeTo(null);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setVisible(true);
  }
  
  boolean ifExists(String user, String pass)
  {
	try(BufferedReader br=new BufferedReader(new FileReader("users.txt")))
	{
	  String line="",words[];
	  while((line=br.readLine())!=null)
	  {
	    words=line.split("-");
		
		if(words[0].compareTo(user)==0 && words[1].compareTo(pass)==0)
		 return true;
	  }
	}
	catch(FileNotFoundException e)
	{
	  e.printStackTrace();
	}
	catch(Exception e)
	{
	  e.printStackTrace();
	}
	return false;
  }// end of function
  
  public void actionPerformed(ActionEvent ae)
  {
	if(ae.getActionCommand().compareTo("Create Account")==0)
	{
      new Register();
	}
	else
	{
		User=username.getText();
	    Pass=new String(password.getPassword());  
	
	    if(ifExists(User,Pass))
	    {
	      success.setText("Login Successful"); 
	      new Dashboard();
	    }
	    else
	    {
	     success.setText("Invalid Credentials");
	    }
	}
  }
}

