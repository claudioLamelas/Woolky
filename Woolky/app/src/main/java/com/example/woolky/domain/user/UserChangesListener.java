package com.example.woolky.domain.user;

import androidx.annotation.NonNull;

import com.example.woolky.domain.user.User;
import com.example.woolky.ui.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserChangesListener implements ValueEventListener {

    private HomeActivity activity;

    public UserChangesListener(HomeActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.getValue() != null) {
            activity.setSignedInUser(snapshot.getValue(User.class));
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
