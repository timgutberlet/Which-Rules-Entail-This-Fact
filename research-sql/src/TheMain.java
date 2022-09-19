import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author tgutberl
 */
public class TheMain {
  public static void main(String[] args) {
    insertKnowledgegraph("a", "b", "r3");
    getByTripel("b", "a", "r");
  }
  //Veraltet - Für Inserts genutzt
  public static void insertKnowledgegraph(String head, String tail, String relation){
    Connection con = DBConnection.connect();
    PreparedStatement ps = null;
    try{
      String sql = "INSERT INTO knowledgegraph(kg_v1, kg_v2, kg_r) VALUES (?,?,?)";
      ps = con.prepareStatement(sql);
      ps.setString(1, head);
      ps.setString(2, tail);
      ps.setString(3, relation);
      ps.execute();
      System.out.println("Data has been inserted");
    }catch (SQLException e){
      e.printStackTrace();
    }
  }
  //Main Function, used for getting the Rules, that are implicated by a given Triple
  public static void getByTripel(String value1, String value2, String rel){
    Connection con = DBConnection.connect();
    PreparedStatement ps = null;
    ResultSet rs = null;
    PreparedStatement ps2 = null;
    ResultSet rs2 = null;

    try {
      String sql = "SELECT * FROM head NATURAL JOIN tail WHERE head_r = ?";
      ps = con.prepareStatement(sql);
      ps.setString(1, rel);
      rs = ps.executeQuery();
      while (rs.next()){
        String id = rs.getString("ID");
        String head_v1 = rs.getString("head_v1");
        String head_v2 = rs.getString("head_v2");
        String head_r = rs.getString("head_r");
        String tail_v1 = rs.getString("tail_v1");
        String tail_v2 = rs.getString("tail_v2");
        String tail_r = rs.getString("tail_r");
        System.out.println(id + " " + head_v1 + " " + head_v2 + " " + head_r + " is implicated by " + tail_v1 + " "+ tail_v2 + " " + tail_r +  "\n");

        String searchRelation = tail_r;
        String search_v1 = "";
        String search_v2 = "";
        if (head_v1 == tail_v1) {
          search_v1 = value1;
        }
        if(head_v1 == tail_v2){
          search_v2 = value1;
        }
        if(head_v2 == tail_v1){
          search_v1 = value2;
        }
        if (head_v2 == tail_v2){
          search_v2 = value2;
        }
        String sql2 = "SELECT * FROM knowledgegraph WHERE kg_v1 LIKE ? AND kg_v2 LIKE ? AND kg_r LIKE ?";
        ps2 = con.prepareStatement(sql2);
        ps2.setString(1, search_v1);
        ps2.setString(2, search_v2);
        ps2.setString(3, searchRelation);
        rs2 = ps.executeQuery();
        while (rs2.next()){
          String kg_v1 = rs.getString("kg_v1");
          String kg_v2 = rs.getString("kg_v2");
          String kg_r = rs.getString("kg_r");
          System.out.println("KnowledgeGraph: " + kg_v1 + " " + kg_v2 + " " + kg_r);
        }
      }
    }catch (SQLException e){
      System.out.println(e.toString());
    } finally {
      try {
        con.close();
        rs.close();
        con.close();
      }catch (SQLException e){
        System.out.println(e.toString());
      }
    }
  }

  public static void readAllData(){
    Connection con = DBConnection.connect();
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String sql = "SELECT * FROM knowledgegraph";
      ps = con.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()){
        String head = rs.getString("head");
        String tail = rs.getString("value2");
        String relation = rs.getString("relation");
        System.out.println(head + " " + tail + " " + relation + "\n");
      }
    }catch (SQLException e){
      System.out.println(e.toString());
    } finally {
      try {
        con.close();
        rs.close();
        con.close();
      }catch (SQLException e){
        System.out.println(e.toString());
      }
    }
  }
  public static void readSpecificDataFromKG(){
    Connection con = DBConnection.connect();
    PreparedStatement ps = null;
    ResultSet rs = null;
    try {
      String sql = "SELECT * FROM knowledgegraph";
      ps = con.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()){
        String head = rs.getString("head");
        String tail = rs.getString("value2");
        String relation = rs.getString("relation");
        System.out.println(head + " " + tail + " " + relation + "\n");
      }
    }catch (SQLException e){
      System.out.println(e.toString());
    } finally {
      try {
        con.close();
        rs.close();
        con.close();
      }catch (SQLException e){
        System.out.println(e.toString());
      }
    }
  }
}

