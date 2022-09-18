package de.unima.ki.anyburl;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import de.unima.ki.anyburl.io.IOHelper;

public class Settings {
	
	
	/**
	 * If set to a value > 0 this value will be used for initialising the hashmaps used for indexing the dataset. This value
	 * should be specified for large datasets. It will be assumed that the number of entities is not larger than this number.
	 * If  this is not the case bad things will happen.
	 * 
	 */
	public static int NUM_OF_ENTITIES_ = 0;
	
	
	/**
	 * In default this is set to true. It means that it filters out anything that is triple that is true in train or test or valid
	 * which is not the target triple. 
	 * 
	 */
	public static boolean FILTER_DURING_APPLICATION = true;
	
	
	/**
	 * Activates a soft functionality threshold. See how it is used in the functionality related methods
	 * in the class TripleSet.
	 * 
	 * If set to 1.0 the threshold is not used at all. The lower the value, the stronger is the impact and the strictnetss of the theshold.
	 */
	public static double FUNCTIONALITY_THRESHOLD = 1.0;
	
	
	

	/**
	 * Activates a hard type threshold.
	 * 
	 * If set to 1.0 the threshold is not used at all. The lower the value, the stronger is the impact and the strictnetss of the theshold.
	 */
	public static double TYPE_THRESHOLD = 0.9;
	
	
	
	
	
	/**
	 * If turned on it is ensured that each string is used only once, and each duplicate is just a duplicate
	 * in terms of a reference instead of heaving the same string several times stored in the heap.
	 * 
	 */
	public static final boolean EFFICIENT_MEMORY_USAGE = true;
	
	
	

	/**
	 * If set to true, it adds a prefix in front of each entity and relation id, when reading triplesets from files
	 * to avoid problem related to input that uses numbers as ids only. 
	 */
	public static boolean SAFE_PREFIX_MODE = false;
	
	public static final String PREFIX_ENTITY = "e";
	public static final String PREFIX_RELATION = "r";
			

	public static HashMap<String, Integer> KEYWORD;
	
	/**
	 * Do not change this. For APPLY its required that this is set to false, for LEARNING both is okay
	 * Currently in Apply its set to false hard-coded. It means this parameter has no impact on Apply.
	 */
	public static boolean REWRITE_REFLEXIV = true;
	
	public static final String REWRITE_REFLEXIV_TOKEN = "me_myself_i";
	public static final boolean BEAM_NOT_DFS = true; 
	
	/**
	 * If set to true, in computeScores the starting point is a randomly chosen entity. 
	 * This entity is randomly chosen from the set of all entities that are a possible starting point.
	 * If set to false its a randomly chosen triple that instantiates the first body atom.
	 * This means that in this setting a starting entity that appears in many such triples, will be more frequently a starting
	 * entity.
	 * 
	 * 
	 */
	public static final boolean BEAM_TYPE_EDIS = true;

	
	
	
	public static String[] SINGLE_RELATIONS = null;
	
	
	public static String[] FORBIDDEN_RELATIONS = null;
	
	public static HashSet<String> FORBIDDEN_RELATIONS_AS_SET = null;
	
	/**
	* Suppresses any rules with constants.
	*/
	public static boolean CONSTANTS_OFF = false;
	
	public static double EPSILON = 0.1;
	
	/**
	* In the first batch the decisions are completely randomized. This random influence becomes less
	* at will be stable at  RANDOMIZED_DECISIONS after this number of batches have been carried out.
	*/
	public static double RANDOMIZED_DECISIONS_ANNEALING = 5;
	

	public static boolean EXPERIMENTAL_AC120_AGGREGATION_ON = false;
	
	
	/**
	* REACTIVATED!
	*  
	* This number defines if a rule to be redundant if the number of groundings for its last atom is less than this parameter.
	* It avoid that rules with constants are too specific and thus redundant compared to shorter rules 
	*    head(X,c) <= hasGender(X, female)
	*    head(X,c) <= hasGender(X, A), hasGender(berta, A)
	* The second rule will be filtered out, because berta has only 1 gender, which is female.
	* 
	*/
	public static int AC_MIN_NUM_OF_LAST_ATOM_GROUNDINGS = 5;
	
	
	/**
	* PROBABLY OUT 
	* 
	* The specialization confidence interval determines that a rule shall only be accepted as a specialization of a more general rule, if
	* it has a higher confidence and if the probability that its confidence is really higher is at least that chosen value.
	* Possible values are 0.9, 0.99 and 0.99.
	* 
	* -1 = turned-off
	*/
	public static double SPECIALIZATION_CI = -1;
	
	/**
	 * Relevant for reinforced learning, how to compute the scores created by a thread.
	 * 
	 * 1 = correct predictions
	 * 2 = correct predictions weighted by confidence
	 * 3 = correct predictions weighted by applied confidence
	 * 4 = correct predictions weighted by applied confidence^2
	 * 5 = correct predictions weighted by applied confidence divided by 2^(rule length-1)
	 */
	public static int REWARD = 5;
	
	
	/**
	 * Relevant for reinforced learning, how to use the scores created by a thread within the decision.
	 * 
	 * 1 = GREEDY = Epsilon greedy: Focus only on the best.
	 * 2 = WEIGHTED = Weighted policy: focus as much as much a a path type, as much as it gave you.
	 * 
	 */
	public static int POLICY = 2;
	
	
	
	/**
	 * Defines the prediction type which also influences the usage of the other parameters. 
	 * Possible values are currently aRx and xRy.
	 */
	public static String PREDICTION_TYPE = "aRx";
	
	
	/**
	 * Path to the file that contains the triple set used for learning the rules.
	 */
	public static String PATH_TRAINING = "";
	
	
	
	/**
	 * Path to the file that contains the triple set used for to test the rules.
	 */
	public static String PATH_TEST = "";
	
	/**
	 * Path to the file that contains the triple set used validation purpose (e.g. learning hyper parameter).
	 */
	public static String PATH_VALID = "";
	
	
	/**
	 * Path to the file that contains the rules that will be refined or will be sued for prediction.
	 */
	public static String PATH_RULES = "";
	
	/**
	 * Path to the file that contains the rules that will be used as base,
	 * i.e. this rule set will be added to all other rule sets loaded.
	 */
	public static String PATH_RULES_BASE = "";
	
	/**
	 * Path to the output file where the rules / predictions  will be stored.
	 */
	public static String PATH_OUTPUT = "";
	
	/**
	 * Path to the output file where the rule index is stored.
	 */
	public static String PATH_RULE_INDEX = "";

	
	/**
	 * Path to the output file where statistics of the dice are stored.
	 * Can be used in reinforcement learning only. If the null value is not overwritten, nothing is stored.
	 */
	public static String PATH_DICE = null;
	
	
	/**
	 * Path to the output file where the explanations are stored. If not set, no explanations are stored.
	 * If set to the value BLIND, the computation of explanations is performed, however, they are not stored.
	 * This setting should be chosen for specific applications that access the explanation during execution.
	 */
	public static String PATH_EXPLANATION = null;
	
	public static PrintWriter EXPLANATION_WRITER = null;
	
	
	
	/**
	 * Takes a snapshot of the rules refined after each time interval specified in seconds.
	 */
	public static int[] SNAPSHOTS_AT = new int[] {10,100};
	
	

	
	
	/**
	* Number of maximal attempts to create body grounding. Every partial body grounding is counted.
	* 
	* NO LONGER IN USE (maybe)
	*/
	public static int TRIAL_SIZE = 1000000;
	
	
	/**
	 * Returns only results for head or tail computation if the results set has less elements than this bound.
	 * The idea is that any results set which has more elements is anyhow not useful for a top-k ranking. 
	 * Should be set to a value thats higher than the k of the requested top-k (however, the higher the value,
	 * the more runtime is required)
	 * 
	 * Value has been increased from 1000 to 10000 in July 2021 as some preliminary results indicate that this has a positive impact on the results quality
	 */
	public static int DISCRIMINATION_BOUND = 10000;	
	
	
	
	/**
	 * This is the upper limit that is allowed as branching factor in a cyclic rule.
	 * If more than this number of children would be created in the search tree, the branch is not visited.
	 * Note that this parameter is nor relevant for the last step, where the DISCRIMINATION_BOUND is the relevant parameter.
	 * 
	 */
	public static int BRANCHINGFACTOR_BOUND = 1000;	

		
	/**
	 * The time that is reserved for one batch in milliseconds. 
	 */
	public static int BATCH_TIME = 1000;
	
	
	
	/**
	 * The maximal number of body atoms in cyclic rules (inclusive this number). If this number is exceeded all computation time
	 * is used for acyclic rules only from that time on.
	 * 
	 */
	public static int MAX_LENGTH_CYCLIC = 3;
	
	
	
	/**
	 * Determines whether or not the zero rules e.g. (gender(X, male) <= [0.5]) are active
	 * Since July 2022 the default value for this type of rules has been set to false, which means that these rules
	 * are no longer mined in the default setting.
	 * 
	 */
	public static boolean ZERO_RULES_ACTIVE = false; 
	
	
	/**
	 * is used for cyclic rules only from that time on.
	 * 
	 */
	public static int MAX_LENGTH_ACYCLIC = 1;
	
	
	/**
	 * The maximal number of body atoms in partially grounded cyclic rules (inclusive this number). If this number is exceeded than a
	 * cyclic path that would allow to construct such a rule (where the constant in the head and in the body is the same) is used for constructing
	 * general rules only, partially grounded rules are not constructed from such a path.
	 */
	public static int MAX_LENGTH_GROUNDED_CYCLIC = 1;
	
	
	/**
	* Experiments have shown that AC2 rules seem make results worse for most datasets. Setting this parameter to true, will result into not learning
	* AC2 rules at all. The rules are not even constructed and computation time is this spent on the other rules-
	*  
	*/
	public static boolean EXCLUDE_AC2_RULES = false;
	
	/**
	 * The saturation defined when to stop the refinement process. Once the saturation is reached, no further refinements are searched for.
	 */
	public static double SATURATION = 0.99;
	
	/**
	 * The threshold for the number of correct prediction created with the rule.
	 */
	public static int THRESHOLD_CORRECT_PREDICTIONS = 2;

	/**
	 * The threshold for the number of correct prediction created with a Y-rule.
	 */
	public static int THRESHOLD_Y_CORRECT_PREDICTIONS = 10;
	
	
	/**
	 * The threshold for the number of correct predictions. Determines which rules are read from a file and which are ignored.
	 */
	public static int READ_THRESHOLD_CORRECT_PREDICTIONS = 2;
	
	/**
	 * The number of negative examples for which we assume that they exist, however, we have not seen them. Rules with high coverage are favored the higher the chosen number. 
	 */
	public static int UNSEEN_NEGATIVE_EXAMPLES = 5;	
	
	
	
	
	/**
	 * The number of negative examples for which we assume that they exist, however, we have not seen them.
	 * This number is for each refinements step, including the refinement of a refined rule.
	 */
	public static int UNSEEN_NEGATIVE_EXAMPLES_REFINE = 5;	
	
	/**
	* These number are added rule specific for in the application phase.
	*                      
	*                                                                U   C  AC1 AC2  X
	*/
	public static int[] UNSEEN_NEGATIVE_EXAMPLES_ATYPED = new int[] {0,  0,  0,  0,  0};

	/**
	 * The threshold for the confidence applied during rule learning (and maybe rule application).
	 */
	public static double THRESHOLD_CONFIDENCE = 0.0;

	/**
	 * The threshold for the confidence of a Y-rule.
	 */
	public static double THRESHOLD_Y_CONFIDENCE = 0.1;
	
	
	
	/**
	 * The threshold for the confidence of the rule. Determines which rules are read from a file by the rule reader.
	 */
	public static double READ_THRESHOLD_CONFIDENCE = 0.0;
	
	
	/**
	 * The maximal size of the rules that are stored when reading them from a file. 
	 * Determines which rules are read from a file by the rule reader.
	 * All rules with a body length > then this number are ignored.
	 */
	public static double READ_THRESHOLD_MAX_RULE_LENGTH = 10;
	
	/** 
	 * The number of worker threads which compute the scores of the constructed rules, should be one less then the number of available cores.
	 */
	public static int WORKER_THREADS = 3;
	
	
	/**
	* Defines how to combine probabilities that come from different rules
	* Possible values are: maxplus, noisyor, maxplus-explanation (currently under development), maxplus-explanation-stdout symbolic
	*/
	public static String AGGREGATION_TYPE = "maxplus";
	
	
	/**
	* If the aggregation type is set to maxplus-explanations, this parameter determines the number of explanations stored
	* in terms of the maximal site of the rule sets-
	*/
	public static int MAX_EXPLANATIONS = 0;
	
	/**
	* If the aggregation type is set to maxplus-explanations, this parameter determines if the correct hit is included in the explanation.
	* WANRING: Only set this to 1 if you are preparing the training set. If yozu set this to 1 for creating explanations for 
	* validation or test set it is cheating.
	*/
	public static int EXPLAIN_HIT = 0;
	
	
	/**
	 * This value is overwritten by the choice made via the AGGREGATION_TYPE parameter
	 */
	public static int AGGREGATION_ID = 1;
	
	/**
	 * No longer intended to be overwritten by properties file.
	 * Is automatically set by inspecting the validation set comparing it to the training set.
	 */
	public static boolean PREDICT_ONLY_UNCONNECTED = false;
	
	// NEW BEAM SAMPLING SETTINGS 
	
	/**
	 * This parameter determines how large the field is that is sampled during the index phase for groundings for, e.g., "? gender female".
	 * It forbids that very large sets are stored for such constellations is the example just shown. For must "? relation entity" it will have no
	 * impact. 
	 */
	public static int MAX_NUM_PRESAMPLING = 5000;
	
	
	/**
	* Used for restricting the number of samples drawn for computing scores as confidence.
	* This value is (probably) only relevant for the setting where beams are deactivated,
	* which is by default not the case, This means that the BEAM_SAMPLING_... are in
	* the default setting relevant. 
	* 
	* This value is in all settings still fully relevant for acyclic rules!!!
	* 
	*/
	public static int SAMPLE_SIZE = 2000;
	
	/**
	 * The maximal number of body groundings. Once this number of body groundings has been reached,
	 * the sampling process stops and confidence score is computed.
	 */
	public static int BEAM_SAMPLING_MAX_BODY_GROUNDINGS = 1000;
	
	/**
	 * The maximal number of attempts to create a body grounding. Once this number of attempts has been reached
	 * the sampling process stops and confidence score is computed.
	 */
	public static int BEAM_SAMPLING_MAX_BODY_GROUNDING_ATTEMPTS = 10000;
	
	/**
	 * If a rule has only few different body groundings this parameter prevents that all attempts are conducted.
	 * The value of this parameter determines how often a same grounding is drawn, before the sampling process stops
	 * and the and confidence score is computed, e.g, 5 means that the algorithm stops if 5 times a grounding is
	 * constructed that has been found previously. The higher the value the more probably it is that the sampling
	 * computes the correct value for rules with few body groundings.
	 */
	public static int BEAM_SAMPLING_MAX_REPETITIONS = 3;	
	
	
	/**
	 * The weights that is multiplied to compute the applied confidence of a zero rule. 
	 * 1.0 means that zero rules are treated in the same way as the other rules.
	 */
	public static double RULE_ZERO_WEIGHT = 0.01;	// default = 0.01
	
	/**
	 * The weights that is multiplied to compute the applied confidence of an AC2 rule. 
	 * 1.0 means that AC2 rules are treated in the same way as the other rules.
	 */
	public static double RULE_AC2_WEIGHT = 0.1; // default = 0.1
	

	/**
	 * The top-k results that are after filtering kept in the results. 
	 */
	public static int TOP_K_OUTPUT = 10;	
	

	

	/**
	 * If a complete explanation output is generated this parameter determines the top-k candidates from which a sample of
	 * TOP_K_OUTPUT candidates is chosen. You need to set TOP_K_OUTPUT_SAMPLING_FROM > TOP_K_OUTPUT if you want to make use of sampling.
	 * If this parameter is set to 0, sampling is not performed.
	 * 
	 */
	public static int TOP_K_OUTPUT_SAMPLING_FROM = 0;	
	
	public static int READ_CYCLIC_RULES = 1;
	public static int READ_ACYCLIC1_RULES = 1;
	public static int READ_ACYCLIC2_RULES = 1;
	public static int READ_ZERO_RULES = 1;
			
	/*
	static {
		
		KEYWORD = new HashMap<String, Integer>();
		KEYWORD.put("greedy", 1);
		KEYWORD.put("weighted", 2);
		KEYWORD.put("sup", 1);
		KEYWORD.put("supXcon", 3);
		KEYWORD.put("supXcon/lr", 5);
		KEYWORD.put("supXcon/rl", 5);
	}
	*/
	
	

}
