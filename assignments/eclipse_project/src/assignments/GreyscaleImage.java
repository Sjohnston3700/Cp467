package assignments;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class GreyscaleImage extends Image {
	
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
	
	@Override
	public void saveToFile(String filename) {
		BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
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
	
	public void convertToBW() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int value = (isBackground(x, y, WHITE_TOLERANCE)) ? WHITE : BLACK;
				assignValue(x, y, value);
			}
	    }
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
		
		GreyscaleImage scaled = new GreyscaleImage((int)Math.ceil(getWidth() * scaleFactor), 
			(int)Math.ceil(getHeight() * scaleFactor));
		GreyscaleImage counts = new GreyscaleImage(scaled.getWidth(), scaled.getHeight());
		
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
		
		return scaled;
	}
	
	/* ----- A5 FUNCTIONS ----- */
	
	//assumes b&w
	//calculates ratio of black to white pixels
	public float calculateZoningFeature(float xStart, float xEnd, float yStart, float yEnd) {
		float blackSum = 0;
		int xStartFloor = (int) Math.floor(xStart);
		int xStartCeil = (int) Math.ceil(xStart);
		int xEndFloor = (int) Math.floor(xEnd);
		int xEndCeil = (int) Math.ceil(xEnd);
		int yStartFloor = (int) Math.floor(yStart);
		int yStartCeil = (int) Math.ceil(yStart);
		int yEndFloor = (int) Math.floor(yEnd);
		int yEndCeil = (int) Math.ceil(yEnd);
		
		//calculate sum of left fraction
		float xStartRemainder = xStartCeil - xStart;
		if (xStartRemainder != 0) {
			for (int y = yStartCeil; y < yEndFloor; y++) {
				blackSum += (getCoordinateValue(xStartFloor, y) == 0) ? xStartRemainder : 0;
			}
		}
		
		//calculate sum of right fraction
		float xEndRemainder = xEnd - xEndFloor;
		if (xEndRemainder != 0) {
			for (int y = yStartCeil; y < yEndFloor; y++) {
				blackSum += (getCoordinateValue(xEndFloor, y) == 0) ? xEndRemainder : 0;
			}
		}
		
		//calculate sum of top fraction
		float yStartRemainder = yStartCeil - yStart;
		if (yStartRemainder != 0) {
			for (int x = xStartCeil; x < xEndFloor; x++) {
				blackSum += (getCoordinateValue(x, yStartFloor) == 0) ? yStartRemainder : 0;
			}
		}
		
		//calculate sum of bottom fraction
		float yEndRemainder = yEnd - yEndFloor;
		if (yEndRemainder != 0) {
			for (int x = xStartCeil; x < xEndFloor; x++) {
				blackSum += (getCoordinateValue(x, yEndFloor) == 0) ? yEndRemainder : 0;
			}
		}
		
		//add top left fraction
		if (xStartRemainder != 0 && yStartRemainder != 0) {
			blackSum += (getCoordinateValue(xStartFloor, yStartFloor) == 0) ? yStartRemainder * xStartRemainder : 0;
		}
		
		//add top right fraction
		if (xEndRemainder != 0 && yStartRemainder != 0) {
			blackSum += (getCoordinateValue(xEndCeil, yStartFloor) == 0) ? yStartRemainder * xEndRemainder : 0;
		}
		
		//add bottom left fraction
		if (xStartRemainder != 0 && yEndRemainder != 0) {
			blackSum += (getCoordinateValue(xStartFloor, yEndCeil) == 0) ? yEndRemainder * xStartRemainder : 0;
		}
		
		//add bottom right fraction
		if (xEndRemainder != 0 && yEndRemainder != 0) {
			blackSum += (getCoordinateValue(xEndCeil, yEndCeil) == 0) ? yEndRemainder * xEndRemainder : 0;
		}
		
		//calculate sum of center part
		for (int y = yStartCeil; y < yEndFloor; y++) {
			for (int x = xStartCeil; x < xEndFloor; x++) {
				blackSum += (getCoordinateValue(x, y) == 0) ? 1: 0;
			}
		}
		
		float area = (xEnd - xStart) * (yEnd - yStart);
		
		return blackSum/area;
	}
	
	/* ----- A8 FUNCTIONS ----- */
	
	//assumes b&w
	//thin image according to Z-S algorithm
	public void thin() {
		List<Pixel> pixelsToChange = new ArrayList<Pixel>();
		boolean pixelsChanged = true;
		
		//continue apply Z-S steps to image until no pixels change in an iteration
		while (pixelsChanged) {
			//Z-S step 1 iteration
			for (int y = 0; y < getHeight(); y++) {
				for (int x = 0; x < getWidth(); x++) {
					if (fulfillsZS1(x, y)) pixelsToChange.add(new Pixel(x, y));
				}
			}
			if (pixelsToChange.size() > 0) {
				changePixels(pixelsToChange);
				pixelsToChange.clear();
			} else {
				pixelsChanged = false;
			}
			
			//Z-S step 2 iteration
			for (int y = 0; y < getHeight(); y++) {
				for (int x = 0; x < getWidth(); x++) {
					if (fulfillsZS2(x, y)) pixelsToChange.add(new Pixel(x, y));
				}
			}
			if (pixelsToChange.size() > 0) {
				changePixels(pixelsToChange);
				pixelsToChange.clear();
				pixelsChanged = true;
			} 
		}		
	}
	
	private boolean fulfillsZS1(int x, int y) {
		if (!fulfillsZSSteps1To3(x, y)) return false;
		
		boolean isWhiteP2 = isWhite(x, y - 1);
		boolean isWhiteP4 = isWhite(x + 1, y);
		boolean isWhiteP6 = isWhite(x, y + 1);
		boolean isWhiteP8 = isWhite(x - 1, y);
		
		//condition 4
		if (!isWhiteP2 && !isWhiteP4 && !isWhiteP6) return false;
		
		//condition 5
		if (!isWhiteP4 && !isWhiteP6 && !isWhiteP8) return false;
		
		//passed all checks
		return true;
	}
	
	private boolean fulfillsZS2(int x, int y) {
		if (!fulfillsZSSteps1To3(x, y)) return false;
		
		boolean isWhiteP2 = isWhite(x, y - 1);
		boolean isWhiteP4 = isWhite(x + 1, y);
		boolean isWhiteP6 = isWhite(x, y + 1);;
		boolean isWhiteP8 = isWhite(x - 1, y);		
		
		//condition 4
		if (!isWhiteP2 && !isWhiteP4 && !isWhiteP8) return false;
		
		//condition 5
		if (!isWhiteP2 && !isWhiteP6 && !isWhiteP8) return false;
		
		//passed all checks
		return true;
	}
	
	private boolean fulfillsZSSteps1To3(int x, int y) {
		//condition 1
		if (isWhite(x, y)) return false;
		
		boolean isWhiteP2 = isWhite(x, y - 1);
		boolean isWhiteP3 = isWhite(x + 1, y - 1);
		boolean isWhiteP4 = isWhite(x + 1, y);
		boolean isWhiteP5 = isWhite(x + 1, y + 1);
		boolean isWhiteP6 = isWhite(x, y + 1);
		boolean isWhiteP7 = isWhite(x - 1, y + 1);
		boolean isWhiteP8 = isWhite(x - 1, y);
		boolean isWhiteP9 = isWhite(x - 1, y - 1);
		
		//condition 2 start
		int blackNeighbourCount = 0;
		
		if (isWhiteP2) blackNeighbourCount++;
		if (isWhiteP3) blackNeighbourCount++;
		if (isWhiteP4) blackNeighbourCount++;		
		if (isWhiteP5) blackNeighbourCount++;
		if (isWhiteP6) blackNeighbourCount++;
		if (isWhiteP7) blackNeighbourCount++;
		if (isWhiteP8) blackNeighbourCount++;
		if (isWhiteP9) blackNeighbourCount++;
		
		//condition 2 check
		if (blackNeighbourCount < 2 || blackNeighbourCount > 6) return false;
		
		//condition 3 start
		int transitionCount = 0;
		
		if (isWhiteP2) {
			if (!isWhiteP3) transitionCount++; 
		}
		if (isWhiteP3) {
			if (!isWhiteP4) transitionCount++;
		}
		if (isWhiteP4) {
			if (!isWhiteP5) transitionCount++;
		}
		if (isWhiteP5) {
			if (!isWhiteP6) transitionCount++;
		}
		if (isWhiteP6) {
			if (!isWhiteP7) transitionCount++;
		}
		if (isWhiteP7) {
			if (!isWhiteP8) transitionCount++;
		}
		if (isWhiteP8) {
			if (!isWhiteP9) transitionCount++;
		}
		if (isWhiteP9) {
			if (!isWhiteP2) transitionCount++;
		}
		
		//condition 3
		if (transitionCount != 1) return false;
		
		//passed conditions 1 - 3
		return true;
	}
		
	//out-of-bounds values will return true
	private boolean isWhite(int x, int y) {
		int greyLevel = getCoordinateValue(x, y);
		
		if (greyLevel == INVALID || greyLevel == WHITE) return true;
		
		return false;
	}
	
	private void changePixels(List<Pixel> pixelsToChange) {
		for (Pixel pixel: pixelsToChange) {
			assignValue(pixel.x, pixel.y, WHITE);
		}
	}
	
	private class Pixel {
		int x, y;
		
		Pixel(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}