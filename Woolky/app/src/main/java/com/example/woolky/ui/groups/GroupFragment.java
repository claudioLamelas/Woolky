package com.example.woolky.ui.groups;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.example.woolky.R;
import com.example.woolky.domain.Group;
import com.example.woolky.domain.User;
import com.example.woolky.domain.games.GameInviteSender;
import com.example.woolky.domain.games.GameMode;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.List;


public class GroupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String groupId;
    private DatabaseReference databaseRef;
    private Group current;
    private User signedInUser;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GroupFragment() {
        // Required empty public constructor
    }

    public GroupFragment(String groupId, DatabaseReference databaseRef) {
        this.groupId = groupId;
        Log.d("id", groupId);
        this.databaseRef = databaseRef;

        databaseRef.child("groups").child(groupId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                current = dataSnapshot.getValue(Group.class);
                Log.d("nome", current.getGroupName());


            }
        });
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);


        ImageButton leaveGroup = view.findViewById(R.id.leftGroupBt);
        leaveGroup.setOnClickListener(v -> {

            FragmentManager fm = getParentFragmentManager();
            LeaveGroupConfirmationDialogFragment leaveGroupDF = LeaveGroupConfirmationDialogFragment.newInstance();
            // SETS the target fragment for use later when sending results
            leaveGroupDF.setTargetFragment(GroupFragment.this, 300);
            //fm.putFragment();
            leaveGroupDF.show(fm, "fragment_add_friends");


        });

        view.findViewById(R.id.inviteToGroupGameButton).setOnClickListener(v -> {
            GameInviteSender sender = new GameInviteSender((HomeActivity) getActivity(), current.getMembers(),
                    GameMode.ESCAPE_ROOM, null);
            sender.createGameInvite();
        });


        return view;
    }

    private void leaveGroup() {

        if (current.getNumberMembers() == 1) {
            signedInUser.leaveGroupIOwn(groupId);
            updateUser(signedInUser.getUserId(), signedInUser);
            deleteGroup();
        }


        else if (current.getOwnerId().equals(signedInUser.getUserId())) {

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


        }
        else {

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
        getParentFragmentManager().beginTransaction().replace(R.id.fragment, new HomeFragment()).commit();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView name = view.findViewById(R.id.groupName_TV);

        //nao eh rapido

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
                        inviteFriendsToGroup();
                    });
                }

            }
        });


        HomeActivity homeActivity = ((HomeActivity) getActivity());
        signedInUser = homeActivity.getSignedInUser();





    }

    private void inviteFriendsToGroup() {

        /*FrameLayout addFriends = getView().findViewById(R.id.addFriendsGroupFragment);
        addFriends.setVisibility(View.VISIBLE);
        getParentFragmentManager().beginTransaction().replace(R.id.addFriendsGroupFragment, new AddFriendsToGroupDialogFragment(groupId)).commit();
*/

        FragmentManager fm = getParentFragmentManager();
        AddFriendsToGroupDialogFragment addNewGroup = AddFriendsToGroupDialogFragment.newInstance("Add Friends", groupId);
        // SETS the target fragment for use later when sending results
        addNewGroup.setTargetFragment(GroupFragment.this, 300);
        //fm.putFragment();
        addNewGroup.show(fm, "fragment_add_friends");
    }

    private void updateMembersUI(View view) {

        List<String> members = current.getMembers();



        for (String name : members) {


            databaseRef.child("users").child(name).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    User current = dataSnapshot.getValue(User.class);

                    ImageView photo = new ImageView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(200,
                            200);

                    params.setMargins(25,10,10,10);


                    photo.setLayoutParams(params);


                    //lp.setMargins(8,8,8,8));
                    //photo.setImageURI(null);
                    //photo.setImageURI(Uri.parse(signedInUser.getPhotoUrl()));
                    Glide.with(getActivity()).load(Uri.parse(current.getPhotoUrl())).circleCrop().into(photo);

                    LinearLayout layout = view.findViewById(R.id.members_scroll_layout);
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

    }
}