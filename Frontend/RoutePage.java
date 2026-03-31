import javax.swing.*;
import java.awt.*;

public class RoutePage extends JFrame {

    JTextField startField, endField;
    JTextArea resultArea;

    public RoutePage(GraphPanel graphPanel) {

        setTitle("Shortest Route");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setPreferredSize(new Dimension(300, 600));

        left.add(new JLabel("Start Location:"));
        startField = new JTextField(15);
        left.add(startField);

        left.add(new JLabel("End Location:"));
        endField = new JTextField(15);
        left.add(endField);

        JButton findBtn = new JButton("Find Route");

        findBtn.addActionListener(e -> {
            resultArea.setText(
                    "From: " + startField.getText() +
                            "\nTo: " + endField.getText() +
                            "\n\nShortest path will be shown here.");
        });

        left.add(findBtn);

        add(left, BorderLayout.WEST);
        add(graphPanel, BorderLayout.CENTER);

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        add(new JScrollPane(resultArea), BorderLayout.EAST);

        setVisible(true);
    }
}