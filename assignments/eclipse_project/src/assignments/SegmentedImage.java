package assignments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SegmentedImage extends Image {
	
	private static final int WHITE_TOLERANCE = 150;
	
	public SegmentedImage(Image orig) {
		super(orig.getWidth(), orig.getHeight());
		
		Image gs = orig.getGrayscaleImage();
		int currentLabel = -1;
		
		List<Set<Integer>> linked = new ArrayList<>();
		Map<Integer, Integer> computedLinkCache = new HashMap<>();
		
		// First pass
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (!gs.isBackground(x, y, WHITE_TOLERANCE)) {
					int[] neighborVals = getProcessedNeighboringValues(x, y);
					
					if (!hasValidValue(neighborVals)) {
						assignValue(x, y, ++currentLabel);
						linked.add(currentLabel, new HashSet<>());
						linked.get(currentLabel).add(currentLabel);
					} else {
						int minVal = getMinValidValue(neighborVals);
						assignValue(x, y, minVal);
						
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
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				if (!gs.isBackground(x, y, WHITE_TOLERANCE)) {
					int val = getCoordinateValue(x, y);
					if (!computedLinkCache.containsKey(val)) {
						computedLinkCache.put(val, findValue(linked, val));
					}
					val = computedLinkCache.get(val);
					assignValue(x, y, val);
				}
			}
		}
	}
	
	public int[] getProcessedNeighboringValues(int x, int y) {
		int[] values = new int[4];
		
		for (int offsetY = -1; offsetY <= 0; offsetY++) {
			for (int offsetX = -1; offsetX <= (offsetY < 0 ? 1 : -1); offsetX++) {
				values[(offsetY + 1) * 3 + (offsetX + 1)] = getCoordinateValue(x + offsetX, y + offsetY);	
			}
		}
		return values;
	}
	
	@Override
	public Image getGrayscaleImage() {
		Image gs = new Image(getWidth(), getHeight());
		
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
			    int p = getCoordinateValue(x, y);
			    
			    int grayScaleValue = p == INVALID ? WHITE : BLACK;
			    gs.assignValue(x, y, grayScaleValue);
			}
	    }
		
		return gs;
	}
	
	private static boolean hasValidValue(int[] data) {
		for (int i = 0; i < data.length; i++)
			if (data[i] != INVALID)
				return true;
		
		return false;
	}
	
	private static int getMinValidValue(int[] data) {
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
	
	private static int findValue(List<Set<Integer>> linked, int value) {
		int last = INVALID;
		
		while (last != value) {
			last = value;
			Set<Integer> s = linked.get(value);
			value = s.iterator().next();
		}
		
		return last;
	}
	
	public Map<Integer, Integer> getNumberPixelsPerSegment() {
		Map<Integer, Integer> segments = new HashMap<Integer, Integer>();
		for (int y = 0; y < getHeight(); y++) {
			for (int x = 0; x < getWidth(); x++) {
				int val = getCoordinateValue(x, y);
				if (val != INVALID) {
					int previous = 0;
					if (segments.containsKey(val)) {
						previous = segments.get(val);
					}
					segments.put(val, previous + 1);
				}
			}
		}
		
		return segments;
	}
}