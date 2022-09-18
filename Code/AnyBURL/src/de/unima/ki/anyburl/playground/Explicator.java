package de.unima.ki.anyburl.playground;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.structure.Atom;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleCyclic;

public class Explicator {
	

	private static String PREFIX = "sr2_";
	
	public static void main(String[] args) throws IOException {

		RuleReader rr = new RuleReader();
		
		TripleSet train = new TripleSet("data/WN18RR/train.txt");
		showStatsTripleSet(train);
		
		
		LinkedList<Rule> rules =rr.read("exp/patrick/explication/wn18rr-rules-60");
		
		System.out.println("Size of training set: " +  train.size());
		PrintWriter pw = new PrintWriter("exp/patrick/explication/wn18rr-temp");
		HashMap<String, Integer> hashedSynRelations = new HashMap<String, Integer>();
		
		int counter = 0;
		int synRelationCounter = 0;
		for (Rule r : rules) {
			if (r instanceof RuleCyclic && r.getAppliedConfidence() > 0.2 & r.bodysize() == 2) {
				RuleCyclic rc = (RuleCyclic)r;
				int id = -1;
				if (hashedSynRelations.containsKey(getBodyPath(rc))) {
					id = hashedSynRelations.get(getBodyPath(rc));
					// System.out.println("--- already seen this body ---");
				}
				else {
					hashedSynRelations.put(getBodyPath(rc), synRelationCounter);
					id = synRelationCounter;
					// System.out.println(id + ": " + getBodyPath(rc));
					synRelationCounter++;
				}
				TripleSet mts= rc.materialize(train);
				for (Triple t : mts.getTriples()) {
					String head = t.getHead();
					String tail = t.getTail();
					pw.println(head + "\t" + (PREFIX + id) + "\t" + tail);
				}
				pw.flush();
				// System.out.println(id + " >>> " + mts.size() +  "\t" + r + "\t" + getBodyPath(rc));
				System.out.println(id + " >>> " + mts.size() +  "\t" + r);
				counter += mts.size();
				// pw.println();
			}
		}
		pw.close();
		
		System.out.println("new triples = " + counter);
		
		
		for (Rule r : rules) {
			if (r instanceof RuleCyclic && r.getAppliedConfidence() > 0.2 & r.bodysize() == 3) {
				System.out.println(r);
				
				
			}
		}
		
		
		
	}
	
	private static void showStatsTripleSet(TripleSet ts) {
		
		double numOfAllTriples = ts.getTriples().size();
		for (String relation : ts.getRelations()) {
			double numOfTriplesPerRelation = ts.getTriplesByRelation(relation).size();
			System.out.println(relation + ": " + (numOfTriplesPerRelation / numOfAllTriples));
		}
		
	}
	
	private static String getBodyPath(RuleCyclic r) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < r.bodysize(); i++) {
			Atom atom = r.getBodyAtom(i);
			sb.append(atom.toString());
			
		}
		return sb.toString();
		
	}

}
