package com.example.woolky.domain.friends;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.woolky.domain.friends.FriendsInvite;
import com.example.woolky.domain.InviteDispatcher;
import com.example.woolky.domain.InviteState;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.friends.FriendsInviteFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.time.Instant;

public class FriendsInvitesListener implements ChildEventListener {

    Context cx;
    HomeActivity activity;

    public FriendsInvitesListener(Context cx, HomeActivity activity) {
        this.cx = cx;
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (snapshot.getValue() != null) {
            String lastInviteKey = snapshot.getKey();
            FriendsInvite lastInvite = snapshot.getValue(FriendsInvite.class);
            DatabaseReference inviteReference = snapshot.getRef();

            if (lastInvite.getInviteState() == InviteState.SENT && Instant.parse(lastInvite.getValidUntil()).isAfter(Instant.now())) {
                FriendsInviteFragment gif = FriendsInviteFragment.newInstance(lastInvite, lastInviteKey);
                gif.setInviteReference(inviteReference);

                InviteDispatcher inviteDispatcher = InviteDispatcher.getInstance(activity);
                inviteDispatcher.addPendingInvite(gif);
            }
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
