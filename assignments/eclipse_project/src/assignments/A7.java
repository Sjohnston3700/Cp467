package assignments;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import assignments.Image.ImageBounds;

public class A7 {

	private A7() {}
	
	public static void run(String testImageFilename, String classificationDataFilename) throws IOException {
		final int CLASS_NAME_INDEX = 0;
		final int[] PROP_INDICIES = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		final int K = 1;
		
		// Parse classification data
		List<String[]> rawData = CSVParser.parseFile(new File(classificationDataFilename));
		List<KnnObject> classifications = new LinkedList<>();
		
		for (String[] line : rawData) {
			classifications.add(KnnObject.fromRawData(line, CLASS_NAME_INDEX, PROP_INDICIES));
		}
		
		// Calculate feature data from test image
		int nZonesX = 3;
		int nZonesY = 3;
		float minSegmentSizeFactor = 0.01f;
		
		Image orig = new Image(testImageFilename);
		GreyscaleImage gs = new GreyscaleImage(orig);
		SegmentedImage segmented = new SegmentedImage(gs);		

		List<String[]> zoningFVs = new LinkedList<>();
		
		int minSegWidth = (int) (segmented.getWidth() * minSegmentSizeFactor);
		int minSegHeight = (int) (segmented.getHeight() * minSegmentSizeFactor);
		
		List<ImageBounds> bounds = segmented.computeSegmentBounds();
		SegmentedImage.filterSegmentBoundsBySize(bounds, minSegWidth, minSegHeight);
		Collections.sort(bounds);
		
		for (ImageBounds b : bounds) {
			String[] zoningFeatures = new String[nZonesX * nZonesY];
			zoningFVs.add(zoningFeatures);
			
			BlackAndWhiteImage segment = segmented.getSubImage(b);
			List<BlackAndWhiteImage> zones = segment.splitImage(nZonesX, nZonesY);
			
			int i = 0;
			for (BlackAndWhiteImage zone : zones) {
				float zoningFeature = zone.calculateZoningFeature();
				zoningFeatures[i++] = String.valueOf(zoningFeature);
			}
		}
		
		// Classify segments
		int i = 0;
		for (String[] segmentRaw : zoningFVs) {
			KnnObject segmentParsed = KnnObject.fromRawData(segmentRaw, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
			String computedClass = segmentParsed.knClassify(K, classifications);
			System.out.printf("Segment %d classified as: %s\n", i++, computedClass);
		}
	}
	
}
