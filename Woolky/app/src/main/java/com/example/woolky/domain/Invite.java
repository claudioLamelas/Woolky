package com.example.woolky.domain;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.woolky.domain.games.GameMode;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

public abstract class Invite {
    private String from;
    private InviteState inviteState;
    private String validUntil;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Invite(String from, InviteState inviteState) {
        this.from = from;
        this.inviteState = inviteState;
        this.validUntil = Instant.now().plusSeconds(120).toString();
    }

    protected Invite() {}

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public InviteState getInviteState() {
        return inviteState;
    }

    public void setInviteState(InviteState inviteState) {
        this.inviteState = inviteState;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;
    }
}
