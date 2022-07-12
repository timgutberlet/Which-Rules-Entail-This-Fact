package de.unima.ki.anyburl.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.unima.ki.anyburl.data.TripleSet;
// import de.unima.ki.anyburl.rescore.AlphaBeta;

public class ResultSet implements Iterable<CompletionResult>{
	
	public HashMap<String, CompletionResult> results;
	public String name;
	
	public static boolean applyThreshold = false;
	public static double threshold = 0.0;
	
	private boolean containsConfidences = false;
	
	public static void main(String[] args) throws FileNotFoundException {
		

		LinkedHashMap<String, Double> map = new LinkedHashMap<String, Double>();
		
		map.put("a", 0.8);
		map.put("b", 0.7);
		map.put("c", 0.9);
		map.put("d", 0.1);
		map.put("e", 2.7);
		map.put("f", 0.88);
		map.put("g", 0.1222);
		
		
		orderByValueDescending(map);
		
		for (Map.Entry<String, Double> e : map.entrySet()) {
			System.out.println(e);
		}
		
		// ResultSet rs = new ResultSet("../KBCTestdataGenerator/experiments/anyburl/numbers-d3-08-p08-p04-p00-uc-predictions", true, 10);
		//for (CompletionResult cr : rs) {
		//	System.out.println(cr);
		//}
	}
	

	
	
	public ResultSet(ResultSet that, String relation) {
		this.containsConfidences = that.containsConfidences;
		this.results = new HashMap<String, CompletionResult>();
		for (String triple :  that.results.keySet()) {
			if (triple.split(" ")[1].equals(relation)) {
				this.results.put(triple, that.results.get(triple));
			}
		}
	}
	
	
	public ResultSet() {
		this.results = new HashMap<String, CompletionResult>();
	}
	
	public ResultSet(String name, String filePath) {
		this(name, filePath, false, 0);
		
	}
	
	public ResultSet(String name, boolean containsConfidences, int k) {
		this(name, name, containsConfidences, k);
	}
	
	public CompletionResult getCompletionResult(String triple) {
		CompletionResult cr = this.results.get(triple);
		return cr;
	}
	
	public Set<String> getTriples() {
		return this.results.keySet();
	}
	
	public ResultSet(String name, String filePath, boolean containsConfidences, int k, boolean adjust) {
		this(name, filePath, containsConfidences, k);
		if (adjust) this.adjust();
	}
	
	public ResultSet(String name, String filePath, boolean containsConfidences, int k) {
		System.out.println("* loading result set at " +  filePath);
		this.containsConfidences = containsConfidences;
		this.name = name;
		this.results = new HashMap<String, CompletionResult>();
		long counter = 0;
		long stepsize = 100000; 
		File file = null;
		try {
			file = new File(filePath);
			// FileReader fileReader = new FileReader(file);
			
			// FileInputStream i = null;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			String tripleLine;
			while ((tripleLine = bufferedReader.readLine()) != null) {
				counter++;
				if (counter % stepsize == 0) System.out.println("* parsed " + counter + " lines of results file");
				if (tripleLine.length() < 3) continue;
				CompletionResult cr = new CompletionResult(tripleLine);
				String headLine = bufferedReader.readLine();
				String tailLine = bufferedReader.readLine();
				String tempLine = "";
				if (headLine.startsWith("Tails:")) {
					// System.out.println("reversed");
					tempLine = headLine;
					headLine = tailLine;
					tailLine = tempLine;
				}
				if (!applyThreshold) {
					cr.addHeadResults(getResultsFromLine(headLine.substring(7)), k);
					cr.addHeadConfidences(getConfidencesFromLine(headLine.substring(7)), k);
					cr.addTailResults(getResultsFromLine(tailLine.substring(7)), k);
					cr.addTailConfidences(getConfidencesFromLine(tailLine.substring(7)), k);
				}
				else {
					cr.addHeadResults(getThresholdedResultsFromLine(headLine.substring(7)), k);
					cr.addHeadConfidences(getThresholdedConfidencesFromLine(headLine.substring(7)), k);
					cr.addTailResults(getThresholdedResultsFromLine(tailLine.substring(7)), k);
					cr.addTailConfidences(getThresholdedConfidencesFromLine(tailLine.substring(7)), k);
				}
				this.results.put(tripleLine.split("\t")[0], cr);
			}
			bufferedReader.close();
		}
		catch (IOException e) {
			System.err.println("problem related to file " + file + ".");
			e.printStackTrace();
		}
	}
	
	public void extendWith(ResultSet rs, int k, double factor) {
		for (String t : this.results.keySet()) {
			CompletionResult thisResult = this.results.get(t);			
			CompletionResult thatResult = rs.results.get(t);
			thisResult.extendWith(thatResult, k, factor);
		}
	}
	
	public void printAsTripleSet(String path) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(path);
		for (String line : this.results.keySet()) {
			String[] token  = line.split("\\s+");
			CompletionResult cr = this.results.get(line);
			int i = 0;
			for (String h : cr.getHeads()) {
				pw.println(h + " " + token[1] + " " + token[2]);
				i++;
			}
			i = 0;
			for (String t : cr.getTails()) {
				pw.println(token[0] + " " + token[1] + " " + t);
				i++;
			}
			pw.flush();
		}
		pw.close();

	}
	
	public void printAsWeightedTripleSet(String path) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(path);
		for (String line : this.results.keySet()) {
			String[] token  = line.split("\\s+");
			CompletionResult cr = this.results.get(line);
			int i = 0;
			for (String h : cr.getHeads()) {
				double dh = cr.getHeadConfidences().get(i);
				pw.println(h + "\t" + token[1] + "\t" + token[2] + "\t" + dh);
				i++;
			}
			i = 0;
			for (String t : cr.getTails()) {
				double dt = cr.getTailConfidences().get(i);
				pw.println(token[0] + "\t" + token[1] + "\t" + t + "\t" + dt);
				i++;
			}
			pw.flush();
		}
		pw.close();

	}
	
	
	
	private String[] getThresholdedResultsFromLine(String rline) {
		if (!containsConfidences) {
			return rline.split("\t");
		}
		else {
			String t = "";
			String cS = "";
			String[] token = rline.split("\t");
			// String[] tokenx = new String[token.length / 2];
			ArrayList<String> tokenx = new ArrayList<String>();
			for (int i = 0; i < token.length / 2; i++) {
				
				t = token[i*2];
				cS = token[i*2 + 1];
				double c = Double.parseDouble(cS);
				if (c > threshold) {
					tokenx.add(t);
				}
				else {
					break;
				}
			}
			String[] tokenxx = (String[]) tokenx.toArray(new String[0]);
			return tokenxx;
		}
	}
	
	private Double[] getThresholdedConfidencesFromLine(String rline) {
		if (!containsConfidences) {
			System.err.println("there are no confidences, you cannot retrieve them (line: " + rline + ")");
			return null;
		}
		else {
			String t = "";
			String cS = "";
			String[] token = rline.split("\t");
			// String[] tokenx = new String[token.length / 2];
			ArrayList<Double> tokenx = new ArrayList<Double>();
			for (int i = 0; i < token.length / 2; i++) {
				
				// t = token[i*2];
				cS = token[i*2 + 1];
				double c = Double.parseDouble(cS);
				if (c > threshold) {
					tokenx.add(c);
				}
				else {
					break;
				}
			}
			Double[] tokenxx = (Double[]) tokenx.toArray(new Double[0]);
			return tokenxx;
		}
	}
	
	private String[] getResultsFromLine(String rline) {
		if (!containsConfidences) {
			return rline.split("\t");
		}
		else {
			String[] token = rline.split("\t");
			String[] tokenx = new String[token.length / 2];
			for (int i = 0; i < tokenx.length; i++) {
				tokenx[i] = token[i*2];
			}
			return tokenx;
		}
	}
	
	private Double[] getConfidencesFromLine(String rline) {
		if (!containsConfidences) {
			System.err.println("there are no confidences, you cannot retrieve them (line: " + rline + ")");
			return null;
		}
		else {
			String[] token = rline.split("\t");
			Double[] tokenx = new Double[token.length / 2];
			
			for (int i = 0; i < tokenx.length; i++) {
				tokenx[i] = Double.parseDouble(token[i*2 + 1]);
			}
			return tokenx;
		}
	}

	public ArrayList<String> getHeadCandidates(String triple) {
		try {
			// System.out.println("head: " + triple);
			CompletionResult cr = this.results.get(triple);
			if (cr == null && triple.contains("\t")) triple.replaceAll("\t", " ");
			// else if (cr == null && triple.contains(" ")) triple.replaceAll(" ", "\t");
			// cr = this.results.get(triple);
			if (cr == null) {
				// System.out.println("ARGGG");
				// System.out.println(triple);
			}
			return cr.getHeads();
		}
		catch(RuntimeException e) {
			return new ArrayList<String>();
		}
	}
	
	
	
	public ArrayList<String> getTailCandidates(String triple) {
		// System.out.println("tail: " + triple);
		try {
			CompletionResult cr = this.results.get(triple);
			//if (cr == null && triple.contains("\t")) triple.replaceAll("\t", " ");
			//else if (cr == null && triple.contains(" ")) triple.replaceAll(" ", "\t");
			// cr = this.results.get(triple);
			if (cr == null) {
				System.err.println("cpould not find required triple (" + triple + ")in results set");
				for (String k: this.results.keySet()) {
					System.out.println(k);
				}
				System.exit(0);
			}
			return cr.getTails();
		}
		catch(RuntimeException e) {
			return new ArrayList<String>();
		}
	}
	
	
	public ArrayList<Double> getHeadConfidences(String triple) {
		try {
			CompletionResult cr = this.results.get(triple);
			return cr.getHeadConfidences();
		}
		catch(RuntimeException e) {
			return new ArrayList<Double>();
		}
	}
	
	public ArrayList<Double> getTailConfidences(String triple) {
		try {
			CompletionResult cr = this.results.get(triple);
			return cr.getTailConfidences();
		}
		catch(RuntimeException e) {
			return new ArrayList<Double>();
		}
	}

	public String getName() {
		return this.name;
	}

	public void supressConnected(TripleSet trainingSet) {
		for (String triple :this.results.keySet()) {
			CompletionResult cr = this.results.get(triple);
			cr.supressConnected(trainingSet);
		}
		
	}

	public Iterator<CompletionResult> iterator() {
		return this.results.values().iterator();
	}
	
	public void write(String filepath) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(filepath);
		
		for (CompletionResult cr : results.values()) {
			cr.write(pw);
		}	
		pw.flush();
		pw.close();
	}

	/**
	 * Reorders the candidates in this results set by the order of that result set.
	 * Each candidate from this result set, which is not specified in that result set is put to the end of the ranking.
	 * If there are several such candidates they are are ordered at the end according to their original ordering.
	 * 
	 * The scores that are used for the ordering constructed by by taking equdistant numbers from 1.0 to 0.5
	 * 
	 */
	public void reorder(ResultSet that) {
		
		for (String tripleAsString : this.results.keySet()) {
			CompletionResult thisCr = this.getCompletionResult(tripleAsString);
			CompletionResult thatCr = that.getCompletionResult(tripleAsString);
			
			CompletionResult reorderedCr = new CompletionResult(tripleAsString);

			
			ArrayList<String> reorderedHeads = new ArrayList<String>();
			ArrayList<Double> reorderedHeadConfidences = new ArrayList<Double>();
			reorderLists(reorderedHeads, reorderedHeadConfidences, thisCr.getHeads(), thatCr.getHeads());
			reorderedCr.setHeads(reorderedHeads);
			reorderedCr.setHeadConfidences(reorderedHeadConfidences);
			
			ArrayList<String> reorderedTails = new ArrayList<String>();
			ArrayList<Double> reorderedTailConfidences = new ArrayList<Double>();
			reorderLists(reorderedTails, reorderedTailConfidences, thisCr.getTails(), thatCr.getTails());
			reorderedCr.setTails(reorderedTails);
			reorderedCr.setTailConfidences(reorderedTailConfidences);
			
			this.results.put(tripleAsString, reorderedCr);
		}	
	}
	
	/**
	 * Reorders the candidates in this results by combining the confidences in this and that result set.
	 * The confidences in this result set are modified in the following way. For each candidate in this 
	 * result set, the position in that result set is determined. Let p be that position. Then the confidence
	 * of this candidate is multiplied by (1 + alpha)/(p + alpha). The higher alpha, the lower is the
	 * impact of the ordering in that result set.
	 * 
	 */
	public ResultSet reorderWeighted(ResultSet that, int alpha, double beta) {
		ResultSet rs = new ResultSet();
		for (String tripleAsString : this.results.keySet()) {
			CompletionResult thisCr = this.getCompletionResult(tripleAsString);
			CompletionResult thatCr = that.getCompletionResult(tripleAsString);
			
			CompletionResult reorderedCr = new CompletionResult(tripleAsString);

			
			ArrayList<String> reorderedHeads = new ArrayList<String>();
			ArrayList<Double> reorderedHeadConfidences = new ArrayList<Double>();
			reorderListsWeighted(reorderedHeads, reorderedHeadConfidences, thisCr.getHeads(), thisCr.getHeadConfidences(), thatCr.getHeads(), alpha, beta);
			reorderedCr.setHeads(reorderedHeads);
			reorderedCr.setHeadConfidences(reorderedHeadConfidences);
			
			ArrayList<String> reorderedTails = new ArrayList<String>();
			ArrayList<Double> reorderedTailConfidences = new ArrayList<Double>();
			reorderListsWeighted(reorderedTails, reorderedTailConfidences, thisCr.getTails(), thisCr.getTailConfidences(), thatCr.getTails(), alpha, beta);
			reorderedCr.setTails(reorderedTails);
			reorderedCr.setTailConfidences(reorderedTailConfidences);
			
			// this.results.put(tripleAsString, reorderedCr);
			
			rs.results.put(tripleAsString, reorderedCr);
			
		}
		return rs;
	}
	
	// is this one still in use?
	/*
	public ResultSet reorderWeighted(ResultSet that, HashMap<String, AlphaBeta> relation2AlphaBeta) {
		ResultSet rs = new ResultSet();
		for (String tripleAsString : this.results.keySet()) {
			
			String r = tripleAsString.split(" ")[1];
			int alpha = 0;
			double beta = 0;
			if (relation2AlphaBeta.containsKey(r)) {
				AlphaBeta ab = relation2AlphaBeta.get(r);
				beta =ab.beta;
			}
			else {
				System.out.println(">>> relation " + r + " has no alpha/beta scoring: set to alpha = 0. beta = 1.0");
				alpha = 0;
				beta = 1.0;
			}
			

			
			CompletionResult thisCr = this.getCompletionResult(tripleAsString);
			CompletionResult thatCr = that.getCompletionResult(tripleAsString);
			
			CompletionResult reorderedCr = new CompletionResult(tripleAsString);

			
			ArrayList<String> reorderedHeads = new ArrayList<String>();
			ArrayList<Double> reorderedHeadConfidences = new ArrayList<Double>();
			reorderListsWeighted(reorderedHeads, reorderedHeadConfidences, thisCr.getHeads(), thisCr.getHeadConfidences(), thatCr.getHeads(), alpha, beta);
			reorderedCr.setHeads(reorderedHeads);
			reorderedCr.setHeadConfidences(reorderedHeadConfidences);
			
			ArrayList<String> reorderedTails = new ArrayList<String>();
			ArrayList<Double> reorderedTailConfidences = new ArrayList<Double>();
			reorderListsWeighted(reorderedTails, reorderedTailConfidences, thisCr.getTails(), thisCr.getTailConfidences(), thatCr.getTails(), alpha, beta);
			reorderedCr.setTails(reorderedTails);
			reorderedCr.setTailConfidences(reorderedTailConfidences);
			
			// this.results.put(tripleAsString, reorderedCr);
			
			rs.results.put(tripleAsString, reorderedCr);
			
		}
		return rs;
	}
	*/
	

	
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
	
	private void reorderLists(ArrayList<String> reorderedCandidates, ArrayList<Double> reorderedConfidences, ArrayList<String> thisCandidates, ArrayList<String> thatCandidates) {
		HashSet<String> reorderedCandidatesHashed = new HashSet<String>();
		for (int i = 0; i < thatCandidates.size(); i++) {
			String candidate =  thatCandidates.get(i);
			if (thisCandidates.contains(candidate)) {
				reorderedCandidates.add(candidate);
				reorderedCandidatesHashed.add(candidate);
			}
		}
		for (int i = 0; i < thisCandidates.size(); i++) {
			String candidate =  thisCandidates.get(i);
			if (!reorderedCandidatesHashed.contains(candidate)) {
				reorderedCandidates.add(candidate);
			}
		}
		
		double stepsize = 0.5 / (double)reorderedCandidates.size();			
		for (int i = 0; i < reorderedCandidates.size(); i++) {
			reorderedConfidences.add(1.0 - (i * stepsize));
		}
	}
	
	private void reorderListsWeighted(ArrayList<String> reorderedCandidates, ArrayList<Double> reorderedConfidences, ArrayList<String> thisCandidates,  ArrayList<Double> thisConfidences, ArrayList<String> thatCandidates, int alpha, double beta) {
		
		LinkedHashMap<String, Double> map = new LinkedHashMap<String, Double>();
		
		for (int i = 0; i < thisCandidates.size(); i++) {
			String candidate =  thisCandidates.get(i);
			double givenConfidence =  thisConfidences.get(i);
			double thatFactor = 0.0;
			int indexInThat = thatCandidates.lastIndexOf(candidate);
			int pos = 0;
			if (indexInThat == -1) {
				thatFactor = (1.0 + alpha) / (double)(thatCandidates.size() + 1 + alpha);
				pos = thatCandidates.size() + 1;
				
			}
			else {
				thatFactor = (1.0 + alpha) / (double)(indexInThat + 1 + alpha);
				pos = indexInThat + 1;
			}
			// System.out.println();
			double score = beta * (givenConfidence * thatFactor) + (1.0 - beta) * (1.0 / pos); 
			// double score = givenConfidence * thatFactor;
			map.put(candidate, score);
		}
		orderByValueDescending(map);
		for (Map.Entry<String, Double> e : map.entrySet()) {
			reorderedCandidates.add(e.getKey());
			reorderedConfidences.add(e.getValue());
		}
		
	}
	
	/**
	 * Reorders the results set according to the specified confidences.
	 */
	public void adjust() {
		
		
		
		for (String task : this.results.keySet()) {
			
			
			CompletionResult cr = this.results.get(task);
			
			LinkedHashMap<String, Double> map = new LinkedHashMap<String, Double>();
			for (int i = 0; i < cr.getHeads().size(); i++) map.put(cr.getHeads().get(i), cr.getHeadConfidences().get(i));
			orderByValueDescending(map);
			
			ArrayList<String> reorderedCandidates = new ArrayList<String>();
			ArrayList<Double> reorderedConfidences = new ArrayList<Double>();
			for (Map.Entry<String, Double> e : map.entrySet()) {
				reorderedCandidates.add(e.getKey());
				reorderedConfidences.add(e.getValue());
			}
			cr.setHeads(reorderedCandidates);
			cr.setHeadConfidences(reorderedConfidences);
			
			
			map.clear();
			for (int i = 0; i < cr.getTails().size(); i++) map.put(cr.getTails().get(i), cr.getTailConfidences().get(i));
			orderByValueDescending(map);
			ArrayList<String> reorderedCandidates2 = new ArrayList<String>();
			ArrayList<Double> reorderedConfidences2 = new ArrayList<Double>();
			for (Map.Entry<String, Double> e : map.entrySet()) {
				reorderedCandidates2.add(e.getKey());
				reorderedConfidences2.add(e.getValue());
			}
			cr.setTails(reorderedCandidates2);
			cr.setTailConfidences(reorderedConfidences2);
			
		}
		
		


		
		
	}



	
	
	
	
	

}
