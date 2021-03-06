package com.example.woolky.domain.games;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.woolky.domain.InviteState;

import java.util.List;

public class EscapeRoomGameInvite extends GameInvite{

    private String escapeRoomId;
    private List<String> playersIds;

    public EscapeRoomGameInvite(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public EscapeRoomGameInvite(String from, String fromId, String to, InviteState inviteState,
                                String escapeRoomId, List<String> playersIds, String gameId) {
        super(from, fromId, to, GameMode.ESCAPE_ROOM, inviteState, gameId);
        this.escapeRoomId = escapeRoomId;
        this.playersIds = playersIds;
    }

    public String getEscapeRoomId() {
        return escapeRoomId;
    }

    public List<String> getPlayersIds() {
        return playersIds;
    }
}
