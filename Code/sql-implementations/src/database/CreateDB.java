package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author timgutberlet
 */
public class CreateDB {

  static Connection connection;

  public static void setConnectDB(Connection con) {
    connection = con;
  }

  /**
   * Create the knowledgegraph Table including all Columns. Only if it not already exists.
   */
  public static void createKnowledgeGraphDB() {
    Statement stmt;
    String sql = "";
    try {
      sql = ""
          + "set schema 'public'; "
          + "create table test ("
          + " pre int)";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
      System.out.println("Success");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    /*
    try {
      sql = "set schema 'rules';"
          + "insert into knowledgegraph values (1, 1, 1)";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
      System.out.println("Success");
    } catch (SQLException e) {
      e.printStackTrace();
    }*/
  }


  /**
   * Creates knowledgegraph with all possible indexes
   */
  public static void createIndexedKnowledgeGraphDB() {
    Statement stmt;
    String sql = "";
    try {
      sql = "create table indexed_knowledgegraph "
          + "( "
          + "    subject   int, "
          + "    predicate int, "
          + "    object    int "
          + "); "
          + " "
          + "create index indexed_knowledgegraph_predicate_index "
          + "    on indexed_knowledgegraph (pred); "
          + " "
          + "create index indexed_knowledgegraph_predicate_object_index "
          + "    on indexed_knowledgegraph (pred, obj); "
          + " "
          + "create index indexed_knowledgegraph_predicate_subject_index "
          + "    on indexed_knowledgegraph (pred, sub); "
          + " "
          + "create index indexed_knowledgegraph_predicate_subject_object_index "
          + "    on indexed_knowledgegraph (pred, sub, obj); "
          + " ";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates knowledgegraph with unique triples
   */
  public static void createIndexedUniqueKnowledgeGraphDB() {
    Statement stmt;
    String sql = "";
    try {
      sql = "create table indexed_knowledgegraph_unique "
          + "( "
          + "    subject   int, "
          + "    predicate int, "
          + "    object    int "
          + "); "
          + " "
          + "create unique index indexed_knowledgegraph_unique_predicate_subject_object_uindex "
          + "    on indexed_knowledgegraph_unique (predicate, subject, object); "
          + " ";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates indizes to match the ids with the strings
   */
  public static void createTextIndizes() {
    Statement stmt;
    String sql = "";
    try {
      sql = "create table predicates "
          + "      ( "
          + "          id   int "
          + "          constraint predicates_pk "
          + "          primary key, "
          + "          text varchar "
          + "      ); "
          + "create table subjects "
          + "      ( "
          + "          id   int "
          + "          constraint subjects_pk "
          + "          primary key, "
          + "          text varchar "
          + "      ); "
          + "create table objects "
          + "      ( "
          + "          id   int "
          + "          constraint objects_pk "
          + "          primary key, "
          + "          text varchar "
          + "      ); ";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }










  /**
   * Create the HeadBaseDB, used for the second case, where all queries are in a single sql
   * statement. Only if it not already exists.
   */
  public static void createHeadBaseDB() {

  }

  /**
   * Reset the knowledgegraph DB empyting all tables
   */
  public static void resetKnowledgeGraphDB() {

  }

  /**
   * Reset the HeadBaseDB emtyping all tables
   */
  public static void resetHeadBaseDB() {

  }

  /**
   * Create whole database new WARNING: All data will be lost
   */
  public static void createAll() {
    createKnowledgeGraphDB();
    createIndexedKnowledgeGraphDB();
    createIndexedUniqueKnowledgeGraphDB();
    createTextIndizes();
  }

  /**
   * Reset whole database WARNING: All data will be lost
   */
  public static void resetAll() {
    Statement stmt;
    String sql = "";
    try {
      sql = "drop table if exists knowledgegraph cascade;"
          + " "
          + "drop table if exists indexed_knowledgegraph cascade; "
          + " "
          + "drop table if exists indexed_knowledgegraph_unique cascade; "
          + " "
          + "drop table if exists predicates cascade; "
          + " "
          + "drop table if exists subjects cascade;";
      stmt = connection.createStatement();
      stmt.executeUpdate(sql);
      stmt.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

}
