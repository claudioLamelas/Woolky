package com.example.woolky.domain.games;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.woolky.domain.InviteDispatcher;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.games.GameInviteFragment;
import com.example.woolky.domain.InviteState;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.time.Instant;
import java.util.HashMap;

public class GameInvitesListener implements ChildEventListener {

    Context cx;
    HomeActivity activity;

    public GameInvitesListener(Context cx, HomeActivity activity) {
        this.cx = cx;
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (snapshot.getValue() != null) {
            String lastInviteKey = snapshot.getKey();
            Object data = snapshot.getValue(Object.class);

            GameInvite invite;
            if (((HashMap<?, ?>) data).containsKey("escapeRoomId"))
                invite = snapshot.getValue(EscapeRoomGameInvite.class);
            else {
                invite = snapshot.getValue(GameInvite.class);
            }
            DatabaseReference inviteReference = snapshot.getRef();

            if (invite.getInviteState() == InviteState.SENT && Instant.parse(invite.getValidUntil()).isAfter(Instant.now())) {
                GameInviteFragment gif = GameInviteFragment.newInstance(invite, invite.getGameId());
                gif.setInviteReference(inviteReference);

                InviteDispatcher inviteDispatcher = InviteDispatcher.getInstance(activity);
                inviteDispatcher.addPendingInvite(gif);
            }
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        //Aqui ver se o invite foi aceite ou recusado e fazer a lógica adequada
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
