package database;

import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import config.Config;
import config.Debug;
import models.*;
import org.postgresql.jdbc2.ArrayAssistant;
import utils.RandomRules;

/**
 * This file provides all functions used for working with the DB
 *
 * @author timgutberlet
 */
public class DBFuncs {

    private static Connection con;

    /**
     * Used for setting the connection
     *
     * @param con
     */
    public static void setCon(Connection con) {
        DBFuncs.con = con;
    }

    /**
     * Method used for inserting a list of triples in the knowledgegraph database. The knowledgegraph
     * table is used, that was specified in the config.properites
     *
     * @param list A list of triples, imported from e.g. a text file, that is supposed to be inserted
     *             in the database
     */
    public static void insertKnowledgegraph(List<Triple> list) {
        PreparedStatement stmt;
        try {
            String sql =
                    "INSERT INTO " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + "(sub, pre, obj) VALUES (?,?,?)";
            stmt = con.prepareStatement(sql);
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            for (Triple triple : list) {
                count++;
                stmt.setInt(1, triple.getSubject());
                stmt.setInt(2, triple.getPredicate());
                stmt.setInt(3, triple.getObject());
                stmt.addBatch();
                if (count % 1000 == 0 || count == list.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted " + count + " of " + list.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
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



    public static void deleteKG() {
        Statement stmt;
        String sql;
        try {
            sql = "DELETE FROM " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE");
            stmt = con.createStatement();
            stmt.executeQuery(sql);
            con.commit();
        } catch (SQLException e) {
        }
    }

    public static void deleteSubjects() {
        Statement stmt;
        String sql;
        try {
            sql = "DELETE FROM subjects;";
            stmt = con.createStatement();
            stmt.executeQuery(sql);
            con.commit();
        } catch (SQLException e) {
        }
    }
    public static void deletePredicates() {
        Statement stmt;
        String sql;
        try {
            sql = "DELETE FROM predicates;";
            stmt = con.createStatement();
            stmt.executeQuery(sql);
            con.commit();
        } catch (SQLException e) {
        }
    }
    public static void deleteObjects() {
        Statement stmt;
        String sql;
        try {
            sql = "DELETE FROM objects;";
            stmt = con.createStatement();
            stmt.executeQuery(sql);
            con.commit();
        } catch (SQLException e) {
        }
    }


    /**
     * Used for testing
     *
     * @param args
     */
    public static void main(String[] args) {
        //Rule r = new Rule(new Triple(1, 1, -1), new ArrayList<Triple>(), 342);
        //r.getBody().add(new Triple(1, 2, -2));
        //r.getBody().add(new Triple(-2, 3, -1));
        //createFunctionsBothUnbound(r);
    }
    public static void setSubjectIndex(HashMap<String, Integer> map){
        try {
        PreparedStatement stmt;
            String sql =
                    "INSERT INTO subjects(id, txt) VALUES (?,?)";
            stmt = con.prepareStatement(sql);
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
        for(Map.Entry<String, Integer> entry : map.entrySet()){
                    count++;
                    stmt.setInt(1, entry.getValue());
                    stmt.setString(2, entry.getKey());
                    stmt.addBatch();
                    if (count % 500 == 0 || count == map.size()) {
                        stmt.executeBatch();
                        stmt.clearBatch();
                        elapsedTime = System.nanoTime() - startTime;
                        startTime = System.nanoTime();
                        System.out.println(
                                "Inserted Subjects " + count + " of " + map.size() + " ; Time: " + (elapsedTime / 1000000)
                                        + "ms");
                    }
        }
        stmt.executeBatch();
        con.commit();
        stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void setPredicateIndex(HashMap<String, Integer> map){
        try {
            PreparedStatement stmt;
            String sql =
                    "INSERT INTO predicates(id, txt) VALUES (?,?)";
            stmt = con.prepareStatement(sql);
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            for(Map.Entry<String, Integer> entry : map.entrySet()){
                count++;
                stmt.setInt(1, entry.getValue());
                stmt.setString(2, entry.getKey());
                stmt.addBatch();
                if (count % 500 == 0 || count == map.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted Predicates " + count + " of " + map.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                }
            }
            stmt.executeBatch();
            con.commit();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void setObjectIndex(HashMap<String, Integer> map){
        try {
            PreparedStatement stmt;
            String sql =
                    "INSERT INTO objects(id, txt) VALUES (?,?)";
            stmt = con.prepareStatement(sql);
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            for(Map.Entry<String, Integer> entry : map.entrySet()){
                count++;
                stmt.setInt(1, entry.getValue());
                stmt.setString(2, entry.getKey());
                stmt.addBatch();
                if (count % 500 == 0 || count == map.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted Objects " + count + " of " + map.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                }
            }
            stmt.executeBatch();
            con.commit();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Subject Index
     */
    public static HashMap<String, Integer> getSubjectIndex() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        PreparedStatement ps;
        ResultSet rs = null;
        int id;
        String txt;
        try {
            String sql = "SELECT id, txt FROM public.subjects";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
                txt = rs.getString("txt");
                hashMap.put(txt, id);
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println(e);
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
        return hashMap;
    }

    /**
     * Get Object Index
     */
    public static HashMap<String, Integer> getObjectIndex() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        PreparedStatement ps;
        ResultSet rs = null;
        int id;
        String txt;
        try {
            String sql = "SELECT id, txt FROM public.objects";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
                txt = rs.getString("txt");
                hashMap.put(txt, id);
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println(e);
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
        return hashMap;
    }

    /**
     * Get Predicate Index
     */
    public static HashMap<String, Integer> getPredicateIndex() {
        HashMap<String, Integer> hashMap = new HashMap<>();
        PreparedStatement ps;
        ResultSet rs = null;
        int id;
        String txt;
        try {
            String sql = "SELECT id, txt FROM public.predicates";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
                txt = rs.getString("txt");
                hashMap.put(txt, id);
            }
        } catch (SQLException e) {
            System.out.println(e);
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println(e);
            } catch (NullPointerException e) {
                System.out.println(e);
            }
        }
        return hashMap;
    }

    /**
     * Change from testRulesUnionAll: Also shorter selects, without exists
     *
     * @param filteredRules
     * @param ogTriple
     */
    public static ArrayList<Integer> testRulesUnionAllShorterSelect(List<Rule> filteredRules, Triple ogTriple) {
        ArrayList<Integer> foundRules = new ArrayList<>();
        boolean first, first2;
        Triple r1, r2;
        ResultSet rs;
        StringBuffer sql, sqlEnd;
        sql = new StringBuffer();
        StringBuffer sub, pre, obj;
        StringBuffer select, where;
        int help;
        boolean firstR = true;
        for (Rule rule : filteredRules) {
            if (rule.getBody().size() == 0) {
                continue;
            }
            first = true;
            first2 = true;
            if (firstR) {
                sql.append("(");
                firstR = false;
            } else {
                sql.append(" UNION ALL (");
            }

            sqlEnd = new StringBuffer(" LIMIT 1) "); //TODO Hash key hier rein
            select = new StringBuffer("SELECT " + rule.getId() + " FROM ");
            where = new StringBuffer(" WHERE");
            help = 0;
            for (Triple triple : rule.getBody()) {

                //Create FROM statements
                if (first) {
                    select.append(Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " k" + help);
                    first = false;
                } else {
                    select.append(", " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " k" + help);
                }
                //Predicate
                if (first2) {
                    pre = new StringBuffer(" k" + help + ".pre = " + triple.getPredicate());
                    first2 = false;
                } else {
                    pre = new StringBuffer(" AND k" + help + ".pre = " + triple.getPredicate());
                }
                //Object
                obj = new StringBuffer();
                if (triple.getObject() >= 0) {
                    if (first2) {
                        obj = new StringBuffer(" k" + help + ".obj = " + triple.getObject());
                        first2 = false;
                    } else {
                        obj = new StringBuffer(" AND k" + help + ".obj = " + triple.getObject());
                    }
                } else {
                    if (triple.getObject() == rule.getHead().getSubject()) {
                        if (first2) {
                            obj.append(" k" + help + ".obj = " + ogTriple.getSubject());
                            first2 = false;
                        } else {
                            obj.append(" AND k" + help + ".obj = " + ogTriple.getSubject());
                        }
                    } else if (triple.getObject() == rule.getHead().getObject()) {
                        if (first2) {
                            obj.append(" k" + help + ".obj = " + ogTriple.getObject());
                            first2 = false;
                        } else {
                            obj.append(" AND k" + help + ".obj = " + ogTriple.getObject());
                        }
                    }
                }


                //Create WHERE Statements
                sub = new StringBuffer();
                if (triple.getSubject() < 0) {
                    if (triple.getSubject() == rule.getHead().getSubject()) {
                        if (first2) {
                            first2 = false;
                            sub.append(" k" + help + ".sub = " + ogTriple.getSubject());
                        } else {
                            sub.append(" AND k" + help + ".sub = " + ogTriple.getSubject());
                        }
                    } else if (triple.getSubject() == rule.getHead().getObject()) {
                        if (first2) {
                            first2 = false;
                            sub.append(" k" + help + ".sub = " + ogTriple.getObject());
                        } else {
                            sub.append(" AND k" + help + ".sub = " + ogTriple.getObject());
                        }
                    }
                    //Check if equal with head
                } else {
                    if (first2) {
                        first2 = false;
                        sub.append(" k" + help + ".sub = " + triple.getSubject());
                    } else {
                        sub.append(" AND k" + help + ".sub = " + triple.getSubject());
                    }

                }
                //Create WHERE Statements


                where.append(pre);
                where.append(obj);
                where.append(sub);
                help++;
            }
            for (int i = 0; i < rule.getBody().size(); ++i) {
                for (int j = i + 1; j < rule.getBody().size(); ++j) {
                    r1 = rule.getBody().get(i);
                    r2 = rule.getBody().get(j);
                    if (r1.getSubject() < 0) {
                        if (r1.getSubject() == r2.getSubject()) {
                            if (first2) {
                                first2 = false;
                                where.append(" k" + i + ".sub = k" + j + ".sub");
                            } else {
                                where.append(" AND k" + i + ".sub = k" + j + ".sub");
                            }
                        }
                        if (r1.getSubject() == r2.getObject()) {
                            if (first2) {
                                first2 = false;
                                where.append(" k" + i + ".sub = k" + j + ".obj");
                            } else {
                                where.append(" AND k" + i + ".sub = k" + j + ".obj");
                            }
                        }
                    }
                    if (r1.getObject() < 0) {
                        if (r1.getObject() == r2.getObject()) {
                            if (first2) {
                                first2 = false;
                                where.append(" k" + i + ".obj = k" + j + ".obj");
                            } else {
                                where.append(" AND k" + i + ".obj = k" + j + ".obj");
                            }
                        }
                        if (r1.getObject() == r2.getSubject()) {
                            if (first2) {
                                first2 = false;
                                where.append(" k" + i + ".sub = k" + j + ".sub");
                            } else {
                                where.append(" AND k" + i + ".sub = k" + j + ".sub");
                            }
                        }
                    }
                }
            }
            sql.append(select);
            sql.append(where);
            sql.append(sqlEnd);
        }
        //System.out.println(sql);
        try {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                if (rs.getString("?column?") != null) {
                    foundRules.add(rs.getInt("?column?"));
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.printMessage(e, e.getMessage());
        }
        return foundRules;
    }

    /**
     * test Rules Function, that uses additional Views for each possible relation to implement more integration
     */
    public static ArrayList<Integer> testRulesUnionAllShorterSelectViewsForRelations(List<Rule> filteredRules, Triple ogTriple) {
        //long startTime1 = System.nanoTime();
        ArrayList<Integer> foundRules = new ArrayList<>();
        boolean first, first2;
        Triple r1, r2;
        Boolean optimizeActivated = "optimizedQuantileAnalysis".equals(Config.getStringValue("TESTRULES_METHOD"));
        ResultSet rs;
        try {
            Statement stmt = con.createStatement();
            StringBuffer sql, sqlEnd;
            sql = new StringBuffer();
            StringBuffer sub, obj;
            StringBuffer select, where;
            int iZahl;
            int help;
            boolean firstR = true;
            for (Rule rule : filteredRules) {
                if (rule.getBody().size() == 0) {
                    continue;
                }
                first = true;
                first2 = true;
                if (firstR) {
                    sql.append("(");
                    firstR = false;
                } else {
                    sql.append(" UNION ALL (");
                }
                if (rule.isLearned() && optimizeActivated) {
                    iZahl = rule.getBound();
                    StringBuffer sqlOpt = new StringBuffer();
                    if (iZahl == 0) {
                        sqlOpt.append("SELECT " + rule.getId() + " FROM x" + rule.getId() + " WHERE sub = " + ogTriple.getSubject() + " AND obj = " + ogTriple.getObject() + " LIMIT 1)");
                    } else if (iZahl == 1) {
                        sqlOpt.append("SELECT " + rule.getId() + " FROM x" + rule.getId() + " WHERE sub = " + ogTriple.getSubject() + " LIMIT 1)");
                    } else if (iZahl == -1){
                        sqlOpt.append("SELECT " + rule.getId() + " FROM x" + rule.getId() + " WHERE obj = " + ogTriple.getObject() + " LIMIT 1)");
                    }
                    sql.append(sqlOpt);
                    //System.out.println(sqlOpt);
                }else {

                    sqlEnd = new StringBuffer(" LIMIT 1) ");
                    select = new StringBuffer("SELECT " + rule.getId() + " FROM ");
                    where = new StringBuffer(" WHERE");
                    help = 0;
                    for (Triple triple : rule.getBody()) {
                        switch (Config.getStringValue("PREDICATE_VIEW")) {
                            case "viewsForPredicate":
                                if (triple.getSubject() >= 0) {
                                    if (first) {
                                        select.append("r" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "r" + triple.getPredicate() + " k" + help);
                                    }
                                } else if (triple.getObject() >= 0) {
                                    if (first) {
                                        select.append("l" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                    }
                                } else if (triple.getSubject() == rule.getHead().getSubject() || triple.getSubject() == rule.getHead().getObject()) {
                                    if (first) {
                                        select.append("l" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                    }
                                } else if (triple.getObject() == rule.getHead().getSubject() || triple.getObject() == rule.getHead().getObject()) {
                                    if (first) {
                                        select.append("r" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "r" + triple.getPredicate() + " k" + help);
                                    }
                                } else {
                                    if (first) {
                                        select.append("l" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                    }
                                }
                                break;
                            case "viewsForPredicateNoIndex":
                            case "viewsForPredicateSubObj":
                            case "viewsForPredicateObjSub":
                                if (first) {
                                    select.append("v" + triple.getPredicate() + " k" + help);
                                    first = false;
                                } else {
                                    select.append(", " + "v" + triple.getPredicate() + " k" + help);
                                }
                                break;
                            case "viewsForPredicateHashIndex":
                                Debug.printMessage("viewsForPredicateHashIndex not implemented yet");
                                break;
                            default:
                        }
                        //Create WHERE Statements
                        sub = new StringBuffer();
                        if (triple.getSubject() < 0) {
                            if (triple.getSubject() == rule.getHead().getSubject()) {
                                if (first2) {
                                    first2 = false;
                                    sub.append(" k" + help + ".sub = " + ogTriple.getSubject());
                                } else {
                                    sub.append(" AND k" + help + ".sub = " + ogTriple.getSubject());
                                }
                            } else if (triple.getSubject() == rule.getHead().getObject()) {
                                if (first2) {
                                    first2 = false;
                                    sub.append(" k" + help + ".sub = " + ogTriple.getObject());
                                } else {
                                    sub.append(" AND k" + help + ".sub = " + ogTriple.getObject());
                                }

                            }
                            //Check if equal with head
                        } else {
                            if (first2) {
                                first2 = false;
                                sub.append(" k" + help + ".sub = " + triple.getSubject());
                            } else {
                                sub.append(" AND k" + help + ".sub = " + triple.getSubject());
                            }

                        }
                        //Create WHERE Statements
                        obj = new StringBuffer();
                        if (triple.getObject() >= 0) {
                            if (first2) {
                                first2 = false;
                                obj = new StringBuffer(" k" + help + ".obj = " + triple.getObject());
                            } else {
                                obj = new StringBuffer(" AND k" + help + ".obj = " + triple.getObject());
                            }
                        } else {
                            if (triple.getObject() == rule.getHead().getSubject()) {
                                if (first2) {
                                    first2 = false;
                                    obj.append(" k" + help + ".obj = " + ogTriple.getSubject());
                                } else {
                                    obj.append(" AND k" + help + ".obj = " + ogTriple.getSubject());
                                }
                            } else if (triple.getObject() == rule.getHead().getObject()) {
                                if (first2) {
                                    first2 = false;
                                    obj.append(" k" + help + ".obj = " + ogTriple.getObject());
                                } else {
                                    obj.append(" AND k" + help + ".obj = " + ogTriple.getObject());
                                }
                            }
                        }
                        where.append(sub);
                        where.append(obj);
                        help++;
                    }
                    for (int i = 0; i < rule.getBody().size(); ++i) {
                        for (int j = i + 1; j < rule.getBody().size(); ++j) {
                            r1 = rule.getBody().get(i);
                            r2 = rule.getBody().get(j);
                            if (r1.getSubject() < 0) {
                                if (r1.getSubject() == r2.getSubject()) {
                                    if (first2) {
                                        first2 = false;
                                        where.append(" k" + i + ".sub = k" + j + ".sub");
                                    } else {
                                        where.append(" AND k" + i + ".sub = k" + j + ".sub");
                                    }

                                }
                                if (r1.getSubject() == r2.getObject()) {
                                    if (first2) {
                                        first2 = false;
                                        where.append(" k" + i + ".sub = k" + j + ".obj");
                                    } else {
                                        where.append(" AND k" + i + ".sub = k" + j + ".obj");
                                    }
                                }
                            }
                            if (r1.getObject() < 0) {
                                if (r1.getObject() == r2.getObject()) {
                                    if (first2) {
                                        first2 = false;
                                        where.append(" k" + i + ".obj = k" + j + ".obj");
                                    } else {
                                        where.append(" AND k" + i + ".obj = k" + j + ".obj");
                                    }

                                }
                                if (r1.getObject() == r2.getSubject()) {
                                    if (first2) {
                                        first2 = false;
                                        where.append(" k" + i + ".obj = k" + j + ".sub");
                                    } else {
                                        where.append(" AND k" + i + ".obj = k" + j + ".sub");
                                    }

                                }
                            }
                        }
                    }
                    sql.append(select);
                    sql.append(where);
                    sql.append(sqlEnd);
                }
            }
            System.out.println(sql.toString());
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                if (rs.getString("?column?") != null) {
                    foundRules.add(rs.getInt("?column?"));
                }
            }
            rs.close();
        } catch (SQLException e) {
            //e.printStackTrace();
            //Debug.printMessage(e, e.getMessage());
        }
        return foundRules;
    }

    public static Double timePerRule(Rule rule, Triple ogTriple) {
        //System.out.println(ogTriple);
        //System.out.println(rule);
        //long startTime1 = System.nanoTime();
        boolean first = true, first2 = true;
        Triple r1, r2;

        ResultSet rs;
        try {
            Statement stmt = con.createStatement();
            StringBuffer sql, sqlEnd;
            sql = new StringBuffer();
            StringBuffer sub, obj;
            StringBuffer select, where;
            int help;
            boolean firstR = true;
            sqlEnd = new StringBuffer(" LIMIT 1 ");
            select = new StringBuffer("EXPLAIN ANALYZE SELECT " + rule.getId() + " FROM ");
            where = new StringBuffer(" WHERE");
            help = 0;
            for (Triple triple : rule.getBody()) {
                switch (Config.getStringValue("PREDICATE_VIEW")) {
                    case "viewsForPredicate":
                        if (triple.getSubject() >= 0) {
                            if (first) {
                                select.append("r" + triple.getPredicate() + " k" + help);
                                first = false;
                            } else {
                                select.append(", " + "r" + triple.getPredicate() + " k" + help);
                            }
                        } else if (triple.getObject() >= 0) {
                            if (first) {
                                select.append("l" + triple.getPredicate() + " k" + help);
                                first = false;
                            } else {
                                select.append(", " + "l" + triple.getPredicate() + " k" + help);
                            }
                        } else if (triple.getSubject() == rule.getHead().getSubject() || triple.getSubject() == rule.getHead().getObject()) {
                            if (first) {
                                select.append("l" + triple.getPredicate() + " k" + help);
                                first = false;
                            } else {
                                select.append(", " + "l" + triple.getPredicate() + " k" + help);
                            }
                        } else if (triple.getObject() == rule.getHead().getSubject() || triple.getObject() == rule.getHead().getObject()) {
                            if (first) {
                                select.append("r" + triple.getPredicate() + " k" + help);
                                first = false;
                            } else {
                                select.append(", " + "r" + triple.getPredicate() + " k" + help);
                            }
                        } else {
                            if (first) {
                                select.append("l" + triple.getPredicate() + " k" + help);
                                first = false;
                            } else {
                                select.append(", " + "l" + triple.getPredicate() + " k" + help);
                            }
                        }
                        break;
                    case "viewsForPredicateNoIndex":
                    case "viewsForPredicateSubObj":
                    case "viewsForPredicateObjSub":
                        if (first) {
                            select.append("v" + triple.getPredicate() + " k" + help);
                            first = false;
                        } else {
                            select.append(", " + "v" + triple.getPredicate() + " k" + help);
                        }
                        break;
                    case "viewsForPredicateHashIndex":
                        Debug.printMessage("viewsForPredicateHashIndex not implemented yet");
                        break;
                    default:
                }
                //Create WHERE Statements
                sub = new StringBuffer();
                if (triple.getSubject() < 0) {
                    if (triple.getSubject() == rule.getHead().getSubject()) {
                        if (first2) {
                            first2 = false;
                            sub.append(" k" + help + ".sub = " + ogTriple.getSubject());
                        } else {
                            sub.append(" AND k" + help + ".sub = " + ogTriple.getSubject());
                        }
                    } else if (triple.getSubject() == rule.getHead().getObject()) {
                        if (first2) {
                            first2 = false;
                            sub.append(" k" + help + ".sub = " + ogTriple.getObject());
                        } else {
                            sub.append(" AND k" + help + ".sub = " + ogTriple.getObject());
                        }

                    }
                    //Check if equal with head
                } else {
                    if (first2) {
                        first2 = false;
                        sub.append(" k" + help + ".sub = " + triple.getSubject());
                    } else {
                        sub.append(" AND k" + help + ".sub = " + triple.getSubject());
                    }

                }
                //Create WHERE Statements
                obj = new StringBuffer();
                if (triple.getObject() >= 0) {
                    if (first2) {
                        first2 = false;
                        obj = new StringBuffer(" k" + help + ".obj = " + triple.getObject());
                    } else {
                        obj = new StringBuffer(" AND k" + help + ".obj = " + triple.getObject());
                    }
                } else {
                    if (triple.getObject() == rule.getHead().getSubject()) {
                        if (first2) {
                            first2 = false;
                            obj.append(" k" + help + ".obj = " + ogTriple.getSubject());
                        } else {
                            obj.append(" AND k" + help + ".obj = " + ogTriple.getSubject());
                        }
                    } else if (triple.getObject() == rule.getHead().getObject()) {
                        if (first2) {
                            first2 = false;
                            obj.append(" k" + help + ".obj = " + ogTriple.getObject());
                        } else {
                            obj.append(" AND k" + help + ".obj = " + ogTriple.getObject());
                        }
                    }
                }
                where.append(sub);
                where.append(obj);
                help++;
            }
            for (int i = 0; i < rule.getBody().size(); ++i) {
                for (int j = i + 1; j < rule.getBody().size(); ++j) {
                    r1 = rule.getBody().get(i);
                    r2 = rule.getBody().get(j);
                    if (r1.getSubject() < 0) {
                        if (r1.getSubject() == r2.getSubject()) {
                            where.append(" AND k" + i + ".sub = k" + j + ".sub");
                        }
                        if (r1.getSubject() == r2.getObject()) {
                            where.append(" AND k" + i + ".sub = k" + j + ".obj");
                        }
                    }
                    if (r1.getObject() < 0) {
                        if (r1.getObject() == r2.getObject()) {
                            where.append(" AND k" + i + ".obj = k" + j + ".obj");
                        }
                        if (r1.getObject() == r2.getSubject()) {
                            where.append(" AND k" + i + ".obj = k" + j + ".sub");
                        }
                    }
                }
            }
            sql.append(select);
            sql.append(where);
            sql.append(sqlEnd);

            //System.out.println(sql.toString());
            String planningTime = "";
            String executionTime = "";
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                //System.out.println(rs.getString("QUERY PLAN"));
                if (rs.getString("QUERY PLAN").startsWith("Planning Time:")) {
                    planningTime = rs.getString("QUERY PLAN");
                }
                if ((rs.getString("QUERY PLAN").startsWith("Execution Time:"))) {
                    executionTime = rs.getString("QUERY PLAN");
                }
            }
            rs.close();
            //System.out.println(planningTime);
            //System.out.println(executionTime);
            Double pTime = Double.valueOf(planningTime.split(" ")[2]);
            Double execTime = Double.valueOf(executionTime.split(" ")[2]);
            //System.out.println(pTime);
            //System.out.println(execTime);
            //System.out.println("Time: " + (execTime + pTime) + " ms");
            /*if ((pTime + execTime) > 100) {
                System.out.println(ogTriple);
                System.out.println(rule);
                System.out.println(sql);
            }*/
            return pTime + execTime;
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.printMessage(e, e.getMessage());
        }
        return -1D;
    }


    public static void viewsForQuantiles(ArrayList<Rule> rules) {
        StringBuffer sql;
        Statement stmt;
        int count = 0;
        long elapsedTime;
        long startTime = System.nanoTime();
        try {
            stmt = con.createStatement();
            for (Rule rule : rules) {
                boolean subSet = false, objSet = false;
                sql = new StringBuffer("DROP MATERIALIZED VIEW IF EXISTS x" + rule.getId() + ";");
                stmt.addBatch(sql.toString());
                sql = new StringBuffer("CREATE MATERIALIZED VIEW x" + rule.getId() + " as ");
                Boolean first = true, first2 = true, first3 = true;
                StringBuffer preSelect = new StringBuffer("SELECT DISTINCT");
                StringBuffer select = new StringBuffer(" FROM ");
                int help = 0;
                StringBuffer sub, obj;
                StringBuffer where = new StringBuffer(" WHERE ");
                for (Triple triple : rule.getBody()) {
                    switch (Config.getStringValue("PREDICATE_VIEW")) {
                        case "viewsForPredicate":
                            if (triple.getSubject() >= 0) {
                                if (first) {
                                    select.append("r" + triple.getPredicate() + " k" + help);
                                    first = false;
                                } else {
                                    select.append(", " + "r" + triple.getPredicate() + " k" + help);
                                }
                            } else if (triple.getObject() >= 0) {
                                if (first) {
                                    select.append("l" + triple.getPredicate() + " k" + help);
                                    first = false;
                                } else {
                                    select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                }
                            } else if (triple.getSubject() == rule.getHead().getSubject() || triple.getSubject() == rule.getHead().getObject()) {
                                if (first) {
                                    select.append("l" + triple.getPredicate() + " k" + help);
                                    first = false;
                                } else {
                                    select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                }
                            } else if (triple.getObject() == rule.getHead().getSubject() || triple.getObject() == rule.getHead().getObject()) {
                                if (first) {
                                    select.append("r" + triple.getPredicate() + " k" + help);
                                    first = false;
                                } else {
                                    select.append(", " + "r" + triple.getPredicate() + " k" + help);
                                }
                            } else {
                                if (first) {
                                    select.append("l" + triple.getPredicate() + " k" + help);
                                    first = false;
                                } else {
                                    select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                }
                            }
                            break;
                        case "viewsForPredicateNoIndex":
                        case "viewsForPredicateSubObj":
                        case "viewsForPredicateObjSub":
                            if (first) {
                                select.append("v" + triple.getPredicate() + " k" + help);
                                first = false;
                            } else {
                                select.append(", " + "v" + triple.getPredicate() + " k" + help);
                            }
                            break;
                        case "viewsForPredicateHashIndex":
                            Debug.printMessage("viewsForPredicateHashIndex not implemented yet");
                            break;
                        default:
                    }
                    //Create WHERE Statements
                    sub = new StringBuffer();
                    if (triple.getSubject() < 0) {
                        if (triple.getSubject() == rule.getHead().getSubject()) {
                            if (first3) {
                                first3 = false;
                                preSelect.append(" k" + help + ".sub as sub");
                                subSet = true;
                            } else {
                                preSelect.append(", k" + help + ".sub as sub");
                                subSet = true;
                            }
                        } else if (triple.getSubject() == rule.getHead().getObject()) {
                            if (first3) {
                                first3 = false;
                                preSelect.append(" k" + help + ".sub as obj");
                                objSet = true;
                            } else {
                                preSelect.append(", k" + help + ".sub as obj");
                                objSet = true;
                            }

                        }
                        //Check if equal with head
                    } else {
                        if (first2) {
                            first2 = false;
                            sub.append(" k" + help + ".sub = " + triple.getSubject());
                        } else {
                            sub.append(" AND k" + help + ".sub = " + triple.getSubject());
                        }

                    }
                    //Create WHERE Statements
                    obj = new StringBuffer();
                    if (triple.getObject() >= 0) {
                        if (first2) {
                            first2 = false;
                            obj = new StringBuffer(" k" + help + ".obj = " + triple.getObject());
                        } else {
                            obj = new StringBuffer(" AND k" + help + ".obj = " + triple.getObject());
                        }
                    } else {
                        if (triple.getObject() == rule.getHead().getSubject()) {
                            if (first3) {
                                first3 = false;
                                preSelect.append(" k" + help + ".obj as sub");
                                subSet = true;
                            } else {
                                preSelect.append(", k" + help + ".obj as sub");
                                subSet = true;
                            }
                        } else if (triple.getObject() == rule.getHead().getObject()) {
                            if (first3) {
                                first3 = false;
                                preSelect.append(" k" + help + ".obj as obj");
                                objSet = true;
                            } else {
                                preSelect.append(", k" + help + ".obj as obj");
                                objSet = true;
                            }
                        }
                    }
                    where.append(sub);
                    where.append(obj);
                    help++;
                }
                Triple r1, r2;
                for (int i = 0; i < rule.getBody().size(); ++i) {
                    for (int j = i + 1; j < rule.getBody().size(); ++j) {
                        r1 = rule.getBody().get(i);
                        r2 = rule.getBody().get(j);
                        if (r1.getSubject() < 0) {
                            if (r1.getSubject() == r2.getSubject()) {
                                if (first2) {
                                    first2 = false;
                                    where.append(" k" + i + ".sub = k" + j + ".sub");
                                } else {
                                    where.append(" AND k" + i + ".sub = k" + j + ".sub");
                                }
                            }
                            if (r1.getSubject() == r2.getObject()) {
                                if (first2) {
                                    first2 = false;
                                    where.append(" k" + i + ".sub = k" + j + ".obj");
                                } else {
                                    where.append(" AND k" + i + ".sub = k" + j + ".obj");
                                }
                            }
                        }
                        if (r1.getObject() < 0) {
                            if (r1.getObject() == r2.getObject()) {
                                if (first2) {
                                    first2 = false;
                                    where.append(" k" + i + ".obj = k" + j + ".obj");
                                } else {
                                    where.append(" AND k" + i + ".obj = k" + j + ".obj");
                                }
                            }
                            if (r1.getObject() == r2.getSubject()) {
                                if (first2) {
                                    first2 = false;
                                    where.append(" k" + i + ".obj = k" + j + ".sub");
                                } else {
                                    where.append(" AND k" + i + ".obj = k" + j + ".sub");
                                }
                            }
                        }
                    }
                }
                preSelect.append(select);
                preSelect.append(where);
                sql.append(preSelect);
                System.out.println(rule);
                System.out.println(sql);
                stmt.addBatch(sql.toString());
                sql = new StringBuffer("CREATE UNIQUE INDEX x" + rule.getId() + "Index ON x" + rule.getId());
                if (subSet && objSet) {
                    sql.append("(sub, obj);");
                    rule.setBound(0);
                } else if (subSet && !objSet) {
                    sql.append("(sub);");
                    rule.setBound(1);
                } else if (!subSet && objSet) {
                    sql.append("(obj);");
                    rule.setBound(-1);
                } else {
                    Debug.printMessage("Error in creating indizes for Optimized Materialized Rules");
                }
                stmt.addBatch(sql.toString());
                sql = new StringBuffer("ALTER MATERIALIZED VIEW x" + rule.getId() + " CLUSTER ON x" + rule.getId() + "Index;");
                stmt.addBatch(sql.toString());
                count++;
                if (count % 2 == 0 || count == rules.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "xViews: Inserted " + count + " of " + rules.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                    con.commit();
                }
            }
            stmt.executeBatch();
            con.commit();
            stmt.close();
            System.out.println("Success");
            System.out.println("Normal Views have been generated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Integer> optimizedQuantileAnalysis (List<Rule> filteredRules, Triple ogTriple, HashMap<Integer, Integer> ruleHashMap){
        //long startTime1 = System.nanoTime();
        ArrayList<Integer> foundRules = new ArrayList<>();
        boolean first, first2;
        Triple r1, r2;

        ResultSet rs;
        try {
            Statement stmt = con.createStatement();
            StringBuffer sql, sqlEnd;
            sql = new StringBuffer();
            StringBuffer sub, obj;
            StringBuffer select, where;
            int iZahl;
            int help;
            boolean firstR = true;
            for (Rule rule : filteredRules) {
                if (rule.getBody().size() == 0) {
                    continue;
                }
                first = true;
                first2 = true;
                if (firstR) {
                    sql.append("(");
                    firstR = false;
                } else {
                    sql.append(" UNION ALL (");
                }
                if (rule.isLearned()) {
                    iZahl = rule.getBound();
                    StringBuffer sqlOpt = new StringBuffer();
                    if (iZahl == 0) {
                        sqlOpt.append("SELECT " + rule.getId() + " FROM x" + rule.getId() + " WHERE sub = " + ogTriple.getSubject() + " AND obj = " + ogTriple.getObject() + " LIMIT 1)");
                    } else if (iZahl == 1) {
                        sqlOpt.append("SELECT " + rule.getId() + " FROM x" + rule.getId() + " WHERE sub = " + ogTriple.getSubject() + " LIMIT 1)");
                    } else if (iZahl == -1){
                        sqlOpt.append("SELECT " + rule.getId() + " FROM x" + rule.getId() + " WHERE obj = " + ogTriple.getObject() + " LIMIT 1)");
                    }
                    sql.append(sqlOpt);
                    //System.out.println(sqlOpt);
                } else {
                    sqlEnd = new StringBuffer(" LIMIT 1) ");
                    select = new StringBuffer("SELECT " + rule.getId() + " FROM ");
                    where = new StringBuffer(" WHERE");
                    help = 0;
                    for (Triple triple : rule.getBody()) {
                        switch (Config.getStringValue("PREDICATE_VIEW")) {
                            case "viewsForPredicate":
                                if (triple.getSubject() >= 0) {
                                    if (first) {
                                        select.append("r" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "r" + triple.getPredicate() + " k" + help);
                                    }
                                } else if (triple.getObject() >= 0) {
                                    if (first) {
                                        select.append("l" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                    }
                                } else if (triple.getSubject() == rule.getHead().getSubject() || triple.getSubject() == rule.getHead().getObject()) {
                                    if (first) {
                                        select.append("l" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                    }
                                } else if (triple.getObject() == rule.getHead().getSubject() || triple.getObject() == rule.getHead().getObject()) {
                                    if (first) {
                                        select.append("r" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "r" + triple.getPredicate() + " k" + help);
                                    }
                                } else {
                                    if (first) {
                                        select.append("l" + triple.getPredicate() + " k" + help);
                                        first = false;
                                    } else {
                                        select.append(", " + "l" + triple.getPredicate() + " k" + help);
                                    }
                                }
                                break;
                            case "viewsForPredicateNoIndex":
                            case "viewsForPredicateSubObj":
                            case "viewsForPredicateObjSub":
                                if (first) {
                                    select.append("v" + triple.getPredicate() + " k" + help);
                                    first = false;
                                } else {
                                    select.append(", " + "v" + triple.getPredicate() + " k" + help);
                                }
                                break;
                            case "viewsForPredicateHashIndex":
                                Debug.printMessage("viewsForPredicateHashIndex not implemented yet");
                                break;
                            default:
                        }
                        //Create WHERE Statements
                        sub = new StringBuffer();
                        if (triple.getSubject() < 0) {
                            if (triple.getSubject() == rule.getHead().getSubject()) {
                                if (first2) {
                                    first2 = false;
                                    sub.append(" k" + help + ".sub = " + ogTriple.getSubject());
                                } else {
                                    sub.append(" AND k" + help + ".sub = " + ogTriple.getSubject());
                                }
                            } else if (triple.getSubject() == rule.getHead().getObject()) {
                                if (first2) {
                                    first2 = false;
                                    sub.append(" k" + help + ".sub = " + ogTriple.getObject());
                                } else {
                                    sub.append(" AND k" + help + ".sub = " + ogTriple.getObject());
                                }

                            }
                            //Check if equal with head
                        } else {
                            if (first2) {
                                first2 = false;
                                sub.append(" k" + help + ".sub = " + triple.getSubject());
                            } else {
                                sub.append(" AND k" + help + ".sub = " + triple.getSubject());
                            }

                        }
                        //Create WHERE Statements
                        obj = new StringBuffer();
                        if (triple.getObject() >= 0) {
                            if (first2) {
                                first2 = false;
                                obj = new StringBuffer(" k" + help + ".obj = " + triple.getObject());
                            } else {
                                obj = new StringBuffer(" AND k" + help + ".obj = " + triple.getObject());
                            }
                        } else {
                            if (triple.getObject() == rule.getHead().getSubject()) {
                                if (first2) {
                                    first2 = false;
                                    obj.append(" k" + help + ".obj = " + ogTriple.getSubject());
                                } else {
                                    obj.append(" AND k" + help + ".obj = " + ogTriple.getSubject());
                                }
                            } else if (triple.getObject() == rule.getHead().getObject()) {
                                if (first2) {
                                    first2 = false;
                                    obj.append(" k" + help + ".obj = " + ogTriple.getObject());
                                } else {
                                    obj.append(" AND k" + help + ".obj = " + ogTriple.getObject());
                                }
                            }
                        }
                        where.append(sub);
                        where.append(obj);
                        help++;
                    }
                    for (int i = 0; i < rule.getBody().size(); ++i) {
                        for (int j = i + 1; j < rule.getBody().size(); ++j) {
                            r1 = rule.getBody().get(i);
                            r2 = rule.getBody().get(j);
                            if (r1.getSubject() < 0) {
                                if (r1.getSubject() == r2.getSubject()) {
                                    if (first2) {
                                        first2 = false;
                                        where.append(" k" + i + ".sub = k" + j + ".sub");
                                    } else {
                                        where.append(" AND k" + i + ".sub = k" + j + ".sub");
                                    }

                                }
                                if (r1.getSubject() == r2.getObject()) {
                                    if (first2) {
                                        first2 = false;
                                        where.append(" k" + i + ".sub = k" + j + ".obj");
                                    } else {
                                        where.append(" AND k" + i + ".sub = k" + j + ".obj");
                                    }
                                }
                            }
                            if (r1.getObject() < 0) {
                                if (r1.getObject() == r2.getObject()) {
                                    if (first2) {
                                        first2 = false;
                                        where.append(" k" + i + ".obj = k" + j + ".obj");
                                    } else {
                                        where.append(" AND k" + i + ".obj = k" + j + ".obj");
                                    }

                                }
                                if (r1.getObject() == r2.getSubject()) {
                                    if (first2) {
                                        first2 = false;
                                        where.append(" k" + i + ".obj = k" + j + ".sub");
                                    } else {
                                        where.append(" AND k" + i + ".obj = k" + j + ".sub");
                                    }

                                }
                            }
                        }
                    }
                    sql.append(select);
                    sql.append(where);
                    sql.append(sqlEnd);
                }
            }
            //System.out.println(sql.toString());
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                if (rs.getString("?column?") != null) {
                    foundRules.add(rs.getInt("?column?"));
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.printMessage(e, e.getMessage());
        }
        return foundRules;
    }
    public static ArrayList<Integer> testRulesSimpleViews(List<Rule> filteredRules, Triple ogTriple) {
        int bodyCount;
        int ruleID;
        Triple head;
        StringBuffer sql = new StringBuffer();
        int headSub = ogTriple.getSubject(), headObj = ogTriple.getObject();
        Boolean firstTriple;
        Boolean firstRule = true;
        for (Rule rule : filteredRules) {
            //System.out.println(rule.toString());
            if (firstRule) {
                sql.append("(");
                firstRule = false;
            } else {
                sql.append(" UNION ALL (");
            }
            firstTriple = true;
            bodyCount = 1;
            ruleID = rule.getId();
            head = rule.getHead();
            sql.append("SELECT " + ruleID + " FROM o" + ruleID + " WHERE");

            for (Triple bodyTriple : rule.getBody()) {
                if (bodyTriple.getSubject() < 0) {
                    if (bodyTriple.getSubject() == head.getSubject()) {
                        if (firstTriple) {
                            firstTriple = false;
                            sql.append(" sub" + bodyCount + " = " + headSub);
                        } else {
                            sql.append(" AND sub" + bodyCount + " = " + headSub);
                        }
                    }
                    if (bodyTriple.getSubject() == head.getObject()) {
                        if (firstTriple) {
                            firstTriple = false;
                            sql.append(" sub" + bodyCount + " = " + headObj);
                        } else {
                            sql.append(" AND sub" + bodyCount + " = " + headObj);
                        }
                    }
                }
                if (bodyTriple.getObject() < 0) {
                    if (bodyTriple.getObject() == head.getSubject()) {
                        if (firstTriple) {
                            firstTriple = false;
                            sql.append(" obj" + bodyCount + " = " + headSub);
                        } else {
                            sql.append(" AND obj" + bodyCount + " = " + headSub);
                        }
                    }
                    if (bodyTriple.getObject() == head.getObject()) {
                        if (firstTriple) {
                            firstTriple = false;
                            sql.append(" obj" + bodyCount + " = " + headObj);
                        } else {
                            sql.append(" AND obj" + bodyCount + " = " + headObj);
                        }
                    }
                }
                bodyCount++;
            }
            sql.append(" LIMIT 1)");
        }


        try {
            //System.out.println(sql);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());
            ArrayList<Integer> ruleResult = new ArrayList<>();
            while (rs.next()) {
                if (rs.getString("?column?") != null) {
                    ruleResult.add(rs.getInt("?column?"));
                }
                //System.out.println("Found: ");
                //System.out.println(rs.getString("case"));
            }
            return ruleResult;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static StringBuffer createFunctionsBothUnbound(Rule rule) {
        StringBuffer sql = new StringBuffer();
        Triple r1, r2;
        StringBuffer where = new StringBuffer();
        Triple t2;
        Triple head = rule.getHead();
        Boolean first = true, first2 = true;
        int count = 0;
        sql.append("CREATE OR REPLACE FUNCTION f" + rule.getId() + " (IN subject INTEGER, IN object INTEGER, OUT iresult int)\n" +
                " RETURNS int AS $$\n" +
                "    BEGIN\n" +
                "    SELECT " + rule.getId() + " INTO iresult FROM ");
        for (Triple t : rule.getBody()) {
            if (first2) {
                sql.append("v" + t.getPredicate() + " k" + count);
                first2 = false;
            } else {
                sql.append(", v" + t.getPredicate() + " k" + count);
            }
            if (t.getSubject() >= 0) {
                if (first) {
                    where.append(" k" + count + ".sub = " + t.getSubject());
                    first = false;
                } else {
                    where.append(" AND k" + count + ".sub = " + t.getSubject());
                }
            } else {
                if (head.getSubject() == t.getSubject()) {
                    if (first) {
                        where.append(" k" + count + ".sub = subject");
                        first = false;
                    } else {
                        where.append(" AND k" + count + ".sub = subject");
                    }
                }
                if (head.getObject() == t.getSubject()) {
                    if (first) {
                        where.append(" k" + count + ".sub = object");
                        first = false;
                    } else {
                        where.append(" AND k" + count + ".sub = object");
                    }
                }
            }
            if (t.getObject() >= 0) {
                if (first) {
                    where.append(" k" + count + ".obj = " + t.getObject());
                    first = false;
                } else {
                    where.append(" AND k" + count + ".obj = " + t.getObject());
                }
            } else {
                if (head.getSubject() == t.getObject()) {
                    if (first) {
                        where.append(" k" + count + ".obj = subject");
                        first = false;
                    } else {
                        where.append(" AND k" + count + ".obj = subject");
                    }
                }
                if (head.getObject() == t.getObject()) {
                    if (first) {
                        where.append(" k" + count + ".obj = object");
                        first = false;
                    } else {
                        where.append(" AND k" + count + ".obj = object");
                    }
                }
            }
            for (int i = 0; i < rule.getBody().size(); ++i) {
                for (int j = i + 1; j < rule.getBody().size(); ++j) {
                    r1 = rule.getBody().get(i);
                    r2 = rule.getBody().get(j);
                    if (r1.getSubject() < 0) {
                        if (r1.getSubject() == r2.getSubject()) {
                            where.append(" AND k" + i + ".sub = k" + j + ".sub");
                        }
                        if (r1.getSubject() == r2.getObject()) {
                            where.append(" AND k" + i + ".sub = k" + j + ".obj");
                        }
                    }
                    if (r1.getObject() < 0) {
                        if (r1.getObject() == r2.getObject()) {
                            where.append(" AND k" + i + ".obj = k" + j + ".obj");
                        }
                        if (r1.getObject() == r2.getSubject()) {
                            where.append(" AND k" + i + ".obj = k" + j + ".sub");
                        }
                    }
                }
            }
            count++;
        }
        sql.append(" WHERE");
        sql.append(where);
        sql.append(" LIMIT 1; RETURN;\n" +
                "    END;\n" +
                "    $$ LANGUAGE 'plpgsql' PARALLEL SAFE;");
        //System.out.println(sql);
        return sql;
    }

    public static void createFunctions(RandomRules randomRules) {
        System.out.println("Create Functions started");
        ArrayList<Rule> ruleArrayList = new ArrayList<>();

        HashMap<Key2Int, ArrayList<Rule>> objBound = randomRules.getObjBound();
        HashMap<Key3Int, ArrayList<Rule>> bothBound = randomRules.getBothBound();
        HashMap<Integer, ArrayList<Rule>> noBoundEqual = randomRules.getNoBoundEqual();
        HashMap<Key2Int, ArrayList<Rule>> subBound = randomRules.getSubBound();
        HashMap<Integer, ArrayList<Rule>> noBoundUnequal = randomRules.getNoBoundUnequal();

        for (Map.Entry<Key2Int, ArrayList<Rule>> entry : objBound.entrySet()) {
            ruleArrayList.addAll(entry.getValue());
        }
        for (Map.Entry<Key2Int, ArrayList<Rule>> entry : subBound.entrySet()) {
            ruleArrayList.addAll(entry.getValue());
        }
        for (Map.Entry<Key3Int, ArrayList<Rule>> entry : bothBound.entrySet()) {
            ruleArrayList.addAll(entry.getValue());
        }
        for (Map.Entry<Integer, ArrayList<Rule>> entry : noBoundEqual.entrySet()) {
            ruleArrayList.addAll(entry.getValue());
        }
        for (Map.Entry<Integer, ArrayList<Rule>> entry : noBoundUnequal.entrySet()) {
            ruleArrayList.addAll(entry.getValue());
        }
        System.out.println("Rule length: " + ruleArrayList.size());
        try {
            Statement stmt = con.createStatement();
            long elapsedTime;
            long startTime = System.nanoTime();
            int count = 0;
            for (Rule rule : ruleArrayList) {
                stmt.addBatch(createFunctionsBothUnbound(rule).toString());
                if (count % 1000 == 0 || count == ruleArrayList.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted " + count + " of " + ruleArrayList.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                }
                count++;
            }
            stmt.executeBatch();
            con.commit();
            stmt.close();
            System.out.println("Success");
        } catch (SQLException e) {
            e.printStackTrace();
            Debug.printMessage(e.getMessage());
        }
    }

    public static ArrayList<Integer> testRulesFunction(List<Rule> rules, Triple triple) {
        StringBuffer sql = new StringBuffer();
        Boolean first = true;
        for (Rule rule : rules) {
            if (first) {
                first = false;
                sql.append("(SELECT f" + rule.getId() + "(" + triple.getSubject() + "," + triple.getObject() + ") AS res)");
            } else {
                sql.append(" UNION ALL (SELECT f" + rule.getId() + "(" + triple.getSubject() + "," + triple.getObject() + ") AS res)");
            }
        }
        //System.out.println(sql);
        ArrayList<Integer> foundRules = new ArrayList<>();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs;
            rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                if (rs.getString("res") != null) {
                    foundRules.add(rs.getInt("res"));
                }
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundRules;
    }


    /**
     * Function to create a materialized view for every Relation possible
     */
    public static void viewsForPredicate(List<Integer> predicateList) {
        try {
            String sql;
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            Statement stmt = con.createStatement();
            for(int i = 0; i < 200000; i++){
                sql = "DROP MATERIALIZED VIEW IF EXISTS x" + i +";";
                stmt.addBatch(sql);
                count++;
                if (count % 500 == 0){
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Dropped  Materialized View " + count + " of " + "200 000" + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                    con.commit();
                }
            }
            stmt.executeBatch();
            con.commit();
            count = 0;
            startTime = System.nanoTime();
            for (Integer integer : predicateList) {
                sql = "DROP MATERIALIZED VIEW IF EXISTS v" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "DROP MATERIALIZED VIEW IF EXISTS l" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "DROP MATERIALIZED VIEW IF EXISTS r" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "create materialized view  l" + integer.toString() + " as \n" +
                        "select sub, obj from " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " where pre = " + integer + ";";
                count++;
                stmt.addBatch(sql);
                sql = "CREATE UNIQUE INDEX l" + integer + "Index ON l" + integer + " (sub, obj);";
                stmt.addBatch(sql);
                sql = "ALTER MATERIALIZED VIEW l" + integer + " CLUSTER ON l" + integer + "Index;";
                stmt.addBatch(sql);
                //
                sql = "DROP MATERIALIZED VIEW IF EXISTS r" + integer + ";";
                stmt.addBatch(sql);
                sql = "create materialized view  r" + integer.toString() + " as \n" +
                        "select sub, obj from " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " where pre = " + integer + ";";
                count++;
                //System.out.println(triple.toString());
                stmt.addBatch(sql);
                sql = "CREATE UNIQUE INDEX r" + integer + "Index ON r" + integer + " (obj, sub);";
                stmt.addBatch(sql);
                sql = "ALTER MATERIALIZED VIEW r" + integer + " CLUSTER ON r" + integer + "Index;";
                stmt.addBatch(sql);
                //
                if (count % 5 == 0 || count == predicateList.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted " + count + " of " + predicateList.size()*2 + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
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

    public static void viewsForPredicateNoIndex(List<Integer> predicateList) {
        try {
            String sql;
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            Statement stmt = con.createStatement();
            for (Integer integer : predicateList) {
                sql = "DROP MATERIALIZED VIEW IF EXISTS v" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "DROP MATERIALIZED VIEW IF EXISTS l" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "DROP MATERIALIZED VIEW IF EXISTS r" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "create materialized view  v" + integer.toString() + " as \n" +
                        "select sub, obj from " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " where pre = " + integer + ";";
                stmt.addBatch(sql);
                count++;
                //System.out.println(triple.toString());
                if (count % 10 == 0 || count == predicateList.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted " + count + " of " + predicateList.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
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

    public static void viewsForPredicateSubObj(List<Integer> predicateList) {
        try {
            String sql;
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            Statement stmt = con.createStatement();
            for (Integer integer : predicateList) {
                sql = "DROP MATERIALIZED VIEW IF EXISTS v" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "DROP MATERIALIZED VIEW IF EXISTS l" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "DROP MATERIALIZED VIEW IF EXISTS r" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "create materialized view  v" + integer.toString() + " as \n" +
                        "select sub, obj from " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " where pre = " + integer + ";";
                count++;
                //System.out.println(triple.toString());
                stmt.addBatch(sql);
                sql = "CREATE UNIQUE INDEX v" + integer + "Index ON v" + integer + " (sub, obj);";
                stmt.addBatch(sql);
                sql = "ALTER MATERIALIZED VIEW v" + integer + " CLUSTER ON v" + integer + "Index;";
                stmt.addBatch(sql);
                if (count % 10 == 0 || count == predicateList.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted " + count + " of " + predicateList.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
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

    public static void viewsForPredicateObjSub(List<Integer> predicateList) {
        try {
            String sql;
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            Statement stmt = con.createStatement();
            for (Integer integer : predicateList) {
                sql = "DROP MATERIALIZED VIEW IF EXISTS v" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "DROP MATERIALIZED VIEW IF EXISTS l" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "DROP MATERIALIZED VIEW IF EXISTS r" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "create materialized view  v" + integer.toString() + " as \n" +
                        "select sub, obj from " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " where pre = " + integer + ";";
                count++;
                //System.out.println(triple.toString());
                stmt.addBatch(sql);
                sql = "CREATE UNIQUE INDEX v" + integer + "Index ON v" + integer + " (obj, sub);";
                stmt.addBatch(sql);
                sql = "ALTER MATERIALIZED VIEW v" + integer + " CLUSTER ON v" + integer + "Index;";
                stmt.addBatch(sql);
                if (count % 10 == 0 || count == predicateList.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted " + count + " of " + predicateList.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
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

    public static void viewsForPredicateHashIndex(List<Integer> predicateList) {
        try {
            String sql;
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            Statement stmt = con.createStatement();
            for (Integer integer : predicateList) {
                sql = "DROP MATERIALIZED VIEW IF EXISTS v" + integer.toString() + ";";
                stmt.addBatch(sql);
                sql = "create materialized view  v" + integer + " as \n" +
                        "select sub, obj from " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " where pre = " + integer + ";";
                count++;
                //System.out.println(triple.toString());
                stmt.addBatch(sql);
                sql = "CREATE UNIQUE INDEX v" + integer + "Index ON v" + integer + " (obj, sub);";
                stmt.addBatch(sql);
                sql = "ALTER MATERIALIZED VIEW v" + integer + " CLUSTER ON v" + integer + "Index;";
                stmt.addBatch(sql);
                if (count % 10 == 0 || count == predicateList.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "Inserted " + count + " of " + predicateList.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
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


    public static StringBuffer viewSqlStatement(ArrayList<Rule> rulesList) {
        boolean first, first2;
        StringBuffer sql, sqlEnd;
        sql = new StringBuffer();
        StringBuffer sub, pre, obj;
        StringBuffer select, where;
        int help;
        boolean firstR = true;
        for (Rule rule : rulesList) {
            first = true;
            first2 = true;
            if (firstR) {
                sql.append("(");
                firstR = false;
            } else {
                sql.append(" UNION ALL (");
            }
            sqlEnd = new StringBuffer(" ) "); //TODO Hash key hier rein
            select = new StringBuffer("SELECT * FROM ");
            where = new StringBuffer(" WHERE");
            help = 1;
            for (Triple triple : rule.getBody()) {
                //Create FROM statements
                if (first) {
                    select.append(Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " k" + help);
                    first = false;
                } else {
                    select.append(", " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " k" + help);
                }

                //Create WHERE Statements
                sub = new StringBuffer();
                if (triple.getSubject() < 0) {
                    if (triple.getObject() < 0 && triple.getObject() != triple.getSubject()) {
                        if (first2) {
                            first2 = false;
                            sub.append(" k" + help + ".sub != " + "k" + help + ".obj");
                        } else {
                            sub.append(" AND k" + help + ".sub != " + "k" + help + ".obj");
                        }
                    } else if (triple.getObject() == triple.getSubject()) {
                        if (first2) {
                            first2 = false;
                            sub.append(" k" + help + ".sub = " + "k" + help + ".obj");
                        } else {
                            sub.append(" AND k" + help + ".sub = " + "k" + help + ".obj");
                        }
                    }
                    //Check if equal with head
                } else {
                    if (first2) {
                        first2 = false;
                        sub.append(" k" + help + ".sub = " + triple.getSubject());
                    } else {
                        sub.append(" AND k" + help + ".sub = " + triple.getSubject());
                    }

                }
                pre = new StringBuffer();
                if (first2) {
                    first2 = false;
                    pre.append(" k" + help + ".pre = " + triple.getPredicate());
                } else {
                    pre.append(" AND k" + help + ".pre = " + triple.getPredicate());
                }
                //Create WHERE Statements
                obj = new StringBuffer();
                if (triple.getObject() >= 0) {
                    if (first2) {
                        first2 = false;
                        obj = new StringBuffer(" k" + help + ".obj = " + triple.getObject());
                    } else {
                        obj = new StringBuffer(" AND k" + help + ".obj = " + triple.getObject());
                    }
                }
                where.append(sub);
                where.append(pre);
                where.append(obj);
                help++;
            }
            sql.append(select);
            sql.append(where);
            sql.append(sqlEnd);
            System.out.println(rule);
        }
        System.out.println(sql.toString());
        return sql;
    }

    public static StringBuffer viewSqlStatementSingleRule(Rule rule) {
        boolean first, first2, first3;
        StringBuffer sql, sqlEnd;
        sql = new StringBuffer();
        StringBuffer sub, pre, obj;
        StringBuffer select, where;
        StringBuffer variables = new StringBuffer();
        StringBuffer from = new StringBuffer();
        int help;
        first = true;
        first2 = true;
        first3 = true;
        where = new StringBuffer(" WHERE");
        help = 1;
        select = new StringBuffer();
        for (Triple triple : rule.getBody()) {
            //Create FROM statements
            if (first) {
                from.append(Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " k" + help);
                first = false;
            } else {
                from.append(", " + Config.getStringValue("KNOWLEDGEGRAPH_TABLE") + " k" + help);
            }

            //Create WHERE Statements
            sub = new StringBuffer();
            if (triple.getSubject() < 0) {
                if (first3) {
                    variables.append("k" + help + ".sub AS sub" + help);
                    first3 = false;
                } else {
                    variables.append(", k" + help + ".sub AS sub" + help);
                }
                if (triple.getObject() < 0 && triple.getObject() != triple.getSubject()) {
                    if (first2) {
                        first2 = false;
                        sub.append(" k" + help + ".sub != " + "k" + help + ".obj");
                    } else {
                        sub.append(" AND k" + help + ".sub != " + "k" + help + ".obj");
                    }
                } else if (triple.getObject() == triple.getSubject()) {
                    if (first2) {
                        first2 = false;
                        sub.append(" k" + help + ".sub = " + "k" + help + ".obj");
                    } else {
                        sub.append(" AND k" + help + ".sub = " + "k" + help + ".obj");
                    }
                }
                //Check if equal with head
            } else {
                if (first2) {
                    first2 = false;
                    sub.append(" k" + help + ".sub = " + triple.getSubject());
                } else {
                    sub.append(" AND k" + help + ".sub = " + triple.getSubject());
                }

            }
            pre = new StringBuffer();
            if (first2) {
                first2 = false;
                pre.append(" k" + help + ".pre = " + triple.getPredicate());
            } else {
                pre.append(" AND k" + help + ".pre = " + triple.getPredicate());
            }
            //Create WHERE Statements
            obj = new StringBuffer();
            if (triple.getObject() >= 0) {
                if (first2) {
                    first2 = false;
                    obj = new StringBuffer(" k" + help + ".obj = " + triple.getObject());
                } else {
                    obj = new StringBuffer(" AND k" + help + ".obj = " + triple.getObject());
                }
            } else {
                if (first3) {
                    variables.append("k" + help + ".obj AS obj" + help);
                    first3 = false;
                } else {
                    variables.append(", k" + help + ".obj AS obj" + help);
                }
            }
            where.append(sub);
            where.append(pre);
            where.append(obj);
            help++;
        }
        select.append("SELECT " + variables + " FROM ");
        sql.append(select);
        sql.append(from);
        sql.append(where);
        //System.out.println(rule);
        //System.out.println(sql);
        return sql;
    }


    /**
     * Function to create a normal view for every rule.
     */
    public static void createNormalViewForRule(RandomRules randomRules) {
        try {
            String sql;
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            Statement stmt = con.createStatement();
            ArrayList<Rule> ruleArrayList = new ArrayList<>();

            HashMap<Key2Int, ArrayList<Rule>> objBound = randomRules.getObjBound();
            HashMap<Key3Int, ArrayList<Rule>> bothBound = randomRules.getBothBound();
            HashMap<Integer, ArrayList<Rule>> noBoundEqual = randomRules.getNoBoundEqual();
            HashMap<Key2Int, ArrayList<Rule>> subBound = randomRules.getSubBound();
            HashMap<Integer, ArrayList<Rule>> noBoundUnequal = randomRules.getNoBoundUnequal();

            for (Map.Entry<Key2Int, ArrayList<Rule>> entry : objBound.entrySet()) {
                ruleArrayList.addAll(entry.getValue());
            }
            for (Map.Entry<Key2Int, ArrayList<Rule>> entry : subBound.entrySet()) {
                ruleArrayList.addAll(entry.getValue());
            }
            for (Map.Entry<Key3Int, ArrayList<Rule>> entry : bothBound.entrySet()) {
                ruleArrayList.addAll(entry.getValue());
            }
            for (Map.Entry<Integer, ArrayList<Rule>> entry : noBoundEqual.entrySet()) {
                ruleArrayList.addAll(entry.getValue());
            }
            for (Map.Entry<Integer, ArrayList<Rule>> entry : noBoundUnequal.entrySet()) {
                ruleArrayList.addAll(entry.getValue());
            }

            for (Rule rule : ruleArrayList) {
                sql = "DROP VIEW IF EXISTS o" + rule.getId() + ";";
                stmt.addBatch(sql);
                sql = "CREATE VIEW o" + rule.getId() + " as " + viewSqlStatementSingleRule(rule);
                //System.out.println(rule);
                //System.out.println(sql);
                stmt.addBatch(sql);
                count++;
                if (count % 2000 == 0 || count == ruleArrayList.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "ObjBound: Inserted " + count + " of " + ruleArrayList.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                    con.commit();
                }
            }
            stmt.executeBatch();
            con.commit();
            stmt.close();
            System.out.println("Success");
            System.out.println("Normal Views have been generated");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to create a normal view for every rule.
     */
    public static void createNormalViewForRuleType(RandomRules randomRules) {
        try {
            String sql;
            long startTime = System.nanoTime();
            long elapsedTime;
            long count = 0;
            Statement stmt = con.createStatement();

            HashMap<Key2Int, ArrayList<Rule>> objBound = randomRules.getObjBound();
            HashMap<Key3Int, ArrayList<Rule>> bothBound = randomRules.getBothBound();
            HashMap<Integer, ArrayList<Rule>> noBoundEqual = randomRules.getNoBoundEqual();
            HashMap<Key2Int, ArrayList<Rule>> subBound = randomRules.getSubBound();
            HashMap<Integer, ArrayList<Rule>> noBoundUnequal = randomRules.getNoBoundUnequal();

            for (Map.Entry<Key2Int, ArrayList<Rule>> entry : objBound.entrySet()) {
                Key2Int key = entry.getKey();
                ArrayList<Rule> ruleList = entry.getValue();
                sql = "DROP VIEW IF EXISTS o" + key.toString() + ";";
                stmt.addBatch(sql);
                sql = "CREATE VIEW o" + key + " as " + viewSqlStatement(ruleList);
                //System.out.println(sql);
                stmt.addBatch(sql);
                count++;
                if (count % 500 == 0 || count == objBound.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "ObjBound: Inserted " + count + " of " + objBound.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                    con.commit();
                }
            }
            count = 0;
            for (Map.Entry<Key2Int, ArrayList<Rule>> entry : subBound.entrySet()) {
                Key2Int key = entry.getKey();
                ArrayList<Rule> ruleList = entry.getValue();
                //sql = "DROP VIEW IF EXISTS s" + key.hashCode() + ";";
                //stmt.addBatch(sql);
                sql = "CREATE VIEW s" + key + " as " + viewSqlStatement(ruleList);
                //System.out.println(sql);
                stmt.addBatch(sql);
                stmt.addBatch(sql);
                count++;
                if (count % 500 == 0 || count == subBound.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "SubBound: Inserted " + count + " of " + subBound.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                    con.commit();
                }
            }
            count = 0;
            for (Map.Entry<Key3Int, ArrayList<Rule>> entry : bothBound.entrySet()) {
                Key3Int key = entry.getKey();
                ArrayList<Rule> ruleList = entry.getValue();
                //sql = "DROP VIEW IF EXISTS b" + key.toString() + ";";
                //stmt.addBatch(sql);
                sql = "CREATE VIEW b" + key.toString() + " as " + viewSqlStatement(ruleList);
                //System.out.println(sql);
                stmt.addBatch(sql);
                count++;
                if (count % 500 == 0 || count == bothBound.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "BothBound: Inserted " + count + " of " + bothBound.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                    con.commit();
                }
            }
            count = 0;
            for (Map.Entry<Integer, ArrayList<Rule>> entry : noBoundEqual.entrySet()) {
                Integer key = entry.getKey();
                ArrayList<Rule> ruleList = entry.getValue();
                //sql = "DROP VIEW IF EXISTS e" + key.toString() + ";";
                //stmt.addBatch(sql);
                sql = "CREATE VIEW e" + key + " as " + viewSqlStatement(ruleList);
                //System.out.println(sql);
                stmt.addBatch(sql);
                count++;
                if (count % 500 == 0 || count == noBoundEqual.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "NoBoundEqual: Inserted " + count + " of " + noBoundEqual.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                    con.commit();
                }
            }
            count = 0;
            for (Map.Entry<Integer, ArrayList<Rule>> entry : noBoundUnequal.entrySet()) {
                Integer key = entry.getKey();
                ArrayList<Rule> ruleList = entry.getValue();
                //sql = "DROP VIEW IF EXISTS u" + key.toString() + ";";
                //stmt.addBatch(sql);
                sql = "CREATE VIEW u" + key + " as " + viewSqlStatement(ruleList);
                //System.out.println(sql);
                stmt.addBatch(sql);
                count++;
                if (count % 500 == 0 || count == noBoundUnequal.size()) {
                    stmt.executeBatch();
                    stmt.clearBatch();
                    elapsedTime = System.nanoTime() - startTime;
                    startTime = System.nanoTime();
                    System.out.println(
                            "NoBoundUnequal: Inserted " + count + " of " + noBoundUnequal.size() + " ; Time: " + (elapsedTime / 1000000)
                                    + "ms");
                    con.commit();
                }
            }
            stmt.executeBatch();
            con.commit();
            stmt.close();
            System.out.println("Success");
            System.out.println("Views have been inserted");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to create a view for every rule and store all possible triples that would fit the body
     * A lot of view (will probably be very ineffiecient)
     */
    public void viewsForRuleTypes(HashMap<Key2Int, ArrayList<Rule>> subBound, HashMap<Key2Int, ArrayList<Rule>> objBound, HashMap<Key3Int, ArrayList<Rule>> bothBound, HashMap<Integer, ArrayList<Rule>> noBoundUnequal, HashMap<Integer, ArrayList<Rule>> noBoundEqual) {
    }


}

