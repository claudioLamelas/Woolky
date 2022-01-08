package com.example.woolky.domain.games.escaperooms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.woolky.domain.games.tictactoe.TicTacToeGame;
import com.example.woolky.ui.games.escaperooms.PlayEscapeRoomFragment;
import com.example.woolky.ui.games.tictactoe.FinishGameDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Objects;

public class EscapeRoomGameListener implements ChildEventListener {

    private PlayEscapeRoomFragment escapeRoomFragment;

    public EscapeRoomGameListener(PlayEscapeRoomFragment escapeRoomGame) {
        this.escapeRoomFragment = escapeRoomGame;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (Objects.equals(snapshot.getKey(), "finito")) {
            escapeRoomFragment.finishGame(snapshot.getValue(Boolean.class));
        }
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
