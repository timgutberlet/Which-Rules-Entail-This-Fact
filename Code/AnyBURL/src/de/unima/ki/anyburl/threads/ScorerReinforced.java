package de.unima.ki.anyburl.threads;


import java.util.ArrayList;


import de.unima.ki.anyburl.LearnReinforced;
import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.algorithm.PathSampler;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.exceptions.TimeOutException;
import de.unima.ki.anyburl.structure.Path;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleAcyclic1;
import de.unima.ki.anyburl.structure.RuleFactory;
import de.unima.ki.anyburl.util.LogSpecial;

/**
 * 
 * The worker thread responsible for learning rules in the reinforced learning setting.
 * 
 */
public class ScorerReinforced extends Thread {
	

	private TripleSet triples;
	private PathSampler sampler;
	
	// private int entailedCounter = 1;

	
	private int createdRules = 0;
	private int storedRules = 0;
	private double producedScore = 0.0;
	
	private int id = 0;
	
	public enum MineParam {MINE_CYCLIC, MINE_ACYCLIC, MINE_ZERO, MINE_Y};
	
	private MineParam mine = MineParam.MINE_CYCLIC;

	// this is not really well done, exactly one of them has to be true all the time
	// private boolean mineParamCyclic = true;
	// private boolean mineParamAcyclic = false;
	// private boolean mineParamZero = false;
	
	private int mineParamLength = 1; // possible values are 1 and 2 (if non-cyclic), or 1, 2, 3, 4, 5 if (cyclic)

	
	private boolean ready = false;
	
	private boolean onlyXY = false;
	
	public boolean done = false;
	
	
	
	// ***** lets go ******
	
	public ScorerReinforced(TripleSet triples, int id) {
		this.triples = triples;
		this.sampler = new PathSampler(triples);
		this.id = id;
	}
	
	public void setSearchParameters(MineParam param, int len) {
		this.mine = param;
		this.mineParamLength = len;
		this.ready = true;
		this.onlyXY = false;
		if (this.mine == MineParam.MINE_CYCLIC) {
			if (this.mineParamLength > Settings.MAX_LENGTH_GROUNDED_CYCLIC)	{
				this.onlyXY = true;
			}
		}
	}
	
	
	private MineParam getType() {
		return this.mine;		
	}
	
	
	public void run() {
		
		while (!LearnReinforced.areAllThere()) {
			LearnReinforced.heyYouImHere(this.id);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// System.out.println("THREAD-" + this.id + " waiting for the others");
		}	
		System.out.println("THREAD-" + this.id + " starts to work with L=" + this.mineParamLength + " C=" + this.getType() + " ");
		
		// outer loop is missing

		// boolean done = false;
		
		// long loopCounter = 0;
		
		while (done == false) {
			/*
			loopCounter++;
			if (loopCounter % 1000 == 0) {
				LogSpecial.write("thread with id=" + this.id + " has loopCounter=" + loopCounter);
			}
			*/
			if (!LearnReinforced.active(this.id, this.storedRules, this.createdRules, this.producedScore, this.mine, this.mineParamLength) || !ready) {
				this.createdRules = 0;
				this.storedRules = 0;
				this.producedScore = 0.0;
				try { Thread.sleep(10);	}
				catch (InterruptedException e) {
					e.printStackTrace();
					// done = true;
				}
			}
			else {
				
				
				ArrayList<Rule> learnedRulesLog = new ArrayList<Rule>();
				// search for zero rules
				if (this.mine == MineParam.MINE_ZERO) {
					Path path = sampler.samplePath(this.mineParamLength + 1, false);
					// System.out.println("zero (sample with steps=" + (this.mineParamLength+1) + "):" + path);
					
					if (path != null) {
						ArrayList<Rule> learnedRules = RuleFactory.getGeneralizations(path, false);
						learnedRulesLog = learnedRules;
						if (!LearnReinforced.active) {
							try { Thread.sleep(10); }
							catch (InterruptedException e) { e.printStackTrace(); }
						}
						else {
							for (Rule learnedRule : learnedRules) {
								this.createdRules++;
								if (learnedRule.isTrivial()) continue;
								if (LearnReinforced.isNotYetStored(learnedRule)) {
									try { learnedRule.computeScores(this.triples); }
									catch(TimeOutException e) { continue; }
									if (learnedRule.getConfidence() >= Settings.THRESHOLD_CONFIDENCE && learnedRule.getCorrectlyPredicted() >= Settings.THRESHOLD_CORRECT_PREDICTIONS) {
										if (LearnReinforced.active) {
											LearnReinforced.storeRule(learnedRule);
											this.producedScore += getScoringGain(learnedRule, learnedRule.getCorrectlyPredicted(), learnedRule.getConfidence(), learnedRule.getAppliedConfidence());
											this.storedRules++;	
										}
									}
								}
							}
						}
					}
				}
				// search for 
				
				
				// search for cyclic rules
				if (this.mine == MineParam.MINE_CYCLIC) {
					Path path = sampler.samplePath(this.mineParamLength + 1, true);
					if (path != null && path.isValid()) {
						// System.out.println(path);
						ArrayList<Rule> learnedRules = RuleFactory.getGeneralizations(path, this.onlyXY);
						learnedRulesLog = learnedRules;
						// System.out.println(learnedRules.size());
						if (!LearnReinforced.active) {
							try { Thread.sleep(10); }
							catch (InterruptedException e) { e.printStackTrace(); }
						}
						else {
							for (Rule learnedRule : learnedRules) {
								this.createdRules++;
								if (learnedRule.isTrivial()) continue;
								if (LearnReinforced.isNotYetStored(learnedRule)) {
									// long ts1 = System.currentTimeMillis();
									learnedRule.computeScores(this.triples);
									// long ts2 = System.currentTimeMillis();
									// if (ts2-ts1 > 2000) {
									//	LogSpecial.write("# Time required for mining a cyclic rule: " + (ts2-ts1));
									//	LogSpecial.write("# Rule: " + learnedRule + "\n");
									//	LogSpecial.flush();
									//}
								
									if (learnedRule.getConfidence() >= Settings.THRESHOLD_CONFIDENCE && learnedRule.getCorrectlyPredicted() >= Settings.THRESHOLD_CORRECT_PREDICTIONS) {
										if (LearnReinforced.active) {
											LearnReinforced.storeRule(learnedRule);											
											// this.producedScore += getScoringGain(learnedRule.getCorrectlyPredictedMax(), learnedRule.getConfidenceMax());
											this.producedScore += getScoringGain(learnedRule, learnedRule.getCorrectlyPredicted(), learnedRule.getConfidence(), learnedRule.getAppliedConfidence());
											this.storedRules++;
										}
									}
								}
							}
						}
					}	
				}
				// search for acyclic rules
				if (this.mine == MineParam.MINE_ACYCLIC) {
					Path path = sampler.samplePath(mineParamLength + 1, false);
					if (path != null && path.isValid()) {
						ArrayList<Rule> learnedRules = RuleFactory.getGeneralizations(path, false);
						learnedRulesLog = learnedRules;
						if (!LearnReinforced.active) {
							try { Thread.sleep(10); }
							catch (InterruptedException e) { e.printStackTrace(); }
						}
						else {
							for (Rule learnedRule : learnedRules) {
								this.createdRules++;
								if (learnedRule.isTrivial()) continue;
								if (LearnReinforced.isNotYetStored(learnedRule)) {
									try  {
										//long ts1 = System.currentTimeMillis();
										learnedRule.computeScores(this.triples);
										//long ts2 = System.currentTimeMillis();
										//if (ts2-ts1 > 2000) {
										//	LogSpecial.write("# Time required: " + (ts2-ts1));
										//	LogSpecial.write("# Rule: " + learnedRule + "\n");
										//	LogSpecial.flush();
										//}	
									
									
									}
									catch(TimeOutException e) { continue; }
									if (learnedRule.getConfidence() >= Settings.THRESHOLD_CONFIDENCE && learnedRule.getCorrectlyPredicted() >= Settings.THRESHOLD_CORRECT_PREDICTIONS) {
										if (LearnReinforced.active) {
											LearnReinforced.storeRule(learnedRule);
											this.producedScore += getScoringGain(learnedRule, learnedRule.getCorrectlyPredicted(), learnedRule.getConfidence(), learnedRule.getAppliedConfidence());
											this.storedRules++;	
										}
									}
								}
							}
						}
					}	
				}
				// long ts2 = System.currentTimeMillis();
				/*
				if (ts2 - ts1 > 100) {
					LogSpecial.write("thread " + this.id + " required more than 100 ms for a single rule set that origins from the same path");
					for (Rule r : learnedRulesLog) {
						LogSpecial.write("   " + r);
					}
					
				}
				*/
			}
		}
	}
	
	public double getScoringGain(Rule rule, int correctlyPredicted, double confidence, double appliedConfidence ) {
		if (Settings.REWARD == 1) return (double)correctlyPredicted;
		if (Settings.REWARD == 2) return (double)correctlyPredicted * confidence;
		if (Settings.REWARD == 3) return (double)correctlyPredicted * appliedConfidence;
		if (Settings.REWARD == 4) return (double)correctlyPredicted * appliedConfidence * appliedConfidence;
		if (Settings.REWARD == 5) return (double)correctlyPredicted * appliedConfidence / Math.pow(2, (rule.bodysize()-1));
		return 0.0;
	}

}
