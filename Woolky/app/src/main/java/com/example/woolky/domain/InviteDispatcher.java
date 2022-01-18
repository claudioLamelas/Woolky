package com.example.woolky.domain;

import com.example.woolky.R;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.InviteFragment;
import com.example.woolky.ui.games.GameInviteFragment;

import java.util.ArrayList;
import java.util.List;

public class InviteDispatcher {

    private static InviteDispatcher inviteDispatcher = null;

    public HomeActivity activity;
    public List<InviteFragment> pendingInvites;

    private InviteDispatcher(HomeActivity activity) {
        this.activity = activity;
        pendingInvites = new ArrayList<>();
    }

    public static InviteDispatcher getInstance(HomeActivity activity) {
        if (inviteDispatcher == null) {
            inviteDispatcher = new InviteDispatcher(activity);
        }
        return inviteDispatcher;
    }

    public void addPendingInvite(InviteFragment invite) {
        pendingInvites.add(invite);
        signalToShowNextInvite();
    }

    public void signalToShowNextInvite() {
        if (pendingInvites.size() > 0) {
            if (activity.getSupportFragmentManager().findFragmentByTag("inviteFragment") == null) {
                InviteFragment nextInviteFragment = pendingInvites.remove(0);

                if (nextInviteFragment instanceof GameInviteFragment && activity.isPlaying) {
                    signalToShowNextInvite();
                    return;
                }

                activity.getSupportFragmentManager().beginTransaction().replace(R.id.inviteFragment, nextInviteFragment, "inviteFragment").commitNow();
            }
        }
    }
}
