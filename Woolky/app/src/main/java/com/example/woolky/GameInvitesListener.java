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
import com.example.woolky.domain.InviteState;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GameInvitesListener implements ChildEventListener {

    Context cx;
    FragmentManager fragmentManager;

    public GameInvitesListener(Context cx, FragmentManager fragmentManager) {
        this.cx = cx;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (snapshot.getValue() != null) {
            String lastInviteKey = snapshot.getKey();
            GameInvite lastInvite = snapshot.getValue(GameInvite.class);
            DatabaseReference inviteReference = snapshot.getRef();

            if (lastInvite.getInviteState() == InviteState.SENT) {
                GameInviteFragment gif = GameInviteFragment.newInstance(lastInvite, lastInviteKey);
                gif.setInviteReference(inviteReference);
                fragmentManager.beginTransaction().replace(R.id.inviteFragment, gif).commitNow();
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