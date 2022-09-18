package de.unima.ki.anyburl.playground;

import de.unima.ki.anyburl.data.*;

public class WNvsFB {

	public static void main(String[] args) {
		
		
		TripleSet train = new TripleSet("data/FB15-237/train.txt");
		
		TripleSet valid = new TripleSet("data/FB15-237/valid.txt");
		
		int i = 0;
		for (Triple v : valid.getTriples()) {
			if (i % 100 == 0) System.out.println(i);
			i++;
			boolean hit = false;
			for (Triple t : train.getTriples()) {
				
				if (v.getHead().equals(t.getHead()) && v.getTail().equals(t.getTail())) {
					System.out.println(v);
					System.out.println(t);
					System.out.println();
					hit = true;
					break;
				}
				if (v.getTail().equals(t.getHead()) && v.getHead().equals(t.getTail())) {
					System.out.println(v);
					System.out.println(t);
					System.out.println();
					hit = true;
					break;
				}
			
			}
			if (hit) {


			}
			
			
			
		}

	}

}
