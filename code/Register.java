package code;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
class Register extends JFrame implements ActionListener,ItemListener 
{
   public static final long serialVersionUID=1L;
   
   JPanel left,right,outer;
   JLabel createacc,user,pass,role,produce,farmStore,success;
   JTextField username,farmStoreName,production;
   JPasswordField password;
   JButton register;
   JComboBox<String> roles;
   
   String u,p,r,n,pr;
   
   Register()
   {
	 String roleList[]= {"Select Occupation","Farmer","Store Owner"};
	 outer=new JPanel();
	 outer.setBackground(Color.BLACK);
	 outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
	 
	 JPanel formPanel = new JPanel();
	 formPanel.setOpaque(false);
	 //formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.X_AXIS));
	 
	 left=new JPanel(new GridBagLayout());
	 left.setLayout(new BoxLayout(left,BoxLayout.Y_AXIS));
	 left.setOpaque(false);
	 
	 right=new JPanel();
	 right.setLayout(new BoxLayout(right,BoxLayout.Y_AXIS));
	 right.setOpaque(false);
	 
	 createacc=new JLabel("Create Account");
	 createacc.setFont(new Font("Arial", Font.BOLD, 25));
	 createacc.setForeground(Color.cyan);
	 
	 user=new JLabel("Username");
	 user.setFont(new Font("Arial", Font.BOLD, 15));
	 user.setForeground(Color.white);
	 
	 pass=new JLabel("Password");
	 pass.setFont(new Font("Arial", Font.BOLD, 15));
	 pass.setForeground(Color.white);
	 
	 role=new JLabel("Role");
	 role.setFont(new Font("Arial", Font.BOLD, 15));
	 role.setForeground(Color.white);
	 
	 farmStore=new JLabel("Farm/Store Name");
	 farmStore.setFont(new Font("Arial", Font.BOLD, 15));
	 farmStore.setForeground(Color.white);
	 
	 produce=new JLabel("What do you produce/sell? (Separate multiple items by commas)");
	 produce.setFont(new Font("Arial", Font.BOLD, 15));
	 produce.setForeground(Color.white);
	 
	 success=new JLabel(" ");
	 success.setFont(new Font("Arial", Font.BOLD, 15));
	 success.setForeground(Color.white);
	 
	 roles=new JComboBox<String>(roleList);
	 roles.addItemListener(this);
	 
	 register=new JButton("Register");
	 register.setBackground(Color.cyan);
	 register.setForeground(Color.black);
	 register.setFont(new Font("Arial", Font.BOLD, 15));
	 register.setOpaque(true);
	 register.setBorderPainted(false);
	 register.addActionListener(this);
	 
	 username=new JTextField(20);
	 password=new JPasswordField(20);
	 farmStoreName=new JTextField(20);
	 production=new JTextField(20);
	 
	 left.add(user);
	 left.add(Box.createVerticalStrut(30));
	 left.add(pass);
	 left.add(Box.createVerticalStrut(30));
	 left.add(role);
	 left.add(Box.createVerticalStrut(30));
	 left.add(farmStore);
	 left.add(Box.createVerticalStrut(30));
	 left.add(produce);
	 
	 right.add(username);
	 right.add(Box.createVerticalStrut(30));
	 right.add(password);
	 right.add(Box.createVerticalStrut(30));
	 right.add(roles);
	 right.add(Box.createVerticalStrut(30));
	 right.add(farmStoreName);
	 right.add(Box.createVerticalStrut(30));
	 right.add(production);
	 
	 formPanel.add(left);
	 formPanel.add(Box.createHorizontalStrut(30));
	 formPanel.add(right);

	 outer.add(Box.createVerticalGlue());
	 outer.add(createacc);
	 outer.add(Box.createVerticalStrut(15));
	 outer.add(formPanel);
	 //outer.add(Box.createVerticalStrut(5));
	 outer.add(success);
	 outer.add(Box.createVerticalGlue());
	 outer.add(register);
	 outer.add(Box.createVerticalGlue());
	 
	 add(outer);
	 setSize(500,450);
	 //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 setVisible(true);
   }
   
   public void actionPerformed(ActionEvent ae)
   {
	 u=username.getText();
	 p=new String(password.getPassword());
	 n=farmStoreName.getText();
	 pr=production.getText();
	 
	 try(FileWriter obj=new FileWriter("users.txt",true))
	 {
	   if(r.equals("Farmer"))
		 n=n+" Farm";
	   else
		 n=n+" Store";
	   
	   obj.write(u+"-"+p+"-"+r+"-"+n+"-"+pr+"\n");
	   success.setText("Account Creation Successful");
	 }
	 catch(IOException e)
	 {
	   e.printStackTrace();
	 }
   }
   
   public void itemStateChanged(ItemEvent ae)
   {
	r=""+roles.getSelectedItem(); 
	/*try(FileWriter obj=new FileWriter("users.txt",true))
	 {
	   obj.write(u+"-"+p+"-"+r+"-"+n+"-"+pr+"\n");
	   success.setText("Account Creation Successful");
	 }
	catch(IOException e)
	 {
	   e.printStackTrace();
	 }*/
   }
}
