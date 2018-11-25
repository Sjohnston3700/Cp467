package assignments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser {
	private CSVParser() {}
	
	public static List<String[]> parseFile(File f) throws IOException {
		List<String[]> data = new ArrayList<>();
		
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		
		String line;
		while ((line = br.readLine()) != null) {
			data.add(line.split(","));
		}
		
		br.close();
		
		return data;
	}
}
