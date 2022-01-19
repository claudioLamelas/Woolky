package com.example.woolky.domain.games.escaperooms;

import android.graphics.Color;

import com.example.woolky.utils.LocationCalculator;
import com.example.woolky.utils.PairCustom;
import com.example.woolky.utils.Triple;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EscapeRoom {

    private String name;
    private PairCustom<Double, Double> userStartPosition;
    private List<Triple<Integer, Integer, Integer>> linesCircles;
    private List<PairCustom<Double, Double>> circlesRelativePositions;
    private List<Quiz> quizzes;

    private List<Circle> vertex;
    private List<Polyline> polylines;

    public EscapeRoom() {
        name = "";
        linesCircles = new ArrayList<>();
        circlesRelativePositions = new ArrayList<>();
        quizzes = new ArrayList<>();
        vertex = new ArrayList<>();
        polylines = new ArrayList<>();
        userStartPosition = null;
    }

    public EscapeRoom(List<Triple<Integer, Integer, Integer>> linesCircles,
                      List<PairCustom<Double, Double>> circlesRelativePositions,
                      List<Quiz> quizzes) {
        this.name = "";
        this.linesCircles = linesCircles;
        this.circlesRelativePositions = circlesRelativePositions;
        this.quizzes = quizzes;

        this.vertex = new ArrayList<>();
        this.polylines = new ArrayList<>();
        this.userStartPosition = null;
    }

    public void drawEscapeRoom(LatLng initialPosition, GoogleMap mMap, double vertexRadius) {
        vertex.clear();
        polylines.clear();

        List<LatLng> circlePositions = LocationCalculator.calculatePositions(initialPosition,
                circlesRelativePositions);

        for (LatLng p : circlePositions) {
            Circle c = mMap.addCircle(new CircleOptions().center(p).radius(vertexRadius)
                    .fillColor(Color.BLACK).clickable(true));
            vertex.add(c);
        }



        for (Triple<Integer, Integer, Integer> t : linesCircles) {
            Polyline p = mMap.addPolyline(new PolylineOptions().add(vertex.get(t.getFirst()).getCenter(),
                    vertex.get(t.getSecond()).getCenter()).clickable(true).color(t.getThird()));

            polylines.add(p);
        }
    }

    @Exclude
    public Triple<Integer, Integer, Integer> getBlueLine() {
        for (Triple<Integer, Integer, Integer> triple : linesCircles) {
            if (triple.getThird() == Color.BLUE) {
                return triple;
            }
        }
        return null;
    }

    public void removeFromMap(GoogleMap mMap) {
        for (Circle c : vertex) {
            c.remove();
        }

        for (Polyline p : polylines){
            p.remove();
        }
    }

    @Exclude
    public List<LatLng> getVertexPosition() {
        List<LatLng> list = new ArrayList<>();
        for (Circle circle : vertex) {
            LatLng center = circle.getCenter();
            list.add(center);
        }
        return list;
    }

    public int countRedWalls() {
        int count = 0;
        for (Triple<Integer, Integer, Integer> triple : linesCircles) {
            if (triple.getThird() == Color.RED) {
                count++;
            }
        }
        return count;
    }

    public List<Circle> getLonelyPoints(int vertex1, int vertex2) {
        List<Circle> lonelyPoints = new ArrayList<>();
        lonelyPoints.add(vertex.get(vertex1));
        lonelyPoints.add(vertex.get(vertex2));

        for (Triple<Integer, Integer, Integer> t : linesCircles) {
            if (lonelyPoints.isEmpty())
                break;

            if (vertex1 == t.getFirst() || vertex1 == t.getSecond()) {
                lonelyPoints.remove(vertex.get(vertex1));
            }
            if (vertex2 == t.getFirst() || vertex2 == t.getSecond()) {
                lonelyPoints.remove(vertex.get(vertex2));
            }
        }
        return lonelyPoints;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Exclude
    public List<Circle> getVertex() {
        return vertex;
    }

    @Exclude
    public List<Polyline> getPolylines() { return polylines; }

    public PairCustom<Double, Double> getUserStartPosition() {
        return userStartPosition;
    }

    public void setUserStartPosition(PairCustom<Double, Double> userStartPosition) {
        this.userStartPosition = userStartPosition;
    }

    public void updateTriples(int i) {
        for (Triple<Integer, Integer, Integer> t : linesCircles) {
            if (t.getFirst() > i)
                t.setFirst(t.getFirst() - 1);

            if (t.getSecond() > i)
                t.setSecond(t.getSecond() - 1);
        }
    }

    public Circle getVertexDifferentFrom(Circle c) {
        for (Circle circle : vertex) {
            if (!circle.equals(c))
                return circle;
        }
        return null;
    }
}
