package assignments;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GreyscaleImage extends Image {
	protected static final int BLACK = 0;
	protected static final int WHITE = 255;	
	
	public GreyscaleImage(int width, int height) {
		super(width, height);
	}
	
	public GreyscaleImage(Image orig) {
		super(orig.getWidth(), orig.getHeight());
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
			    int p = orig.getCoordinateValue(x, y);
			    
			    int r = (p>>16)&0xff;
			    int g = (p>>8)&0xff;
			    int b = p&0xff;
			    int grayScaleValue = (r + g + b) / 3;
			    
			    assignValue(x, y, grayScaleValue);
			}
	    }
	}
	
	public GreyscaleImage(GreyscaleImage orig) {
		super(orig.getWidth(), orig.getHeight());
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
			    assignValue(x, y, orig.getCoordinateValue(x, y));
			}
	    }
	}
	
	@Override
	protected void assignValue(int x, int y, int value) {
		super.assignValue(x, y, Math.max(Math.min(value, WHITE), BLACK));
	}
	
	@Override
	public void saveToFile(String filename) {
		BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int value = getCoordinateValue(x, y);
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
	
	/* ----- A1 FUNCTIONS ----- */
	
	public GreyscaleImage getFilteredImage(float[] operator) {
		GreyscaleImage filtered = new GreyscaleImage(getWidth(), getHeight());
		int fillerValue = 0;
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
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
	
	/* ----- A2 FUNCTIONS ----- */
	
	public boolean isBackground(int x, int y) {
		return isBackground(x, y, 0);
	}
	
	public boolean isBackground(int x, int y, int tolerance) {
		return Math.abs(WHITE - getCoordinateValue(x, y)) <= tolerance;		
	}
	
	/* ----- A4 FUNCTIONS ----- */
	
	public GreyscaleImage getSubImage(ImageBounds b) {
		GreyscaleImage sub = new GreyscaleImage(b.getWidth(), b.getHeight());
		
		for (int y = 0; y < b.getHeight(); y++) {
			for (int x = 0; x < b.getWidth(); x++) {
				sub.assignValue(x, y, getCoordinateValue(b.getMinX() + x, b.getMinY() + y));
			}
		}
		
		return sub;
	}
	
	public GreyscaleImage getScaledImage(float scaleFactor) {
		if (scaleFactor >= 1 || scaleFactor <= 0)
			throw new IllegalArgumentException("Only positive scale factors less than 1 are supported.");
		
		Image scaled = new Image((int)Math.ceil(getWidth() * scaleFactor), 
			(int)Math.ceil(getHeight() * scaleFactor));
		Image counts = new Image(scaled.getWidth(), scaled.getHeight());
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int val = getCoordinateValue(x, y);
				
				int mappedX = (int)(x * scaleFactor);
				int mappedY = (int)(y * scaleFactor);
				int mappedVal = scaled.getCoordinateValue(mappedX, mappedY);
				int count = counts.getCoordinateValue(mappedX, mappedY);
				if (mappedVal == INVALID) {
					mappedVal = 0;
					count = 0;
				}
				
				scaled.assignValue(mappedX, mappedY, mappedVal + val);
				counts.assignValue(mappedX, mappedY, count + 1);
			}
		}
		
		for (int y = 0; y < scaled.getHeight(); y++) {
			for (int x = 0; x < scaled.getWidth(); x++) {
				int val = scaled.getCoordinateValue(x, y);
				int count = counts.getCoordinateValue(x, y);
				
				if (val == INVALID) {
					scaled.assignValue(x, y, WHITE);
				} else {
					scaled.assignValue(x, y, val / count);
				}
			}
		}
		
		GreyscaleImage result = new GreyscaleImage(scaled.getWidth(), scaled.getHeight());
		
		for (int y = 0; y < scaled.getHeight(); y++) {
			for (int x = 0; x < scaled.getWidth(); x++) {
				result.assignValue(x, y, scaled.getCoordinateValue(x, y));
			}
		}
		
		return result;
	}
}