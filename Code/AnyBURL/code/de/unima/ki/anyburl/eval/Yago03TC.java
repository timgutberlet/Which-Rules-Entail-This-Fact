package de.unima.ki.anyburl.eval;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;

public class Yago03TC {
	
	
	public static final double THRESHOLD = 0.5;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		
		PrintWriter pw = new PrintWriter("exp/yago3-tc/xxx.txt");
		
		ResultSet rs = new ResultSet("100", "exp/yago3-tc/yago-p-valid-1000", true, 10);
		
		TripleSet positives = new TripleSet("data/YAGO03-10/TC/valid_positive.txt");
		
		int counter = 0;
		int counterAccept = 0;
		int counterReject = 0;
		int counterAcceptTrue = 0;
		int counterAcceptFalse = 0;
		int counterRejectTrue = 0;
		int counterRejectFalse = 0;
		for (CompletionResult cr : rs) {
			counter++;
			String[] token = cr.getTripleAsString().split(" ");
			Triple triple = new Triple(token[0], token[1], token[2]);
			boolean accepted = false;
			for (int i = 0; i < cr.getHeads().size(); i++) {
				String h = cr.getHeads().get(i);
				double c = cr.getHeadConfidences().get(i);
				if (triple.getHead().equals(h) && c > THRESHOLD) {
					accepted = true;
				}
			}
			for (int i = 0; i < cr.getTails().size(); i++) {
				String t = cr.getTails().get(i);
				double c = cr.getTailConfidences().get(i);
				if (triple.getTail().equals(t) && c > THRESHOLD) {
		
					accepted = true;
				}
			}
			if (accepted) {
				if (counterAccept % 100 == 0) System.out.println(triple + "\t1");
				counterAccept++;
				pw.println(triple.getHead() + "\t" + triple.getRelation() + "\t" + triple.getTail() + "\t1");
				if (positives.isTrue(triple)) {
					counterAcceptTrue++;
				}
				else {
					counterAcceptFalse++;
				}
			}
			else {
				counterReject++;
				if (!positives.isTrue(triple)) {
					counterRejectTrue++;
				}
				else {
					counterRejectFalse++;
				}
				pw.println(triple.getHead() + "\t" + triple.getRelation() + "\t" + triple.getTail() + "\t0");
			}

			pw.flush();
			
			
		}
		pw.close();
		System.out.println("all=" + counter + " accepted=" + counterAccept + " rejected=" + counterReject);
		System.out.println("accepted,true=" + counterAcceptTrue);
		System.out.println("accepted,false=" + counterAcceptFalse);
		System.out.println("rejected,true=" + counterRejectTrue);
		System.out.println("rejected,false=" + counterRejectFalse);

		
		System.out.println("Accuracy: " + ((double)(counterRejectTrue + counterAcceptTrue) /  counter));
		
		double precision = ((double)(counterAcceptTrue) /  counterAccept);
		double recall = ((double)(counterAcceptTrue) /  (counterAcceptTrue + counterRejectFalse));
		double f1 = 2.0 * precision * recall / (precision + recall);
		
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		
		System.out.println("F1: " + f1);
	}

}
