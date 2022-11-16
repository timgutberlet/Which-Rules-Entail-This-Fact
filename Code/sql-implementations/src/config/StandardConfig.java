package config;

/**
 * Class to Store default properties and make them accessible.
 *
 * @author tgutberl
 */
public class StandardConfig {

  /**
   * 2D Field, used for storing standard/default values for the config file. Will be used if the
   * Config File is not used or outdated.
   */
  public static final String[][] standardConfig = new String[][]{
      {"VERSION", "2"},
      {"CLASSNAME", "org.postgresql.Driver"},
      {"URL", "jdbc:postgresql://localhost:5432/postgres"},
      {"USER", "postgres"},
      {"PASSWORD", "root"},
      {"KNOWLEDGEGRAPH", "./files/Comparison/knowledgegraph/train.txt"},
      {"RULES_PATH", "./files/Comparison/rules/alpha-50"},
      {"QUERYTRIPLES", "./files/Comparison/queryTriples/valid.txt"},
      {"QUERYTRIPLESFORMAT", "TEXT"},
      {"KNOWLEDGEGRAPH_TABLE", "iku"},
      {"REFILL_TABLES", "YES"},
      {"VOCABULARY_DATASET", "./files/Vocabulary/YAGO3-10.txt"},
      {"TESTRULES_METHOD", "testRulesUnionAllShorterSelectViewsForRelations"},
      {"FILTER_SIMPLE_RULES", "YES"},
  };

  /**
   * Constructor.
   */
  private StandardConfig() {

  }

}
