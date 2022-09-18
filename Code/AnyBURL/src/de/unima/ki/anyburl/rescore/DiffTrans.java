package de.unima.ki.anyburl.rescore;

import de.unima.ki.anyburl.eval.ResultSet;

public class DiffTrans {

	public static void main(String[] args) {
		
		
		ResultSet rs1 = new ResultSet("anyburl      ", "exp/transformer/anyburl-ranking-maxplus-filteredall-valid", true, 50, true);
		ResultSet rs2 = new ResultSet("transformer  ", "exp/transformer/transformer-ranking-filteredall-valid", true, 50, true);
		
		int tailCounter = 0;
		int headCounter = 0;
		
		for (String t : rs1.getTriples()) {
			boolean hitRS2 = false;
			// System.out.println(t);
			for (int i = 0; i < rs2.getTailConfidences(t).size(); i++) {
				String h2 = rs2.getTailCandidates(t).get(i);
				if (t.endsWith(" " + h2)) hitRS2 = true;
			}
			for (int i = 0; i < rs1.getTailCandidates(t).size(); i++) {
				String h1 = rs1.getTailCandidates(t).get(i);
				if (t.endsWith(" " + h1)) {
					if (!hitRS2) {
						System.out.println("(" + i + ") " + t);
						tailCounter++;
						break;
					}
				}
			}
		}
		
		for (String t : rs1.getTriples()) {
			boolean hitRS2 = false;
			for (int i = 0; i < rs2.getHeadConfidences(t).size(); i++) {
				String h2 = rs2.getHeadCandidates(t).get(i);
				if (t.startsWith(h2 + " ")) hitRS2 = true;
			}
			for (int i = 0; i < rs1.getHeadCandidates(t).size(); i++) {
				String h1 = rs1.getHeadCandidates(t).get(i);
				if (t.startsWith(h1 + " ")) {
					if (!hitRS2) {
						System.out.println("(" + i + ") " + t);
						headCounter++;
					}
				}
			}

		}
		

		
		
		System.out.println("Correct tail hits not in transformer ranking: " + tailCounter);
		System.out.println("Correct head hits not in transformer ranking: " + headCounter);
		
		System.out.println("Completion tasks: " + rs1.getTriples().size());

	}

}
