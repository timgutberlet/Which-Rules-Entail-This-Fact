/**
 * @author tgutberl
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TimeTracking {

  public static void main(String[] args) {
    readKnowledgegraph("../Code/Data/YAGO3-10/test.txt");
    fillDB("rules.txt");
  }

  public static BufferedReader readKnowledgegraph(String file){
    DBFuncs.deleteKG();
    BufferedReader reader;
    try {
      reader = new BufferedReader((new FileReader(file)));
      String line = reader.readLine();
      String[] triple;
      List<Triple> kgList = new ArrayList<>();
      while (line != null){
        //System.out.println(line);
        triple = line.split("\\s");
        System.out.println("triple: " + triple[0] + ", " + triple[2] +", "+ triple[1]);
        kgList.add(new Triple(triple[0], triple[2], triple[1]));
        line = reader.readLine();
      }
      /*for (Triple t : kgList){
        System.out.println(t.toString());
      }*/
      DBFuncs.insertKnowledgegraph(kgList);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

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

  public static void fillDB(String file) {
    DBFuncs.deleteHead();
    DBFuncs.deleteTail();
    BufferedReader reader;
    String sql = "";
    Boolean erstes = true;
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
            tailList.add(new Triple(ID, delE(tailV1), delE(tailV2), delR(tailRelation)));
          }
        }
        //Uncomment this, if you also want to inlcude empty rules
        /*else {
          tailList.add(new Triple(ID, tailV1, tailV2, tailRelation));
        }*/
        help1 = first.split("\\(", 2)[1].split(",", 2);
        if (help1.length > 1){
          headV1 = help1[0];
          headV2 = help1[1].substring(0, help1[1].length()-1);
        }
        //System.out.println(headRelation + " : " + headV1 + " " + headV2 + " <= " + second);
        headList.add(new Triple(ID, delE(headV1), delE(headV2), delR(headRelation)));
        // read next line
        line = reader.readLine();
      }
      DBFuncs.insertHead(headList);
      DBFuncs.insertTail(tailList);
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
