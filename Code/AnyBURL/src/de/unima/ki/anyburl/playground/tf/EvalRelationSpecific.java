package de.unima.ki.anyburl.playground.tf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.HitsAtK;
import de.unima.ki.anyburl.eval.ResultSet;

public class EvalRelationSpecific {
	
	
	public static void main(String[] args) throws IOException {
		
		TripleSet test = new TripleSet("data/WN18RR/test.txt");
		TripleSet train = new TripleSet("data/WN18RR/train.txt");
		
		

		
		

		// predictions-60-maxplus-explanation-stdout-nozero
		// ResultSet rs1            =  new ResultSet("", "exp/transformer/sparse/codex-m/checkpoint_best-ranking", true, 100);
		
		ResultSet rs1            = 	new ResultSet("BFNet ", "exp/bfnet/wn18rr-preds-50", true, 100);
		// ResultSet rs1            = 	new ResultSet("BFNet ", "exp/bfnet/rankings.txt", true, 100);
		
		
		if (Settings.FUNCTIONALITY_THRESHOLD < 1.0) {
			train.computeFunctionality();
			rs1.applyFunctionalityThreshold(train);
			rs1.adjust();
		}		
		
		class WRelation implements Comparable {
			
			public String r;
			public int n; 
			
			public WRelation(String relation, int numOftriples)  {
				this.r = relation;
				this.n = numOftriples;
			}

			@Override
			public int compareTo(Object that) {
				if (that instanceof WRelation) {
					WRelation thatRelation = (WRelation)that;
					int score =  thatRelation.n - this.n;
					if (score == 0) {
						this.r.compareTo(thatRelation.r);
					}
					else {
						return score;
					}
				}
				return 0;
			}

		}

		ArrayList<WRelation> wrelations = new ArrayList<WRelation>();
		
		for (String relation : test.getRelations()) {
			WRelation wrelation = new WRelation(relation, test.getTriplesByRelation(relation).size());
			wrelations.add(wrelation);
		}
		
		Collections.sort(wrelations);
		
		
		HitsAtK hitsFull = new HitsAtK();
		for (WRelation wrelation : wrelations) {
			HitsAtK hits = new HitsAtK();
			String relation = wrelation.r;
			int numOfTriples = test.getTriplesByRelation(relation).size();
			for (Triple t : test.getTriples()) {
				if (t.getRelation().equals(relation)) {
					ArrayList<String> candHead1 = rs1.getHeadCandidates(t.toString());
					// hitsAtK1Head.evaluateHead(candHead1, t);
					ArrayList<String> candTail1 = rs1.getTailCandidates(t.toString());
					// hitsAtK1Tail.evaluateTail(candTail1, t);
					
					hits.evaluateHead(candHead1, t);
					hits.evaluateTail(candTail1, t);
					
					hitsFull.evaluateHead(candHead1, t);
					hitsFull.evaluateTail(candTail1, t);
	
				}
			}
			
			
			System.out.println(relation + "\t" + numOfTriples + "\t" + hits.getHitsAtK(0) + "\t" + hits.getHitsAtKHeads(0) + "\t" + hits.getHitsAtKTails(0));
			// System.out.println(relation + "\t" + numOfTriples + "\t" + hitsAtK1Head.getMRR() + "\t" + hitsAtK2Head.getMRR() + "\t" + hitsAtK1Tail.getMRR() + "\t" + hitsAtK2Tail.getMRR());
		}
		
		System.out.println();
		System.out.println("all-relations\t\t" + hitsFull.getHitsAtK(0) + "\t" + hitsFull.getHitsAtKHeads(0) + "\t" + hitsFull.getHitsAtKTails(0));
		
		

		
	}
	

}
