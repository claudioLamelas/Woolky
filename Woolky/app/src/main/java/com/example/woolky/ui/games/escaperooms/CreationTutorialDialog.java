package com.example.woolky.ui.games.escaperooms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.woolky.R;


public class CreationTutorialDialog extends DialogFragment {

    public CreationTutorialDialog() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_creation_tutorial_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_creation_tutorial_dialog, null);
        ((TextView) v.findViewById(R.id.textView20)).
                setText(getText(R.string.tutorial));
        builder.setView(v)
                .setTitle("Escape Room Creation Tutorial")
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}