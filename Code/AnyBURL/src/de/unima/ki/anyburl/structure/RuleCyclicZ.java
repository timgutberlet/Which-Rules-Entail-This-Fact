package de.unima.ki.anyburl.structure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.SampledPairedResultSet;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.exceptions.Timer;
import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.exceptions.TimeOutException;

public class RuleCyclicZ extends RuleCyclic  {
	
	private int lowerZBound = 0;
	private int upperZBound = 0;
	
	
	private boolean isMaster = false;
	private RuleCyclicZ master = null;
	
	

	public static void main(String[] args) throws IOException {
		RuleReader rr = new RuleReader();
		LinkedList<Rule> rules = rr.read("exp/grounding/ruletest/example-rules.txt");
		for (Rule r: rules) {
			System.out.println(r);
		}
	}

	
	public RuleCyclicZ(RuleUntyped r) {
		super(r);
		this.confidence = (double)this.correctlyPredicted / (double)this.predicted;
	}
	
	public TripleSet materialize(TripleSet trainingSet) {
		throw new RuntimeException("functionality not available for this type of rule");
	}


	public HashSet<String> computeTailResults(String head, TripleSet ts) {
		
		HashSet<String> results = new HashSet<String>();
		Timer count = new Timer();
		this.getCyclic("X", "Y", head, 0, true, ts,  new HashSet<String>(), results, count);
		HashSet<String> zResults = new HashSet<String>();
		for (String tail : results) {
			 int zcard = this.getZCardinality(ts, head, tail);
			 if (zcard >= lowerZBound && zcard <= upperZBound) zResults.add(tail);
		}
		return zResults;
	}


	public HashSet<String> computeHeadResults(String tail, TripleSet ts) {
		HashSet<String> results = new HashSet<String>();
		Timer count = new Timer();
		super.getCyclic("Y", "X", tail, this.bodysize() - 1, false, ts,  new HashSet<String>(), results, count);
		HashSet<String> zResults = new HashSet<String>();
		for (String head : results) {
			 int zcard = this.getZCardinality(ts, head, tail);
			 if (zcard >= lowerZBound && zcard <= upperZBound) zResults.add(head);
		}
		return zResults;
	}
	
	@Override
	public void computeScores(TripleSet triples) {
		throw new RuntimeException("functionality not available for this type of rule");
	}
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.predicted + "\t");
		sb.append(this.correctlyPredicted + "\t");
		sb.append(this.lowerZBound + ".." +  this.upperZBound + "\t");
		sb.append(this.head);
		sb.append(" <= ");
		sb.append(this.body.toString());
		return sb.toString();
	}
	

	public void setBounds(int lowerZBound, int upperZBound) {
		this.lowerZBound = lowerZBound;
		this.upperZBound = upperZBound;
	}


	public void setMaster(RuleCyclicZ zMaster) {
		this.master = zMaster;
	}
	
	public RuleCyclicZ getMaster() {
		return this.master;
	}
	
	public boolean isMaster() {
		return this.isMaster;
	}
	
	public void becomeMaster() {
		this.isMaster = true;
	}

	
}
