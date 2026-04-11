package Frontend;

import javax.swing.*;
import java.awt.*;

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

        JLabel hint = new JLabel("(Hint: admin / 1234)");
        UIUtils.styleLabel(hint, 10, false);
        hint.setForeground(Color.GRAY);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        right.add(Box.createRigidArea(new Dimension(0, 10)));
        right.add(hint);

        right.add(Box.createVerticalGlue());

        // ===== LOGIN ACTION =====
        Runnable loginTask = () -> {
            String user = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (user.equals("admin") && pass.equals("1234")) {
                new Dashboard();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Credentials ❌");
            }
        };

        loginBtn.addActionListener(e -> loginTask.run());

        // Allow Enter key to trigger login
        passwordField.addActionListener(e -> loginTask.run());
        usernameField.addActionListener(e -> loginTask.run());

        // ===== ADD TO CARD =====
        card.add(left);
        card.add(right);

        mainPanel.add(card);

        add(mainPanel);
        setVisible(true);
    }
}