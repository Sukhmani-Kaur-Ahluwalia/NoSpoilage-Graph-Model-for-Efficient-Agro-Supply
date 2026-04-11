package Frontend;

import Frontend.LoginPage;
import Frontend.Dashboard;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        // Makes UI look modern (important)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        new LoginPage();
    }
}