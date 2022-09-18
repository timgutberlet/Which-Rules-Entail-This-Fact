package de.unima.ki.anyburl.playground;

public class MultiplicationTest {

	public static void main(String[] args) {
		
		Double dd1 = Double.parseDouble("0.00000001");
		Double dd2 = Double.parseDouble(" 0.99829612");
		
		System.out.println("dd1 = " + dd1);
		System.out.println("dd2 = " + dd2);


		
		
		double[] values = new double[] {0.8372093023255814, 0.8372093023255814, 0.8372093023255814, 0.8372093023255814, 0.8372093023255814};
		
		double score = 1.0;
		
		for (double d : values) {
			
			score = score * (1.0 - d);
			
		}
		double result = 1.0 - score;
		
		System.out.println(result);

	}

}
