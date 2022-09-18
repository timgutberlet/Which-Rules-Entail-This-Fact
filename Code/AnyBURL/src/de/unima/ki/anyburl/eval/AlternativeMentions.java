package de.unima.ki.anyburl.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;


public class AlternativeMentions {
	
	
	HashMap<String, HashSet<String>> alternatives = new HashMap<String, HashSet<String>>();
	
	public boolean sameAs(String e1, String e2) {
		if (e1.equals(e2)) {
			return true;
		}
		if (!this.alternatives.containsKey(e1)) return false;
		
		if (this.alternatives.get(e1).contains(e2)) {
			return true;
		}
		return false;
	}
	
	public AlternativeMentions(String filepath) {
		
		Path file = (new File(filepath)).toPath();
		// Charset charset = Charset.forName("US-ASCII");
		Charset charset = Charset.forName("UTF8");
		String line = null;
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) { 
			while ((line = reader.readLine()) != null) {

				String[] token = line.split("\t");
				

				String subject = token[0];
				String relation = token[1];
				String object = token[2];
				String subjectAlt = token[3];
				String objectAlt = token[4];
				subject = subject.replace(" ", "_");
				relation = relation.replace(" ", "_");
				object = object.replace(" ", "_");
				subjectAlt = subjectAlt.replace(" ", "_");
				objectAlt = objectAlt.replace(" ", "_");
				
				if (!this.alternatives.containsKey(subject)) this.alternatives.put(subject, new HashSet<String>());
				if (!this.alternatives.containsKey(object)) this.alternatives.put(object, new HashSet<String>());
				
				for (String sA : subjectAlt.split("\\|\\|\\|")) {
					this.alternatives.get(subject).add(sA);
				}
				for (String oA : objectAlt.split("\\|\\|\\|")) {
					this.alternatives.get(object).add(oA);
					
				}
			}
		}
		catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.err.format("Error occured for line: " + line + " LINE END");
		}

	}

}
