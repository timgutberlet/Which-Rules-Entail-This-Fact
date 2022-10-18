import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Properties;

/**
 * @author tgutberl
 */
public class DBConnection {
  public static Connection connect(){
    Connection con = null;
    try {
      Class.forName("org.sqlite.JDBC");
      Properties props = new Properties();
      props.setProperty("rewriteBatchedStatements", "true");
      con = DriverManager.getConnection("jdbc:sqlite:research.db", props);
      //con = DriverManager.getConnection("jdbc:sqlite:research.db");
      System.out.println("Connected");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return con;
  };


}
