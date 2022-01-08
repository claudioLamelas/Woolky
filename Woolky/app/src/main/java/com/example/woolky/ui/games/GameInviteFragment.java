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

import com.example.woolky.domain.games.EscapeRoomGameInvite;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.games.GameInvite;
import com.example.woolky.domain.InviteState;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameInviteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameInviteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private GameInvite gameInvite;
    private String gameInviteID;
    private DatabaseReference inviteReference;
    private Handler handler;

    public GameInviteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gameInvite Parameter 1.
     * @return A new instance of fragment GameInviteFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game_invite, container, false);
        ((TextView)v.findViewById(R.id.inviteDescription)).setText(this.gameInvite.getFrom() +
                " has invited you to play " + this.gameInvite.getGameMode().toString());

        Button acceptButton = v.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener((view) -> {
            handler.removeCallbacksAndMessages(null);
            inviteReference.child("inviteState").setValue(InviteState.ACCEPTED);

            if (gameInvite instanceof EscapeRoomGameInvite) {
                ((HomeActivity) getActivity()).setupEscapeRoomGame(gameInviteID,
                        ((EscapeRoomGameInvite) gameInvite).getEscapeRoomId(), gameInvite.getFromId());
            } else {
                ((HomeActivity) getActivity()).setupTicTacToeGame(gameInviteID, true);
            }

            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitNow();
        });

        Button declineButton = v.findViewById(R.id.declineButton);
        declineButton.setOnClickListener((view) -> {
            handler.removeCallbacksAndMessages(null);
            inviteReference.child("inviteState").setValue(InviteState.DECLINED);
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commitNow();
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
                requireActivity().getSupportFragmentManager().beginTransaction().remove(thisFragment).commitNow();
            }
        }, secondsDelayed * 1000);
    }

    public void setInviteReference(DatabaseReference inviteReference) {
        this.inviteReference = inviteReference;
    }
}