package assignments;

import java.io.IOException;

public class A8 {
	
	private A8() {}
	
	//thins the segments in the image according to the Z-S algorithm
	public static void run(String originalFilename, String outputFilename) throws IOException {
		Image orig = new Image(originalFilename);
		BlackAndWhiteImage bw = new BlackAndWhiteImage(orig);
		bw.thin();
		bw.saveToFile(outputFilename);
		
		ImageJPanel viewer = new ImageJPanel(originalFilename, outputFilename);
		viewer.display();
	}
	
	
}