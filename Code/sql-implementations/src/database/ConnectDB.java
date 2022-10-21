package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author tgutberl
 */
public class ConnectDB {
  private Connection connection = null;
  private String className, URL, user, password;

  /**
   * Constructor for connecting to the DB. Can be called by Main.
   * @param className
   * @param URL
   * @param user
   * @param password
   */
  public ConnectDB(String className, String URL, String user, String password){
    this.className = className;
    this.URL = URL;
    this.user = user;
    this.password = password;
    connect();
  }

  /**
   * Actual connection happens here. Assigns the connection to the connection variable
   */
  public void connect(){
    try {
      Class.forName(this.className);
      connection = DriverManager.getConnection(this.URL, this.user, this.password);
      if(connection != null){
        System.out.println("Connection OK");
        connection.setAutoCommit(false);
      }else {
        System.out.println("Connection Failed");
      }
    }catch (Exception e){
      System.out.println(e);
    }
  }

  /**
   * Retruns connection
   * @return connection
   */
  public Connection getConnection(){
    return this.connection;
  }

  /**
   * Executes a sql query, given here. Should be migrated to DBFuncs.
   * @param query
   */
  public void executeQuery(String query)
  {
    ResultSet resultSet = null;
    try
    {
      //executing query
      Statement stmt = connection.createStatement();
      resultSet = stmt.executeQuery(query);
      //Get Number of columns
      ResultSetMetaData metaData = resultSet.getMetaData();
      int columnsNumber = metaData.getColumnCount();
      //Printing the results
      while(resultSet.next())
      {
        for(int i = 1; i <= columnsNumber; i++)
        {
          System.out.printf("%-25s", (resultSet.getObject(i) != null)?resultSet.getObject(i).toString(): null );
        }
      }
    }
    catch (SQLException ex)
    {
      System.out.println("Exception while executing statement. Terminating program... " + ex.getMessage());
    }
    catch (Exception ex)
    {
      System.out.println("General exception while executing query. Terminating the program..." + ex.getMessage());
    }
  }

  /**
   * Closes the connection to the POSTGRESQL Database.
   * Should always be done at end of program!
   */
  public void closeConnection(){
    try {
      this.connection.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
