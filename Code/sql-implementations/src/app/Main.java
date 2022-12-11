package app;

import config.Config;
import database.ConnectDB;
import database.CreateDB;
import database.DBFuncs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import utils.DbFill;
import utils.RandomRules;

/**
 * @author tgutberl
 */
public class Main {
  private static ConnectDB connectDB = new ConnectDB(Config.getStringValue("CLASSNAME"), Config.getStringValue("URL"), Config.getStringValue("USER"), Config.getStringValue("PASSWORD"));

  public static void main(String[] args) {
    initalize();
    DbFill dbFill = new DbFill();
    ArrayList<Integer> arrayList = new ArrayList<>();
    /*arrayList.add(1);
    arrayList.add(2);
    arrayList.add(3);
    DBFuncs.viewsForPredicate(arrayList);*/
    if(Config.getStringValue("REFILL_TABLES").equals("YES")){
      dbFill.fillVocabulary();
      dbFill.fillKnowledgegraph();
      System.out.println("Knowledgegraph filled");
    }else {
      dbFill.setIndexes();
      System.out.println("Knowledgegraph already filled, set Indexes");
    }
    RandomRules randomRules = new RandomRules(dbFill.getSubjectIndex(), dbFill.getPredicateIndex(), dbFill.getObjectIndex());
    System.out.println("RandomRules Set");
    if(Config.getStringValue("TESTRULES_METHOD").equals("optimizedQuantileAnalysis") && Config.getStringValue("REFILL_TABLES").equals("YES")){
      randomRules.learnQuery(dbFill.getSampleTriples());
    } else if(Config.getStringValue("TESTRULES_METHOD").equals("optimizedQuantileAnalysis") && Config.getStringValue("REFILL_TABLES").equals("NO")){
      randomRules.learnRules();
    }
    if(!Config.getStringValue("NATIVE").equals("YES")){
      randomRules.startQuery();
    }else {
      System.out.println("NATIVE!!!");
      randomRules.startQueryNative();
    }

    //CreateDB.createKnowledgeGraphDB();
    // DBFuncs.readAllData();
    //Code here
    connectDB.closeConnection();
    //connectDB.closeConnection();
  }

  public static void initalize(){
    Config.loadProperty();
    //Config.loadStandardConfig();
    //Config.saveProperty();
    DBFuncs.setCon(connectDB.getConnection());
    CreateDB.setConnectDB(connectDB.getConnection());
  }

}
