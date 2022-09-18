package de.unima.ki.anyburl.playground;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Synthetic {
	
	public static Random rand = new Random();	

	public static void main(String[] args) throws FileNotFoundException {
		
		int numOfNumberSets = 2;
		
		PrintWriter train = new PrintWriter("data/NUMBERS/MINI/train.txt");
		PrintWriter valid = new PrintWriter("data/NUMBERS/MINI/valid.txt");
		PrintWriter test  = new PrintWriter("data/NUMBERS/MINI/test.txt");
		
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		for (int n = 0; n < 100; n++) numbers.add(n);
		
		
		for (int s = 1; s < numOfNumberSets; s++) {
			Collections.shuffle(numbers);
			
			for (int n = 0; n < 100; n++) {
				print(train, triple(nid(s,numbers.get(n)),"from", sid(s)));
			}
			
			for (int n = 0; n < 5; n++) {
				print(test, triple(nid(s,numbers.get(n)),"suc", nid(s,(numbers.get(n)+1) % 100)));
				
				
			}
			for (int n = 5; n < 10; n++) {
				print(valid, triple(nid(s,numbers.get(n)),"suc", nid(s,(numbers.get(n)+1) % 100)));
				
			}
			for (int n = 10; n < 100; n++) {
				print(train, triple(nid(s,numbers.get(n)),"suc", nid(s,(numbers.get(n)+1) % 100)));
			}
			
		}
		
		train.flush();
		train.close();
		valid.flush();
		valid.close();
		test.flush();
		test.close();
		System.out.println("done");
		
		
	}
	
	private static void print(PrintWriter train, PrintWriter valid, PrintWriter test, String triple) {
		PrintWriter pw;
		int r = rand.nextInt(20);
		if (r == 0) pw = test;
		else if (r == 1) pw = valid;
		else pw = train;
		pw.println(triple);
	}
	
	private static void print(PrintWriter pw, String triple) {
		pw.println(triple);
	}
	
	
	private static String nid(int ns, int n) {
		return "s" + ns + "n" + n;
	}
	
	private static String sid(int ns) {
		return "s" + ns;
	}
	
	private static String triple(String s, String r, String o) {
		return s + "\t" + r + "\t" + o;
	}

}
