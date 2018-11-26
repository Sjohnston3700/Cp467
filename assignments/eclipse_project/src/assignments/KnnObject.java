package assignments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnnObject {
	private static final String UNCLASSIFIED = "[unclassified]";
	
	private double[] props;
	private String classification;
	
	private KnnObject(String classification, int nProps) {
		this.classification = classification;
		this.props = new double[nProps];
	}
	
	public boolean isClass(String classVal) {
		return this.classification.equals(classVal);
	}
	
	public String knClassify(int k, List<KnnObject> training) {
		int maxV = 0;
		String maxClass = UNCLASSIFIED;
		
		Map<String, Integer> classes = new HashMap<>();
		
		for (int i = 0; i < props.length; i++) {
			String classVal = knClassifyForProp(k, training, i);
			
			int current = classes.containsKey(classVal) ? classes.get(classVal) : 0;
			classes.put(classVal, ++current);
						
			if (current > maxV) {
				maxV = current;
				maxClass = classVal;
			}
		}
		
		return maxClass;
	}
	
	private String knClassifyForProp(int k, List<KnnObject> training, int propId) {
		List<KnnObject> sorted = new ArrayList<>(training);
		
		Collections.sort(sorted, new Comparator<KnnObject>() {
			@Override
			public int compare(KnnObject l, KnnObject r) {
				double diff = distForProp(l, propId) - distForProp(r, propId);
				
				if (diff < 0)
					return -1;
				
				if (diff > 0)
					return 1;
				
				return 0;
			}
		});
		
		int maxV = 0;
		String maxClass = UNCLASSIFIED;
		
		Map<String, Integer> classes = new HashMap<>();
		for (int i = 0; i < k; i++) {
			KnnObject o = sorted.get(i);
			
			int current = classes.containsKey(o.classification) ? classes.get(o.classification) : 0;
			classes.put(o.classification, current++);
			
			if (current > maxV) {
				maxV = current;
				maxClass = o.classification;
			}
		}
		
		return maxClass;
	}
	
	private double distForProp(KnnObject other, int propId) {
		return Math.abs(this.props[propId] - other.props[propId]);
	}
	
	public static KnnObject fromRawData(String[] data, int classIndex, int[] propIndicies) {
		String className = UNCLASSIFIED;
		
		if (classIndex >= 0 && classIndex < data.length) {
			className = data[classIndex];
		}
		
		return fromRawData(className, data, propIndicies);
	}
	
	public static KnnObject fromRawData(String[] data, int[] propIndicies) {
		return fromRawData(UNCLASSIFIED, data, propIndicies);
	}
	
	public static KnnObject fromRawData(String className, String[] data, int[] propIndicies) {
		KnnObject parsed = new KnnObject(className, propIndicies.length);
		
		try {
			for (int i = 0; i < propIndicies.length; i++) {
				parsed.props[i] = Double.parseDouble(data[propIndicies[i]]);
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return parsed;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{ props: [");
		for (int i = 0; i < props.length; i++) {
			sb.append((i != 0 ? "," : "") + props[i]);
		}
		sb.append("], class: ");
		sb.append(this.classification);
		sb.append(" }");
		return sb.toString();
	}
}
