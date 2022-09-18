package de.unima.ki.anyburl.threads;

import java.util.ArrayList;
import java.util.HashMap;

import de.unima.ki.anyburl.RuleEngine;
import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.structure.Rule;

public class Predictor extends Thread {
	
	private TripleSet testSet;
	private TripleSet trainingSet;
	private TripleSet validationSet;
	private int k;
	private HashMap<String, ArrayList<Rule>> relation2Rules4Prediction;
	private HashMap<String, HashMap<Rule,Integer>> relation2RulesIndex;
	
	
	public Predictor(TripleSet testSet, TripleSet trainingSet, TripleSet validationSet, int k, HashMap<String, ArrayList<Rule>> relation2Rules4Prediction, HashMap<String, HashMap<Rule,Integer>> relation2RulesIndex) {
		this.testSet = testSet;
		this.trainingSet = trainingSet;
		this.validationSet = validationSet;
		this.k = k;
		this.relation2Rules4Prediction = relation2Rules4Prediction;
		this.relation2RulesIndex = relation2RulesIndex;
	}
	
	
	public void run() {
		Triple triple = RuleEngine.getNextPredictionTask();
		// Rule rule = null;
		while (triple != null) {
			// System.out.println(this.getName() + " making prediction for " + triple);
			if (Settings.AGGREGATION_ID == 1) {
				RuleEngine.predictMax(testSet, trainingSet, validationSet, k, relation2Rules4Prediction, triple);
			}
			else if (Settings.AGGREGATION_ID == 2) {
				RuleEngine.predictNoisyOr(testSet, trainingSet, validationSet, k, relation2Rules4Prediction, triple, true);
				RuleEngine.predictNoisyOr(testSet, trainingSet, validationSet, k, relation2Rules4Prediction, triple, false);
			}
			else if (Settings.AGGREGATION_ID == 3) {
				RuleEngine.predictMaxPlusSpecial(testSet, trainingSet, validationSet, relation2Rules4Prediction, triple, relation2RulesIndex);
			}
			else if (Settings.AGGREGATION_ID == 5) {
				RuleEngine.predictMaxPlusSpecial(testSet, trainingSet, validationSet, relation2Rules4Prediction, triple, relation2RulesIndex);
			}
			else if (Settings.AGGREGATION_ID == 4) {
				RuleEngine.predictSymbolic(testSet, trainingSet, validationSet, k, relation2Rules4Prediction, triple, relation2RulesIndex);
			}
			
			//System.out.println(this.getName() + " going for next prediction");
			triple = RuleEngine.getNextPredictionTask();
			// System.out.println(this.getName() + " going for next prediction");
			// rule = RuleEngine.getNextRuleMaterializationTask();
			// if (rule != null) RuleEngine.materializeRule(rule, trainingSet);
			// Thread.yield();
		}
		
	}

}
