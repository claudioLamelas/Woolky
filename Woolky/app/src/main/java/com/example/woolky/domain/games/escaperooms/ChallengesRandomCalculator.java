package com.example.woolky.domain.games.escaperooms;

import java.util.Random;

public class ChallengesRandomCalculator {
    public static final Random random = new Random();
    public static final int NUMBER_CHALLENGES = 2;

    public static int chooseNextChallenge(int quizzesSize) {
        int number = random.nextInt(quizzesSize + NUMBER_CHALLENGES);
        return Math.min(number, NUMBER_CHALLENGES);
    }

    public static int nextQuiz(int size) {
        return random.nextInt(size);
    }
}
