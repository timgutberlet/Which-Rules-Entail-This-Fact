package de.unima.ki.anyburl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unima.ki.anyburl.*;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.CompletionResult;
import de.unima.ki.anyburl.eval.ResultSet;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleAcyclic1;
import de.unima.ki.anyburl.structure.RuleCyclic;
import de.unima.ki.anyburl.structure.ScoreTree;

public class Explain {
	
	private static final int DURATION = 100;
	

	/**
	 * 
	 * @param args First command line argument should be the path where the target.txt is located and all output files will be written to
	 * Second argument is the folder of the dataset where train.txt, test.txt and valid.txt are located. 
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		
		String mainFolder = null;
		String dataFolder = null;
		
		int ruleLength = 2;
		
		
		if (args != null && args.length == 2) {
			mainFolder = args[0];
			dataFolder = args[1];
		}
		else {
			System.err.print("please specify the folder where target file and final results are stored followed by the folder that contains the dataset");
			System.exit(1);
		}

	
		File f1 = new File(mainFolder);
		if (!(f1.exists() && f1.isDirectory())) {
			System.err.println("there is no folder " + mainFolder);
			System.exit(1);
		}
		
		File f2 = new File(dataFolder);
		if (!(f2.exists() && f2.isDirectory())) {
			System.out.println("there is no folder " + dataFolder);
			System.exit(1);
		}
		
		
		// the prefix of the path where the rules will be stores, -DURATION will be added
		String rulePath = mainFolder + "/rules";
		// path to the training set used for learning and for the predictions
		String trainPath = dataFolder + "/train.txt";
		// some methods require a validation set as input, this is not requires, however an empty file needs to be specified
		String emptyPath = mainFolder + "/empty.txt";
		// path to the file that contains the triples for which an explanation is computed
		String targetPath = mainFolder + "/target.txt";
		// path to the final explanation of the target file (= the triples to be deleted)
		String outputDeletePath = mainFolder + "/delete.txt";
		String outputDeletePathVB = mainFolder + "/delete-verbose.txt";
		// path to the final explanation of the target file
		String outputAdditionPath = mainFolder + "/addition.txt";
		// path to temporary output of the predicted rankings, will be deleted afterwards
		String outputRankingsTmpPath = mainFolder + "/tmp_ranking.txt";
		
		
		System.out.println(">>> create empty file that that works a input of an empty validation set");
		PrintWriter epw = new PrintWriter(emptyPath);
		epw.print("");
		epw.close();
		
		
		System.out.println(">>> collect relations from the target file");
		TripleSet targetTS = new TripleSet(targetPath);
		Set<String> targetRelations = targetTS.getRelations();
		int counter = 0;
		String[] singleRelations = new String[targetRelations.size()];
		for (String r : targetRelations) singleRelations[counter++] = r;
		System.out.println(">>> collected " + singleRelations.length + " different relations");
		
		System.err.println(">>> SKIP rule learning");


		/* System.out.println(">>> learn rules for the previously collected relations");
		
		Settings.MAX_LENGTH_CYCLIC = ruleLength;
		Settings.PATH_TRAINING = trainPath;
		Settings.PATH_OUTPUT   = rulePath;
		Settings.SNAPSHOTS_AT = new int[]{DURATION};
		Settings.WORKER_THREADS = 12;
		Settings.SINGLE_RELATIONS = singleRelations;
		
		LearnReinforced.main(null);

		*/

		
		System.out.println(">>> apply previously learned rules");
		
		// prepare setting for running the Apply
		Settings.PATH_TRAINING  = trainPath;
		Settings.PATH_VALID     = emptyPath;
		Settings.PATH_TEST      = targetPath;
		Settings.PATH_RULES      = rulePath + "-" + DURATION;
		// thats the output destination for the rankings, not really needed, will be deleted afterwards
		Settings.PATH_OUTPUT     = outputRankingsTmpPath;
		Settings.PATH_EXPLANATION = "BLIND";
		// Settings.PATH_EXPLANATION = debugPath;
		Settings.TOP_K_OUTPUT = 100;
		
		// prepare data structures to store the score trees that contain the explaining rules
		HashMap<Triple, ScoreTree> explanationsHead = new HashMap<Triple, ScoreTree>();
		HashMap<Triple, ScoreTree> explanationsTail = new HashMap<Triple, ScoreTree>();
		

		
		// run the Apply and store the rule based explanations generated when doing this
		RuleEngine.listenToExplanations(explanationsHead, explanationsTail);

		long startTime = System.nanoTime();
		long elapsedTime;

		// Settings.REWRITE_REFLEXIV = false;
		Apply.main(null);

		// Settings.REWRITE_REFLEXIV = true;
		
		System.out.println(">>> ground the rules to find the explaining triples");
		
		TripleSet train = new TripleSet(trainPath);
		
		int explanationCounter = 0;
		HashMap<Triple, Rule> predictedTripleToExplainingRule = new HashMap<Triple, Rule>();
		HashMap<Triple, Triple> predictedTripleToExplainingTriple = new HashMap<Triple, Triple>();
		HashMap<Triple, Double> predictedTripleToConfidenceOfExplanation = new HashMap<Triple, Double>();
		
		ArrayList<Triple> targets = new ArrayList<Triple>();
		
		for (Triple target : explanationsTail.keySet()) {
			if (!targets.contains(getFixedTriple(target))) targets.add(getFixedTriple(target));
			ArrayList<Rule> rules = getExplainingRules(target, explanationsTail.get(target), false);
			if (rules.size() > 0) {
				Triple explanation = getExplainingTripleTail(target, train,  rules.get(0));
				if (explanation != null) {
					if (predictedTripleToExplainingTriple.containsKey(getFixedTriple(target))) {
						if (predictedTripleToConfidenceOfExplanation.get(getFixedTriple(target)) < rules.get(0).getAppliedConfidence()) {
							predictedTripleToExplainingTriple.put(getFixedTriple(target), explanation);
							predictedTripleToConfidenceOfExplanation.put(getFixedTriple(target), rules.get(0).getAppliedConfidence());
							predictedTripleToExplainingRule.put(getFixedTriple(target), rules.get(0));
						}
					}
					else {
						predictedTripleToExplainingTriple.put(getFixedTriple(target), explanation);
						explanationCounter++;
						predictedTripleToConfidenceOfExplanation.put(getFixedTriple(target), rules.get(0).getAppliedConfidence());
						predictedTripleToExplainingRule.put(getFixedTriple(target), rules.get(0));
					}
				}
			}
	
			// System.out.println("-------------------");
		}
		
		
		System.out.println(">>> collected "+ explanationCounter + " triples for attacking " + predictedTripleToExplainingTriple.keySet().size() + " predictions.");

		elapsedTime = System.nanoTime();
		System.out.println("");
		System.out.println("Gesamtzeit: " + ((elapsedTime - startTime) / 1000000) + " ms");
		System.out.println("Durchschnittszeit: " + (((elapsedTime - startTime) / 1000000) / targets.size()) + " ms");
		System.out.println("Abfragen: " + targets.size());
		System.out.println("");

		PrintWriter pw = new PrintWriter(outputDeletePath);
		for (Triple target : targets) {
			Triple explanation = predictedTripleToExplainingTriple.get(getFixedTriple(target));
			if (explanation != null) pw.println(explanation);
			else {
				explanation = getRandomTripleFromNeighborhood(getFixedTriple(target), train);
				if (explanation != null) {
					predictedTripleToExplainingTriple.put(getFixedTriple(target), explanation);
					pw.println(explanation);
				}
			}
		}
		pw.flush();
		pw.close();
		
		PrintWriter pw_vb = new PrintWriter(outputDeletePathVB);
		for (Triple target : targets) {
			Triple explanation = predictedTripleToExplainingTriple.get(getFixedTriple(target));
			if (explanation != null) {
				pw_vb.println(getFixedTriple(target) + "\t" + explanation + "\t" + predictedTripleToExplainingRule.get(getFixedTriple(target)));
			}
		}

		pw_vb.flush();
		pw_vb.close();
		
		System.out.println(">>> saved triples to be deleted in " + outputDeletePath);
		
		PrintWriter pwAdd = new PrintWriter(outputAdditionPath);
		pwAdd.print("");
		for (Triple target : targets) {
			Triple explanation = predictedTripleToExplainingTriple.get(getFixedTriple(target));
			Triple ruined = getRuinedTriple(getFixedTriple(explanation), getFixedTriple(target), train);
			if (explanation != null) pwAdd.println(ruined);
		}
		pwAdd.flush();
		pwAdd.close();
		System.out.println(">>> saved triples to be added in " + outputAdditionPath + "");
	

	}
	
	private static Triple getRandomTripleFromNeighborhood(Triple target, TripleSet train) {
		ArrayList<Triple> triplesH = new ArrayList<Triple>();
		ArrayList<Triple> triplesT = new ArrayList<Triple>();
		triplesH.addAll(train.getTriplesByHead(target.getHead()));
		triplesH.addAll(train.getTriplesByTail(target.getHead()));	
		triplesT.addAll(train.getTriplesByHead(target.getTail()));
		triplesT.addAll(train.getTriplesByTail(target.getTail()));	
		if (triplesH.size() + triplesT.size() == 0) return null;
		if (triplesT.size() == 0 || (triplesH.size() <= triplesT.size() && triplesH.size() > 0)) {
			Collections.shuffle(triplesH);
			return getFixedTriple(triplesH.get(0));
		}
		else {
			Collections.shuffle(triplesT);
			return getFixedTriple(triplesT.get(0));
		}
	}

	private static Triple getRuinedTriple(Triple explanation, Triple target, TripleSet train) {
		// the tail remains the same and the head is ruined
		Triple ruined = null;
		if (explanation.getTail().equals(target.getTail()) || explanation.getTail().equals(target.getHead())) {
			for (int i = 0; i < 50; i++) {
				String e = train.getRandomEntity();
				if (e.equals(Settings.REWRITE_REFLEXIV_TOKEN)) continue;
				ruined = new Triple(e, explanation.getRelation(), explanation.getTail());
				if (train.getEntities(explanation.getRelation(), e, true).size() == 0) return ruined;				
			}
			System.err.println("Coud not find an entioty that completely ruins the triple in 50 attempts!");
			System.err.println("Target triple: " + target + " Explanation: " + explanation + " Ruined: " + ruined);
			return ruined;
		}
		
		// the head remains the same and the tail is ruined
		if (explanation.getHead().equals(target.getTail()) || explanation.getHead().equals(target.getHead())) {
			for (int i = 0; i < 50; i++) {
				String e = train.getRandomEntity();
				if (e.equals(Settings.REWRITE_REFLEXIV_TOKEN)) continue;
				ruined = new Triple(explanation.getHead(), explanation.getRelation(), e);
				if (train.getEntities(explanation.getRelation(), e, false).size() == 0) return ruined;				
			}
			System.err.println("Coud not find an entity that completely ruins the triple in 50 attempts!");
			System.err.println("Target triple: " + target + " Explanation: " + explanation + " Ruined: " + ruined);
			return ruined;
		}
		// this case should not occur
		System.err.println("Something went wrong!");
		System.err.println("Target triple: " + target + " Explanation: " + explanation);
		System.exit(1);
		return null;
	}

	/**
	 * Rewrites a reflexive triple specified in the AnyBURL specific  formalization back into the "normal" representation.
	 * 
	 * @param triple An reflexive or irreflexive triple.
	 * @return The triple in normal representation.
	 */
	private static Triple getFixedTriple(Triple triple) {
		if (isRewrittenReflexiveTriple(triple)) {
			Triple fixed;
			boolean before = Settings.REWRITE_REFLEXIV;
			Settings.REWRITE_REFLEXIV = false;
			if (triple.getHead().equals(Settings.REWRITE_REFLEXIV_TOKEN)) fixed = new Triple(triple.getTail(), triple.getRelation(), triple.getTail());
			else fixed = new Triple(triple.getHead(), triple.getRelation(), triple.getHead());
			Settings.REWRITE_REFLEXIV = before;
			return fixed;
		}
		return triple;
	}
	
	
	private static boolean isRewrittenReflexiveTriple(Triple triple) {
		if (triple.getHead().equals(Settings.REWRITE_REFLEXIV_TOKEN) || triple.getTail().equals(Settings.REWRITE_REFLEXIV_TOKEN)) {
			return true;
		}
		return false;
	}
	
	

	/**
	 * Returns a triple that explains the prediction of the tail-entity o within a given triple s r o
	 * which is based on a given rule.
	 * 
	 * 
	 * @param target The triple for which the tail prediction has to be explained.
	 * @param train The training set which might be used to find that triple
	 * @param rule The rule that predicted the triple.
	 * @return
	 */
	private static Triple getExplainingTripleTail(Triple target, TripleSet train, Rule rule) {
		// covers the case of a cyclic rule r(X,Y) <= and the case of a 
		if (rule.getHead().getLeft().equals("X")) {
			if (rule instanceof RuleAcyclic1 && rule.bodysize() == 1) {
				String left = rule.getBodyAtom(0).getLeft();
				String right = rule.getBodyAtom(0).getRight();
				String relation = rule.getBodyAtom(0).getRelation();	
				left = left.equals("X") ? target.getHead() : left;
				right = right.equals("X") ? target.getHead() : right;
				Triple explanation = new Triple(left, relation, right);
				return explanation;
			}
			if (rule instanceof RuleCyclic) {
				HashSet<Triple> explanations = getExplainingTriples(target, train, (RuleCyclic)rule);
				return getOne(explanations);
			}
		}
		else { // 	= if not (rule.getHead().getLeft().equals("X")) {
			// the case of cyclic rules is covered in the upper branch
			if (rule instanceof RuleAcyclic1 && rule.bodysize() == 1) {
				String left = rule.getBodyAtom(0).getLeft();
				String right = rule.getBodyAtom(0).getRight();
				String relation = rule.getBodyAtom(0).getRelation();	
				left = left.equals("Y") ? target.getTail() : left;
				right = right.equals("Y") ? target.getTail() : right;
				Triple explanation = new Triple(left, relation, right);
				return explanation;
			}
		}
		return null;
	}

	private static HashSet<Triple> getSmallestNonEmpty(HashSet<Triple> triples1, HashSet<Triple> triples2) {
		if (triples1.size() == 0 && triples2.size() == 0) return null;
		if (triples1.size() == 0 && triples2.size() > 0) return triples2;
		if (triples2.size() == 0 && triples1.size() > 0) return triples1;
		return ((triples1.size() < triples2.size()) ? triples1 : triples2);
	}

	/**
	 * Returns one triple from a set of triples. If the input set is empty returns null.
	 * 
	 * @param triples An input set of triples.
	 * @return One of the triples.
	 */
	private static Triple getOne(HashSet<Triple> triples) {
		for (Triple triple : triples) {
			return triple;
		}
		return null;
	}

	/*
	private static Triple getExplainingTriple(Triple target, TripleSet train, RuleCyclic rule, boolean useFirstNotLastAtom) {
		int atomIndex = useFirstNotLastAtom ? 0 : rule.bodysize() - 1;
		String left = rule.getBodyAtom(atomIndex).getLeft();
		String right = rule.getBodyAtom(atomIndex).getRight();
		String relation = rule.getBodyAtom(atomIndex).getRelation();
		Triple explanation = null;
		if (useFirstNotLastAtom) {
			if (left.equals("X")) {
				if (rule.bodysize() == 1) explanation = new Triple(target.getHead(), relation, target.getTail());
				else {
					Set<String> candidates = train.getTailEntities(relation, target.getHead());
					if (candidates.size() == 1) {
						for (String candidate : candidates) explanation = new Triple(target.getHead(), relation, candidate);
					}
				}
			}
			if (right.equals("X")) {
				if (rule.bodysize() == 1) explanation = new Triple(target.getTail(), relation, target.getHead());
				else {
					Set<String> candidates = train.getHeadEntities(relation, target.getHead());
					if (candidates.size() == 1) {
						for (String candidate : candidates) explanation = new Triple(candidate, relation, target.getHead());
					}
				}
			}
		}
		else {
			if (left.equals("Y")) {
				Set<String> candidates = train.getTailEntities(relation, target.getTail());
				if (candidates.size() == 1) {
					for (String candidate : candidates) explanation = new Triple(target.getTail(), relation, candidate);
				}
			}
			if (right.equals("Y")) {
				Set<String> candidates = train.getHeadEntities(relation, target.getTail());
				if (candidates.size() == 1) {
					for (String candidate : candidates) explanation = new Triple(candidate, relation, target.getTail());
				}
			}
		}
		return explanation;
	}
	*/
	
	private static HashSet<Triple> getExplainingTriples(Triple target, TripleSet train, RuleCyclic rule) {

		String left = rule.getBodyAtom(0).getLeft();
		String right = rule.getBodyAtom(0).getRight();
		String relation = rule.getBodyAtom(0).getRelation();
		HashSet<Triple> explanations = new HashSet<Triple>();

		if (left.equals("X")) {
			if (rule.bodysize() == 1) explanations.add(new Triple(target.getHead(), relation, target.getTail()));
			if (rule.bodysize() == 2) {
				Set<String> candsA = train.getTailEntities(relation, target.getHead());
				for (String a : candsA) {
					if (rule.getBodyAtom(1).getRight().equals("Y")) {
						if (train.isTrue(a, rule.getBodyAtom(1).getRelation(), target.getTail())) explanations.add(new Triple(target.getHead(), relation, a));
					}
					if (rule.getBodyAtom(1).getLeft().equals("Y")) {
						if (train.isTrue(target.getTail(), rule.getBodyAtom(1).getRelation(), a)) explanations.add(new Triple(target.getHead(), relation, a));
					}
				}
			}
		}
		if (right.equals("X")) {
			if (rule.bodysize() == 1) explanations.add(new Triple(target.getTail(), relation, target.getHead()));
			if (rule.bodysize() == 2) {
				Set<String> candsA = train.getHeadEntities(relation, target.getHead());
				for (String a : candsA) {
					if (rule.getBodyAtom(1).getRight().equals("Y")) {
						if (train.isTrue(a, rule.getBodyAtom(1).getRelation(), target.getTail())) explanations.add(new Triple(a, relation, target.getHead()));
					}
					if (rule.getBodyAtom(1).getLeft().equals("Y")) {
						if (train.isTrue(target.getTail(), rule.getBodyAtom(1).getRelation(), a)) explanations.add(new Triple(a, relation, target.getHead()));
					}
				}
			}
		}
		return explanations;
	}
	
	private static ArrayList<Rule> getExplainingRules(Triple target, ScoreTree tree, boolean headNotTail) {
		String hit = headNotTail ? target.getHead() : target.getTail();
		ArrayList<Rule> rules = new ArrayList<Rule>();;
		HashMap<String, HashSet<Rule>> explainedCandidates = tree.getExplainedCandidates();
		for (String candidate : explainedCandidates.keySet()) {
			if (hit.equals(candidate)) rules.addAll(explainedCandidates.get(candidate));
		}
		Collections.sort(rules);
		return rules;
	}

	/*
	public static void sample100Hits() throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("exp/fb237/sample100.txt");
		
		// load the KGE results set and pick 100 randomly samples triples for which the correct entity has been ranked 
		ResultSet kgeRS = new ResultSet("complex", "E:/code/eclipse-workspace/AnyBURL/exp/understanding/fb237/complex-100-test", true, 100);
		
	
		ArrayList<String> tailHits = new ArrayList<String>();
		ArrayList<String> headHits = new ArrayList<String>();
		ArrayList<String> bothHits = new ArrayList<String>();
		for (CompletionResult cr : kgeRS) {
			String ts = cr.getTripleAsString();
			String[] token = ts.split("\\s+");
			String head = token[0];
			String tail = token[2];
			if (cr.getTails().get(0).equals(tail)) {
				tailHits.add(ts);
			}
			if (cr.getHeads().get(0).equals(head)) {
				headHits.add(ts);
			}
			if (cr.getTails().get(0).equals(tail) && cr.getHeads().get(0).equals(head)) {
				bothHits.add(ts);
			}
		}
		
		Collections.shuffle(headHits);
		Collections.shuffle(tailHits);
		Collections.shuffle(bothHits);
		
		System.out.println("head: " + headHits.size() + " tail: " + tailHits.size() + " both: " + bothHits.size());

		for (int i = 0; i < 100; i++) {
			pw.println(bothHits.get(i));
		}
		
		pw.flush();
		pw.close();
	}
	*/
	

}
