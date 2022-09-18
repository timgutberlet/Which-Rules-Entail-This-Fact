package de.unima.ki.anyburl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Properties;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.io.IOHelper;
import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.playground.dream.IndexAC12ZeroTree;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleAcyclic1;
import de.unima.ki.anyburl.structure.RuleAcyclic2;
import de.unima.ki.anyburl.structure.RuleCyclic;
import de.unima.ki.anyburl.structure.RuleCyclicZ;

public class Apply {
		
	
	private static String CONFIG_FILE = "config-apply.properties"; 
	
	
	/**
	 * Filter the rule set prior to applying it. Removes redundant rules which do not have any impact (or no desired impact). 
	 */
	// public static boolean FILTER = true;	

	
	
	/**
	* Always should be set to false. The TILDE results are based on a setting where this is set to true.
	* This parameter is sued to check in how far this setting increases the quality of the results.
	*/
	public static boolean USE_VALIDATION_AS_BK = false;
	
	
	public static void main(String[] args) throws IOException {
		
		TripleSet.supportRandomAccess = false;
		
		if (args != null) {
			if (args.length == 1) {
				CONFIG_FILE = args[0];
				System.out.println("* reading params from file " + CONFIG_FILE);
			}
		}
		Settings.REWRITE_REFLEXIV = false;
		Rule.applicationMode();
		Properties prop = new Properties();
		InputStream input = null;
		try {
			if (args != null) {
				input = new FileInputStream(CONFIG_FILE);
				prop.load(input);
				Settings.PREDICTION_TYPE = IOHelper.getProperty(prop, "PREDICTION_TYPE", Settings.PREDICTION_TYPE);
				if (Settings.PREDICTION_TYPE.equals("aRx")) {
					Settings.SAFE_PREFIX_MODE = IOHelper.getProperty(prop, "SAFE_PREFIX_MODE", Settings.SAFE_PREFIX_MODE);
					Settings.PATH_TRAINING = IOHelper.getProperty(prop, "PATH_TRAINING", Settings.PATH_TRAINING);
					Settings.PATH_TEST = IOHelper.getProperty(prop, "PATH_TEST", Settings.PATH_TEST);
					Settings.PATH_VALID = IOHelper.getProperty(prop, "PATH_VALID",Settings.PATH_VALID);
					Settings.PATH_OUTPUT = IOHelper.getProperty(prop, "PATH_OUTPUT", Settings.PATH_OUTPUT);
					Settings.PATH_EXPLANATION = IOHelper.getProperty(prop, "PATH_EXPLANATION", Settings.PATH_EXPLANATION);
					Settings.MAX_EXPLANATIONS = IOHelper.getProperty(prop, "MAX_EXPLANATIONS", Settings.MAX_EXPLANATIONS);
					Settings.PATH_RULE_INDEX = IOHelper.getProperty(prop, "PATH_RULE_INDEX", Settings.PATH_RULE_INDEX);
					Settings.PATH_RULES = IOHelper.getProperty(prop, "PATH_RULES", Settings.PATH_RULES);
					Settings.PATH_RULES_BASE = IOHelper.getProperty(prop, "PATH_RULES_BASE", Settings.PATH_RULES_BASE);
					Settings.TOP_K_OUTPUT = IOHelper.getProperty(prop, "TOP_K_OUTPUT", Settings.TOP_K_OUTPUT);
					Settings.TOP_K_OUTPUT_SAMPLING_FROM = IOHelper.getProperty(prop, "TOP_K_OUTPUT_SAMPLING_FROM", Settings.TOP_K_OUTPUT_SAMPLING_FROM);
					
					Settings.UNSEEN_NEGATIVE_EXAMPLES = IOHelper.getProperty(prop, "UNSEEN_NEGATIVE_EXAMPLES", Settings.UNSEEN_NEGATIVE_EXAMPLES);
					Settings.UNSEEN_NEGATIVE_EXAMPLES_REFINE = IOHelper.getProperty(prop, "UNSEEN_NEGATIVE_EXAMPLES_REFINE", Settings.UNSEEN_NEGATIVE_EXAMPLES_REFINE);
					Settings.THRESHOLD_CONFIDENCE = IOHelper.getProperty(prop, "THRESHOLD_CONFIDENCE", Settings.THRESHOLD_CONFIDENCE);
					Settings.DISCRIMINATION_BOUND = IOHelper.getProperty(prop, "DISCRIMINATION_BOUND",  Settings.DISCRIMINATION_BOUND);
					Settings.TRIAL_SIZE = IOHelper.getProperty(prop, "TRIAL_SIZE",  Settings.TRIAL_SIZE);
					Settings.WORKER_THREADS = IOHelper.getProperty(prop, "WORKER_THREADS",  Settings.WORKER_THREADS);
					Settings.UNSEEN_NEGATIVE_EXAMPLES_ATYPED = IOHelper.getProperty(prop, "UNSEEN_NEGATIVE_EXAMPLES_ATYPED",  Settings.UNSEEN_NEGATIVE_EXAMPLES_ATYPED);
					Settings.BEAM_SAMPLING_MAX_BODY_GROUNDINGS = IOHelper.getProperty(prop, "BEAM_SAMPLING_MAX_BODY_GROUNDINGS", Settings.BEAM_SAMPLING_MAX_BODY_GROUNDINGS);
					Settings.BEAM_SAMPLING_MAX_BODY_GROUNDING_ATTEMPTS = IOHelper.getProperty(prop, "BEAM_SAMPLING_MAX_BODY_GROUNDING_ATTEMPTS", Settings.BEAM_SAMPLING_MAX_BODY_GROUNDING_ATTEMPTS);
					Settings.BEAM_SAMPLING_MAX_REPETITIONS = IOHelper.getProperty(prop, "BEAM_SAMPLING_MAX_REPETITIONS", Settings.BEAM_SAMPLING_MAX_REPETITIONS);
					
					Settings.READ_CYCLIC_RULES   = IOHelper.getProperty(prop, "READ_CYCLIC_RULES",  Settings.READ_CYCLIC_RULES);
					Settings.READ_ACYCLIC1_RULES = IOHelper.getProperty(prop, "READ_ACYCLIC1_RULES",  Settings.READ_ACYCLIC1_RULES);
					Settings.READ_ACYCLIC2_RULES = IOHelper.getProperty(prop, "READ_ACYCLIC2_RULES",  Settings.READ_ACYCLIC2_RULES);
					Settings.READ_ZERO_RULES = IOHelper.getProperty(prop, "READ_ZERO_RULES",  Settings.READ_ZERO_RULES);
					Settings.EXPLAIN_HIT = IOHelper.getProperty(prop, "EXPLAIN_HIT",  Settings.EXPLAIN_HIT);
					
					Settings.READ_THRESHOLD_CONFIDENCE = IOHelper.getProperty(prop, "READ_THRESHOLD_CONFIDENCE", Settings.READ_THRESHOLD_CONFIDENCE);
					Settings.READ_THRESHOLD_CORRECT_PREDICTIONS = IOHelper.getProperty(prop, "READ_THRESHOLD_CORRECT_PREDICTIONS", Settings.READ_THRESHOLD_CORRECT_PREDICTIONS);
					Settings.READ_THRESHOLD_MAX_RULE_LENGTH = IOHelper.getProperty(prop, "READ_THRESHOLD_MAX_RULE_LENGTH", Settings.READ_THRESHOLD_MAX_RULE_LENGTH);
					
					//FILTER = IOHelper.getProperty(prop, "FILTER", FILTER);
					Settings.AGGREGATION_TYPE = IOHelper.getProperty(prop, "AGGREGATION_TYPE", Settings.AGGREGATION_TYPE);
				
					Settings.FILTER_DURING_APPLICATION = IOHelper.getProperty(prop, "FILTER_DURING_APPLICATION", Settings.FILTER_DURING_APPLICATION);
					
					
					if (Settings.AGGREGATION_TYPE.equals("maxplus")) Settings.AGGREGATION_ID = 1;
					if (Settings.AGGREGATION_TYPE.equals("noisyor")) Settings.AGGREGATION_ID = 2;
					if (Settings.AGGREGATION_TYPE.equals("maxplus-explanation")) Settings.AGGREGATION_ID = 3;
					if (Settings.AGGREGATION_TYPE.equals("maxplus-explanation-stdout")) Settings.AGGREGATION_ID = 5;
					if (Settings.AGGREGATION_TYPE.equals("symbolic")) Settings.AGGREGATION_ID = 4;
				}
				
				else {
					System.err.println("The prediction type " + Settings.PREDICTION_TYPE + " is not yet supported.");
					System.exit(1);
				}
			}
			else {
				System.out.println("* not using a config file for running the code");
			}

		}
		catch (IOException ex) {
			System.err.println("Could not read relevant parameters from the config file " + CONFIG_FILE);
			ex.printStackTrace();
			System.exit(1);
		}
		
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		
		if (Settings.PREDICTION_TYPE.equals("aRx")) {			
			String[] values = getMultiProcessing(Settings.PATH_RULES);
			PrintWriter log = null;
			
			if (values.length == 0) log = new PrintWriter(Settings.PATH_RULES + "_plog");
			else log = new PrintWriter(Settings.PATH_OUTPUT.replace("|", "") + "_plog");

			
			log.println("Logfile");
			log.println("~~~~~~~\n");
			log.println();
			log.println(IOHelper.getParams());
			log.flush();
			
			RuleReader rr = new RuleReader();
			LinkedList<Rule> base = new LinkedList<Rule>();
			if (!Settings.PATH_RULES_BASE.equals("")) {
				System.out.println("* reading additional rule file as base");
				base = rr.read(Settings.PATH_RULES_BASE);
			}
			
			for (String value : values) {
				
				long startTime = System.currentTimeMillis();
				String path_output_used = null;
				String path_rules_used = null;
				if (value == null) {
					path_output_used = Settings.PATH_OUTPUT;
					path_rules_used = Settings.PATH_RULES;
				}
				if (value != null) {
					path_output_used = Settings.PATH_OUTPUT.replaceFirst("\\|.*\\|", "" + value); 
					path_rules_used = Settings.PATH_RULES.replaceFirst("\\|.*\\|", "" + value);
					
				}
				log.println("rules:   " + path_rules_used);
				log.println("output: " + path_output_used);
				log.flush();
			
				PrintWriter pw = new PrintWriter(new File(path_output_used));

				
				if (Settings.PATH_EXPLANATION != null && !Settings.PATH_EXPLANATION.equals("BLIND")) {
					Settings.EXPLANATION_WRITER = new PrintWriter(new File(Settings.PATH_EXPLANATION));
				}
				System.out.println("* writing prediction to " + path_output_used);
				
				TripleSet trainingSet = new TripleSet(Settings.PATH_TRAINING);
				TripleSet testSet = new TripleSet(Settings.PATH_TEST);
				TripleSet validSet = new TripleSet(Settings.PATH_VALID);
				
				// check if you should predict only unconnected
				// ACHTUNG: Never remove that comment here
				// checkIfPredictOnlyUnconnected(validSet, trainingSet);
				
				if (USE_VALIDATION_AS_BK) {
					trainingSet.addTripleSet(validSet);
					validSet = new TripleSet();
				}
				
				LinkedList<Rule> rules = rr.read(path_rules_used);
				rules.addAll(base);

				int rulesSize = rules.size();
				LinkedList<Rule> rulesThresholded = new LinkedList<Rule>();
				for (Rule r : rules) {
					if (r.getConfidence() > Settings.THRESHOLD_CONFIDENCE) {
						rulesThresholded.add(r);		
					}
				}
				System.out.println("* applied confidence threshold of " + Settings.THRESHOLD_CONFIDENCE + " and reduced from " + rules.size() + " to " + rulesThresholded.size() + " rules");

				rules = rulesThresholded;
				
				long startApplicationTime = System.currentTimeMillis();
				
				RuleEngine.applyRulesARX(rules, testSet, trainingSet, validSet, Settings.TOP_K_OUTPUT, pw);
			
			
				long endTime = System.currentTimeMillis();
				System.out.println("* evaluated " + rulesSize + " rules to propose candiates for " + testSet.getTriples().size() + "*2 completion tasks");
				System.out.println("* finished in " + (endTime - startTime) + "ms.");
				
				
				System.out.println();
				
				log.println("finished in " + (endTime - startApplicationTime) / 1000 + "s (rule indexing and application, creation and storage of ranking).");
				log.println("finished in " + (endTime - startTime) / 1000 + "s including all operations (+ loading triplesets,  + loading rules).");
				log.println();
				log.flush();
			}
			log.close();
		}
	
	}


	/**
	 * Checks whether the validation set has more than 500 triples. If yes, it is checked whether those
	 * are only connecting entities that are unconnected in the training set. If this is the case the corresponding
	 * parameter is set to true, which results into a filtering out such predictions.
	 * 
	 * @param validSet
	 * @param trainingSet
	 */
	private static void checkIfPredictOnlyUnconnected(TripleSet validSet, TripleSet trainingSet) {
		// TODO surpressed due to inefficiency and because it is not rally needed anymore
		/*
		 * 
		
		if (validSet.size() > 200) {
			for (Triple t : validSet.getTriples()) {
				if (trainingSet.getRelations(t.getHead(), t.getTail()).size() > 0) {
					return;
				}
				if (trainingSet.getRelations(t.getTail(),t.getHead()).size() > 0) {
					return;
				}
			}
			System.out.println("* set param PREDICT_ONLY_UNCONNECTED due to validation set characteristics");
			Settings.PREDICT_ONLY_UNCONNECTED = true;

		}
		*/
		

		

		
	}



	private static void filterTSA(String[] TSA, int TSAindex, LinkedList<Rule> rulesThresholded, Rule r) {
		switch(TSA[TSAindex]) {
		case "ALL":
			rulesThresholded.add(r);
			break;
		case "C-1":
			if (r instanceof RuleCyclic && r.bodysize() == 1) rulesThresholded.add(r);
			break;
		case "C-2":
			if (r instanceof RuleCyclic && r.bodysize() == 2) rulesThresholded.add(r);
			break;
		case "C-3":
			if (r instanceof RuleCyclic && r.bodysize() == 3) rulesThresholded.add(r);
			break;
		case "AC1-1":
			if (r instanceof RuleAcyclic1 && r.bodysize() == 1) rulesThresholded.add(r);
			break;
		case "AC1-2":
			if (r instanceof RuleAcyclic1 && r.bodysize() == 2) rulesThresholded.add(r);
			break;	
		case "AC2-1":
			if (r instanceof RuleAcyclic2 && r.bodysize() == 1) rulesThresholded.add(r);
			break;
		case "AC2-2":
			if (r instanceof RuleAcyclic2 && r.bodysize() == 2) rulesThresholded.add(r);
			break;	
		case "N-C-1":
			if (!(r instanceof RuleCyclic && r.bodysize() == 1)) rulesThresholded.add(r);
			break;
		case "N-C-2":
			if (!(r instanceof RuleCyclic && r.bodysize() == 2)) rulesThresholded.add(r);
			break;
		case "N-C-3":
			if (!(r instanceof RuleCyclic && r.bodysize() == 3)) rulesThresholded.add(r);
			break;
		case "N-AC1-1":
			if (!(r instanceof RuleAcyclic1 && r.bodysize() == 1)) rulesThresholded.add(r);
			break;
		case "N-AC1-2":
			if (!(r instanceof RuleAcyclic1 && r.bodysize() == 2)) rulesThresholded.add(r);
			break;	
		case "N-AC2-1":
			if (!(r instanceof RuleAcyclic2 && r.bodysize() == 1)) rulesThresholded.add(r);
			break;
		case "N-AC2-2":
			if (!(r instanceof RuleAcyclic2 && r.bodysize() == 2)) rulesThresholded.add(r);
			break;		
		}
	}
	
	

	/*
	private static void showRulesStats(List<Rule> rules) { 
		int xyCounter = 0;
		int xCounter = 0;
		int yCounter = 0;
		for (Rule rule : rules) {
			if (rule.isXYRule()) xyCounter++;
			if (rule.isXRule()) xCounter++;
			if (rule.isYRule()) yCounter++;
		}
		System.out.println("XY=" + xyCounter + " X="+ xCounter + " Y=" + yCounter);
		
	}
	*/

	public static String[] getMultiProcessing(String path1) {
		String token[] = path1.split("\\|");
		if (token.length < 2) {
			return new String[] {null};
		}
		else {
			String values[] = token[1].split(",");
			return values;
		}
	}
	

		

}
