package com.example.woolky.ui.games.escaperooms.challenges;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.woolky.domain.games.escaperooms.OnChallengeCompletedListener;
import com.google.android.gms.maps.model.Polyline;

import java.util.Random;


public class FastClickDialog extends DialogFragment {

    public static final int MAXIMUM_TIME_ACCEPTABLE = 450;
    private Handler handler;
    private Polyline polyline;
    private long startTime;
    private CountDownTimer countDownTimer;
    private boolean touched;
    private OnChallengeCompletedListener listener;

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
            if (!touched) {
                if (countDownTimer != null)
                    countDownTimer.cancel();

                boolean valid = false;
                if (colorSquare.getBackground() instanceof ColorDrawable)
                    valid = ((ColorDrawable) colorSquare.getBackground()).getColor() == Color.GREEN;

                if (!valid) {
                    handler.removeCallbacksAndMessages(null);
                    timeTV.setText("Too soon Sir...");
                    terminateChallenge(false);
                } else if (startTime < MAXIMUM_TIME_ACCEPTABLE) {
                    timeTV.setText("YOU DID IT!");
                    terminateChallenge(true);
                } else {
                    timeTV.setText("You are too slow. You shall not pass!");
                    terminateChallenge(false);
                }

                touched = true;
            }
        });

        long randomGreenTime = new Random().nextInt(6000) + 1000;

        handler.postDelayed(() -> {
            colorSquare.setBackgroundColor(Color.GREEN);
            countDownTimer = new CountDownTimer(4000, 10) {
                @Override
                public void onTick(long millisUntilFinished) {
                    startTime = (4000 - millisUntilFinished);
                    timeTV.setText((startTime / 1000.0) + " s");
                }

                @Override
                public void onFinish() {
                    if (!touched)
                        timeTV.setText("Jesus Christ.. Not even a snail could be this slow");
                    terminateChallenge(false);
                }
            }.start();
        }, randomGreenTime);
    }

    private void terminateChallenge(boolean passed) {
        handler.postDelayed(() -> {
            if (passed)
                listener.challengeCompleted(this, polyline);
            else
                this.dismiss();
        }, 2000);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnChallengeCompletedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement SequenceListener");
        }
    }
}