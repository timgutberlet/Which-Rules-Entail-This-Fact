package de.unima.ki.anyburl.structure;

import java.util.ArrayList;
import java.util.HashSet;

import de.unima.ki.anyburl.Settings;

public class RuleFactory {

	
	public static ArrayList<Rule> getGeneralizations(Path p, boolean onlyXY) {
		RuleUntyped rv = new RuleUntyped();
		rv.init(p);

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
				
				if (leftright == null && !Settings.EXCLUDE_AC2_RULES) {
					RuleUntyped leftFree = left.createCopy();
					leftFree.replaceAllConstantsByVariables();
					generalizations.add(new RuleAcyclic2(leftFree));
				}
				left.replaceNearlyAllConstantsByVariables();
				generalizations.add(new RuleAcyclic1(left));
			}
		}
		
		RuleUntyped right = rv.getRightGeneralization();
		if (right != null) {
			if (right.bodysize() == 0 ) {
				generalizations.add(new RuleZero(right));
			}
			else {
				if (leftright == null && !Settings.EXCLUDE_AC2_RULES) {
					RuleUntyped rightFree = right.createCopy();
					rightFree.replaceAllConstantsByVariables();
					generalizations.add(new RuleAcyclic2(rightFree));
				}
				right.replaceNearlyAllConstantsByVariables();
				generalizations.add(new RuleAcyclic1(right));
			}
		}
		
		
		
		return generalizations;
	}
	
	
	public static RuleAcyclic1 getAC1GeneralizationOnly(Path p) {
		RuleUntyped rv = new RuleUntyped();
		rv.init(p);
		RuleUntyped left = rv.getLeftGeneralization();
		if (left != null) {
			left.replaceNearlyAllConstantsByVariables();
			return (new RuleAcyclic1(left));
		}
		
		RuleUntyped right = rv.getRightGeneralization();
		if (right != null) {
			right.replaceNearlyAllConstantsByVariables();
			return (new RuleAcyclic1(right));
		}
		return null;
	}

	

		
	
}
