package com.example.woolky.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class LocationCalculator {

    public static LatLng getPositionXMetersRight(LatLng posicaoInicial, int distanceFromPoint, int multiplier) {
        if (multiplier == 0) {
            return posicaoInicial;
        } else {
            String longitudeDegrees = Location.convert(posicaoInicial.longitude, Location.FORMAT_SECONDS);

            String[] separatedLongi = longitudeDegrees.split(":");
            String secondsLongitude = separatedLongi[2];

            secondsLongitude = secondsLongitude.replace(",", ".");
            double secondsLongi = Double.parseDouble(secondsLongitude);

            calculateNewCoordinates(distanceFromPoint, multiplier, separatedLongi, secondsLongi);

            String longitudePlusOneSecond = separatedLongi[0] + ":" + separatedLongi[1] + ":" + separatedLongi[2];
            double newLongitude = Location.convert(longitudePlusOneSecond);
            return new LatLng(posicaoInicial.latitude, newLongitude);
        }
    }

    public static LatLng getPositionXMetersBelow(LatLng posicaoInicial, int distanceFromPoint, int multiplier) {
        if (multiplier == 0) {
            return posicaoInicial;
        } else {
            String latitudeDegrees = Location.convert(posicaoInicial.latitude, Location.FORMAT_SECONDS);

            String[] separated = latitudeDegrees.split(":");
            String secondsLatitude = separated[2];

            secondsLatitude = secondsLatitude.replace(",", ".");
            double secondsLat = Double.parseDouble(secondsLatitude);

            calculateNewCoordinates(distanceFromPoint, multiplier, separated, secondsLat);

            String latitudeMinusOneSecond = separated[0] + ":" + separated[1] + ":" + separated[2];
            double newLatitude = Location.convert(latitudeMinusOneSecond);
            return new LatLng(newLatitude, posicaoInicial.longitude);
        }
    }

    //Com um multiplier positivo ou vai para baixo ou para a direita, com multiplier negativo ou vai para cima ou para a esquerda
    private static void calculateNewCoordinates(int distanceFromPoint, int multiplier, String[] degreesMinutesSeconds, double seconds) {
        seconds -= distanceFromPoint / 30.0 * multiplier;
        if (seconds >= 60.0) {
            seconds -= 60.0;
            String minutesLatitude = degreesMinutesSeconds[1];
            int minutesLat = Integer.parseInt(minutesLatitude);
            minutesLat += 1;
            if (minutesLat >= 60) {
                minutesLat -= 60;
                String degreesLatitude = degreesMinutesSeconds[0];
                int degreesLat = Integer.parseInt(degreesLatitude);
                degreesLat += 1;
                degreesMinutesSeconds[0] = String.valueOf(degreesLat);
            }
            degreesMinutesSeconds[1] = String.valueOf(minutesLat);
        }

        if (seconds < 0.0) {
            seconds += 60;
            String minutesLatitude = degreesMinutesSeconds[1];
            int minutesLat = Integer.parseInt(minutesLatitude);
            minutesLat -= 1;
            if (minutesLat < 0) {
                minutesLat += 60;
                String degreesLatitude = degreesMinutesSeconds[0];
                int degreesLat = Integer.parseInt(degreesLatitude);
                degreesLat -= 1;
                degreesMinutesSeconds[0] = String.valueOf(degreesLat);
            }
            degreesMinutesSeconds[1] = String.valueOf(minutesLat);
        }
        degreesMinutesSeconds[2] = String.valueOf(seconds);
    }

    public static List<LatLng> calculatePositions(LatLng initialPosition, List<PairCustom<Double, Double>> circlesRelativePositions) {
        List<LatLng> positions = new ArrayList<>();

        LatLng previousPosition = initialPosition;
        LatLng newPosition;
        for (PairCustom<Double, Double> p : circlesRelativePositions) {
            if (p.getFirst() <= 0) {
                newPosition = getPositionXMetersBelow(previousPosition, p.getFirst().intValue() * -1, -1);
            } else {
                newPosition = getPositionXMetersBelow(previousPosition, p.getFirst().intValue(), 1);
            }

            if (p.getSecond() <= 0) {
                newPosition = getPositionXMetersRight(newPosition, p.getSecond().intValue() * -1, -1);
            } else {
                newPosition = getPositionXMetersRight(newPosition, p.getSecond().intValue(), 1);
            }

            positions.add(newPosition);
            previousPosition = newPosition;
        }

        return positions;
    }

    public static double distancePointToPoint(LatLng p1, LatLng p2) {
        float[] results = new float[1];
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, results);
        return results[0];
    }

    public static PairCustom<Double, Double> diferenceBetweenPoints(LatLng previousPosition, LatLng currentPosition) {
        LatLng onlyLatitude = new LatLng(currentPosition.latitude, previousPosition.longitude);
        LatLng onlyLongitude = new LatLng(previousPosition.latitude, currentPosition.longitude);

        //Com isto estou a verificar em que sentido é, se para a esquerda ou direita OU se para baixo ou para cima
        //Uma latDif < 0 quer dizer que está em baixo
        //Uma lonDif < 0 quer dizer que está à esquerda
        double latDif = (currentPosition.latitude - previousPosition.latitude) < 0 ? 1 : -1;
        double lonDif = (currentPosition.longitude - previousPosition.longitude) < 0 ? -1 : 1;

        float[] resultsLat = new float[1];
        Location.distanceBetween(previousPosition.latitude, previousPosition.longitude,
                onlyLatitude.latitude, onlyLatitude.longitude, resultsLat);

        float[] resultsLon = new float[1];
        Location.distanceBetween(previousPosition.latitude, previousPosition.longitude,
                onlyLongitude.latitude, onlyLongitude.longitude, resultsLon);

        return new PairCustom<>(resultsLat[0] * latDif, resultsLon[0] * lonDif);
    }

    public static boolean doLineSegmentsIntersect(Point p, Point p2, Point q, Point q2) {
        Point r = subtractPoints(p2, p);
        Point s = subtractPoints(q2, q);

        double uNumerator = crossProduct(subtractPoints(q, p), r);
        double denominator = crossProduct(r, s);

        if (denominator == 0) {
            return false;
        }

        double u = uNumerator / denominator;
        double t = crossProduct(subtractPoints(q, p), s) / denominator;

        return (t >= 0) && (t <= 1) && (u >= 0) && (u <= 1);
    }

    private static double crossProduct(Point point1, Point point2) {
        return point1.x * point2.y - point1.y * point2.x;
    }

    private static Point subtractPoints(Point point1, Point point2) {
        return new Point(point1.x - point2.x, point1.y - point2.y);
    }
}
