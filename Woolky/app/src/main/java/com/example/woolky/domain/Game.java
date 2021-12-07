package com.example.woolky.domain;

import com.google.firebase.database.Exclude;

public abstract class Game {

    @Exclude
    protected int numParticipants;

    public Game(int numParticipants) {
        this.numParticipants = numParticipants;
    }

    public abstract int isFinished();
}
