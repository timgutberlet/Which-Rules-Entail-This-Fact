package de.unima.ki.anyburl.rescore;

import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class Outlier {

	public static void main(String[] args) {

		ResultSet rs1 = new ResultSet("anyburl  ", "exp/understanding/codex-s/anyburl-c3-3600-100-test", true, 100);
		ResultSet rs2 = new ResultSet("complex  ", "exp/understanding/codex-s/complex-100-test", true, 100);
		
		double minFactor = 1.5;
		
		for (CompletionResult cr2 :rs2) {
			
			String triple = cr2.getTripleAsString();
			String token[] = triple.split(" ");
			String head = token[0];
			String reation = token[1];
			String tail = token[2];
			
			String cr2HeadFirst = cr2.getHeads().get(0);
			String cr2TailFirst = cr2.getTails().get(0);
			
			CompletionResult cr1 = rs1.getCompletionResult(triple);
			
			
			double topC = cr1.getHeadConfidences().get(0);
			String topCandidate = cr1.getHeads().get(0);
			for (int i = 0; i < cr1.getHeads().size(); i++) {
				String candidate = cr1.getHeads().get(i);
				double c = cr1.getHeadConfidences().get(i);
				if (cr2HeadFirst.equals(candidate)) {
					if (topC / c > minFactor) {
						boolean abHit = head.equals(topCandidate);
						boolean emHit = head.equals(candidate);
						if (!abHit && !emHit) continue;
						System.out.println("triple: " + triple);
						System.out.println(abHit + " | anyburl #1 " + topC + ": " + topCandidate);
						System.out.println(emHit + " | top embedding has an anyburl confidence of " + c + ": " + candidate);
						System.out.println("");
	
					}
				}
				
				
			}
			
		}
		
		
		
		
	}

}
