package com.example.woolky.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.woolky.HomeActivity;
import com.example.woolky.LoginActivity;
import com.example.woolky.R;
import com.example.woolky.SplashScreenActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        v.findViewById(R.id.buttonLogOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut(v);
            }
        });

        return v;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }


    public void logOut(View v){
        FirebaseAuth.getInstance().signOut();
        HomeActivity home = (HomeActivity) getActivity();
        home.logout();

    }
}