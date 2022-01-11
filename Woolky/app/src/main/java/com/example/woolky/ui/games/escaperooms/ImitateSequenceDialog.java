package com.example.woolky.ui.games.escaperooms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.woolky.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ImitateSequenceDialog extends DialogFragment {

    private CountDownTimer countDownTimer;
    private List<FrameLayout> tiles;
    private List<Integer> sequence;
    private boolean missed;

    public ImitateSequenceDialog() {
        tiles = new ArrayList<>();
        sequence = new ArrayList<>();
        missed = false;
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

           countDownTimer = new CountDownTimer(4000, 1000) {
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
                if (!missed) {
                    Handler handler = new Handler();
                    int index = tiles.indexOf(fl);

                    if (index == sequence.get(0)) {
                        sequence.remove(0);

                        if (sequence.isEmpty())
                            missed = true;

                        fl.setBackgroundColor(Color.GREEN);
                        handler.postDelayed(() -> {
                            fl.setBackgroundColor(getResources().getColor(R.color.alice_blue, null));
                        }, 200);
                    } else {
                        fl.setBackgroundColor(Color.RED);
                        missed = true;
                    }
                }
            });
        }
    }
}