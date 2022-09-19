import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * @author tgutberl
 */
public class DBConnection {
  public static Connection connect(){
    Connection con = null;
    try {
      Class.forName("org.sqlite.JDBC");
      con = DriverManager.getConnection("jdbc:sqlite:research.db");
      System.out.println("Connected");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return con;
  };


}
