package app;

import config.IOHelper;
import database.ConnectDB;
import config.Settings;
import database.CreateDB;
import database.DBFuncs;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;
import utils.DbFill;

/**
 * @author tgutberl
 */
public class Main {
  private static String CONFIG_FILE = "config.properties";

  public static void main(String[] args) {
    initalize();
    ConnectDB connectDB = new ConnectDB(Settings.CLASSNAME, Settings.URL, Settings.USER, Settings.PASSWORD);
    DBFuncs.setCon(connectDB.getConnection());
    CreateDB.setConnectDB(connectDB.getConnection());
    DbFill.fillKnowledgegraph();
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
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
