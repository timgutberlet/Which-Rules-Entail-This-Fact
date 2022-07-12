package de.unima.ki.anyburl.eval;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import de.unima.ki.anyburl.data.Triple;
import de.unima.ki.anyburl.data.TripleSet;

public class ComparativeEvaluation {
	
	public static String[] categories = new String[]{"Symmetry", "Equivalence", "Subsumption", "Path", "Not covered"};
	
	// WN18 WN18RR FB15 FB15-237 YAGO NELL995 OPENBIO CODEX-S (M,L)
	public static String target = "CODEX-L";
	
	// 0 = ALL, 1 = HEAD ONLY, 2 = TAIL ONLY
	public static int ALL_HEAD_TAIL = 0;
	
	// filters out all test triples that are not using relations in this list
	// no filtering, if the set is empty
	public static HashSet<String> RELATION_FILTER = new HashSet<String>();
	
	
	public static void main(String[] args) throws IOException {
		
		
		TripleSet trainingSet = null, validationSet = null, testSet = null;
		AlternativeMentions am = null;
		GoldStandard gold = null;
		
		if (target.equals("CODEX-S")) {
			trainingSet = new TripleSet("data/CODEX/S/train.txt");
			validationSet = new TripleSet("data/CODEX/S/valid.txt");
			testSet = new TripleSet("data/CODEX/S/test.txt");
			gold = null;
		}
		if (target.equals("CODEX-M")) {
			trainingSet = new TripleSet("data/CODEX/M/train.txt");
			validationSet = new TripleSet("data/CODEX/M/valid.txt");
			testSet = new TripleSet("data/CODEX/M/test.txt");
			gold = null;
		}
		if (target.equals("CODEX-L")) {
			trainingSet = new TripleSet("data/CODEX/L/train.txt");
			validationSet = new TripleSet("data/CODEX/L/valid.txt");
			testSet = new TripleSet("data/CODEX/L/test.txt");
			gold = null;
		}
		
		if (target.equals("DIANA")) {
			trainingSet = new TripleSet("exp/diana/train.txt");
			validationSet = new TripleSet("exp/diana/valid.txt");
			testSet = new TripleSet("exp/diana/test.txt");
			gold = null;
		}
		
		if (target.equals("NUMBERS")) {
			trainingSet = new TripleSet("../KBCTestdataGenerator/data/numbers-d3-08-p08-p04-p00-uc/train.txt");
			validationSet = new TripleSet("../KBCTestdataGenerator/data/numbers-d3-08-p08-p04-p00-uc/valid.txt");
			testSet = new TripleSet("../KBCTestdataGenerator/data/numbers-d3-08-p08-p04-p00-uc/test.txt");
			gold = null;
		}

		
		if (target.equals("WN18")) {
			trainingSet = new TripleSet("data/WN18/train.txt");
			validationSet = new TripleSet("data/WN18/valid.txt");
			testSet = new TripleSet("data/WN18/test.txt");
			gold = new GoldStandard("data/WN18/gold.txt");
		}
		if (target.equals("FB15")) {
			trainingSet = new TripleSet("data/FB15k/train.txt");
			validationSet = new TripleSet("data/FB15k/valid.txt");
			testSet = new TripleSet("data/FB15k/test.txt");
			gold = new GoldStandard("data/FB15k/gold.txt");
		}
		
		if (target.equals("FB15-237")) {
			trainingSet =   new TripleSet("data/FB15-237/train.txt");
			validationSet = new TripleSet("data/FB15-237/valid.txt");
			testSet =       new TripleSet("data/FB15-237/test.txt");
			gold =          new GoldStandard("data/FB15-237/gold.txt");
		}
		
		if (target.equals("WN18RR")) {
			trainingSet = new TripleSet("data/WN18RR/train.txt");
			validationSet = new TripleSet("data/WN18RR/valid.txt");
			testSet = new TripleSet("data/WN18RR/test.txt");
			// gold = new GoldStandard("../RuleN18/data/WN18RR/gold.txt");
		}
		if (target.equals("YAGO")) {
			trainingSet = new TripleSet("data/YAGO03-10/train.txt");
			validationSet = new TripleSet("data/YAGO03-10/valid.txt");
			testSet = new TripleSet("data/YAGO03-10/test.txt");
			gold = null;
		}
		if (target.equals("NELL995")) {
			trainingSet   = new TripleSet("data/NELL995/train.txt");
			validationSet = new TripleSet("data/NELL995/valid.txt");
			testSet       = new TripleSet("data/NELL995/test.txt");
			gold = null;
		}
		if (target.equals("DB500")) {
			trainingSet = new TripleSet("data/DB500/train.txt");
			validationSet = new TripleSet("data/DB500/valid.txt");
			testSet = new TripleSet("data/DB500/test.txt");
			gold = null;
		}
		if (target.equals("OPENBIO")) {
			trainingSet = new TripleSet("data/OpenBio/train_sample.csv");
			validationSet = new TripleSet("data/OpenBio/val_sample.csv");
			testSet = new TripleSet("data/OpenBio/test_sample.csv");
			gold = null;
		}
		if (target.equals("ASS")) {
			trainingSet   = new TripleSet("experiments/SemAssocs/data/empty.txt");
			validationSet = new TripleSet("experiments/SemAssocs/data/empty.txt");
			testSet       = new TripleSet("experiments/SemAssocs/data/assoc_test.nt");
			gold = null;
		}
		
		if (target.equals("MOB")) {
			trainingSet   = new TripleSet("data/mob/train.txt");
			validationSet = new TripleSet("data/mob/valid.txt");
			testSet       = new TripleSet("data/mob/test.txt");
			gold = null;
		}
		
		if (target.equals("WD")) {
			trainingSet = new TripleSet("data/WIKIDATA/empty.txt");
			validationSet = new TripleSet("data/WIKIDATA/empty.txt");
			testSet = new TripleSet("data/WIKIDATA/test.txt");
			gold = null;
		}
		
		if (target.equals("OLPBENCH")) {
			trainingSet = new TripleSet("data/OLPBENCH/empty.txt");
			validationSet = new TripleSet("data/OLPBENCH/empty.txt");
			testSet = new TripleSet("data/OLPBENCH/test_data.txt");
			am = new AlternativeMentions("data/OLPBENCH/test_data.txt");
			
			gold = null;
		}
		if (target.equals("OLPBENCH2")) {
			trainingSet = new TripleSet("data/OLPBENCH/empty.txt");
			validationSet = new TripleSet("data/OLPBENCH/empty.txt");
			testSet = new TripleSet("data/OLPBENCH/validation_data_linked_mention.txt");
			gold = null;
		}		
		
		
		ResultSet[] results = null;
		
		boolean html = false;
		if (target.equals("ASS")) {

			
			results = new ResultSet[]{
					
					new ResultSet("RuleN", "experiments/SemAssocs/predictions/p12-s200.txt", true, 100),
					new ResultSet("RuleN", "experiments/SemAssocs/predictions/p12-s200-mul.txt", true, 100),
					new ResultSet("RuleN", "experiments/SemAssocs/predictions/p123-s200.txt", true, 100),
					new ResultSet("RuleN", "experiments/SemAssocs/predictions/p123-s500.txt", true, 100),

					
					
			};			
		}
		
		if (target.equals("WD")) {

			
			results = new ResultSet[]{
					
					new ResultSet("500      ","exp/february/final/rg/wikidata-rt-c2a1g1-p2s3-predictionsZ-AVG-500", true, 10),
					new ResultSet("1000     ","exp/february/final/rg/wikidata-rt-c2a1g1-p2s3-predictionsZ-AVG-1000", true, 10),
					new ResultSet("5000     ","exp/february/final/rg/wikidata-rt-c2a1g1-p2s3-predictionsZ-AVG-5000", true, 10),
					new ResultSet("10000    ","exp/february/final/rg/wikidata-rt-c2a1g1-p2s3-predictionsZ-AVG-10000", true, 10),
					
			};			
		}
		
		
		if (target.equals("DIANA")) {

			
			results = new ResultSet[]{
					
					new ResultSet("500      ","exp/diana/predictions-c5-a2-100", true, 10),
					new ResultSet("500      ","exp/diana/horsemen-predictions", true, 10),
			
					
			};			
		}
		
		if (target.equals("MOB")) {

			
			results = new ResultSet[]{
					
					new ResultSet("RuleN", "exp/summer/mob/mob-rules-pred-100", true, 100),
				
			};	
		}

		
		
		if (target.equals("WN18")) {

			
			results = new ResultSet[]{

					new ResultSet("all", "exp/september/wn18/wn18-def-rules-1000", true, 10),
					null,
					new ResultSet("wout ac1L1", "exp/september/wn18/wn18-def-rules-1000-without-ac1L1", true, 10),
					new ResultSet("wout ac2L1", "exp/september/wn18/wn18-def-rules-1000-without-ac2L1", true, 10),
					new ResultSet("wout cL1", "exp/september/wn18/wn18-def-rules-1000-without-cL1", true, 10),
					new ResultSet("wout cL2", "exp/september/wn18/wn18-def-rules-1000-without-cL2", true, 10),
					new ResultSet("wout cL3", "exp/september/wn18/wn18-def-rules-1000-without-cL3", true, 10),
					new ResultSet("wout ac1L1X", "exp/september/wn18/wn18-def-rules-1000-without-ac1L1X", true, 10),
					null,
					new ResultSet("only ac1L1", "exp/september/wn18/wn18-def-rules-1000-only-ac1L1", true, 10),
					new ResultSet("only ac2L1", "exp/september/wn18/wn18-def-rules-1000-only-ac2L1", true, 10),
					new ResultSet("only cL1", "exp/september/wn18/wn18-def-rules-1000-only-cL1", true, 10),
					new ResultSet("only cL2", "exp/september/wn18/wn18-def-rules-1000-only-cL2", true, 10),
					new ResultSet("only cL3", "exp/september/wn18/wn18-def-rules-1000-only-cL3", true, 10),
					new ResultSet("only ac1L1X", "exp/september/wn18/wn18-def-rules-1000-only-ac1L1X", true, 10),
					null,
					// new ResultSet("best", "exp/september/wn18/wn18-def-rules-10000-best", true, 10),

					
			};	
		}
		
		
		if (target.equals("CODEX-S")) {

			results = new ResultSet[]{
					

					
					
					new ResultSet("anyburl      ", "exp/understanding/codex-s/anyburl-c3-3600-100-test", true, 100),
					new ResultSet("AB-complex-MM", "exp/understanding/codex-s/anyburl-c3-3600-100-test-complex-MM", true, 100),
					new ResultSet("AB-conve-MM  ", "exp/understanding/codex-s/anyburl-c3-3600-100-test-conve-MM", true, 100),
					new ResultSet("AB-hitter-MM ", "exp/understanding/codex-s/anyburl-c3-3600-100-test-hitter-MM", true, 100),
					new ResultSet("AB-rescal-MM ", "exp/understanding/codex-s/anyburl-c3-3600-100-test-rescal-MM", true, 100),
					new ResultSet("AB-transe-MM ", "exp/understanding/codex-s/anyburl-c3-3600-100-test-transe-MM", true, 100),
					new ResultSet("AB-tucker-MM ", "exp/understanding/codex-s/anyburl-c3-3600-100-test-tucker-MM", true, 100),
					
	

			};	
		}
		
		if (target.equals("CODEX-M")) {

			results = new ResultSet[]{
					new ResultSet("anyburl       ", "exp/understanding/codex-m/anyburl-c3-3600-100-test", true, 100),
					
					
					new ResultSet("complex-RF ", "exp/understanding/codex-m/complex-100-test-RF", true, 100, true),
					new ResultSet("conve-RF   ", "exp/understanding/codex-m/conve-100-test-RF", true, 100, true),
					new ResultSet("hitter-RF  ", "exp/understanding/codex-m/hitter-100-test-RF", true, 100, true),
					new ResultSet("rescal-RF  ", "exp/understanding/codex-m/rescal-100-test-RF", true, 100, true),
					new ResultSet("transe-RF  ", "exp/understanding/codex-m/transe-100-test-RF", true, 100, true),
					new ResultSet("tucker-RF  ", "exp/understanding/codex-m/tucker-100-test-RF", true, 100, true),
					
					
					/*
					
					// new ResultSet("AB-complex    ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-complex-reordered-directed", true, 100),
					// new ResultSet("AB-complex-01 ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-complex-01", true, 100),
					//new ResultSet("AB-complex-MM ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-complex-MM", true, 100),
					new ResultSet("AB-complex-MM2", "exp/understanding/codex-m/anyburl-c2-3600-100-test-complex-MM", true, 100),
					//new ResultSet("complex       ", "exp/understanding/codex-m/complex-100-test", true, 100),
					// new ResultSet("AB-conve     ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-conve-reordered-directed", true, 100),
					//new ResultSet("AB-conve-01  ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-conve-01", true, 100),
					//new ResultSet("AB-conve-MM  ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-conve-MM", true, 100),
					new ResultSet("AB-conve-MM2 ", "exp/understanding/codex-m/anyburl-c2-3600-100-test-conve-MM", true, 100),
					//new ResultSet("conve        ", "exp/understanding/codex-m/conve-100-test", true, 100),
					//new ResultSet("AB-hitter     ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-hitter-reordered-directed", true, 100),
					//new ResultSet("AB-hitter-01  ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-hitter-01", true, 100),
					//new ResultSet("AB-hitter-MM ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-hitter-MM", true, 100),
					new ResultSet("AB-hitter-MM2", "exp/understanding/codex-m/anyburl-c2-3600-100-test-hitter-MM", true, 100),
					//new ResultSet("hitter       ", "exp/understanding/codex-m/hitter-100-test", true, 100),
					//new ResultSet("AB-rescal    ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-rescal-reordered-directed", true, 100),
					//new ResultSet("AB-rescal-01 ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-rescal-01", true, 100),
					//new ResultSet("AB-rescal-MM ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-rescal-MM", true, 100),
					new ResultSet("AB-rescal-MM2", "exp/understanding/codex-m/anyburl-c2-3600-100-test-rescal-MM", true, 100),
					//new ResultSet("rescal       ", "exp/understanding/codex-m/rescal-100-test", true, 100),
					//new ResultSet("AB-transe    ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-transe-reordered-directed", true, 100),
					//new ResultSet("AB-transe-01 ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-transe-01", true, 100),
					//new ResultSet("AB-transe-MM ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-transe-MM", true, 100),
					new ResultSet("AB-transe-MM2", "exp/understanding/codex-m/anyburl-c2-3600-100-test-transe-MM", true, 100),
					//new ResultSet("transe       ", "exp/understanding/codex-m/transe-100-test", true, 100),
					//new ResultSet("AB-tucker    ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-tucker-reordered-directed", true, 100),
					//new ResultSet("AB-tucker-01 ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-tucker-01", true, 100),
					//new ResultSet("AB-tucker-MM ", "exp/understanding/codex-m/anyburl-c3-3600-100-test-tucker-MM", true, 100),
					new ResultSet("AB-tucker-MM2", "exp/understanding/codex-m/anyburl-c2-3600-100-test-tucker-MM", true, 100),
					//new ResultSet("tucker       ", "exp/understanding/codex-m/tucker-100-test", true, 100),
					
					// new ResultSet("AB-conve-d  ", "exp/understanding/wn18rr/anyburl-c5-1000-50-test-conve-reordered-directed", true, 50),
					// new ResultSet("conve       ", "exp/understanding/wn18rr/conve-50-test", true, 50),
					*/

			};	
		}
		
		
		if (target.equals("CODEX-L")) {

			results = new ResultSet[]{
					
					/*
					new ResultSet("anyburl      ", "exp/understanding/codex-l/anyburl-c3-3600-100-test", true, 100),

					new ResultSet("AB-complex   ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-complex-reordered-directed", true, 100),
					new ResultSet("AB-complex-MM", "exp/understanding/codex-l/anyburl-c3-3600-100-test-complex-MM", true, 100),
					

					new ResultSet("AB-conve     ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-conve-reordered-directed", true, 100),
					new ResultSet("AB-conve-MM  ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-conve-MM", true, 100),
					
					new ResultSet("hitter       ", "exp/understanding/codex-l/hitter-100-test", true, 100),
					// new ResultSet("AB-hitter   ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-hitter-reordered-directed", true, 100),
					new ResultSet("AB-hitter-MM ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-hitter-MM", true, 100),
					
					
					null,
					*/
					new ResultSet("AB-complex-MM    ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-complex-MM", true, 100),
					new ResultSet("AB-complex-FF    ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-complex", true, 100, true),
					new ResultSet("AB-conve-MM      ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-conve-MM", true, 100),
					new ResultSet("AB-conve-FF      ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-conve", true, 100, true),
					new ResultSet("AB-hitter-MM     ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-hitter-MM", true, 100),
					new ResultSet("AB-hitter-FF     ", "exp/understanding/codex-l/anyburl-c3-3600-100-test-hitter", true, 100, true),
					null
					

			};	
		}
		
		if (target.equals("WN18RR")) {


			results = new ResultSet[]{
					
		
					
					new ResultSet("anyburl  all     ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test", true, 100),
					new ResultSet("anyburl  2    ", "exp/understanding/wn18rr/anyburl-c2-3600-100-test", true, 100),
					new ResultSet("anyburl  1    ", "exp/understanding/wn18rr/anyburl-c1-3600-100-test", true, 100),
					
					new ResultSet("complex-filtered  ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-complex", true, 100, true),
					new ResultSet("conve-filtered    ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-conve", true, 100, true),
					new ResultSet("distmult-filtered ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-distmult", true, 100, true),
					new ResultSet("hitter-filtered   ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-hitter", true, 100, true),
					new ResultSet("rescal-filtered   ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-rescal", true, 100, true),
					new ResultSet("transe-filtered   ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-transe", true, 100, true),
				
					null,
					
					
					new ResultSet("complex-RF ", "exp/understanding/wn18rr/complex-100-test-RF", true, 100, true),
					new ResultSet("conve-RF   ", "exp/understanding/wn18rr/conve-100-test-RF", true, 100, true),
					new ResultSet("distmult-RF", "exp/understanding/wn18rr/distmult-100-test-RF", true, 100, true),
					new ResultSet("hitter-RF  ", "exp/understanding/wn18rr/hitter-100-test-RF", true, 100, true),
					new ResultSet("rescal-RF  ", "exp/understanding/wn18rr/rescal-100-test-RF", true, 100, true),
					new ResultSet("transe-RF  ", "exp/understanding/wn18rr/transe-100-test-RF", true, 100, true),
	
					
					
					/*
					new ResultSet("AB-conve-MM   ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-conve-MM", true, 100),
					//new ResultSet("AB-conve-MM2  ", "exp/understanding/wn18rr/anyburl-c2-3600-100-test-conve-MM", true, 100),
					new ResultSet("AB-conve-MM1  ", "exp/understanding/wn18rr/anyburl-c1-3600-100-test-conve-MM", true, 100),
					
					new ResultSet("AB-distmult-MM ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-distmult-MM", true, 100),
					//new ResultSet("AB-distmult-MM2", "exp/understanding/wn18rr/anyburl-c2-3600-100-test-distmult-MM", true, 100),
					new ResultSet("AB-distmult-MM1", "exp/understanding/wn18rr/anyburl-c1-3600-100-test-distmult-MM", true, 100),
					
					new ResultSet("AB-hitter-MM  ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-hitter-MM", true, 100),
					//new ResultSet("AB-hitter-MM2 ", "exp/understanding/wn18rr/anyburl-c2-3600-100-test-hitter-MM", true, 100),
					new ResultSet("AB-hitter-MM1 ", "exp/understanding/wn18rr/anyburl-c1-3600-100-test-hitter-MM", true, 100),
	
					new ResultSet("AB-rescal-MM  ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-rescal-MM", true, 100),
					//new ResultSet("AB-rescal-MM2 ", "exp/understanding/wn18rr/anyburl-c2-3600-100-test-rescal-MM", true, 100),
					new ResultSet("AB-rescal-MM1 ", "exp/understanding/wn18rr/anyburl-c1-3600-100-test-rescal-MM", true, 100),
					
					new ResultSet("AB-transe-MM  ", "exp/understanding/wn18rr/anyburl-c5-3600-100-test-transe-MM", true, 100),
					//new ResultSet("AB-transe-MM2 ", "exp/understanding/wn18rr/anyburl-c2-3600-100-test-transe-MM", true, 100),
					new ResultSet("AB-transe-MM1 ", "exp/understanding/wn18rr/anyburl-c1-3600-100-test-transe-MM", true, 100),
					*/

			};	
		}
		
		
		if (target.equals("NUMBERS")) {

			
			results = new ResultSet[]{
					new ResultSet("UC identified           ", "../KBCTestdataGenerator/experiments/anyburl/numbers-d3-08-p08-p04-p00-uc-predictions", true, 10),
					new ResultSet("UC ignored (no overfit) ", "../KBCTestdataGenerator/experiments/anyburl/numbers-d3-08-p08-p04-p00-ucX-predictions", true, 10),
					
					// new ResultSet("best", "exp/september/wn18rr/wn18rr-def-rules-10000-best", true, 10),		

			};	
		}
		
		
		if (target.equals("YAGO")) {

	
			
			results = new ResultSet[]{
					
					
					new ResultSet("1000 all        ", "exp/zero/yago/yago-predictions-1000-all", true, 20),
					new ResultSet("1000 all-wo-ac0 ", "exp/zero/yago/yago-predictions-1000-all-wo-ac0", true, 20),
					new ResultSet("1000 all-wo-ac02", "exp/zero/yago/yago-predictions-1000-all-wo-ac02", true, 20),
					new ResultSet("1000 all-x      ", "exp/zero/yago/yago-predictions-1000-all-x", true, 20),
					null,
					new ResultSet("1000 ac0        ", "exp/zero/yago/yago-predictions-1000-ac0", true, 20),
					new ResultSet("1000 ac2        ", "exp/zero/yago/yago-predictions-1000-ac2", true, 20),
					new ResultSet("1000 ac1        ", "exp/zero/yago/yago-predictions-1000-ac1", true, 20),			
					new ResultSet("1000 c          ", "exp/zero/yago/yago-predictions-1000-c", true, 20),	


			};
			
			
			
		}
		
	if (target.equals("NELL995")) {

			results = new ResultSet[] {

		
					new ResultSet("200 edisOn1   ", "exp/february/temp/nell-predictions-311-edisOn1-200", true, 10),
					new ResultSet("200 edisOn2   ", "exp/february/temp/nell-predictions-311-edisOn2-200", true, 10),
					null,
					new ResultSet("200 edisOff1  ", "exp/february/temp/nell-predictions-311-edisOff1-200", true, 10),
					new ResultSet("200 edisOff2  ", "exp/february/temp/nell-predictions-311-edisOff2-200", true, 10),
					
			};
			
		}
		
	
		
		if (target.equals("DB500")) {

			results = new ResultSet[]{
					new ResultSet("100     ", "exp/january/reinforced/db500-predictions-100", true, 10),

				
			};
			
			
			
		}
		
		
		if (target.equals("FB15-237")) {
			
			
			// RELATION_FILTER.add("/award/award_nominee/award_nominations./award/award_nomination/award_nominee"); // 108 in valid
			
			
			// RELATION_FILTER.add("/common/topic/webpage./common/webpage/category");
			// ALL_HEAD_TAIL = 1;
			
			results = new ResultSet[]{
					
					
					new ResultSet("AnyBURL       ", "exp/understanding/fb237/anyburl-c3-3600-100-test", true, 100),

					new ResultSet("complex-RF ", "exp/understanding/fb237/complex-100-test-RF", true, 100, true),
					new ResultSet("conve-RF   ", "exp/understanding/fb237/conve-100-test-RF", true, 100, true),
					new ResultSet("distmult-RF", "exp/understanding/fb237/distmult-100-test-RF", true, 100, true),
					new ResultSet("hitter-RF  ", "exp/understanding/fb237/hitter-100-test-RF", true, 100, true),
					new ResultSet("rescal-RF  ", "exp/understanding/fb237/rescal-100-test-RF", true, 100, true),
					new ResultSet("transe-RF  ", "exp/understanding/fb237/transe-100-test-RF", true, 100, true),
					
					/*
					new ResultSet("complex-filtered  ", "exp/understanding/fb237/anyburl-c3-3600-100-test-complex", true, 100, true),
					new ResultSet("conve-filtered    ", "exp/understanding/fb237/anyburl-c3-3600-100-test-conve", true, 100, true),
					new ResultSet("distmult-filtered ", "exp/understanding/fb237/anyburl-c3-3600-100-test-distmult", true, 100, true),
					new ResultSet("hitter-filtered   ", "exp/understanding/fb237/anyburl-c3-3600-100-test-hitter", true, 100, true),
					new ResultSet("rescal-filtered   ", "exp/understanding/fb237/anyburl-c3-3600-100-test-rescal", true, 100, true),
					new ResultSet("transe-filtered   ", "exp/understanding/fb237/anyburl-c3-3600-100-test-transe", true, 100, true),
					*/
					
					/*
					new ResultSet("AB-complex-MM ", "exp/understanding/fb237/anyburl-c3-3600-100-test-complex-MM", true, 100),
					new ResultSet("AB-conve-MM    ", "exp/understanding/fb237/anyburl-c3-3600-100-test-conve-MM", true, 100),
					new ResultSet("AB-distmult-MM", "exp/understanding/fb237/anyburl-c3-3600-100-test-distmult-MM", true, 100),
					new ResultSet("AB-hitter-MM  ", "exp/understanding/fb237/anyburl-c3-3600-100-test-hitter-MM", true, 100),
					new ResultSet("AB-rescal-MM   ", "exp/understanding/fb237/anyburl-c3-3600-100-test-rescal-MM", true, 100),
					new ResultSet("AB-transe-MM   ", "exp/understanding/fb237/anyburl-c3-3600-100-test-transe-MM", true, 100),
					*/
					
					
					
					/*
					new ResultSet("complex       ", "exp/understanding/fb237/complex-100-test", true, 100),
					new ResultSet("complex FR    ", "exp/understanding/fb237/complex-100-test-FR", true, 100),
					new ResultSet("AB-complex-d  ", "exp/understanding/fb237/anyburl-c3-3600-100-test-complex-reordered-directed", true, 100),
					null,
					new ResultSet("transe        ", "exp/understanding/fb237/transe-100-test", true, 100),
					new ResultSet("transe FR     ", "exp/understanding/fb237/transe-100-test-FR", true, 100),
					new ResultSet("AB-transe-d   ", "exp/understanding/fb237/anyburl-c3-3600-100-test-transe-reordered-directed", true, 100),
					null,
					new ResultSet("rescal        ", "exp/understanding/fb237/rescal-100-test", true, 100),
					new ResultSet("rescal FR     ", "exp/understanding/fb237/rescal-100-test-FR", true, 100),
					new ResultSet("AB-rescal-d   ", "exp/understanding/fb237/anyburl-c3-3600-100-test-rescal-reordered-directed", true, 100),
					*/
					
					
				
					
					
	
			};
			
		}
		
		
		
		if (target.equals("FB15")) {

			
			
			results = new ResultSet[]{
					
					
					new ResultSet("1000 all        ", "exp/zero/fb15/fb15-predictions-1000-all", true, 20),
					new ResultSet("1000 wo-ac0     ", "exp/zero/fb15/fb15-predictions-1000-wo-ac0", true, 20),
					new ResultSet("1000 wo-ac02    ", "exp/zero/fb15/fb15-predictions-1000-wo-ac02", true, 20),
					new ResultSet("1000 all-x      ", "exp/zero/fb15/fb15-predictions-1000-all-x", true, 20),
				

			};
			
			
		}
		
	if (target.equals("OPENBIO")) {

			
			
			results = new ResultSet[]{
					
					new ResultSet("all", "exp/openbio/pred-C1-100", true, 10),
			
			};
			
			
		}
		
		if (target.equals("OLPBENCH")) {

			results = new ResultSet[]{
					
					// new ResultSet("   100", "exp/july/olpbench/predictions-thorough-100", true, 10),
					// new ResultSet("   500", "exp/july/olpbench/predictions-thorough-500", true, 10),
					new ResultSet("  1000", "exp/july/olpbench/predictions-thorough-1000", true, 20),
					new ResultSet("  5000", "exp/july/olpbench/predictions-thorough-5000", true, 20),
					new ResultSet(" 10000", "exp/july/olpbench/predictions-thorough-10000", true, 20),
					null,
					new ResultSet("  1000", "exp/july/olpbench/predictions-c5-thorough-1000", true, 20),
					new ResultSet("  5000", "exp/july/olpbench/predictions-c5-thorough-5000", true, 20),
					new ResultSet(" 10000", "exp/july/olpbench/predictions-c5-thorough-10000", true, 10),
					new ResultSet(" 20000", "exp/july/olpbench/predictions-c5-thorough-20000", true, 10),
					new ResultSet(" 40000", "exp/july/olpbench/predictions-c5-thorough-40000", true, 10),

			};
			
		}
		
		
		if (target.equals("OLPBENCH2")) {

			results = new ResultSet[]{
					
					// new ResultSet("  5000", "exp/july/olpbench/predictions-vdlm-thorough-5000", true, 10),
					new ResultSet(" 10000", "exp/july/olpbench/predictions-vdlm-thorough-10000", true, 10),
			};
			
		}		
		
		
		
		
		HitsAtK hitsAtK = new HitsAtK();
		
		hitsAtK.addFilterTripleSet(trainingSet);
		hitsAtK.addFilterTripleSet(validationSet);
		hitsAtK.addFilterTripleSet(testSet); 
		
		if (am != null) hitsAtK.addAlternativeMentions(am);
		
		GoldStandard goldSymmetry    = null;	
		GoldStandard goldEquivalence = null;
		GoldStandard goldSubsumption = null;
		GoldStandard goldPath        = null;
		GoldStandard goldUncovered   = null;
		
		if (gold != null) {
			goldSymmetry    = gold.getSubset("Symmetry");	
			goldEquivalence = gold.getSubset("Equivalence");	
			goldSubsumption = gold.getSubset("Subsumption");	
			goldPath        = gold.getSubset("Path");	
			goldUncovered   = gold.getSubset("Not covered");	
		}
	

		
		for (ResultSet rs : results) {
			
			// symmetry
			
			if (rs == null) {
				System.out.println("------------------");
				continue;
			}
			if (gold == null) {
				computeScores(rs, testSet, hitsAtK);
				// System.out.println(hitsAtK);
				
				// computeScores(rs, testSet, hitsAtK);
				
				System.out.print(rs.getName() + "\t" + hitsAtK.getHitsAtK(0) + "\t" + hitsAtK.getHitsAtK(9) + "\t" + hitsAtK.getApproxMRR());
				//System.out.print(rs.getName() + "   " + hitsAtK.getHitsAtK(0) + "   " + hitsAtK.getHitsAtK(2) + "   " + hitsAtK.getHitsAtK(9) + "   " + hitsAtK.getApproxMRR());
				// hitsAtK.reset();
				hitsAtK.reset();
			}
			else if (html == true) {
				computeScores(rs, testSet, hitsAtK);
				System.out.print("<tr><td><span class=\"important\">" + rs.getName() + "</span></td><td>" + hitsAtK.getHitsAtK(0) + "</td> <td>" + hitsAtK.getHitsAtK(9) + "</td> <td></td> <td></td> <td></td></tr>");
				hitsAtK.reset();
			}
			
			else {
				computeScores(rs, gold, hitsAtK);
				// System.out.println(hitsAtK);
				// System.out.print(rs.getName() + "\t" + hitsAtK.getHitsAtK(0) + "\t" + hitsAtK.getHitsAtK(9) + "\t" + hitsAtK.getHitsAtK(19) + "\t" + hitsAtK.getApproxMRR()  + "\t" + hitsAtK.getHitsAtKCalculation(0));
				System.out.print(rs.getName() + "\t" + hitsAtK.getHitsAtK(0) + "\t" + hitsAtK.getHitsAtK(9) + "\t" + hitsAtK.getApproxMRR());
				
				hitsAtK.reset();
			}
			/*
			
				computeScores(rs, gold, hitsAtK);
				System.out.print(rs.getName() + "   " + hitsAtK.getHitsAtK(0) + "   " + hitsAtK.getHitsAtK(9) + "   " + hitsAtK.getApproxMRR() + "   ");
				
				System.out.println(hitsAtK);
				hitsAtK.reset();

				
				// symmetry
				computeScores(rs, goldSymmetry, hitsAtK);
				System.out.print( hitsAtK.getHitsAtK(0) + "   " + hitsAtK.getHitsAtK(9) + "   ");
				hitsAtK.reset();
				
				
				// equivalence
				computeScores(rs, goldEquivalence, hitsAtK);
				System.out.print( hitsAtK.getHitsAtK(0) + "   " + hitsAtK.getHitsAtK(9) + "   ");
				hitsAtK.reset();
				// subsumption
				computeScores(rs, goldSubsumption, hitsAtK);
				System.out.print( hitsAtK.getHitsAtK(0) + "   " + hitsAtK.getHitsAtK(9) + "   ");
				hitsAtK.reset();
				// Path
				computeScores(rs, goldPath, hitsAtK);
				System.out.print( hitsAtK.getHitsAtK(0) + "   " + hitsAtK.getHitsAtK(9) + "   ");
				hitsAtK.reset();
				// not covered
				computeScores(rs, goldUncovered, hitsAtK);
				System.out.print( hitsAtK.getHitsAtK(0) + "   " + hitsAtK.getHitsAtK(9) + "");
				hitsAtK.reset();
				
			}
			*/
			
			
			System.out.println();
		}
		
		
		
		
		
		
	}


	private static void computeScores(ResultSet rs, GoldStandard gold, HitsAtK hitsAtK) {
		for (String triple : gold.triples) {
			if (RELATION_FILTER.size() > 0) {
				String relation = triple.split(" ")[1];
				if (!RELATION_FILTER.contains(relation)) continue;
			}
			String[] tt = triple.split(" ");
			Triple t = new Triple(tt[0], tt[1], tt[2]);
			// System.out.print(t+ "\t");
			if (gold.getCategory(triple, true) != null) {

				ArrayList<String> cand = rs.getHeadCandidates(triple);
				// System.out.print(cand.size() + "\t");
				// String c = cand.size() > 0 ? cand.get(0) : "-";
				if (ALL_HEAD_TAIL == 0 || ALL_HEAD_TAIL == 1) hitsAtK.evaluateHead(cand, t);
			}
			if (gold.getCategory(triple, false) != null) {

				ArrayList<String> cand = rs.getTailCandidates(triple);
				// System.out.print(cand.size() + "\t");
				// String c = cand.size() > 0 ? cand.get(0) : "-";
				if (ALL_HEAD_TAIL == 0 || ALL_HEAD_TAIL == 2) hitsAtK.evaluateTail(cand, t);
				// if (rank == -1) System.out.println("NOT FOUND: " + triple);
			}
			// System.out.println();
		}
	}
	
	
	private static void computeScores(ResultSet rs, TripleSet gold, HitsAtK hitsAtK) {
		for (Triple t : gold.getTriples()) {
			if (RELATION_FILTER.size() > 0) {
				if (!RELATION_FILTER.contains(t.getRelation())) continue;
			}
			// System.out.print(t+ "\t");
			
				if (!target.equals("ASS")) {
					ArrayList<String> cand1 = rs.getHeadCandidates(t.toString());
					// System.out.print(cand.size() + "\t");
					String c1 = cand1.size() > 0 ? cand1.get(0) : "-";
					if (ALL_HEAD_TAIL == 0 || ALL_HEAD_TAIL == 1) hitsAtK.evaluateHead(cand1, t);
				}
			
				ArrayList<String> cand2 = rs.getTailCandidates(t.toString());
				// System.out.print(cand.size() + "\t");
				// String c2 = cand2.size() > 0 ? cand2.get(0) : "-";
				if (ALL_HEAD_TAIL == 0 || ALL_HEAD_TAIL == 2) hitsAtK.evaluateTail(cand2, t);
			// System.out.println();
		}
	}
	
	
	private static void printAndMarkUnfoundTriples(ResultSet rs, GoldStandard gold, HitsAtK hitsAtK) {
		for (String triple : gold.triples) {
			String[] tt = triple.split(" ");
			Triple t = new Triple(tt[0], tt[1], tt[2]);
			if (gold.getCategory(triple, true) != null) {
				ArrayList<String> cand = rs.getHeadCandidates(triple);
				String c = cand.size() > 0 ? cand.get(0) : "-";
				int foundAt = hitsAtK.evaluateHead(cand, t);
				if (foundAt < 0) System.out.println(t.getHead() + " headX" + t.getRelation() + " " + t.getTail());
			}
			if (gold.getCategory(triple, false) != null) {
				ArrayList<String> cand = rs.getTailCandidates(triple);
				String c = cand.size() > 0 ? cand.get(0) : "-";
				int foundAt = hitsAtK.evaluateTail(cand, t);
				if (foundAt < 0) System.out.println(t.getHead() + " tailX" + t.getRelation() + " " + t.getTail());
			}
		}
	}
	
	
	private static void compareResultSets(ResultSet rs1, ResultSet rs2, GoldStandard gold, HitsAtK hitsAtK) {
		for (String triple : gold.triples) {
			String[] tt = triple.split(" ");
			Triple t = new Triple(tt[0], tt[1], tt[2]);
			if (gold.getCategory(triple, true) != null) {
				ArrayList<String> cand1 = rs1.getHeadCandidates(triple);
				ArrayList<String> cand2 = rs2.getHeadCandidates(triple);
				boolean foundBy1 = false;
				for (String c1 : cand1) {
					if (t.getHead().equals(c1)) foundBy1 = true;
				}
				boolean foundBy2 = false;
				for (String c2 : cand2) {
					if (t.getHead().equals(c2)) foundBy2 = true;
				}
				if (foundBy1 != foundBy2) {
					System.out.println("H " + rs1.getName() + "=" + foundBy1 + " " + rs2.getName() + "=" + foundBy2 + " " + triple);
				}
				
			}
			if (gold.getCategory(triple, false) != null) {
				ArrayList<String> cand1 = rs1.getTailCandidates(triple);
				ArrayList<String> cand2 = rs2.getTailCandidates(triple);;
				boolean foundBy1 = false;
				for (String c1 : cand1) {
					if (t.getTail().equals(c1)) foundBy1 = true;
				}
				boolean foundBy2 = false;
				for (String c2 : cand2) {
					if (t.getTail().equals(c2)) foundBy2 = true;
				}
				if (foundBy1 != foundBy2) {
					System.out.println("T " + rs1.getName() + "=" + foundBy1 + " " + rs2.getName() + "=" + foundBy2 + " " + triple);
				}
			}
		}
	}

}
