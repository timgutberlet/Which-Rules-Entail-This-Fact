package utils;

import config.Config;
import config.Debug;
import database.DBFuncs;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import models.*;

/**
 * @author timgutberlet
 */
public class RandomRules {
    private HashMap<Integer, Integer> ruleHashMap = new HashMap<>();
    private HashMap<Integer, RuleTime> ruleTimeHashMap =  new HashMap<>();
    private HashMap<String, Integer> subjectIndex;
    private HashMap<String, Integer> predicateIndex;
    private HashMap<String, Integer> objectIndex;
    private HashMap<Key2Int, ArrayList<Rule>> subBound = new HashMap<>();
    private HashMap<Key2Int, ArrayList<Rule>> objBound = new HashMap<>();
    private HashMap<Key3Int, ArrayList<Rule>> bothBound = new HashMap<>();
    private HashMap<Integer, ArrayList<Rule>> noBoundUnequal = new HashMap<>();
    private HashMap<Integer, ArrayList<Rule>> noBoundEqual = new HashMap<>();

    public HashMap<Key2Int, ArrayList<Rule>> getObjBound() {
        return objBound;
    }

    public HashMap<Key3Int, ArrayList<Rule>> getBothBound() {
        return bothBound;
    }

    public HashMap<Key2Int, ArrayList<Rule>> getSubBound() {
        return subBound;
    }

    public HashMap<Integer, ArrayList<Rule>> getNoBoundUnequal() {
        return noBoundUnequal;
    }

    public HashMap<Integer, ArrayList<Rule>> getNoBoundEqual() {
        return noBoundEqual;
    }

    public RandomRules(HashMap<String, Integer> subjectIndex,
                       HashMap<String, Integer> predicateIndex, HashMap<String, Integer> objectIndex) {
        this.subjectIndex = subjectIndex;
        this.predicateIndex = predicateIndex;
        this.objectIndex = objectIndex;
        importRules();
    }

    public void importRules() {
        Key3Int key3Int;
        Key2Int key2Int;
        String file = Config.getStringValue("RULES_PATH");
        Rule rule;
        Integer counter = 0;
        Boolean continuer;
        ArrayList<String> filteredRulesStrings = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String first;
            String second;
            String[] help2, help1, help3;
            String headPredicate, headSubject, headObject;
            String bodyPredicate, bodySubject, bodyObject;
            int subjectID, objectID, predicateID;
            Triple head;
            List<Triple> body;
            Triple tripleHelp;
            int c = 0;
            int bodyCount = 0;
            for (String line; (line = reader.readLine()) != null; ) {
                body = new ArrayList<>();
                headSubject = "";
                headObject = "";
                // Get the left characters that are important
                help1 = line.split("<=");
                first = help1[0];
                help2 = first.split("\\s+");
                first = help2[3];
                headPredicate = first.split("\\(", 2)[0];
                if (help1.length > 1) {
                    second = help1[1].strip();
                    help3 = second.split(",\\s");
                    for (String triple : help3) {
                        bodyPredicate = triple.split("\\(", 2)[0];
                        help1 = triple.split("\\(", 2)[1].split(",", 2);
                        bodySubject = help1[0];
                        bodyObject = help1[1].substring(0, help1[1].length() - 1);
                        if (bodySubject.length() == 1) {
                            subjectID = Variables.getID(bodySubject);
                        } else {
                            if (subjectIndex.get(delE(bodySubject)) != null) {
                                subjectID = subjectIndex.get(delE(bodySubject));
                            } else {
                                subjectID = -99;
                            }
                        }
                        if (bodyObject.length() == 1) {
                            objectID = Variables.getID(bodyObject);
                        } else {
                            if (objectIndex.get(delE((bodyObject))) != null) {
                                objectID = objectIndex.get(delE(bodyObject));
                            } else {
                                objectID = -99;
                            }
                        }
                        if (predicateIndex.get(delR(bodyPredicate)) != null) {
                            predicateID = predicateIndex.get(delR(bodyPredicate));
                        } else {
                            predicateID = -99;
                        }
                        if (subjectID != -99 && predicateID != -99 && objectID != -99) {
                            tripleHelp = new Triple(subjectID, predicateID,
                                    objectID);
                            body.add(tripleHelp);
                        }
                        //tailList.add(new Triple(Integer.parseInt(bodySubject), Integer.parseInt(delE(bodyObject)), Integer.parseInt(bodyPredicate)));
                    }
                }
                //Uncomment this, if you also want to inlcude empty rules
        /*else {
          tripleHelp = new Triple(DBFuncs.getSubjectID(bodySubject), DBFuncs.getPredicateID(bodyPredicate),
              DBFuncs.getObjectID(bodyObject));
          body.add(tripleHelp);
          System.out.println(tripleHelp);
        }*/
                help1 = first.split("\\(", 2)[1].split(",", 2);
                if (help1.length > 1) {
                    headSubject = help1[0];
                    headObject = help1[1].substring(0, help1[1].length() - 1);
                }
                //System.out.println(headPredicate + " : " + headSubject + " " + headObject + " <= " + second);
                if (headSubject.length() == 1) {

                }
                if (headSubject.length() == 1) {
                    subjectID = Variables.getID(headSubject);
                } else {
                    if (subjectIndex.get(delE(headSubject)) != null) {
                        subjectID = subjectIndex.get(delE(headSubject));
                    } else {
                        subjectID = -99;
                    }
                }
                if (headObject.length() == 1) {
                    objectID = Variables.getID(headObject);
                } else {
                    if (objectIndex.get(delE(headObject)) != null) {
                        objectID = objectIndex.get(delE(headObject));
                    } else {
                        objectID = -99;
                    }
                }
                if (predicateIndex.get(delR(headPredicate)) != null) {
                    predicateID = predicateIndex.get(delR(headPredicate));
                } else {
                    predicateID = -99;
                }
                if (subjectID != -99 && predicateID != -99 && objectID != -99) {
                    head = new Triple(subjectID, predicateID, objectID);
                    rule = new Rule(head, body);
                } else {
                    continue;
                }


                if (Config.getStringValue("FILTER_SIMPLE_RULES").equals("YES")) {
                    continuer = false;

                    if ( rule.getBody().size() == 4) {
                            continuer = true;
                            System.out.println(c++ + " Type2: " + rule);
                    }

                    /*if (rule.getHead().getSubject() < 0
                            && rule.getHead().getObject() < 0
                            && rule.getBody().size() == 1) {
                        if (rule.getHead().getSubject() == rule.getBody().get(0).getSubject()
                                && rule.getHead().getObject() == rule.getBody().get(0).getObject()) {
                            continuer = true;
                            System.out.println(c++ + " Type1: " + rule);
                        }
                    } else if (rule.getHead().getSubject() < 0
                            && rule.getHead().getObject() < 0
                            && rule.getBody().size() == 2) {
                        if (rule.getHead().getSubject() == rule.getBody().get(0).getSubject()
                                && rule.getHead().getObject() == rule.getBody().get(1).getObject()
                                && rule.getBody().get(0).getObject() == rule.getBody().get(1).getSubject()) {
                            continuer = true;
                            System.out.println(c++ + " Type2: " + rule);
                        }
                    } else if (rule.getHead().getSubject() < 0
                            && rule.getHead().getObject() >= 0
                            && rule.getBody().size() == 1) {
                        if (rule.getBody().get(0).getSubject() == rule.getHead().getSubject()
                                && rule.getBody().get(0).getObject() >= 0) {
                            continuer = true;
                            System.out.println(c++ + " Type3: " + rule);
                        }
                    }*/

                    System.out.println(bodyCount++);

                    if (!continuer) {
                        continue;
                    } else {
                        filteredRulesStrings.add(line);
                    }
                }
                if (rule.getBody().size() == 0) {
                    continue;
                }

                //Beide ungebunden & ungleich
                if (rule.getHead().getObject() < 0 && rule.getHead().getSubject() < 0 && rule.getHead().getObject() != rule.getHead().getSubject()) {
                    if (!noBoundUnequal.containsKey(rule.getHead().getPredicate())) {
                        noBoundUnequal.put(rule.getHead().getPredicate(), new ArrayList<>());
                    }
                    noBoundUnequal.get(rule.getHead().getPredicate()).add(rule);
                }
                //Beide ungebunden und gleich
                if (rule.getHead().getObject() < 0 && rule.getHead().getSubject() < 0 && rule.getHead().getObject() == rule.getHead().getSubject()) {
                    if (!noBoundEqual.containsKey(rule.getHead().getPredicate())) {
                        noBoundEqual.put(rule.getHead().getPredicate(), new ArrayList<>());
                    }
                    noBoundEqual.get(rule.getHead().getPredicate()).add(rule);
                }
                // Beide Gebunden
                if (rule.getHead().getSubject() >= 0 && rule.getHead().getObject() >= 0) {
                    key3Int = new Key3Int(rule.getHead().getSubject(), rule.getHead().getPredicate(), rule.getHead().getObject());
                    if (!bothBound.containsKey(key3Int)) {
                        bothBound.put(key3Int, new ArrayList<>());
                    }
                    bothBound.get(key3Int).add(rule);
                }

                // Subject Gebunden, Object Frei
                if (rule.getHead().getSubject() >= 0 && rule.getHead().getObject() < 0) {
                    key2Int = new Key2Int(rule.getHead().getSubject(), rule.getHead().getPredicate());
                    if (!subBound.containsKey(key2Int)) {
                        subBound.put(key2Int, new ArrayList<>());
                    }
                    subBound.get(key2Int).add(rule);
                }

                // Object gebunden, Subject Frei
                if (rule.getHead().getObject() >= 0 && rule.getHead().getSubject() < 0) {
                    key2Int = new Key2Int(rule.getHead().getPredicate(), rule.getHead().getObject());
                    if (!objBound.containsKey(key2Int)) {
                        objBound.put(key2Int, new ArrayList<>());
                    }
                    objBound.get(key2Int).add(rule);
                }

                rule.setId(counter++);
                System.out.println(counter);

                //ruleList.add(new Rule(head, body));
            }
            System.out.println("Import finished");
            reader.close();
            if (Config.getStringValue("TESTRULES_METHOD").equals("testRulesSimpleViews") && Config.getStringValue("REFILL_TABLES").equals("YES")) {
                DBFuncs.createNormalViewForRule(this);
            }
            System.out.println("Check for Create Functions");
            if (Config.getStringValue("TESTRULES_METHOD").equals("testRulesFunction") && Config.getStringValue("REFILL_TABLES").equals("YES")) {
                DBFuncs.createFunctions(this);
            }
            System.out.println("Executed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Used for deleting all 'e's at the start of an entity, as entities from the ruleset start with
     * an unnessecary 'e', when they are given from AnyBURL
     *
     * @param string
     * @return returns the given String without the e at the start
     */
    public static String delE(String string) {
        if (string.startsWith("e")) {
            try {
                string = string.substring(1, string.length());
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
                System.out.println(string);
                e.printStackTrace();
                System.exit(0);
            }
        }
        return string;
    }

    public static String delR(String string) {
        if (string.startsWith("r")) {
            try {
                string = string.substring(1, string.length());
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println(e.getMessage());
                System.out.println(string);
                e.printStackTrace();
                System.exit(0);
            }
        }
        return string;
    }

    public ArrayList<Integer> searchByTriple(Triple triple) {

        Integer key;
        Key3Int key3Int;
        Key2Int key2IntSub, key2IntObj;
        ArrayList<Rule> ruleSet;

        List<Rule> filteredRules = new ArrayList<>();
        key = triple.getPredicate();
        key2IntSub = new Key2Int(triple.getSubject(), triple.getPredicate());
        key3Int = new Key3Int(triple.getSubject(), triple.getPredicate(), triple.getObject());
        key2IntObj = new Key2Int(triple.getPredicate(), triple.getObject());
        if (triple.getSubject() == triple.getObject()) {
            ruleSet = noBoundEqual.get(key);
            if (ruleSet != null) {
                filteredRules.addAll(ruleSet);
            }
        } else {
            ruleSet = noBoundUnequal.get(key.hashCode());
            if (ruleSet != null) {
                filteredRules.addAll(ruleSet);
            }
        }
        ruleSet = bothBound.get(key3Int.hashCode());
        if (ruleSet != null) {
            filteredRules.addAll(ruleSet);
        }

        ruleSet = subBound.get(key2IntSub.hashCode());
        if (ruleSet != null) {
            filteredRules.addAll(ruleSet);
        }
        ruleSet = objBound.get(key2IntObj);
        if (ruleSet != null) {
            filteredRules.addAll(ruleSet);
        }
        ArrayList<Integer> resultList;
        switch (Config.getStringValue("TESTRULES_METHOD")) {
            case "testRulesUnionAllShorterSelect":
                resultList = DBFuncs.testRulesUnionAllShorterSelect(filteredRules, triple);
                break;
            case "testRulesUnionAllShorterSelectViewsForRelations":
                resultList = DBFuncs.testRulesUnionAllShorterSelectViewsForRelations(filteredRules, triple);
                break;
            case "testRulesSimpleViews":
                resultList = DBFuncs.testRulesSimpleViews(filteredRules, triple);
                break;
            case "testRulesFunction":
                resultList = DBFuncs.testRulesFunction(filteredRules, triple);
                break;
            case "optimizedQuantileAnalysis":
                resultList = DBFuncs.optimizedQuantileAnalysis(filteredRules, triple, ruleHashMap);
                break;
            default:
                resultList = null;
                Debug.printMessage("Keine Methode ausgewählt");

        }
        return resultList;
    }

    public static void main(String[] args) {
        System.out.println(isFloat(3.5));
        System.out.println(isFloat(3L));
        System.out.println(isFloat(0.5*4));
        ArrayList<Long> timeList = new ArrayList<>();
        timeList.add(3L);
        timeList.add(3L);
        timeList.add(1L);
        timeList.add(2L);
        timeList.add(5L);
        timeList.add(7L);
        timeList.add(8L);
        timeList.add(9L);
        quantilCalc(timeList);
        timeList = new ArrayList<>();
        timeList.add(1L);
        timeList.add(1L);
        timeList.add(2L);
        timeList.add(4L);
        timeList.add(4L);
        timeList.add(7L);
        timeList.add(8L);
        quantilCalc(timeList);
    }

    public List<Triple> importQueryTriples() {
        String file = Config.getStringValue("QUERYTRIPLES");
        List<Triple> tripleList = new ArrayList<>();
        String[] importList;
        int sub, pre, obj;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            for (String line; (line = reader.readLine()) != null; ) {
                importList = line.split("\\s+");
                if (importList.length == 3) {
                    //System.out.println(Arrays.toString(importList));
                    if (Config.getStringValue("QUERYTRIPLESFORMAT").equals("TEXT")) {
                        if (subjectIndex.get(importList[0]) == null) {
                            //System.out.println("Added " + importList[0]);
                            sub = subjectIndex.size() + 1;
                            subjectIndex.put(importList[0], sub);
                        } else {
                            sub = subjectIndex.get(importList[0]);
                        }
                        if (predicateIndex.get(importList[1]) == null) {
                            //System.out.println("Added " + importList[1]);
                            pre = predicateIndex.size() + 1;
                            predicateIndex.put(importList[1], sub);
                        } else {
                            pre = predicateIndex.get(importList[1]);
                        }
                        if (objectIndex.get(importList[2]) == null) {
                            //System.out.println("Added " + importList[2]);
                            obj = objectIndex.size() + 1;
                            objectIndex.put(importList[2], sub);
                        } else {
                            obj = objectIndex.get(importList[2]);
                        }
                    } else {
                        sub = Integer.parseInt(importList[0]);
                        pre = Integer.parseInt(importList[1]);
                        obj = Integer.parseInt(importList[2]);
                    }
                    tripleList.add(new Triple(sub, pre, obj));

                } else {
                    System.out.println("Error while reading QueryTriples");
                }
            }
            //System.out.println("Import finished");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tripleList;
    }


    /**
     * 1. Variante - alle Abfragen befinden sich
     * Used for getting the average time for a query to find a fitting rule. This will create random
     * rules out of all possible subjects, predicates and objects
     */
    public void startQuery() {
        List<Triple> queryTriples = importQueryTriples();
        System.out.println("Testing Method: " + Config.getStringValue("TESTRULES_METHOD"));
        HashMap<Triple, TimeTuple> resultMap = new HashMap<>();
        //rules.forEach(rule -> System.out.println(rule));
        long queries = 0;
        long startTime = System.nanoTime();
        long elapsedTime;
        long startTime2 = System.nanoTime();
        long elapsedTime2;
        for (Triple triple : queryTriples) {
            startTime = System.nanoTime();
            resultMap.put(triple, new TimeTuple(searchByTriple(triple)));
            elapsedTime = System.nanoTime() - startTime;
            resultMap.get(triple).setTime(elapsedTime);
            queries++;
            //System.out.println(triple);
            //resultMap.get(triple).getRuleList().forEach(e -> System.out.println(e.toString()));
            //System.out.println(elapsedTime / 1000000);
            if (queries % 10 == 0) {
                elapsedTime2 = System.nanoTime();
                System.out.println("Gesamtzeit: " + ((elapsedTime2 - startTime2) / 1000000) + " ms");
                System.out.println("Durchschnittszeit: " + (((elapsedTime2 - startTime2) / 1000000) / queries) + " ms");
                System.out.println("Abfragen: " + queries);
            }
        }
        elapsedTime = System.nanoTime();
        System.out.println("Gesamtzeit: " + ((elapsedTime - startTime) / 1000000) + " ms");
        System.out.println("Durchschnittszeit: " + (((elapsedTime - startTime) / 1000000) / queries) + " ms");
        System.out.println("Abfragen: " + queries);
        ArrayList<Long> timeList = new ArrayList<>();
        for(TimeTuple timeTuple : resultMap.values()){
            timeList.add(timeTuple.getTime());
        }
        quantilCalc(timeList);

    }
    public static void quantilCalc(ArrayList<Long> timeList){
        Collections.sort(timeList);
        timeList.forEach(aLong -> System.out.println(aLong));
        double help;
        double quantile = 0.05;
        for(double i = quantile; i <= 1; i += quantile){
            if(isFloat(i * timeList.size())){
                help = timeList.get((int) Math.floor(i * timeList.size()));
            }else {
                help = 0.5 * ((timeList.get((int)(timeList.size() * i)-1)) + timeList.get((int)(timeList.size() * i)));

            }
            System.out.println((double) Math.round(i * 100)/100 + "% : " + (help/1000000) + " ms");
        }

        Long average, sum = 0L;
        Long max = 0L, min = Long.MAX_VALUE;
        for(long l : timeList){
            if(l > max){
                max = l;
            }
            if(l < min){
                min = l;
            }
            sum += l;
        }
        average = sum / timeList.size();
        System.out.println("Average: " + ((double)average / 1000000D) + " ms");
        System.out.println("Min: " + ((double)min / 1000000D) + " ms");
        System.out.println("Max: " + ((double)max / 1000000D) + " ms");
    }
    public static void quantilCalcSum(ArrayList<Long> timeList){
        Collections.sort(timeList);
        timeList.forEach(aLong -> System.out.println(aLong));
        double help;
        double quantile = 0.05;
        for(double i = quantile; i <= 1; i += quantile){
            if(isFloat(i * timeList.size())){
                help = timeList.get((int) Math.floor(i * timeList.size()));
            }else {
                help = 0.5 * ((timeList.get((int)(timeList.size() * i)-1)) + timeList.get((int)(timeList.size() * i)));

            }
            System.out.println((double) Math.round(i * 100)/100 + "% : " + (help) + " ms");
        }

        Long average, sum = 0L;
        Long max = 0L, min = Long.MAX_VALUE;
        for(long l : timeList){
            if(l > max){
                max = l;
            }
            if(l < min){
                min = l;
            }
            sum += l;
        }
        average = sum / timeList.size();
        System.out.println("Average: " + ((double)average ) + " ms");
        System.out.println("Min: " + ((double)min ) + " ms");
        System.out.println("Max: " + ((double)max ) + " ms");
        System.exit(0);
    }
    public static boolean isFloat(double value) {
        int dec = (int)value;
        if(value - dec != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Zweite Variante wo alle Abfragen in einem SQL Statement / wenigen zusammengefasst werden
     */
    public void learnQuery(ArrayList<Triple> queryTriples) {
        long queries = 0;
        long startTime2 = System.nanoTime();
        long elapsedTime2;
        for (Triple triple : queryTriples) {
            learnQuerySearch(triple);
            queries++;
            if (queries % 10 == 0) {
                elapsedTime2 = System.nanoTime();
                System.out.println("Gesamtzeit: " + ((elapsedTime2 - startTime2) / 1000000) + " ms");
                System.out.println("Durchschnittszeit: " + (((elapsedTime2 - startTime2) / 1000000) / queries) + " ms");
                System.out.println("Abfragen: " + queries);
            }
        }
        quantilOptimize();
    }

    public void learnQuerySearch(Triple triple) {
        Integer key;
        Key3Int key3Int;
        Key2Int key2IntSub, key2IntObj;
        ArrayList<Rule> ruleSet;
        List<Rule> filteredRules = new ArrayList<>();
        key = triple.getPredicate();
        key2IntSub = new Key2Int(triple.getSubject(), triple.getPredicate());
        key3Int = new Key3Int(triple.getSubject(), triple.getPredicate(), triple.getObject());
        key2IntObj = new Key2Int(triple.getPredicate(), triple.getObject());
        if (triple.getSubject() == triple.getObject()) {
            ruleSet = noBoundEqual.get(key);
            if (ruleSet != null) {
                filteredRules.addAll(ruleSet);
            }
        } else {
            ruleSet = noBoundUnequal.get(key.hashCode());
            if (ruleSet != null) {
                filteredRules.addAll(ruleSet);
            }
        }
        ruleSet = bothBound.get(key3Int.hashCode());
        if (ruleSet != null) {
            filteredRules.addAll(ruleSet);
        }

        ruleSet = subBound.get(key2IntSub.hashCode());
        if (ruleSet != null) {
            filteredRules.addAll(ruleSet);
        }
        ruleSet = objBound.get(key2IntObj);
        if (ruleSet != null) {
            filteredRules.addAll(ruleSet);
        }
        for(Rule rule :  filteredRules){
            if(ruleTimeHashMap.containsKey(rule.getId())){
                ruleTimeHashMap.get(rule.getId()).addTime(DBFuncs.timePerRule(rule, triple));
            }else {
                ruleTimeHashMap.put(rule.getId(), new RuleTime(DBFuncs.timePerRule(rule, triple) ,rule));
            }
        }
    }

    public void quantilOptimize(){
        ArrayList<RuleTime> helpList = new ArrayList<>();
        ArrayList<Rule> ruleList = new ArrayList<>();
        ArrayList<Long> timeList = new ArrayList<>();
        for (Map.Entry<Integer, RuleTime> entry : ruleTimeHashMap.entrySet()){
            helpList.add(entry.getValue());
            timeList.add((long)(entry.getValue().sum()));
        }
        Collections.sort(helpList);
        int i = 0;
        int count = 0;
        for(RuleTime ruleTime  : helpList){
            System.out.println(" ");
            System.out.println(ruleTime.avg());
            System.out.println(ruleTime.sum());
            System.out.println(ruleTime.getCount());
            System.out.println(ruleTime.getRule());
            count++;
            if(count % Config.getIntValue("QUANTIL_LEARN_COUNT") == 0){
                System.out.println("Learned: " + i);
                System.out.println("Count: " + count);
                //System.exit(0);
            }
            if(ruleTime.getRule().getBody().size() == 2) {
                ruleList.add(ruleTime.getRule());
                i++;
            }
            if (i ==Config.getIntValue("QUANTIL_LEARN_COUNT")){
                break;
            }
        }
        quantilCalcSum(timeList);
        DBFuncs.viewsForQuantiles(ruleList);
        System.out.println("RuleList");
        for(Rule rule : ruleList){
            rule.setLearned();
            System.out.println(rule);
            System.out.println(rule.getBound());
            System.out.println(rule.isLearned());
        }
        rulePreSave(ruleList);
    }
    public void learnRules(){
        HashMap<Integer, Rule> ruleMap = new HashMap<>();
        for (Map.Entry<Key2Int, ArrayList<Rule>> entry : objBound.entrySet()) {
            for(Rule r: entry.getValue()){
                ruleMap.put(r.getId(), r);
            }
        }
        for (Map.Entry<Key2Int, ArrayList<Rule>> entry : subBound.entrySet()) {
            for(Rule r: entry.getValue()){
                ruleMap.put(r.getId(), r);
            }
        }
        for (Map.Entry<Key3Int, ArrayList<Rule>> entry : bothBound.entrySet()) {
            for(Rule r: entry.getValue()){
                ruleMap.put(r.getId(), r);
            }
        }
        for (Map.Entry<Integer, ArrayList<Rule>> entry : noBoundEqual.entrySet()) {
            for(Rule r: entry.getValue()){
                ruleMap.put(r.getId(), r);
            }
        }
        for (Map.Entry<Integer, ArrayList<Rule>> entry : noBoundUnequal.entrySet()) {
            for(Rule r: entry.getValue()){
                ruleMap.put(r.getId(), r);
            }
        }
        rulePreRead(ruleMap);
    }
    public void rulePreSave(ArrayList<Rule> rules){
        String fileString = Config.getStringValue("RULEPRESAVE");
        File file = new File(fileString);
        FileWriter fr = null;
        BufferedWriter br = null;
        try{
            fr = new FileWriter(file);
            br = new BufferedWriter(fr);
            for(Rule r : rules){
                String dataWithNewLine=r.getId() +" " + r.getBound() +System.getProperty("line.separator");
                br.write(dataWithNewLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    public void rulePreRead(HashMap<Integer, Rule> ruleMap){
        String file = Config.getStringValue("RULEPRESAVE");
        String[] importList;
        int ruleID, bound;
        Rule r;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            for (String line; (line = reader.readLine()) != null; ) {
                importList = line.split("\\s+");
                if (importList.length == 2) {
                    ruleID = Integer.valueOf(importList[0]);
                    bound = Integer.valueOf(importList[1]);
                    if(ruleMap.containsKey(ruleID)){
                        r = ruleMap.get(ruleID);
                        r.setLearned();
                        r.setBound(bound);
                    }
                } else {
                    System.out.println("Error while reading QueryTriples");
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
