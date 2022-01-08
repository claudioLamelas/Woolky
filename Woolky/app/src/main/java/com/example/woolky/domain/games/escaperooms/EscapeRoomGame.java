package com.example.woolky.domain.games.escaperooms;

import com.example.woolky.domain.games.Game;

public class EscapeRoomGame extends Game {

    private final EscapeRoom escapeRoom;
    private boolean isFinito;

    public EscapeRoomGame(EscapeRoom escapeRoom) {
        super(8);
        this.escapeRoom = escapeRoom;
        this.isFinito = false;
    }

    @Override
    public int isFinished() {
        //TODO
        return 0;
    }

    public EscapeRoom getEscapeRoom() {
        return escapeRoom;
    }

    public boolean isFinito() {
        return isFinito;
    }

    public void setFinito(boolean finished) {
        isFinito = finished;
    }
}
