package assignments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class A5 {
	
	private A5() {}
	
	public static void run(String originalFilename) throws IOException {
		Image orig = new Image(originalFilename);
		GreyscaleImage gs = new GreyscaleImage(orig);
		SegmentedImage segmented = new SegmentedImage(gs);
		
		List<ArrayList<Float>> zoningFVs = segmented.calculateZoningFVs();
		
		for (int i = 0; i < zoningFVs.size(); i++) {
			List<Float> zoningFeatures = zoningFVs.get(i);
			System.out.println("Segment " + i);
			for (int j = 0; j < zoningFeatures.size(); j++) {
				System.out.println("  Zoning feature " + i + " is " + zoningFeatures.get(i));
			}
		}
	}
	
}
