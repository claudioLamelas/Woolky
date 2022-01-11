package com.example.woolky.ui.groups;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.woolky.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.Group;
import com.example.woolky.domain.User;
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


        Log.d("nome", "teste");

        return view;
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

            }
        });

        //name.setText(current.getGroupName());

        //Log.d("nome", current.getGroupName());

        /////!!!!!!!!!!!!!!!!!!!!!!!!!

        HomeActivity homeActivity = ((HomeActivity) getActivity());
        signedInUser = homeActivity.getSignedInUser();






    }

    private void updateMembersUI(View view) {

        List<String> members = current.getMembers();



        for (String name : members) {


            databaseRef.child("users").child(name).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    User current = dataSnapshot.getValue(User.class);

                    ImageView photo = new ImageView(getContext());
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
}