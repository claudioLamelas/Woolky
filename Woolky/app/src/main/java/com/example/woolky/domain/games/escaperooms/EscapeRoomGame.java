package com.example.woolky.domain.games.escaperooms;

import com.example.woolky.domain.games.Game;
import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Random;

public class EscapeRoomGame extends Game {

    private EscapeRoom escapeRoom;
    private List<String> playersIds;
    private boolean isFinito;

    private String finalCode;

    public EscapeRoomGame() {
        super(8);
    }

    public EscapeRoomGame(EscapeRoom escapeRoom, List<String> playersIds) {
        super(8);
        this.escapeRoom = escapeRoom;
        this.isFinito = false;
        this.playersIds = playersIds;

        Random random = new Random();
        finalCode = "";
        for (int i = 0; i < escapeRoom.countRedWalls(); i++) {
            finalCode += "" + random.nextInt(10);
        }
    }

    // -1 == não acabou
    // 1 == acabou
    @Override
    @Exclude
    public int isFinished() {
        return escapeRoom.getBlueLine() == null ? 1 : -1;
    }

    @Exclude
    public EscapeRoom getEscapeRoom() {
        return escapeRoom;
    }

    public boolean isFinito() {
        return isFinito;
    }

    public void setFinito(boolean finished) {
        isFinito = finished;
    }

    @Exclude
    public List<String> getPlayersIds() {
        return playersIds;
    }

    @Exclude
    public String getFinalCode() {
        return finalCode;
    }
}
