package utils; /**
 * @author timgutberlet
 */

import config.Config;
import database.DBFuncs;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import models.Triple;

public class DbFill {
  private HashMap<String, Integer> subjectIndex =  new HashMap<>();
  private HashMap<String, Integer> predicateIndex =  new HashMap<>();
  private HashMap<String, Integer> objectIndex =  new HashMap<>();

  private ArrayList<Triple> sampleTriples = new ArrayList<>();

  public HashMap<String, Integer> getObjectIndex() {
    return objectIndex;
  }

  public HashMap<String, Integer> getPredicateIndex() {
    return predicateIndex;
  }

  public HashMap<String, Integer> getSubjectIndex() {
    return subjectIndex;
  }



  public ArrayList<Triple> getSampleTriples() {
    this.setSampleTriples();
    return sampleTriples;
  }

  /**
   * Fills the KnowledgeGraph Table with all rows given in the knowledgegraph text file
   */
  public void fillKnowledgegraph() {
    String file = Config.getStringValue("KNOWLEDGEGRAPH");
    //DBFuncs.deleteKG();
    BufferedReader reader;
    Triple t;
    try {
      // java.io.InputStream
      reader = new BufferedReader(new FileReader(file));
      String[] triple;
      List<Triple> kgList = new ArrayList<>();
      String subject, predicate, object;
      Integer subH, predH, objH;
      int helpCount = 0;
      for (String line; (line = reader.readLine()) != null; ) {
        triple = line.split("\\s");
        subject = triple[0];
        predicate = triple[1];
        object = triple[2];
        subH = subjectIndex.get(subject);
        predH = predicateIndex.get(predicate);
        objH = objectIndex.get(object);
        t = new Triple(subH, predH, objH, subject, predicate, object);
        kgList.add(t);
      }
      DBFuncs.deleteKG();
      DBFuncs.insertKnowledgegraph(kgList);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void setSampleTriples() {
    String file = Config.getStringValue("QUANTIL_LEARN_TRIPLES");
    //DBFuncs.deleteKG();
    BufferedReader reader;
    Triple t;
    try {
      // java.io.InputStream
      reader = new BufferedReader(new FileReader(file));
      String[] triple;
      String subject, predicate, object;
      Integer subH, predH, objH;
      int count = 1;
      for (String line; (line = reader.readLine()) != null; ) {
        System.out.println(count++);
        triple = line.split("\\s");
        subject = triple[0];
        predicate = triple[1];
        object = triple[2];
        subH = subjectIndex.get(subject);
        predH = predicateIndex.get(predicate);
        objH = objectIndex.get(object);
        t = new Triple(subH, predH, objH, subject, predicate, object);
        System.out.println(t);
        sampleTriples.add(t);
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Fills the Index Database files
   */
  public void fillVocabulary() {
    String kg = Config.getStringValue("KNOWLEDGEGRAPH");
    BufferedReader reader;
    try {
      // java.io.InputStream
      reader = new BufferedReader(new FileReader(kg));
      String[] triple;
      String subject, predicate, object;
      Integer subC = 0, predC = 0, objC = 0;
      for (String line; (line = reader.readLine()) != null; ) {
        triple = line.split("\\s");
        subject = triple[0];
        predicate = triple[1];
        object = triple[2];
        if (!subjectIndex.containsKey(subject)) {
          subjectIndex.put(subject, subC++);
        }
        if (!predicateIndex.containsKey(predicate)) {
          predicateIndex.put(predicate, predC++);
        }
        if (!objectIndex.containsKey(object)) {
          objectIndex.put(object, objC++);
        }
      }
    String query = Config.getStringValue("QUERYTRIPLES");
      // java.io.InputStream
      reader = new BufferedReader(new FileReader(query));
      for (String line; (line = reader.readLine()) != null; ) {
        triple = line.split("\\s");
        subject = triple[0];
        predicate = triple[1];
        object = triple[2];
        if (!subjectIndex.containsKey(subject)) {
          subjectIndex.put(subject, subC++);
        }
        if (!predicateIndex.containsKey(predicate)) {
          predicateIndex.put(predicate, predC++);
        }
        if (!objectIndex.containsKey(object)) {
          objectIndex.put(object, objC++);
        }
      }
    String valid = Config.getStringValue("QUANTIL_LEARN_TRIPLES");
      reader = new BufferedReader(new FileReader(valid));
      for (String line; (line = reader.readLine()) != null; ) {
        triple = line.split("\\s");
        subject = triple[0];
        predicate = triple[1];
        object = triple[2];
        if (!subjectIndex.containsKey(subject)) {
          subjectIndex.put(subject, subC++);
        }
        if (!predicateIndex.containsKey(predicate)) {
          predicateIndex.put(predicate, predC++);
        }
        if (!objectIndex.containsKey(object)) {
          objectIndex.put(object, objC++);
        }
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<Integer> predicateList = predicateIndex.values().stream().toList();
    switch (Config.getStringValue("PREDICATE_VIEW")) {
      case "viewsForPredicate":
        DBFuncs.viewsForPredicate(predicateList);
        break;
      case "viewsForPredicateNoIndex":
        DBFuncs.viewsForPredicateNoIndex(predicateList);
        break;
      case "viewsForPredicateSubObj":
        DBFuncs.viewsForPredicateSubObj(predicateList);
        break;
      case "viewsForPredicateObjSub":
        DBFuncs.viewsForPredicateObjSub(predicateList);
        break;
      case "viewsForPredicateHashIndex":
        DBFuncs.viewsForPredicateHashIndex(predicateList);
        break;
      default:
    }
    fillIndizes();
    System.out.println("Vocabulary filled");
  }
  public void fillIndizes() {
    DBFuncs.deleteIndizes();
    DBFuncs.setSubjectIndex(subjectIndex);
    DBFuncs.setPredicateIndex(predicateIndex);
    DBFuncs.setObjectIndex(objectIndex);
  }

  public void setIndexes() {
    this.subjectIndex = DBFuncs.getSubjectIndex();
    this.objectIndex = DBFuncs.getObjectIndex();
    this.predicateIndex = DBFuncs.getPredicateIndex();
  }
}