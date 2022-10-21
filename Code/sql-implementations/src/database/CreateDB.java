package database;

/**
 * @author tgutberl
 */
public class CreateDB {
  ConnectDB connectDB;
  public CreateDB(ConnectDB connectDB){
    this.connectDB = connectDB;
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
