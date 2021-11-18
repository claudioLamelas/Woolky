package com.example.woolky;

import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;
import java.util.List;

public class MockUsers {
    public static List<LatLng> usersPositions = Arrays.asList(
            new LatLng(38.79954, -9.14414),
            new LatLng(38.80002, -9.14424),
            new LatLng(38.80056, -9.14342));
    public static List<String> usersInformation = Arrays.asList("Juan Calamares", "Francesca Giacomo", "Ana Caxo Paulo");
    public static List<Integer> usersColors = Arrays.asList(Color.RED, Color.BLUE, Color.rgb(222, 157, 35));
}
