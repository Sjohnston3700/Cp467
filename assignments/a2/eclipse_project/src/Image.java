import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class Image {
	private BufferedImage image;
	private String fileName;
	private int[] grayScaleValues;
	private int[] computedGrayScales;
	private int width;
	private int height;
	public static final int BLACK = 0;
	public static final int WHITE = 255;

	public Image(String fileLocation) {
		try {
			image = ImageIO.read(new File(fileLocation));
			width = image.getWidth();
			height = image.getHeight();
			grayScaleValues = new int[height * width];
			computedGrayScales = new int[height * width];
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return image;
	}
	
	public void makeImageBlackAndWhite(String newFileName) {
		storeGrayValues();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int position = y * width + x;
				
				computedGrayScales[position] = grayScaleValues[position] > 150 ? 255 : 0;
			}
		}
		createNewConvolutedImage(newFileName);
	}
	
	private void createNewConvolutedImage(String newFileName) {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int value = computedGrayScales[y * width + x];
				value = value > 255 ?  255 : value;
				value = value < 0 ? 0 : value;
				int rgb = new Color(value, value, value).getRGB();
				newImage.setRGB(x, y, rgb);
			}
	    }
		try {
			ImageIO.write(newImage, "jpg", new File(newFileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ImageJPanel imageJpanel = new ImageJPanel(image, newImage);
		imageJpanel.displayImage();
	} 

	private void storeGrayValues() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
			    int p = image.getRGB(x,y);
			    int r = (p>>16)&0xff;
			    int g = (p>>8)&0xff;
			    int b = p&0xff;
			    int grayScaleValue = (r + g + b) / 3;
			    grayScaleValues[y * width + x] = grayScaleValue;
		
			}
	    }
	}
}
