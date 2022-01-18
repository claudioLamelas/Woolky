package com.example.woolky.domain.friends;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.woolky.domain.Invite;
import com.example.woolky.domain.InviteState;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

public class FriendsInvite extends Invite implements Serializable {
    private String from_id;

    public FriendsInvite() {}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public FriendsInvite(String from, String from_id, InviteState inviteState) {
        //TODO: adicionar validUntil
        super(from, inviteState);
        this.from_id = from_id;
    }

//    public static FriendsInvite deserialize(HashMap<String, Object> o) {
//        FriendsInvite gi = new FriendsInvite();
//        gi.setFrom((String) o.get("from"));
//        gi.setInviteState(InviteState.valueOf((String) o.get("inviteState")));
//        gi.setFrom_id((String) o.get("from_id"));
//        return gi;
//    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }
}
