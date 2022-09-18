package de.unima.ki.anyburl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import de.unima.ki.anyburl.Settings;
import de.unima.ki.anyburl.io.IOHelper;

public class TripleSet {
	
	
	public static boolean supportRandomAccess = true;
	
	
	private ArrayList<Triple> triples;
	private HashMap<AnnotatedTriple, Double> atriples = new HashMap<AnnotatedTriple, Double>();
	private Random rand;
	
	
	// relation to degree of (inverse) functionality 
	private HashMap<String, Double> relation2DOF =  new HashMap<String, Double>();
	private HashMap<String, Double> relation2DOIF =  new HashMap<String, Double>();
	
	
	// forall X !exists Z r2(X,Z) <=  exists Y r1(X,Y) ... without object identity
	// !r2 if r1 with conf
	private HashMap<String, HashMap<String, Double>> nXYXZ =  new HashMap<String, HashMap<String, Double>>();
	private HashMap<String, HashMap<String, Double>> nXYYZ =  new HashMap<String, HashMap<String, Double>>();
	private HashMap<String, HashMap<String, Double>> nXYZX =  new HashMap<String, HashMap<String, Double>>();
	private HashMap<String, HashMap<String, Double>> nXYZY =  new HashMap<String, HashMap<String, Double>>();
	

	private HashMap<String, ArrayList<Triple>> headToList;
	private HashMap<String, ArrayList<Triple>> tailToList;
	private HashMap<String, ArrayList<Triple>> relationToList;
	

	private HashMap<String, HashMap<String, HashSet<String>>> headRelation2Tail;
	private HashMap<String, HashMap<String, HashSet<String>>> tailRelation2Head;
	
	private HashMap<String, HashMap<String, String[]>> headRelation2TailField;
	private HashMap<String, HashMap<String, String[]>> tailRelation2HeadField;
	
	HashSet<String> frequentRelations = new HashSet<String>();
	
	private HashMap<String,  ArrayList<String>> relation2HeadSample = new HashMap<String, ArrayList<String>>();
	private HashMap<String,  ArrayList<String>> relation2TailSample = new HashMap<String, ArrayList<String>>();

	
	

	
	public static void main(String[] args) {
		
		TripleSet ts = new TripleSet("data/WN18RR/train.txt");

		ts.computeFunctionality();
		ts.showFunctionalityScores();
		
		Triple t = new Triple("02785648", "_has_part", "10488865");
		
		System.out.println(ts.getFunctionalityBasedWeight(t));
		
		
		
		ts.computeNegativeRelationDependencies();
		
		
		
	}
	
	public TripleSet(String filepath) {
		
		this();
		DecimalFormat df = new DecimalFormat("000000.00");
		this.readTriples(filepath, true);
		// System.out.println("MEMORY REQUIRED: " + df.format(Runtime.getRuntime().totalMemory() / 1000000.0) + " MByte");
		this.indexTriples();
		// System.out.println("MEMORY REQUIRED: " + df.format(Runtime.getRuntime().totalMemory() / 1000000.0) + " MByte");
		this.setupListStructure();
		// System.out.println("MEMORY REQUIRED: " + df.format(Runtime.getRuntime().totalMemory() / 1000000.0) + " MByte");
	}
	
	public TripleSet(String filepath, boolean ignore4Plus) {
		
		this();
		DecimalFormat df = new DecimalFormat("000000.00");
		this.readTriples(filepath, ignore4Plus);
		// System.out.println("MEMORY REQUIRED: " + df.format(Runtime.getRuntime().totalMemory() / 1000000.0) + " MByte");
		this.indexTriples();
		// System.out.println("MEMORY REQUIRED: " + df.format(Runtime.getRuntime().totalMemory() / 1000000.0) + " MByte");
		this.setupListStructure();
		// System.out.println("MEMORY REQUIRED: " + df.format(Runtime.getRuntime().totalMemory() / 1000000.0) + " MByte");
	}
	
	public TripleSet() {
		
		this.rand = new Random();
		
		this.triples = new ArrayList<Triple>();

		this.relationToList = new HashMap<String, ArrayList<Triple>>();

		this.headToList = new HashMap<String, ArrayList<Triple>>();
		this.tailToList = new HashMap<String, ArrayList<Triple>>();
		
		this.headRelation2Tail = new HashMap<String, HashMap<String, HashSet<String>>>();
		this.tailRelation2Head = new HashMap<String, HashMap<String, HashSet<String>>>();
		
		this.headRelation2TailField = new HashMap<String, HashMap<String, String[]>>();
		this.tailRelation2HeadField = new HashMap<String, HashMap<String, String[]>>();

			
	}
	
	public void addTripleSet(TripleSet ts) {
		for (Triple t : ts.triples) {
			this.addTriple(t);
		}
		for (AnnotatedTriple at : ts.atriples.keySet()) {
			this.addTriple(at);
		}
	}
	
	public void addTriples(ArrayList<Triple> triples) {
		for (Triple t : triples) {
			this.addTriple(t);
		}
	}
	
	
	public void addTriple(AnnotatedTriple t) {
		if (this.isTrue(t) && !this.atriples.containsKey(t)) {
			return;
		}
		else {
			if (this.atriples.containsKey(t)) {
				double storedConfidence = this.atriples.get(t);
				double newConfidence = t.getConfidence();
				if (newConfidence > storedConfidence) {
					this.atriples.put(t, t.getConfidence());
				}
			}
			else {
				// System.out.println(" adding new annotated triple " + t);
				this.atriples.put(t, t.getConfidence());
				this.addTripleToIndex(t);
			}
		}
	}
	
	// fix stuff here
	public void addTriple(Triple t) {
		if (this.isTrue(t) && !this.atriples.containsKey(t)) {
			return;
		}
		else {
			if (!t.invalid) this.triples.add(t);
			//if (this.atriples.containsKey(t)) {
			//	this.atriples.remove(t);
			//}
			//else {
				if (!t.invalid) this.addTripleToIndex(t);
			//}
		}
	}

	
	private void indexTriples() {
		long tCounter = 0;
		long divisor = 10000;
		for (Triple t : triples) {
			tCounter++;
			if (tCounter % divisor == 0) {
				System.out.println("* indexed " + tCounter + " triples");
				divisor *= 2;
			}
			addTripleToIndex(t);
		}
		System.out.println("* set up index for " + this.relationToList.keySet().size() + " relations, " + this.headToList.keySet().size() + " head entities, and " + this.tailToList.keySet().size() + " tail entities" );
	}
	
	
	private void setupListStructure() {
		if (supportRandomAccess) {
			System.out.print("* set up list structure ... ");
			// head -> relation -> tails
			for (String relation : this.headRelation2Tail.keySet()) {
				this.headRelation2TailField.put(relation, new HashMap<String, String[]>());
				for (String head : this.headRelation2Tail.get(relation).keySet()) {
					String[] field;
					if (this.headRelation2Tail.get(relation).get(head) == null || this.headRelation2Tail.get(relation).get(head).size() == 0) {
						field = new String[0];
					}
					else {
						field = new String[this.headRelation2Tail.get(relation).get(head).size()];
					}
					int i = 0;
					for (String value : this.headRelation2Tail.get(relation).get(head)) {
						field[i] = value;
						i++;
					}
					if (i > Settings.MAX_NUM_PRESAMPLING) field = getSampledSubset(field);
					this.headRelation2TailField.get(relation).put(head, field);
				}
			}
			// tail -> relation -> head
			for (String relation : this.tailRelation2Head.keySet()) {
				this.tailRelation2HeadField.put(relation, new HashMap<String, String[]>());
				for (String tail : this.tailRelation2Head.get(relation).keySet()) {
					String[] field;
					if (this.tailRelation2Head.get(relation).get(tail) == null || this.tailRelation2Head.get(relation).get(tail).size() == 0) {
						field = new String[0];
					}
					else {
						field = new String[this.tailRelation2Head.get(relation).get(tail).size()];
					}
					
					int i = 0;
					for (String value : this.tailRelation2Head.get(relation).get(tail)) {
						field[i] = value;
						i++;
					}
					if (i > Settings.MAX_NUM_PRESAMPLING) field = getSampledSubset(field);
					this.tailRelation2HeadField.get(relation).put(tail, field);
				}
			}
			System.out.println(" done");
		}
	}
	
	private String[] getSampledSubset(String[] field) {
		String[] subField = new String[Settings.MAX_NUM_PRESAMPLING];
		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < field.length; i++) list.add(field[i]);
		Collections.shuffle(list);
		for (int i = 0; i < Settings.MAX_NUM_PRESAMPLING; i++) subField[i] = list.get(i);
		return subField;
	}
	
	
	private void addTripleToIndex(Triple t) {
		String head = t.getHead();
		String tail = t.getTail();
		String relation = t.getRelation();
		// index head
		if (!this.headToList.containsKey(head)) {
			this.headToList.put(head, new ArrayList<Triple>());
		}
		this.headToList.get(head).add(t);
		// index tail
		if (!this.tailToList.containsKey(tail)) {
			this.tailToList.put(tail, new ArrayList<Triple>());
		}
		this.tailToList.get(tail).add(t);
		// index relation
		if (!this.relationToList.containsKey(relation)) {
			this.relationToList.put(relation, new ArrayList<Triple>());
		}
		this.relationToList.get(relation).add(t);
		
		
		
		// index head-relation => tail
		if(!this.headRelation2Tail.containsKey(relation)) {
			this.headRelation2Tail.put(relation, new HashMap<String, HashSet<String>>());
		}
		if (!this.headRelation2Tail.get(relation).containsKey(head)) {
			this.headRelation2Tail.get(relation).put(head, new HashSet<String>());
		}
		this.headRelation2Tail.get(relation).get(head).add(tail);
		
		
		
		
		// index tail-relation => head
		if(!this.tailRelation2Head.containsKey(relation)) {
			this.tailRelation2Head.put(relation, new HashMap<String, HashSet<String>>());
		}
		if (!this.tailRelation2Head.get(relation).containsKey(tail)) {
			this.tailRelation2Head.get(relation).put(tail, new HashSet<String>());
		}
		this.tailRelation2Head.get(relation).get(tail).add(head);
		
	}
	
	/**
	 * This code is base on the assumption that the given triple is contained in this triple set.
	 * If not there will be null pointer exceptions.
	 * 
	 * Take care, the triple that is removed from the index will still be in the overall list of triples.
	 * However, a specific query related to this triple will be negative after the removal,
	 * 
	 * @param t The triple to be removed from the index.
	 */
	public void removeTripleFromIndex(Triple t) {
		String head = t.getHead();
		String tail = t.getTail();
		String relation = t.getRelation();
		// index head
		this.headToList.get(head).remove(t);
		// index tail
		this.tailToList.get(tail).remove(t);
		// index relation
		this.relationToList.get(relation).remove(t);
		// index head-relation => tail
		this.headRelation2Tail.get(relation).get(head).remove(tail);
		// index tail-relation => head
		this.tailRelation2Head.get(relation).get(tail).remove(head);
	}


	private void readTriples(String filepath, boolean ignore4Plus) {
		Path file = (new File(filepath)).toPath();
		// Charset charset = Charset.forName("US-ASCII");
		Charset charset = Charset.forName("UTF8");
		String line = null;
		long lineCounter = 0;
		try (BufferedReader reader = Files.newBufferedReader(file, charset)) { 
			while ((line = reader.readLine()) != null) {
				//System.out.println("--- " + line);
				
				// System.out.println(line);
				lineCounter++;
				if (lineCounter % 1000000 == 0) {
					System.out.println(">>> parsed " + lineCounter + " lines");
				}
				if (line.length() <= 2) continue;
				String[] token = line.split("\t");
				if (token.length < 3) token = line.split(" ");
				Triple t = null;
				String s;
				String r;
				String o;
				if (Settings.EFFICIENT_MEMORY_USAGE) {
					if (Settings.SAFE_PREFIX_MODE) {
						s = (Settings.PREFIX_ENTITY + token[0]).intern();
						r = (Settings.PREFIX_RELATION + token[1]).intern();
						o = (Settings.PREFIX_ENTITY + token[2]).intern();					
					}
					else {
						s = token[0].intern();
						r = token[1].intern();
						o = token[2].intern();
					}

					
				}
				else {
					s = token[0];
					r = token[1];
					o = token[2];
					if (Settings.SAFE_PREFIX_MODE) {
						s = Settings.PREFIX_ENTITY + s;
						r = Settings.PREFIX_RELATION + r;
						o = Settings.PREFIX_ENTITY + o;					
					}
				}
				// if (!(r.equals("r96") || r.equals("r25") || r.equals("r2"))) continue;
				if (token.length == 3) t = new Triple(s, r, o);
				if (token.length != 3 && ignore4Plus) t = new Triple(s, r, o);
				if (token.length == 4 && !ignore4Plus) {
					if (token[3].equals(".")) {
						 t = new Triple(s, r, o);
					}
					else {
						try {
							t = new AnnotatedTriple(s, r, o);
							((AnnotatedTriple)t).setConfidence(Double.parseDouble(token[3]));
						}
						catch (NumberFormatException nfe) {
							System.err.println("could not parse line " + line);
							t = null;
						}	
					}
				}
				// VERY SPECIAL CASE FOR SAMUELS DATASET
				if (token.length == 5 && !ignore4Plus) {
					String subject = token[0];
					String relation = token[1];
					String object = token[2];
					subject = subject.replace(" ", "_");
					relation = relation.replace(" ", "_");
					object = object.replace(" ", "_");
					t = new Triple(subject, relation, object);
				}
				
				if (t == null) { }
				else {
					if (!t.invalid) {
						//System.out.println(">>> " + t);
						this.triples.add(t);
					}
					
					if (Settings.REWRITE_REFLEXIV && t.getTail().equals(Settings.REWRITE_REFLEXIV_TOKEN)) {
						Triple trev;
						if (!(t instanceof AnnotatedTriple)) trev = new Triple(t.getTail(), t.getRelation(), t.getHead());
						else {
							trev = new AnnotatedTriple(t.getTail(), t.getRelation(), t.getHead());
							((AnnotatedTriple)trev).setConfidence(t.getConfidence());
						}
						if (!trev.invalid) this.triples.add(trev);
					}
				}	
			}
		}
		catch (IOException x) {
			System.err.format("IOException: %s%n", x);
			System.err.format("Error occured for line: " + line + " LINE END");
		}
		// Collections.shuffle(this.triples);
		System.out.println("* read " + this.triples.size() + " triples");
	}
	
	public ArrayList<Triple> getTriples() {
		return this.triples;
	}
	
	public HashMap<AnnotatedTriple, Double> getAnnotatedTriples() {
		return this.atriples;
	}
	
	
	
	public ArrayList<Triple> getTriplesByHead(String head) {
		if (this.headToList.containsKey(head)) {
			return this.headToList.get(head);
		}
		else {
			return new ArrayList<Triple>();
		}
	}
	
	
	public ArrayList<Triple> getTriplesByTail(String tail) {
		if (this.tailToList.containsKey(tail)) {
			return this.tailToList.get(tail);
		}
		else {
			return new ArrayList<Triple>();
		}
	}
	
	public ArrayList<Triple> getTriplesByHeadNotTail(String headOrTail, boolean byHeadNotTail) {
		if (byHeadNotTail) return this.getTriplesByHead(headOrTail);
		else return this.getTriplesByTail(headOrTail);
	}
	
	/*
	public ArrayList<Triple> getNTriplesByHead(String head, int n) {
		if (this.headToList.containsKey(head)) {
			if (this.headToList.get(head).size() <= n) return this.headToList.get(head);
			else {
				ArrayList<Triple> chosen = new ArrayList<Triple>();
				for (int i = 0; i < n; i++) {
					int index = this.rand.nextInt(this.headToList.get(head).size());
					chosen.add(this.headToList.get(head).get(index));
				}
				return chosen;
			}
		}
		else return new ArrayList<Triple>();
	}
	*/

	/*
	public ArrayList<Triple> getNTriplesByTail(String tail, int n) {
		
		if (this.tailToList.containsKey(tail)) {
			if (this.tailToList.get(tail).size() <= n) return this.tailToList.get(tail);
			else {
				ArrayList<Triple> chosen = new ArrayList<Triple>();
				for (int i = 0; i < n; i++) {
					int index = this.rand.nextInt(this.tailToList.get(tail).size());
					chosen.add(this.tailToList.get(tail).get(index));
				}
				return chosen;
			}
		}
		else return new ArrayList<Triple>();
	}
	*/
	
	
	public ArrayList<Triple> getTriplesByRelation(String relation) {
		if (this.relationToList.containsKey(relation)) {
			return this.relationToList.get(relation);
		}
		else {
			return new ArrayList<Triple>();
		}
	}
	
	public Triple getRandomTripleByRelation(String relation) {
		if (this.relationToList.containsKey(relation)) {
			return this.relationToList.get(relation).get(this.rand.nextInt(this.relationToList.get(relation).size()));
		}
		return null;
	}
	
	
	public Triple getRandomTriple() {
		int index = this.rand.nextInt(this.triples.size());
		return this.triples.get(index);
	}
	
	public ArrayList<String> getNRandomEntitiesByRelation(String relation, boolean headNotTail, int n) {
		if (headNotTail) {
			if (relation2HeadSample.containsKey(relation)) return relation2HeadSample.get(relation);
		}
		else {
			if (relation2TailSample.containsKey(relation)) return relation2TailSample.get(relation);
		}
		return computeNRandomEntitiesByRelation(relation, headNotTail, n);
	}
	
	
	private synchronized ArrayList<String> computeNRandomEntitiesByRelation(String relation, boolean headNotTail, int n) {
		
		if (this.relationToList.containsKey(relation)) {
			/*
			ArrayList<String> entities = new ArrayList<String>();
			HashSet<String> entitiesAsSet = new HashSet<String>();
			for (Triple triple : this.relationToList.get(relation)) {
				String value = triple.getValue(headNotTail);
				if (!entitiesAsSet.contains(value)) {
					entitiesAsSet.add(value);
					entities.add(value);
				}
			}
			*/
			ArrayList<String> sampledEntities = new ArrayList<String>();
			for (int i = 0; i < n ; i++) {
				Triple triple = this.relationToList.get(relation).get(rand.nextInt(this.relationToList.get(relation).size()));
				String value = triple.getValue(headNotTail);
				sampledEntities.add(value);
			}
			if (headNotTail) this.relation2HeadSample.put(relation, sampledEntities);
			else this.relation2TailSample.put(relation, sampledEntities);
			return sampledEntities;
		}
		else {
			System.err.println("something is strange, internal reference to relation " + relation + ", which is not indexed");
			System.err.println("check if rule set and triple set fit together");
			return null;
		}
	}
	
	/**
	 * Select randomly n entities that appear in head (or tail) position of a triple using ab given relation.
	 * More frequent entities appear more frequent. This is the difefrence compared to the method computeNRandomEntitiesByRelation.
	 * 
	 * @param relation
	 * @param headNotTail
	 * @param n
	 * @return
	 */
	public ArrayList<String> selectNRandomEntitiesByRelation(String relation, boolean headNotTail, int n) {
		
		if (this.relationToList.containsKey(relation)) {
			ArrayList<String> entities = new ArrayList<String>();
			int j = 0;
			for (Triple triple : this.relationToList.get(relation)) {
				j++;
				String value = triple.getValue(headNotTail);
				entities.add(value);
				if (j == n) break;
			}
			ArrayList<String> sampledEntities = new ArrayList<String>();
			for (int i = 0; i < n; i++) {
				String entity = entities.get(rand.nextInt(entities.size()));
				sampledEntities.add(entity);
			}
			return sampledEntities;
		}
		else {
			System.err.println("something is strange, internal reference to relation " + relation + ", which is not indexed");
			System.err.println("check if rule set and triple set fit together");
			return null;
		}
	}
	
	
	public Set<String> getRelations() {
		return this.relationToList.keySet();
	}
	
	public Set<String> getHeadEntities(String relation, String tail) {
		if (tailRelation2Head.get(relation) != null) {
			if (tailRelation2Head.get(relation).get(tail) != null) {
				return tailRelation2Head.get(relation).get(tail);
			}
		}
		return new HashSet<String>();
	}
	
	public Set<String> getTailEntities(String relation, String head) {
		if (headRelation2Tail.get(relation) != null) {
			if (headRelation2Tail.get(relation).get(head) != null) {
				return headRelation2Tail.get(relation).get(head);
			}
		}
		return new HashSet<String>();
	}
	
	/**
	* Returns those values for which the relation holds for a given value. If the headNotTail is 
	* set to true, the value is interpreted as head value and the corresponding tails are returned.
	* Otherwise, the corresponding heads are returned.
	*  
	* @param relation The specified relation.
	* @param value The value interpreted as given head or tail.
	* @param headNotTail Whether to interpret the value as head and not as tail (false interprets as tail).
	* @return The resulting values.
	*/
	public Set<String> getEntities(String relation, String value, boolean headNotTail) {
		if (headNotTail) return this.getTailEntities(relation, value);
		else return this.getHeadEntities(relation, value);
		
	}
	
	/**
	* Returns a random value for which the relation holds for a given value. If the headNotTail is 
	* set to true, the value is interpreted as head value and the corresponding tails are returned.
	* Otherwise, the corresponding heads are returned.
	*  
	* @param relation The specified relation.
	* @param value The value interpreted as given head or tail.
	* @param headNotTail Whether to interpret the value as head and not as tail (false interprets as tail).
	* @return The resulting value or null if no such value exists.
	*/
	
	
	public String getRandomEntity(String relation, String value, boolean headNotTail) {
		if (headNotTail) return this.getRandomTailEntity(relation, value);
		else return this.getRandomHeadEntity(relation, value);
		
	}
	
	
	
	public String getRandomEntity() {
		Triple triple = this.getRandomTriple();
		boolean headNotTail = this.rand.nextBoolean();
		return triple.getValue(headNotTail);
		
	}
	
	
	private String getRandomHeadEntity(String relation, String tail) {
		if (!tailRelation2HeadField.containsKey(relation)) return null;
		String[] field = this.tailRelation2HeadField.get(relation).get(tail);
		if (field == null || field.length == 0) return null;
		return field[this.rand.nextInt(field.length)];
	}
	
	private String getRandomTailEntity(String relation, String head) {
		if (!headRelation2TailField.containsKey(relation)) return null;
		String[] field = this.headRelation2TailField.get(relation).get(head);
		if (field == null || field.length == 0) return null;
		return field[this.rand.nextInt(field.length)];
	}
	
	
	/*
	public Set<String> getRelations(String head, String tail) {
		if (headTail2Relation.get(head) != null) {
			if (headTail2Relation.get(head).get(tail) != null) {
				return headTail2Relation.get(head).get(tail);
			}
		}
		return new HashSet<String>();
	}
	*/
	
	public boolean isTrue(String head, String relation, String tail) {
		if (tailRelation2Head.get(relation) != null) {
			if (tailRelation2Head.get(relation).get(tail) != null) {
				return tailRelation2Head.get(relation).get(tail).contains(head);
			}
		}
		return false;	
	}
	
	
	
	public boolean isTrue(Triple triple) {
		return this.isTrue(triple.getHead(), triple.getRelation(), triple.getTail());
	}
	
	public double getConfidence(Triple t) {
		if (this.isTrue(t)) return 1.0;
		if (this.atriples.containsKey(t)) return this.atriples.get(t);
		return 0.0;
	}

	public void compareTo(TripleSet that, String thisId, String thatId) {
		System.out.println("* Comparing two triple sets");
		int counter = 0;
		for (Triple t : triples) {
			if (that.isTrue(t)) {
				counter++;
			}
		}
		
		System.out.println("* size of " + thisId + ": " +  this.triples.size());
		System.out.println("* size of " + thatId + ": " +  that.triples.size());
		System.out.println("* size of intersection: " + counter);
		
	}

	public TripleSet getIntersectionWith(TripleSet that) {
		TripleSet ts = new TripleSet(); 
		for (Triple t : triples) {
			if (that.isTrue(t)) {
				ts.addTriple(t);
			}
		}
		return ts;
	}

	public TripleSet minus(TripleSet that) {
		TripleSet ts = new TripleSet(); 
		for (Triple t : triples) {
			if (!that.isTrue(t)) {
				ts.addTriple(t);
			}
		}
		return ts;
	}

	public int getNumOfEntities() {
		return headToList.keySet().size() + tailToList.keySet().size();
	}

	public void determineFrequentRelations(double coverage) {
		HashMap<String, Integer> relationCounter = new HashMap<String, Integer>();
		int allCounter = 0;
		for (Triple t : this.triples) {
			allCounter++;
			String r = t.getRelation();
			if (relationCounter.containsKey(r)) {
				int count = relationCounter.get(r);
				relationCounter.put(r, count + 1);
			}
			else {
				relationCounter.put(r, 1);
			}
		}
		
		ArrayList<Integer> counts = new ArrayList<Integer>();
		counts.addAll(relationCounter.values());
		Collections.sort(counts);
		int countUp = 0;
		int border = 0;
		for (Integer c : counts) {
			countUp = countUp + c;
			//System.out.println("countUp: " + countUp);
			//System.out.println("c: " + c);
			if (((double)(allCounter - countUp) / (double)allCounter) < coverage) {
				border = c;
				break;
			}
		}
		
		//System.out.println("Number of all relations: " + relationCounter.size());
		//System.out.println("Relations covering " + coverage + " of all triples");
		for (String r : relationCounter.keySet()) {
			
			if (relationCounter.get(r) > border) {
				frequentRelations.add(r);
				//System.out.println(r + " (used in " + relationCounter.get(r) + " triples)");
			}
		}
		//System.out.println("Number of frequent (covering " + coverage+ " of all) relations: " + frequentRelations.size());
	}

	public boolean isFrequentRelation(String relation) {
		return this.frequentRelations.contains(relation);
	}

	/*
	public boolean existsPath(String x, String y, int pathLength) {
		if (pathLength == 1) {
			if (this.getRelations(x, y).size() > 0) {
				return true;
			}
			if (this.getRelations(y, x).size() > 0) {
				return true;
			}
			return false;
		}
		if (pathLength == 2) {
			Set<String> hop1x = new HashSet<String>();
			for (Triple hx : this.getTriplesByHead(x)) { hop1x.add(hx.getTail()); }
			for (Triple tx : this.getTriplesByTail(x)) { hop1x.add(tx.getHead()); }

			for (Triple hy : this.getTriplesByHead(y)) {
				if (hop1x.contains(hy.getTail())) return true;
			}
			for (Triple ty : this.getTriplesByTail(y)) {
				if (hop1x.contains(ty.getHead()))  return true;
			}
			return false;
		}
		if (pathLength > 2 ) {
			System.err.println("checking the existence of a path longer than 2 is so far not supported");
			System.exit(-1);
			
		}
		return false;
		
	}
	*/

	public Set<String> getEntities() {
		HashSet<String> entities = new HashSet<String>();
		entities.addAll(headToList.keySet());
		entities.addAll(tailToList.keySet());
		return entities;
	}
	
	public void write(String filepath) throws FileNotFoundException {
		PrintWriter  pw = new PrintWriter(filepath);
		
		for (Triple t : this.triples) {
			 pw.println(t);
		}
		for (Triple t : this.atriples.keySet()) {
			 pw.println(t);
		}
		pw.flush();
		pw.close();
		
	}
	
	public int size() {
		return this.triples.size() + this.atriples.size();
	}
	
	
	public void computeFunctionality() {
		for (String r : this.getRelations()) {
			ArrayList<Triple> triples = this.getTriplesByRelation(r);
			HashSet<String> heads = new HashSet<String>();
			HashSet<String> tails = new HashSet<String>();
			for (Triple triple : triples) {
				heads.add(triple.getHead());
				tails.add(triple.getTail());
			}
			int counter = 0;
			for (String h : heads) {
				int num = this.getTailEntities(r, h).size();
				if (num == 1) counter++;
				// if (num == 2) { System.out.println(h); }
			}
			double dof = (double)counter / (double)heads.size();
			counter = 0;
			for (String t : tails) {
				int num = this.getHeadEntities(r, t).size();
				if (num == 1) counter++;
			}
			double doif = (double)counter / (double)tails.size();			
			this.relation2DOF.put(r, dof);
			this.relation2DOIF.put(r, doif);
		}
		showFunctionalityScores();
	}
	
	
	
	
	
	
	public void showFunctionalityScores() {
		DecimalFormat df = new DecimalFormat("0.000");
		System.out.println("-------------------------");
		for (String relation : this.getRelations()) {
			System.out.println(relation + ": " + df.format(this.relation2DOF.get(relation)) + " | " + df.format(this.relation2DOIF.get(relation)));
		}
		System.out.println("-------------------------");	
	}
	
	

	public void computeNegativeRelationDependencies() {
		for (String r : this.getRelations()) {
			this.nXYXZ.put(r, new HashMap<String, Double>());
			this.nXYYZ.put(r, new HashMap<String, Double>());
			this.nXYZX.put(r, new HashMap<String, Double>());
			this.nXYZY.put(r, new HashMap<String, Double>());
		}
		
		for (String rb : this.getRelations()) {
			ArrayList<Triple> triplesBody = this.getTriplesByRelation(rb);
			HashSet<String> xs = new HashSet<String>();
			HashSet<String> ys = new HashSet<String>();
			for (Triple tripleBody : triplesBody) {
				xs.add(tripleBody.getHead());
				ys.add(tripleBody.getTail());
			}
			for (String rh : this.getRelations()) {
				
				ArrayList<Triple> triplesHead = this.getTriplesByRelation(rh);
				HashSet<String> xsh = new HashSet<String>();
				HashSet<String> ysh = new HashSet<String>();
				for (Triple tripleHead : triplesHead) {
					xsh.add(tripleHead.getHead());
					ysh.add(tripleHead.getTail());
				}
				
				
				
				if (rh.equals(rb)) continue;
				int counterXYXZ = 0;
				int counterXYYZ = 0;
				int counterXYZX = 0;
				int counterXYZY = 0;
				for (String x : xs) {
					if (this.getTailEntities(rh, x).size() > 0) counterXYXZ++; 
					if (this.getHeadEntities(rh, x).size() > 0) counterXYYZ++; 
				}
				for (String y : ys) {
					if (this.getTailEntities(rh, y).size() > 0)	counterXYZX++;
					if (this.getHeadEntities(rh, y).size() > 0)	counterXYZY++;
					
				}
				
				// System.out.println(rh + "| " +  rb + ": "  + counterXYXZ +  " / "+ xs.size() + " xor " + ys.size());

				int offset = 0;
				
				int popXYXZ = Math.min(xs.size(), xsh.size());
				int popXYYZ = Math.min(xs.size(), ysh.size());
				int popXYZX = Math.min(ys.size(), xsh.size());
				int popXYZY = Math.min(ys.size(), ysh.size());
				
				this.nXYXZ.get(rh).put(rb,  (double)(popXYXZ - (counterXYXZ + offset)) / (double)popXYXZ);
				this.nXYYZ.get(rh).put(rb,  (double)(popXYYZ - (counterXYYZ + offset)) / (double)popXYYZ);
				this.nXYZX.get(rh).put(rb,  (double)(popXYZX - (counterXYZX + offset)) / (double)popXYZX);
				this.nXYZY.get(rh).put(rb,  (double)(popXYZY - (counterXYZY + offset)) / (double)popXYZY);
			}
		}
		showNegativeRelationDependencies();

	}
	
	public void showNegativeRelationDependencies() {
		DecimalFormat df = new DecimalFormat("0.0000000");
		System.out.println("-------------------------");
		for (String rb : this.getRelations()) {
			for (String rh : this.getRelations()) {
				if (rh.equals(rb)) continue;
				
				if (this.nXYXZ.get(rh).get(rb) > Settings.TYPE_THRESHOLD) {
					System.out.println("[" + df.format(this.nXYXZ.get(rh).get(rb)) + "] !" + rh + "(X,Y) <= " + rb + "(X,Z)");
				}
				if (this.nXYZX.get(rh).get(rb)  > Settings.TYPE_THRESHOLD) {
					System.out.println("[" + df.format(this.nXYZX.get(rh).get(rb)) + "] !" + rh + "(X,Y) <= " + rb + "(Z,X)");
				}
				if (this.nXYYZ.get(rh).get(rb) > Settings.TYPE_THRESHOLD) {
					System.out.println("[" + df.format(this.nXYYZ.get(rh).get(rb)) + "] !" + rh + "(X,Y) <= " + rb + "(Y,Z)");
				}
				if (this.nXYZY.get(rh).get(rb)  > Settings.TYPE_THRESHOLD) {
					System.out.println("[" + df.format(this.nXYZY.get(rh).get(rb)) + "] !" + rh + "(X,Y) <= " + rb + "(Z,Y)");
				}
				
			}
		}
		System.out.println("-------------------------");	
	}
	
	
	public double getTypeBasedWeight(Triple triple) {
		
		// System.out.println("checking  " + triple);
		if (this.isTrue(triple)) return 1.0;

		
		String h = triple.getHead();
		String r = triple.getRelation();
		String t = triple.getTail();		
		
		double threshold = Settings.TYPE_THRESHOLD;
		double min = 1.0;
		double factor = 1.0 / (1.0 - threshold);

		for (String br : this.nXYXZ.get(r).keySet()) {
			if (this.nXYXZ.get(r).get(br) > threshold) {
				if (this.getEntities(br, h, true).size() > 0) {
					double w = 1.0 - ((this.nXYXZ.get(r).get(br) - threshold) * factor);
					min = min > w ? w : min;
				}
			}
		}
		
		for (String br : this.nXYZX.get(r).keySet()) {
			if (this.nXYZX.get(r).get(br) > threshold) {
				if (this.getEntities(br, h, false).size() > 0) {
					double w = 1.0 - ((this.nXYZX.get(r).get(br) - threshold) * factor);
					min = min > w ? w : min;
				}
			}
		}
		
		for (String br : this.nXYYZ.get(r).keySet()) {
			if (this.nXYYZ.get(r).get(br) > threshold) {
				// System.out.println("head-relation = " + r + "   body-relation = " + br + "  => " + this.nXYYZ.get(r).get(br));
				if (this.getEntities(br, t, true).size() > 0) {
					double w = 1.0 - ((this.nXYYZ.get(r).get(br) - threshold) * factor);
					min = min > w ? w : min;
				}
			}
		}
		
		for (String br : this.nXYZY.get(r).keySet()) {
			if (this.nXYZY.get(r).get(br) > threshold) {
				// System.out.println("head-relation = " + r + "   body-relation = " + br + "  => " + this.nXYYZ.get(r).get(br));
				if (this.getEntities(br, t, false).size() > 0) {
					double w = 1.0 - ((this.nXYZY.get(r).get(br) - threshold) * factor);
					min = min > w ? w : min;
				}
			}
		}
		return min;	
	}
	
	
	public double getFunctionalityBasedWeight(Triple triple) {
		
		if (this.isTrue(triple)) return 1.0;

		String r = triple.getRelation();
		String h = triple.getHead();
		String t = triple.getTail();
		
		double threshold = Settings.FUNCTIONALITY_THRESHOLD;
		double factor = 1.0 / (1.0 - threshold);
		
		double dof = this.relation2DOF.get(r) != null ? this.relation2DOF.get(r) : 0.1;
		double doif = this.relation2DOIF.get(r) != null ? this.relation2DOIF.get(r) : 0.1;
		
		double w1 = 1.0;
		// check functionality violation
		if (this.getTailEntities(r, h).size() > 0 && dof > threshold) {
			w1 = 1.0 - ((dof - threshold) * factor);
			// w1 = 0;
		}
				
		double w2 = 1.0;
		// check inverse functionality violation
		if (this.getHeadEntities(r, t).size() > 0 && doif > threshold) {
			w2 = 1.0 - ((doif - threshold) * factor);
			// w2 = 0;
		}
		// System.out.println(w1 * w2);
		
		return (w1 * w2);
	}


	
	
	
	
	
}
