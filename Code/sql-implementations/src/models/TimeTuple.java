package models;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TimeTuple {
    private ArrayList<Integer> ruleList = new ArrayList<>();
    private long time;
    public TimeTuple(ArrayList<Integer> ruleList, long time){
        this.ruleList = ruleList;
        this.time = time;
    }
    public TimeTuple(ArrayList<Integer> ruleList){
        this.ruleList = ruleList;
    }

    public long getTime() {
        return time;
    }

    public ArrayList<Integer> getRuleList() {
        return ruleList;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
