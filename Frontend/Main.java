// this is the entry point for our NoSpoilage project
// it just sets the look and feel and opens the login screen
 
import javax.swing.*;
 
public class Main {
    public static void main(String[] args) {
 
        // try to use the system's native look and feel so it looks less like default Java
        try {
            String systemLook = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(systemLook);
        } catch (Exception e) {
            // if it fails, just use the default - not a big deal
            // System.out.println("could not set system look and feel");
        }
 
        // open the login screen to start the app
        LoginPage loginScreen = new LoginPage();
    }
}