import java.io.File;
import java.net.URL;
import javax.imageio.ImageIO;

public class Main {
  public static void main(final String args[]) throws Exception {
	// Sharpen
	float[] operator = { 0, -1, 0, -1, 5f, -1, 0, -1, 0 };
	
	// Blur
	//float[] operator = { 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
    Image image = new Image("cat.jpg");
    image.convoluteImage(operator, "image_convoluated.jpg");
  }
}