package de.unima.ki.anyburl.playground;

import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.AlternativeMentions;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;


/**
 * Checks if there if the overfitting related to FB-237 has been happening. 
 *
 */
public class PredictionAnalysis {

	public static void main(String[] args) {
		
		// anyburl: 7982 / 42284
		// distmult: 8533 / 45569
		// rescal: 5671 / 36510
		// complex: 8209 / 44755
		// transe: 6487 / 43764
		// conve: 7122 / 45468
		
		

		String model = "conve";
		String filepath = "exp/fb237-analysis/dog-conve-predictions-20";
		
		ResultSet rs = new ResultSet(model, filepath, true, 10);
		
		
		// ResultSet rs = new ResultSet("Rescal 1 ", "exp/fb237-analysis/dog-transe-predictions-20", true, 10);
		// ResultSet rs = new ResultSet("Rescal 1 ", "exp/fb237-analysis/dog-distmult-predictions", true, 10);

		TripleSet trainingSet = new TripleSet("data/FB15-237/train.txt");
		TripleSet validationSet = new TripleSet("data/FB15-237/valid.txt");
		TripleSet  testSet = new TripleSet("data/FB15-237/test.txt");
		
		int hpredCounter1 = 0;
		int tpredCounter1 = 0;
		
		int hpredCounter20 = 0;
		int tpredCounter20 = 0;
		
		for (CompletionResult cr : rs) {
			
			String tripleAsString = cr.getTripleAsString();
			
			String[] token = tripleAsString.split(" ");
			String subject = token[0];
			// String relation = token[1];
			String object = token[2];
			boolean first = true;
			for (String hpred : cr.getHeads()) {
				if (trainingSet.existsPath( hpred, object, 1)) {
					if (first) hpredCounter1++;
					hpredCounter20++;
				}
				first = false;
			}
			first = true;
			for (String tpred : cr.getTails()) {
				if (trainingSet.existsPath(subject, tpred, 1)) {
					if (first) tpredCounter1++;
					tpredCounter20++;
				}
				first = false;
			}
			


			
		}
		
		System.out.println("tail predictions connected 1: " + tpredCounter1);
		System.out.println("head predictions connected 1: " + hpredCounter1);
		System.out.println("head+tail predictions connected 1: " + (hpredCounter1 + tpredCounter1));

		System.out.println("tail predictions connected 20: " + tpredCounter20);
		System.out.println("head predictions connected 20: " + hpredCounter20);
		System.out.println("head+tail predictions connected 20: " + (hpredCounter20 + tpredCounter20));
		System.out.println("---------------");
		System.out.println(model + ": " + (hpredCounter1 + tpredCounter1) +  " / " + (hpredCounter20 + tpredCounter20));
		
	}

}
