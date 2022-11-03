package utils;

import config.Settings;
import database.DBFuncs;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import models.Rule;
import models.Triple;
import models.Variables;

/**
 * @author timgutberlet
 */
public class RandomRules {

  private Integer queryCount;
  private List<Rule> rules;

  public RandomRules(Integer queryCount) {
    this.queryCount = queryCount;
    this.rules = importRules();
  }

  public List<Rule> getRules() {
    return rules;
  }

  public void searchByTriple(Triple triple){
    List<Rule> filteredRules;
    if (triple.getObject().equals(triple.getSubject())){
      filteredRules = this.rules
          .stream()
          .filter(rule -> rule.getHead().getPredicate().equals(triple.getPredicate()))
          .filter(rule -> rule.getHead().getObject().equals(rule.getHead().getSubject()))
          .filter(rule -> rule.getHead().getObject().equals(triple.getObject()) && rule.getHead().getSubject().equals(triple.getSubject())
          || rule.getHead().getSubject() < 0 && rule.getHead().getObject().equals(triple.getObject())
          || rule.getHead().getSubject().equals(triple.getSubject()) && rule.getHead().getObject() < 0
          || rule.getHead().getSubject() < 0 && rule.getHead().getObject() <0)
          .collect(Collectors.toList());
    }else {
      filteredRules = this.rules
          .stream()
          .filter(rule -> rule.getHead().getPredicate().equals(triple.getPredicate()))
          .filter(rule -> !rule.getHead().getObject().equals(rule.getHead().getSubject()))
          .filter(rule -> rule.getHead().getObject().equals(triple.getObject()) && rule.getHead().getSubject().equals(triple.getSubject())
          || rule.getHead().getSubject() < 0 && rule.getHead().getObject().equals(triple.getObject())
          || rule.getHead().getSubject().equals(triple.getSubject()) && rule.getHead().getObject() < 0
          || rule.getHead().getSubject() < 0 && rule.getHead().getObject() <0)
          .collect(Collectors.toList());
    }

    DBFuncs.testRules(filteredRules, triple);
    //filteredRules.forEach(rule -> System.out.println(rule));
  }

  public static List<Rule> importRules(){
    String file = Settings.RULES_PATH;
    InputStream is = DbFill.class.getResourceAsStream(file);
    InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(streamReader);
    List<Rule> ruleList = new ArrayList<>();
    try {
      String first;
      String second = "";
      String[] help2, help1, help3;
      String headPredicate, headSubject, headObject;
      String bodyPredicate, bodySubject, bodyObject;
      int subjectID, objectID, predicateID;
      Triple head;
      List<Triple> body = new ArrayList<>();
      Triple tripleHelp;
      int ID = 0;
      for (String line; (line = reader.readLine()) != null; ) {
        body = new ArrayList<>();
        ID++;
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
            if (bodySubject.length() == 1){
              subjectID = Variables.getID(bodySubject);
            }else {
              subjectID = DBFuncs.getSubjectID(delE(bodySubject));
            }
            if(bodyObject.length() == 1){
              objectID = Variables.getID(bodyObject);
            }else {
              objectID = DBFuncs.getObjectID(delE(bodyObject));
            }
            predicateID = DBFuncs.getPredicateID(delR(bodyPredicate));
            tripleHelp = new Triple(subjectID, predicateID,
                objectID);
            body.add(tripleHelp);
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
        if (headSubject.length() == 1){

        }
        if (headSubject.length() == 1){
          subjectID = Variables.getID(headSubject);
        }else {
          subjectID = DBFuncs.getSubjectID(delE(headSubject));
        }
        if(headObject.length() == 1){
          objectID = Variables.getID(headObject);
        }else {
          objectID = DBFuncs.getObjectID(delE(headObject));
        }
        predicateID = DBFuncs.getPredicateID(delR(headPredicate));
        head = new Triple(subjectID, predicateID, objectID);
        ruleList.add(new Rule(head, body));
      }
      System.out.println("Import finished");

      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return ruleList;
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

  /**
   * Used for getting the average time for a query to find a fitting rule. This will create random
   * rules out of all possible subjects, predicates and objects
   */
  public void startQuery() {
    long queries = 0;
    long startTime = System.nanoTime();
    long elapsedTime;
    int random1, random2, random3;
    Map<Integer, Triple> map = DBFuncs.getKnowledgeGraph();
    //Comment out when Integer IDs were implemented
    /*while (queries < queryCount){
      random1 = (int) (Math.random() * map.size());
      random2 = (int) (Math.random() * map.size());
      random3 = (int) (Math.random() * map.size());
      Hashtable<String, ArrayList<String>> field = DBFuncs.getByTripel(map.get(random1).getSubject(), map.get(random2).getObject(), map.get(random3).getPredicate());
      if (field.size() > 0 ){
        System.out.println("V1: "+ map.get(random1).getSubject() + " ; V2: "+ map.get(random2).getObject() + " ; Relaton: "+ map.get(random3).getPredicate());
        System.out.println(field);
      }
      queries++;
    }*/
    elapsedTime = System.nanoTime();
    System.out.println("Gesamtzeit: " + ((elapsedTime - startTime) / 1000000) + " ms");
    System.out.println("Durchschnittszeit: " + (((elapsedTime - startTime) / 1000000) / queries));
    System.out.println("Abfragen: " + queries);
  }
}
