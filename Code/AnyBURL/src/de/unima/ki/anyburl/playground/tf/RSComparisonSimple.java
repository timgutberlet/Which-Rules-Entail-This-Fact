package de.unima.ki.anyburl.playground.tf;

import java.util.ArrayList;
import java.util.HashSet;

import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class RSComparisonSimple {

	public static void main(String[] args) {

		ResultSet rs1            =  new ResultSet("maxplus    ", "exp/transformer/re/predictions-60-maxplus-explanation-stdout-nozero", true, 50);
		ResultSet rs2            = 	new ResultSet("transform  ", "exp/transformer/re/checkpoint_best-50-valid-rankings", true, 50);
		
		for (String t : rs1.getTriples()) {
			
			CompletionResult cr1 = rs1.getCompletionResult(t);
			CompletionResult cr2 = rs2.getCompletionResult(t);
			
			int numOfHeadPredictions1 = getNumOfMeaningfulPredictions(cr1.getHeadConfidences());
			int numOfTailPredictions1 = getNumOfMeaningfulPredictions(cr1.getTailConfidences());
			
			int numOfHeadPredictions2 = getNumOfMeaningfulPredictions(cr2.getHeadConfidences());
			int numOfTailPredictions2 = getNumOfMeaningfulPredictions(cr2.getTailConfidences());
			
			if (numOfHeadPredictions1 != numOfHeadPredictions2)	{
				
				System.out.println(numOfHeadPredictions1 + "\t" + numOfHeadPredictions2);
				System.out.println(t);
				
				System.out.print("In cr1 and not in cr2: ");
				HashSet<String> diff = getDifferences(cr1.getHeads(), cr2.getHeads());
				for (String d : diff) {
					if (t.startsWith(d + " ")) {System.out.println(" XXX"); }
					System.out.print(d + " ");
				}
				System.out.println();
				
				
			}
			// if (numOfHeadPredictions)
			
		}
		
		

	}
	
	private static HashSet<String> getDifferences(ArrayList<String> candidates1, ArrayList<String> candidates2) {
		HashSet<String> diff = new HashSet<String>();
		for (String c : candidates1) {
			if (!candidates2.contains(c)) {
				diff.add(c);
			}
		}
		return diff;
	}

	private static int getNumOfMeaningfulPredictions(ArrayList<Double> confidences) {
		for (int i = 0; i < confidences.size(); i++) {
			if (confidences.get(i) < -999) return i;
		}
		return confidences.size();
	}
	


}
