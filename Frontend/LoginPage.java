// LoginPage.java - the login screen for the NoSpoilage delivery app
// shows username and password fields, lets new users register too

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPage extends JFrame {

    // fields for user input
    JTextField usernameField;
    JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login - NoSpoilage Delivery System");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create the main background panel with gradient
        JPanel mainBackgroundPanel = UIUtils.createGradientPanel();
        mainBackgroundPanel.setLayout(new GridBagLayout()); // centers the card

        // card panel holds everything - split into left and right halves
        JPanel loginCard = UIUtils.createDarkPanel(180);
        loginCard.setPreferredSize(new Dimension(800, 450));
        loginCard.setLayout(new GridLayout(1, 2));
        loginCard.setBorder(BorderFactory.createLineBorder(UIUtils.NEON_PRIMARY, 2));

        // ===== LEFT SIDE - branding / welcome message =====
        JPanel leftBrandingPanel = new JPanel();
        leftBrandingPanel.setLayout(new BoxLayout(leftBrandingPanel, BoxLayout.Y_AXIS));
        leftBrandingPanel.setOpaque(false);
        leftBrandingPanel.setBorder(BorderFactory.createEmptyBorder(50, 30, 50, 30));

        JLabel appNameLabel = new JLabel("Welcome to NoSpoilage");
        UIUtils.styleLabel(appNameLabel, 22, true);

        JLabel appDescriptionLabel = new JLabel("<html>Optimize your delivery supply chain<br>using smart graph algorithms</html>");
        UIUtils.styleLabel(appDescriptionLabel, 14, false);

        leftBrandingPanel.add(Box.createVerticalGlue());
        leftBrandingPanel.add(appNameLabel);
        leftBrandingPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftBrandingPanel.add(appDescriptionLabel);
        leftBrandingPanel.add(Box.createVerticalGlue());

        // ===== RIGHT SIDE - login form =====
        JPanel rightLoginPanel = new JPanel();
        rightLoginPanel.setLayout(new BoxLayout(rightLoginPanel, BoxLayout.Y_AXIS));
        rightLoginPanel.setOpaque(false);

        JLabel loginTitleLabel = new JLabel("USER LOGIN");
        UIUtils.styleLabel(loginTitleLabel, 20, true);
        loginTitleLabel.setForeground(UIUtils.NEON_PRIMARY);
        loginTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel usernameLabel = new JLabel("Username");
        UIUtils.styleLabel(usernameLabel, 12, false);
        usernameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password");
        UIUtils.styleLabel(passwordLabel, 12, false);
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // create the input fields
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        usernameField.setMaximumSize(new Dimension(250, 35));
        passwordField.setMaximumSize(new Dimension(250, 35));

        JButton loginButton = new JButton("LOGIN");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        UIUtils.styleButton(loginButton);

        JButton registerButton = new JButton("NEW USER? REGISTER");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        UIUtils.styleButton(registerButton);
        registerButton.setBackground(new Color(30, 30, 30));
        registerButton.setForeground(UIUtils.NEON_PRIMARY);

        // add components to the right panel
        rightLoginPanel.add(Box.createVerticalGlue());

        rightLoginPanel.add(loginTitleLabel);
        rightLoginPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        rightLoginPanel.add(usernameLabel);
        rightLoginPanel.add(usernameField);
        rightLoginPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        rightLoginPanel.add(passwordLabel);
        rightLoginPanel.add(passwordField);
        rightLoginPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        rightLoginPanel.add(loginButton);
        rightLoginPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        rightLoginPanel.add(registerButton);

        rightLoginPanel.add(Box.createVerticalGlue());

        // ===== LOGIN LOGIC =====
        // this runs when the user clicks login or presses Enter
        Runnable loginAction = new Runnable() {
            @Override
            public void run() {
                String enteredUsername = usernameField.getText().trim();
                // get password as char array then convert to string
                String enteredPassword = new String(passwordField.getPassword()).trim();

                // check that both fields are filled
                if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Please enter both username and password.");
                    return;
                }

                // check credentials against stored users
                boolean loginSuccess = UserStore.authenticate(enteredUsername, enteredPassword);

                if (loginSuccess) {
                    // open dashboard and close login page
                    new Dashboard();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginPage.this, "Invalid credentials. Please try again.");
                }
            }
        };

        // attach login action to button and Enter key on both fields
        loginButton.addActionListener(e -> loginAction.run());
        passwordField.addActionListener(e -> loginAction.run());
        usernameField.addActionListener(e -> loginAction.run());

        // register button opens the registration dialog
        registerButton.addActionListener(e -> showRegisterDialog());

        // assemble the card
        loginCard.add(leftBrandingPanel);
        loginCard.add(rightLoginPanel);

        mainBackgroundPanel.add(loginCard);
        add(mainBackgroundPanel);
        setVisible(true);
    }

    // shows a popup dialog for creating a new user account
    private void showRegisterDialog() {
        JDialog registerDialog = new JDialog(this, "Register New Account", true);
        registerDialog.setSize(350, 250);
        registerDialog.setLocationRelativeTo(this);
        registerDialog.setLayout(new GridBagLayout());
        registerDialog.getContentPane().setBackground(new Color(20, 20, 20));

        GridBagConstraints layoutConstraints = new GridBagConstraints();
        layoutConstraints.insets = new Insets(8, 10, 8, 10);
        layoutConstraints.fill = GridBagConstraints.HORIZONTAL;

        // dialog title
        JLabel dialogTitle = new JLabel("Create New Account");
        UIUtils.styleLabel(dialogTitle, 16, true);
        dialogTitle.setForeground(UIUtils.NEON_PRIMARY);
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 0;
        layoutConstraints.gridwidth = 2;
        registerDialog.add(dialogTitle, layoutConstraints);

        // username row
        layoutConstraints.gridwidth = 1;
        JLabel newUsernameLabel = new JLabel("Username:");
        UIUtils.styleLabel(newUsernameLabel, 12, false);
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 1;
        registerDialog.add(newUsernameLabel, layoutConstraints);

        JTextField newUsernameField = new JTextField(15);
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 1;
        registerDialog.add(newUsernameField, layoutConstraints);

        // password row
        JLabel newPasswordLabel = new JLabel("Password:");
        UIUtils.styleLabel(newPasswordLabel, 12, false);
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 2;
        registerDialog.add(newPasswordLabel, layoutConstraints);

        JPasswordField newPasswordField = new JPasswordField(15);
        layoutConstraints.gridx = 1;
        layoutConstraints.gridy = 2;
        registerDialog.add(newPasswordField, layoutConstraints);

        // register submit button
        JButton submitRegistrationButton = new JButton("CREATE ACCOUNT");
        UIUtils.styleButton(submitRegistrationButton);
        layoutConstraints.gridx = 0;
        layoutConstraints.gridy = 3;
        layoutConstraints.gridwidth = 2;
        registerDialog.add(submitRegistrationButton, layoutConstraints);

        submitRegistrationButton.addActionListener(e -> {
            String newUsername = newUsernameField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();

            // both fields must be filled
            if (newUsername.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(registerDialog, "Username and password cannot be empty.");
                return;
            }

            // try to register the new user
            boolean registerSuccess = UserStore.register(newUsername, newPassword);

            if (registerSuccess) {
                JOptionPane.showMessageDialog(registerDialog, "Account created successfully!\nYou can now log in with your new credentials.");
                registerDialog.dispose();
            } else {
                // username already taken
                JOptionPane.showMessageDialog(registerDialog, "That username already exists. Please choose a different one.");
            }
        });

        registerDialog.setVisible(true);
    }
}
