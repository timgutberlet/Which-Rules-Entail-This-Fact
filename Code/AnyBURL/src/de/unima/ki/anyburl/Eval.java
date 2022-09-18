package de.unima.ki.anyburl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import java.util.Properties;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;
import de.unima.ki.anyburl.eval.HitsAtK;
import de.unima.ki.anyburl.eval.ResultSet;
import de.unima.ki.anyburl.io.IOHelper;
import de.unima.ki.anyburl.structure.Rule;

public class Eval {
		
	private static String CONFIG_FILE = "config-eval.properties";
	
	/**
	 * Read the top k from the ranking file for MRR computation. If less candidates are available, no error is thrown.
	 */
	public static int TOP_K = 100;
	
	/**
	 * Path to the file that contains the triple set used for learning the rules.
	 */
	public static String PATH_TRAINING = "";
	
	
	/**
	 * Path to the file that contains the triple set used for to test the rules.
	 */
	public static String PATH_TEST = "";
	
	/**
	 * Path to the file that contains the triple set used for validation purpose.
	 */
	public static String PATH_VALID = "";
	
	/**
	 * Path to the output file where the predictions are stored.
	 */
	public static String PATH_PREDICTIONS = "";

	
	
	
	/**
	 * Only relevant for evaluating stuff.
	 */
	public static boolean EVAL_HEAD_ONLY = false;	
	public static boolean EVAL_TAIL_ONLY = false;
	
	public static void main(String[] args) throws IOException {
		
		TripleSet.supportRandomAccess = false;
		
		if (args.length == 1) {
			CONFIG_FILE = args[0];
			System.out.println("reading params from file " + CONFIG_FILE);
		}
		
		Rule.applicationMode();
		
		Settings.REWRITE_REFLEXIV = false;
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(CONFIG_FILE);
			prop.load(input);
			Settings.SAFE_PREFIX_MODE = IOHelper.getProperty(prop, "SAFE_PREFIX_MODE", Settings.SAFE_PREFIX_MODE);
			PATH_TRAINING = IOHelper.getProperty(prop, "PATH_TRAINING",PATH_TRAINING);
			PATH_TEST = IOHelper.getProperty(prop, "PATH_TEST", PATH_TEST);
			PATH_VALID = IOHelper.getProperty(prop, "PATH_VALID",PATH_VALID);
			PATH_PREDICTIONS = IOHelper.getProperty(prop, "PATH_PREDICTIONS", PATH_PREDICTIONS);
			TOP_K = IOHelper.getProperty(prop, "TOP_K", TOP_K);
			Settings.FUNCTIONALITY_THRESHOLD = IOHelper.getProperty(prop, "FUNCTIONALITY_THRESHOLD", Settings.FUNCTIONALITY_THRESHOLD);
			Settings.TYPE_THRESHOLD = IOHelper.getProperty(prop, "TYPE_THRESHOLD", Settings.TYPE_THRESHOLD);
		}
		catch (IOException ex) {
			System.err.println("Could not read relevant parameters from the config file " + CONFIG_FILE);
			ex.printStackTrace();
			System.exit(1);
		}
		
		finally {
			if (input != null) {
				try {
					input.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		
		
		
		TripleSet trainingSet =  new TripleSet(PATH_TRAINING);
		TripleSet validationSet = new TripleSet(PATH_VALID);
		TripleSet testSet = new TripleSet(PATH_TEST);
		
		
		
		String[] values = Apply.getMultiProcessing(PATH_PREDICTIONS);

		HitsAtK hitsAtK = new HitsAtK();
		hitsAtK.addFilterTripleSet(trainingSet);
		hitsAtK.addFilterTripleSet(validationSet);
		hitsAtK.addFilterTripleSet(testSet);
		
		
		
		if (values.length == 1) {
			ResultSet rs = new ResultSet(PATH_PREDICTIONS, true, TOP_K);
			// IFASDetector ifas = new IFASDetector(trainingSet);
			// ifas.init();
			
		    // ifas.addTestSetForDebugging(testSet);
			
		    // rs.applyFASATThreshold(ifas);
			// rs.adjust();
			
			// System.out.println("Connectin Counter = " + ifas.getConnectionCounter());
			// System.out.println("True Counter = " + ifas.getTrueCounter());
			
			
			// rs.write("temp.txt");
			
			// rs.shuffle();
			// rs.adjust();
			computeScores(rs, testSet, hitsAtK);
			System.out.println("HEAD-TAIL   HITS-AT-1  HITS-AT-3  HITS-AT-10 MRR");
			System.out.println("ALL         " + hitsAtK.getHitsAtK(0) + "   " + hitsAtK.getHitsAtK(2) + "   " + hitsAtK.getHitsAtK(9) + "   " + hitsAtK.getMRR());
			System.out.println("HEAD only   " + hitsAtK.getHitsAtKHeads(0) + "   " + hitsAtK.getHitsAtKHeads(2) + "   " + hitsAtK.getHitsAtKHeads(9) + "   " + hitsAtK.getMRRHeads());
			System.out.println("TAIL only   " + hitsAtK.getHitsAtKTails(0) + "   " + hitsAtK.getHitsAtKTails(2) + "   " + hitsAtK.getHitsAtKTails(9) + "   " + hitsAtK.getMRRTails());
		}
		else {
			System.out.println("ID\tHITS-AT-1\tHITS-AT-10\tMRR");
			for (String value : values) {
				String path = PATH_PREDICTIONS.replaceFirst("\\|.*\\|", "" + value); 
				ResultSet rs = new ResultSet(path, true, TOP_K);	
				computeScores(rs, testSet, hitsAtK);
				System.out.println(value + "\t" + hitsAtK.getHitsAtK(0) + "\t" + hitsAtK.getHitsAtK(9) + "\t" + hitsAtK.getMRR());
				hitsAtK.reset();
				
			}
			
		}
	
	}

	
	private static void computeScores(ResultSet rs, TripleSet gold, HitsAtK hitsAtK) {
		for (Triple t : gold.getTriples()) {		
			ArrayList<String> cand1 = rs.getHeadCandidates(t.toString());
			// String c1 = cand1.size() > 0 ? cand1.get(0) : "-";
			hitsAtK.evaluateHead(cand1, t);
			ArrayList<String> cand2 = rs.getTailCandidates(t.toString());
			// String c2 = cand2.size() > 0 ? cand2.get(0) : "-";
			hitsAtK.evaluateTail(cand2, t);
		}
	}
	
	

		
}
