package com.example.woolky.domain.games;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.woolky.domain.Invite;
import com.example.woolky.domain.InviteState;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

public class GameInvite extends Invite implements Serializable {
    private String to, fromId, gameId;
    private GameMode gameMode;

    public GameInvite(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public GameInvite(String from, String fromId, String to, GameMode gameMode, InviteState inviteState, String gameId) {
        //TODO: meter validUntil
        super(from, inviteState);
        this.to = to;
        this.fromId = fromId;
        this.gameMode = gameMode;
        this.gameId = gameId;
    }

//    public static GameInvite deserialize(HashMap<String, Object> o) {
//        GameInvite gi = new GameInvite();
//        gi.setFrom((String) o.get("from"));
//        gi.setGameMode(GameMode.valueOf((String) o.get("gameMode")));
//        gi.setInviteState(InviteState.valueOf((String) o.get("inviteState")));
//        gi.setTo((String) o.get("to"));
//        return gi;
//    }


    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
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
