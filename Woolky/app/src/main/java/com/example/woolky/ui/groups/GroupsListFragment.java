package com.example.woolky.ui.groups;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.woolky.R;
import com.example.woolky.domain.Group;
import com.example.woolky.domain.user.User;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.Collections;
import java.util.List;

public class GroupsListFragment extends Fragment{

    private User signedInUser;
    private DatabaseReference databaseRef;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);

        view.findViewById(R.id.groupsBackButton).setOnClickListener(v -> getActivity().onBackPressed());
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HomeActivity homeActivity = ((HomeActivity) getActivity());
        signedInUser = homeActivity.getSignedInUser();
        databaseRef = homeActivity.getDatabaseReference();

        Button newGroupButton = view.findViewById(R.id.newGroup);
        newGroupButton.setOnClickListener(v -> {
            createNewGroup();
        });
        updateUI();
    }


    private void updateUI() {
        databaseRef.child("users").child(signedInUser.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                signedInUser = dataSnapshot.getValue(User.class);
                updateUIList();
            }
        });
    }

    private void createNewGroup() {
        FragmentManager fm = getParentFragmentManager();
        AddNewGroupFragment addNewGroup = AddNewGroupFragment.newInstance("Add Group");
        addNewGroup.setTargetFragment(GroupsListFragment.this, 300);
        addNewGroup.show(fm, "fragment_edit_name");
    }

    private void updateUIList() {
        List<String> groupsIOwn = signedInUser.getGroupsIOwn();
        List<String> groupsIBelong = signedInUser.getGroupsIBelong();

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.scrollLayout);
        layout.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,20,10,10);

        TextView noGroupsOwn = new TextView(getContext());
        noGroupsOwn.setText("You don't own any group");
        noGroupsOwn.setTag("tv_no_groups_own");
        noGroupsOwn.setLayoutParams(params);
        noGroupsOwn.setGravity(Gravity.CENTER);

        TextView noGroupsBelong = new TextView(getContext());
        noGroupsBelong.setText("You don't belong to any group");
        noGroupsBelong.setTag("tv_no_groups_belong");
        noGroupsBelong.setLayoutParams(params);
        noGroupsBelong.setGravity(Gravity.CENTER);

        TextView groupsOwn = new TextView(getContext());
        groupsOwn.setText("Groups I Own");
        groupsOwn.setTag("tv_own");
        groupsOwn.setLayoutParams(params);
        groupsOwn.setGravity(Gravity.CENTER);

        TextView groupsBelong = new TextView(getContext());
        groupsBelong.setText("Groups I Belong");
        groupsBelong.setTag("tv_belong");
        groupsBelong.setLayoutParams(params);
        groupsBelong.setGravity(Gravity.CENTER);

        if (!groupsIOwn.isEmpty()) {

            Collections.reverse(groupsIOwn);
            TextView testOwn = layout.findViewWithTag("tv_own");
            if (testOwn == null) {
                layout.addView(groupsOwn);
            }

            for (String id : groupsIOwn) {
                Button btnTag = new Button(getContext());
                btnTag.setBackgroundResource(R.drawable.button_group);
                btnTag.setTextColor(Color.WHITE);
                btnTag.setTransformationMethod(null);
                btnTag.setTextSize(16);
                btnTag.setPadding(5,5,5,5);

                databaseRef.child("groups").child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Group current = dataSnapshot.getValue(Group.class);
                        btnTag.setText(current.getGroupName());
                    }
                });

                //add button to the layout
                btnTag.setOnClickListener(v -> {
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment, new GroupFragment(id, databaseRef)).addToBackStack(null).commit();
                });

                params.setMargins(0,20,0,0);
                btnTag.setLayoutParams(params);
                layout.addView(btnTag);
            }
        }
        else {
            layout.addView(noGroupsOwn);
        }


        if (!groupsIBelong.isEmpty()) {
            Collections.reverse(groupsIBelong);
            groupsBelong.setPadding(0, 50,0,0);
            TextView testBelong= layout.findViewWithTag("tv_belong");

            if (testBelong == null) {
                layout.addView(groupsBelong);
            }

            for (String name : groupsIBelong) {
                Button btnTag = new Button(getContext());
                btnTag.setBackgroundResource(R.drawable.button_group);
                btnTag.setTextColor(Color.WHITE);
                btnTag.setTransformationMethod(null);
                btnTag.setTextSize(16);
                btnTag.setPadding(5,5,5,5);

                databaseRef.child("groups").child(name).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Group current = dataSnapshot.getValue(Group.class);
                        btnTag.setText(current.getGroupName());
                    }
                });

                btnTag.setOnClickListener(v -> {
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment, new GroupFragment(name, databaseRef)).addToBackStack(null).commit();
                });

                params.setMargins(0,20,0,0);
                btnTag.setLayoutParams(params);
                layout.addView(btnTag);
            }
        }
        else {
            layout.addView(noGroupsBelong);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 0) {
            updateUI();
            Utils.showSuccesSnackBar(getActivity(), getView(), "New Group Created");
        }
    }
}