// UIUtils has all the shared styling stuff used across all the screens
// instead of copying the same button style code everywhere, i put it here
 
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
 
public class UIUtils {
 
    // color constants for the neon theme used across all screens
      public static final Color NEON_PRIMARY   = new Color(0, 255, 255);   // neon cyan - used for borders/highlights
    public static final Color NEON_START     = new Color(18, 18, 18);    // dark base background
    
    public static final Color NEON_SECONDARY = new Color(255, 0, 255);   // neon magenta - used for labels
public static final Color NEON_END       = new Color(45, 45, 45);    // slightly lighter dark
 
    // these are the three gradient colors for the main background
    public static final Color SOFT_NEON_MID   = new Color(255, 0, 255);  // vibrant pink
    public static final Color SOFT_NEON_START = new Color(122, 0, 255);  // deep purple
    public static final Color SOFT_NEON_END   = new Color(0, 219, 255);  // electric blue
 
    // creates a panel with the purple-to-blue gradient background
    // used as the main background on most screens
    public static JPanel createGradientPanel() {
 
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
 
                // set up 3 color stops at 0%, 50%, 100% of the panel
Color[] gradientColors = { SOFT_NEON_START, SOFT_NEON_MID, SOFT_NEON_END };
                float[] colorStops = { 0.0f, 0.5f, 1.0f };
                
 
                LinearGradientPaint lgp = new LinearGradientPaint(
                        0, 0, getWidth(), getHeight(),
                        colorStops,
                        gradientColors);
 
                g2.setPaint(lgp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
 
        return gradientPanel;
    }
 
    // creates a semi-transparent dark panel - used for cards and overlays
    // alpha controls how transparent it is (0 = invisible, 255 = fully opaque)
    public static JPanel createDarkPanel(int alpha) {
        JPanel darkPanel = new JPanel();
        darkPanel.setBackground(new Color(20, 20, 30, alpha));
 
        // only set opaque if fully solid, otherwise transparency won't show through
        boolean isFullyOpaque = false;
        if (alpha == 255) {
            isFullyOpaque = true;
        }
        darkPanel.setOpaque(isFullyOpaque);
 
        return darkPanel;
    }
 
    // applies the neon cyan glow look to any button
    // also adds hover effects so it changes color when mouse is over it
    public static void styleButton(JButton button) {
button.setBackground(new Color(255, 255, 255, 40));
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
 button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setOpaque(false);
       
        button.setBorder(BorderFactory.createLineBorder(NEON_PRIMARY, 2));
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        
 
        // change colors when mouse hovers over the button
        button.addMouseListener(new MouseAdapter() {
 
            @Override
            public void mouseEntered(MouseEvent e) {
                // fill with semi-transparent cyan on hover
                int r = NEON_PRIMARY.getRed();
                int b = NEON_PRIMARY.getBlue();
                   int g = NEON_PRIMARY.getGreen();
                button.setBackground(new Color(r, g, b, 60));
  button.setContentAreaFilled(true);
                button.setBorder(BorderFactory.createLineBorder(NEON_SECONDARY, 2));
              
            }
 
           
            public void mouseExited(MouseEvent e) {
                // go back to the default look when mouse leaves
                 button.setBorder(BorderFactory.createLineBorder(NEON_PRIMARY, 2));
                button.setBackground(new Color(255, 255, 255, 40));
               
                button.setContentAreaFilled(false);
            }
        });
    }
 
    // applies white neon font styling to a label
    // fontSize and bold are passed in so we can reuse this for different labels
    public static void styleLabel(JLabel label, int fontSize, boolean bold) {
        label.setForeground(Color.WHITE);
 
        int fontStyle = Font.PLAIN;
        if (bold == true) {
            fontStyle = Font.BOLD;
        }
 
        label.setFont(new Font("Segoe UI", fontStyle, fontSize));
    }
}