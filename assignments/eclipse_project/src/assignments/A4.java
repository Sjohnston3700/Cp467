package assignments;

import java.io.IOException;

public class A4 {
	
	private A4() {}
	
	public static void run(String originalFilename, float scaleFactor, String destFilename) throws IOException {
		Image orig = new Image(originalFilename);
		GreyscaleImage gs = new GreyscaleImage(orig);
		SegmentedImage segmented = new SegmentedImage(gs);
		
		segmented.printImage();
		GreyscaleImage scaled = segmented.scaleSegments(scaleFactor);
		scaled.saveToFile(destFilename);
		
		ImageJPanel viewer = new ImageJPanel(originalFilename, destFilename);
		viewer.display();
	}
	
}
