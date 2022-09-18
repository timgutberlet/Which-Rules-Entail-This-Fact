package de.unima.ki.anyburl.util;

import java.io.FileNotFoundException;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;

public class VaidTestRewriter {

	public static void main(String[] args) throws FileNotFoundException {
		
		// employe = P108
		// Michale Fisher Q1364884
		// Kings College Q245247
		//  Q1364884 P108 Q245247
		
		
		TripleSet test = new TripleSet("data/CODEX/M/test.txt");		
		TripleSet valid = new TripleSet("data/CODEX/M/valid.txt");
		
		TripleSet testX = new TripleSet();
		
		for (Triple t : test.getTriples()) {
			if (t.getHead().equals("Q1364884") && t.getTail().equals("Q245247") && t.getRelation().equals("P108") ) {
				testX.addTriple(t);
				System.out.println("found test triple");
			}
			else {
				valid.addTriple(t);
			}
		}
		
		valid.write("data/CODEX/M/validX.txt");
		testX.write("data/CODEX/M/testX.txt");
		
	}

}
