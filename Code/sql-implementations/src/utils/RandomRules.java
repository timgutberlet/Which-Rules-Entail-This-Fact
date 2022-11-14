package utils;

import config.Settings;
import database.DBFuncs;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import models.Key2Int;
import models.Key3Int;
import models.Rule;
import models.Triple;
import models.Variables;
import org.postgresql.jdbc2.ArrayAssistant;

/**
 * @author timgutberlet
 */
public class RandomRules {
  private HashMap<String, Integer> subjectIndex;
  private HashMap<String, Integer> predicateIndex;
  private HashMap<String, Integer> objectIndex;
  private HashMap<Key2Int, ArrayList<Rule>> subBound= new HashMap<>();
  private HashMap<Key2Int, ArrayList<Rule>> objBound= new HashMap<>();
  private HashMap<Key3Int, ArrayList<Rule>> bothBound= new HashMap<>();
  private HashMap<Integer, ArrayList<Rule>> noBoundUnequal = new HashMap<>();
  private HashMap<Integer, ArrayList<Rule>> noBoundEqual = new HashMap<>();

  public RandomRules(HashMap<String, Integer> subjectIndex,
      HashMap<String, Integer> predicateIndex, HashMap<String, Integer> objectIndex) {
    this.subjectIndex = subjectIndex;
    this.predicateIndex = predicateIndex;
    this.objectIndex = objectIndex;
    importRules();
  }

  public void importRules() {
    Key3Int key3Int;
    Key2Int key2Int;
    String file = Settings.RULES_PATH;
    InputStream is = DbFill.class.getResourceAsStream(file);
    InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(streamReader);
    Rule rule;
    Integer counter = 0;
    Boolean continuer;
    try {
      String first;
      String second;
      String[] help2, help1, help3;
      String headPredicate, headSubject, headObject;
      String bodyPredicate, bodySubject, bodyObject;
      int subjectID, objectID, predicateID;
      Triple head;
      List<Triple> body;
      Triple tripleHelp;
      ArrayList<String> filteredRulesStrings = new ArrayList<>();
      for (String line; (line = reader.readLine()) != null; ) {
        body = new ArrayList<>();
        headSubject = "";
        headObject = "";
        // Get the left characters that are important
        help1 = line.split("<=");
        first = help1[0];
        help2 = first.split("\\s+");
        first = help2[3];
        headPredicate = first.split("\\(", 2)[0];
        if (help1.length > 1) {
          second = help1[1].strip();
          help3 = second.split(",\\s");
          for (String triple : help3) {
            bodyPredicate = triple.split("\\(", 2)[0];
            help1 = triple.split("\\(", 2)[1].split(",", 2);
            bodySubject = help1[0];
            bodyObject = help1[1].substring(0, help1[1].length() - 1);
            if (bodySubject.length() == 1) {
              subjectID = Variables.getID(bodySubject);
            } else {
              if (subjectIndex.get(delE(bodySubject)) != null){
                subjectID = subjectIndex.get(delE(bodySubject));
              }else {
                subjectID = -99;
              }
            }
            if (bodyObject.length() == 1) {
              objectID = Variables.getID(bodyObject);
            } else {
              if (objectIndex.get(delE((bodyObject))) != null){
                objectID = objectIndex.get(delE(bodyObject));
              }else {
                objectID = -99;
              }
            }
            if(predicateIndex.get(delR(bodyPredicate)) != null){
              predicateID = predicateIndex.get(delR(bodyPredicate));
            }else {
              predicateID = -99;
            }
            if (subjectID != -99 && predicateID != -99 && objectID != -99){
              tripleHelp = new Triple(subjectID, predicateID,
                  objectID);
              body.add(tripleHelp);
            }
            //tailList.add(new Triple(Integer.parseInt(bodySubject), Integer.parseInt(delE(bodyObject)), Integer.parseInt(bodyPredicate)));
          }
        }
        //Uncomment this, if you also want to inlcude empty rules
        /*else {
          tripleHelp = new Triple(DBFuncs.getSubjectID(bodySubject), DBFuncs.getPredicateID(bodyPredicate),
              DBFuncs.getObjectID(bodyObject));
          body.add(tripleHelp);
          System.out.println(tripleHelp);
        }*/
        help1 = first.split("\\(", 2)[1].split(",", 2);
        if (help1.length > 1) {
          headSubject = help1[0];
          headObject = help1[1].substring(0, help1[1].length() - 1);
        }
        //System.out.println(headPredicate + " : " + headSubject + " " + headObject + " <= " + second);
        if (headSubject.length() == 1) {

        }
        if (headSubject.length() == 1) {
          subjectID = Variables.getID(headSubject);
        } else {
          if (subjectIndex.get(delE(headSubject)) != null){
            subjectID = subjectIndex.get(delE(headSubject));
          }else {
            subjectID = -99;
          }
        }
        if (headObject.length() == 1) {
          objectID = Variables.getID(headObject);
        } else {
          if (objectIndex.get(delE(headObject)) != null){
            objectID = objectIndex.get(delE(headObject));
          }else {
            objectID = -99;
          }
        }
        if (predicateIndex.get(delR(headPredicate)) != null){
          predicateID = predicateIndex.get(delR(headPredicate));
        }else {
          predicateID = -99;
        }
        if(subjectID != -99 && predicateID !=  -99 && objectID != -99){
          head = new Triple(subjectID, predicateID, objectID);
          rule = new Rule(head, body);
        }else {
          continue;
        }


        if(Settings.FILTER_SIMPLE_RULES.equals("YES")){
          continuer = false;

          if(rule.getHead().getSubject() < 0
                  && rule.getHead().getObject() < 0
                  && rule.getBody().size() == 1){
            if (rule.getHead().getSubject() == rule.getBody().get(0).getSubject()
                    && rule.getHead().getObject() == rule.getBody().get(0).getObject()){
              continuer = true;
              System.out.println("Type1: "+ rule);
            }
          }
          else if(rule.getHead().getSubject() < 0
                  && rule.getHead().getObject() < 0
                  && rule.getBody().size() == 2){
            if (rule.getHead().getSubject() == rule.getBody().get(0).getSubject()
                    && rule.getHead().getObject() == rule.getBody().get(1).getObject()
                    && rule.getBody().get(0).getObject() == rule.getBody().get(1).getSubject() ){
              continuer = true;
              System.out.println("Type2: "+ rule);
            }
          }
          else if(rule.getHead().getSubject() < 0
                  && rule.getHead().getObject() >= 0
                  && rule.getBody().size() == 1 ){
            if(rule.getBody().get(0).getSubject() == rule.getHead().getSubject()
                    && rule.getBody().get(0).getObject() >= 0){
              continuer = true;
              System.out.println("Type3: "+ rule);
            }
          }

          if (!continuer){
            continue;
          }else {
            filteredRulesStrings.add(line);
          }
        }


        //Beide ungebunden & ungleich
        if (rule.getHead().getObject()<0 && rule.getHead().getSubject() <0 && rule.getHead().getObject() != rule.getHead().getSubject()){
          if(!noBoundUnequal.containsKey(rule.getHead().getPredicate())){
            noBoundUnequal.put(rule.getHead().getPredicate(), new ArrayList<>());
          }
            noBoundUnequal.get(rule.getHead().getPredicate()).add(rule);
        }
        //Beide ungebunden und gleich
        if (rule.getHead().getObject()<0 && rule.getHead().getSubject() <0 && rule.getHead().getObject() == rule.getHead().getSubject()){
          if(!noBoundEqual.containsKey(rule.getHead().getPredicate())){
            noBoundEqual.put(rule.getHead().getPredicate(), new ArrayList<>());
          }
          noBoundEqual.get(rule.getHead().getPredicate()).add(rule);
        }
        // Beide Gebunden
        if(rule.getHead().getSubject() >= 0 && rule.getHead().getObject() >= 0){
          key3Int = new Key3Int(rule.getHead().getSubject(), rule.getHead().getPredicate(), rule.getHead().getObject());
          if(!bothBound.containsKey(key3Int)){
            bothBound.put(key3Int, new ArrayList<>());
          }
          bothBound.get(key3Int).add(rule);
        }

        // Subject Gebunden, Object Frei
        if (rule.getHead().getSubject() >= 0 && rule.getHead().getObject() < 0){
          key2Int = new Key2Int(rule.getHead().getSubject(), rule.getHead().getPredicate());
          if(!subBound.containsKey(key2Int)){
            subBound.put(key2Int, new ArrayList<>());
          }
          subBound.get(key2Int).add(rule);
        }

        // Object gebunden, Subject Frei
        if (rule.getHead().getObject() >= 0 && rule.getHead().getSubject() < 0){
          key2Int = new Key2Int(rule.getHead().getPredicate(), rule.getHead().getObject());
          if (!objBound.containsKey(key2Int)){
            objBound.put(key2Int, new ArrayList<>());
          }
          objBound.get(key2Int).add(rule);
        }

        rule.setId(counter++);

        //ruleList.add(new Rule(head, body));
      }
      System.out.println("Import finished");

      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if(Settings.FILTER_SIMPLE_RULES.equals("YES")){

    }
    //System.out.println("noBoundUnequal");
    //noBoundUnequal.forEach((integer, rules) -> System.out.println(integer.toString() + " : " +rules));
    //System.out.println("noBoundEqual");
    //noBoundEqual.forEach((integer, rules) -> System.out.println(integer.toString() + " : " +rules));
    //System.out.println("objBound");
    //objBound.forEach((integer, rules) -> System.out.println(integer.toString() + " : " +rules));
    //System.out.println("subBound");
    //subBound.forEach((integer, rules) -> System.out.println(integer.toString() + " : " +rules));
    //System.out.println("bothBound");
    //bothBound.forEach((integer, rules) -> System.out.println(integer.toString() + " : " +rules));
  }


  /**
   * Used for deleting all 'e's at the start of an entity, as entities from the ruleset start with
   * an unnessecary 'e', when they are given from AnyBURL
   *
   * @param string
   * @return returns the given String without the e at the start
   */
  public static String delE(String string) {
    if (string.startsWith("e")) {
      try {
        string = string.substring(1, string.length());
      } catch (StringIndexOutOfBoundsException e) {
        System.out.println(e.getMessage());
        System.out.println(string);
        e.printStackTrace();
        System.exit(0);
      }
    }
    return string;
  }

  public static String delR(String string) {
    if (string.startsWith("r")) {
      try {
        string = string.substring(1, string.length());
      } catch (StringIndexOutOfBoundsException e) {
        System.out.println(e.getMessage());
        System.out.println(string);
        e.printStackTrace();
        System.exit(0);
      }
    }
    return string;
  }

  public StringBuffer searchByTriple(Triple triple) {

    long startTime = System.nanoTime();

    Integer key;
    Key3Int key3Int;
    Key2Int key2IntSub, key2IntObj;
    ArrayList<Rule> ruleSet;

    List<Rule> filteredRules = new ArrayList<>();
    //System.out.println("Triple: "+ triple);
    //Add all no bounds
      key = triple.getPredicate();
      //System.out.println("Key: "+ key);
      key2IntSub = new Key2Int(triple.getSubject(), triple.getPredicate());
      //System.out.println("Key2IntSub: " + key2IntSub);
      key3Int = new Key3Int(triple.getSubject(), triple.getPredicate(), triple.getObject());
      //System.out.println("Key3Int: " + key3Int);
      key2IntObj = new Key2Int(triple.getPredicate(), triple.getObject());
      //System.out.println("key2IntObj: " + key2IntObj + " : " + key2IntObj.hashCode());
    if(triple.getSubject() == triple.getObject()){
      ruleSet = noBoundEqual.get(key);
      if(ruleSet != null){
        //System.out.println("No Bound added");
        filteredRules.addAll(ruleSet);
      }
    }else{
      ruleSet = noBoundUnequal.get(key.hashCode());
      if(ruleSet != null){
        //System.out.println("No Bound added");
        filteredRules.addAll(ruleSet);
      }
    }
    ruleSet = bothBound.get(key3Int.hashCode());
    if(ruleSet != null){
      //System.out.println("Both Bound added");
      filteredRules.addAll(ruleSet);
    }

    ruleSet = subBound.get(key2IntSub.hashCode());
    if(ruleSet != null){
      //System.out.println("Sub Bound added");
      filteredRules.addAll(ruleSet);
    }
    ruleSet = objBound.get(key2IntObj);
    if(ruleSet != null){
      //System.out.println("Obj Bound added");
      filteredRules.addAll(ruleSet);
    }

    //filteredRules.forEach(rule1 -> System.out.println(rule1));
    //System.out.println("Rules Filtered");
    long elapsedTime = System.nanoTime();
    System.out.println("Zeit für Regelsuche: " + ((elapsedTime - startTime) / 1000000) + " ms");
    startTime = System.nanoTime();
    StringBuffer stringBuffer = new StringBuffer();
    if(Settings.TESTRULES_METHOD.equals("testRules")){
      stringBuffer = DBFuncs.testRules(filteredRules, triple);
    }else if (Settings.TESTRULES_METHOD.equals("testRulesUnionAll")){
      stringBuffer = DBFuncs.testRulesUnionAll(filteredRules, triple);
    }else if (Settings.TESTRULES_METHOD.equals("testRulesUnionAllShorterSelect")){
      stringBuffer = DBFuncs.testRulesUnionAllShorterSelect(filteredRules, triple);
    }else if (Settings.TESTRULES_METHOD.equals("testRulesUnionAllShorterSelectViewsForRelations")){
      stringBuffer = DBFuncs.testRulesUnionAllShorterSelectViewsForRelations(filteredRules, triple);
    }else if (Settings.TESTRULES_METHOD.equals("")){

    }else if (Settings.TESTRULES_METHOD.equals("")){

    }else if (Settings.TESTRULES_METHOD.equals("")){

    } else {
      System.out.println("Keine methode ausgewählt: "+ Settings.TESTRULES_METHOD);
    }
    elapsedTime = System.nanoTime();
    System.out.println("Zeit für SQL Abfrage: " + ((elapsedTime - startTime) / 1000000) + " ms");
    return stringBuffer;
    //filteredRules.forEach(rule -> System.out.println(rule));
  }

  public static void main(String[] args) {
    //Used for testing
    Key3Int key3Int1 = new Key3Int(-28, 223, 323);
    Key3Int key3Int2 = new Key3Int(-28, 223, 323);
    Key3Int key3Int3 = new Key3Int(-28, 223, 323);

  }

  public List<Triple> importQueryTriples() {
    String file = Settings.QUERYTRIPLES;
    InputStream is = DbFill.class.getResourceAsStream(file);
    InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(streamReader);
    List<Triple> tripleList = new ArrayList<>();
    String[] importList;
    int sub, pre, obj;

    try {

      for (String line; (line = reader.readLine()) != null; ) {
        importList = line.split("\\s+");
        if (importList.length == 3) {
          //System.out.println(Arrays.toString(importList));
          if (Settings.QUERYTRIPLESFORMAT.equals("TEXT")) {
            if (subjectIndex.get(importList[0]) == null){
              //System.out.println("Added " + importList[0]);
              sub = subjectIndex.size()+1;
              subjectIndex.put(importList[0], sub);
            }else {
              sub = subjectIndex.get(importList[0]);
            }
            if (predicateIndex.get(importList[1]) == null){
              //System.out.println("Added " + importList[1]);
              pre = predicateIndex.size()+1;
              predicateIndex.put(importList[1], sub);
            }else {
              pre = predicateIndex.get(importList[1]);
            }
            if (objectIndex.get(importList[2]) == null){
              //System.out.println("Added " + importList[2]);
              obj = objectIndex.size()+1;
              objectIndex.put(importList[2], sub);
            }else {
              obj = objectIndex.get(importList[2]);
            }
          } else {
            sub = Integer.parseInt(importList[0]);
            pre = Integer.parseInt(importList[1]);
            obj = Integer.parseInt(importList[2]);
          }
          tripleList.add(new Triple(sub, pre, obj));

        } else {
          System.out.println("Error while reading QueryTriples");
        }
      }
      //System.out.println("Import finished");
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tripleList;
  }


  /**
   * 1. Variante - alle Abfragen befinden sich
   * Used for getting the average time for a query to find a fitting rule. This will create random
   * rules out of all possible subjects, predicates and objects
   */
  public void startQuery() {
    List<Triple> queryTriples = importQueryTriples();
    StringBuffer found = new StringBuffer();
    //rules.forEach(rule -> System.out.println(rule));
    long queries = 0;
    long startTime = System.nanoTime();
    long elapsedTime;
    for (Triple triple : queryTriples){
      queries++;
      //found.append("Query: " + triple.toString() + " : " + searchByTriple(triple).toString() +" \n");
      System.out.println("Query: " + triple.toString() + " : " + searchByTriple(triple));
      searchByTriple(triple);
      //searchByTriple(triple);
      System.out.println("--------------------------------------------");
      if(queries % 10 == 0){
        elapsedTime = System.nanoTime();
        //System.out.println(found);
        System.out.println("");
        System.out.println("Gesamtzeit: " + ((elapsedTime - startTime) / 1000000) + " ms");
        System.out.println("Durchschnittszeit: " + (((elapsedTime - startTime) / 1000000) / queries) + " ms");
        System.out.println("Abfragen: " + queries);
        System.out.println("");
      }
    }
    elapsedTime = System.nanoTime();
    System.out.println(found);
    System.out.println("Gesamtzeit: " + ((elapsedTime - startTime) / 1000000) + " ms");
    System.out.println("Durchschnittszeit: " + (((elapsedTime - startTime) / 1000000) / queries) + " ms");
    System.out.println("Abfragen: " + queries);
  }

  /**
   * Zweite Variante wo alle Abfragen in einem SQL Statement / wenigen zusammengefasst werden
   */
  public void startQuery2() {
    List<Triple> queryTriples = importQueryTriples();
    //rules.forEach(rule -> System.out.println(rule));
    long queries = 0;
    long startTime = System.nanoTime();
    long elapsedTime;
    for (Triple triple : queryTriples){
      queries++;
      searchByTriple(triple);
    }
    elapsedTime = System.nanoTime();
    System.out.println("Gesamtzeit: " + ((elapsedTime - startTime) / 1000000) + " ms");
    System.out.println("Durchschnittszeit: " + (((elapsedTime - startTime) / 1000000) / queries) + " ms");
    System.out.println("Abfragen: " + queries);
  }
}
