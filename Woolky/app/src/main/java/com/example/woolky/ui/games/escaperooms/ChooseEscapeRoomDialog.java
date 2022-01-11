package com.example.woolky.ui.games.escaperooms;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.woolky.domain.games.escaperooms.EscapeRoom;
import com.example.woolky.domain.games.escaperooms.Quiz;
import com.example.woolky.ui.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class ChooseEscapeRoomDialog extends DialogFragment {

    public interface OnChosenEscapeRoomListener {
        void onEscapeRoomChosen(DialogFragment dialog, String escapeRoomId);
    }

    private OnChosenEscapeRoomListener listener;

    public ChooseEscapeRoomDialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        HomeActivity homeActivity = (HomeActivity) getActivity();
        ListView lv = v.findViewById(R.id.escapeRoomsList);
        DatabaseReference ref = homeActivity.getDatabaseRef();
        List<String> escapeRoomIds = new ArrayList<>();
        ref.child("escapeRooms").child(homeActivity.getSignedInUser().getUserId()).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot != null) {
                for (DataSnapshot er : dataSnapshot.getChildren()) {
                    escapeRoomIds.add(er.getKey());
                }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                    (getActivity(), android.R.layout.simple_list_item_1, escapeRoomIds);
            lv.setAdapter(arrayAdapter);
            }
        });

        lv.setOnItemClickListener((parent, view, position, id) -> {
            String escapeRoomId = escapeRoomIds.get(position);
            listener.onEscapeRoomChosen(this, escapeRoomId);
        });
        
        builder.setView(v)
                .setTitle("Choose an Escape Room")
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (OnChosenEscapeRoomListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnChosenEscapeRoomListener");
        }
    }
}