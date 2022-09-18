package de.unima.ki.anyburl.playground;

public class KullbackLeibler {
	
	
	
	/// profession(john, ?) => (actor, politician, ...)
	
	// get ZERO rule for profession
	// compute the distribution for (actor, politician, ...) based on ZERO rule
	// for all candidates c in  (actor, politician, ...)
	//    find explanations for profession(john, c) in terms of AC1 rules, AC2 rules, and ZERO rules
	//    score each triple profession(john, c), i.e. compute the (actor, politician, ...)  distribution
	// compare all distributios agauns the ZERO distribution
	// get weighted average, weight the distributions higher that are more far away from ZERO
	
	// open isses: what of there is no zero ditribution?
	// what if the zero distribution has very low values that give a sum clearly less than 1
	

	
	
	
	
	
	public static void main(String[] args) {
		double[] base = new double[] {0.55, 0.20,  0.25};
		double[] p1 = new double[]   {0.60, 0.60,  0.20};
		double[] p2 = new double[]   {0.50, 0.31,  0.20};
		
		
		normalize(p1);
		normalize(p2);
		for (double d : p1) {
			System.out.println(d);
		}
		
		System.out.println("KL: " + klDivergence(p1, base));
		System.out.println("KL: " + klDivergence(p2, base));
		
		
	}
	
	public static double[] normalize(double[] d) {
		double total = 0.0;
		double min = 0.001;
		for (int i = 0; i < d.length; i++ ) {
			if (d[i] < min) d[i] += min;
			total += d[i];
		}
		for (int i = 0; i < d.length; i++ ) d[i] = d[i] / total;
		return d;
			
	}
	
	
	 public static double klDivergence(double[] p1, double[] p2) {
		  double klDiv = 0.0;
		  for (int i = 0; i < p1.length; ++i) {
		    if (p1[i] == 0.0) p1[i] = 0.001;
		    if (p2[i] == 0.0) p2[i] = 0.001; 
		    klDiv += p1[i] * Math.log( p1[i] / p2[i] );
		  }

		  return klDiv; 
		}
	

}
