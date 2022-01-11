package com.example.woolky.domain;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

public class FriendsInvite implements Serializable {
    private String from;
    private String from_id;
    private InviteState inviteState;
    private Timestamp validUntil;

    public FriendsInvite(){}

    public FriendsInvite(String from, String from_id, InviteState inviteState) {
        this.from = from;
        this.from_id = from_id;
        this.inviteState = inviteState;
    }

    public static FriendsInvite deserialize(HashMap<String, Object> o) {
        FriendsInvite gi = new FriendsInvite();
        gi.setFrom((String) o.get("from"));
        gi.setInviteState(InviteState.valueOf((String) o.get("inviteState")));
        gi.setFrom_id((String) o.get("from_id"));
        return gi;
    }

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

    public Timestamp getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Timestamp validUntil) {
        this.validUntil = validUntil;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }
}
