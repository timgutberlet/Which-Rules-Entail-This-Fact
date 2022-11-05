package config;

/**
 * @author tgutberl
 */
public class Settings {
  public static String CLASSNAME = "org.postgresql.Driver";
  public static String URL = "jdbc:postgresql://localhost:5432/postgres";
  public static String USER = "postgres";
  public static String PASSWORD = "root";
  public static String KNOWLEDGEGRAPH = "/YAGO3-10/test";
  public static String RULES_PATH = "rules.txt";
  public static String QUERYTRIPLES = "/DATA/comparison/queryTriples/queryTriples";
  public static String QUERYTRIPLESFORMAT = "TEXT"; //Could also be INDEX
  public static String KNOWLEDGEGRAPH_TABLE = "indexed_knowledgegraph_unique"; //Could also be INDEX
  public static String REFILL_TABLES = "YES";
  public static String VOCABULARY_DATASET = "/Data/Vocabulary/YAGO-10.txt";
}
