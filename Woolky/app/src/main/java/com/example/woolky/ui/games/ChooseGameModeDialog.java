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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.woolky.R;
import com.example.woolky.domain.games.GameInviteSender;
import com.example.woolky.domain.games.GameMode;


public class ChooseGameModeDialog extends DialogFragment {

    private OnGameModeChosenListener listener;

    public interface OnGameModeChosenListener {
        void onChosenGameMode(DialogFragment dialog, GameMode gameMode);
    }

    public ChooseGameModeDialog(GameInviteSender gameInviteSender) {
        listener = gameInviteSender;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_escape_rooms_list, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_escape_rooms_list, null);
        ListView lv = v.findViewById(R.id.escapeRoomsList);
        ArrayAdapter<GameMode> arrayAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_list_item_1, GameMode.values());
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener((parent, view, position, id) -> {
            GameMode gameMode = GameMode.values()[position];
            listener.onChosenGameMode(this, gameMode);
        });

        builder.setView(v)
                .setTitle("Choose a Game Mode")
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        return builder.create();
    }
}