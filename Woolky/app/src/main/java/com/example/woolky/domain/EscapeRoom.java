package com.example.woolky.domain;

import android.util.Pair;

import com.example.woolky.utils.PairCustom;
import com.example.woolky.utils.Triple;

import java.util.ArrayList;
import java.util.List;

public class EscapeRoom {

    private List<Triple<Integer, Integer, Integer>> linesCircles;
    private List<PairCustom<Double, Double>> circlesRelativePositions;

    public EscapeRoom() {
        linesCircles = new ArrayList<>();
        circlesRelativePositions = new ArrayList<>();
    }

    public EscapeRoom(List<Triple<Integer, Integer, Integer>> linesCircles, List<PairCustom<Double, Double>> circlesRelativePositions) {
        this.linesCircles = linesCircles;
        this.circlesRelativePositions = circlesRelativePositions;
    }


    public List<Triple<Integer, Integer, Integer>> getLinesCircles() {
        return linesCircles;
    }

    public void setLinesCircles(List<Triple<Integer, Integer, Integer>> linesCircles) {
        this.linesCircles = linesCircles;
    }

    public List<PairCustom<Double, Double>> getCirclesRelativePositions() {
        return circlesRelativePositions;
    }

    public void setCirclesRelativePositions(List<PairCustom<Double, Double>> circlesRelativePositions) {
        this.circlesRelativePositions = circlesRelativePositions;
    }
}
