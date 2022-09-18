package de.unima.ki.anyburl.playground.dream;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.ComparativeEvaluation;
import de.unima.ki.anyburl.eval.GoldStandard;
import de.unima.ki.anyburl.eval.HitsAtK;
import de.unima.ki.anyburl.eval.ResultSet;

public class EvaluationPerRelation {
	
	public static int ALL_HEAD_TAIL = 0; 
	
	
	
	public static void main(String[] args) throws IOException {
		
		Settings.REWRITE_REFLEXIV = false;
		
		ComparativeEvaluation.target = "CODEX_M";
		
		TripleSet trainingSet   = new TripleSet();
		TripleSet validationSet = new TripleSet();
		TripleSet testSet = new TripleSet();
		
		trainingSet = new TripleSet("data/CODEX/M/train.txt");
		validationSet = new TripleSet("data/CODEX/M/valid.txt");
		testSet = new TripleSet("data/CODEX/M/test.txt");
		
		
		TripleSet[] vSets = new TripleSet[testSet.getRelations().size()];
		int i = 0;
		for (String relation : testSet.getRelations()) {
			vSets[i] = new TripleSet();
			vSets[i].addTriples(testSet.getTriplesByRelation(relation));
			i++;
		}
		
		
		DecimalFormat df = new DecimalFormat("00");
		DecimalFormat df2 = new DecimalFormat(".00000");
		
		System.out.print("#\tMRR\tHead\tTail\t");
		for (int j = 0; j < vSets.length; j++) {
			String r = vSets[j].getTriples().get(0).getRelation();
			if (vSets[j].getTriples().size() < 100) continue;
			System.out.print("(" + testSet.getTriplesByRelation(r).size() + ") " + r +  "\t\t");
		}
		System.out.println();
		
		ResultSet rs = new ResultSet("normal", "exp/dream/codex-m-explore/codex-m-500-50-c-u5", true, 50);
		
		
		HitsAtK hitsAtK = new HitsAtK();
		hitsAtK.addFilterTripleSet(trainingSet);
		hitsAtK.addFilterTripleSet(testSet);
		hitsAtK.addFilterTripleSet(validationSet);
		computeScores(rs, testSet, hitsAtK);
		System.out.print(hitsAtK.getHitsAtK(0) + "\t" + hitsAtK.getHitsAtKHeads(0) + "\t" + hitsAtK.getHitsAtKTails(0)  + "\t");
		hitsAtK.reset();

		for (int j = 0; j < vSets.length; j++) {
			if (vSets[j].getTriples().size() < 100) continue;
			computeScores(rs, vSets[j], hitsAtK);
			System.out.print(hitsAtK.getHitsAtKHeads(0) + "\t" + hitsAtK.getHitsAtKTails(0)  + "\t");
			hitsAtK.reset();
		}
		System.out.println();
			
	}
		
	
	private static void computeScores(ResultSet rs, TripleSet gold, HitsAtK hitsAtK) {
		for (Triple triple : gold.getTriples()) {
			ArrayList<String> candHead = rs.getHeadCandidates(triple.toString());
			if (ALL_HEAD_TAIL == 0 || ALL_HEAD_TAIL == 1) hitsAtK.evaluateHead(candHead, triple);
			ArrayList<String> candTail = rs.getTailCandidates(triple.toString());
			if (ALL_HEAD_TAIL == 0 || ALL_HEAD_TAIL == 2) hitsAtK.evaluateTail(candTail, triple);
		}
	}

}
