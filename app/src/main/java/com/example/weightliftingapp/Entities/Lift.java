package com.example.weightliftingapp.Entities;
import java.util.Date;

/**
 * Entity for single Lift
 */
public class Lift {
    public long itemID;

    private String liftName;
    private long weight;
    private long reps;
    private long sets;
    private long logTime;

    public Lift() {

    }

    public Lift(long itemID, String liftName, long weight, long reps, long sets, long logTime) {
        this.itemID = itemID;
        this.liftName = liftName;
        this.weight = weight;
        this.reps = reps;
        this.sets = sets;
        this.logTime = logTime;
    }

    public long getItemID() {
        return itemID;
    }

    public void setItemID(long itemID) {
        this.itemID = itemID;
    }

    public String getLiftName() {
        return liftName;
    }

    public void setLiftName(String liftName) {
        this.liftName = liftName;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public long getReps() {
        return reps;
    }

    public void setReps(long reps) {
        this.reps = reps;
    }

    public long getSets() {
        return sets;
    }

    public void setSets(long sets) {
        this.sets = sets;
    }

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }
}

