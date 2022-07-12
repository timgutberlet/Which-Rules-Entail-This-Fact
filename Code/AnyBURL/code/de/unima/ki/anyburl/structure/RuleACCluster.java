package de.unima.ki.anyburl.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;


/**
 * Has not been used. This cluster is intended to be a cluster of an AC2 rule and each AC1 rule that can be constructed from the AC2 rule by
 * replacing the free variable in the last body atom by a constant. It well help to finally implement the appropriate aggregation strategy. 
 *
 * Dropped this idea, probably this class ca be removed again.
 *
 */
public class RuleACCluster {
	
	
	
	private RuleAcyclic2 motherRule;
	
	HashMap<String, RuleAcyclic1> bodyConstant2Confidence = new HashMap<String,  RuleAcyclic1>();
	
	
	
	public RuleACCluster(RuleAcyclic2 motherRule) {
		this.motherRule = motherRule;
	}
	
	/**
	 * This method does not check if the added AC1 rule really fits with the AC2 mother rule.
	 * 
	 * @param ruleAC1 The rule to be added.
	 */
	public void addAC1Rule(RuleAcyclic1 ruleAC1) {
		String c = ruleAC1.getBodyAtom(0).getConstant();
		
		
		
	}
	
	
	/**
	 * This method is not really called for a cluster, because the cluster is not created during learning
	 * but aggregated from learned rules.
	 * 
	 */

	public void computeScores(TripleSet ts) {
		// do nothing, not required

	}

	
	public HashMap<String, Double> computeAnnotatedTailResults(String head, TripleSet ts) {
		HashMap<String, Double> annotatedResults = new HashMap<String, Double>();
		Atom bodyAtom = this.motherRule.getBodyAtom(0);
		if (this.motherRule.getHead().isLeftC()) { // h(c,X) <=
			if (!(head.equals(this.motherRule.getHead().getLeft()))) return annotatedResults;
			String unboundVariable = this.motherRule.getUnboundVariable();
			ArrayList<Triple> triples = ts.getTriplesByRelation(bodyAtom.getRelation());	
			// h(X,c) <= b(X,A)
			if (bodyAtom.getRight().equals(unboundVariable)) {
				checkBodyAtom1(annotatedResults, triples, true);
			}
			// h(X,c) <= b(A,X)
			else {
				checkBodyAtom1(annotatedResults, triples, false);
			}
		}
		else { // h(X,c) <=
			// h(c,X) <= b(X,A)
			if (bodyAtom.getRight().equals(this.motherRule.getUnboundVariable())) {
				Set<String> unboundValues = ts.getTailEntities(bodyAtom.getRelation(), head);
				checkBodyAtom2(annotatedResults, unboundValues, this.motherRule.getHead().getRight());
			}
			// h(c,X) <= b(A,X)
			if (bodyAtom.getRight().equals(this.motherRule.getUnboundVariable())) {
				Set<String> unboundValues = ts.getTailEntities(bodyAtom.getRelation(), head);
				checkBodyAtom2(annotatedResults, unboundValues, this.motherRule.getHead().getRight());
			}
		}
		return annotatedResults;
	}
	
	
	// here we go first of all ----
	
	/**
	 * Computes, given a fix tail c, the head results for a query r(?,c).
	 * It is assumed that the correctness of the relation r is checked prior to calling the method.
	 * 
	 * 
	 * @param tail The given fix tail constant.
	 * @param ts The triples set which is the known evidence.
	 * @return The results set where each result is annotated with a confidence.
	 */
	public HashMap<String, Double> computeAnnotatedHeadResults(String tail, TripleSet ts) {
		HashMap<String, Double> annotatedResults = new HashMap<String, Double>();
		Atom bodyAtom = this.motherRule.getBodyAtom(0);
		if (this.motherRule.getHead().isRightC()) { // h(X,c) <=
			if (!(tail.equals(this.motherRule.getHead().getRight()))) return annotatedResults;
			String unboundVariable = this.motherRule.getUnboundVariable();
			ArrayList<Triple> triples = ts.getTriplesByRelation(bodyAtom.getRelation());	
			// h(X,c) <= b(X,A)
			if (bodyAtom.getRight().equals(unboundVariable)) {
				checkBodyAtom1(annotatedResults, triples, true);
			}
			// h(X,c) <= b(A,X)
			else {
				checkBodyAtom1(annotatedResults, triples, false);
			}
		}
		else { // h(c,X) <=
			// h(c,X) <= b(X,A)
			if (bodyAtom.getRight().equals(this.motherRule.getUnboundVariable())) {
				Set<String> unboundValues = ts.getTailEntities(bodyAtom.getRelation(), tail);
				checkBodyAtom2(annotatedResults, unboundValues, this.motherRule.getHead().getLeft());
			}
			// h(c,X) <= b(A,X)
			if (bodyAtom.getRight().equals(this.motherRule.getUnboundVariable())) {
				Set<String> unboundValues = ts.getTailEntities(bodyAtom.getRelation(), tail);
				checkBodyAtom2(annotatedResults, unboundValues, this.motherRule.getHead().getLeft());
			}
		}
		return annotatedResults;
	}

	private void checkBodyAtom1(HashMap<String, Double> annotatedResults, ArrayList<Triple> triples, boolean headNotTail) {
		for (Triple t : triples) {
			String unboundValue = t.getValue(!headNotTail);
			RuleAcyclic1 rule = this.bodyConstant2Confidence.get(unboundValue);
			if (rule == null) annotatedResults.put(t.getValue(headNotTail), this.motherRule.getAppliedConfidence());
			else annotatedResults.put(t.getValue(headNotTail), rule.getAppliedConfidence());
		}
	}

	private void checkBodyAtom2(HashMap<String, Double> annotatedResults, Set<String> unboundValues, String resultingValue) {
		boolean specificRuleFired = false;
		double maxSpecificConfidence = 0.0;
		
		
		for (String unboundValue : unboundValues) {
			RuleAcyclic1 rule = this.bodyConstant2Confidence.get(unboundValue);
			if (rule == null && !specificRuleFired) annotatedResults.put(this.motherRule.getHead().getLeft(), this.motherRule.getAppliedConfidence());
			if (rule != null) {
				double c = rule.getAppliedConfidence();
				if (c > maxSpecificConfidence) {
					specificRuleFired = true;
					maxSpecificConfidence = c;
					annotatedResults.put(resultingValue, c);	
				}
			}
		}
	}
	


}
