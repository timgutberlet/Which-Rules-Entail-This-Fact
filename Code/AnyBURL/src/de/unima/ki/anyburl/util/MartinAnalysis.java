package de.unima.ki.anyburl.util;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

/**
 * Written to check a bug (or maybe just strange behavior reported by Martin Svatos in June 2021.
 * 
 *
 */
public class MartinAnalysis {

	public static void main(String[] args) {
		
		
		ResultSet rs = new ResultSet("codex-m", "exp/martin/codex-m-prediction-500", true, 100);
		
		
		int extremeCases = 0; 
		
		for (CompletionResult cr : rs) {
			String[] token = cr.getTripleAsString().split(" ");
			
			String head = token[0];
			String tail = token[2];
			String relation = token[1];
			
			boolean hitTail = false; 
			boolean hitHead = false; 
			
			double confHead = 0.0;
			double confTail = 0.0;
			
			// cr.getTripleAsString();
			for (int i = 0; i < cr.getHeads().size(); i++) {
				if (cr.getHeads().get(i).equals(head)) {
					// System.out.println(head  + " => " + cr.getHeadConfidences().get(i));
					hitHead = true;
					confHead = cr.getHeadConfidences().get(i);
				}
			}
			
			
			
			for (int i = 0; i < cr.getTails().size(); i++) {
				if (cr.getTails().get(i).equals(tail)) {
					// System.out.println(tail  + " => " + cr.getTailConfidences().get(i));
					hitTail = true;
					confTail = cr.getTailConfidences().get(i);
				}
			}
			
			
			if (hitTail && hitHead) {
				if (Math.abs(confTail - confHead) > 0.00001) {
					extremeCases++;
					System.out.println(cr.getTripleAsString() + " => tail=" + confTail + " head=" + confHead);
				}
			}
			
		}
		System.out.println("Extreme Cases: " +  extremeCases);

	}

}
