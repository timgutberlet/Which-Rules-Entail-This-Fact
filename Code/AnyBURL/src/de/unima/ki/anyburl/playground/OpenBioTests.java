package de.unima.ki.anyburl.playground;

import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class OpenBioTests {

	public static void main(String[] args) {
		
		
		// TripleSet ts = new TripleSet("data/OpenBio/train_sample.csv", true);
		
		ResultSet rs = new ResultSet("all", "exp/openbio/pred-C1-100", true, 10);
		
		
		int counterAll = 0;
		int counterConfidentHeads = 0;
		int counterConfidentTails = 0;
		for (CompletionResult cr : rs) {
			counterAll += 2;
			if (cr.getHeads().size() >= 10) {
				double d = cr.getHeadConfidences().get(0);
				 if (d > 0.5) {
					 counterConfidentHeads++;
				 }
			}
			if (cr.getTails().size() >= 10) {
				double d = cr.getTailConfidences().get(0);
				 if (d > 0.5) {
					 counterConfidentTails++;
				 }
			}
			
		}
		
		System.out.println("all: " + counterAll);

		System.out.println("confidente heads: " + counterConfidentHeads);
		System.out.println("confidente tailss: " + counterConfidentTails);
		System.out.println("confidente together: " + (counterConfidentHeads + counterConfidentTails));
		
	}

}
