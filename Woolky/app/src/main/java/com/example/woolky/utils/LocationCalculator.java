package com.example.woolky.utils;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

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
}
