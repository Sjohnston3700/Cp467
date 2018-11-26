package assignments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class SegmentedImage extends BlackAndWhiteImage {
	private static final int STARTING_LABEL = 1;	
	private Image segmentData;
	
	public SegmentedImage(GreyscaleImage gs) {
		this(new BlackAndWhiteImage(gs));
	}
	
	public SegmentedImage(BlackAndWhiteImage orig) {
		super(orig);
		segmentData = new Image(orig.getWidth(), orig.getHeight(), STARTING_LABEL - 1);
		
		int currentLabel = STARTING_LABEL - 1;
		
		Map<Integer, Set<Integer>> linked = new HashMap<>(); 
		Map<Integer, Integer> computedLinkCache = new HashMap<>();
		
		// First pass
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (isBlack(x, y)) {
					int[] neighborVals = getProcessedNeighboringValues(x, y);
					
					if (!hasValidLabel(neighborVals)) {
						setSegmentValue(x, y, ++currentLabel);
						linked.put(currentLabel, new HashSet<>());
						linked.get(currentLabel).add(currentLabel);
					} else {
						int minVal = getMinValidLabel(neighborVals);
						setSegmentValue(x, y, minVal);
						
						for (int i : neighborVals) {
							for (int j : neighborVals) {
								if (isLabelValid(i) && isLabelValid(j) && i != j) {
									linked.get(i).add(j);
								}
							}
						}
					}
				}
			}
		}
		
		// Second pass
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (isBlack(x, y)) {
					int label = getSegmentValue(x, y);
					if (!computedLinkCache.containsKey(label)) {
						computedLinkCache.put(label, findValue(linked, label));
					}
					label = computedLinkCache.get(label);
					setSegmentValue(x, y, label);
				}
			}
		}
	}
	
	protected int getSegmentValue(int x, int y) {
		return segmentData.getCoordinateValue(x, y);
	}

	protected void setSegmentValue(int x, int y, int value) {
		segmentData.assignValue(x, y, value);
	}
	
	private int[] getProcessedNeighboringValues(int x, int y) {
		int[] values = new int[4];
		
		for (int offsetY = -1; offsetY <= 0; offsetY++) {
			for (int offsetX = -1; offsetX <= (offsetY < 0 ? 1 : -1); offsetX++) {
				values[(offsetY + 1) * 3 + (offsetX + 1)] = getSegmentValue(x + offsetX, y + offsetY);	
			}
		}
		return values;
	}
	
	public GreyscaleImage getGrayscaleImage() {		
		return new GreyscaleImage((GreyscaleImage)this);
	}
	
	private static boolean isLabelValid(int label) {
		return label >= STARTING_LABEL;
	}
	
	private static boolean hasValidLabel(int[] data) {
		for (int i = 0; i < data.length; i++)
			if (isLabelValid(data[i]))
				return true;
		
		return false;
	}
	
	private static int getMinValidLabel(int[] data) {
		int min = Integer.MAX_VALUE;
		
		for (int i = 0; i < data.length; i++) {
			if (isLabelValid(data[i])) {
				min = Math.min(min,  data[i]);
			}
		}
		
		if (min == Integer.MAX_VALUE)
			throw new IllegalArgumentException("Data contains no valid values!");
		
		return min;
	}
	
	private static int findValue(Map<Integer, Set<Integer>> linked, int value) {
		List<Integer> checked = new LinkedList<Integer>();
		
		Set<Integer> s = new TreeSet<>(linked.get(value));
		checked.add(value);
		
		boolean noop = false;
		while (!noop) {
			noop = true;
			for (Integer label : new HashSet<>(s)) {
				if (!checked.contains(label)) {
					s.addAll(linked.get(label));
					checked.add(label);
					noop = false;
				}
			}
		}
		
		return s.iterator().next();
	}
	
	@Override
	public void printImage() {
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				System.out.printf("%02d ", getSegmentValue(x, y));
			}
			System.out.println();
		}
		
		super.printImage();
	}
	
	/* ----- A2 FUNCTIONS ----- */
	
	public Map<Integer, Integer> getNumberPixelsPerSegment() {
		Map<Integer, Integer> segments = new HashMap<Integer, Integer>();
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int label = getSegmentValue(x, y);
				if (isLabelValid(label)) {
					int previous = 0;
					if (segments.containsKey(label)) {
						previous = segments.get(label);
					}
					segments.put(label, previous + 1);
				}
			}
		}
		
		return segments;
	}
	
	/* ----- A4 FUNCTIONS ----- */
	
	public List<ImageBounds> computeSegmentBounds() {
		Map<Integer, ImageBounds> bounds = new HashMap<>();
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int label = getSegmentValue(x, y);
				if (isLabelValid(label)) {
					ImageBounds b = bounds.get(label);
					
					if (b == null) {
						b = new ImageBounds(x, y);
						bounds.put(label, b);
					}
					
					b.addPoint(x, y);
				}
			}
		}
		
		return new ArrayList<>(bounds.values());
	}
	
	public GreyscaleImage scaleSegments(float scaleFactor) {
		GreyscaleImage orig = getGrayscaleImage();
		GreyscaleImage processed = getGrayscaleImage();
		
		for (ImageBounds bounds : computeSegmentBounds()) {
			GreyscaleImage sub = orig.getSubImage(bounds);			
			GreyscaleImage scaled = sub.getScaledImage(scaleFactor);
			
			processed.assignArea(bounds, WHITE);
			processed.insertImage(scaled, bounds.getMinX(), bounds.getMinY());
		}
		
		return processed;
	}
	
	/* ----- A5 FUNCTIONS ----- */
	
	public static void filterSegmentBoundsBySize(List<ImageBounds> bounds, int minWidth, int minHeight) {	
		for (int i = bounds.size() - 1; i >= 0; i--) {
			ImageBounds b = bounds.get(i);
			
			if (b.getWidth() < minWidth || b.getHeight() < minHeight) {
				bounds.remove(i);
			}
		}
	}
}
