// UserRegPortal is a standalone registration window
// used for testing registration separately from the main login screen
// validates that password is strong enough before accepting it

import java.awt.*;
import javax.swing.*;

public class UserRegPortal extends JFrame {

    private JTextField input_name;      // username input box
    private JPasswordField input_secret; // password input box

    public UserRegPortal() {

        setTitle("NoSpoilage - Registration");
        setSize(850, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // center on screen
        System.out.println("Register New user screen setup done.");
        // background panel that draws the farmer image + dark overlay
        JPanel mainBackground = new JPanel() {
           
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // draw the background image stretched to fill
                ImageIcon bgImg = new ImageIcon("farmer_2_05.jpg");
                g.drawImage(bgImg.getImage(), 0, 0, getWidth(), getHeight(), this);

                // draw semi-transparent black overlay on top of the image
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 80));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        mainBackground.setLayout(new GridBagLayout());
        setContentPane(mainBackground);

        // the dark registration card in the center of the screen
        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(new Color(0, 0, 0, 180));
      loginBox.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1));
        loginBox.setPreferredSize(new Dimension(400, 450));
        

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // title at the top of the card
        JLabel headerLbl = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        headerLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        headerLbl.setForeground(Color.WHITE);
      gbc.gridy = 0;
        gbc.gridx = 0;
        
        gbc.gridwidth = 2;
        loginBox.add(headerLbl, gbc);

        // username row
      gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridy = 1;
       
        loginBox.add(makeLabel("User ID:"), gbc);

        input_name = new JTextField(15);
        gbc.gridx = 1;
            loginBox.add(input_name, gbc);

        // password row
     gbc.gridy = 2;
        gbc.gridx = 0;
        loginBox.add(makeLabel("Password:"), gbc);

        input_secret = new JPasswordField(15);
        gbc.gridx = 1;
            loginBox.add(input_secret, gbc);

        // register button
        JButton submitBtn = new JButton("Register Now");
       submitBtn.setFocusPainted(false);
 submitBtn.setForeground(Color.WHITE);
        submitBtn.setBackground(new Color(100, 20, 67));
       
            gbc.gridx = 0;
        gbc.gridy = 3;
       
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 10, 10);
              loginBox.add(submitBtn, gbc);

        add(loginBox);

        // handle the register button click
        submitBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {

                String username = input_name.getText().trim();
                String password = new String(input_secret.getPassword());

                // both fields must be filled in
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // check password rules one at a time

                // rule 1: must be at least 6 characters
                boolean isLongEnough = false;
                if (password.length() >= 6) {
                    isLongEnough = true;
                }

                // rule 2: must contain at least one digit
                // check each character manually instead of using regex
                boolean hasDigit = false;
                for (int i = 0; i < password.length(); i++) {
                    char c = password.charAt(i);
                    if (Character.isDigit(c)) {
                        hasDigit = true;
                    }
                }

                // rule 3: must contain at least one special character
                boolean hasSpecial = false;
                if (password.contains("@") || password.contains("#") || password.contains("$")) {
               System.out.println("Contains special characters.");
                    hasSpecial = true;
                }

                System.out.println("longEnough=" + isLongEnough + " hasDigit=" + hasDigit + " hasSpecial=" + hasSpecial);

                // show the right error message for whichever rule failed first
                if (isLongEnough == false) {
                    JOptionPane.showMessageDialog(null,
                            "Password must be at least 6 characters long!",
                            "Weak Password", JOptionPane.WARNING_MESSAGE);

                } else if (hasDigit == false) {
                  System.out.println("Weak password. Entered the false if conditiion.");
                    JOptionPane.showMessageDialog(null,
                            "Password must include at least one numeric digit!",
                            "Weak Password", JOptionPane.WARNING_MESSAGE);

                } else if (hasSpecial == false) {
               System.out.println("No special character. Entered the false if.");
                    JOptionPane.showMessageDialog(null,
                            "Password must include a special character (like @, #, $)",
                            "Weak Password", JOptionPane.WARNING_MESSAGE);

                } else {
                    // all rules passed - register the user
                    JOptionPane.showMessageDialog(null, "Registered Successfully!");
                }
            }
        });
    }

    // helper to make a styled gray label for field names
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Color.LIGHT_GRAY);
        return lbl;
    }
}