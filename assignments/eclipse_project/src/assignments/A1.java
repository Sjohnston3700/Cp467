package assignments;

import java.io.IOException;

public class A1 {
	
	private A1() {}
	
	public static void run(String originalFilename, float[] operator, String newFilename) throws IOException {
		Image orig = new Image(originalFilename);
		Image gs = orig.getGrayscaleImage();
		
		Image filtered = gs.getFilteredImage(operator);
		filtered.saveGrayscaleToFile(newFilename);
		
		ImageJPanel viewer = new ImageJPanel(originalFilename, newFilename);
		viewer.display();
	}
	
	
}
