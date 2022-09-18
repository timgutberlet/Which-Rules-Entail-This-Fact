package de.unima.ki.anyburl.rescore;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;

public class Join {

	public static void main(String[] args) throws FileNotFoundException {
		
		
		
		
		
		
		ResultSet rs1 = new ResultSet("complex1", "exp/understanding/wn18rr/distmult1-100-test", true, 100);
		ResultSet rs2 = new ResultSet("complex2", "exp/understanding/wn18rr/distmult2-100-test", true, 100);
		ResultSet rs3 = new ResultSet("complex3", "exp/understanding/wn18rr/distmult3-100-test", true, 100);
		ResultSet rs4 = new ResultSet("complex2", "exp/understanding/wn18rr/distmult4-100-test", true, 100);
		ResultSet rs5 = new ResultSet("complex3", "exp/understanding/wn18rr/distmult5-100-test", true, 100);
		
		ResultSet rsJoined = new ResultSet();
		
		String outputPath = "exp/understanding/wn18rr/distmultJ-100-test";
		
		for (String triple : rs1.getTriples()) {
			
			
			
			CompletionResult cr1 = rs1.getCompletionResult(triple);
			CompletionResult cr2 = rs2.getCompletionResult(triple);
			CompletionResult cr3 = rs3.getCompletionResult(triple);
			
			CompletionResult cr4 = rs4.getCompletionResult(triple);
			CompletionResult cr5 = rs5.getCompletionResult(triple);
			
			CompletionResult crJoined = new CompletionResult(triple);
			
			ArrayList<CompletionResult> results = new ArrayList<CompletionResult>();
			results.add(cr1);
			results.add(cr2);
			results.add(cr3);
			results.add(cr4);
			results.add(cr5);
			
			LinkedHashMap<String, Double> heads = new LinkedHashMap<String, Double>();
			LinkedHashMap<String, Double> tails = new LinkedHashMap<String, Double>();
			
			for (CompletionResult cr : results) {
				for (int i = 0; i < cr.getHeads().size(); i++) {
					String h = cr.getHeads().get(i);
					double ch = cr.getHeadConfidences().get(i);
					if (heads.containsKey(h)) {
						double c = heads.get(h);
						heads.put(h, c + ch);
					}
					else heads.put(h, ch);
				}
			}
			
			
			for (CompletionResult cr : results) {
				for (int i = 0; i < cr.getTails().size(); i++) {
					String t = cr.getTails().get(i);
					double ct = cr.getTailConfidences().get(i);
					if (tails.containsKey(t)) {
						double c = tails.get(t);
						tails.put(t, c + ct);
					}
					else tails.put(t, ct);
				}
			}
			
			orderByValueDescending(heads);
			orderByValueDescending(tails);
			
			String[] headList = new String[heads.size()];
			String[] tailList = new String[tails.size()];
			Double[] headConfidenceList = new Double[heads.size()];
			Double[] tailConfidenceList = new Double[tails.size()];
			
			int i = 0;
			for (Entry<String, Double> e : heads.entrySet()) {
				String h = e.getKey();
				Double c = e.getValue();
				headList[i] = h;
				headConfidenceList[i] = c;
				i++;
			}
			i = 0;
			for (Entry<String, Double> e : tails.entrySet()) {
				String t = e.getKey();
				Double c = e.getValue();
				tailList[i] = t;
				tailConfidenceList[i] = c;
				// System.out.println(i + "x");
				
				i++;
			}
			crJoined.addHeadResults(headList, 0);
			crJoined.addHeadConfidences(headConfidenceList, 0);
			crJoined.addTailResults(tailList, 0);
			crJoined.addTailConfidences(tailConfidenceList, 0);
		
			rsJoined.results.put(triple, crJoined);
			
		}
		
		
		rsJoined.write(outputPath);
		

	}
	

	private static void orderByValueDescending(LinkedHashMap<String, Double> m) {
	    List<Map.Entry<String, Double>> entries = new ArrayList<>(m.entrySet());
	    

	    Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
	        @Override
	        public int compare(Map.Entry<String, Double> lhs, Map.Entry<String, Double> rhs) {
	        	if (lhs.getValue() - rhs.getValue() > 0) return -1;
	        	else {
	        		if (lhs.getValue() - rhs.getValue() == 0) return 0;
	        		else return 1;
	        	}
	        }
	    });

	    m.clear();
	    for(Map.Entry<String, Double> e : entries) {
	        m.put(e.getKey(), e.getValue());
	    }
	}

}
