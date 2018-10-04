package assignments;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

public class Image {
	private BufferedImage image;
	private String fileName;
	private int[] originalImage;
	private int[] altereredImage;
	private int width;
	private int height;
	private int operatorSize = 9;
	public static final int BLACK = 0;
	public static final int WHITE = 255;
	private final int INVALID = -1;

	public Image(String fileLocation) {
		try {
			image = ImageIO.read(new File(fileLocation));
			width = image.getWidth();
			height = image.getHeight();
			originalImage = new int[height * width];
			altereredImage = new int[height * width];
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BufferedImage getImage() {
		return image;
	}
	
	private int[] getThreeByThreePixelBlock(int[] image, int x, int y, int fillerValue) {
		int[] temp = new int[9];
		
		int index = 0;
		for (int offsetX = -1; offsetX <= 1; offsetX++) {
			for (int offsetY = -1; offsetY <= 1; offsetY++) {
				int val = getCoordinateValue(image, x + offsetX, y + offsetY);
				if (val == INVALID)
					val = fillerValue;
				temp[index++] = val;				
			}
		}
		
		return temp;
	}
	
	private int getCoordinateValue(int[] image, int x, int y)
	{
		if (x < 0 || x >= width || y < 0 || y >= height)
			return INVALID;
		
		return image[y * width + x];		
	}
	
	private int[] getProcessedNeighboringValues(int[] image, int x, int y)
	{
		int[] values = new int[4];
		
		for (int offsetY = -1; offsetY <= 0; offsetY++) {
			for (int offsetX = -1; offsetX <= (offsetY < 0 ? 1 : -1); offsetX++) {
				values[(offsetY + 1) * 3 + (offsetX + 1)] = getCoordinateValue(image, x + offsetX, y + offsetY);	
			}
		}
		return values;
	}
	
	private boolean hasValidValue(int[] data) {
		for (int i = 0; i < data.length; i++)
			if (data[i] != INVALID)
				return true;
		
		return false;
	}
	
	private int getMinValidValue(int[] data) {
		int min = Integer.MAX_VALUE;
		
		for (int i = 0; i < data.length; i++) {
			if (data[i] != INVALID) {
				min = Math.min(min,  data[i]);
			}
		}
		
		if (min == Integer.MAX_VALUE)
			throw new IllegalArgumentException("Data contains no valid values!");
		
		return min;
	}
	
	private boolean isBackground(int[] image, int x, int y) {
		return isBackground(image, x, y, 0);
	}
	
	private boolean isBackground(int[] image, int x, int y, int tolerance) {
		return Math.abs(WHITE - image[y * width + x]) <= tolerance;		
	}
	
	private void assignValue(int[] image, int x, int y, int value) {
		image[y * width + x] = value;
	}
	
	private int findValue(List<Set<Integer>> linked, int value) {
		int last = INVALID;
		
		while (last != value) {
			last = value;
			Set<Integer> s = linked.get(value);
			value = s.iterator().next();
		}
		
		return last;
	}

	public void apply3DFilter(float[] operator, String newFileName) {
		storeGrayValues();
		int fillerValue = 0;
		int[] temp = new int[operatorSize];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int position = y * width + x;
				temp = getThreeByThreePixelBlock(originalImage, x, y, fillerValue);
				int sum = 0;
				for (int i = 0; i < operatorSize; i++) {
					sum += temp[i] * operator[i];
				}
				altereredImage[position] = sum;
			}
		}
		createNewImage(newFileName);
	}
	
	public void printImage(int[] image) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				System.out.print(image[y * width + x] + " ");
			}
			System.out.println();
		}
	}
	
	public void findSegments() {
		generateBlackAndWhiteImage();
		int currentLabel = -1;
		int[] labels = new int[height * width];		
		List<Set<Integer>> linked = new ArrayList<>();
		Map<Integer, Integer> computedLinkCache = new HashMap<>();
		
		for (int i = 0; i < labels.length; i++)
			labels[i] = INVALID;
		
		// First pass
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!isBackground(altereredImage, x, y)) {
					
					int[] neighborVals = getProcessedNeighboringValues(labels, x, y);
					
					if (!hasValidValue(neighborVals)) {
						assignValue(labels, x, y, ++currentLabel);
						linked.add(currentLabel, new HashSet<>());
						linked.get(currentLabel).add(currentLabel);
					} else {
						int minVal = getMinValidValue(neighborVals);
						assignValue(labels, x, y, minVal);
						
						for (int i : neighborVals) {
							for (int j : neighborVals) {
								if (i != INVALID && j != INVALID && i != j) {
									linked.get(i).add(j);
								}
							}
						}
					}
				}
			}
		}
		
		// Second pass
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (!isBackground(altereredImage, x, y)) {
					int val = getCoordinateValue(labels, x, y);
					if (!computedLinkCache.containsKey(val)) {
						computedLinkCache.put(val, findValue(linked, val));
					}
					val = computedLinkCache.get(val);
					assignValue(labels, x, y, val);
				}
			}
		}
		
		//printImage(labels);
		HashMap<Integer, Integer> segments = getNumberPixelsPerSegment(labels, true);
	}
	
	private HashMap<Integer, Integer> getNumberPixelsPerSegment(int[] image, boolean printValues) {
		HashMap<Integer, Integer> segments = new HashMap<Integer, Integer>();
		for (int i = 0; i < image.length; i++) {
			if (image[i] != INVALID) {
				int previous = 0;
				if (segments.containsKey(image[i])) {
					previous = segments.get(image[i]);
				}
				segments.put(image[i], previous + 1);
			}
		}

		if (printValues) {
			int i = 1;
			for (Integer key : segments.keySet()) {
				System.out.println("Segment " + i + " has " + segments.get(key) + " black pixels.");
				i++;
			}
		}
		return segments;
	}

	private void generateBlackAndWhiteImage() {
		storeGrayValues();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int position = y * width + x;
				altereredImage[position] = originalImage[position] > 150 ? 255 : 0;
			}
		}
	}

	public void makeImageBlackAndWhite(String newFileName) {
		storeGrayValues();
		generateBlackAndWhiteImage();
		createNewImage(newFileName);
	}
	
	private void createNewImage(String newFileName) {
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int value = altereredImage[y * width + x];
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
			    originalImage[y * width + x] = grayScaleValue;
		
			}
	    }
	}
}
