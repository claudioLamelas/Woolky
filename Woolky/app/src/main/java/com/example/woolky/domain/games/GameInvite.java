package com.example.woolky.domain.games;

import com.example.woolky.domain.InviteState;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

public class GameInvite implements Serializable {
    private String from, to, fromId, gameId;
    private GameMode gameMode;
    private InviteState inviteState;
    private Timestamp validUntil;

    public GameInvite(){}

    public GameInvite(String from, String fromId, String to, GameMode gameMode, InviteState inviteState, String gameId) {
        this.from = from;
        this.to = to;
        this.fromId = fromId;
        this.gameMode = gameMode;
        this.inviteState = inviteState;
        this.gameId = gameId;
    }

    public static GameInvite deserialize(HashMap<String, Object> o) {
        GameInvite gi = new GameInvite();
        gi.setFrom((String) o.get("from"));
        gi.setGameMode(GameMode.valueOf((String) o.get("gameMode")));
        gi.setInviteState(InviteState.valueOf((String) o.get("inviteState")));
        gi.setTo((String) o.get("to"));
        return gi;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public InviteState getInviteState() {
        return inviteState;
    }

    public void setInviteState(InviteState inviteState) {
        this.inviteState = inviteState;
    }

    public Timestamp getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Timestamp validUntil) {
        this.validUntil = validUntil;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }
}
