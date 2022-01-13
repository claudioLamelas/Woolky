package com.example.woolky.ui.groups;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.woolky.R;
import com.example.woolky.domain.Group;
import com.example.woolky.domain.User;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.friends.Friend;
import com.example.woolky.ui.friends.FriendsListFragment;
import com.example.woolky.utils.MarginItemDecoration;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class addFriendsToGroup extends Fragment {


    private final String groupId;
    private List<String> currentMembers;

    private RecyclerView recyclerView;

    private TextView noFriendsMessage;

    private User signedInUser;

    private addFriendsToGroup.FriendsListGroupAdapter adapter;

    private HomeActivity homeActivity;

    private DatabaseReference databaseRef;

    public addFriendsToGroup(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //trocar aqui
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_add_friends_to_group, container, false);

        //trocar aqui
        recyclerView = view.findViewById(R.id.listFriendsToAdd);
        noFriendsMessage = view.findViewById(R.id.no_friends_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MarginItemDecoration(getResources().getDimensionPixelSize(R.dimen.friends_li_padding)));

        // TODO: Remove this mock data and get DTOs from somewhere else (firebase, ...)

        HomeActivity homeActivity = ((HomeActivity) getActivity());

        signedInUser = homeActivity.getSignedInUser();

        List<Friend> friends = new ArrayList<>();


        //adapter.notifyDataSetChanged();

         homeActivity = (HomeActivity) getActivity();
         databaseRef = homeActivity.getDatabaseRef();
         List<User> users = homeActivity.getUsers();
         List<String> friendsId = signedInUser.getFriends();

        if (signedInUser.getFriends()!=null)    {

            for (User friend: users) {
                if (friendsId.contains(friend.getUserId())){

                    friends.add(new Friend(friend.getUserName(), friend.getPhotoUrl(), friend.getUserId()));
                }

            }

        }

        adapter = new addFriendsToGroup.FriendsListGroupAdapter(friends);
        //adapter = new addFriendsToGroup.FriendsListGroupAdapter(friends);
        //adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        //showMessageIfNoFriends(friends);
        return view;
    }

    class FriendsListGroupAdapter extends RecyclerView.Adapter<addFriendsToGroup.FriendsListGroupAdapter.ViewHolder> {
        private List<Friend> friends;

        public FriendsListGroupAdapter(List<Friend> friends) {
            this.friends = friends;
        }

        @NonNull
        @Override
        public addFriendsToGroup.FriendsListGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
            return new addFriendsToGroup.FriendsListGroupAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull addFriendsToGroup.FriendsListGroupAdapter.ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(view ->
                    Toast.makeText(view.getContext(), holder.name.getText() + "'s profile", Toast.LENGTH_SHORT).show()
            );
            //viewHolder.avatar = ...
            holder.name.setText(friends.get(position).name);
            holder.playButton.setText("Invite");
            holder.playButton.setOnClickListener(view ->{
                   addFriendToGroup(friends.get(position).id);
            Toast.makeText(view.getContext(), holder.name.getText() + "'added friend", Toast.LENGTH_SHORT).show(); });
            Glide.with(getActivity()).load(Uri.parse(friends.get(position).photoUrl)).circleCrop().into(holder.avatar);

        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public ImageView avatar;
            public TextView name;
            public Button playButton;

            ViewHolder(View view) {
                super(view);
                this.view = view;
                this.avatar = itemView.findViewById(R.id.avatar);
                this.name = itemView.findViewById(R.id.name);
                this.playButton = itemView.findViewById(R.id.play_button);
            }
        }
    }

    private void addFriendToGroup(String id) {

        databaseRef.child("groups").child(groupId).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                Group current = dataSnapshot.getValue(Group.class);
                current.addMember(id);
                databaseRef.child("groups").child(groupId).setValue(current);

                databaseRef.child("users").child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        User friend = dataSnapshot.getValue(User.class);
                        friend.addNewGroup(groupId);
                        databaseRef.child("users").child(friend.getUserId()).setValue(friend);




                    }
                });



            }
        });
    }

    private void showMessageIfNoFriends(List<Friend> friends) {
        if (friends.isEmpty()) {
            noFriendsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}