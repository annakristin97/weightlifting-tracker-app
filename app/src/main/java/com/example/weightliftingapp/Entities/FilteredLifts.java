package com.example.weightliftingapp.Entities;

import java.util.List;

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

