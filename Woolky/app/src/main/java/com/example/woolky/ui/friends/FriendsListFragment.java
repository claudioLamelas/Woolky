package com.example.woolky.ui.friends;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woolky.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.ShareLocationType;
import com.example.woolky.domain.User;
import com.example.woolky.utils.MarginItemDecoration;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FriendsListFragment extends Fragment {
    private RecyclerView recyclerView;
    private FriendsListAdapter adapter;
    private TextView noFriendsMessage;
    private User signedInUser;
    private DatabaseReference databaseRef;
    private List<User> users; //talvez nao seja boa ideia, mas funciona
    private List<Friend> friends;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_friends_list, container, false);
        recyclerView = view.findViewById(R.id.friends_list);
        noFriendsMessage = view.findViewById(R.id.no_friends_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MarginItemDecoration(getResources().getDimensionPixelSize(R.dimen.friends_li_padding)));
        // TODO: Remove this mock data and get DTOs from somewhere else (firebase, ...)
        HomeActivity homeActivity = ((HomeActivity) getActivity());
        databaseRef = homeActivity.getDatabaseRef();
        signedInUser = homeActivity.getSignedInUser();
        users = homeActivity.getUsers();
        updateUser(signedInUser.getUserId(),signedInUser);
        adapter = new FriendsListAdapter(friends);
        recyclerView.setAdapter(adapter);
        showMessageIfNoFriends(friends);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        return view;
    }
    Friend friend;
    String friend_name;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getBindingAdapterPosition();
            friend = friends.get(position);
            friend_name = friend.getName();
            removeFriend(friends.get(position).getId(),position);//apagar da base de dados
            adapter.notifyDataSetChanged();
            Snackbar.make(recyclerView,friend_name,Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friends.add(position,friend);
                    adapter.notifyDataSetChanged();
                }
            }).show();
        }
        @Override
        public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftLabel("Remove")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(),R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    private void removeFriendFromLists(int position){
        //Apagar do outro user

        for (User user : users){
            if (user.getFriends()!=null)
            {
                if (user.getUserId().equals(signedInUser.getFriends().get(position))){
                    for (String friend : user.getFriends()){
                        if (friend.equals(signedInUser.getUserId())){
                            user.getFriends().remove(friend);
                            updateUser(user.getUserId(), user);
                            break;
                        }
                    }
                }
            }
        }
        //Apaga do user em que estamos
        signedInUser.getFriends().remove(position);
        friends.remove(position);
    }

    private void removeFriend(String id,int position) {
        if (friends.size() == 1) {
            removeFriendFromLists(position);
            updateUser(signedInUser.getUserId(), signedInUser);
            deleteFriend(signedInUser.getUserId(),id);
            deleteGroup(signedInUser.getUserId());
            //friends.clear();
        }/*
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
            updateGroup();
        }*/
    }
    private void deleteFriend(String userID,String id){
        databaseRef.child("users").child(userID).child("friends").child(id).removeValue();
    }

    private void updateUser(String userID,User user){
        friends = new ArrayList<>();
        databaseRef.child("users").child(userID).setValue(user);
        if (user.getFriends()!=null)
        {
            for (String id : user.getFriends()){
                for (User user_n : users){
                    if (user_n.getUserId().equals(id)){
                        Friend friend = new Friend(user_n.getUserName(),user_n.getUserId());
                        friends.add(friend);
                    }
                }
            }
        }

    }

    private void deleteGroup(String userID){
        databaseRef.child("users").child(userID).child("friends").removeValue();
    }


    static class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {
        private List<Friend> friends;

        public FriendsListAdapter(List<Friend> friends) {
            this.friends = friends;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(view ->
                    Toast.makeText(view.getContext(), holder.name.getText() + "'s profile", Toast.LENGTH_SHORT).show()
            );
            //viewHolder.avatar = ...
            holder.name.setText(friends.get(position).name);
            holder.playButton.setOnClickListener(view ->
                    Toast.makeText(view.getContext(), "Play with " + holder.name.getText(), Toast.LENGTH_SHORT).show()
            );
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public View view;
            // public ImageView avatar; // TODO: Avatar should be specific to user. It is a mock for now
            public TextView name;
            public Button playButton;

            ViewHolder(View view) {
                super(view);
                this.view = view;
                // this.avatar = itemView.findViewById(R.id.avatar);
                this.name = itemView.findViewById(R.id.name);
                this.playButton = itemView.findViewById(R.id.play_button);
            }
        }
    }

    private void showMessageIfNoFriends(List<Friend> friends) {
        if (friends.isEmpty()) {
            noFriendsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}