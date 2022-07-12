package de.unima.ki.anyburl.playground;

import java.util.ArrayList;
import java.util.HashSet;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;

public class Functionality {

	public static void main(String[] args) {
		
		 TripleSet ts = new TripleSet("data/FB15-237/train.txt");
		 
		 
		 HashSet<String> relations = new HashSet<String>();
		 relations.addAll(ts.getRelations());
		 
		 
		 
		 
		 for (String relation : relations) {
			System.out.println("relation: " + relation);
			ArrayList<Triple> rTriples = ts.getTriplesByRelation(relation); 
			HashSet<String> rHeads = new HashSet<String>();
			HashSet<String> rTails = new HashSet<String>();
			for (Triple rtriple : rTriples) {
				rHeads.add(rtriple.getHead());
				rTails.add(rtriple.getTail());
			}
			int tailsPerHeadAll = 0;
			int headsPerTailAll = 0;		
			for (String rh : rHeads) {
				int i = ts.getEntities(relation, rh, true).size();
				tailsPerHeadAll += i;
				
			}
			
			for (String rt : rTails) {
				int i = ts.getEntities(relation, rt, false).size();
				headsPerTailAll += i;
			}
			double headsPerTailFraction = (double)headsPerTailAll / (double)rTails.size();
			double tailsPerHeadFraction = (double)tailsPerHeadAll / (double)rHeads.size();
			System.out.println("   headsPerTailFraction: " + headsPerTailFraction);
			System.out.println("   tailsPerHeadFraction: " + tailsPerHeadFraction);
			
		 }
		
		

	}

}
