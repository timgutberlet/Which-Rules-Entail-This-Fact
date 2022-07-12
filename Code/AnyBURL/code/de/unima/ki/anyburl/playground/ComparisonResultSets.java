package de.unima.ki.anyburl.playground;

import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class ComparisonResultSets {
	
	public static void main(String[] args) {
		
		ResultSet rs1 = new ResultSet("RESCAL", "exp/fb237-analysis/dog-rescal-predictions-20-filtered", true, 20);
		ResultSet rs2 = new ResultSet("AnyBURL", "exp/zero/fb237-predictions-1000-all-x-F-top200", true, 200);
		
		TripleSet train = new TripleSet("data/FB15-237/train.txt");
		
		int tp2TailCounter = 0;

		
		for (CompletionResult cr1 : rs1) {
			CompletionResult cr2 = rs2.getCompletionResult(cr1.getTripleAsString());
			if (cr1.getTails().size() > 0) {
				String tp = cr1.getTails().get(0);
				if (cr1.isTrueTail(tp)) { 
					
					int foundAt = -1;
					double lastConf = -1.0;
					for (int i = 0; i < cr2.getTails().size(); i++) {
						// System.out.print("(" + cr2.getTailConfidences().get(i) + ")" + cr2.getTails().get(i)  + " ");
						if (cr2.getTails().get(i).equals(tp)) {
							foundAt = i;
							break;
						}
						lastConf = cr2.getTailConfidences().get(i);
					}
				
					if (foundAt < 0) {
						
						//String tp2 = cr2.getTails().get(0);
						System.out.println("correct (in tail pos / in head pos):   " + train.getTriplesByTail(tp).size() + " | " + train.getTriplesByHead(tp).size());
						//System.out.println("anyburl#1: " + train.getTriplesByTail(tp2).size());
						//System.out.println("correct:   " + train.getTriplesByHead(tp).size());
						//System.out.println("anyburl#1: " + train.getTriplesByHead(tp2).size());						
						
						System.out.println(">>>" + cr1.getTripleAsString());
						
						System.out.println("must have confidence lower than: " + lastConf);
						System.out.println("---> ");
						for (int i = 0; i < cr2.getTails().size(); i++) {
							System.out.print("(" + cr2.getTailConfidences().get(i) + ")" + cr2.getTails().get(i)  + " ");
							

							
						}
						System.out.println();
						System.out.println("");
					}
				}
			}
			
		}
		
	}
	
	

}
