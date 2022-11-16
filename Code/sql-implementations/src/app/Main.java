package app;

import config.IOHelper;
import database.ConnectDB;
import config.Settings;
import database.CreateDB;
import database.DBFuncs;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;
import utils.DbFill;
import utils.RandomRules;

/**
 * @author tgutberl
 */
public class Main {
  private static String CONFIG_FILE = "config.properties";
  private static ConnectDB connectDB = new ConnectDB(Settings.CLASSNAME, Settings.URL, Settings.USER, Settings.PASSWORD);

  public static void main(String[] args) {
    initalize();
    DbFill dbFill = new DbFill();
    ArrayList<Integer> arrayList = new ArrayList<>();
    /*arrayList.add(1);
    arrayList.add(2);
    arrayList.add(3);
    DBFuncs.viewsForPredicate(arrayList);*/
    if(Settings.REFILL_TABLES.equals("YES")){
      dbFill.fillVocabulary();
      dbFill.fillKnowledgegraph();
      System.out.println("Knowledgegraph filled");
    }else {
      dbFill.setIndexes();
      System.out.println("Knowledgegraph already filled, set Indexes");
    }
    RandomRules randomRules = new RandomRules(dbFill.getSubjectIndex(), dbFill.getPredicateIndex(), dbFill.getObjectIndex());
    System.out.println("RandomRules Set");
    randomRules.startQuery();

    //CreateDB.createKnowledgeGraphDB();
    // DBFuncs.readAllData();
    //Code here
    connectDB.closeConnection();
    //connectDB.closeConnection();
  }

  public static void initalize(){
    InputStream input;
    Properties prop = new Properties();
    try {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      input = classLoader.getResourceAsStream(CONFIG_FILE);
      try {
        prop.load(input);
      }catch (NullPointerException e){
        e.printStackTrace();
        System.out.println("Config file does not exist");
      }
      Settings.CLASSNAME = IOHelper.getProperty(prop, "CLASSNAME", Settings.CLASSNAME);
      Settings.PASSWORD = IOHelper.getProperty(prop, "PASSWORD", Settings.PASSWORD);
      Settings.URL = IOHelper.getProperty(prop, "URL", Settings.URL);
      Settings.USER = IOHelper.getProperty(prop, "USER", Settings.USER);
      Settings.KNOWLEDGEGRAPH = IOHelper.getProperty(prop, "KNOWLEDGEGRAPH", Settings.KNOWLEDGEGRAPH);
      Settings.RULES_PATH = IOHelper.getProperty(prop, "RULES_PATH", Settings.RULES_PATH);
      Settings.QUERYTRIPLES = IOHelper.getProperty(prop, "QUERYTRIPLES", Settings.QUERYTRIPLES);
      Settings.QUERYTRIPLESFORMAT = IOHelper.getProperty(prop, "QUERYTRIPLESFORMAT", Settings.QUERYTRIPLESFORMAT);
      Settings.KNOWLEDGEGRAPH_TABLE = IOHelper.getProperty(prop, "KNOWLEDGEGRAPH_TABLE", Settings.KNOWLEDGEGRAPH_TABLE);
      Settings.REFILL_TABLES = IOHelper.getProperty(prop, "REFILL_TABLES", Settings.REFILL_TABLES);
      Settings.RULES_IN_DB_MODE = IOHelper.getProperty(prop, "RULES_IN_DB_MODE", Settings.RULES_IN_DB_MODE);
      Settings.VOCABULARY_DATASET = IOHelper.getProperty(prop, "VOCABULARY_DATASET", Settings.VOCABULARY_DATASET);
      Settings.TESTRULES_METHOD = IOHelper.getProperty(prop, "TESTRULES_METHOD", Settings.TESTRULES_METHOD);
      Settings.FILTER_SIMPLE_RULES = IOHelper.getProperty(prop, "FILTER_SIMPLE_RULES ", Settings.FILTER_SIMPLE_RULES);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    DBFuncs.setCon(connectDB.getConnection());
    CreateDB.setConnectDB(connectDB.getConnection());
  }

}
