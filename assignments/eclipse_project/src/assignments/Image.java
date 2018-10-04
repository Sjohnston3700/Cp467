package assignments;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Image {
	protected static final int BLACK = 0;
	protected static final int WHITE = 255;
	protected static final int INVALID = -1;
	
	private int[] data;
	private int width, height;
	

	public Image(String fileLocation) {
		try {
			BufferedImage image = ImageIO.read(new File(fileLocation));
			
			width = image.getWidth();
			height = image.getHeight();
			data = new int[height * width];
			
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int rgb = image.getRGB(x,y);
				    assignValue(x, y, rgb);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Image(int width, int height) {
		this.width = width;
		this.height = height;
		this.data = new int[width * height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				assignValue(x, y, INVALID);
			}
		}
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getCoordinateValue(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return INVALID;
		
		return data[y * width + x];		
	}
	
	public void assignValue(int x, int y, int value) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IllegalArgumentException("Given coordinates are outside of bounds of image!");
		}
		
		data[y * width + x] = value;
	}
	
	public Image getGrayscaleImage() {
		Image gs = new Image(width, height);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
			    int p = getCoordinateValue(x, y);
			    
			    int r = (p>>16)&0xff;
			    int g = (p>>8)&0xff;
			    int b = p&0xff;
			    int grayScaleValue = (r + g + b) / 3;
			    gs.assignValue(x, y, grayScaleValue);
			}
	    }
		
		return gs;
	}
	
	public void saveGrayscaleToFile(String filename) {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int value = Math.max(Math.min(getCoordinateValue(x, y), WHITE), BLACK);
				int rgb = new Color(value, value, value).getRGB();
				newImage.setRGB(x, y, rgb);
			}
	    }
		
		try {
			ImageIO.write(newImage, "jpg", new File(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printImage() {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print(data[y * width + x] + " ");
			}
			System.out.println();
		}
	}
	
	/* ----- A1 FUNCTIONS ----- */
	
	public Image getFilteredImage(float[] operator) {
		Image filtered = new Image(width, height);
		int fillerValue = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int[] temp = getThreeByThreePixelBlock(x, y, fillerValue);
				int sum = 0;
				for (int i = 0; i < operator.length; i++) {
					sum += temp[i] * operator[i];
				}
				filtered.assignValue(x, y, sum);
			}
		}
		
		return filtered;
	}
	
	private int[] getThreeByThreePixelBlock(int x, int y, int fillerValue) {
		int[] temp = new int[9];
		
		int index = 0;
		for (int offsetX = -1; offsetX <= 1; offsetX++) {
			for (int offsetY = -1; offsetY <= 1; offsetY++) {
				int val = getCoordinateValue(x + offsetX, y + offsetY);
				if (val == INVALID)
					val = fillerValue;
				temp[index++] = val;				
			}
		}
		
		return temp;
	}
	
	/* ----- A2 FUNCTIONS ----- */
	
	public boolean isBackground(int x, int y) {
		return isBackground(x, y, 0);
	}
	
	public boolean isBackground(int x, int y, int tolerance) {
		return Math.abs(WHITE - data[y * width + x]) <= tolerance;		
	}
}
