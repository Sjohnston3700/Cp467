package assignments;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class A6 {
	
	private A6() {}
	
	//saves the zoning feature vector for the digits in originalFilename
	//assumes digits are ordered in ascending order by y value
	public static void run(String originalFilename, String outputFilename) throws IOException {
		Image orig = new Image(originalFilename);
		GreyscaleImage gs = new GreyscaleImage(orig);
		//calculateZoningFVs assumes b&w
		gs.convertToBW();
		SegmentedImage segmented = new SegmentedImage(gs);
		
		List<ArrayList<Float>> zoningFVs = segmented.calculateZoningFVs((float) 0.01);
		BufferedWriter fileWriter = new BufferedWriter(new FileWriter(outputFilename));
		
		for (int i = 0; i < zoningFVs.size(); i++) {
			List<Float> zoningFeatures = zoningFVs.get(i);
			
			//write in format: digit,feature0,...featureN\n
			fileWriter.write(i + ",");
			fileWriter.write(zoningFeatures.stream().map(String::valueOf).collect(Collectors.joining(",")));
			fileWriter.newLine();			
		}
		
		fileWriter.close();
	}
}