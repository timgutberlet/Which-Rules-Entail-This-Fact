package de.unima.ki.anyburl;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
// import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

// import org.apache.commons.math3.util.Decimal64;

import java.util.Map.Entry;


import de.unima.ki.anyburl.data.*;
import de.unima.ki.anyburl.structure.*;
import de.unima.ki.anyburl.structure.compare.RuleConfidenceComparator;
import de.unima.ki.anyburl.threads.Predictor;

public class RuleEngine {

	private final static double EPSILON = 0.0001;
	
	
	private static LinkedList<Triple> predictionTasks = new LinkedList<Triple>();
	private static int predictionsMade = 0;
	private static PrintWriter predictionsWriter = null;
	
	private static int DEBUG_TESTSET_SUBSET = 0;
	
	private static HashMap<Triple, ScoreTree> explanationsHead = null;
	private static HashMap<Triple, ScoreTree> explanationsTail = null;

	public static void listenToExplanations(HashMap<Triple, ScoreTree> explHead, HashMap<Triple, ScoreTree> explTail ) {
		 explanationsHead = explHead;
		 explanationsTail = explTail;
	}
	
	public static void materializeRules(LinkedList<Rule> rules, TripleSet trainingSet, TripleSet materializedSet) {
		int ruleCounter = 0;
		
		for (Rule rule : rules) {
			ruleCounter++;
			if (ruleCounter % (rules.size() / 100) == 0) System.out.println("* " + (100.0 * (ruleCounter / (double)rules.size())) + "% of all rules materialized");
			if (rule.bodysize() > 2) continue;
			TripleSet materializedRule = rule.materialize(trainingSet);
			if (materializedRule != null) {
				// System.out.println(materializedRule.size());
				materializedSet.addTripleSet(materializedRule);
				// System.out.println(materializedSet.size());
			}
		}
	}
	
	
	public static void applyRulesARX(LinkedList<Rule> rules, TripleSet testSet, TripleSet trainingSet, TripleSet validationSet, int k, PrintWriter resultsWriter) {
		
		if (DEBUG_TESTSET_SUBSET > 0) {
			System.out.println("* debugging mode, choosing small fraction of testset");
			TripleSet testSetReduced = new TripleSet();
			for (int i = 0; i < DEBUG_TESTSET_SUBSET; i++) {
				Triple t = testSet.getTriples().get(i);
				testSetReduced.addTriple(t);
				
				
			}
			for (int i = DEBUG_TESTSET_SUBSET; i < testSet.getTriples().size(); i++) {
				Triple t = testSet.getTriples().get(i);
				validationSet.addTriple(t);
			}
			testSet = testSetReduced;
		}
		
		System.out.println("* applying " + rules.size()  + " rules");
		// HashMap<String, HashSet<Rule>> relation2Rules = createRuleIndex(rules);

		HashMap<String, ArrayList<Rule>> relation2Rules4Prediction = createOrderedRuleIndex(rules);
		
		
		HashMap<String, HashMap<Rule, Integer>>  relation2RulesIndex = null;
		
		if (Settings.AGGREGATION_ID == 3) {
			try {
				PrintWriter pw = new PrintWriter(Settings.PATH_RULE_INDEX);
				relation2RulesIndex = new HashMap<String, HashMap<Rule,Integer>>();
				for (String relation : relation2Rules4Prediction.keySet()) {
					pw.println(">>> " + relation);
					relation2RulesIndex.put(relation, new HashMap<Rule, Integer>());
					int index = 0;
					for (Rule rule : relation2Rules4Prediction.get(relation)) {
						pw.println(index + "\t" + rule);
						relation2RulesIndex.get(relation).put(rule, index);
						index++;
					}
					pw.flush();
				}
				pw.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		
		
		
		
		
		System.out.println("* set up index structure covering rules for head prediction for " + relation2Rules4Prediction.size() + " relations");
		// TripleSet filterSet_ = new TripleSet();
		// filterSet.addTripleSet(trainingSet);
		// filterSet.addTripleSet(validationSet);
		// filterSet.addTripleSet(testSet);
		// if (materializedSet != null) trainingSet.addTripleSet(materializedSet);
		
	
		
		// System.out.println("* constructed filter set with " + filterSet.getTriples().size() + " triples");
		// if (filterSet.getTriples().size() == 0) {
		// 	System.err.println("WARNING: using empty filter set!");
		// }
		// prepare the data structures used a s cache for question that are reoccuring
		// HashMap<SimpleImmutableEntry<String, String>, LinkedHashMap<String, Double>> headCandidateCache = new HashMap<SimpleImmutableEntry<String, String>, LinkedHashMap<String, Double>>();
		// HashMap<SimpleImmutableEntry<String, String>, LinkedHashMap<String, Double>> tailCandidateCache = new HashMap<SimpleImmutableEntry<String, String>, LinkedHashMap<String, Double>>();
		// start iterating over the test cases
		
		// int counter = 0;
		// long startTime = System.currentTimeMillis();
		// long currentTime = 0;
		
		
		ScoreTree.LOWER_BOUND = k;
		ScoreTree.UPPER_BOUND = ScoreTree.LOWER_BOUND;
		ScoreTree.EPSILON = EPSILON;

		
		predictionTasks.addAll(testSet.getTriples());
		predictionsWriter = resultsWriter;
		
		Thread[] predictors = new Thread[Settings.WORKER_THREADS];
		System.out.print("* creating worker threads ");
		for (int threadCounter = 0; threadCounter < Settings.WORKER_THREADS; threadCounter++) {
			System.out.print("#" + threadCounter + " ");
			predictors[threadCounter] = new Predictor(testSet, trainingSet, validationSet, k, relation2Rules4Prediction, relation2RulesIndex);
			predictors[threadCounter].start();
		}
		System.out.println();
		
		
		
		while (alive(predictors)) {
			try {
				Thread.sleep(500);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		predictionsWriter.flush();
		predictionsWriter.close();
		
		System.out.println("* done with rule application");
		RuleEngine.predictionsMade = 0;

	}
	
	private static boolean alive(Thread[] threads) {
		for (Thread t : threads) {
			if (t.isAlive()) return true;
		}
		return false;
	}
	
	
	
	public static synchronized Triple getNextPredictionTask()  {
		predictionsMade++;
		Triple triple = predictionTasks.poll();
		if (predictionsMade % 100 == 0) {
			if (triple != null) System.out.println("* (#" + predictionsMade + ") trying to guess the tail and head of " + triple.toString());
			predictionsWriter.flush();
		}
		return triple;
	}
	
	
	/*
	
	public static synchronized Rule getNextRuleMaterializationTask()  {
		Rule rule = ruleMaterializationTasksListed.poll();
		if (rule == null) return null;
		// ruleMaterializationTasks.remove(rule);
		return rule;
	}
	
	public static synchronized void addRuleToBeMaterialized(Rule rule)  {
		if (ruleMaterializationTasks.contains(rule)) return;
		ruleMaterializationTasksListed.add(rule);
		// System.out.println("list=" + ruleMaterializationTasksListed.size());
		ruleMaterializationTasks.add(rule);
		// System.out.println("set=" + ruleMaterializationTasks.size());
	}
	
	public static void materializeRule(Rule rule, TripleSet ts) {

		System.err.println("materialize rule: " + rule);
		TripleSet materializedSet = rule.materialize(ts);
		System.err.println("finished");
		
		synchronized (materializedRules) {
			materializedRules.put(rule, materializedSet);
			ruleMaterializationsMade++;
		}
		System.err.println("... and stored");
		
	}
	
	*/
	
	
	
	public static void predictMax(TripleSet testSet, TripleSet trainingSet, TripleSet validationSet, int k, HashMap<String, ArrayList<Rule>> relation2Rules4Prediction, Triple triple) {
		//System.out.println("=== " + triple + " ===");
		ScoreTree kTree = new ScoreTree();
		LinkedHashMap<String, Double> kTailCandidates = predictMax(testSet, trainingSet, validationSet, k, relation2Rules4Prediction, triple, false, kTree);
		ScoreTree kTailTree = kTree;
		kTree = new ScoreTree();

		LinkedHashMap<String, Double> kHeadCandidates = predictMax(testSet, trainingSet, validationSet, k, relation2Rules4Prediction, triple, true, kTree);
		ScoreTree kHeadTree = kTree;
		
		if (Settings.PATH_EXPLANATION != null) writeTopKExplanation(triple, testSet, kHeadCandidates, kHeadTree, kTailCandidates, kTailTree, k);

		writeTopKCandidates(triple, testSet, kHeadCandidates, kTailCandidates, predictionsWriter, k);
	}
	
	/*
	private static void replaceSelfByValue_(LinkedHashMap<String, Double> kCandidates, String value) {
		
		if (kCandidates.containsKey(Settings.REWRITE_REFLEXIV_TOKEN)) {
			double confidence = kCandidates.get(Settings.REWRITE_REFLEXIV_TOKEN);
			kCandidates.remove(Settings.REWRITE_REFLEXIV_TOKEN);
			kCandidates.put(value, confidence);
		}
	}
	*/

	
	// OLD VERSION: its completely unclear why the previous
	// there is not difference on FB15k between old and new version, thats why new should be preferred


	public static LinkedHashMap<String, Double> predictMaxOLD(TripleSet testSet, TripleSet trainingSet, TripleSet filterSet, int k, HashMap<String, ArrayList<Rule>> relation2Rules, Triple triple, boolean predictHeadNotTail, ScoreTree kTree) {

		String relation = triple.getRelation();
		String head = triple.getHead();
		String tail = triple.getTail();

		if (relation2Rules.containsKey(relation)) {
			ArrayList<Rule> relevantRules = relation2Rules.get(relation);
			
			Rule previousRule = null;
			Set<String> candidates = new HashSet<String>();
			Set<String> fCandidates = new HashSet<String>();
			
			for (Rule rule : relevantRules) {
				// long startTime = System.currentTimeMillis();
				if (previousRule != null) {
					if (predictHeadNotTail) candidates = previousRule.computeHeadResults(tail, trainingSet);
					else candidates = previousRule.computeTailResults(head, trainingSet);
					fCandidates.addAll(getFilteredEntities(trainingSet, filterSet, testSet, triple, candidates, !predictHeadNotTail)); // the negation seems to be okay here
					if (previousRule.getAppliedConfidence() > rule.getAppliedConfidence()) {
						if (!kTree.fine()) {
							if (fCandidates.size() > 0) {
								if (Settings.PATH_EXPLANATION != null) kTree.addValues(previousRule.getAppliedConfidence(), fCandidates, previousRule);
								else kTree.addValues(previousRule.getAppliedConfidence(), fCandidates, null);
								fCandidates.clear();
							}
						}
						else break;
					}
				}
				previousRule = rule;
			}
			
			if (!kTree.fine() && previousRule != null) {
				if (predictHeadNotTail) candidates = previousRule.computeHeadResults(tail, trainingSet);
				else candidates = previousRule.computeTailResults(head, trainingSet);
				fCandidates.addAll(getFilteredEntities(trainingSet, filterSet, testSet, triple, candidates, !predictHeadNotTail));
				if (Settings.PATH_EXPLANATION != null) kTree.addValues(previousRule.getAppliedConfidence(), fCandidates, previousRule);
				else kTree.addValues(previousRule.getAppliedConfidence(), fCandidates, null);
				fCandidates.clear();
			}
		}
		
		LinkedHashMap<String, Double> kCandidates = new LinkedHashMap<String, Double>();		
		kTree.getAsLinkedList(kCandidates, (predictHeadNotTail ? tail : head));
		
		return kCandidates;

	}
	
	
	// NEW SIMPLIFIED VERSION
	public static LinkedHashMap<String, Double> predictMax(TripleSet testSet, TripleSet trainingSet, TripleSet validationSet, int k, HashMap<String, ArrayList<Rule>> relation2Rules, Triple triple, boolean predictHeadNotTail, ScoreTree kTree) {

		String relation = triple.getRelation();
		String head = triple.getHead();
		String tail = triple.getTail();

		if (relation2Rules.containsKey(relation)) {
			ArrayList<Rule> relevantRules = relation2Rules.get(relation);
			Set<String> candidates = new HashSet<String>();
			Set<String> fCandidates = new HashSet<String>();
			for (Rule rule : relevantRules) {
				if (predictHeadNotTail) candidates = rule.computeHeadResults(tail, trainingSet);
				else candidates = rule.computeTailResults(head, trainingSet);
				fCandidates.addAll(getFilteredEntities(trainingSet, validationSet, testSet, triple, candidates, !predictHeadNotTail)); // the negation seems to be okay here
				if (!kTree.fine()) {
					if (fCandidates.size() > 0) {
						if (Settings.PATH_EXPLANATION != null) kTree.addValues(rule.getAppliedConfidence(), fCandidates, rule);
						else kTree.addValues(rule.getAppliedConfidence(), fCandidates, null);
						fCandidates.clear();
					}
				}
				else break;
			}
		}
		LinkedHashMap<String, Double> kCandidates = new LinkedHashMap<String, Double>();		
		kTree.getAsLinkedList(kCandidates, (predictHeadNotTail ? tail : head));
		
		return kCandidates;

	}
	
	
	public static void predictNoisyOr(TripleSet testSet, TripleSet trainingSet, TripleSet validationSet, int k, HashMap<String, ArrayList<Rule>> relation2Rules, Triple triple, boolean headNotTailPrediction) {
		System.err.println("noisy or prediction not yet re-implemented");
		String relation = triple.getRelation();
		String head = triple.getHead();
		String tail = triple.getTail();
		
		HashMap<String, HashSet<Rule>> explainedTailCandidates = new HashMap<String, HashSet<Rule>>();
		HashMap<String, HashSet<Rule>> explainedHeadCandidates = new HashMap<String, HashSet<Rule>>();
		
		if (relation2Rules.containsKey(relation)) {
			ArrayList<Rule> relevantRules = relation2Rules.get(relation);
			for (Rule rule : relevantRules) {
				
				HashSet<String> tailCandidates = rule.computeTailResults(head, trainingSet);
				HashSet<String> fTailCandidates = getFilteredEntities(trainingSet, validationSet, testSet, triple, tailCandidates, true);
				for (String fTailCandidate : fTailCandidates) {
					if (!explainedTailCandidates.containsKey(fTailCandidate)) explainedTailCandidates.put(fTailCandidate, new HashSet<Rule>());
					explainedTailCandidates.get(fTailCandidate).add(rule);
				}
				
				HashSet<String> headCandidates = rule.computeHeadResults(tail, trainingSet);
				HashSet<String> fHeadCandidates = getFilteredEntities(trainingSet, validationSet, testSet, triple, headCandidates, false);
				for (String fHeadCandidate : fHeadCandidates) {
					if (!explainedHeadCandidates.containsKey(fHeadCandidate)) explainedHeadCandidates.put(fHeadCandidate, new HashSet<Rule>());
					explainedHeadCandidates.get(fHeadCandidate).add(rule);
				}
				
				
			}

		}
		
		LinkedHashMap<String, Double> kTailCandidates = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> kHeadCandidates = new LinkedHashMap<String, Double>();
	
		computeNoisyOr(explainedTailCandidates, kTailCandidates);
		computeNoisyOr(explainedHeadCandidates, kHeadCandidates);
		
		// final sorting
		sortByValue(kTailCandidates);
		sortByValue(kHeadCandidates);
	
		
		// if (Settings.PATH_EXPLANATION != null) writeTopKExplanation(triple, testSet, kHeadCandidates, explainedHeadCandidates, kTailCandidates, explainedTailCandidates, k);
		writeTopKCandidates(triple, testSet, kHeadCandidates, kTailCandidates, predictionsWriter, k);

	}
	

	/**
	 * Creates a special type of output for Patrick.
	 *  
	 * @param testSet
	 * @param trainingSet
	 * @param filterSet
	 * @param k
	 * @param relation2Rules
	 * @param triple
	 * @param headNotTailPrediction
	 */
	public static void predictMaxPlusSpecial(TripleSet testSet, TripleSet trainingSet, TripleSet validationSet, HashMap<String, ArrayList<Rule>> relation2Rules, Triple triple,  HashMap<String, HashMap<Rule,Integer>> relation2RulesIndex) {
		
		// System.out.println("predict Top R");
		String relation = triple.getRelation();
		String head = triple.getHead();
		String tail = triple.getTail();
		
		int TOP_K = (Settings.TOP_K_OUTPUT < Settings.TOP_K_OUTPUT_SAMPLING_FROM) ? Settings.TOP_K_OUTPUT_SAMPLING_FROM : Settings.TOP_K_OUTPUT;
		
		HashMap<String, ArrayList<Rule>> explainedTailCandidates = new HashMap<String, ArrayList<Rule>>();
		HashMap<String, ArrayList<Rule>> explainedHeadCandidates = new HashMap<String, ArrayList<Rule>>();
		
		if (Settings.EXPLAIN_HIT == 1) {
			explainedTailCandidates.put(tail, new ArrayList<Rule>());
			explainedHeadCandidates.put(head, new ArrayList<Rule>());
		}
		
		ArrayList<Rule> relevantRules = null;
		if (relation2Rules.containsKey(relation)) {
			relevantRules = relation2Rules.get(relation);
			
			for (Rule rule : relevantRules) {
				
				HashSet<String> tailCandidates = rule.computeTailResults(head, trainingSet);
				HashSet<String> fTailCandidates = getFilteredEntities(trainingSet, validationSet, testSet, triple, tailCandidates, true);
				int xtcSize = explainedTailCandidates.size();
				for (String fTailCandidate : fTailCandidates) {
					if (xtcSize >= TOP_K) {
						if (explainedTailCandidates.containsKey(fTailCandidate)) {
							explainedTailCandidates.get(fTailCandidate).add(rule);
						}
					}
					else {
						if (!explainedTailCandidates.containsKey(fTailCandidate)) explainedTailCandidates.put(fTailCandidate, new ArrayList<Rule>());
						explainedTailCandidates.get(fTailCandidate).add(rule);
					}
				}
				
				

				HashSet<String> headCandidates = rule.computeHeadResults(tail, trainingSet);
				HashSet<String> fHeadCandidates = getFilteredEntities(trainingSet, validationSet, testSet, triple, headCandidates, false);
				int xhcSize = explainedHeadCandidates.size();
				for (String fHeadCandidate : fHeadCandidates) {
					if (xhcSize >= TOP_K) {
						if (explainedHeadCandidates.containsKey(fHeadCandidate)) {
							explainedHeadCandidates.get(fHeadCandidate).add(rule);
						}
					}
					else {
						if (!explainedHeadCandidates.containsKey(fHeadCandidate)) explainedHeadCandidates.put(fHeadCandidate, new ArrayList<Rule>());
						explainedHeadCandidates.get(fHeadCandidate).add(rule);
					}
				}
			}

		}
		
		LinkedHashMap<String, Double> kTailCandidates = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> kHeadCandidates = new LinkedHashMap<String, Double>();
	
		computeBruteForceMaxPlus(explainedTailCandidates, kTailCandidates);
		computeBruteForceMaxPlus(explainedHeadCandidates, kHeadCandidates);
		
		if (Settings.TOP_K_OUTPUT_SAMPLING_FROM > 0) {
			
			sortByValue(kTailCandidates);
			sortByValue(kHeadCandidates);
			
			kTailCandidates = sampleTopK(kTailCandidates, tail, Settings.TOP_K_OUTPUT, false);
			kHeadCandidates = sampleTopK(kHeadCandidates, head, Settings.TOP_K_OUTPUT, true);
		}

		if (Settings.EXPLAIN_HIT == 1) {
			sortByValue(kTailCandidates, tail, Settings.TOP_K_OUTPUT);
			sortByValue(kHeadCandidates, head, Settings.TOP_K_OUTPUT);
		}
		else {
			sortByValue(kTailCandidates);
			sortByValue(kHeadCandidates);
			
		}
	
		
		// if (Settings.PATH_EXPLANATION != null) writeTopKExplanation(triple, testSet, kHeadCandidates, explainedHeadCandidates, kTailCandidates, explainedTailCandidates, k);
		if (Settings.AGGREGATION_ID == 5) {
			writeTopKCandidates(triple, testSet, kHeadCandidates, kTailCandidates, predictionsWriter, Settings.TOP_K_OUTPUT);
		}
		else {
			writeTopKCandidatesWithRuleIndex(triple, testSet, kHeadCandidates, explainedHeadCandidates, kTailCandidates, explainedTailCandidates, predictionsWriter, Settings.TOP_K_OUTPUT, relation2RulesIndex);
		}

	}
	
	



	/**
	 * Some test method for trying out new simple types of aggregation.
	 *  
	 * @param testSet
	 * @param trainingSet
	 * @param filterSet
	 * @param k
	 * @param relation2Rules
	 * @param triple
	 * @param headNotTailPrediction
	 */
	public static void predictSymbolic(TripleSet testSet, TripleSet trainingSet, TripleSet validationSet, int k, HashMap<String, ArrayList<Rule>> relation2Rules, Triple triple,  HashMap<String, HashMap<Rule,Integer>> relation2RulesIndex) {
		
		int MAX_EXPLANATIONS = 500;
		
		// System.out.println("predict Top R");
		String relation = triple.getRelation();
		String head = triple.getHead();
		String tail = triple.getTail();
		
		HashMap<String, HashSet<Rule>> explainedTailCandidates = new HashMap<String, HashSet<Rule>>();
		HashMap<String, HashSet<Rule>> explainedHeadCandidates = new HashMap<String, HashSet<Rule>>();
		
		ArrayList<Rule> relevantRules = null;
		if (relation2Rules.containsKey(relation)) {
			relevantRules = relation2Rules.get(relation);

			//HashSet<String> fullyExplainedTailCandidates = new HashSet<String>();
			//HashSet<String> fullyExplainedHeadCandidates = new HashSet<String>();
			
			for (Rule rule : relevantRules) {
				
				// if (fullyExplainedHeadCandidates.size() >= Settings.TOP_K_OUTPUT && fullyExplainedTailCandidates.size() >= Settings.TOP_K_OUTPUT) break;
				// if (!(rule instanceof RuleAcyclic1)) continue;

				
				// if (explainedTailCandidates.size() < Settings.TOP_K_OUTPUT) {
				HashSet<String> tailCandidates = rule.computeTailResults(head, trainingSet);
				

				
				HashSet<String> fTailCandidates = getFilteredEntities(trainingSet, validationSet, testSet, triple, tailCandidates, true);
				for (String fTailCandidate : fTailCandidates) {
					//if (explainedTailCandidates.size() >= Settings.TOP_K_OUTPUT) {
					//	if (explainedTailCandidates.containsKey(fTailCandidate)) {
					//		if (explainedTailCandidates.get(fTailCandidate).size() < MAX_EXPLANATIONS)  explainedTailCandidates.get(fTailCandidate).add(rule);
					//		else fullyExplainedTailCandidates.add(fTailCandidate);
					//	}
					//}
					//else {
						if (!explainedTailCandidates.containsKey(fTailCandidate)) explainedTailCandidates.put(fTailCandidate, new HashSet<Rule>());
						if (explainedTailCandidates.get(fTailCandidate).size() < MAX_EXPLANATIONS)  explainedTailCandidates.get(fTailCandidate).add(rule);
					//}
				}
				// }
				
				// if (explainedHeadCandidates.size() < Settings.TOP_K_OUTPUT) {
				HashSet<String> headCandidates = rule.computeHeadResults(tail, trainingSet);

				HashSet<String> fHeadCandidates = getFilteredEntities(trainingSet, validationSet, testSet, triple, headCandidates, false);
				for (String fHeadCandidate : fHeadCandidates) {
					// if (explainedHeadCandidates.size() >= Settings.TOP_K_OUTPUT) {
					//	if (explainedHeadCandidates.containsKey(fHeadCandidate)) {
					//		if (explainedHeadCandidates.get(fHeadCandidate).size() < MAX_EXPLANATIONS)  explainedHeadCandidates.get(fHeadCandidate).add(rule);
					//		else fullyExplainedHeadCandidates.add(fHeadCandidate);
					//	}
					//}
					//else {
						if (!explainedHeadCandidates.containsKey(fHeadCandidate)) explainedHeadCandidates.put(fHeadCandidate, new HashSet<Rule>());
						if (explainedHeadCandidates.get(fHeadCandidate).size() < MAX_EXPLANATIONS)  explainedHeadCandidates.get(fHeadCandidate).add(rule);
					//}
				}
			
				
			}

		}
		
		// System.out.println(triple + " => ");
		LinkedHashMap<String, Double> kTailCandidates = new LinkedHashMap<String, Double>();
		LinkedHashMap<String, Double> kHeadCandidates = new LinkedHashMap<String, Double>();
	
		// System.out.println("tails => ");
		computeNoisyOrSymbolic(explainedTailCandidates, kTailCandidates);
		// System.out.println("heads => ");
		computeNoisyOrSymbolic(explainedHeadCandidates, kHeadCandidates);
		
		// System.out.println("");
		
		// computeBruteForceMax(explainedTailCandidates, kTailCandidates);
		// computeBruteForceMax(explainedHeadCandidates, kHeadCandidates);
		
		// final sorting
		sortByValue(kTailCandidates);
		sortByValue(kHeadCandidates);
	
		
		// if (Settings.PATH_EXPLANATION != null) writeTopKExplanation(triple, testSet, kHeadCandidates, explainedHeadCandidates, kTailCandidates, explainedTailCandidates, k);
		writeTopKCandidates(triple, testSet, kHeadCandidates, kTailCandidates, predictionsWriter, k);
		
		// writeTopKCandidatesWithRuleIndex(triple, testSet, kHeadCandidates, explainedHeadCandidates, kTailCandidates, explainedTailCandidates, predictionsWriter, k, relation2RulesIndex);

	}
	



	// TODO this is wrong, needs to be fixed
	private static void computeNoisyOr(HashMap<String, HashSet<Rule>> allCandidates, LinkedHashMap<String, Double> kCandidates) { 
		for (String cand : allCandidates.keySet()) {
			double best = 0.0;
			for (Rule r : allCandidates.get(cand)) {
				double current = r.getAppliedConfidence();
				if (current > best) {
					best = current;
				}
			}
			kCandidates.put(cand, best);
		}
		
	}
	
	// TODO this is experimental for the new method symbolic
	private static void computeBruteForceMaxPlus(HashMap<String, ArrayList<Rule>> allCandidates, LinkedHashMap<String, Double> kCandidates) { 

		for (String cand : allCandidates.keySet()) {
			
		
			
			ArrayList<Rule> rulesSorted = new ArrayList<Rule>();
			rulesSorted.addAll(allCandidates.get(cand));
			
			Collections.sort(rulesSorted);
			
			// System.out.print(cand + " : ");
			double x = 1.0;
			// if (allCandidates.get(cand).size() > 7) System.out.println();
			int i = 0;
			double total = 0.0;
			for (Rule r : rulesSorted) {
				double current = r.getAppliedConfidence();
				total += current *  Math.pow(ScoreTree.EPSILON, (double)i);
				i++;
				// if (cand.equals("Q395")) System.out.println(cand + "::: " + total); 
				// if (i > 10) break;
			}
			kCandidates.put(cand, total);
			
			// System.out.println(cand + ": " + total); 
			
		}
		// System.out.println();
		
	}
	

	private static void computeBruteForceMax(HashMap<String, HashSet<Rule>> allCandidates, LinkedHashMap<String, Double> kCandidates) { 
		for (String cand : allCandidates.keySet()) {
			double best = 0.0;
			for (Rule r : allCandidates.get(cand)) {
				double current = r.getAppliedConfidence();
				if (current > best) {
					best = current;
				}
			}
			kCandidates.put(cand, best);
		}
	}
	
	
	// TODO this is experimental for the new method symbolic
	private static void computeNoisyOrSymbolic(HashMap<String, HashSet<Rule>> allCandidates, LinkedHashMap<String, Double> kCandidates) { 

		
		
		for (String cand : allCandidates.keySet()) {
			
			
			ArrayList<Rule> rulesSorted = new ArrayList<Rule>();
			rulesSorted.addAll(allCandidates.get(cand));
			
			Collections.sort(rulesSorted);
			
			// System.out.print(cand + " : ");
			double x = 1.0;
			// if (allCandidates.get(cand).size() > 7) System.out.println();
			int i = 0;
			double total = 0.0;
			
			double xlg = Math.log(1.0);
			
			
			for (Rule r : rulesSorted) {
				double current = r.getAppliedConfidence();
				// total += 0.01;
				// x = x * (1.0 - current / Math.pow(2, (double)i));
				// x = x * (1.0 - current);
				xlg = xlg + Math.log(1.0 - current);
				i += 1;
				// if (i == 10) break;  // System.out.print(x + "  ");
			}
			
			
		
			kCandidates.put(cand, - xlg);
			
			// kCandidates.put(cand, 1.0 - x);
			// kCandidates.put(cand, total);
		}
		//System.out.println();
		
	}
	

	/*
	private static void show(LinkedHashMap<String, Double> kCandidates, String headline) {
		System.out.println("*** " + headline + " ***");
		for (String candidate : kCandidates.keySet()) {
			double conf = kCandidates.get(candidate);
			System.out.println(conf + " = " + candidate);
		}
	}

	private static HashMap<String, HashSet<Rule>> createRuleIndex(List<Rule> rules) {
		
		// int counterL1C = 0;
		// int counterL2C = 0;
		// int counterL1AC = 0;
		// int counterL1AN = 0;
		// int counterOther = 0;
		
		HashMap<String, HashSet<Rule>> relation2Rules = new HashMap<String, HashSet<Rule>>();
		for (Rule rule : rules) {
			
			
			
			if (rule.isXYRule()) {
				if (rule.bodysize() == 1)  counterL1C++;
				if (rule.bodysize() == 2)  counterL2C++;
			}
			else {
				
				if (rule.bodysize() == 1)  {
					if (rule.hasConstantInBody()) counterL1AC++;
					else counterL1AN++;
				}
				else {
					if (rule.hasConstantInBody()) continue;
				}	
			}
			
			
			String relation = rule.getTargetRelation();
			if (!relation2Rules.containsKey(relation)) relation2Rules.put(relation, new HashSet<Rule>());
			relation2Rules.get(relation).add(rule);
			
			
		}
		// System.out.println("L1C=" + counterL1C + " L2C=" + counterL2C + " L1AC=" + counterL1AC + " L1AN=" + counterL1AN + " OTHER=" + counterOther);
		return relation2Rules;
	}
	*/
	
	
	public static HashMap<String, ArrayList<Rule>> createOrderedRuleIndex(LinkedList<Rule> rules) {
		// String predictionGoal = headNotTailPrediction ? "head" : "tail";
		HashMap<String, ArrayList<Rule>> relation2Rules = new HashMap<String, ArrayList<Rule>>();
		long l = 0;
		for (Rule rule : rules) {
		
			if (Settings.THRESHOLD_CORRECT_PREDICTIONS > rule.getCorrectlyPredicted()) continue;
			if (Settings.THRESHOLD_CONFIDENCE > rule.getConfidence()) continue;
			
			String relation = rule.getTargetRelation();
			if (!relation2Rules.containsKey(relation)) {
				relation2Rules.put(relation, new ArrayList<Rule>());
			}
			relation2Rules.get(relation).add(rule);	
			if (l % 100000 == 0 && l > 1) {
				System.out.println("* indexed " + l + " rules for prediction");
			}
			l++;
		}
		for (String relation : relation2Rules.keySet()) {
			relation2Rules.get(relation).trimToSize();
			Collections.sort(relation2Rules.get(relation), new RuleConfidenceComparator());
		}
		System.out.println("* indexed and sorted " + l + " rules for using them to make predictions");
		return relation2Rules;
	}


	/*
	private static void updateCandidateProbabailities(Rule rule, boolean tailNotHead, String candidate, HashMap<String, Double> candidates2Probabilities) {
		double prob = rule.getAppliedConfidence();
		if (!candidates2Probabilities.containsKey(candidate)) candidates2Probabilities.put(candidate, prob);
		else {
			double previousProb = candidates2Probabilities.get(candidate);
			double newProb = combineProbability(prob, previousProb);
			candidates2Probabilities.put(candidate, newProb);
		}
	}
	*/
	
	/*
	private static LinkedHashMapK getFilteredCandidates(TripleSet filterSet, TripleSet testSet, Triple t, HashMap<String, Double> candidates, boolean tailNotHead) {
		// LinkedHashMap<String, Double> candidatesSorted = sortByValue(candidates);
		LinkedHashMap<String, Double> kCandidates = new LinkedHashMap<String, Double>();
		int i = 0;
		for (Entry<String, Double> entry : candidates.entrySet()) {
			if (!tailNotHead) {
				if (!filterSet.isTrue(entry.getKey(), t.getRelation(), t.getTail())) {
					kCandidates.put(entry.getKey(), entry.getValue());
					i++;
				}
				if (testSet.isTrue(entry.getKey(), t.getRelation(), t.getTail())) {
					kCandidates.put(entry.getKey(), entry.getValue());
				}
			}
			if (tailNotHead) {
				if (!filterSet.isTrue(t.getHead(), t.getRelation(), entry.getKey())) {
					kCandidates.put(entry.getKey(), entry.getValue());
					i++;
				}
				if (testSet.isTrue(t.getHead(), t.getRelation(), entry.getKey())) {
					kCandidates.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return (new LinkedHashMapK(kCandidates, i));
	}
	*/
	
	
	private synchronized static void writeTopKCandidatesWithRuleIndex(Triple t, TripleSet testSet, LinkedHashMap<String, Double> kHeadCandidates, HashMap<String, ArrayList<Rule>> explainedHeadCandidates, LinkedHashMap<String, Double> kTailCandidates, HashMap<String, ArrayList<Rule>> explainedTailCandidates, PrintWriter writer, int k,  HashMap<String, HashMap<Rule,Integer>> relation2RulesIndex) {
		writer.println("{ \"" + t + "\" : ");
		int i = 0;
		writer.println("{  \"heads\" : { ");
		StringBuilder headPreds = new StringBuilder();
		StringBuilder headIndices = new StringBuilder();
		boolean first = true;
		for (Entry<String, Double> entry : kHeadCandidates.entrySet()) {
			if (t.getHead().equals(entry.getKey()) || !testSet.isTrue(entry.getKey(), t.getRelation(), t.getTail())) {
				// System.out.println("XXX: " + relation2RulesIndex);
				String indexList = getIndexList(explainedHeadCandidates.get(entry.getKey()), relation2RulesIndex.get(t.getRelation()));
				if (first) {
					headPreds.append("\"" + entry.getKey() + "\"");
					headIndices.append("[" + indexList + "]");
					first = false;
				}
				else {
					headPreds.append(",\"" + entry.getKey() + "\"");
					headIndices.append(", [" + indexList + "]");
				}
				
				// writer.print(entry.getKey() + "\t" + getIndexList(explainedHeadCandidates.get(entry.getKey()), relation2RulesIndex.get(t.getRelation())) + "\t");
				i++;
			}
			if (i == k) break;
		}
		writer.println("    \"candidates\" : [ " + headPreds + " ], ");
		writer.println("    \"rules\" : [ " + headIndices + " ], ");
		writer.println("},");
		i = 0;
		writer.println("\"tails\" : { ");
		StringBuilder tailPreds = new StringBuilder();
		StringBuilder tailIndices = new StringBuilder();
		 first = true;
		for (Entry<String, Double> entry : kTailCandidates.entrySet()) {
			if (t.getTail().equals(entry.getKey()) || !testSet.isTrue(t.getHead(), t.getRelation(), entry.getKey())) {
				String indexList = getIndexList(explainedTailCandidates.get(entry.getKey()), relation2RulesIndex.get(t.getRelation()));
				if (first) {
					tailPreds.append("\"" + entry.getKey() + "\"");
					tailIndices.append("[" + indexList + "]");
					first = false;
				}
				else {
					tailPreds.append(",\"" + entry.getKey() + "\"");
					tailIndices.append(", [" + indexList + "]");
				}
				
				// writer.print(entry.getKey() + "\t" + getIndexList(explainedHeadCandidates.get(entry.getKey()), relation2RulesIndex.get(t.getRelation())) + "\t");
				i++;
			}
			if (i == k) break;
		}
		writer.println("    \"candidates\" : [ " + tailPreds + " ], ");
		writer.println("    \"rules\" : [ " + tailIndices + " ], ");
		writer.println("}}},");
		writer.flush();
		
	}
	
	private static String getIndexList(ArrayList<Rule> xRules, HashMap<Rule,Integer> rulesIndex) {
		 StringBuilder sb = new StringBuilder();
		 boolean first = true;
		 int counter = 0;
		 for (Rule r : xRules) {
			 int i = rulesIndex.get(r);
			 if (first) {
				 sb.append(i);
				 first = false; 
			 }
			 else {
				 sb.append("," + i); 
			 }
			 counter++;
			 if (counter == Settings.MAX_EXPLANATIONS) break;
		 }
		 return sb.toString();
		 
	}
	
	
	private static HashSet<String> getFilteredEntities(TripleSet trainingSet, TripleSet validationSet, TripleSet testSet, Triple t, Set<String> candidateEntities, boolean tailNotHead) {
		// LinkedHashMap<String, Double> candidatesSorted = sortByValue(candidates);
		HashSet<String> filteredEntities = new HashSet<String>();
		if (!Settings.FILTER_DURING_APPLICATION) filteredEntities.addAll(candidateEntities);
		for (String entity : candidateEntities) {
			if (!tailNotHead) {
				/*
				if (Settings.PREDICT_ONLY_UNCONNECTED) {
					Set<String> links = trainingSet.getRelations(entity, t.getTail());
					Set<String> invLinks = trainingSet.getRelations(t.getTail(), entity);
					if (invLinks.size() > 0) continue;
					if (!links.contains(t.getRelation()) && links.size() > 0) continue;
					if (links.contains(t.getRelation()) && links.size() > 1) continue; 
				}
				*/
				if (!validationSet.isTrue(entity, t.getRelation(), t.getTail()) && !trainingSet.isTrue(entity, t.getRelation(), t.getTail()) && !testSet.isTrue(entity, t.getRelation(), t.getTail())) {
					filteredEntities.add(entity);
				}
				if (testSet.isTrue(entity, t.getRelation(), t.getTail())) {
					// TAKE CARE, remove to reactivate the possibility of storing previous results
					if (entity.equals(t.getHead())) filteredEntities.add(entity);
				}
				
			}
			if (tailNotHead) {
				/*
				if (Settings.PREDICT_ONLY_UNCONNECTED) {
					Set<String> links = trainingSet.getRelations(t.getHead(), entity);
					Set<String> invLinks = trainingSet.getRelations(entity, t.getHead());
					if (invLinks.size() > 0) continue;
					if (!links.contains(t.getRelation()) && links.size() > 0) continue;
					if (links.contains(t.getRelation()) && links.size() > 1) continue;	
				}
				*/
				if (!validationSet.isTrue(t.getHead(), t.getRelation(), entity) && !trainingSet.isTrue(t.getHead(), t.getRelation(), entity) && !testSet.isTrue(t.getHead(), t.getRelation(), entity)) {
					filteredEntities.add(entity);
				}
				if (testSet.isTrue(t.getHead(), t.getRelation(), entity)) {
					// TAKE CARE, remove to reactivate the possibility of storing previous results
					if (entity.equals(t.getTail())) filteredEntities.add(entity);
				}
			}
		}
		return filteredEntities;
	}
	
	private static synchronized void writeTopKCandidates(Triple t, TripleSet testSet, LinkedHashMap<String, Double> kHeadCandidates, LinkedHashMap<String, Double> kTailCandidates, PrintWriter writer, int k) {
		writer.println(t);
		int i = 0;
		writer.print("Heads: ");
		for (Entry<String, Double> entry : kHeadCandidates.entrySet()) {
			if (t.getHead().equals(entry.getKey()) || !testSet.isTrue(entry.getKey(), t.getRelation(), t.getTail())) {
				writer.print(entry.getKey() + "\t" + entry.getValue() + "\t");
				i++;
			}
			if (i == k) break;
		}
		writer.println();
		i = 0;
		writer.print("Tails: ");
		for (Entry<String, Double> entry : kTailCandidates.entrySet()) {
			if (t.getTail().equals(entry.getKey()) || !testSet.isTrue(t.getHead(), t.getRelation(), entry.getKey())) {
				writer.print(entry.getKey() + "\t" + entry.getValue() + "\t");
				i++;
			}
			if (i == k) break;
		}
		writer.println();
		writer.flush();

	}
	
	
	private static synchronized void writeTopKExplanation(Triple t, TripleSet testSet, LinkedHashMap<String, Double> kHeadCandidates, ScoreTree headTree, LinkedHashMap<String, Double> kTailCandidates, ScoreTree tailTree, int k) {
		
		
		
		if (explanationsHead != null && explanationsTail != null) {
			explanationsHead.put(t, headTree);
			explanationsTail.put(t, tailTree);
		}
		
		if (Settings.EXPLANATION_WRITER != null) {
			Settings.EXPLANATION_WRITER.println(t);
			Settings.EXPLANATION_WRITER.println("Heads:");
			Settings.EXPLANATION_WRITER.println(headTree);
			Settings.EXPLANATION_WRITER.println("Tails:");
			Settings.EXPLANATION_WRITER.println(tailTree);
			Settings.EXPLANATION_WRITER.flush();
		}
	}
	
	/*
	private static synchronized void writeTopKCandidatesPlusExplanation(Triple t, TripleSet testSet, LinkedHashMap<String, Double> kHeadCandidates, ScoreTree allHeadCandidates, LinkedHashMap<String, Double> kTailCandidates, ScoreTree allTailCandidates,	PrintWriter writer, int k) {
		Settings.EXPLANATION_WRITER.println(t);
		Settings.EXPLANATION_WRITER.println("Heads:");
		Settings.EXPLANATION_WRITER.println(allHeadCandidates);
		Settings.EXPLANATION_WRITER.println("Tails:");
		Settings.EXPLANATION_WRITER.println(allTailCandidates);
		Settings.EXPLANATION_WRITER.flush();
	}
	*/
	
	
	/*
	private static void processTopKCandidates(TripleSet testSet, Triple t, HashMap<String, Double> tailCandidates, HashMap<String, Double> headCandidates, TripleSet filterSet, int k, PrintWriter writer, HashMap<String, Double> kTailCandidates, HashMap<String, Double> kHeadCandidates) {
		LinkedHashMap<String, Double> tailCandidatesSorted = sortByValue(tailCandidates);
		LinkedHashMap<String, Double> headCandidatesSorted = sortByValue(headCandidates);
		writer.println(t);
		writer.print("Heads: ");
		int i = 0;
		for (Entry<String, Double> entry : headCandidatesSorted.entrySet()) {
			if (i < k) {
				if (!filterSet.isTrue(entry.getKey(), t.getRelation(), t.getTail()) || t.getHead().equals(entry.getKey())) {
					writer.print(entry.getKey() + "\t" + entry.getValue() + "\t");
					kHeadCandidates.put(entry.getKey(), entry.getValue());
					i++;
				}
				if (testSet.isTrue(entry.getKey(), t.getRelation(), t.getTail())) {
					kHeadCandidates.put(entry.getKey(), entry.getValue());
				}
			}
		}
		writer.println();
		writer.print("Tails: ");
		int j = 0;
		for (Entry<String, Double> entry : tailCandidatesSorted.entrySet()) {
			if (j < k) {
				if (!filterSet.isTrue(t.getHead(), t.getRelation(), entry.getKey())
						|| t.getTail().equals(entry.getKey())) {
					writer.print(entry.getKey() + "\t" + entry.getValue() + "\t");
					kTailCandidates.put(entry.getKey(), entry.getValue());
					j++;
				}
				if (testSet.isTrue(t.getHead(), t.getRelation(), entry.getKey())) {
					kTailCandidates.put(entry.getKey(), entry.getValue());
				}
			}
		}
		writer.println();
		writer.flush();
	}
	*/

	/*
	private static double combineProbability(double prob, double previousProb) {
		double newProb;
		switch (COMBINATION_RULE_ID) {
		case 1: // multiplication
			newProb = 1.0 - ((1.0 - previousProb) * (1.0 - prob));
			break;
		case 2: // maxplus
			newProb = Math.max(previousProb, prob) + EPSILON;
			break;
		case 3: // max
		default:
			newProb = Math.max(previousProb, prob);
			break;
		}
		return newProb;
	}
	*/
	

	/*
	private static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	*/
	
	public static void sortByValue(LinkedHashMap<String, Double> m) {
	    List<Map.Entry<String, Double>> entries = new ArrayList<>(m.entrySet());
	    Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
	        public int compare(Map.Entry<String, Double> lhs, Map.Entry<String, Double> rhs) {
	            if (lhs.getValue() < rhs.getValue()) return 1;
	            else if (lhs.getValue() > rhs.getValue())  return -1;
	            else return 0;
	        }
	    });

	    m.clear();
	    for(Map.Entry<String, Double> e : entries) {
	        m.put(e.getKey(), e.getValue());
	    }
	}
	
	
	private static LinkedHashMap<String, Double> sampleTopK(LinkedHashMap<String, Double> candidates, String hit, int k, boolean headDir) {
		
		
		LinkedHashMap<String, Double> sampledCandidates = new LinkedHashMap<String, Double>();
		ArrayList<String> shortenedCandidateList = new ArrayList<String>();
		
		int counter = 0;
		boolean foundHit = false;
		for (String c : candidates.keySet()) {
			shortenedCandidateList.add(c);
			if (c.equals(hit)) {
				//if (!headDir) {
					// if (counter > 1000) System.out.println("*");
					//System.out.println(counter);
				//}
				foundHit = true;
			}
			counter++;
			if (foundHit && counter >= k) break;
		}
		// if (!foundHit && !headDir) System.out.println("-");
		
	
		Collections.shuffle(shortenedCandidateList);
		boolean selectedHit = false;
		for (int i = 1; i < Math.min(k, shortenedCandidateList.size()); i++) {
			String c = shortenedCandidateList.get(i);
			sampledCandidates.put(c, candidates.get(c));
			
			if (c.equals(hit))  {
				selectedHit =  true;
				
			}
		}
		if (selectedHit || (!(candidates.keySet().contains(hit)))) {
			if (shortenedCandidateList.size() > 0) {
				String c = shortenedCandidateList.get(0);
				sampledCandidates.put(c, candidates.get(c));
			}
		}
		else {
			// if (headDir) System.out.println("headDir at 100");
			sampledCandidates.put(hit, candidates.get(hit));
		}
		return sampledCandidates;
	}
	
	
	public static void sortByValue(LinkedHashMap<String, Double> m, String hit, int k) {
	    List<Map.Entry<String, Double>> entries = new ArrayList<>(m.entrySet());
	    Collections.sort(entries, new Comparator<Map.Entry<String, Double>>() {
	        public int compare(Map.Entry<String, Double> lhs, Map.Entry<String, Double> rhs) {
	            if (lhs.getValue() < rhs.getValue()) return 1;
	            else if (lhs.getValue() > rhs.getValue())  return -1;
	            else return 0;
	        }
	    });
	    m.clear();
	    int counter = 0;
	    boolean foundHit = false;
	    for(Map.Entry<String, Double> e : entries) {
	    	counter++;
	        if (counter == k && !foundHit) {
	        	m.put(hit, 0.00001);	        	
	        }
	        else {
	        	m.put(e.getKey(), e.getValue());
	        	if (e.getKey().equals(hit)) foundHit = true;
	        }
	        if (counter == k) break;
	    }
	}
	
}
