package assignments;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class A5 {
	
	private A5() {}
	
	//outputs the zoning feature vector for each segment
	public static void run(String originalFilename) throws IOException {
		Image orig = new Image(originalFilename);
		GreyscaleImage gs = new GreyscaleImage(orig);
		//calculateZoningFVs assumes b&w
		gs.convertToBW();
		SegmentedImage segmented = new SegmentedImage(gs);
		
		List<ArrayList<Float>> zoningFVs = segmented.calculateZoningFVs((float) 0);
		
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