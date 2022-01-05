package com.example.woolky.domain.escaperooms;

import android.graphics.Color;
import android.util.Pair;

import com.example.woolky.utils.LocationCalculator;
import com.example.woolky.utils.PairCustom;
import com.example.woolky.utils.Triple;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class EscapeRoom {

    private List<Triple<Integer, Integer, Integer>> linesCircles;
    private List<PairCustom<Double, Double>> circlesRelativePositions;
    private List<Quiz> quizzes;

    private List<Circle> vertex;

    public EscapeRoom() {
        linesCircles = new ArrayList<>();
        circlesRelativePositions = new ArrayList<>();
        quizzes = new ArrayList<>();
        vertex = new ArrayList<>();
    }

    public EscapeRoom(List<Triple<Integer, Integer, Integer>> linesCircles,
                      List<PairCustom<Double, Double>> circlesRelativePositions,
                      List<Quiz> quizzes) {
        this.linesCircles = linesCircles;
        this.circlesRelativePositions = circlesRelativePositions;
        this.quizzes = quizzes;

        this.vertex = new ArrayList<>();
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

    public List<Quiz> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quiz> quizzes) {
        this.quizzes = quizzes;
    }

    public void drawEscapeRoom(LatLng initialPosition, GoogleMap mMap) {
        List<LatLng> circlePositions = LocationCalculator.calculatePositions(initialPosition,
                circlesRelativePositions);

        for (LatLng p : circlePositions) {
            Circle c = mMap.addCircle(new CircleOptions().center(p).radius(6)
                    .fillColor(Color.BLACK).clickable(true));
            vertex.add(c);
        }

        for (Triple<Integer, Integer, Integer> t : linesCircles) {
            Polyline p = mMap.addPolyline(new PolylineOptions().add(vertex.get(t.getFirst()).getCenter(),
                    vertex.get(t.getSecond()).getCenter()).clickable(true).color(t.getThird()));

            //polylines.add(p);
        }
    }
}
