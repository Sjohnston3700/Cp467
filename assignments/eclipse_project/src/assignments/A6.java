package assignments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import assignments.Image.ImageBounds;

public class A6 {
	
	private A6() {}
	
	//saves the zoning feature vector for the digits in originalFilename
	//assumes digits are ordered in ascending order by y value
	public static void run(String originalFilename, String outputFilename) throws IOException {
		Image orig = new Image(originalFilename);
		GreyscaleImage gs = new GreyscaleImage(orig);
		SegmentedImage segmented = new SegmentedImage(gs);
		
		// Calculate feature values
		int nZonesX = 3;
		int nZonesY = 3;
		float minSegmentSizeFactor = 0.01f;

		List<List<String>> zoningFVs = new LinkedList<>();
		
		int minSegWidth = (int) (segmented.getWidth() * minSegmentSizeFactor);
		int minSegHeight = (int) (segmented.getHeight() * minSegmentSizeFactor);
		
		List<ImageBounds> bounds = segmented.computeSegmentBounds();
		SegmentedImage.filterSegmentBoundsBySize(bounds, minSegWidth, minSegHeight);
		Collections.sort(bounds);
		
		for (ImageBounds b : bounds) {
			List<String> zoningFeatures = new ArrayList<>(nZonesX * nZonesY);
			zoningFVs.add(zoningFeatures);
			
			BlackAndWhiteImage segment = segmented.getSubImage(b);
			List<BlackAndWhiteImage> zones = segment.splitImage(nZonesX, nZonesY);
			
			for (BlackAndWhiteImage zone : zones) {
				float zoningFeature = zone.calculateZoningFeature();
				zoningFeatures.add(String.valueOf(zoningFeature));
			}
		}
			
		// Save result to file
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outputFilename));
		
		int id = 0;
		for (List<String> zoningFeatures : zoningFVs) {			
			//write in format: digit,feature0,...featureN\n
			fileWriter.write(String.valueOf(id++));
			fileWriter.write(",");
			fileWriter.write(String.join(",", zoningFeatures));
			fileWriter.newLine();			
		}
		
		fileWriter.close();
	}
}