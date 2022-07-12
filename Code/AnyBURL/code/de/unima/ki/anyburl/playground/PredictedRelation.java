package de.unima.ki.anyburl.playground;

public class PredictedRelation {

	public String relation;
	public double confidence;
	
	public PredictedRelation(String r, double c) {
		this.relation = r;
		this.confidence = c;
		
	}
	
	public String toString() {
		return this.relation + " with confidence=" + this.confidence;
	}
	
	
	
	
}
