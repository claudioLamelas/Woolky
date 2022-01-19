package com.example.woolky.ui.games.escaperooms.challenges;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.woolky.R;
import com.example.woolky.domain.games.escaperooms.OnChallengeCompletedListener;
import com.example.woolky.utils.Utils;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ImitateSequenceDialog extends DialogFragment {

    private OnChallengeCompletedListener listener;

    private Polyline polyline;
    private CountDownTimer countDownTimer;
    private List<FrameLayout> tiles;
    private List<Integer> sequence;
    private Handler handler;
    private boolean itsOver;

    public ImitateSequenceDialog(Polyline polyline) {
        this.polyline = polyline;
        tiles = new ArrayList<>();
        sequence = new ArrayList<>();
        itsOver = false;
        handler = new Handler();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_imitate_sequence_dialog, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null)
            countDownTimer.cancel();
        handler.removeCallbacksAndMessages(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_imitate_sequence_dialog, null);

        FrameLayout fl0 = v.findViewById(R.id.tile0);
        FrameLayout fl1 = v.findViewById(R.id.tile1);
        FrameLayout fl2 = v.findViewById(R.id.tile2);
        FrameLayout fl3 = v.findViewById(R.id.tile3);
        FrameLayout fl4 = v.findViewById(R.id.tile4);
        FrameLayout fl5 = v.findViewById(R.id.tile5);
        FrameLayout fl6 = v.findViewById(R.id.tile6);
        FrameLayout fl7 = v.findViewById(R.id.tile7);
        FrameLayout fl8 = v.findViewById(R.id.tile8);
        tiles = Arrays.asList(fl0, fl1, fl2, fl3, fl4, fl5, fl6, fl7, fl8);

        v.findViewById(R.id.startSequence).setOnClickListener(v1 -> {

           countDownTimer = new CountDownTimer(6000, 500) {
                boolean red = false;
                int index = 0;
                Random random = new Random();

                @Override
                public void onTick(long millisUntilFinished) {
                    if (!red) {
                        red = true;
                        index = random.nextInt(9);
                        sequence.add(index);
                        tiles.get(index).setBackgroundColor(Color.RED);
                    } else {
                        red = false;
                        tiles.get(index).setBackgroundColor(getResources().getColor(R.color.alice_blue, null));
                    }
                }

                @Override
                public void onFinish() {
                    v.findViewById(R.id.startSequence).setVisibility(View.GONE);
                    v.findViewById(R.id.textView19).setVisibility(View.VISIBLE);
                    addListeners();
                }
            }.start();
        });

        builder.setView(v)
                .setTitle("Sequence Test")
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        return builder.create();
    }

    private void addListeners() {
        for (FrameLayout fl : tiles) {
            fl.setOnClickListener(v -> {
                if (!itsOver) {
                    int index = tiles.indexOf(fl);

                    if (index == sequence.get(0)) {
                        sequence.remove(0);

                        if (sequence.isEmpty()) {
                            itsOver = true;
                            listener.challengeCompleted(this, this.polyline);
                        }

                        fl.setBackgroundColor(Color.GREEN);
                        handler.postDelayed(() -> {
                            try {
                                fl.setBackgroundColor(getResources().getColor(R.color.alice_blue, null));
                            } catch (IllegalStateException ignored) {}
                        }, 200);
                    } else {
                        fl.setBackgroundColor(Color.RED);
                        itsOver = true;
                        Utils.showWarningSnackBar(getActivity(), getParentFragment().getView(), "Wrong sequence");
                        this.dismiss();
                    }
                }
            });
        }
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