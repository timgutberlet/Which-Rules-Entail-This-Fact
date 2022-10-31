package utils;

import database.DBFuncs;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import models.Triple;

/**
 * @author timgutberlet
 */
public class RandomRules {
  private Integer queryCount;
  public RandomRules(Integer queryCount){
    this.queryCount = queryCount;
  }

  /**
   * Used for getting the average time for a query to find a fitting rule.
   * This will create random rules out of all possible subjects, predicates and objects
   */
  public void startQuery(){
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
    System.out.println("Gesamtzeit: " + ((elapsedTime - startTime)/1000000) + " ms");
    System.out.println("Durchschnittszeit: " + (((elapsedTime - startTime)/1000000)/queries));
    System.out.println("Abfragen: "+ queries);
  }
}
