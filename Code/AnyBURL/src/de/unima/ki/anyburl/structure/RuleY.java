package de.unima.ki.anyburl.structure;

import java.util.HashSet;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.exceptions.TimeOutException;
import de.unima.ki.anyburl.exceptions.Timer;

public class RuleY extends RuleAcyclic {
	
	private RuleAcyclic1 r1 = null;
	private RuleAcyclic1 r2 = null;
	
	public RuleY(RuleUntyped r) {
		super(r);
	}
	
	public void setAC1Rules(RuleAcyclic1 r1, RuleAcyclic1 r2) {
		this.r1 = r1;
		this.r2 = r2;
	}
	
	public void computeScores(TripleSet triples) {
		Timer count = new Timer();
		if (this.isXRule()) {
			HashSet<String> xvalues = new HashSet<String>();
			if (Settings.BEAM_NOT_DFS) this.beamValuesReversed("X", xvalues, triples);
			else {
				try { this.computeValuesReversed("X", xvalues, triples, count); }
				catch(TimeOutException e) {};
			}
			int predicted = 0, correctlyPredicted = 0;
			for (String xvalue : xvalues) {
				predicted++;
				if (triples.isTrue(xvalue, this.head.getRelation(), this.head.getRight())) correctlyPredicted++;
			}
			this.predicted = predicted;
			this.correctlyPredicted = correctlyPredicted;
			this.confidence = (double)correctlyPredicted / (double)predicted;
		}
		else {
			HashSet<String> yvalues = new HashSet<String>();
			try { this.computeValuesReversed("Y", yvalues, triples, count); }
			catch (Exception e) { }
			int predicted = 0, correctlyPredicted = 0;
			for (String yvalue : yvalues) {
				predicted++;
				if (triples.isTrue(this.head.getLeft(), this.head.getRelation(), yvalue)) correctlyPredicted++;
			}
			this.predicted = predicted;
			this.correctlyPredicted = correctlyPredicted;
			this.confidence = (double)correctlyPredicted / (double)predicted;
		}
	}
	

	// a Y rule has no unbound variable
	protected String getUnboundVariable() {
		return null;
	}

	// an Y rule has no last atom
	public int getGroundingsLastAtom(TripleSet triples) {
		return 0;
	}

	@Override
	public boolean isSingleton(TripleSet triples) {
		// TODO Auto-generated method stub
		System.err.println("is singleton is not yet implemented for y types (not sure if its is required at all)");
		System.exit(1);
		return false;
	}
	
	

	
	

}
