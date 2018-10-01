package a1;

public class Main {
  public static void main(final String args[]) throws Exception {
	  
	///////////////////////////// A1 //////////////////////////
	// Sharpen
//	float[] operator = { 0, -1, 0, -1, 5f, -1, 0, -1, 0 };
	
	// Blur
//	float[] operator = { 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.1f};
//
//    Image image = new Image("cat.jpg");
//    image.apply3DFilter(operator, "image_altered.jpg");
    ///////////////////////////// A1 //////////////////////////
    
    Image image = new Image("segments2.jpg");
    //image.makeImageBlackAndWhite("image_altered.jpg");
    image.findSegments();
  }
}
