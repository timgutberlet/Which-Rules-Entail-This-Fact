package models;

import java.util.HashMap;

/**
 * Vocabulary for a variable representation (A...Z) converts to (-1 .. -26)
 *
 * @author tgutberl
 */
public class Variables {
  public static int getID(String c) {
    int count = -1;
    HashMap<String, Integer> map = new HashMap<>();
    for (char ch = 'A'; ch <= 'Z'; ++ch)
      map.put(String.valueOf(ch), count--);
    return map.get(c);
  }

  /**
   * Returns the char for a given ID
   * @param i ID
   * @return Returns the String
   */
  public static String getChar(int i){
    int count = -1;
    HashMap<Integer, String> map = new HashMap<>();
    for (char ch = 'A'; ch <= 'Z'; ++ch)
      map.put(count--, String.valueOf(ch));
    return map.get(i);
  }
}
