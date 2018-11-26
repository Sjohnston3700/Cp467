package assignments;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class Image {
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
		this(width, height, INVALID);
	}
		
	public Image(int width, int height, int defaultValue) {
		this.width = width;
		this.height = height;
		this.data = new int[width * height];
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				assignValue(x, y, defaultValue);
			}
		}
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	protected int getCoordinateValue(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return INVALID;
		
		return data[y * width + x];		
	}
	
	protected void assignValue(int x, int y, int value) {
		if (x < 0 || x >= width || y < 0 || y >= height) {
			throw new IllegalArgumentException("Given coordinates are outside of bounds of image!");
		}
		
		data[y * width + x] = value;
	}
	
	public void saveToFile(String filename) {
		BufferedImage newImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				newImage.setRGB(x, y, getCoordinateValue(x, y));
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
				System.out.printf("%03d ", getCoordinateValue(x, y));
			}
			System.out.println();
		}
	}
	
	/* ----- A1 FUNCTIONS ----- */
	
	protected int[] getThreeByThreePixelBlock(int x, int y, int fillerValue) {
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
	
	/* ----- A4 FUNCTIONS ----- */
	
	protected static class ImageBounds implements Comparable<ImageBounds> {
		private int minX, minY, maxX, maxY;
		
		public ImageBounds(int x, int y) {
			this(x, y, x, y);
		}

		public ImageBounds(int minX, int minY, int maxX, int maxY) {
			setMaxX(maxX);
			setMaxY(maxY);
			setMinX(minX);
			setMinY(minY);
		}
		
		public int getMinY() {
			return minY;
		}

		public void setMinY(int minY) {
			if (minY > maxY) {
				throw new IllegalArgumentException("minY cannot be greater than maxY!");
			}
			
			this.minY = minY;
		}

		public int getMaxY() {
			return maxY;
		}

		public void setMaxY(int maxY) {
			if (maxY < minY) {
				throw new IllegalArgumentException("minY cannot be greater than maxY!");
			}
			
			this.maxY = maxY;
		}

		public int getMinX() {
			return minX;
		}

		public void setMinX(int minX) {
			if (minX > maxX) {
				throw new IllegalArgumentException("minX cannot be greater than maxX!");
			}
			
			this.minX = minX;
		}

		public int getMaxX() {
			return maxX;
		}

		public void setMaxX(int maxX) {
			if (maxX < minX) {
				throw new IllegalArgumentException("minX cannot be greater than maxX!");
			}
			
			this.maxX = maxX;
		}
		
		public void addPoint(int x, int y) {			
			if (x > maxX)
				setMaxX(x);
			
			if (y > maxY)
				setMaxY(y);
			
			if (x < minX)
				setMinX(x);
			
			if (y < minY)
				setMinY(y);
		}
		
		public int getWidth() {
			return maxX - minX + 1;
		}
		
		public int getHeight() {
			return maxY - minY + 1;
		}
		
		public int getCenterX() {
			return minX + getWidth() / 2;
		}
		
		public int getCenterY() {
			return minY + getHeight() / 2;
		}
		
		public boolean intersectsX(ImageBounds other) {
			return other.minX < this.maxX && other.maxX > this.minX;
		}
		
		public boolean intersectsY(ImageBounds other) {
			return other.minY < this.maxY && other.maxY > this.minY;
		}
		
		public boolean intersects(ImageBounds other) {
			return this.intersectsX(other) && this.intersectsY(other);
		}
		
		@Override
		public String toString() {
			return String.format("(%d, %d -> %d, %d)", minX, minY, maxX, maxY);
		}
		
		@Override
		public int compareTo(ImageBounds other) {
			return DEFAULT.compare(this, other);
		}
		
		/**
		 * Sorts ImageBounds by occurrence as one would read top-down, left-to-right
		 */
		public static final Comparator<ImageBounds> TD_LTR = new Comparator<Image.ImageBounds>() {
			@Override
			public int compare(ImageBounds l, ImageBounds r) {
				// If bounds intersect or no vertical intersection, take highest
				if (l.intersects(r) || !l.intersectsY(r)) {
					return l.minY - r.minY;
				}
				
				// Otherwise, take leftmost
				return l.minX - r.minX;
			}
		};
		
		public static final Comparator<ImageBounds> DEFAULT = TD_LTR;
	}
	
	protected void clearArea(ImageBounds b) {
		assignArea(b, INVALID);
	}
	
	protected void assignArea(ImageBounds b, int value) {
		for (int y = Math.max(b.getMinY(), 0); y < Math.min(b.getMaxY() + 1, getHeight()); y++) {
			for (int x = Math.max(b.getMinX(), 0); x < Math.min(b.getMaxX() + 1, getWidth()); x++) {
				assignValue(x, y, value);
			}
		}
	}
	
	protected void insertImage(Image img, int originX, int originY) {
		for (int y = 0; y < img.getHeight() && y + originY < getHeight(); y++) {
			for (int x = 0; x < img.getWidth() && x + originX < getWidth(); x++) {
				assignValue(x + originX, y + originY, img.getCoordinateValue(x, y));
			}
		}
	}
}
