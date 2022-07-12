package de.unima.ki.anyburl.structure;

import java.util.ArrayList;
import java.util.HashSet;

import de.unima.ki.anyburl.Settings;

public class RuleFactory {

	
	
	public static ArrayList<Rule> getGeneralizations(Path p, boolean onlyXY) {
		RuleUntyped rv = new RuleUntyped();
		rv.body = new Body();
		if (p.markers[0] == '+') {
			rv.head = new Atom(p.nodes[0], p.nodes[1], p.nodes[2], true, true);
		}
		else {
			rv.head = new Atom(p.nodes[2], p.nodes[1], p.nodes[0], true, true);
		}
		for (int i = 1; i < p.markers.length; i++) {
			if (p.markers[i] == '+') {
				rv.body.add(new Atom(p.nodes[i*2], p.nodes[i*2+1], p.nodes[i*2+2], true, true));
			}
			else {
				rv.body.add(new Atom(p.nodes[i*2+2], p.nodes[i*2+1], p.nodes[i*2], true, true));
			}			
		}
		ArrayList<Rule> generalizations = new ArrayList<>();
		RuleUntyped leftright = rv.getLeftRightGeneralization();
		if (leftright != null) {
			leftright.replaceAllConstantsByVariables();
			generalizations.add(new RuleCyclic(leftright));
		}	
		if (onlyXY) return generalizations;
		// acyclic rule
		RuleUntyped left = rv.getLeftGeneralization();
		
		if (left != null) {
			if (left.bodysize() == 0 ) {
				generalizations.add(new RuleZero(left));
			}
			else {
				RuleUntyped leftFree = left.createCopy();
				if (leftright == null) leftFree.replaceAllConstantsByVariables();
				left.replaceNearlyAllConstantsByVariables();
				if (!Settings.EXCLUDE_AC2_RULES) if (leftright == null) generalizations.add(new RuleAcyclic2(leftFree));
				generalizations.add(new RuleAcyclic1(left));
			}
		}
		RuleUntyped right = rv.getRightGeneralization();
		if (right != null) {
			if (right.bodysize() == 0 ) {
				generalizations.add(new RuleZero(right));
			}
			else {
				RuleUntyped rightFree = right.createCopy();
				if (leftright == null) rightFree.replaceAllConstantsByVariables();
				right.replaceNearlyAllConstantsByVariables();
				if (!Settings.EXCLUDE_AC2_RULES) if (leftright == null) generalizations.add(new RuleAcyclic2(rightFree));
				generalizations.add(new RuleAcyclic1(right));
			}
		}
		return generalizations;
	}

	

		
	
}
