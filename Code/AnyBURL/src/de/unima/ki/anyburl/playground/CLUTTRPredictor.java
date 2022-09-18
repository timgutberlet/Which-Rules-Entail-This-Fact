package de.unima.ki.anyburl.playground;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.structure.Rule;

public class CLUTTRPredictor {
	
	
	private LinkedList<Rule> rules;

	
	private HashMap<String, PredictedRelation> pair2Label;
	HashSet<String> relations;
	
	// data_089907f8 - the harder
	// data_db9b8f04 - the easier
	public static final String RULE_PATH = "E:/projects/patrick/data_db9b8f04/anyburl/rules-100";
	
	
	public static final String TEST_PATH = "E:/projects/patrick/data_db9b8f04/anyburl/test18.txt";
	
	public static final String TRAIN_PATH = "E:/projects/patrick/data_db9b8f04/anyburl/train.txt";
	

	
	
	public static void main(String[] args) throws IOException {
		
		
		Settings.READ_CYCLIC_RULES = 1;
		Settings.READ_ACYCLIC1_RULES = 0;
		Settings.READ_ACYCLIC2_RULES = 0;
		Settings.READ_ZERO_RULES = 0;
		Settings.READ_THRESHOLD_CONFIDENCE = 0.1;
		
		
		
		RuleReader rr = new RuleReader();
		
		LinkedList<Rule> rules = rr.read(RULE_PATH);
		
		System.out.println("read " + rules.size() + " rules");
		
		CLUTTRPredictor cp = new CLUTTRPredictor(rules);
		
		TripleSet testAll = new TripleSet(TEST_PATH);
		TripleSet trainAll = new TripleSet(TRAIN_PATH);
		
		HashSet<String> relations = new HashSet<String>();
		relations.addAll(trainAll.getRelations());
		
		int counter = 0;
		int counterCorrect = 0;
				
		
		for (int i = 1; i < 100000; i++) {
			String prefix = "t" + i + "_";
			TripleSet given = new TripleSet();
			Triple target = null;
			for (Triple t : testAll.getTriples()) {
				if (t.getHead().startsWith(prefix)) {
					if (t.getRelation().startsWith("?")) {
						target = new Triple(t.getHead(), t.getRelation().substring(2), t.getTail());		
					}
					else {
						given.addTriple(t);
					}
				}
			}
			if (given.size() == 0) {
				System.out.println("no further test case found ...\n");
				break;
			}
			else {
				System.out.print("predicting testcase " + prefix + ": ");
			}
			
			// System.out.println("given are " +  given.size() + " triples");
			for (Triple g : given.getTriples()) {
				// System.out.println("  >>>" + g);
			}
			// System.out.println("target = " + target);	
			String predictedRelation = cp.makePrediction(given, target);
			System.out.println(target + " => " + predictedRelation);
			if (target.getRelation().equals(predictedRelation)) counterCorrect++;
			counter++;
			// break;
		}
		
		double accuracy = (double)counterCorrect / (double)counter;
		System.out.println("Accuracy: " + accuracy);
		

	}
	
	public CLUTTRPredictor(LinkedList<Rule> rules) {
		this.rules = rules;
		// this.relations = relations;
		this.pair2Label = new HashMap<String,  PredictedRelation>();
		
		
	}
	
	public String makePrediction(TripleSet given, Triple target) {
		
		HashSet<String> entities = new HashSet<String>();
		
		for (Triple t : given.getTriples()) {
			entities.add(t.getHead());
			entities.add(t.getTail());
			this.pair2Label.put(t.getHead() + "-" + t.getTail(), new PredictedRelation(t.getRelation(), 1.0));
		}
		
		for (String e1 : entities) {
			for (String e2 : entities) {
				if (!e1.equals(e2)) {
					String pair = e1 + "-" + e2;
					if (!this.pair2Label.containsKey(pair)) this.pair2Label.put(pair, null);
				}
			}
		}
		
		
		boolean solved = false;
		boolean unsolvable = false;
		String predictedRelation = null;
		
		
		
		HashSet<String> freshPredictedPairs = new  HashSet<String>();
		
		int iteration_counter = 0;
		
		do {
			iteration_counter++;
			// System.out.println("ITERATION: " + iteration_counter);
			freshPredictedPairs.clear();
			for (String entity : entities) {
				for (Rule r : rules) {
					HashSet<String> predictedTails = r.computeTailResults(entity, given);
					String relation = r.getTargetRelation();
					for (String predictedTail : predictedTails) {
						String pair = entity + "-" + predictedTail;
						PredictedRelation pr = this.pair2Label.get(pair);
						if (pr == null) {
							this.pair2Label.put(pair, new PredictedRelation(relation, r.getAppliedConfidence()));
							freshPredictedPairs.add(pair);
							// System.out.println("-> predicted: " + entity + " " + relation + " " + predictedTail + " with confidence " + r.getConfidence());
						}
						else {
							if (pr.confidence < r.getAppliedConfidence()) {		
								// System.out.println("-> predicted: " + entity + " " + relation + " " + predictedTail + " with confidence " + r.getConfidence());
								this.pair2Label.put(pair, new PredictedRelation(relation, r.getAppliedConfidence()));
								freshPredictedPairs.add(pair);
							}
						}
							
						if (target.getHead().equals(entity) && target.getTail().equals(predictedTail)) {
							predictedRelation = relation;
							solved = true;
						}
					}
				}
			}
			
			for (String freshPair : freshPredictedPairs) {
				String[] token = freshPair.split("-");
				PredictedRelation pr = this.pair2Label.get(freshPair);
				// System.out.println("added " + pr);
				Triple freshTriple = new Triple(token[0], this.pair2Label.get(freshPair).relation, token[1]);
				given.addTriple(freshTriple);
				// System.out.println("added this fresh triple: " + freshTriple);
				
			}
			
			if (freshPredictedPairs.isEmpty() && solved == false) unsolvable = true;
			
			
			
			
		} while(!solved && !unsolvable);
		return predictedRelation;
		
	
	}
	
	

	
	

}
