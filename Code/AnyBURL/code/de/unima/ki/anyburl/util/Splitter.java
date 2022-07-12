package de.unima.ki.anyburl.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

import de.unima.ki.anyburl.io.RuleReader;
import de.unima.ki.anyburl.structure.Rule;
import de.unima.ki.anyburl.structure.RuleAcyclic1;
import de.unima.ki.anyburl.structure.RuleAcyclic2;
import de.unima.ki.anyburl.structure.RuleCyclic;

public class Splitter {

	public static void main(String[] args) throws IOException {
		String filepath = "";
		int ruleLength = 3;
		// System.out.println("num of arguments = " + args.length);
		if (args.length >= 1) filepath = args[0];
		if (args.length == 2) {
			ruleLength = Integer.parseInt(args[1]);
		}
		RuleReader rr = new RuleReader();
		LinkedList<Rule> rules = rr.read(filepath);
		
		
		ArrayList<String> types = new ArrayList<String>();
		types.add("ac1L1");
		types.add("ac2L1");
		
		for (int i = 1; i<= ruleLength; i++) {
			types.add("cL" + i);
			types.add("ac1L" + i + "X");
		}
		
		
		
		// 
		for (String type : types) {
			long counter = 0;
			PrintWriter pw = new PrintWriter(filepath + "-without-" + type);
			for (Rule r : rules) {
				if (type.equals("ac1L1")) {
					if (r.bodysize() == 1 && r instanceof RuleAcyclic1) continue;
				}
				if (type.equals("ac2L1")) {
					if (r.bodysize() == 1 && r instanceof RuleAcyclic2) continue;
				}
				if (type.equals("cL1")) {
					if (r.bodysize() == 1 && r instanceof RuleCyclic) continue;
				}
				if (type.equals("cL2")) {
					if (r.bodysize() == 2 && r instanceof RuleCyclic) continue;
				}
				if (type.equals("cL3")) {
					if (r.bodysize() == 3 && r instanceof RuleCyclic) continue;
				}
				if (type.equals("cL4")) {
					if (r.bodysize() == 3 && r instanceof RuleCyclic) continue;
				}
				if (type.equals("cL5")) {
					if (r.bodysize() == 3 && r instanceof RuleCyclic) continue;
				}
				boolean continueTrue = false;
				for (int i = 1; i < 5; i++) {
					if (type.equals("ac1L" + i + "X")) {
						if (r.bodysize() == i && r instanceof RuleAcyclic1) {
							RuleAcyclic1 ra = (RuleAcyclic1)r;
							if (ra.getHead().getConstant().equals(ra.getBodyAtom(i-1).getConstant())) {
								continueTrue = true;
								continue;
							}	
						}
					}
				}
				if (continueTrue) continue;
				counter++;
				pw.println(r);
			}
			pw.flush();
			pw.close();
			System.out.println("created rule file of type without_" + type + " with " + counter + " rules");
		}
		System.out.println("---");
		
		for (String type : types) {
			long counter = 0;
			PrintWriter pw = new PrintWriter(filepath + "-only-" + type);
			for (Rule r : rules) {
				if (type.equals("ac1L1")) {
					if (r.bodysize() == 1 && r instanceof RuleAcyclic1) {
						counter++;
						pw.println(r);
					}
	
				}
				if (type.equals("ac2L1")) {
					if (r.bodysize() == 1 && r instanceof RuleAcyclic2) {
						counter++;
						pw.println(r);
					}
				}
				if (type.equals("cL1")) {
					if (r.bodysize() == 1 && r instanceof RuleCyclic) {
						counter++;
						pw.println(r);
					}
				}
				if (type.equals("cL2")) {
					if (r.bodysize() == 2 && r instanceof RuleCyclic) {
						counter++;
						pw.println(r);
					}
				}
				if (type.equals("cL3")) {
					if (r.bodysize() == 3 && r instanceof RuleCyclic) {
						counter++;
						pw.println(r);
					}
				}
				if (type.equals("cL4")) {
					if (r.bodysize() == 3 && r instanceof RuleCyclic) {
						counter++;
						pw.println(r);
					}
				}
				if (type.equals("cL5")) {
					if (r.bodysize() == 3 && r instanceof RuleCyclic) {
						counter++;
						pw.println(r);
					}
				}
				for (int i = 1; i < 5; i++) {
					if (type.equals("ac1L" + i + "X")) {
						if (r.bodysize() == i && r instanceof RuleAcyclic1) {
							RuleAcyclic1 ra = (RuleAcyclic1)r;
							if (ra.getHead().getConstant().equals(ra.getBodyAtom(i-1).getConstant()))	{
								counter++;
								pw.println(r);
							}
						}
					}
				}
				
				
			}
			pw.flush();
			pw.close();
			System.out.println("created rule file of type only_" + type + " with " + counter + " rules");
		}
		
		System.out.println("done.");


	}

}
