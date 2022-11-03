package database;

import config.Settings;
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
import models.Rule;
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
      stmt.close();
      System.out.println("Success");

      System.out.println("Data has been inserted");
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
      stmt.close();
      System.out.println("Data has been inserted");

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
      stmt.close();
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

  public static void deleteIndizes(){
    Statement stmt;
    String sql;
    try {
      sql = "DELETE FROM objects; DELETE FROM predicates; DELETE FROM subjects";
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
        int subject = rs.getInt("sub");
        int predicate = rs.getInt("pre");
        int object = rs.getInt("obj");
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

  public static int getSubjectID(String txt) {
    PreparedStatement ps;
    ResultSet rs = null;
    int id;
    try {
      String sql = "SELECT id FROM subjects WHERE txt = ?";
      ps = con.prepareStatement(sql);
      ps.setString(1, txt);
      rs = ps.executeQuery();
      while (rs.next()) {
        id = rs.getInt("id");
        return id;
      }
    } catch (SQLException e) {
      System.out.println(e);
    } finally {
      try {
        rs.close();
      } catch (SQLException e) {
        return -99;
      } catch (NullPointerException e){
        return -99;
      }
    }
    return -99;
  }

  public static int getPredicateID(String txt) {
    PreparedStatement ps;
    ResultSet rs = null;
    int id;
    try {
      String sql = "SELECT id FROM predicates WHERE txt = ?";
      ps = con.prepareStatement(sql);
      ps.setString(1, txt);
      rs = ps.executeQuery();
      while (rs.next()) {
        id = rs.getInt("id");
        return id;
      }
    } catch (SQLException e) {
      System.out.println(e);
    } finally {
      try {
        rs.close();
      } catch (SQLException e) {
        System.out.println(e);
      } catch (NullPointerException e){
        return -99;
      }
    }
    return -99;
  }

  public static int getObjectID(String txt) {
    PreparedStatement ps;
    ResultSet rs = null;
    int id;
    try {
      String sql = "SELECT id FROM public.objects WHERE txt = ?";
      ps = con.prepareStatement(sql);
      ps.setString(1, txt);
      rs = ps.executeQuery();
      while (rs.next()) {
        id = rs.getInt("id");
        return id;
      }
    } catch (SQLException e) {
      System.out.println(e);
    } finally {
      try {
        rs.close();
      } catch (SQLException e) {
        return -99;
      } catch (NullPointerException e){
        return -99;
      }
    }
    return -99;
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
      } catch (SQLException e) {
        System.out.println(e.toString());
      }
    }
    return list;
  }

  /**
   * Used for testing
   * @param args
   */
  public static void main(String[] args) {
    ConnectDB connectDB = new ConnectDB(Settings.CLASSNAME, Settings.URL, Settings.USER, Settings.PASSWORD);
    con = connectDB.getConnection();
  }

  public static void testRules(List<Rule> filteredRules, Triple ogTriple){
    boolean first;
    PreparedStatement stmt;
    ResultSet rs = null;
    try {
      StringBuffer sql, sqlEnd;
      StringBuffer sub, pre, obj;
      StringBuffer select, where;
      int help;

      long count = 0;
      for (Rule rule : filteredRules){
        first = true;
        sql = new StringBuffer("SELECT case when EXISTS (");
        sqlEnd = new StringBuffer(") THEN ? end;");
        select = new StringBuffer("SELECT 1 FROM ");
        where = new StringBuffer(" WHERE 1=1");
        help = 1;
        for (Triple triple : rule.getBody()){
          /** TODO Idea to create SQL Statements with Intersects and not with joins
          if (first){
            first = false;
          }else {
            sql.append(" INTERSECT ");
          }
          **/

          //Create FROM statements
          if (first){
            select.append("knowledgegraph kg" + help);
            first = false;
          }else {
            select.append(", knowledgegraph kg" + help);
          }

          //Create WHERE Statements
          sub = new StringBuffer();
          if(triple.getSubject() < 0){
            if (triple.getObject() < 0 && triple.getObject() != triple.getSubject()){
              sub.append(" AND kg"+help+".sub != " + "kg"+help+".obj");
            }
            if (triple.getSubject() == rule.getHead().getSubject()){
              sub.append(" AND kg"+help+".sub = " + ogTriple.getSubject() );
            } else if (triple.getSubject() == rule.getHead().getObject()){
              sub.append(" AND kg"+help+".sub = " + ogTriple.getObject() );
            }
            //Check if equal with head
          }else{
            sub.append(" AND kg"+help+".sub = " + triple.getSubject());
          }
          //Create WHERE Statements
          obj = new StringBuffer();
          if(triple.getObject() >= 0){
            obj = new StringBuffer(" AND kg"+help+".obj = " + triple.getObject());
          }else {
            if (triple.getObject() == rule.getHead().getSubject()){
              obj.append(" AND kg"+help+".obj = " + ogTriple.getSubject() );
            } else if (triple.getObject() == rule.getHead().getObject()){
              obj.append(" AND kg"+help+".obj = " + ogTriple.getObject() );
            }
          }
          pre = new StringBuffer(" AND kg"+help+".pre = " + triple.getPredicate());
          where.append(sub);
          where.append(pre);
          where.append(obj);
          help++;
        }
        sql.append(select);
        sql.append(where);
        sql.append(sqlEnd);
        System.out.println(sql);
        stmt = con.prepareStatement(sql.toString());
        stmt.setString(1, rule.toString());
        rs = stmt.executeQuery();
        System.out.println("Success");
        while (rs.next()) {
          System.out.println("Found: ");
          System.out.println(rs.getString("case"));
        }
      }
    } catch (SQLException e) {
      System.out.println(e);
    }
  }
}

