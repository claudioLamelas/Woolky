package com.example.woolky.domain;

public class Statistics {

    private int totalWins;

    public Statistics() {}

    public Statistics(int totalWins) {
        this.totalWins = totalWins;
    }

    public int getTotalWins() {
        return totalWins;
    }

    public void setTotalWins(int totalWins) {
        this.totalWins = totalWins;
    }

    public void addOneWin() {
        this.totalWins++;
    }

}
