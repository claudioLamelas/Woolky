package com.example.woolky.ui.friends;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.woolky.R;
import com.example.woolky.domain.friends.FriendsInvite;
import com.example.woolky.domain.InviteDispatcher;
import com.example.woolky.ui.InviteFragment;
import com.example.woolky.domain.InviteState;
import com.example.woolky.ui.HomeActivity;
import com.google.firebase.database.DatabaseReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FriendsInviteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendsInviteFragment extends InviteFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FriendsInvite friendsInvite;
    private String friendsInviteID;
    private DatabaseReference inviteReference;
    private Handler handler;

    public FriendsInviteFragment() {}

    public static FriendsInviteFragment newInstance(FriendsInvite friendsInvite, String friendsInviteID) {
        FriendsInviteFragment fragment = new FriendsInviteFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, friendsInvite);
        args.putString(ARG_PARAM2, friendsInviteID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            friendsInvite = (FriendsInvite) getArguments().getSerializable(ARG_PARAM1);
            friendsInviteID = getArguments().getString(ARG_PARAM2);
            handler = new Handler();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_friends_invite, container, false);
        ((TextView)v.findViewById(R.id.inviteDescription)).setText(this.friendsInvite.getFrom() + " invited you to be friends");

        Button acceptButton = v.findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener((view) -> {
            handler.removeCallbacksAndMessages(null);
            inviteReference.child("inviteState").setValue(InviteState.ACCEPTED);

            ((HomeActivity) getActivity()).setupFriend(friendsInviteID, true, "");
            HomeActivity activity = (HomeActivity) requireActivity();
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