package de.unima.ki.anyburl.eval;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;

public class DiffRank1 {
	
	public static void main(String[] args) {
		
		
		
		String input1Path = "exp/bfnet/pat/bfnet-x.txt";
		String input2Path = "exp/bfnet/rankings.txt";
		
		
		
		
		/*
		String input1Path = "exp/bfnet/new/wn18rr-preds-100c5-filtered";
		String input2Path = "exp/bfnet/new/wn18rr-preds-100c5-filteredValid";
		*/
		
		
		// String input2Path = "exp/bfnet/new/wn18rr-preds-100c5-rankings.txt";
		
		// String input1Path = "exp/understanding/wn18rr/anyburl-c5-3600-100-test";
		
		
		
		
		
		// String input2Path = "exp/understanding/wn18rr/complex-100-test";
		
		String trainPath = "data/wn18rr/train.txt";
		String validPath = "data/wn18rr/valid.txt";
		String testPath = "data/wn18rr/test.txt";
		
		TripleSet triples = new TripleSet(testPath);
		TripleSet train = new TripleSet(trainPath);
		TripleSet valid = new TripleSet(validPath);
		
		
		TripleSet trainValid = new TripleSet();
		trainValid.addTripleSet(train);
		trainValid.addTripleSet(valid);
		
		if (args != null && args.length == 3) {
			input1Path = args[0];
			input2Path = args[1];
			testPath   = args[2];			
		}
		
		

		ResultSet rs1            =  new ResultSet("input1", input1Path, true, 100);
		ResultSet rs2            =  new ResultSet("input2", input2Path, true, 100);
		
		/*
		if (Settings.FUNCTIONALITY_THRESHOLD < 1.0) {
			train.computeFunctionality();
			rs1.applyFunctionalityThreshold(train);
			rs1.adjust();
		}
		*/
		
		
		Settings.REWRITE_REFLEXIV = false;
		

		
		rs1.filter(triples, train, valid);
		rs2.filter(triples, train, valid);
		
	
		
		// GoldStandard goldSymmetry    = gold.getSubset("Subsumption");	
		
		int deltaPosTail = 0;
		int deltaNegTail = 0;
		
		int deltaPosHead = 0;
		int deltaNegHead = 0;
		
		double distance = -1.0;
		
		HashMap<String, Integer> rel2Score = new HashMap<String, Integer>();
		HashMap<String, Integer> rel2Count = new HashMap<String, Integer>();
		
		
		int counterValidConnectsOuts = 0; 
		int counterOuts = 0; 
		int counterValidConnectsFines = 0; 
		int counterFines = 0; 

		for (Triple t : triples.getTriples()) {
		
			
			String triple = t.toString();
			String[] tt = triple.split(" ");
			// Triple t = new Triple(tt[0], tt[1], tt[2]);
			
			// if (!tt[1].equals("_hypernym")) continue;
			
			if (!rel2Score.containsKey(tt[1])) rel2Score.put(tt[1], 0);	
			if (rel2Count.containsKey(tt[1])) rel2Count.put(tt[1], rel2Count.get(tt[1]) + 1);
			if (!rel2Count.containsKey(tt[1])) rel2Count.put(tt[1], 1);
			
			String proposedHead1 = (rs1.getHeadCandidates(triple).size() > 0) ? rs1.getHeadCandidates(triple).get(0) : "-";
			String proposedHead2 = (rs2.getHeadCandidates(triple).size() > 0) ? rs2.getHeadCandidates(triple).get(0) : "-";
			
			
			if (proposedHead1.equals(t.getHead()) && proposedHead2.equals(t.getHead())) {
				if (valid.getEntities(t.getRelation(), t.getHead(), true).size() > 0 || valid.getEntities(t.getRelation(), t.getHead(), false).size() > 0) {
					counterValidConnectsFines++;
				}
				counterFines++;
			}
			
			if (!proposedHead2.equals("-") && !proposedHead1.equals("-") && !proposedHead1.equals(proposedHead2)) {
				
				if (!proposedHead1.equals(t.getHead()) && proposedHead2.equals(t.getHead())) {
					rel2Score.put(tt[1], rel2Score.get(tt[1]) + 1);
					System.out.println(triple);
					System.out.println(">>> head 1: " + proposedHead1 + ", " + (proposedHead1.equals(t.getHead())) + " [score=" + (rs1.getHeadConfidences(triple).size() == 0 ? "?" : rs1.getHeadConfidences(triple).get(0)) + "]");	
					System.out.println(">>> head 2: " + proposedHead2 + ", " + (proposedHead2.equals(t.getHead())) + " [score=" + (rs2.getHeadConfidences(triple).size() == 0 ? "?" : rs2.getHeadConfidences(triple).get(0)) + "]");		
					deltaPosHead++;
					int indexOfHitInRS1 = rs1.getHeadCandidates(triple).indexOf(t.getHead());
					if (indexOfHitInRS1 >= 0) System.out.println("RS1: wrong candidate at #1, hit is at " + (indexOfHitInRS1 + 1) + " [score=" + rs1.getHeadConfidences(triple).get(indexOfHitInRS1) + "]");
					else  System.out.println("RS1: wrong candidate at #1, hit is not in ranking [score unknown, last score is " +  rs1.getHeadConfidences(triple).get(rs1.getHeadConfidences(triple).size()-1) + "]");
				}
				
				if (proposedHead1.equals(t.getHead()) && !proposedHead2.equals(t.getHead())) {
					counterOuts++;
					
					if (valid.getEntities(t.getRelation(), t.getHead(), true).size() > 0 || valid.getEntities(t.getRelation(), t.getHead(), false).size() > 0) {
						counterValidConnectsOuts++;
					}
					
					rel2Score.put(tt[1], rel2Score.get(tt[1]) -1);
					System.out.println(triple);
					System.out.println(">>> head 1: " + proposedHead1 + ", " + (proposedHead1.equals(t.getHead())) + " [score=" + rs1.getHeadConfidences(triple).get(0) + "]");	
					System.out.println(">>> head 2: " + proposedHead2 + ", " + (proposedHead2.equals(t.getHead())) + " [score=" + rs2.getHeadConfidences(triple).get(0) + "]");		
					deltaNegHead++;
					int indexOfHitInRS2 = rs2.getHeadCandidates(triple).indexOf(t.getHead());
					if (indexOfHitInRS2 >= 0) System.out.println("RS2: wrong candidate at #1, hit is at " + (indexOfHitInRS2 + 1) + " [score=" + rs2.getHeadConfidences(triple).get(indexOfHitInRS2) + "]");
					else  System.out.println("RS2: wrong candidate at #1, hit is not in ranking [score unknown, last score is " +  rs2.getHeadConfidences(triple).get(rs2.getHeadConfidences(triple).size()-1) + "]");
				}
			}
			
			
			String proposedTail1 = (rs1.getTailCandidates(triple).size() > 0) ? rs1.getTailCandidates(triple).get(0) : "-";
			String proposedTail2 = (rs2.getTailCandidates(triple).size() > 0) ? rs2.getTailCandidates(triple).get(0) : "-";
			if (!proposedTail2.equals("-") && !proposedTail1.equals("-") && !proposedTail1.equals(proposedTail2)) {
				
				if (!proposedTail1.equals(t.getTail()) && proposedTail2.equals(t.getTail())) {
					rel2Score.put(tt[1], rel2Score.get(tt[1]) + 1);
					deltaPosTail++;
					System.out.println(triple);
					System.out.println(">>> tail 1: " + proposedTail1 + ", " + (proposedTail1.equals(t.getTail())) + " [score=" + rs1.getTailConfidences(triple).get(0) + "]");	
					System.out.println(">>> tail 2: " + proposedTail2 + ", " + (proposedTail2.equals(t.getTail())) + " [score=" + rs2.getTailConfidences(triple).get(0) + "]");		
					
					int indexOfHitInRS1 = rs1.getTailCandidates(triple).indexOf(t.getTail());
					if (indexOfHitInRS1 >= 0) System.out.println("RS1: wrong candidate at #1, hit is at " + (indexOfHitInRS1 + 1) + " [score=" + rs1.getTailConfidences(triple).get(indexOfHitInRS1) + "]");
					else  System.out.println("RS1: wrong candidate at #1, hit is not in ranking [score unknown, last score is " +  rs1.getTailConfidences(triple).get(rs1.getTailConfidences(triple).size()-1) + "]");
				
					
					
				}
				
				if (proposedTail1.equals(t.getTail()) && !proposedTail2.equals(t.getTail())) {
					rel2Score.put(tt[1], rel2Score.get(tt[1]) - 1);
					deltaNegTail++;
					System.out.println(triple);
					System.out.println(">>> tail 1: " + proposedTail1 + ", " + (proposedTail1.equals(t.getTail())) + " [score=" + rs1.getTailConfidences(triple).get(0) + "]");	
					System.out.println(">>> tail 2: " + proposedTail2 + ", " + (proposedTail2.equals(t.getTail())) + " [score=" + rs2.getTailConfidences(triple).get(0) + "]");		
					
					int indexOfHitInRS2 = rs2.getTailCandidates(triple).indexOf(t.getTail());
					if (indexOfHitInRS2 >= 0) System.out.println("RS2: wrong candidate at #1, hit is at " + (indexOfHitInRS2 + 1) + " [score=" + rs2.getTailConfidences(triple).get(indexOfHitInRS2) + "]");
					else  System.out.println("RS2: wrong candidate at #1, hit is not in ranking [score unknown, last score is " +  rs2.getTailConfidences(triple).get(rs2.getTailConfidences(triple).size()-1) + "]");
					
					
				}
			}
			
		}
	
		
		System.out.println("Outs:  " + counterValidConnectsOuts + " / " + counterOuts );
		System.out.println("Fines: " + counterValidConnectsFines + " / " + counterFines );
		System.out.println("Delta Head : pos=" + deltaPosHead + " neg=" + deltaNegHead );
		System.out.println("Delta Tail : pos=" + deltaPosTail + " neg=" + deltaNegTail );
		System.out.println("Delta All  : pos=" + (deltaPosHead + deltaPosTail) + " neg=" + (deltaNegHead + deltaNegTail));
		System.out.println("threshold of " + distance + " = " + (100.0 * (((deltaPosHead + deltaPosTail) - (deltaNegHead + deltaNegTail)) / ((double)triples.getTriples().size() * 2.0))) + "%");
		
		
		System.out.println("----");
		
		ArrayList<String> sortedRelations = new ArrayList<String>();
		sortedRelations.addAll(rel2Count.keySet());
		Collections.sort(sortedRelations);
		
		for (String relation : sortedRelations) {
			System.out.println(relation + "\t" + rel2Score.get(relation) + "\t" + (rel2Count.get(relation) * 2));
		}

		System.out.println("A negative score in the second column means that more correct hits have been put on #1 by RS1 compared to RS2");	
		System.out.println("A positive score in the second column means that more correct hits have been put on #1 by RS2 compared to RS1");
	
		
		
		
	}


}
