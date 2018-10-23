package assignments;

import java.io.IOException;
import java.util.Map;

public class A2 {	
	
	private A2() {}
	
	public static void run(String originalFilename, String destFilename) throws IOException {
		Image orig = new Image(originalFilename);
		GreyscaleImage bAndW = new GreyscaleImage(orig);
		bAndW.convertToBW();
		
		SegmentedImage labels = new SegmentedImage(bAndW);
		Map<Integer, Integer> segments = labels.getNumberPixelsPerSegment();
		
		int i = 1;
		for (Integer key : segments.keySet()) {
			System.out.println("Segment " + i + " has " + segments.get(key) + " black pixels.");
			i++;
		}
		
		bAndW.saveToFile(destFilename);
		
		ImageJPanel viewer = new ImageJPanel(originalFilename, destFilename);
		viewer.display();
	}
	
}
