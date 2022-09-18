package de.unima.ki.anyburl.rescore;

import java.text.DecimalFormat;

/**
 * A helper class that wraps parameters to joint two result sets.
 * For historical reasons its like this, even though the alpha score is no longer existing.
 *
 */
public class AlphaBeta {
	
	public double beta;
	
	private static  DecimalFormat df = new DecimalFormat("#.##");
	
	public AlphaBeta(double beta) {
		this.beta = beta;
	}
	
	public String toString() {
		return "beta=" + df.format(beta);
	}

}
