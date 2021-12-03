package com.example.woolky;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.woolky.domain.GameInvite;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class GameInvitesListener implements ChildEventListener {

    Context cx;
    LayoutInflater layoutInflater;
    FragmentManager fragmentManager;

    public GameInvitesListener(Context cx, LayoutInflater layoutInflater, FragmentManager fragmentManager) {
        this.cx = cx;
        this.layoutInflater = layoutInflater;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (snapshot.getValue() != null) {
            String lastInviteKey = snapshot.getKey();
            GameInvite lastInvite = snapshot.getValue(GameInvite.class);

            GameInviteFragment gif = GameInviteFragment.newInstance(lastInvite);
            fragmentManager.beginTransaction().replace(R.id.inviteFragment, gif).commitNow();
            int secondsDelayed = 10;
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    fragmentManager.beginTransaction().remove(gif).commitNow();
                }
            }, secondsDelayed * 1000);
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
