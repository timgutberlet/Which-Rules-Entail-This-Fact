package de.unima.ki.anyburl.playground;

import java.io.IOException;
import java.util.ArrayList;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.HitsAtK;
import de.unima.ki.anyburl.eval.ResultSet;

public class TopK500 {

	public static void main(String[] args) throws IOException {
		
		TripleSet train = new TripleSet("data/codex/M/train.txt");
		TripleSet valid = new TripleSet("data/codex/M/valid.txt");
		TripleSet test = new TripleSet("data/codex/M/test.txt");
		
		ResultSet rs50 = new ResultSet("50-100", "exp/transformer/sparse/codex-m/500worse/50-100-ranking-valid", true, 500);
		ResultSet rs500 = new ResultSet("500", "exp/transformer/sparse/codex-m/500worse/500-ranking-valid", true, 500);
		
		HitsAtK hitsAtK50 = new HitsAtK();
		hitsAtK50.addFilterTripleSet(train);
		hitsAtK50.addFilterTripleSet(valid);
		hitsAtK50.addFilterTripleSet(test); 
		
		HitsAtK hitsAtK500 = new HitsAtK();
		hitsAtK500.addFilterTripleSet(train);
		hitsAtK500.addFilterTripleSet(valid);
		hitsAtK500.addFilterTripleSet(test); 
		
		computeScores(rs50, valid, hitsAtK50);
		computeScores(rs500, valid, hitsAtK500);
		
		int[] ks = new int[] {0, 9, 19, 29, 39, 49, 99, 199, 499};
		
		System.out.println("h@k\tALL        \t          \tHEAD       \t        \tTAIL   ");
		System.out.println("   \t50         \t500       \t50         \t500     \t50         \t500");
		
		for (int i = 0; i < ks.length; i++) {
			System.out.print((ks[i]+1) + "\t");
			if (ks[i] <= 49) System.out.print(hitsAtK50.getHitsAtK(ks[i]) + "\t" + hitsAtK500.getHitsAtK(ks[i]) + "\t");
			else System.out.print("-       \t" + hitsAtK500.getHitsAtK(ks[i]) + "\t");
			if (ks[i] <= 49) System.out.print(hitsAtK50.getHitsAtKHeads(ks[i]) + "\t" + hitsAtK500.getHitsAtKHeads(ks[i]) + "\t");
			else System.out.print("-       \t" + hitsAtK500.getHitsAtKHeads(ks[i]) + "\t");
			if (ks[i] <= 49) System.out.print(hitsAtK50.getHitsAtKTails(ks[i]) + "\t" + hitsAtK500.getHitsAtKTails(ks[i]) + "\t");
			else System.out.print("-       \t" + hitsAtK500.getHitsAtKTails(ks[i]) + "\t");
			System.out.println();
		}
		
		
		findThem(valid, rs500, rs50, true);
		
		

	}
	
	
	private static void findThem(TripleSet gold, ResultSet rs500, ResultSet rs50, boolean headNotTail) {
		ArrayList<String> candRich;
		ArrayList<String> candFew;
		ArrayList<Double> confRich;
		ArrayList<Double> confFew;
		String hit;
		
		
		for (Triple t : gold.getTriples()) {	
			if (headNotTail) {
				candRich = rs500.getHeadCandidates(t.toString());
				confRich = rs500.getHeadConfidences(t.toString());
				candFew = rs50.getHeadCandidates(t.toString());
				confFew = rs50.getHeadConfidences(t.toString());
				hit = t.getHead();
			}
			else  {
				candRich = rs500.getTailCandidates(t.toString());
				confRich = rs500.getTailConfidences(t.toString());
				candFew = rs50.getTailCandidates(t.toString());
				confFew = rs50.getTailConfidences(t.toString());
				hit = t.getTail();
			}
			
			for (int j = 0; j < 1; j++) {
				if (candFew.get(j).equals(hit)) {
					if (!candFew.contains(candRich.get(j))) {
						System.out.println("#" + (j+1) + "\t" + t + "\t" +  candRich.get(j));
						System.out.println("\t[rich#1-#40: " + confRich.get(j) + "-" +confRich.get(39) + "]");
						System.out.println("\t[few#1-#40: " + confFew.get(j) + "-" +confFew.get(39)  + "]");
						for (int x = 0; x < 50; x++) {
							if (candRich.get(x).equals(hit)) System.out.println(" hit has been moved in rich to #" + (x+1));
						}
						
						System.out.println();
					}
				}
			}
		}
		
	}


	private static void computeScores(ResultSet rs, TripleSet gold, HitsAtK hitsAtK ) {
		for (Triple t : gold.getTriples()) {		
			ArrayList<String> cand1 = rs.getHeadCandidates(t.toString());
			// System.out.println(cand1.size());
			hitsAtK.evaluateHead(cand1, t);
			ArrayList<String> cand2 = rs.getTailCandidates(t.toString());
			// System.out.println(cand2.size());
			hitsAtK.evaluateTail(cand2, t);
		}
	}

}
