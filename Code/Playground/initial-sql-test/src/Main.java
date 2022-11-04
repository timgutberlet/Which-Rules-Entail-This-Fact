import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * @author tgutberl
 */
public class Main {
  public static void main(String[] args) {
    //Hashtable<String, ArrayList<String>> field = DBFuncs.getByTripel("a", "a", "r");
    randomRules(50);
    //System.out.println(field.toString());
  }
  public static void test(int count){
    long startTime = System.nanoTime();
    long elapsedTime;
    Hashtable<String, ArrayList<String>> field = DBFuncs.getByTripel("Thomas_Kleine", "Germany_national_football_B_team", "playsFor");
    //Hashtable<String, ArrayList<String>> field = DBFuncs.getByTripel("aa", "cc", "rr");
    System.out.println(field.toString());
    elapsedTime = System.nanoTime();
    System.out.println("Time: " + ((elapsedTime - startTime)/1000000) + " ms");
  }

  public static void randomRules(int count){
    int i = 0;
    long abfragen = 0;
    long startTime = System.nanoTime();
    long elapsedTime;
    int random1, random2, random3;
    Map<Integer, Triple> map = DBFuncs.getKnowledgeGraph();
    while (i <= count){
      random1 = (int) (Math.random() * map.size());
      random2 = (int) (Math.random() * map.size());
      random3 = (int) (Math.random() * map.size());
      Hashtable<String, ArrayList<String>> field = DBFuncs.getByTripel(map.get(random1).getV1(), map.get(random2).getV2(), map.get(random3).getRelation());
      if (field.size() > 0 ){
        System.out.println("V1: "+ map.get(random1).getV1() + " ; V2: "+ map.get(random2).getV2() + " ; Relaton: "+ map.get(random3).getRelation());
        System.out.println(field);
        i++;
      }
      abfragen++;
    }
    elapsedTime = System.nanoTime();
    System.out.println("Gesamtzeit: " + ((elapsedTime - startTime)/1000000) + " ms");
    System.out.println("Durchschnittszeit: " + (((elapsedTime - startTime)/1000000)/abfragen));
    System.out.println("Abfragen: "+ abfragen);
  }
}
