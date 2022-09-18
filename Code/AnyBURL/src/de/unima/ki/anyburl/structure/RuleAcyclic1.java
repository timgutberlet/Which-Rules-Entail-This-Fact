package de.unima.ki.anyburl.structure;

import java.util.HashSet;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.TripleSet;


/**
 * A rule of the form h(a,X) <= b1(X,A1), ..., bn(An-1,c) with a constant in the head and in the last body atom.
 * 
 */
public class RuleAcyclic1 extends RuleAcyclic {

	
	public RuleAcyclic1(RuleUntyped r) {
		super(r);
	}
	


	protected String getUnboundVariable() {
		return null; 
	}
	
	
	// public double getAppliedConfidence() {
	// 	return (double)this.getCorrectlyPredictedHeads() / ((double)this.getPredictedHeads() + Settings.UNSEEN_NEGATIVE_EXAMPLES + Settings.UNSEEN_NEGATIVE_EXAMPLES_ATYPED[2]);
	// }
	
	/*
	public double getAppliedConfidence() {
		return (double)this.getCorrectlyPredicted() / ((double)this.getPredicted() + Math.pow(Settings.UNSEEN_NEGATIVE_EXAMPLES, this.bodysize()));
	}
	*/
	
	
	public double getAppliedConfidence() {
		if (Settings.EXPERIMENTAL_AC120_AGGREGATION_ON) {
			RuleAcyclic2 parent = (RuleAcyclic2)this.getParentRule();
			RuleAcyclic2 grandparent = null;
			if (parent != null) grandparent = (RuleAcyclic2)this.getParentRule();
			return super.getAppliedConfidence(new Rule[]{ this, parent, grandparent });
		}
		else {
			return super.getAppliedConfidence();
		}
	
	}
	

	/**
	* Returns the number of groundings w.r.t the given triple set for the variable in the last atom.
	* @param triples The triples set to check for groundings.
	* @return The number of groundings.
	*/
	public int getGroundingsLastAtom(TripleSet triples) {
		Atom last = this.body.getLast();
		if (last.isRightC()) {
			return triples.getHeadEntities(last.getRelation(), last.getRight()).size();
		}
		else {
			return triples.getTailEntities(last.getRelation(), last.getLeft()).size();
		}
	}




	public boolean isSingleton(TripleSet triples) {
		// return false;
		
		if (this.body.get(0).getRight().equals("X") && this.body.get(0).getRight().equals("Y")) {
			String head = this.body.get(0).getLeft();
			String relation = this.body.get(0).getRelation();
			if (triples.getTailEntities(relation, head).size() > 1) return false;
			else return true;
		}
		else {
			String tail = this.body.get(0).getRight();
			String relation = this.body.get(0).getRelation();
			if (triples.getHeadEntities(relation, tail).size() > 1) return false;
			else return true;
		}
		
	}
	
	
	public boolean isMoreSpecific(Rule general) {
		if (general instanceof RuleCyclic && this.isCyclic()) {
			RuleCyclic rg = (RuleCyclic)general;
			if (!this.head.getXYGeneralization().equals(rg.head)) return false;
			if (this.bodysize() == rg.bodysize()) {
				if (this.body.get(0).contains("X")) {
					for (int i = 0; i < this.bodysize() - 1; i++) {
						if (!this.body.get(i).equals(rg.body.get(i))) return false;
					}
					if (this.body.get(this.bodysize()- 1).moreSpecial(rg.body.get(this.bodysize()-1))) return true;
					return false;
				}
				else {
					String vSpecific = "Y";
					String vGeneral = "Y";
					for (int i = 0; i < this.bodysize() - 1; i++) {
						Atom sAtom = this.body.get(i);
						Atom gAtom = rg.body.get(this.bodysize() - 1 - i);
						if (!sAtom.equals(gAtom, vSpecific, vGeneral)) return false;
						vSpecific = sAtom.getOtherTerm(vSpecific);
						vGeneral = gAtom.getOtherTerm(vGeneral);
					}
					if (this.body.get(this.bodysize()- 1).moreSpecial(rg.body.get(0), vSpecific, vGeneral)) return true;	
				}
			}
		}
		if (general instanceof RuleAcyclic2) {
			// System.out.println("this:     " + this);
			// System.out.println("general:  " + general);
			
			RuleAcyclic2 rg = (RuleAcyclic2)general;
			if (!this.head.equals(rg.head)) return false;
			if (this.bodysize() > rg.bodysize()) return false;
			for (int i = 0; i < this.bodysize() - 1; i++) {
				if (!this.body.get(i).equals(rg.body.get(i))) {
					return false;
				}
			}
			if (this.body.get(this.bodysize()- 1).moreSpecial(rg.body.get(this.bodysize()-1))) return true;
			
		}
		return false;
	}


	public boolean isCyclic() {
		if (this.getHead().getConstant().equals(this.body.getLast().getConstant())) return true;
		return false;
	}


	public String toXYString() {
		if (this.head.getLeft().equals("X")) {
			String c = this.head.getRight();
			StringBuilder sb = new StringBuilder();
			sb.append(this.getHead().toString(c, "Y"));
			for (int i = 0; i < this.bodysize(); i++) { sb.append(this.getBodyAtom(i).toString(c,"Y")); }
			String rs = sb.toString();
			return rs;
		}
		if (this.head.getRight().equals("Y")) {
			String c = this.head.getLeft();
			StringBuilder sb = new StringBuilder();
			sb.append(this.getHead().toString(c, "X"));
			for (int i = this.bodysize()-1; i >= 0; i--) { sb.append(this.getBodyAtom(i).toString(c,"X")); }
			String rs = sb.toString();
			return rs;
		}
		System.err.println("toXYString of the following rule not implemented: " + this);
		System.exit(1);
		return null;
	}


	public boolean validates(String h, String relation, String t, TripleSet ts) {
		if (this.getTargetRelation().equals(relation)) {
			// this rule is a X rule
			if (this.head.isRightC() && this.head.getRight().equals(t)) {
				// could be true if body is true
				HashSet<String> previousValues = new HashSet<String>();
				previousValues.add(h);
				previousValues.add(this.head.getRight());
				return (this.isBodyTrueAcyclic("X", h, 0, previousValues, ts));
			}
			// this rule is a Y rule
			if (this.head.isLeftC() && this.head.getLeft().equals(h)) {
				// could be true if body is true
				
				HashSet<String> previousValues = new HashSet<String>();
				previousValues.add(t);
				previousValues.add(this.head.getLeft());
				return (this.isBodyTrueAcyclic("Y", t, 0, previousValues, ts));
			}
			return false;
		}
		return false;
		
	}




}
