package a1;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
 
public class ImageJPanel extends JPanel {
    BufferedImage original;
    BufferedImage altered;
    Dimension size = new Dimension();
 
    public ImageJPanel(BufferedImage originalImage, BufferedImage altertedImage) {
        original = originalImage;
        altered = altertedImage;
    }

    protected void displayImage() {
        JPanel panel = new JPanel();
        
        JLabel label = new JLabel(new ImageIcon(original));
        panel.add(label);
        
        JLabel label2 = new JLabel(new ImageIcon(altered));
        panel.add(label2);
     
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Altered Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
        frame.add(panel); 
        frame.pack();
        frame.setVisible(true);
    }

}
