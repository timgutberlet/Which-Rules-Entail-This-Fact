import java.util.ArrayList;
import java.util.Hashtable;

/**
 * @author tgutberl
 */
public class Main {
  public static void main(String[] args) {
    //Hashtable<String, ArrayList<String>> field = DBFuncs.getByTripel("a", "a", "r");
    randomRules(5);
    //System.out.println(field.toString());
  }
  public static void randomRules(int count){
    long startTime = System.nanoTime();
    long elapsedTime;
    Hashtable<String, ArrayList<String>> field = DBFuncs.getByTripel("Thomas_Kleine", "Germany_national_football_B_team", "playsFor");
    //Hashtable<String, ArrayList<String>> field = DBFuncs.getByTripel("aa", "cc", "rr");
    System.out.println(field.toString());
    elapsedTime = System.nanoTime();
    System.out.println("Time: " + ((elapsedTime - startTime)/1000000) + " ms");
  }
}
