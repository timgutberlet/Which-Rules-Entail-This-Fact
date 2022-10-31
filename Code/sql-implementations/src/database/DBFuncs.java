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
 * @author timgutberlet
 */
public class DBFuncs {
  private static Connection con;

  /**
   * Used for setting the connection
   * @param con
   */
  public static void setCon(Connection con) {
    DBFuncs.con = con;
  }

  public static void insertKnowledgegraph(List<Triple> list) {
    PreparedStatement stmt;
    try {
      String sql = "INSERT INTO knowledgegraph(sub, pre, obj) VALUES (?,?,?)";
      stmt = con.prepareStatement(sql);
      long startTime = System.nanoTime();
      long elapsedTime;
      long count = 0;
      for (Triple triple : list){
        count++;
        //System.out.println(triple.toString());
        stmt.setInt(1, triple.getSubject());
        stmt.setInt(2, triple.getPredicate());
        stmt.setInt(3, triple.getObject());
        stmt.addBatch();
        if (count % 500 == 0 || count == list.size()){
          stmt.executeBatch();
          stmt.clearBatch();
          elapsedTime = System.nanoTime() - startTime;
          startTime = System.nanoTime();
          //System.out.println("Inserted " + triple.getID() + " of " + list.size() +" ; Time: "+ (elapsedTime/1000000) + "ms");
        }
      }
      stmt.executeBatch();
      con.commit();
      System.out.println("Success");

      System.out.println("Data has been inserted");
      con.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void insertSubjects(HashMap<String, Integer> map) {
    PreparedStatement stmt;
    try {
      String sql = "INSERT INTO subjects(id, txt) VALUES (?,?)";
      stmt = con.prepareStatement(sql);
      long count = 0;
      for (String txt : map.keySet()){
        count++;
        //System.out.println(triple.toString());
        stmt.setInt(1, map.get(txt));
        stmt.setString(2, txt);
        stmt.addBatch();
        if (count % 500 == 0 || count == map.size()){
          stmt.executeBatch();
          stmt.clearBatch();
        }
      }
      stmt.executeBatch();
      con.commit();
      stmt.close();
      System.out.println("Data has been inserted");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void insertPredicates(HashMap<String, Integer> map) {
    PreparedStatement stmt;
    try {
      String sql = "INSERT INTO predicates(id, txt) VALUES (?,?)";
      stmt = con.prepareStatement(sql);
      long count = 0;
      for (String txt : map.keySet()){
        count++;
        //System.out.println(triple.toString());
        stmt.setInt(1, map.get(txt));
        stmt.setString(2, txt);
        stmt.addBatch();
        if (count % 500 == 0 || count == map.size()){
          stmt.executeBatch();
          stmt.clearBatch();
        }
      }
      stmt.executeBatch();
      con.commit();
      stmt.close();
      System.out.println("Data has been inserted");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void insertObjects(HashMap<String, Integer> map) {
    PreparedStatement stmt;
    try {
      String sql = "INSERT INTO objects(id, txt) VALUES (?,?)";
      stmt = con.prepareStatement(sql);
      long count = 0;
      for (String txt : map.keySet()){
        count++;
        //System.out.println(triple.toString());
        stmt.setInt(1, map.get(txt));
        stmt.setString(2, txt);
        stmt.addBatch();
        if (count % 500 == 0 || count == map.size()){
          stmt.executeBatch();
          stmt.clearBatch();
        }
      }
      stmt.executeBatch();
      con.commit();
      stmt.close();
      System.out.println("Data has been inserted");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void insertIndexedKnowledgegraph(List<Triple> list) {
    PreparedStatement stmt;
    try {
      String sql = "INSERT INTO indexed_knowledgegraph(sub, pred, obj) VALUES (?,?,?)";
      stmt = con.prepareStatement(sql);
      long startTime = System.nanoTime();
      long elapsedTime;
      long count = 0;
      for (Triple triple : list){
        count++;
        //System.out.println(triple.toString());
        stmt.setInt(1, triple.getSubject());
        stmt.setInt(2, triple.getPredicate());
        stmt.setInt(3, triple.getObject());
        stmt.addBatch();
        if (count % 500 == 0 || count == list.size()){
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

  public static void insertIndexedKnowledgegraphUnique(List<Triple> list) {
    PreparedStatement stmt;
    try {
      String sql = "INSERT INTO indexed_knowledgegraph_unique(sub, pre, obj) VALUES (?,?,?)";
      stmt = con.prepareStatement(sql);
      long startTime = System.nanoTime();
      long elapsedTime;
      long count = 0;
      for (Triple triple : list){
        count++;
        //System.out.println(triple.toString());
        stmt.setInt(1, triple.getSubject());
        stmt.setInt(2, triple.getPredicate());
        stmt.setInt(3, triple.getObject());
        stmt.addBatch();
        if (count % 500 == 0 || count == list.size()){
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
  /**
  public static void insertHead(List<Triple> list) {
    PreparedStatement stmt;
    int count = 0;
    try {
      String sql = "INSERT INTO head(subject, predicate, object) VALUES (?,?,?)";
      System.out.println("SQL Statement");
      stmt = con.prepareStatement(sql);
      long startTime = System.nanoTime();
      long elapsedTime;
      for (Triple triple : list){
        stmt.setInt(1, triple.getSubject());
        stmt.setInt(2, triple.getPredicate());
        stmt.setInt(3, triple.getObject());
        stmt.addBatch();
        if (count % 1000 == 0 || count == list.size()){
          stmt.executeBatch();
          stmt.clearBatch();
          elapsedTime = System.nanoTime() - startTime;
          startTime = System.nanoTime();
          System.out.println("Inserted " + count + " of " + list.size() +" ; Time: "+ (elapsedTime/1000000) + "ms");
        }
        count++;
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
      String sql = "INSERT INTO tail(subject, predicate, object) VALUES (?,?,?)";
      System.out.println("SQL Statement");
      stmt = con.prepareStatement(sql);
      long startTime = System.nanoTime();
      long elapsedTime;
      long count = 0;
      for (Triple triple : list){
        count++;
        stmt.setInt(1, triple.getSubject());
        stmt.setInt(2, triple.getPredicate());
        stmt.setInt(3, triple.getObject());
        stmt.addBatch();
        if (count% 1000 == 0 || count == list.size()){
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
  **/




  /**
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
  **/
  /**
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
  **/
  public static void deleteKG(){
    Statement stmt;
    String sql;
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
        int subject = rs.getInt("subject");
        int predicate = rs.getInt("predicate");
        int object = rs.getInt("object");
        System.out.println(subject + " " + predicate + " " + object + "\n");
      }
    } catch (SQLException e) {
      System.out.println(e.toString());
    } finally {
      try {
        rs.close();
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
        int v1 = rs.getInt("kg_v1");
        int v2 = rs.getInt("kg_v2");
        int relation = rs.getInt("kg_r");
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

