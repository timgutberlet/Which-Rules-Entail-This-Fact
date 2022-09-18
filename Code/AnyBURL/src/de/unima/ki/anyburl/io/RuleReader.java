package de.unima.ki.anyburl.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.structure.Atom;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleAcyclic1;
import de.unima.ki.anyburl.structure.RuleAcyclic2;
import de.unima.ki.anyburl.structure.RuleCyclic;
import de.unima.ki.anyburl.structure.RuleCyclicZ;
import de.unima.ki.anyburl.structure.RuleUntyped;
import de.unima.ki.anyburl.structure.RuleZero;


public class RuleReader {
	
	public static final int TYPE_UNDEFINED = 0;
	
	public static final int TYPE_CYCLIC = 1;
	public static final int TYPE_ACYCLIC = 2;
	public static final int TYPE_REFINED = 3;
	
	public static void main(String[] args) throws IOException {
		RuleReader rr = new RuleReader();
		LinkedList<Rule> r1 = rr.read("exp/zero/fb237-rules-1000");

		for (Rule r : r1) {
			if (r.getHead().toString().startsWith("/film/film/story_by") && r.getHead().toString().contains("X,Y") && r.getAppliedConfidence() > 0.001) {
				System.out.println(r);
			}
		}
		
		
		// /music/genre/artists(/m/0dl5d,Y)
	}
	
	
	/**
	* @param filepath The file to read the rules from.
	* @returnA list of rules.
	* @throws IOException
	*/
	public LinkedList<Rule> read(String filepath) throws IOException {
		System.out.print("* reading rules from " +  filepath + "");
		// int i = 0;
		LinkedList<Rule> rules = new LinkedList<Rule>();
		// HashMap<Long, Rule> ids2Rules = new HashMap<Long,Rule>();
		File file = new File(filepath);
		BufferedReader br = new BufferedReader((new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
		long counter = 0;
		try {
		    String line = br.readLine();
		    while (line != null) {
		        if (line == null || line.equals("")) break;
		        Rule r = null;
		        try {
		        	r = this.getRule(line);
		        }
		        catch(Exception e) {
		        	System.err.println(e);
		        	System.err.println("caused by the line: " + line);

		        	System.exit(1);
		        	
		        }
		        if (r != null && r.getConfidence() >= Settings.READ_THRESHOLD_CONFIDENCE && r.getCorrectlyPredicted() >= Settings.READ_THRESHOLD_CORRECT_PREDICTIONS && r.bodysize() <= Settings.READ_THRESHOLD_MAX_RULE_LENGTH) {
		        	rules.add(r);
		        	counter++;
		        	if (counter % 1000000 == 0) System.out.print(" ~");
		        }
		        line = br.readLine();
		    }
		}
		finally {  br.close(); }
		System.out.println(", read " + rules.size() + " rules");
		return rules;
	}
	
	private void indexZRules(LinkedList<Rule> rules) {
		
		HashMap<String, HashSet<RuleCyclicZ>> zRules = new HashMap<String, HashSet<RuleCyclicZ>>();
		
		for (Rule r : rules) {
			if (r instanceof RuleCyclicZ) {
				String irs = r.toInnerRuleString();
				if (!zRules.containsKey(irs)) zRules.put(irs, new HashSet<RuleCyclicZ>());
				zRules.get(irs).add((RuleCyclicZ)r);
			}
		}
		
		
		for (String key : zRules.keySet()) {
			double maxConf = 0.0;
			RuleCyclicZ zMaster = null;
			for (RuleCyclicZ rz : zRules.get(key)) {
				if (rz.getAppliedConfidence() > maxConf) {
					zMaster = rz;
					maxConf = rz.getAppliedConfidence();
				}
			}
			zMaster.becomeMaster();
			for (RuleCyclicZ rz : zRules.get(key)) {
				rz.setMaster(zMaster);
			}
			
		}
		
		
		
	}
	
	
	
	
	/**
	* @param filepath The file to read the rules from.
	* @returnA list of rules.
	* @throws IOException
	*/
	public LinkedList<Rule> readRefinable(String filepath) throws IOException {
		// int i = 0;
		LinkedList<Rule> rules = new LinkedList<Rule>();
		// HashMap<Long, Rule> ids2Rules = new HashMap<Long,Rule>();
		File file = new File(filepath);
		BufferedReader br = new BufferedReader((new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)));
		try {
		    String line = br.readLine();
		    while (line != null) {
		        if (line == null || line.equals("")) break;
		        // System.out.println(line);
		        Rule r = this.getRule(line);
		        if (r != null) {
		        	if (r.isRefinable()) rules.add(r);
		        }
		        line = br.readLine();
		    }
		}
		finally {  br.close(); }
		return rules;
	}
	
	public Rule getRule(String line) {
		if (line.startsWith("#")) return null;
		String token[] = line.split("\t");
		// rule with constant in head
		RuleUntyped r = null;
		
		double confidence = 0.0;
		int[] zBounds = new int[2];
		boolean zBounded = false;
		if (token[2].contains("..")) {
			String[] zBoundsTokens = token[2].split("\\.\\.");
			zBounds[0] = Integer.parseInt(zBoundsTokens[0]);
			zBounds[1] = Integer.parseInt(zBoundsTokens[1]);
			zBounded = true;
		}
		else confidence = Double.parseDouble(token[2]);
		if (token.length == 4) {
			r = new RuleUntyped(
					Integer.parseInt(token[0]),
					Integer.parseInt(token[1]),
					confidence
			);
		}
		if (token.length == 7) {
			System.err.println("you are trying to read am old rule set which is based on head/tail distiction not yet supported anymore");
			System.exit(0);
		}
		r  = (RuleUntyped)r;
		String atomsS[] = token[token.length-1].split(" ");
		r.setHead(new Atom(atomsS[0]));
		for (int i = 2; i < atomsS.length; i++) {
			Atom lit = new Atom(atomsS[i]);
			r.addBodyAtom(lit);
		}
		if (r.isCyclic()) {
			if (Settings.READ_CYCLIC_RULES == 1) {
				if (zBounded) {
					if (r.bodysize() == 2 || r.bodysize() == 3) {
						RuleCyclicZ rcz = new RuleCyclicZ(r);
						rcz.setBounds(zBounds[0], zBounds[1]);
						return rcz;
					}
					else {
						System.err.println("the following line in the rule file uses a z-refinement syntax and is not a cyclic rule of length 2 or 3 (z-refinement is not supported for other rule types so far)");
						System.err.println(line);
						System.exit(0);
					}
				}
				else return new RuleCyclic(r);
			}
		}
		if (r.isAcyclic1()) {
			if (Settings.READ_ACYCLIC1_RULES == 1) return new RuleAcyclic1(r);
		}
		if (r.isAcyclic2())  {
			if (Settings.READ_ACYCLIC2_RULES == 1) return new RuleAcyclic2(r);
		}
		if (r.isZero())  {
			if (Settings.READ_ZERO_RULES == 1) return new RuleZero(r);
		}
		
		
		return null;
	}
	
}
