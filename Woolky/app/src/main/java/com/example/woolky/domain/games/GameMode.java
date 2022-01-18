package com.example.woolky.domain.games;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

public enum GameMode {
    TIC_TAC_TOE("Tic Tac Toe"),
    ESCAPE_ROOM("Escape Room");

    private final String name;

    GameMode(String s) {
        this.name = s;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
