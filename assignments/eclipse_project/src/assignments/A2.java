package assignments;

import java.util.Map;

public class A2 {	
	
	public static void run(String imageFilename) {
		Image orig = new Image(imageFilename);
		
		SegmentedImage labels = new SegmentedImage(orig);
		
		Map<Integer, Integer> segments = labels.getNumberPixelsPerSegment();
		
		int i = 1;
		for (Integer key : segments.keySet()) {
			System.out.println("Segment " + i + " has " + segments.get(key) + " black pixels.");
			i++;
		}
	}
	
	
	
	
	
	
}
