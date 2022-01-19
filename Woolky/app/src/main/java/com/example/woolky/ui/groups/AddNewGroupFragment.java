package com.example.woolky.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.woolky.R;
import com.example.woolky.domain.Group;
import com.example.woolky.domain.user.User;
import com.example.woolky.ui.HomeActivity;
import com.google.firebase.database.DatabaseReference;


public class AddNewGroupFragment extends DialogFragment {



    public AddNewGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    public static AddNewGroupFragment newInstance(String title) {
        AddNewGroupFragment frag = new AddNewGroupFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_group, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_add_new_group, null);

        builder.setView(v)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Create", (dialog, which) -> createNewGroup(v));
        return builder.create();
    }

    public void createNewGroup(View view) {
        EditText groupNameET = view.findViewById(R.id.groupsNameInput);
        String groupName = groupNameET.getText().toString();

        if (!groupName.isEmpty()) {
            HomeActivity ha = (HomeActivity) getActivity();
            DatabaseReference databaseRef = ha.getDatabaseRef();
            User owner = ha.getSignedInUser();



            DatabaseReference groupsRef = databaseRef.child("groups");
            DatabaseReference push = groupsRef.push();
            String key = push.getKey();
            Group newGroup = new Group(groupName, owner.getUserId(),key);
            groupsRef.child(key).setValue(newGroup);

            owner.createNewGroup(key);
            databaseRef.child("users").child(owner.getUserId()).setValue(owner);

            sendBack();
        }
    }


    public void sendBack() {

        Intent i = new Intent();
        getTargetFragment().onActivityResult(1,0, i);
        dismiss();
    }
}