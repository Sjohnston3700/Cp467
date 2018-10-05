package assignments;

import java.io.IOException;

public class A3 {
	
	private A3() {}
	
	public static void run(String originalFilename, String newFilename, String newFilename2) throws IOException {
		Image orig = new Image(originalFilename);
		Image gs = orig.getGrayscaleImage();
		
		float[] operator1 = { 0.3f, 0.3f, 0.3f, 0, 0, 0, 0, 0, 0 };
		float[] operator2 = { 0.3f, 0, 0, 0.3f, 0, 0, 0.3f, 0, 0 };
		Image filtered = gs.getFilteredImage(operator1);
		Image filtered2 = filtered.getFilteredImage(operator2);
		filtered2.saveGrayscaleToFile(newFilename);
		
		float[] operator3 = { 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f };
		Image filtered3 = gs.getFilteredImage(operator3);
		filtered3.saveGrayscaleToFile(newFilename2);
		
		ImageJPanel viewer = new ImageJPanel(newFilename, newFilename2);
		viewer.display();
	}
	
	
}
