package com.example.woolky.ui.groups;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.woolky.R;
import com.example.woolky.domain.Group;
import com.example.woolky.domain.User;
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
        View view = inflater.inflate(R.layout.fragment_add_new_group, container, false);

        Button newGroup = view.findViewById(R.id.create_new_group);
        newGroup.setOnClickListener(v -> createNewGroup(view));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = getDialog().getWindow().getWindowManager().getDefaultDisplay();
        display.getSize(size);

        getDialog().getWindow().setLayout((int)(size.x * 0.85),(int)(size.y * 0.60));
    }

    private void createNewGroup(View view) {
        EditText groupNameET = view.findViewById(R.id.groupsNameInput);
        String groupName = groupNameET.getText().toString();

        if(!groupName.isEmpty()){
            HomeActivity ha = (HomeActivity) getActivity();
            DatabaseReference databaseRef = ha.getDatabaseRef();
            User owner = ha.getSignedInUser();

            Group newGroup = new Group(groupName, owner.getUserId());

            DatabaseReference groupsRef = databaseRef.child("groups");
            DatabaseReference push = groupsRef.push();
            String key = push.getKey();
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