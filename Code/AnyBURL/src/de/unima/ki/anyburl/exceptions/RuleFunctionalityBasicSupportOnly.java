package de.unima.ki.anyburl.exceptions;

public class RuleFunctionalityBasicSupportOnly extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2037385071211375547L;
	

	public RuleFunctionalityBasicSupportOnly() {
	
	}
		
	public String toString() {
		return "RuleFunctionalityBasicSupportOnly Exception (some specific method is called for a rule type that supports (currently) only basic methods)";

	}
	
	

}
