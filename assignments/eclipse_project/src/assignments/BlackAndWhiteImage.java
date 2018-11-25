package assignments;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BlackAndWhiteImage extends GreyscaleImage {
	protected static final int WHITE_TOLERANCE = 150;
	
	public BlackAndWhiteImage(int width, int height) {
		super(width, height);
	}
	
	public BlackAndWhiteImage(Image orig) {
		this(new GreyscaleImage(orig));
	}
	
	public BlackAndWhiteImage(GreyscaleImage orig) {
		super(orig.getWidth(), orig.getHeight());
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int value = orig.isBackground(x, y, WHITE_TOLERANCE) ? WHITE : BLACK;
				assignValue(x, y, value);
			}
		}
	}
	
	@Override
	protected void assignValue(int x, int y, int value) {
		super.assignValue(x, y, value == BLACK ? BLACK : WHITE);
	}
	
	protected boolean isBlack(int x, int y) {
		return getCoordinateValue(x, y) == BLACK;
	}
	
	protected boolean isWhite(int x, int y) {
		return !isBlack(x, y);
	}
	
	@Override
	public BlackAndWhiteImage getSubImage(ImageBounds b) {
		return new BlackAndWhiteImage(super.getSubImage(b));
	}
	
	@Override
	public void printImage() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				System.out.printf("%d ", getCoordinateValue(x, y) == BLACK ? 1 : 0);
			}
			System.out.println();
		}
	}
	
	/* ----- A5 FUNCTIONS ----- */
	
	public List<BlackAndWhiteImage> splitImage(int nSegmentsX, int nSegmentsY) {
		List<BlackAndWhiteImage> subImages = new LinkedList<BlackAndWhiteImage>();
		
		int zoneWidth = getWidth() / nSegmentsX;
		int zoneHeight = getHeight() / nSegmentsY;
		
		for (int y = 0; y < nSegmentsY; y++) {
			for (int x = 0; x < nSegmentsX; x++) {
				int minX = zoneWidth * x;
				int maxX = x == nSegmentsX - 1 ? getWidth() : zoneWidth * (x + 1);
				int minY = zoneHeight * y;
				int maxY = y == nSegmentsY - 1 ? getHeight() : zoneHeight * (y + 1);
				
				subImages.add(getSubImage(new ImageBounds(minX, minY, maxX, maxY)));
			}
		}
		
		return subImages;
	}
	
	/**
	 * Calculates ratio of black to white pixels
	 */
	public float calculateZoningFeature() {
		int blackCount = 0;
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (isBlack(x, y)) {
					blackCount++;
				}
			}
		}
		
		return (float)blackCount / (getWidth() * getHeight());
	}
	
	/* ----- A8 FUNCTIONS ----- */
	
	/**
	 * Thin image according to Z-S algorithm
	 */
	public void thin() {
		List<Pixel> pixelsToChange = new ArrayList<Pixel>();
		boolean pixelsChanged = true;
		
		//continue apply Z-S steps to image until no pixels change in an iteration
		while (pixelsChanged) {
			pixelsChanged = false;
			
			//Z-S step 1 iteration
			for (int y = 0; y < getHeight(); y++) {
				for (int x = 0; x < getWidth(); x++) {
					if (fulfillsZS1(x, y)) pixelsToChange.add(new Pixel(x, y));
				}
			}
			
			if (pixelsToChange.size() > 0) {
				changePixels(pixelsToChange, WHITE);
				pixelsToChange.clear();
				pixelsChanged = true;
			}
			
			//Z-S step 2 iteration
			for (int y = 0; y < getHeight(); y++) {
				for (int x = 0; x < getWidth(); x++) {
					if (fulfillsZS2(x, y)) pixelsToChange.add(new Pixel(x, y));
				}
			}
			
			if (pixelsToChange.size() > 0) {
				changePixels(pixelsToChange, WHITE);
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

	private void changePixels(List<Pixel> pixelsToChange, int newValue) {
		for (Pixel pixel: pixelsToChange) {
			assignValue(pixel.x, pixel.y, newValue);
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
