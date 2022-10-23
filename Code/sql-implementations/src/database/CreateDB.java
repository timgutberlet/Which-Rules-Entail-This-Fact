package database;

import java.sql.Connection;

/**
 * @author tgutberl
 */
public class CreateDB {
  static Connection connection;
  public static void setConnectDB(Connection con){
    connection = con;
  }

  /**
   * Create the knowledgegraph Table including all Columns.
   * Only if it not already exists.
   */
  public void createKnowledgeGraphDB(){

  }

  /**
   * Create the HeadBaseDB, used for the second case, where
   * all queries are in a single sql statement.
   * Only if it not already exists.
   */
  public void createHeadBaseDB(){

  }

}
