package de.unima.ki.anyburl.playground;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * Testing how quickly there are clashes in the hascode.
 * 
 * @author Christian
 *
 */
public class HashCodeTest {

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		// 86 => 87 millionen
		int numOfElements = 100000000;
		// int numOfElements = 1000000;
		
		HashSet<String> keys = new HashSet<String>(numOfElements + 100, 1.0f);
	
	

		String[] names = new String[numOfElements];
		long m1 = System.currentTimeMillis();
		for (int i = 0; i < numOfElements; i = i + 1) {
			//if (i % 500000 == 0) System.out.println(i);
			
			String name = "e" + i;

			keys.add(name);
			names[i] = name;
			
		}
		
		long m2 = System.currentTimeMillis();
		System.out.println("keys: " + keys.size());
		System.out.println("time: " + (m2-m1));
		System.out.println("init done ... check existence:");
		
		long all = 0;
		long min = 10000000;
		long erster = 0;
		for (int j = 0; j < 10; j++) {
			long m3 = System.currentTimeMillis();
			for (int i = 0; i < numOfElements; i = i + 3) keys.contains(names[i]);
			long m4 = System.currentTimeMillis();
			long t = m4 - m3;
			// System.out.println(t);
			if (j > 0) {
				if (t < min) min = t;
			}
			else erster = t;
			all += t;
		}
		
		// gib wichtig werte aus
		System.out.println("erste zahl:     " + erster);
		System.out.println("min ohne erste: " + min); 
		System.out.println("mittelwert:     " + all / 10);
		System.out.println("----");
		
		all = 0;
		min = 10000000;
		erster = 0;
		for (int j = 0; j < 10; j++) {
			long m3 = System.currentTimeMillis();
			for (int i = 2; i < numOfElements; i = i + 3) keys.contains(names[i]);
			long m4 = System.currentTimeMillis();
			long t = m4 - m3;
			// System.out.println(t);
			if (j > 0) {
				if (t < min) min = t;
			}
			else erster = t;
			all += t;
		}
		
		// gib wichtig werte aus
		System.out.println("erste zahl:     " + erster);
		System.out.println("min ohne erste: " + min);
		System.out.println("mittelwert:     " + all / 10);

		
		
		
		
		

	}

}
