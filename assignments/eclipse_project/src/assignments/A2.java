package assignments;

import java.util.Map;

public class A2 {	
	
	private A2() {}
	
	public static void run(String imageFilename) {
		Image orig = new Image(imageFilename);
		GreyscaleImage gs = new GreyscaleImage(orig);
		
		SegmentedImage labels = new SegmentedImage(gs);
		
		Map<Integer, Integer> segments = labels.getNumberPixelsPerSegment();
		
		int i = 1;
		for (Integer key : segments.keySet()) {
			System.out.println("Segment " + i + " has " + segments.get(key) + " black pixels.");
			i++;
		}
	}
	
}
