package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {

    JTextField usernameField;
    JPasswordField passwordField;

    public LoginPage() {

        setTitle("Login - AgroDirect");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ===== MAIN PANEL WITH NEON GRADIENT =====
        JPanel mainPanel = UIUtils.createGradientPanel();

        mainPanel.setLayout(new GridBagLayout());

        // ===== CARD PANEL =====
        JPanel card = UIUtils.createDarkPanel(180); // Semi-transparent dark
        card.setPreferredSize(new Dimension(800, 450));
        card.setLayout(new GridLayout(1, 2));
        card.setBorder(BorderFactory.createLineBorder(UIUtils.NEON_PRIMARY, 2));

        // ===== LEFT SIDE =====
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel welcome = new JLabel("Welcome to AgroDirect");
        UIUtils.styleLabel(welcome, 22, true);

        JLabel subtitle = new JLabel("<html>Optimize your supply chain<br>using smart algorithms</html>");
        UIUtils.styleLabel(subtitle, 14, false);

        left.add(Box.createVerticalGlue());
        left.add(welcome);
        left.add(Box.createRigidArea(new Dimension(0, 10)));
        left.add(subtitle);
        left.add(Box.createVerticalGlue());

        left.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));
        left.setOpaque(false); // Make it transparent to show gradient

        // ===== RIGHT SIDE =====
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        JLabel loginLabel = new JLabel("USER LOGIN");
        UIUtils.styleLabel(loginLabel, 20, true);
        loginLabel.setForeground(UIUtils.NEON_PRIMARY);
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        UIUtils.styleLabel(userLabel, 12, false);
        JLabel passLabel = new JLabel("Password");
        UIUtils.styleLabel(passLabel, 12, false);

        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        usernameField.setMaximumSize(new Dimension(250, 35));
        passwordField.setMaximumSize(new Dimension(250, 35));

        JButton loginBtn = new JButton("LOGIN");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        UIUtils.styleButton(loginBtn);

        JButton registerBtn = new JButton("NEW USER? REGISTER");
        registerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        UIUtils.styleButton(registerBtn);
        registerBtn.setBackground(new Color(30, 30, 30));
        registerBtn.setForeground(UIUtils.NEON_PRIMARY);

        // ===== ADD COMPONENTS (CLEAN ORDER) =====
        right.add(Box.createVerticalGlue());

        right.add(loginLabel);
        right.add(Box.createRigidArea(new Dimension(0, 20)));

        right.add(userLabel);
        right.add(usernameField);

        right.add(Box.createRigidArea(new Dimension(0, 10)));

        right.add(passLabel);
        right.add(passwordField);

        right.add(Box.createRigidArea(new Dimension(0, 20)));

        right.add(loginBtn);
        right.add(Box.createRigidArea(new Dimension(0, 10)));
        right.add(registerBtn);

        right.add(Box.createVerticalGlue());

        // ===== LOGIN ACTION =====
        Runnable loginTask = () -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username and password.");
                return;
            }

            if (UserStore.authenticate(user, pass)) {
                new Dashboard();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials ❌");
            }
        };

        loginBtn.addActionListener(e -> loginTask.run());
        passwordField.addActionListener(e -> loginTask.run());
        usernameField.addActionListener(e -> loginTask.run());

        // ===== REGISTER ACTION =====
        registerBtn.addActionListener(e -> showRegisterDialog());

        // ===== ADD TO CARD =====
        card.add(left);
        card.add(right);

        mainPanel.add(card);

        add(mainPanel);
        setVisible(true);
    }

    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "Register New User", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        dialog.getContentPane().setBackground(new Color(20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Create Account");
        UIUtils.styleLabel(title, 16, true);
        title.setForeground(UIUtils.NEON_PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        dialog.add(title, gbc);

        gbc.gridwidth = 1;
        JLabel uLabel = new JLabel("Username:");
        UIUtils.styleLabel(uLabel, 12, false);
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(uLabel, gbc);

        JTextField newUser = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 1;
        dialog.add(newUser, gbc);

        JLabel pLabel = new JLabel("Password:");
        UIUtils.styleLabel(pLabel, 12, false);
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(pLabel, gbc);

        JPasswordField newPass = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        dialog.add(newPass, gbc);

        JButton submitBtn = new JButton("REGISTER");
        UIUtils.styleButton(submitBtn);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(submitBtn, gbc);

        submitBtn.addActionListener(e -> {
            String username = newUser.getText().trim();
            String password = new String(newPass.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username and password cannot be empty.");
                return;
            }
            if (UserStore.register(username, password)) {
                JOptionPane.showMessageDialog(dialog, "Account created successfully! ✅\nYou can now log in.");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Username already exists. Choose another.");
            }
        });

        dialog.setVisible(true);
    }
}