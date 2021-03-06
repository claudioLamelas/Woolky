package com.example.woolky.ui.games.escaperooms.challenges;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.woolky.R;


public class InputDataDialog extends DialogFragment {


    public interface OnDataSubmitted {
        void processData(DialogFragment dialogFragment, String inputData);
    }

    private OnDataSubmitted listener;
    private String title;
    private String text;
    private String hint;
    private int inputType;

    public InputDataDialog(String title, String text, String hint, int inputType) {
        this.title = title;
        this.text = text;
        this.hint = hint;
        this.inputType = inputType;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_input_data_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_input_data_dialog, null);

        EditText input = v.findViewById(R.id.inputField);
        input.setText(text);
        input.setHint(hint);
        input.setInputType(inputType);

        builder.setView(v)
                .setTitle(title)
                .setPositiveButton("Submit", (dialog, id) -> {
                    retrieveInputData(v);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        return builder.create();
    }

    private void retrieveInputData(View v) {
        EditText input = v.findViewById(R.id.inputField);
        listener.processData(this, input.getText().toString());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnDataSubmitted) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnDataSubmitted");
        }
    }
}