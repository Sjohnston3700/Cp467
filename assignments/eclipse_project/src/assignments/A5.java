package assignments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import assignments.Image.ImageBounds;

public class A5 {
	
	private A5() {}
	
	// Outputs the zoning feature vector for each segment
	public static void run(String originalFilename, int nZonesX, int nZonesY) throws IOException {
		Image orig = new Image(originalFilename);
		GreyscaleImage gs = new GreyscaleImage(orig);
		SegmentedImage segmented = new SegmentedImage(gs);
		
		// Calculate feature values
		List<List<Float>> zoningFVs = new LinkedList<List<Float>>();		
		List<ImageBounds> bounds = segmented.computeSegmentBounds();
		
		for (ImageBounds b : bounds) {
			List<Float> zoningFeatures = new ArrayList<Float>(nZonesX * nZonesY);
			zoningFVs.add(zoningFeatures);
			
			BlackAndWhiteImage segment = segmented.getSubImage(b);
			List<BlackAndWhiteImage> zones = segment.splitImage(nZonesX, nZonesY);
			
			for (BlackAndWhiteImage zone : zones) {
				float zoningFeature = zone.calculateZoningFeature();
				zoningFeatures.add(zoningFeature);
			}
		}
		
		// Output to console
		for (int i = 0; i < zoningFVs.size(); i++) {
			List<Float> zoningFeatures = zoningFVs.get(i);
			System.out.println("Segment " + i + " zoning feature:");
			
			for (int j = 0; j < zoningFeatures.size(); j++) {
				if (j != 0 && j % 3 == 0) {
					System.out.println(",");
				}
				
				System.out.printf("  %6f", zoningFeatures.get(j));
			}
			
			System.out.println("");
		}
	}
}