package com.example.woolky;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.woolky.domain.TicTacToe;
import com.example.woolky.ui.map.GameModeFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Objects;

public class GameListener implements ChildEventListener {

    GameModeFragment gameModeFragment;

    public GameListener(GameModeFragment gameModeFragment){
        this.gameModeFragment = gameModeFragment;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (Objects.equals(snapshot.getKey(), "currentPlayer")) {
            gameModeFragment.getTicTacToe().setCurrentPlayer(snapshot.getValue(TicTacToe.Piece.class));
            gameModeFragment.getConfirmPlayButton().setEnabled(gameModeFragment.getTicTacToe().isMyTurn());
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (Objects.equals(snapshot.getKey(), "currentPlayer")) {
            gameModeFragment.getTicTacToe().setCurrentPlayer(snapshot.getValue(TicTacToe.Piece.class));
            gameModeFragment.getConfirmPlayButton().setEnabled(gameModeFragment.getTicTacToe().isMyTurn());
        }

        if (Objects.equals(snapshot.getKey(), "lastPlayedPosition")) {
            gameModeFragment.getTicTacToe().updatesLastPosition((List<Long>) snapshot.getValue(), gameModeFragment.getMap());
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
