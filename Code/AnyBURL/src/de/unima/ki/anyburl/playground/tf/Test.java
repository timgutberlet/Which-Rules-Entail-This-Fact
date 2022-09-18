package de.unima.ki.anyburl.playground.tf;

public class Test {

	public static void main(String[] args) {
		
		
		double d = 0.0001;
		double total = 0.5;
		total += Math.pow(d, 1);
		total += Math.pow(d, 2);
		total += Math.pow(d, 3);
		total += Math.pow(d, 4);
		System.out.println(total);
		total += Math.pow(d, 5);
		total += Math.pow(d, 6);
		System.out.println(total);
		

	}

}
