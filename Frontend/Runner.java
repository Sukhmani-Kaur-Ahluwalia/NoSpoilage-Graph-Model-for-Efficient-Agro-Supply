// Runner is an alternate entry point used for testing the registration portal
// i use this when i just want to test UserRegPortal without going through login
 
public class Runner {
    public static void main(String[] args) {
 
        // run on the event dispatch thread - swing requires this
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                System.out.println("launching registration portal...");
                UserRegPortal regWindow = new UserRegPortal();
                regWindow.setVisible(true);
            }
        });
    }
}