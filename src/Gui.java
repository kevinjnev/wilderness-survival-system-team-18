import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.FlowLayout;

public class Gui {
    public void showImage(BufferedImage image) {
        JFrame frame = new JFrame("JPanel Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 1200);
    // 1200 x 1200

        // 2. Create a JPanel
        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY); // Set background color
        panel.setLayout(new FlowLayout()); 
        panel.add(new JLabel(new ImageIcon(image)));
        frame.add(panel);
        frame.setVisible(true);
    }
}
