package Frontend;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIUtils {

    // Neon Gradient Colors
    public static final Color NEON_START = new Color(18, 18, 18); // Dark Base
    public static final Color NEON_END = new Color(45, 45, 45); // Darker Base
    public static final Color NEON_PRIMARY = new Color(0, 255, 255); // Neon Cyan
    public static final Color NEON_SECONDARY = new Color(255, 0, 255); // Neon Magenta

    // Soft Neon Gradient Colors (Updated for extreme vibrancy)
    public static final Color SOFT_NEON_START = new Color(122, 0, 255); // Deep Purple
    public static final Color SOFT_NEON_MID = new Color(255, 0, 255);   // Vibrant Pink
    public static final Color SOFT_NEON_END = new Color(0, 219, 255);   // Electric Blue

    /**
     * Creates a JPanel with a neon gradient background.
     */
    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Create a 3-color gradient
                LinearGradientPaint lgp = new LinearGradientPaint(
                        0, 0, getWidth(), getHeight(),
                        new float[]{0.0f, 0.5f, 1.0f},
                        new Color[]{SOFT_NEON_START, SOFT_NEON_MID, SOFT_NEON_END});

                g2.setPaint(lgp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    /**
     * Creates a semi-transparent dark panel.
     */
    public static JPanel createDarkPanel(int alpha) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(20, 20, 30, alpha));
        panel.setOpaque(alpha == 255);
        return panel;
    }

    /**
     * Applies neon styling and hover effects to a button.
     */
    public static void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(255, 255, 255, 40)); // Semi-transparent white
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(NEON_PRIMARY, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);
        button.setContentAreaFilled(false);

        // Hover Effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(NEON_PRIMARY.getRed(), NEON_PRIMARY.getGreen(), NEON_PRIMARY.getBlue(), 60));
                button.setBorder(BorderFactory.createLineBorder(NEON_SECONDARY, 2));
                button.setContentAreaFilled(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 255, 255, 40));
                button.setBorder(BorderFactory.createLineBorder(NEON_PRIMARY, 2));
                button.setContentAreaFilled(false);
            }
        });
    }

    /**
     * Styles a label for neon look.
     */
    public static void styleLabel(JLabel label, int fontSize, boolean bold) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, fontSize));
    }
}
