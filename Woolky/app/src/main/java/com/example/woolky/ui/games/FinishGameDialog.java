package com.example.woolky.ui.games;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FinishGameDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinishGameDialog extends DialogFragment {

    private static final String ARG_PARAM1 = "param1";

    private String message;

    public FinishGameDialog() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param message message to be shown
     * @return A new instance of fragment TicTacToeFinishDialog.
     */
    public static FinishGameDialog newInstance(String message) {
        FinishGameDialog fragment = new FinishGameDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tic_tac_toe_finish_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_tic_tac_toe_finish_dialog, null);
        v.findViewById(R.id.returnToMapButton).setOnClickListener((view) -> ((HomeActivity) getActivity()).changeToMap());
        ((TextView)v.findViewById(R.id.finishGameMessage)).setText(message);
        builder.setView(v);
        return builder.create();
    }
}