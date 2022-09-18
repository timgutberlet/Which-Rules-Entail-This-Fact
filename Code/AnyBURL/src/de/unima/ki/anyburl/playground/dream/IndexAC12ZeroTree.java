package de.unima.ki.anyburl.playground.dream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashMap;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleAcyclic1;
import de.unima.ki.anyburl.structure.RuleAcyclic2;
import de.unima.ki.anyburl.structure.RuleZero;

public class IndexAC12ZeroTree {

	
	
	public static void main(String[] args) throws IOException {
		System.out.println(Math.cbrt(64) / 4);
		
		/*
		String rulepath = "exp/dream/fb237-explore/fb237-rules-500";
		
		RuleReader rr = new RuleReader();
		LinkedList<Rule> rules = rr.read(rulepath);

		IndexAC12ZeroTree.setParents(rules);
		rules = IndexAC12ZeroTree.supressAllExceptAC1(rules);
		
		
		int counter = 0;
		for (Rule r : rules) {
			if (r instanceof RuleAcyclic1) {
				RuleAcyclic1 ac1 = (RuleAcyclic1)r;
				
				double dx = ac1.getAppliedConfidence();
				double d = ac1.getAppliedConfidence();
				
				
				if (dx + 0.1 < d) {
					System.out.println(d + " => " + dx + "   " + ac1);
					System.out.println("    " + ac1.getParentRule());
					counter++;
					if (counter == 10) break;
				}
				

			}
		
		}
		*/
	}
	
	public static LinkedList<Rule> supressAllExceptAC1(LinkedList<Rule> rules) {
		LinkedList<Rule> rulesFiltered = new LinkedList<Rule>();
		
		for (Rule r : rules) {
			if (r instanceof RuleAcyclic1) {
				rulesFiltered.add(r);
			}
		}
		return rulesFiltered;
		
	}

	public static void setParents(LinkedList<Rule> rules) {
		Settings.EXPERIMENTAL_AC120_AGGREGATION_ON = true;
		System.out.println("* set parent relations from AC1 to AC2 to zero rules");
		HashMap<String, HashMap<String, RuleZero>> rel2con2ZeroX = new HashMap<String, HashMap<String,RuleZero>>();
		HashMap<String, HashMap<String, RuleZero>> rel2con2ZeroY = new HashMap<String, HashMap<String,RuleZero>>();
		
		HashMap<String, HashMap<String, HashMap<String, RuleAcyclic2>>> rel2con2rel2ac2X = new HashMap<String, HashMap<String,HashMap<String,RuleAcyclic2>>>();
		HashMap<String, HashMap<String, HashMap<String, RuleAcyclic2>>> rel2con2rel2ac2XRev = new HashMap<String, HashMap<String,HashMap<String,RuleAcyclic2>>>();
		HashMap<String, HashMap<String, HashMap<String, RuleAcyclic2>>> rel2con2rel2ac2Y = new HashMap<String, HashMap<String,HashMap<String,RuleAcyclic2>>>();
		HashMap<String, HashMap<String, HashMap<String, RuleAcyclic2>>> rel2con2rel2ac2YRev = new HashMap<String, HashMap<String,HashMap<String,RuleAcyclic2>>>();
		
		ArrayList<RuleAcyclic2> ac2s = new ArrayList<RuleAcyclic2>();
		ArrayList<RuleAcyclic1> ac1s = new ArrayList<RuleAcyclic1>();
		
		// System.out.println("Building up index structures");
		
		for (Rule r : rules) {
			if (r instanceof RuleZero) {
				RuleZero zero = (RuleZero)r;
				String relation = zero.getTargetRelation();
				String constant = zero.getHead().getConstant();
				if (zero.isXRule()) {
					if (!rel2con2ZeroX.containsKey(relation)) rel2con2ZeroX.put(relation, new HashMap<String, RuleZero>());
					rel2con2ZeroX.get(relation).put(constant, zero);	
				}
				else {
					if (!rel2con2ZeroY.containsKey(relation)) rel2con2ZeroY.put(relation, new HashMap<String, RuleZero>());
					rel2con2ZeroY.get(relation).put(constant, zero);	
				}
			}
			else if (r instanceof RuleAcyclic2) {
				RuleAcyclic2 ac2 = (RuleAcyclic2)r;
				String relation = ac2.getTargetRelation();
				String constant = ac2.getHead().getConstant();
				String bodyRelation = ac2.getBodyAtom(0).getRelation();
				HashMap<String, HashMap<String, HashMap<String, RuleAcyclic2>>> targetHash;
				if (ac2.isXRule()) {
					if (ac2.getBodyAtom(0).getLeft().equals("X")) targetHash = rel2con2rel2ac2X;
					else  targetHash = rel2con2rel2ac2XRev;
				}
				else {
					if (ac2.getBodyAtom(0).getRight().equals("Y")) targetHash = rel2con2rel2ac2Y;
					else  targetHash = rel2con2rel2ac2YRev;	
				}
				if (!targetHash.containsKey(relation)) targetHash.put(relation, new HashMap<String, HashMap<String, RuleAcyclic2>>());
				if (!targetHash.get(relation).containsKey(constant)) targetHash.get(relation).put(constant, new HashMap<String, RuleAcyclic2>());
				targetHash.get(relation).get(constant).put(bodyRelation, ac2);
				// fine
				ac2s.add((RuleAcyclic2)r);
			}
			else if (r instanceof RuleAcyclic1) {
				ac1s.add((RuleAcyclic1)r);
			}
		}

		
		for (RuleAcyclic2 ac2 : ac2s) {
			String relation = ac2.getTargetRelation();
			String constant = ac2.getHead().getConstant();
			if (ac2.isXRule()) {
				if (rel2con2ZeroX.containsKey(relation)) {
					if (rel2con2ZeroX.get(relation).containsKey(constant)) {
						ac2.setParent(rel2con2ZeroX.get(relation).get(constant));
					}
				}
			}
			if (ac2.isYRule()) {
				if (rel2con2ZeroY.containsKey(relation)) {
					if (rel2con2ZeroY.get(relation).containsKey(constant)) {
						ac2.setParent(rel2con2ZeroY.get(relation).get(constant));
					}
				}
			}
		}
		
		for (RuleAcyclic1 ac1 : ac1s) {
			String relation = ac1.getTargetRelation();
			String constant = ac1.getHead().getConstant();
			String bodyRelation = ac1.getBodyAtom(0).getRelation();
			HashMap<String, HashMap<String, HashMap<String, RuleAcyclic2>>> targetHash = null;
			if (ac1.isXRule()) {
				if (ac1.getBodyAtom(0).getLeft().equals("X")) targetHash = rel2con2rel2ac2X;
				else  targetHash = rel2con2rel2ac2XRev;
			}
			if (ac1.isYRule()) {
				if (ac1.getBodyAtom(0).getRight().equals("Y")) targetHash = rel2con2rel2ac2Y;
				else  targetHash = rel2con2rel2ac2YRev;
			}
			boolean foundParent = false;
			if (targetHash.containsKey(relation)) {
				if (targetHash.get(relation).containsKey(constant)) {
					if (targetHash.get(relation).get(constant).containsKey(bodyRelation)) {
						ac1.setParent(targetHash.get(relation).get(constant).get(bodyRelation));
						foundParent = true;
					}
				}
			}
			// if (!foundParent) System.out.println(ac1);
		}
		
		// System.out.println("AC2-X rules with parent: " + ac2WithParentXCounter);
		// System.out.println("AC2-X rules with parent: " + ac2WithParentYCounter);
		// System.out.println("all AC2 rules with parent: " + (ac2WithParentXCounter + ac2WithParentYCounter));
		// System.out.println("all AC1 rules with parent: " + ac1WithParentCounter);
	}

}
