package com.example.woolky.ui.games.escaperooms;

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

import com.example.woolky.R;
import com.example.woolky.ui.HomeActivity;

import java.util.Random;


public class CodeNumberDialog extends DialogFragment {

    private char codeNumber;

    public CodeNumberDialog(char codeNumber) {
        this.codeNumber = codeNumber;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_code_number_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_code_number_dialog, null);
        v.findViewById(R.id.returnToGameButton).setOnClickListener((view) -> this.dismiss());
        ((TextView)v.findViewById(R.id.codeNumber)).setText("Next Code Digit: " + codeNumber);
        builder.setView(v);
        return builder.create();
    }
}