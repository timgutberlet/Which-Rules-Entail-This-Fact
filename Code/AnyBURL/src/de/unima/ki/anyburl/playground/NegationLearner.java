package de.unima.ki.anyburl.playground;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.structure.Atom;
import de.unima.ki.anyburl.structure.RuleAcyclic1;
import de.unima.ki.anyburl.structure.RuleUntyped;

public class NegationLearner {

	// def (no extension): 500, 500, 50, 10 => 900 rules
	// s1: 500, 500, 50, 5 => 1000 rules
	// s2: 500, 500, 10, 10 => 76000 rules
	
	
	
	// only consider relations in negative rules with a minimum number of instantiations
	public static int MIN_USAGE_RELATION = 500;
	
	// do a sample with a certain size to find the most frequent entities that appear in head and tail poistion
	// w.r.t to a certain relation
	public static int SAMPLE_SIZE = 500;
	
	// consider only those entities that appear more than a minimum  times in the sample
	public static int MIN_USAGE_XATOM = 10;
	
	// consider only those pairs of relations to be used in body and head that that result in a minimum number of joins
	// this works as a check that the relations describe the same type of entities
	public static int MIN_CONNECTEDNESS = 10;
	
	
	// sometimes there are mitakes in the datasets, which means that 
	
	
	public static String outputPath = "exp/wave2/negation/nrules_s2";
	
	public static void main(String[] args) throws FileNotFoundException {
		
		
		
		
		TripleSet train = new TripleSet("data/FB15-237/train.txt");
		
		Set<String> relations = train.getRelations();
		
		ArrayList<Atom> frequentXAtoms = new ArrayList<Atom>();
	
		PrintWriter pw = new PrintWriter(outputPath);
		
		for (String r : relations) {
			// if (!(r.equals("/people/person/nationality"))) continue;
			
			if (train.getTriplesByRelation(r).size() < MIN_USAGE_RELATION) continue;
			
			HashMap<String, Integer> headCount = new HashMap<String, Integer>();
			HashMap<String, Integer> tailCount = new HashMap<String, Integer>();
			
			ArrayList<String> heads = train.selectNRandomEntitiesByRelation(r, true, SAMPLE_SIZE);
			ArrayList<String> tails = train.selectNRandomEntitiesByRelation(r, false, SAMPLE_SIZE);
			
			for (String h : heads) {
				if (!headCount.containsKey(h)) headCount.put(h, 0);
				headCount.put(h, headCount.get(h) + 1);
			}
			for (String t : tails) {
				if (!tailCount.containsKey(t)) tailCount.put(t, 0);
				tailCount.put(t, tailCount.get(t) + 1);
			}
			
			// System.out.println("RELATION: " + r + " => " + train.getTriplesByRelation(r).size());
			for (String h : headCount.keySet()) {
				if (headCount.get(h) >= MIN_USAGE_XATOM) {
					// System.out.println(headCount.get(h) + ": " + r + "(" + h + ",?)");
					Atom atom = new Atom(h, r, "X", true, false);
					frequentXAtoms.add(atom);
				}
			}
			
			for (String t : tailCount.keySet()) {
				if (tailCount.get(t) >= MIN_USAGE_XATOM) {
					// System.out.println(tailCount.get(t) + ": " + r + "(?, " + t + ")");
					Atom atom = new Atom("X", r, t, false, true);
					frequentXAtoms.add(atom);
				}
			}
		}
		
		// System.out.println("==================");
		System.out.println("found " + frequentXAtoms.size() + " frequent atoms");
		
		HashSet<Atom> strippedXAtomsAsSet = new HashSet<Atom>();
		ArrayList<Atom> strippedXAtoms = new ArrayList<Atom>();
		
		for (Atom fxa :frequentXAtoms) {
			if (fxa.isRightC())  {
				Atom stripped = new Atom(fxa.getLeft(), fxa.getRelation(), "something", false, true);
				strippedXAtomsAsSet.add(stripped);
			}
			else {
				Atom stripped = new Atom("something", fxa.getRelation(), fxa.getRight(), true, false);
				strippedXAtomsAsSet.add(stripped);
			}
		}
		
		strippedXAtoms.addAll(strippedXAtomsAsSet);
		System.out.println("reduced to " + strippedXAtoms.size() + " stripped atom patterns");
		
		
		HashMap<Atom, HashSet<Atom>> validNegativePatterns = new HashMap<Atom, HashSet<Atom>>();
		
		int countPotentialNegations = 0;
		for (int i = 0; i < strippedXAtoms.size() - 1; i++) {
			for (int j = i+1; j < strippedXAtoms.size(); j++) {
				
				
				Atom ai = strippedXAtoms.get(i);
				Atom aj = strippedXAtoms.get(j);
				
				int counter;
				
				HashSet<String> distinctJoinValues = new HashSet<String>();
				
				counter = 0;
				ArrayList<Triple> aiTriples = train.getTriplesByRelation(ai.getRelation());
				for (Triple aiT : aiTriples) {
					String joinValue = ai.isRightC() ? aiT.getHead() : aiT.getTail() ;
					Set<String> result = train.getEntities(aj.getRelation(), joinValue, aj.isRightC());
					if (result.size() > 0) {
						distinctJoinValues.add(joinValue);
						counter++;
					}
				}
				if (distinctJoinValues.size() >= MIN_CONNECTEDNESS) {
					
					/*
					if (ai.getRelation().equals("/people/person/gender") && (ai.isRightC())) {
						if (aj.getRelation().equals("/organization/organization_member/member_of./organization/organization_membership/organization") && aj.isRightC()) {
							System.out.println("HIT HIT HIT: " +  distinctJoinValues.size());
							for (String jv : distinctJoinValues) {
								System.out.println("     " +  jv);
							}
							// System.out.println("join value = " + joinValue + " from " + aiT);
							// System.out.println("results.size() = " +  result.size());
						}
					}
					*/
					
		
					
					if (!validNegativePatterns.containsKey(ai)) {
						validNegativePatterns.put(ai, new HashSet<Atom>());
					}
					validNegativePatterns.get(ai).add(aj);
					
					if (!validNegativePatterns.containsKey(aj)) {
						validNegativePatterns.put(aj, new HashSet<Atom>());
					}
					validNegativePatterns.get(aj).add(ai);
					countPotentialNegations++;
				}
			}
		}
		System.out.println("there are potentially " + countPotentialNegations + " patterns for negative rules");
		
		int countInstValidNegativePatterns = 0;
		for (int i = 0; i < frequentXAtoms.size() -1; i++) {
			for (int j = i+1; j < frequentXAtoms.size(); j++) {
				
				// check if its valid
				Atom ai = frequentXAtoms.get(i);
				Atom aj = frequentXAtoms.get(j);
				
				Atom sai = ai.createCopy();
				sai.replace(ai.getConstant(), "something");
				Atom saj = aj.createCopy();
				saj.replace(aj.getConstant(), "something");	
				
				if (validNegativePatterns.containsKey(sai) && validNegativePatterns.get(sai).contains(saj)) {
					countInstValidNegativePatterns++;
					// make the count
			
					Set<String> xIValues = train.getEntities(ai.getRelation(), ai.getConstant(), ai.isLeftC());
					Set<String> xJValues = train.getEntities(aj.getRelation(), aj.getConstant(), aj.isLeftC());
					Set<String> xIxJValues = new HashSet<String>();
					xIxJValues.addAll(xIValues);
					xIxJValues.retainAll(xJValues);
					
					if (xIxJValues.size() == 0) {
						
						RuleUntyped ui = new RuleUntyped(xIValues.size(), 0, 0.0);
						ui.setHead(aj);
						ui.addBodyAtom(ai);
						RuleAcyclic1 ri = new RuleAcyclic1(ui);
						ri.detachAndPolish();
						
						
						RuleUntyped uj = new RuleUntyped(xJValues.size(), 0, 0.0);
						uj.setHead(ai);
						uj.addBodyAtom(aj);
						RuleAcyclic1 rj = new RuleAcyclic1(uj);
						rj.detachAndPolish();
						
						pw.println(ri);
						pw.println(rj);
						
						pw.flush();
						
						
						// System.out.println(ai);
						
						//System.out.println("relation: "+ sai.getRelation() + ", " + sai.getConstant() + ", " + sai.isLeftC());
						
						int c = 0;
						System.out.println("SCORES: " + xIValues.size() + " | " +  xJValues.size());
						
						System.out.println(ai);
						for (String xi : xIValues) {
							c++;
							System.out.println("   x = " + xi);
							if (c == 3) break;
						}
						c = 0;
						System.out.println(aj);
						for (String xj : xJValues) {
							c++;
							System.out.println("   x = " + xj);
							if (c == 3) break;
						}
						System.out.println();
					}
				}
				
			}
		
		}
		
		System.out.println("there are " + countInstValidNegativePatterns + " instantiations of valid negative patterns");
		pw.close();

	}

}
