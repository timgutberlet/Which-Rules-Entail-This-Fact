package de.unima.ki.anyburl.rescore.fixes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class ReflexivityFix {

	public static void main(String[] args) throws FileNotFoundException {
		
		

		
		// String inputRS = "exp/understanding/fb237/complex-100-test";
		// String outputRS = "exp/understanding/fb237/complex-100-test-FR";
		
		String trainTS = "data/CODEX/M/train.txt";
		
		String[] models = new String[] {"complex", "conve", "hitter", "rescal", "transe", "tucker"};
		
		for (String model : models) {
			System.out.println("RF on " + model);
			String inputRS = "exp/understanding/codex-m/" + model + "-100-test";
			String outputRS = "exp/understanding/codex-m/" + model + "-100-test-RF";
			

			
			writeFilteredResultSet(trainTS, inputRS, outputRS);			
		}
		

		

	}

	private static void writeFilteredResultSet(String trainTS, String inputRS, String outputRS)
			throws FileNotFoundException {
		TripleSet ts = new TripleSet(trainTS);
		HashSet<String> neverReflexivRelations = new HashSet<String>();
		ResultSet rs = new ResultSet("anonym", inputRS, true, 100);
		
		for (String relation : ts.getRelations()) {
			ArrayList<Triple> triplesR = ts.getTriplesByRelation(relation);
			boolean neverReflexiv = true;
			for (Triple t : triplesR) {
				if (t.getHead().equals(t.getTail())) neverReflexiv = false;					
			}
			if (neverReflexiv) neverReflexivRelations.add(relation);
			
		}
		
		System.out.println("found " + neverReflexivRelations.size() + " relations that are never reflexive ");
		
		ResultSet rsF = new ResultSet();
		
		for (String t : rs.getTriples()) {
			CompletionResult cr = rs.getCompletionResult(t);
			
			String[] token = t.split(" ");
			String head = token[0];
			String relation = token[1];
			String tail = token[2];
			
			if (!neverReflexivRelations.contains(relation)) {
				rsF.results.put(t, cr);
				continue;
			}
			
			CompletionResult crF = new CompletionResult(t);
			
			ArrayList<Double> headConfidences = new ArrayList<Double>();
			ArrayList<Double> tailConfidences = new ArrayList<Double>();
			
			ArrayList<String> heads = new ArrayList<String>();
			ArrayList<String> tails = new ArrayList<String>();
			
			for (int i = 0; i < cr.getHeads().size(); i++) {
				if (!cr.getHeads().get(i).equals(tail)) {
					heads.add(cr.getHeads().get(i));	
					headConfidences.add(cr.getHeadConfidences().get(i));
				}
			}
			
			for (int i = 0; i < cr.getTails().size(); i++) {
				if (!cr.getTails().get(i).equals(head)) {
					tails.add(cr.getTails().get(i));	
					tailConfidences.add(cr.getTailConfidences().get(i));
				}
			}
			
			crF.setHeads(heads);
			crF.setHeadConfidences(headConfidences);
			crF.setTails(tails);
			crF.setTailConfidences(tailConfidences);
			rsF.results.put(t, crF);
			
		}
		System.out.println("wrote result set to " + outputRS);
		rsF.write(outputRS);
	}
	
	
	

}
