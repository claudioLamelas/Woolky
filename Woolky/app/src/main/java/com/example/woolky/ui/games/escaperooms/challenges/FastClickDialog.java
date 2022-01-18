package com.example.woolky.ui.games.escaperooms.challenges;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.woolky.R;
import com.google.android.gms.maps.model.Polyline;


public class FastClickDialog extends DialogFragment {

    private Handler handler;
    private Polyline polyline;
    private long startTime;
    private CountDownTimer countDownTimer;
    private boolean touched;

    public FastClickDialog(Polyline polyline) {
        this.polyline = polyline;
        this.handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fast_click_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_fast_click_dialog, null);

        v.findViewById(R.id.startFastClickChallengeButton).setOnClickListener(view -> {
            startChallenge(v);
        });

        builder.setView(v).setTitle("Fast Click Challenge");
        return builder.create();
    }

    private void startChallenge(View view) {
        TextView timeTV = view.findViewById(R.id.fastClickText);
        Button reactionButton = view.findViewById(R.id.startFastClickChallengeButton);
        FrameLayout colorSquare = view.findViewById(R.id.fastClickColorLayout);
//        reactionButton.setText("Click");
        reactionButton.setVisibility(View.INVISIBLE);
        timeTV.setText("0.000 s");

        colorSquare.setOnClickListener((v) -> {
            touched = true;
            if (countDownTimer != null)
                countDownTimer.cancel();
            
        });

        handler.postDelayed(() -> {
            colorSquare.setBackgroundColor(Color.GREEN);
            countDownTimer = new CountDownTimer(4000, 10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeTV.setText(((4000 - millisUntilFinished) / 1000.0) + " s");
                }

                @Override
                public void onFinish() {

                }
            }.start();
        }, 5000);
    }
}