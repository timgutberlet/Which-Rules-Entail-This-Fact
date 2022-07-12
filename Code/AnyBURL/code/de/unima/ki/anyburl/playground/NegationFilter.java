package de.unima.ki.anyburl.playground;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;
import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleAcyclic1;

public class NegationFilter {

	public static void main(String[] args) throws IOException {
		
		String inputRSPath = "exp/wave2/negation/fb237-predictions-1000";
		String outputRSPath = "exp/wave2/negation/fb237-50-filtered-hard";
		
		
		RuleReader rr = new RuleReader();
		
		TripleSet train = new TripleSet("data/FB15-237/train.txt");
		
		Settings.READ_THRESHOLD_CONFIDENCE = 0.0;
		Settings.READ_THRESHOLD_CORRECT_PREDICTIONS = 0;
		
		LinkedList<Rule> rules = rr.read("exp/wave2/negation/nrules_s2");
		HashMap<String, LinkedList<Rule>> relation2Rules = new HashMap<String, LinkedList<Rule>>();
		

		for (Rule r : rules) {
			if (!relation2Rules.containsKey(r.getTargetRelation())) relation2Rules.put(r.getTargetRelation(), new LinkedList<Rule>());
			relation2Rules.get(r.getTargetRelation()).add(r);
		}
		
		System.out.println(rules.size());
		
		
		
		ResultSet rs = new ResultSet("fb237-default", inputRSPath, true, 50);
		
		// TripleSet test = new TripleSet("data/FB15-237/train.txt");
		
		
		int counter = 0;
		int counterAllHeadCandidates = 0;
		int counterAllTailCandidates = 0;
		int filteredHead = 0;
		int filteredTail = 0;
		int correctlyFilteredHead = 0;
		int correctlyFilteredTail = 0;	
		
		HashMap<RuleAcyclic1, Integer> ruleToMistakeCount = new HashMap<RuleAcyclic1, Integer>();
		
		for (CompletionResult cr : rs) {
			
			if (counter % 1000 == 0) System.out.println(counter + " completion tasks have been filtered");
			
			String tripleAsString = cr.getTripleAsString();
			String[] token = tripleAsString.split("\\s+");
			Triple triple = new Triple(token[0], token[1], token[2]);
			
			LinkedList<Rule> relevantRules = relation2Rules.get(triple.getRelation());
			if (relevantRules == null) relevantRules = new LinkedList<Rule>();
			
			// System.out.println(t);
			
			counter++;
			
			for (int hi = 0; hi < cr.getHeads().size(); hi++) {
				String h = cr.getHeads().get(hi);
				counterAllHeadCandidates++;
				boolean filter = false;

				for (Rule r : relevantRules) {
					if (r instanceof RuleAcyclic1) {
						RuleAcyclic1 rule = (RuleAcyclic1)r;
						filter = rule.validates(h, triple.getRelation(), triple.getTail(), train);
						
						// if (filter && (h.equals(triple.getHead()))) {
						//if (filter) {
						//	System.out.println(counter + ": filtered out: " + h + " (indexed at pos " + hi + ") as head proposal for " + triple);
						//	System.out.println("due to " + rule);
						//	System.out.println();
						//}
						if (filter) break;
					}	
				}
				if (filter) {
					
					
					//System.out.println("removed: " +  removedElement);
					//System.out.println();
					
					String removedElement = cr.getHeads().remove(hi);
					cr.getHeadConfidences().remove(hi);
					hi--;					

					filteredHead++;
					if (!h.equals(triple.getHead())) {
						correctlyFilteredHead++;
					}
					else {

					}
				}
			}
			
			
			for (int ti = 0; ti < cr.getTails().size(); ti++) {
				String t = cr.getTails().get(ti);
				counterAllTailCandidates++;
				boolean filter = false;

				for (Rule r : relevantRules) {
					if (r instanceof RuleAcyclic1) {
						RuleAcyclic1 rule = (RuleAcyclic1)r;
						filter = rule.validates(triple.getHead(), triple.getRelation(), t, train);
						
						/*
						if (filter && (t.equals(triple.getHead()))) {
							System.out.println(counter + ":");
							System.out.println("incorrectly filtered out: " + t + " as head proposal for " + triple);
							System.out.println("due to " + rule);
							System.out.println();
						}
						*/
						if (filter) break;
					}	
				}
				if (filter) {
					
					cr.getTails().remove(ti);
					cr.getTailConfidences().remove(ti);
					ti--;

					filteredTail++;
					if (!t.equals(triple.getTail())) {
						correctlyFilteredTail++;
					}
				}
			}
			
		}
		
		rs.write(outputRSPath);
		
		System.out.println("all completion tasks: " + counter);
		System.out.println("random selection mistake rate: " + ((2.0 * counter) / (double)(counterAllHeadCandidates + counterAllTailCandidates)));
		double removalRate1 = (double)(filteredHead + filteredTail) / (double)(counterAllHeadCandidates + counterAllTailCandidates);
		double removalRate2 = (double)(filteredHead + filteredTail) / (double)(counter * 2.0);
		
		System.out.println("removal rate: " + removalRate1 + " (per candidate) or " + removalRate2 + " per task");
		
		double misHead = 1.0 - (double)correctlyFilteredHead / filteredHead;
		double misTail = 1.0 - (double)correctlyFilteredTail / filteredTail;
		System.out.println("filtered heads: " + filteredHead + " correctly filtered: " + correctlyFilteredHead + " mistake-rate: " + misHead);
		System.out.println("filtered tails: " + filteredTail + " correctly filtered: " + correctlyFilteredTail + " mistake-rate: " + misTail);		
		
		
		
	
		
	}

}
