package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import models.Triple;

/**
 *
 * This file provides all functions used for working with the DB
 *
 * @author tgutberl
 */
public class DBFuncs {
  private static Connection con;

  public static void setCon(Connection con) {
    DBFuncs.con = con;
  }

  public static void insertKnowledgegraph(List<Triple> list) {
    PreparedStatement stmt;
    try {
      String sql = "INSERT INTO knowledgegraph(kg_v1, kg_v2, kg_r) VALUES (?,?,?)";
      System.out.println("SQL Statement");
      stmt = con.prepareStatement(sql);
      long startTime = System.nanoTime();
      long elapsedTime;
      long count = 0;
      for (Triple triple : list){
        count++;
        System.out.println(triple.toString());
        stmt.setString(1, triple.getV1());
        stmt.setString(2, triple.getV2());
        stmt.setString(3, triple.getRelation());
        stmt.addBatch();
        if (count % 500 == 0 || triple.getID() == list.size()){
          stmt.executeBatch();
          stmt.clearBatch();
          elapsedTime = System.nanoTime() - startTime;
          startTime = System.nanoTime();
          //System.out.println("Inserted " + triple.getID() + " of " + list.size() +" ; Time: "+ (elapsedTime/1000000) + "ms");
        }
      }
      stmt.executeBatch();
      con.commit();

      System.out.println("Data has been inserted");
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  public static void insertHead(List<Triple> list) {
    PreparedStatement stmt;
    try {
      String sql = "INSERT INTO head(ID, head_v1, head_v2, head_r) VALUES (?,?,?,?)";
      System.out.println("SQL Statement");
      stmt = con.prepareStatement(sql);
      long startTime = System.nanoTime();
      long elapsedTime;
      for (Triple triple : list){
        stmt.setInt(1, triple.getID());
        stmt.setString(2, triple.getV1());
        stmt.setString(3, triple.getV2());
        stmt.setString(4, triple.getRelation());
        stmt.addBatch();
        if (triple.getID() % 1000 == 0 || triple.getID() == list.size()){
          stmt.executeBatch();
          stmt.clearBatch();
          elapsedTime = System.nanoTime() - startTime;
          startTime = System.nanoTime();
          System.out.println("Inserted " + triple.getID() + " of " + list.size() +" ; Time: "+ (elapsedTime/1000000) + "ms");
        }
      }
      stmt.executeBatch();
      con.commit();

      System.out.println("Data has been inserted");
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  public static void insertTail(List<Triple> list) {
    PreparedStatement stmt;
    try {
      con.setAutoCommit(false);
      String sql = "INSERT INTO tail(ID, tail_v1, tail_v2, tail_r) VALUES (?,?,?,?)";
      System.out.println("SQL Statement");
      stmt = con.prepareStatement(sql);
      long startTime = System.nanoTime();
      long elapsedTime;
      long count = 0;
      for (Triple triple : list){
        count++;
        stmt.setInt(1, triple.getID());
        stmt.setString(2, triple.getV1());
        stmt.setString(3, triple.getV2());
        stmt.setString(4, triple.getRelation());
        stmt.addBatch();
        if (count% 1000 == 0 || triple.getID() == list.size()){
          stmt.executeBatch();
          stmt.clearBatch();
          elapsedTime = System.nanoTime() - startTime;
          startTime = System.nanoTime();
          System.out.println("Inserted " + count + " of " + list.size() +" ; Time: "+ (elapsedTime/1000000) + "ms");
        }
      }
      stmt.executeBatch();
      con.commit();
      System.out.println("Data has been inserted");
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  //Main Function, used for getting the Rules, that are implicated by a given Triple
  public static Hashtable<String, ArrayList<String>> getByTripel(String value1, String value2,
      String rel) {
    //Create Empty values for later use
    Hashtable<String, ArrayList<String>> queries = new Hashtable<String, ArrayList<String>>();
    //Create connection
    //Create Empty values for later use
    ArrayList<String> array = new ArrayList<String>();
    PreparedStatement ps = null;
    ResultSet rs = null;
    PreparedStatement ps2 = null;
    ResultSet rs2 = null;
    String s = null;

    try {
      String sql = "";
      //Get all Heads and Tails that have the same relation in the head as the search values as well as the same strong valus
      if (value1 != value2) {
        sql = "SELECT * FROM head NATURAL JOIN tail WHERE head_r = ? AND head_v1 != head_v2 AND "
            + "((head_v1 = ? AND head_v2 = ?) OR"
            + "(LENGTH(head_v1) = 1 AND LENGTH(head_v2) = 1) OR (LENGTH(head_v1) = 1 AND head_v2 = ?) OR (LENGTH(head_v2) = 1 AND head_v1 = ?)) "
            + "AND WHERE EXISTS ("
            + "SELECT * "
            + "FROM knowledgegraph "
            + "WHERE kg_v1 = CASE WHEN (head.head_v1 = tail.tail_v1) THEN ? /*(6 Value1)*/ "
            + "WHEN (head.head_v2 = tail.tail_v1) THEN ? /*7(value2)*/ END "
            + "AND kg_v1 != CASE WHEN (head.head_v1 != tail.tail_v1) THEN ? /*(8 Value1*/"
            + "WHEN (head.head_v2 != tail.tail_v1) THEN ? /*(9Value2)*/ END "
            + "kg_v2 = CASE WHEN (head.head_v1 = tail.tail_v2) THEN ? /*(10 Value1)*/"
            + "WHEN (head.head_v2 = tail.tail_v2) THEN ? /*11 Value2*/ END "
            + "kg_v2 != CASE WHEN (head.head_v1 != tail.tail_v2) THEN ? /*(12 Value1)*/"
            + "WHEN (head.head_v2 = tail.tail_v2) THEN ? /*(13 Value2)*/"
            + "AND kg_r LIKE tail.tail_r"
            + ")";
        //Attention! Fix so that ever tail gets checked, that alle have a value in the knowledgegraph not only one
      }else {
        sql = "SELECT * FROM head NATURAL JOIN tail WHERE head_r = ? AND head_v1 = head_v2 AND "
            + "((head_v1 = ? AND head_v2 = ?) OR"
            + "(LENGTH(head_v1) = 1 AND LENGTH(head_v2) = 1) OR (LENGTH(head_v1) = 1 AND head_v2 = ?) OR (LENGTH(head_v2) = 1 AND head_v1 = ?))";
      }

      //Start test

      //End test
      ps = con.prepareStatement(sql);
      ps.setString(1, rel);
      ps.setString(2, value1);
      ps.setString(3, value2);
      ps.setString(4, value2);
      ps.setString(5, value1);

      ps.setString(6, value1);
      ps.setString(7, value2);
      ps.setString(8, value1);
      ps.setString(9, value2);
      ps.setString(10, value1);
      ps.setString(11, value2);
      ps.setString(12, value1);
      ps.setString(13, value2);

      System.out.println();
      //System.out.println(rel + " " + value1 + " " + value2);
      rs = ps.executeQuery();
      //Get all connected values
      while (rs.next()) {
        String id = rs.getString("ID");
        String head_v1 = rs.getString("head_v1");
        String head_v2 = rs.getString("head_v2");
        String head_r = rs.getString("head_r");
        String tail_v1 = rs.getString("tail_v1");
        String tail_v2 = rs.getString("tail_v2");
        String tail_r = rs.getString("tail_r");
        //System.out.println(
        //    id + " " + head_v1 + " " + head_v2 + " " + head_r + " is implicated by " + tail_v1 + " "
        //        + tail_v2 + " " + tail_r + "\n");
        // Section

        String searchRelation = tail_r;
        String search_v1 = "%";
        String search_v2 = "%";
        String unlike = "";
        //Check for equal / unequal values
        if (head_v1.equals(tail_v1)) {
          search_v1 = value1;
        } else {
          unlike += " AND kg_v1 != '" + value1 + "'";
        }
        if (head_v1.equals(tail_v2)) {
          search_v2 = value1;
        } else {
          unlike += " AND kg_v2 != '" + value1 + "'";
        }
        if (head_v2.equals(tail_v1)) {
          search_v1 = value2;
        } else {
          unlike += " AND kg_v1 != '" + value2 + "'";
        }
        if (head_v2.equals(tail_v2)) {
          search_v2 = value2;
        } else {
          unlike += " AND kg_v2 != '" + value2 + "'";
        }
        //Check for equal values and get the values out of the Knowledge Graph
        if(tail_v1.equals(tail_v2)){
          //System.out.println(unlike);
          String sql2 = "SELECT * FROM knowledgegraph WHERE kg_v1 LIKE kg_v2 AND kg_r LIKE ?" + unlike;
          System.out.println(sql2);
          ps2 = con.prepareStatement(sql2);
          ps2.setString(1, searchRelation);
        }else{
          String sql2 = "SELECT * FROM knowledgegraph WHERE kg_v1 LIKE ? AND kg_v2 LIKE ? AND kg_r LIKE ?" + unlike;
          ps2 = con.prepareStatement(sql2);
          //System.out.println(search_v1 + " " + search_v2 + " " + searchRelation);
          ps2.setString(1, search_v1);
          ps2.setString(2, search_v2);
          ps2.setString(3, searchRelation);
        }

        rs2 = ps2.executeQuery();
        if (rs2.next() == false) {
          if (queries.containsKey(id)) {
            queries.remove(id);
          }
        } else {
          do {
            String kg_v1 = rs2.getString("kg_v1");
            String kg_v2 = rs2.getString("kg_v2");
            String kg_r = rs2.getString("kg_r");
            //System.out.println("KnowledgeGraph: " + kg_v1 + " " + kg_v2 + " " + kg_r);
            s = head_v1 + " " + head_v2 + " " + head_r + " <-- " + tail_v1 + " "
                + tail_v2 + " " + tail_r;
            if (queries.containsKey(id)) {
              array = queries.get(id);
              array.add(s);
              queries.put(id, array);
              break;
            } else {
              array = new ArrayList<>();
              array.add(s);
              queries.put(id, array);
              break;
            }
          } while (rs2.next());
        }
      }
    } catch (SQLException e) {
      System.out.println(e.toString());
    } finally {
      try {
        rs.close();
        con.close();
      } catch (SQLException e) {
        System.out.println(e.toString());
      }
    }
    return queries;
  }
  public static void deleteHead(){
    Statement stmt;
    String sql = "";
    try {
      sql = "DELETE FROM head";
      stmt =  con.createStatement();
      stmt.executeQuery(sql);
    } catch (SQLException e) {
    }
  }
  public static void deleteTail(){
    Statement stmt;
    String sql = "";
    try {
      sql = "DELETE FROM Tail";
      stmt =  con.createStatement();
      stmt.executeQuery(sql);
    } catch (SQLException e) {
    }
  }
  public static void deleteKG(){
    Statement stmt;
    String sql = "";
    try {
      sql = "DELETE FROM knowledgegraph";
      stmt =  con.createStatement();
      stmt.executeQuery(sql);
    } catch (SQLException e) {
    }
  }

  public static void readAllData() {
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
      String sql = "SELECT * FROM knowledgegraph";
      ps = con.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String head = rs.getString("head");
        String tail = rs.getString("value2");
        String relation = rs.getString("relation");
        System.out.println(head + " " + tail + " " + relation + "\n");
      }
    } catch (SQLException e) {
      System.out.println(e.toString());
    } finally {
      try {
        con.close();
        rs.close();
        con.close();
      } catch (SQLException e) {
        System.out.println(e.toString());
      }
    }
  }

  public static Map<Integer, Triple> getKnowledgeGraph() {
    PreparedStatement ps = null;
    ResultSet rs = null;
    Map<Integer, Triple> list = new HashMap<Integer, Triple>();
    int count = 0;
    try {
      String sql = "SELECT * FROM knowledgegraph";
      ps = con.prepareStatement(sql);
      rs = ps.executeQuery();
      while (rs.next()) {
        String v1 = rs.getString("kg_v1");
        String v2 = rs.getString("kg_v2");
        String relation = rs.getString("kg_r");
        list.put(count, new Triple(v1, v2, relation));
        count++;
      }
    } catch (SQLException e) {
      System.out.println(e.toString());
    } finally {
      try {
        rs.close();
        con.close();
      } catch (SQLException e) {
        System.out.println(e.toString());
      }
    }
    return list;
  }
}

