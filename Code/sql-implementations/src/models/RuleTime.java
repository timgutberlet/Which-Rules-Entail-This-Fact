package models;


import java.util.ArrayList;

public class RuleTime implements Comparable<RuleTime>{
    private ArrayList<Double> timeList = new ArrayList<>();
    private Rule rule;

    public RuleTime(Double time, Rule rule){
        timeList.add(time);
        this.rule = rule;
    }

    public int getRuleID() {
        return rule.getId();
    }

    public Rule getRule() {
        return rule;
    }

    public ArrayList<Double> getTimeList() {
        return timeList;
    }

    public int getCount() {
        return timeList.size();
    }

    public void addTime(Double time){
        timeList.add(time);
    }

    public double sum(){
        double sum = 0D;
        for (Double e : timeList){
            sum += e;
        }
        return sum;
    }

    public double max(){
        double max = 0D;
        for (Double e : timeList){
            if(max < e){
                max = e;
            }
        }
        return max;
    }

    @Override
    public int compareTo(RuleTime o) {
        if(this.sum() < o.sum()){
            return 1;
        }else if (this.sum() > o.sum()){
            return -1;
        }else {
            return 0;
        }
    }
}
