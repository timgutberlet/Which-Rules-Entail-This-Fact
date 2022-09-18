package de.unima.ki.anyburl.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import de.unima.ki.anyburl.data.TripleSet;

public class OLPBenchExtender {

	public static void main(String[] args) throws FileNotFoundException {
		
		
		
		String path = args[0];
		PrintWriter pw = new PrintWriter(args[1]);
		TripleSet ts = new TripleSet(path);
		
		long numOfEntities = ts.getEntities().size();
		
		HashMap<String, HashSet<String>> mappedByPre = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> mappedByEnd = new HashMap<String, HashSet<String>>();
		
		
		long counter = 0;
		
		
	
		for (String e : ts.getEntities()) {
			counter++;
			if (counter % 10000 == 0) System.out.println("Put to bins: " + (double)counter / (double)numOfEntities);
			if (e.length() > 5) {
				String pre = e.substring(0, 5);
				String end =  e.substring(e.length() - 5, e.length());
				if (!mappedByPre.containsKey(pre)) mappedByPre.put(pre, new HashSet<String>());
				if (!mappedByEnd.containsKey(end)) mappedByEnd.put(end, new HashSet<String>());
				mappedByPre.get(pre).add(e);
				mappedByEnd.get(end).add(e);
			}
		}
		
		
		int preEndCounter = 0; 
		for (String pre : mappedByPre.keySet()) {
			if (preEndCounter % 100 == 0) System.out.println("pre: "  + ((double)preEndCounter / mappedByPre.keySet().size()) + " ");
			
			preEndCounter++;
			for (String e1 : mappedByPre.get(pre)) {
				for (String e2 : mappedByPre.get(pre)) {
					if (e1.equals(e2)) continue;
					if (e1.startsWith(e2)) {
						pw.println(e1 + "\t" + "sb:startsWith" + "\t" + e2);	
					}
					if (e2.startsWith(e1)) {
						pw.println(e2 + "\t" + "sb:startsWith" + "\t" + e1);	
					}
								
				}
				pw.flush();
			}	
		}
		
		
		preEndCounter = 0; 
		for (String end : mappedByEnd.keySet()) {
			if (preEndCounter % 100 == 0) System.out.println("end: "  + ((double)preEndCounter / mappedByEnd.keySet().size()) + " ");
			
			preEndCounter++;
			for (String e1 : mappedByEnd.get(end)) {
				for (String e2 : mappedByEnd.get(end)) {
					if (e1.equals(e2)) continue;
					if (e1.endsWith(e2)) {
						pw.println(e1 + "\t" + "sb:endsWith" + "\t" + e2);
					}
					if (e2.endsWith(e1)) {
						pw.println(e2 + "\t" + "sb:endsWith" + "\t" + e1);
					}
								
				}
				pw.flush();
			}	
		}
		
		pw.close();
		
		/*
		for (String e1 : ts.getEntities()) {
			counter++;
			if (counter % 1000 == 0) System.out.println("Progress: " + (double)counter / (double)numOfEntities);
			for (String e2 : ts.getEntities()) {
				if (e1.length() > 3 && e2.length() > 3) {
					
				}
				
			}

		}
		*/

	}

}
