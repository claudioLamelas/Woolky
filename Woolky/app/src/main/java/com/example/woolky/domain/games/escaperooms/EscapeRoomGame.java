package com.example.woolky.domain.games.escaperooms;

import com.example.woolky.domain.games.Game;
import com.google.firebase.database.Exclude;

import java.util.List;

public class EscapeRoomGame extends Game {

    private EscapeRoom escapeRoom;
    private List<String> playersIds;
    private boolean isFinito;

    public EscapeRoomGame() {
        super(8);
    }

    public EscapeRoomGame(EscapeRoom escapeRoom, List<String> playersIds) {
        super(8);
        this.escapeRoom = escapeRoom;
        this.isFinito = false;
        this.playersIds = playersIds;
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
}
