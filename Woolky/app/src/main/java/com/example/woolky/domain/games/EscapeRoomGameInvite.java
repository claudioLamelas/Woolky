package com.example.woolky.domain.games;

import com.example.woolky.domain.InviteState;

public class EscapeRoomGameInvite extends GameInvite{

    private String escapeRoomId;

    public EscapeRoomGameInvite() {}

    public EscapeRoomGameInvite(String from, String fromId, String to, InviteState inviteState, String escapeRoomId) {
        super(from, fromId, to, GameMode.ESCAPE_ROOM, inviteState);
        this.escapeRoomId = escapeRoomId;
    }

    public String getEscapeRoomId() {
        return escapeRoomId;
    }
}
