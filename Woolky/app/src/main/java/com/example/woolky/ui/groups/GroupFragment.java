package com.example.woolky.ui.groups;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.example.woolky.R;
import com.example.woolky.domain.Group;
import com.example.woolky.domain.user.User;
import com.example.woolky.domain.games.GameInviteSender;
import com.example.woolky.domain.games.GameMode;
import com.example.woolky.ui.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;


public class GroupFragment extends Fragment {

    private String groupId;
    private DatabaseReference databaseRef;
    private Group current;
    private User signedInUser;
    private HomeActivity homeActivity;

    public GroupFragment() {}

    public GroupFragment(String groupId, DatabaseReference databaseRef) {
        this.groupId = groupId;
        this.databaseRef = databaseRef;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseRef.child("groups").child(groupId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                current = dataSnapshot.getValue(Group.class);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group, container, false);


        ImageButton leaveGroup = view.findViewById(R.id.leftGroupBt);
        leaveGroup.setOnClickListener(v -> {

            FragmentManager fm = getParentFragmentManager();
            LeaveGroupConfirmationDialogFragment leaveGroupDF = LeaveGroupConfirmationDialogFragment.newInstance();
            leaveGroupDF.setTargetFragment(GroupFragment.this, 300);
            leaveGroupDF.show(fm, "fragment_add_friends");
        });

        view.findViewById(R.id.inviteToGroupGameButton).setOnClickListener(v -> {
            GameInviteSender sender = new GameInviteSender((HomeActivity) getActivity(), current.getMembers(),
                    GameMode.ESCAPE_ROOM, null);
            sender.createGameInvite();
        });

        view.findViewById(R.id.groupBackButton).setOnClickListener(v -> getActivity().onBackPressed());


        return view;
    }

    private void leaveGroup() {

        if (current.getNumberMembers() == 1) {
            signedInUser.leaveGroupIOwn(groupId);
            updateUser(signedInUser.getUserId(), signedInUser);
            deleteGroup();

        } else if (current.getOwnerId().equals(signedInUser.getUserId())) {
            signedInUser.leaveGroupIOwn(groupId);
            updateUser(signedInUser.getUserId(), signedInUser);
            current.deleteOwner();
            String newGroupOwnerID = current.getOwnerId();

            databaseRef.child("users").child(newGroupOwnerID).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    User newOwner = dataSnapshot.getValue(User.class);
                    newOwner.changeFromBelongToOwn(groupId);
                    updateUser(newOwner.getUserId(), newOwner);
                    updateGroup();
                    goodbye();
                }
            });

        } else {

            signedInUser.leaveGroup(groupId);
            updateUser(signedInUser.getUserId(), signedInUser);
            current.deleteMember(signedInUser.getUserId());
            updateGroup();
            goodbye();
        }
    }

    private void updateGroup() {
        databaseRef.child("groups").child(groupId).setValue(current);
    }

    private void updateUser(String id, User user) {
        databaseRef.child("users").child(id).setValue(user);
    }

    private void deleteGroup() {
        databaseRef.child("groups").child(groupId).removeValue();
        goodbye();
    }

    private void goodbye() {
        getParentFragmentManager().beginTransaction().remove(this).commitNow();
        getParentFragmentManager().popBackStack();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView name = view.findViewById(R.id.groupName_TV);

        databaseRef.child("groups").child(groupId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                current = dataSnapshot.getValue(Group.class);

                name.setText(current.getGroupName());

                updateMembersUI(view);

                if (signedInUser.getUserId().equals(current.getOwnerId())){
                    Button addFriends = view.findViewById(R.id.addFriendsGroupBt);
                    addFriends.setVisibility(View.VISIBLE);
                    addFriends.setOnClickListener(v -> {
                        inviteFriendsToGroup(current);
                    });
                }
            }
        });

        homeActivity = ((HomeActivity) getActivity());
        signedInUser = homeActivity.getSignedInUser();
        List<User> membersUsers = new ArrayList<>();

        databaseRef.child("groups").child(groupId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                current = dataSnapshot.getValue(Group.class);

                List<String> m = current.getMembers();

                for (String id: m) {
                    databaseRef.child("users").child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            membersUsers.add(user);

                            if (membersUsers.size() == m.size())
                                handleTheAdapter(view, membersUsers);
                        }
                    });
                }
            }
        });


    }

    private void handleTheAdapter(View view, List<User> users) {
        ListView topMembersSteps = view.findViewById(R.id.top3_members);

        TopMembersGroupAdapter adapter = new TopMembersGroupAdapter(homeActivity, topMembersMostSteps(users), getWeekDay());
        topMembersSteps.setAdapter(adapter);

    }

    private int getWeekDay() {
        Calendar now = Calendar.getInstance();
        ArrayList<String> days = new ArrayList<>(7);
        int week = -now.get(GregorianCalendar.DAY_OF_WEEK) + 2; //add 2 if your week start on monday
        return week;
    }


    private List<User> topMembersMostSteps(List<User> users) {

        ArrayList<User> top3Members = new ArrayList<>(3);

        int week = getWeekDay();
        int max1 = 0, max2 = 0, max3 = 0;

        for ( User user : users) {
            int steps = user.getTotalNumberSteps(week);

            if (steps > max1){
                top3Members.add(0, user);
                max3 = max2;
                max2 = max1;
                max1 = steps;
            }
            else if(steps > max2){
                top3Members.add(1, user);
                max3 = max2;
                max2 = steps;

            }
            else if(steps > max3){
                top3Members.add(2, user);
                max3 = steps;
            }

        }

        return top3Members;

    }
    

    private void inviteFriendsToGroup(Group current) {
        FragmentManager fm = getParentFragmentManager();
        AddFriendsToGroupDialogFragment addNewGroup = AddFriendsToGroupDialogFragment.newInstance("Add Friends", current);
        addNewGroup.setTargetFragment(GroupFragment.this, 300);
        addNewGroup.show(fm, "fragment_add_friends");
    }

    private void updateMembersUI(View view) {
        List<String> members = current.getMembers();
        LinearLayout layout = view.findViewById(R.id.members_scroll_layout);
        layout.removeAllViews();

        for (String name : members) {
            databaseRef.child("users").child(name).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    User current = dataSnapshot.getValue(User.class);

                    ImageView photo = new ImageView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(170,
                            170);

                    params.setMargins(25,10,10,10);

                    photo.setLayoutParams(params);
                    Glide.with(getActivity()).load(Uri.parse(current.getPhotoUrl())).circleCrop().into(photo);

                    layout.addView(photo);
                }
            });
        }
    }

    /*
    Usado para deixar o grupo
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 0) {
            leaveGroup();
        }
        else if (requestCode == 2 && resultCode == 0) {
            onViewCreated(getView(), null);
        }
    }
}