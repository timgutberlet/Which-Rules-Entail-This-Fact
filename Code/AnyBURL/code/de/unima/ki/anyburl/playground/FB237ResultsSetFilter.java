package de.unima.ki.anyburl.playground;

import java.io.FileNotFoundException;

import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.AlternativeMentions;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class FB237ResultsSetFilter {

	public static void main(String[] args) throws FileNotFoundException {
		
		// anyburl: 7982 / 42284
		// distmult: 8533 / 45569
		// rescal: 5671 / 36510
		// complex: 8209 / 44755
		// transe: 6487 / 43764
		// conve: 7122 / 45468
		
		

		String model = "hitter";
		String filepath = "exp/fb237-analysis/dog-" + model + "-predictions-20";
		
		// String model = "anyburl";
		// String filepath = "exp/fb237-analysis/anyburl-predictions-1000";
		
		
		ResultSet rs = new ResultSet(model, filepath, true, 20);
		
		TripleSet trainingSet = new TripleSet("data/FB15-237/train.txt");
		TripleSet validationSet = new TripleSet("data/FB15-237/valid.txt");
		TripleSet  testSet = new TripleSet("data/FB15-237/test.txt");
		
		int hpredCounter1 = 0;
		int tpredCounter1 = 0;
		
		int hpredCounter20 = 0;
		int tpredCounter20 = 0;
		
		
		
		int removalCounter = 0;
		
		
		
		for (CompletionResult cr : rs) {
			
			String tripleAsString = cr.getTripleAsString();
			
			String[] token = tripleAsString.split(" ");
			String subject = token[0];
		
			String object = token[2];
			
			
			
			int i = 0;
			while (i < cr.getTails().size()) {
				String tpred = cr.getTails().get(i);
				if (trainingSet.existsPath(subject, tpred, 1)) {
					cr.getTails().remove(i);
					cr.getTailConfidences().remove(i);
					removalCounter++;
				}
				else {
					i++;
				}
			}
			
			i = 0;
			while (i < cr.getHeads().size()) {
				String hpred = cr.getHeads().get(i);
				if (trainingSet.existsPath(hpred, object, 1)) {
					cr.getHeads().remove(i);
					cr.getHeadConfidences().remove(i);
					removalCounter++;
				}
				else {
					i++;
				}
			}
			
			
			

		}
		
		rs.write(filepath + "-filtered");
		
		System.out.println("removed " + removalCounter + " predictions");
		

		
	}

}
