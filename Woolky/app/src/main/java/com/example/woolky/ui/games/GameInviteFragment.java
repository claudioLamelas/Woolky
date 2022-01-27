package com.example.woolky.ui.games;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.woolky.domain.InviteDispatcher;
import com.example.woolky.ui.InviteFragment;
import com.example.woolky.domain.games.EscapeRoomGameInvite;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.games.GameInvite;
import com.example.woolky.domain.InviteState;
import com.example.woolky.utils.Utils;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameInviteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameInviteFragment extends InviteFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GameInvite gameInvite;
    private String gameInviteID;
    private DatabaseReference inviteReference;
    private Handler handler;

    public GameInviteFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gameInvite Parameter 1.
     * @return A new instance of fragment GameInviteFragment.
     */
    public static GameInviteFragment newInstance(GameInvite gameInvite, String gameInviteID) {
        GameInviteFragment fragment = new GameInviteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, gameInvite);
        args.putString(ARG_PARAM2, gameInviteID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameInvite = (GameInvite) getArguments().getSerializable(ARG_PARAM1);
            gameInviteID = getArguments().getString(ARG_PARAM2);
            handler = new Handler();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_invite, container, false);
        ((TextView)v.findViewById(R.id.inviteDescription)).setText(this.gameInvite.getFrom() +
                " has invited you to play " + this.gameInvite.getGameMode().toString());

        Button acceptButton = v.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener((view) -> {
            handler.removeCallbacksAndMessages(null);
            inviteReference.child("inviteState").setValue(InviteState.ACCEPTED);
            HomeActivity activity = (HomeActivity) requireActivity();
            if (!activity.isPlaying) {

                activity.isPlaying = true;
                if (gameInvite instanceof EscapeRoomGameInvite) {
                    EscapeRoomGameInvite invite = ((EscapeRoomGameInvite) gameInvite);
                    ((HomeActivity) getActivity()).setupEscapeRoomGame(gameInviteID,
                            invite.getEscapeRoomId(), invite.getFromId(), invite.getPlayersIds(), true);
                } else {
                    ((HomeActivity) getActivity()).setupTicTacToeGame(gameInviteID, true);
                }

            } else {
                Utils.showInfoSnackBar(activity, getView(), "Already in a game");
            }

            activity.getSupportFragmentManager().beginTransaction().remove(this).commitNow();
            signalFragmentExit(activity);
        });

        Button declineButton = v.findViewById(R.id.declineButton);
        declineButton.setOnClickListener((view) -> {
            handler.removeCallbacksAndMessages(null);
            inviteReference.child("inviteState").setValue(InviteState.DECLINED);
            HomeActivity activity = (HomeActivity) requireActivity();
            activity.getSupportFragmentManager().beginTransaction().remove(this).commitNow();
            signalFragmentExit(activity);
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Fragment thisFragment = this;
        int secondsDelayed = 20;
        handler.postDelayed(new Runnable() {
            public void run() {
                inviteReference.child("inviteState").setValue(InviteState.DECLINED);
                HomeActivity activity = (HomeActivity) requireActivity();
                activity.getSupportFragmentManager().beginTransaction().remove(thisFragment).commitNow();
                signalFragmentExit(activity);
            }
        }, secondsDelayed * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public void setInviteReference(DatabaseReference inviteReference) {
        this.inviteReference = inviteReference;
    }

    private void signalFragmentExit(HomeActivity activity) {
        InviteDispatcher.getInstance().signalToShowNextInvite();
    }
}