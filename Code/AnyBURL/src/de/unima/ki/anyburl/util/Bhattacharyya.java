package de.unima.ki.anyburl.util;

public class Bhattacharyya {

	public static void main(String[] args) {
		
		
		double[] p = new double[] {0.3, 0.3, 0.4, 0.5};
		double[] q = new double[] {0.3, 0.4, 0.3, 0.5};

		
		System.out.println(getBhattacharyyaCoefficient(p, q));
		
	}
	
	public static double getKullbackLeiblerQinsteadP(double[] p, double[] q) {
		double distance = 0.0;
		for (int x = 0; x < p.length; x++) {
			distance += p[x] * Math.log(p[x] / q[x]);
		}
		return distance;
	}
	
	public static double  getBhattacharyyaCoefficient(double[] p, double[] q) {
		double c = 0.0;
		for (int x = 0; x < p.length; x++) {
			c += Math.sqrt(p[x] * q[x]);
		}
		return c;
		
		
	}
	
	

}
