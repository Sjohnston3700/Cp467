package assignments;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.swing.*;
 
public class ImageJPanel extends JPanel {
    BufferedImage left, right;
    
    public ImageJPanel(String leftImageFilename, String rightImageFilename) throws IOException {
        left = ImageIO.read(new File(leftImageFilename));
        
        if (rightImageFilename != null) {
        	right = ImageIO.read(new File(rightImageFilename));
        }
    }
    
    public ImageJPanel(String leftImageFilename) throws IOException {
        this(leftImageFilename, null);
    }

    public void display() {
        JPanel panel = new JPanel();
        
        if (left != null) {
        	JLabel label = new JLabel(new ImageIcon(left));
            panel.add(label);
        }
        
        if (right != null) {
        	 JLabel label2 = new JLabel(new ImageIcon(right));
             panel.add(label2);
        }
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Altered Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     
        frame.add(panel); 
        frame.pack();
        frame.setVisible(true);
    }

}
