package com.example.weightliftingapp.Entities;

import java.util.List;

/**
 * List of lifts - used to store multiple (filtered) lifts to plot or show in ListView
 */
public class FilteredLifts {

    private List<Lift> Lifts;

    public FilteredLifts() {

    }

    public FilteredLifts(List<Lift> lifts) { this.Lifts = lifts; }

    public List<Lift> getLifts() {
        return Lifts;
    }

    public void setLifts(List<Lift> lifts) {
        this.Lifts = lifts;
    }
}

