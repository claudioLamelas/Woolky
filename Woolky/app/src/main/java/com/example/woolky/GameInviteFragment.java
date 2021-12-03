package com.example.woolky;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.woolky.domain.GameInvite;
import com.example.woolky.domain.GameMode;

import java.io.Serializable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GameInviteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameInviteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private GameInvite gameInvite;

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
    public static GameInviteFragment newInstance(GameInvite gameInvite) {
        GameInviteFragment fragment = new GameInviteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, gameInvite);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameInvite = (GameInvite) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_game_invite, container, false);
        ((TextView)v.findViewById(R.id.inviteDescription)).setText(this.gameInvite.getFrom() +
                " has invited you to play " + this.gameInvite.getGameMode().toString());
        return v;
    }
}