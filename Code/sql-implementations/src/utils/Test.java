package utils;

import java.util.ArrayList;
import java.util.HashMap;
import models.Key2Int;
import models.Key3Int;
import models.Rule;
import models.Triple;

/**
 * @author tgutberl
 */
public class Test {
  private static HashMap<Key2Int, ArrayList<Rule>> subBound= new HashMap<>();
  private static HashMap<Key2Int, ArrayList<Rule>> objBound= new HashMap<>();
  private static HashMap<Key2Int, ArrayList<Rule>> bothBound= new HashMap<>();
  private static HashMap<Integer, ArrayList<Rule>> noBoundUnequal = new HashMap<>();
  private static HashMap<Integer, ArrayList<Rule>> noBoundEqual = new HashMap<>();
  public static void main(String[] args) {
    Key2Int key2Int1 = new Key2Int(4, 16325);
    Key2Int key2Int2 = new Key2Int(4, 16325);
    Key2Int key2Int3 = new Key2Int(4, 16325);
    //Rule rule = new Rule(new Triple(-24, 4, 16325), new ArrayList<Triple>());

    if(!bothBound.containsKey(key2Int1)){
      bothBound.put(key2Int1, new ArrayList<>());
    }
      //bothBound.get(key2Int1).add(rule);


    if(!bothBound.containsKey(key2Int2)){
      bothBound.put(key2Int2, new ArrayList<>());
    }
    //bothBound.get(key2Int2).add(rule);
    System.out.println(bothBound.get(key2Int3));
  }
}
