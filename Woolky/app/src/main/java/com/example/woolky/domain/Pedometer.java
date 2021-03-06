package com.example.woolky.domain;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.woolky.R;
import com.example.woolky.ui.HomeActivity;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class Pedometer implements SensorEventListener {

    public static final String PREVIOUS_TOTAL_STEPS = "previousTotalSteps";
    public static final String CURRENT_DAY = "currentDay";
    //IN METERS
    public static final double AVERAGE_STEP_LENGTH = 0.74;

    private final HomeActivity activity;
    private final SensorManager sensorManager;
    private final Sensor stepCounterSensor;
    private float previousTotalSteps;
    private int currentSteps;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Pedometer(HomeActivity activity) {
        this.activity = activity;
        this.sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        this.stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        loadData();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadData() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        previousTotalSteps = sharedPreferences.getFloat(PREVIOUS_TOTAL_STEPS, 0f);
        int day = sharedPreferences.getInt(CURRENT_DAY, 1);

        if (day < LocalDateTime.now().getDayOfMonth())
            previousTotalSteps = 0f;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveData() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        int day = sharedPreferences.getInt(CURRENT_DAY, 1);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (day < LocalDateTime.now().getDayOfMonth()) {
            editor.putFloat(PREVIOUS_TOTAL_STEPS, 0f);
        } else {
            editor.putFloat(PREVIOUS_TOTAL_STEPS, previousTotalSteps);
        }
        editor.putInt(CURRENT_DAY, LocalDateTime.now().getDayOfMonth());
        editor.apply();

        activity.updateStepsDistanceBD(currentSteps, getDistanceTravelled());

    }

    public void startCounter() {
        if (!this.sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST)) {
            new Handler().postDelayed(this::startCounter, 15000);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (previousTotalSteps == 0)
            previousTotalSteps = event.values[0];

        float totalSteps = event.values[0];
        currentSteps = (int) Math.abs((totalSteps - previousTotalSteps));
        if (activity.areWeHome()) {
            activity.updateHomeStats(currentSteps, getDistanceTravelled());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public int getCurrentSteps() {
        return currentSteps;
    }

    public double getDistanceTravelled() {
        double distance = (currentSteps * AVERAGE_STEP_LENGTH / 1000);
        return Math.round(distance * 100) / 100.0;
    }
}
