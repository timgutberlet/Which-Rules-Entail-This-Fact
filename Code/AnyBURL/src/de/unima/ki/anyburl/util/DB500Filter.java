package de.unima.ki.anyburl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.data.AnnotatedTriple;
import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;

/**
 * Converts files in format 'subject object relation' to 'subject relation object'
 * @author Christian
 *
 */
public class DB500Filter {

	public static void main(String[] args) throws FileNotFoundException {


		String dbtrainPath = "data/DB500/train.txt";
		String dbtestPath = "data/DB500/test.txt";
		String fdbtestPath = "data/DB500/ftest.txt";
		
		TripleSet dbtrain = new TripleSet(dbtrainPath);
		TripleSet dbtest = new TripleSet(dbtestPath);
		
		int counterIn = 0;
		int counterOut = 0;
		
		TripleSet fdbtest = new TripleSet();
		
		for (Triple t : dbtest.getTriples()) {
			boolean headIn = false;
			boolean tailIn = false;
			if (dbtrain.getTriplesByHead(t.getHead()).size() > 0 || dbtrain.getTriplesByTail(t.getHead()).size() > 0) {
				headIn= true;
			}
			
			if (dbtrain.getTriplesByHead(t.getTail()).size() > 0 || dbtrain.getTriplesByTail(t.getTail()).size() > 0) {
				tailIn= true;
			}
			
			if (headIn && tailIn) {
				counterIn++;
				fdbtest.addTriple(t);
			}
			else {
				counterOut++;
			}
		}
		System.out.println("counterIn = " + counterIn);
		System.out.println("counterOut = " + counterOut);
		
		fdbtest.write(fdbtestPath);
		System.out.println("done, written to " + fdbtestPath + ".");
		
		
		
	}

}
