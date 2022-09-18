package de.unima.ki.anyburl.util;

import java.io.IOException;
import java.util.LinkedList;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleCyclic;

public class RuleExplorer {
	
	
	public static void main(String[] args) throws IOException {
		
		
		
		
		RuleReader rr = new RuleReader();
		
		LinkedList<Rule> rules = rr.read("exp/understanding/codex-m/anyburl-rules-c3-3600");
		
		LinkedList<Rule> rulesP108 = new LinkedList<Rule>(); 
		
		for (Rule r : rules) {
			
			if (r.getHead().getRelation().equals("P108")) {
				rulesP108.add(r);
				
			}
		}
		System.out.println(rulesP108.size());
		
		int[] counter = new int[5];
		for (Rule r108 : rulesP108) {
			// if (r108 instanceof RuleCyclic && r108.getAppliedConfidence() > 0.05) System.out.println(">>>" + r108);
			if (r108.getAppliedConfidence() < 0.1) {
				counter[0]++;
			}
			if (r108.getAppliedConfidence() >= 0.1 && r108.getAppliedConfidence() < 0.2) {
				counter[1]++;
			}
			if (r108.getAppliedConfidence() >= 0.2 && r108.getAppliedConfidence() < 0.3) {
				System.out.println(r108);
				counter[2]++;
			}
			if (r108.getAppliedConfidence() >= 0.3 && r108.getAppliedConfidence() < 0.4) {
				// System.out.println(r108);
				counter[3]++;
			}
			if (r108.getAppliedConfidence() >= 0.4) {
				// System.out.println(r108);
				counter[4]++;
			}
		}
		System.out.println("0 to 0.1: " + counter[0]);
		System.out.println("0.1 to 0.2: " + counter[1]);
		System.out.println("0.2 to 0.3: " + counter[2]);
		System.out.println("0.3 to 0.4: " + counter[3]);
		System.out.println("0.4 to ...: " + counter[4]);

		
		
		
		
	
		
		
		// r.computeScores(ts);
		// System.out.println(r.getConfidence());
		
	}
	
	
	
	

}
