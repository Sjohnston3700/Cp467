package assignments;

public class Main {
	public static void main(final String args[]) throws Exception {

		final int ASSIGNMENT = 7;

		switch (ASSIGNMENT) {
		case 1:
			// Sharpen
			float[] operator = { 0, -1, 0, -1, 5f, -1, 0, -1, 0 };
			// Blur
			// float[] operator = { 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};

			A1.run("cat.jpg", operator, "cat_a1.jpg");
			break;
		case 2:
			A2.run("segments2.jpg", "black_and_white.jpg");
			break;
		case 3:
			A3.run("cat.jpg", "cat_a3_1.jpg", "cat_a3_2.jpg");
			break;
		case 4:
			A4.run("segments2.jpg", 0.75f, "segments2_scaled.jpg");
			break;
		case 5:
			A5.run("digits.jpg", 3, 3);
			break;
		case 6:
			A6.run("digits.jpg", "digit_zoning_FVs.csv");
			break;
		case 7:
			A7.run("test3.jpg", "digit_zoning_FVs.csv");
			break;
		case 8:
			A8.run("segments2.jpg", "segments2_thinned.jpg");
			break;
		}
	}
}
