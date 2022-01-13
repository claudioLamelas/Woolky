package com.example.woolky.domain.games;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.woolky.domain.InviteState;
import com.example.woolky.domain.User;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.games.ChooseGameModeDialog;
import com.example.woolky.ui.games.escaperooms.ChooseEscapeRoomDialog;
import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.Objects;

public class GameInviteSender implements ChooseEscapeRoomDialog.OnChosenEscapeRoomListener,
        ChooseGameModeDialog.OnGameModeChosenListener {

    private HomeActivity activity;
    private List<String> players;
    @Nullable
    private GameMode gameMode;
    @Nullable
    private String escapeRoomId;

    public GameInviteSender(HomeActivity activity, List<String> players,
                            @Nullable GameMode gameMode, @Nullable String escapeRoomId) {
        this.activity = activity;
        this.players = players;
        this.gameMode = gameMode;
        this.escapeRoomId = escapeRoomId;
    }

    public void createGameInvite() {
        User signedInUser = activity.getSignedInUser();

        if (gameMode == null) {
            ChooseGameModeDialog dialog = new ChooseGameModeDialog(this);
            dialog.show(activity.getSupportFragmentManager(), "choose");
            return;
        }

        if (gameMode == GameMode.ESCAPE_ROOM && escapeRoomId == null) {
            ChooseEscapeRoomDialog dialog = new ChooseEscapeRoomDialog(this);
            dialog.show(activity.getSupportFragmentManager(), "choose");
            return;
        }

        DatabaseReference gamesReference = activity.getDatabaseRef().child("games");
        String gameId = gamesReference.push().getKey();
        for (String userId : players) {
            if (userId.equals(signedInUser.getUserId()))
                continue;

            DatabaseReference ref = activity.getDatabaseRef().child("gameInvites").child(userId);
            String id = ref.push().getKey();


            GameInvite gameInvite = newGameInvite(signedInUser, userId, gameId);

            ref.child(id).setValue(gameInvite);

            DatabaseReference inviteStateRef = ref.child(id).child("inviteState");
            activity.setListenerToGameInvite(gameId, inviteStateRef, gameInvite);
        }
    }

    private GameInvite newGameInvite(User signedInUser, String userId, String gameId) {
        switch (Objects.requireNonNull(gameMode)) {
            case TIC_TAC_TOE: {
                return new GameInvite(signedInUser.getUserName(), signedInUser.getUserId(),
                        userId, GameMode.TIC_TAC_TOE, InviteState.SENT, gameId);
            }
            case ESCAPE_ROOM: {
                return new EscapeRoomGameInvite(signedInUser.getUserName(), signedInUser.getUserId(),
                        userId, InviteState.SENT, escapeRoomId, players, gameId);
            }
            default: return null;
        }
    }

    @Override
    public void onEscapeRoomChosen(DialogFragment dialog, String escapeRoomId) {
        dialog.dismiss();
        this.escapeRoomId = escapeRoomId;
        this.createGameInvite();
    }

    @Override
    public void onChosenGameMode(DialogFragment dialog, GameMode gameMode) {
        dialog.dismiss();
        this.gameMode = gameMode;
        this.createGameInvite();
    }
}
