package com.example.woolky.ui.games.escaperooms.challenges;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.woolky.R;
import com.example.woolky.domain.games.escaperooms.ChallengesRandomCalculator;
import com.example.woolky.domain.games.escaperooms.OnChallengeCompletedListener;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FastPianoDialog extends DialogFragment {

    public static final int COLOR_ON_SCREEN_TIME = 700;
    public static final int NUMBER_OF_COLORS_SHOWN = 9;
    public static final int COUNTDOWN_TOTAL_TIME = COLOR_ON_SCREEN_TIME * NUMBER_OF_COLORS_SHOWN * 2;

    private static final List<String> words = Arrays.asList("Blue", "Red", "Yellow", "Green", "Black", "Orange");
    private static final List<Integer> colors =
            Arrays.asList(R.color.cornell_red, R.color.napier_green, R.color.tangerine_yellow,
                    R.color.blueberry_blue, R.color.purple_sage_bush);

    private Polyline polyline;
    private List<ConstraintLayout> showColorRects;
    private List<TextView> showColorTextViews;

    private CountDownTimer countDownTimer;
    private int currentColor = -1;
    private boolean missed;
    private boolean touched;

    private OnChallengeCompletedListener listener;
    private Handler handler;


    public FastPianoDialog(Polyline polyline) {
        this.polyline = polyline;
        this.showColorRects = new ArrayList<>();
        this.showColorTextViews = new ArrayList<>();
        this.handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fast_piano_dialog, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        handler.removeCallbacksAndMessages(null);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_fast_piano_dialog, null);

        fillLists(v);

        TextView description = v.findViewById(R.id.FastPianoDescriptionTV);

        Button startButton = v.findViewById(R.id.startPianoChallenge);
        startButton.setOnClickListener((view) -> {
            startButton.setVisibility(View.INVISIBLE);
            description.setVisibility(View.GONE);
            startChallenge(v);
        });

        builder.setView(v).setTitle("Piano Tiles Challenge");
        return builder.create();
    }

    private void fillLists(View v) {
        showColorRects.add(v.findViewById(R.id.show1));
        showColorRects.add(v.findViewById(R.id.show2));
        showColorRects.add(v.findViewById(R.id.show3));

        showColorTextViews.add(v.findViewById(R.id.textShow1));
        showColorTextViews.add(v.findViewById(R.id.textShow2));
        showColorTextViews.add(v.findViewById(R.id.textShow3));
    }

    private void startChallenge(View v) {

        registerListeners(v);

        countDownTimer = new CountDownTimer(COUNTDOWN_TOTAL_TIME, COLOR_ON_SCREEN_TIME) {
            boolean showingColor = false;

            int index;
            int color;
            int indexText;
            int colorText;

            @Override
            public void onTick(long millisUntilFinished) {
                if (!showingColor) {
                    showingColor = true;
                    touched = false;

                    index = ChallengesRandomCalculator.nextInt(showColorRects.size());
                    color = ChallengesRandomCalculator.nextInt(colors.size());
                    indexText = ChallengesRandomCalculator.nextInt(words.size());
                    colorText = ChallengesRandomCalculator.nextInt(colors.size());

                    currentColor = colors.get(color);

                    showColorRects.get(index).setBackgroundColor(getResources().getColor(currentColor, null));
                    TextView chosen = showColorTextViews.get(index);
                    chosen.setVisibility(View.VISIBLE);
                    chosen.setText(words.get(indexText));
                    chosen.setTextColor(getResources().getColor(colors.get(colorText), null));
                } else {
                    showingColor = false;
                    if (!touched && currentColor != R.color.purple_sage_bush)
                        terminateChallenge(v);
                    currentColor = -1;
                    resetRect(index);
                }
            }

            @Override
            public void onFinish() {
                v.findViewById(R.id.FastPianoDescriptionTV).setVisibility(View.VISIBLE);
                ((TextView) v.findViewById(R.id.FastPianoDescriptionTV)).setText("You did it!");
                complete();
            }
        }.start();
    }

    private void complete() {
        handler.postDelayed(() -> {
            if (!missed)
                listener.challengeCompleted(this, polyline);
            else
                this.dismiss();
        }, 2000);
    }

    private void terminateChallenge(View v) {
        countDownTimer.cancel();
        missed = true;
        v.findViewById(R.id.FastPianoDescriptionTV).setVisibility(View.VISIBLE);
        ((TextView) v.findViewById(R.id.FastPianoDescriptionTV)).setText("You failed!");
        complete();
    }

    private void registerListeners(View v) {
        List<FrameLayout> fls = new ArrayList<>();
        fls.add(v.findViewById(R.id.btcnColor1));
        fls.add(v.findViewById(R.id.btcnColor2));
        fls.add(v.findViewById(R.id.btcnColor3));
        fls.add(v.findViewById(R.id.btcnColor4));

        for (int i = 0; i < fls.size(); i++) {
            int finalI = i;
            fls.get(i).setOnClickListener(view -> {
                int touchedColor = colors.get(finalI);

                if (currentColor == -1 || touchedColor != currentColor) {
                    if (countDownTimer != null) {
                        terminateChallenge(v);
                    }
                }
                touched = true;
            });
        }
    }

    private void resetRect(int index) {
        showColorTextViews.get(index).setVisibility(View.INVISIBLE);
        showColorRects.get(index).setBackgroundColor(getResources().getColor(R.color.alice_blue, null));
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