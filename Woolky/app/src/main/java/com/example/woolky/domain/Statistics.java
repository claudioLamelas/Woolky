package com.example.woolky.domain;

import android.util.Pair;

import com.example.woolky.utils.PairCustom;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Statistics {

    private int totalWins;

    private List<PairCustom<String,Integer>> weeklySteps;
    private List<PairCustom<String,Double>> weeklyDistance;
    private int positionWeek;
    private boolean firstUse = true;


    public Statistics() {

        this.weeklySteps = new ArrayList<>(7);
        this.weeklyDistance = new ArrayList<>(7);
        this.positionWeek = 0;
    }


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

    public List<PairCustom<String, Double>> getWeeklyDistance() {
        return weeklyDistance;
    }

    public int getPositionWeek() {
        return positionWeek;
    }

    public List<PairCustom<String, Integer>> getWeeklySteps() {
        return weeklySteps;
    }

    public void updateStepsAndDistance (int steps, double distance ) {

        if (weeklySteps == null){
            weeklySteps = new ArrayList<>();
        }
        if (weeklyDistance == null) {
            weeklyDistance = new ArrayList<>();
        }

        int lastPos;
        if (firstUse)
            lastPos = 0;
        else
            lastPos = (positionWeek == 0) ? 6 : positionWeek-1;


        String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        if (weeklySteps.get(lastPos).getFirst().equals(currentDate)){
            PairCustom<String, Integer> s = weeklySteps.get(lastPos);
            s.setSecond(steps);
            weeklySteps.add(lastPos, s);

            PairCustom<String, Double> d = weeklyDistance.get(lastPos);
            d.setSecond(distance);
            weeklyDistance.add(lastPos, d);
        }
        else {
            weeklySteps.add(positionWeek, new PairCustom<>(currentDate, steps));
            weeklyDistance.add(positionWeek, new PairCustom<>(currentDate, distance));
        }



        positionWeek = (positionWeek + 1) % 7;
        firstUse = false;


    }

    /*
    Por semana
     */
    public int getTotalNumberSteps(int week) {

        if (weeklySteps == null) {
            weeklySteps = new ArrayList<>();
        }


        Calendar now = Calendar.getInstance();

        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");


        ArrayList<String> days = new ArrayList<>(7);

        //int delta = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; //add 2 if your week start on monday

        now.add(Calendar.DAY_OF_MONTH, week);
        for (int i = 0; i < 7; i++)
        {
            days.add(i,format.format(now.getTime()));
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

        int total = 0;
        for (PairCustom<String, Integer> dailySteps : weeklySteps) {

            if (days.contains(dailySteps.getFirst()))
                total += dailySteps.getSecond();

        }

        return total;

    }

}
