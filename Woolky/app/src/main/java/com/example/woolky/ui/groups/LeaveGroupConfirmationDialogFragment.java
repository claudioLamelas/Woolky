package com.example.woolky.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.woolky.R;
import com.example.woolky.domain.games.GameMode;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaveGroupConfirmationDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LeaveGroupConfirmationDialogFragment extends DialogFragment {

    public LeaveGroupConfirmationDialogFragment() {}


    public static LeaveGroupConfirmationDialogFragment newInstance() {
        LeaveGroupConfirmationDialogFragment fragment = new LeaveGroupConfirmationDialogFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_leave_group_confirmation, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(getView())
                .setTitle("Leave the group")
                .setMessage("Are you sure you want to leave the group?")
                .setPositiveButton("YES",(dialog, id) -> leaveGroup())
                .setNegativeButton("NO", (dialog, id) -> dialog.dismiss());
        return builder.create();
    }


    public void leaveGroup() {
        Intent i = new Intent();
        getTargetFragment().onActivityResult(1,0, i);
        dismiss();
    }
}