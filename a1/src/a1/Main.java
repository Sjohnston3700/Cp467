package a1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;

public class Main {
  public static void main(final String args[]) throws Exception {
	// Operator for sharpening
	float[] operator = { 0, -1, 0, -1, 5f, -1, 0, -1, 0 };
    Image image = new Image("cat.jpg");
    image.convoluteImage(operator, "image_convoluated.jpg");
  }
}