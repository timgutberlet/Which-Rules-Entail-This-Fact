package de.unima.ki.anyburl.playground;

import java.text.DecimalFormat;
import java.util.HashMap;

public class MemoryTest {
	
	public static void main(String[] args) throws InterruptedException {
		
		HashMap<Integer, String> mem = new HashMap<Integer, String>();
		
		
		// free 1536686080
		// free 1533016064
		// heap 3001024512
		
		//       2147483647  
		//  a..z *  5000000 => 2397
		//  a..z * 10000000 => 4444
		//  a..z * 15000000 => 5673
		//  a..z * 30000000 => 6410
	    //  a..z * 50000000 => 7516
		
		//  a *     5000000 => 2397
	    //  a *    10000000 => 2397
		//  a *    15000000 => 4444
     	//  a *    30000000 => 6410
	    //  a *    50000000 => 6853
	    //  a * %  50000000 => 6853
		
		// => all now with % 1000
		//  a..z * 10000000 => 4444
		//  a..z * 20000000 => 5673
	    //  a..z * 30000000 => 6410
		//  a..z * 40000000 => 6853
		//  a..z * 50000000 => 7118
		
		// => all now with % 1000 stored only once
		//  a..z * 10000000 =>  880
		//  a..z * 20000000 => 1760
	    //  a..z * 30000000 => 2856
		//  a..z * 40000000 => 3427
		//  a..z * 50000000 => 4508
		
		
		
		
		for (int i = 0; i < 50000000; i++) {
			String s = "abcdefghijklmnopqrstuvwxyz" + (i % 1000);
			
			if (mem.values().contains(s)) {
				s = mem.get(i % 1000);	
			}
			
			mem.put(i,s);
		}
		
		
		long heapSize = Runtime.getRuntime().totalMemory(); 
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		
		DecimalFormat df = new DecimalFormat("000000.00");
		
		System.out.println(df.format(heapSize / 1000000.0));
		System.out.println(df.format(heapMaxSize / 1000000.0));
		System.out.println(df.format(heapFreeSize / 1000000.0));
		
		// Runtime. getRuntime(). gc();
		
		/*
		System.out.println(df.format(heapSize / 1000000.0));
		System.out.println(df.format(heapMaxSize / 1000000.0));
		System.out.println(df.format(heapSize / 1000000.0));
		*/
		
		Thread.sleep(10000);
		

		
	}

}
