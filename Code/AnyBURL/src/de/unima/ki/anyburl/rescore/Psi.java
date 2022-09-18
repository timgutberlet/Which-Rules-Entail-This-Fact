package de.unima.ki.anyburl.rescore;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class Psi {
	
	private CompletionResult kgeResult;
	private CompletionResult abResult;
	
	public static void main(String[] args) throws IOException {
		
		String path = "E:/code/libKGE/kge/data/fb15k-237/entity_strings.del";
		Translator t = new Translator(path);
		
		DecimalFormat df = new DecimalFormat("0.000");
		
		ResultSet rs1 = new ResultSet("anyburl      ", "exp/transformer/valid-applied-candidates-NORMAL", true, 50);
		ResultSet rs2 = new ResultSet("transformer  ", "exp/transformer/checkpoint_best-100-valid", true, 50);
		
		
		FileWriter fw = new FileWriter("exp/understanding/fb237/rescal-100-wins");
		
		
		// output is generated for each triple in this list
		HashSet<String> whiteList = new HashSet<String>();
		whiteList.add("/m/0chghy /location/location/contains /m/02bm8"); // australia
		whiteList.add("/m/0d6_s /film/film/produced_by /m/08d9z7"); // tom raider
		whiteList.add("/m/0c4b8 /location/country/capital /m/01_vrh"); // union of south africa
		whiteList.add("/m/0c73g /influence/influence_node/influenced_by /m/0k4gf");
		
		ArrayList<String> triples = new ArrayList<String>();
		triples.addAll(rs1.getTriples());
		
		// Collections.shuffle(triples);

		
		int counterHitBelowAnomalyTail = 0;
		int counterHitBelowAnomalyHead = 0;
		
		for (int i = 0; i < triples.size(); i++) {
			
			
			String triple = triples.get(i);
			String[] token = triple.split(" ");
			String head = token[0];
			String relation = token[1];
			String tail = token[2];
			// System.out.println(triple);
			Psi psi = new Psi(rs2.getCompletionResult(triple), rs1.getCompletionResult(triple));
			

			boolean foundAnomalyTail = false;
			boolean foundHitBelowAnomalyTail = false;
			
			StringBuilder sb = new StringBuilder();
			sb.append(triple + "\n");
			sb.append(t.get(head) + " " + relation + " " + t.get(tail) + "\n");
			for (int j = 0; j < 50; j++) {
				String tailPrediction = rs2.getCompletionResult(triple).getTails().get(j);
				double score = psi.getTailScore(j);
				String explanation = psi.getTailExplanation(j);
				if (score > 0.2 || foundAnomalyTail) {
					String marker = "";
					if (tail.equals(tailPrediction)) marker = "[HIT]";
					sb.append("[" + j + "] " + df.format(score) + ": " + marker + " " +  t.get(tailPrediction) +  " " + explanation + "\n");
					foundAnomalyTail = true;
				}
				if  (tail.equals(tailPrediction))  {
					sb.append("[" + j + "] >>> " + df.format(score) + ": " +  t.get(tailPrediction) +  " " + explanation + "\n");
					if (foundAnomalyTail) foundHitBelowAnomalyTail = true;
				}
			}
			
			if (foundHitBelowAnomalyTail) {
				counterHitBelowAnomalyTail++;
				fw.write(sb.toString() +  "\n\n");
			}
			
			/*
			if (foundHitBelowAnomalyTail || whiteList.contains(triple)) {
				counterHitBelowAnomalyTail++;
				fw.write(sb.toString() +  "\n\n");
			}
			*/
			
			
			/// ************ HEAD PREDICTIONS ****************
			
			
			boolean foundAnomalyHead = false;
			boolean foundHitBelowAnomalyHead = false;
			
			sb = new StringBuilder();
			sb.append(triple + "\n");
			sb.append(t.get(head) + " " + relation + " " + t.get(tail) + "\n");
			for (int j = 0; j < 50; j++) {
				String headPrediction = rs2.getCompletionResult(triple).getHeads().get(j);
				double score = psi.getHeadScore(j);
				String explanation = psi.getHeadExplanation(j);
				if (score < 0.2 || foundAnomalyHead) {
					String marker = "";
					if (head.equals(headPrediction)) marker = "[HIT]";
					sb.append("[" + j + "] " + df.format(score) + ": " + marker + " " +  t.get(headPrediction) +  " " + explanation + "\n");
					foundAnomalyHead = true;
				}
				if  (head.equals(headPrediction))  {
					sb.append("[" + j + "] >>> " + df.format(score) + ": " +  t.get(headPrediction) +  " " + explanation + "\n");
					if (foundAnomalyHead) foundHitBelowAnomalyHead = true;
				}
			}
			
			if (foundHitBelowAnomalyHead) {
				counterHitBelowAnomalyHead++;
				fw.write(sb.toString() +  "\n\n");
			}
			/*
			if (foundHitBelowAnomalyHead || whiteList.contains(triple)) {
				counterHitBelowAnomalyHead++;
				fw.write(sb.toString() +  "\n\n");
			}
			*/
			
			fw.flush();

			
		
			
		}
		
		fw.close();
		
		System.out.println();
		System.out.println("test triples: " + triples.size());
		
		System.out.println("tail predictions: found " + counterHitBelowAnomalyTail + " anomalies");
		System.out.println("head predictions: found " + counterHitBelowAnomalyHead + " anomalies");
		
		
		
	}
	
	



	public Psi(CompletionResult kgeResult, CompletionResult abResult) {
		this.kgeResult = kgeResult;
		this.abResult = abResult;
	}
	
	
	private String getHeadExplanation(int posInKGE) {
		String candidate = this.kgeResult.getHeads().get(posInKGE);
		double kgeScore = this.kgeResult.getHeadConfidences().get(posInKGE);
		if (!(posInKGE < this.abResult.getHeads().size())) {
			return null;
		}
		double abConfidence = 0.0;
		int index = 0;
		do {
			String abCandidate = this.abResult.getHeads().get(index);
			if (candidate.equals(abCandidate)) {
				abConfidence = this.abResult.getHeadConfidences().get(index);
				break;
			}
			index++;
		} while (index < this.abResult.getHeads().size());
		if (abConfidence == 0.0) abConfidence = this.abResult.getHeadConfidences().get(this.abResult.getHeadConfidences().size() - 1);
		return "| " + kgeScore + "{" + index + "," + abConfidence + "}";
	}
	
	
	private String getTailExplanation(int posInKGE) {
		String candidate = this.kgeResult.getTails().get(posInKGE);
		double kgeScore = this.kgeResult.getTailConfidences().get(posInKGE);
		if (!(posInKGE < this.abResult.getTails().size())) {
			return null;
		}
		double abConfidence = 0.0;
		int index = 0;
		do {
			String abCandidate = this.abResult.getTails().get(index);
			if (candidate.equals(abCandidate)) {
				abConfidence = this.abResult.getTailConfidences().get(index);
				break;
			}
			index++;
		} while (index < this.abResult.getTails().size());
		if (abConfidence == 0.0) abConfidence = this.abResult.getTailConfidences().get(this.abResult.getTailConfidences().size() - 1);
		return "| " + kgeScore + "{" + index + "," + abConfidence + "}";
	}
	
	
	
	
	public double getHeadScore(int posInKGE) {
		String candidate = this.kgeResult.getHeads().get(posInKGE);
		double abConfindeceAtPos;
		if (posInKGE < this.abResult.getHeads().size()) {
			abConfindeceAtPos = this.abResult.getHeadConfidences().get(posInKGE);
		}
		else {
			return Double.NaN;
		}
		double abConfidence = 0.0;
		int index = 0;
		do {
			String abCandidate = this.abResult.getHeads().get(index);
			if (candidate.equals(abCandidate)) {
				abConfidence = this.abResult.getHeadConfidences().get(index);
				break;
			}
			index++;
		} while (index < this.abResult.getHeads().size());
		if (abConfidence == 0.0) abConfidence = this.abResult.getHeadConfidences().get(this.abResult.getHeadConfidences().size() - 1);
		return abConfidence / abConfindeceAtPos;
	}
	
	
	public double getTailScore(int posInKGE) {
		String candidate = this.kgeResult.getTails().get(posInKGE);
		double abConfindeceAtPos;
		if (posInKGE < this.abResult.getTails().size()) {
			abConfindeceAtPos = this.abResult.getTailConfidences().get(posInKGE);
		}
		else {
			return Double.NaN;
		}
		
		
		double abConfidence = 0.0;
		int index = 0;
		do {
			String abCandidate = this.abResult.getTails().get(index);
			if (candidate.equals(abCandidate)) {
				abConfidence = this.abResult.getTailConfidences().get(index);
				break;
			}
			index++;
		} while (index < this.abResult.getTails().size());
		if (abConfidence == 0.0) abConfidence = this.abResult.getTailConfidences().get(this.abResult.getTailConfidences().size() - 1);
		return abConfidence / abConfindeceAtPos;
	}
	
	
	
	
	

}
