package de.unima.ki.anyburl.structure.compare;

import java.util.Comparator;

import de.unima.ki.anyburl.structure.Body;

public class BodyLexicalComparator implements Comparator<Body> {

	public int compare(Body b1, Body b2) {
		return b1.toString().compareTo(b2.toString());
	}

}
