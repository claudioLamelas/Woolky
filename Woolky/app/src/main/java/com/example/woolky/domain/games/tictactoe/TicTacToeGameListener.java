package com.example.woolky.domain.games.tictactoe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.woolky.ui.games.tictactoe.PlayTicTacToeFragment;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.List;
import java.util.Objects;

public class TicTacToeGameListener implements ChildEventListener {

    PlayTicTacToeFragment playTicTacToeFragment;

    public TicTacToeGameListener(PlayTicTacToeFragment playTicTacToeFragment){
        this.playTicTacToeFragment = playTicTacToeFragment;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (Objects.equals(snapshot.getKey(), "currentPlayer")) {
            playTicTacToeFragment.getTicTacToe().setCurrentPlayer(snapshot.getValue(TicTacToeGame.Piece.class));
            playTicTacToeFragment.getConfirmPlayButton().setEnabled(playTicTacToeFragment.getTicTacToe().isMyTurn());
        }

        if (Objects.equals(snapshot.getKey(), "winner")) {
            playTicTacToeFragment.finishGame(snapshot.getValue(TicTacToeGame.Piece.class));
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        if (Objects.equals(snapshot.getKey(), "currentPlayer")) {
            playTicTacToeFragment.getTicTacToe().setCurrentPlayer(snapshot.getValue(TicTacToeGame.Piece.class));
            playTicTacToeFragment.getConfirmPlayButton().setEnabled(playTicTacToeFragment.getTicTacToe().isMyTurn());
        }

        if (Objects.equals(snapshot.getKey(), "lastPlayedPosition")) {
            playTicTacToeFragment.getTicTacToe().updatesLastPosition((List<Long>) snapshot.getValue(), playTicTacToeFragment.getMap());
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
