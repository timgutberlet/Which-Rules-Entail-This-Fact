package utils; /**
 * @author timgutberlet
 */

import config.Settings;
import database.DBFuncs;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import models.Triple;

public class DbFill {
  private HashMap<String, Integer> subjectIndex;
  private HashMap<String, Integer> predicateIndex;
  private HashMap<String, Integer> objectIndex;

  public HashMap<String, Integer> getObjectIndex() {
    return objectIndex;
  }

  public HashMap<String, Integer> getPredicateIndex() {
    return predicateIndex;
  }

  public HashMap<String, Integer> getSubjectIndex() {
    return subjectIndex;
  }

  /**
   * Fills the KnowledgeGraph Table with all rows given in the knowledgegraph text file
   */
  public void fillKnowledgegraph(){
    String file = Settings.KNOWLEDGEGRAPH;
    //DBFuncs.deleteKG();
    BufferedReader reader;
    try {
      // java.io.InputStream
      InputStream is = DbFill.class.getResourceAsStream(file);
      InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
      reader = new BufferedReader(streamReader);
      String[] triple;
      List<Triple> kgList = new ArrayList<>();
      subjectIndex = new HashMap<>();
      predicateIndex = new HashMap<>();
      objectIndex = new HashMap<>();
      String subject, predicate, object;
      Integer subC = 0, predC = 0, objC = 0;
      Integer subH, predH, objH;
      Integer count = 0;
      for (String line; (line = reader.readLine()) != null;) {
        // Process line
        //System.out.println(line);
        triple = line.split("\\s");
        System.out.println(count++ + " - triple: " + triple[0] + ", " + triple[1] +", "+ triple[2]);
        subject = triple[0];
        predicate = triple[1];
        object = triple[2];
        //Check for subject
        if (subjectIndex.containsKey(subject)){
          subH = subjectIndex.get(subject);
        }else {
          subH = subC;
          subjectIndex.put(subject, subC++);
        }
        //Check for predicte
        if (predicateIndex.containsKey(predicate)){
          predH = predicateIndex.get(predicate);
        }else {
          predH = predC;
          predicateIndex.put(predicate, predC++);
        }
        //Check for object
        if (objectIndex.containsKey(object)){
          objH = objectIndex.get(object);
        }else {
          objH = objC;
          objectIndex.put(object, objC++);
        }
        kgList.add(new Triple(subH, predH, objH));
      }
      DBFuncs.deleteKG();
      DBFuncs.deleteIndizes();
      DBFuncs.insertSubjects(subjectIndex);
      DBFuncs.insertPredicates(predicateIndex);
      DBFuncs.insertObjects(objectIndex);
      DBFuncs.insertKnowledgegraph(kgList);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setIndexes(){
    this.subjectIndex = getSubjectIndex();
    this.objectIndex = getObjectIndex();
    this.predicateIndex = getPredicateIndex();
  };

  /**
   * Used for deleting all 'e's at the start of an entity, as entities from the ruleset start with an
   * unnessecary 'e', when they are given from AnyBURL
   * @param string
   * @return returns the given String without the e at the start
   */
  public static String delE(String string){
    if (string.startsWith("e")){
      try {
        string = string.substring(1, string.length());
      }catch(StringIndexOutOfBoundsException e){
        System.out.println(e.getMessage());
        System.out.println(string);
        e.printStackTrace();
        System.exit(0);
      }
    }
    return string;
  }
  /**
   * Used for deleting all 'r's at the start of an entity, as entities from the ruleset start with an
   * unnessecary 'r', when they are given from AnyBURL
   * @param string
   * @return returns the given String without the r at the start
   */
  public static String delR(String string){
    if (string.startsWith("r")){
      try {
        string = string.substring(1, string.length());
      }catch(StringIndexOutOfBoundsException e){
        System.out.println(e.getMessage());
        System.out.println(string);
        e.printStackTrace();
        System.exit(0);
      }
    }
    return string;
  }

  /**
   * This Method is used for filling the Database tables head and base,
   * given from the rules provided by AnyBURL
   */
/**
  public static void fillHeadAndBase() {
    String file = Settings.RULES_PATH;
    DBFuncs.deleteHead();
    DBFuncs.deleteTail();
    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(
          file));
      String line = reader.readLine();
      String first;
      String second = "";
      String [] help2;
      String [] help1;
      String [] help3;
      String headRelation, headV1, headV2;
      String tailRelation, tailV1, tailV2;
      int ID = 0;
      List<Triple> headList = new ArrayList<>();
      List<Triple> tailList = new ArrayList<>();
      while (line != null) {
        ID++;
        headV1 = "";
        headV2 = "";
        tailV1 = "";
        tailV2 = "";
        tailRelation = "";
        second = "";
        // Get the left characters that are important
        help1 = line.split("<=");
        first = help1[0];
        help2 = first.split("\\s+");
        first  = help2[3];
        headRelation = first.split("\\(",2)[0];
        if (help1.length > 1){
          second = help1[1].strip();
          help3 = second.split(",\\s");
          for (String triple : help3){
            System.out.println(triple + " ID: "+  ID);
            tailRelation= triple.split("\\(",2)[0];
            help1 = triple.split("\\(", 2)[1].split(",", 2);
            tailV1 = help1[0];
            tailV2 = help1[1].substring(0, help1[1].length()-1);
            tailList.add(new Triple(Integer.parseInt(tailV1), Integer.parseInt(delE(tailV2)), Integer.parseInt(tailRelation)));
          }
        }
        //Uncomment this, if you also want to inlcude empty rules
        /*else {
          tailList.add(new Triple(ID, tailV1, tailV2, tailRelation));
        }
        help1 = first.split("\\(", 2)[1].split(",", 2);
        if (help1.length > 1){
          headV1 = help1[0];
          headV2 = help1[1].substring(0, help1[1].length()-1);
        }
        //System.out.println(headRelation + " : " + headV1 + " " + headV2 + " <= " + second);
        headList.add(new Triple(Integer.parseInt(tailV1), Integer.parseInt(delE(tailV2)), Integer.parseInt(tailRelation)));
        // read next line
        line = reader.readLine();
      }
      DBFuncs.insertHead(headList);
      DBFuncs.insertTail(tailList);
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }**/
}
