package models;

import java.util.HashMap;

/**
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

  public static String getChar(int i){
    int count = -1;
    HashMap<Integer, String> map = new HashMap<>();
    for (char ch = 'A'; ch <= 'Z'; ++ch)
      map.put(count--, String.valueOf(ch));
    return map.get(i);
  }
}
