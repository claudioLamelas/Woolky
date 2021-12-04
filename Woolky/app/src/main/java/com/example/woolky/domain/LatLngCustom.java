package com.example.woolky.domain;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

public class LatLngCustom {
    private double latitude, longitude;

    public LatLngCustom(){}

    public LatLngCustom(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Exclude
    public LatLng getLatLng() {
        return new LatLng(this.latitude, this.longitude);
    }
}
