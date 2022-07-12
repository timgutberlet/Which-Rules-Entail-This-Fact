package de.unima.ki.anyburl.playground;

import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class Get {

	public static void main(String[] args) {
		
		for (int j = 1; j <= 6; j++) {

			// ResultSet rs = new ResultSet("X", "exp/understanding/codex-m/hitter-e2-removed-" + j + "-300-testX", true, 300);
			
			ResultSet rs = new ResultSet("X", "exp/understanding/codex-m/hitter" + (j) + "-300-testX", true, 300);
			
			CompletionResult cr = rs.getCompletionResult("Q1364884 P108 Q245247");
			// System.out.println(cr.getTails().size());
			
			for (int i = 0; i < cr.getTails().size(); i++) {
				if (cr.getTails().get(i).equals("Q245247")) {
					System.out.println((i+1) + "\t" + cr.getTailConfidences().get(i));
				}
			}
			
		}
		

		
			
	

	}

}
