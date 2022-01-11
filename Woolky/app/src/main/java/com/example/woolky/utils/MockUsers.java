package com.example.woolky.utils;

import android.graphics.Color;

import com.example.woolky.domain.ShareLocationType;
import com.example.woolky.domain.User;

import java.util.Arrays;
import java.util.List;

public class MockUsers {
    public static List<User> users = Arrays.asList(
            new User("1", "JuanCalamares", 20, Color.RED, new LatLngCustom(38.79954, -9.14414), ShareLocationType.ALL),
            new User("2", "FrancescaGiacomo", 5, Color.BLUE, new LatLngCustom(38.80002, -9.14424), ShareLocationType.ALL),
            new User("3", "AnaCaxoPaulo", 17, Color.rgb(222, 157, 35), new LatLngCustom(38.80056, -9.14342), ShareLocationType.ALL)
    );
}
