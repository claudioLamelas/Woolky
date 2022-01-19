package com.example.woolky.ui.friends;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woolky.R;
import com.example.woolky.domain.games.GameInviteSender;
import com.example.woolky.domain.user.User;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.utils.MarginItemDecoration;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FriendsListFragment extends Fragment {
    private RecyclerView recyclerView;
    private FriendsListAdapter adapter;
    private TextView noFriendsMessage;
    private User signedInUser;
    private DatabaseReference databaseRef;
    private List<User> users;
    private List<Friend> friends;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, container, false);
        recyclerView = view.findViewById(R.id.friends_list);
        noFriendsMessage = view.findViewById(R.id.no_friends_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MarginItemDecoration(getResources().getDimensionPixelSize(R.dimen.friends_li_padding)));

        view.findViewById(R.id.friendsBackButton).setOnClickListener(v -> getActivity().onBackPressed());

        HomeActivity homeActivity = ((HomeActivity) getActivity());
        databaseRef = homeActivity.getDatabaseRef();
        users = homeActivity.getUsers();
        signedInUser = homeActivity.getSignedInUser();
        friends = new ArrayList<>();
        if (signedInUser.getFriends() != null) {
            for (User user : users) {
                if (signedInUser.getFriends().contains(user.getUserId())) {
                    Friend friend = new Friend(user.getUserId(), user.getUserName(), user.getPhotoUrl());
                    friends.add(friend);
                }
                if (friends.size() == signedInUser.getFriends().size())
                    break;
            }
        }
        adapter = new FriendsListAdapter(friends);
        recyclerView.setAdapter(adapter);
        showMessageIfNoFriends(friends);

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);

        return view;
    }
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getBindingAdapterPosition();
            removeFriend(friends.get(position).getId(), position);

        }
        @Override
        public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,float dX, float dY,int actionState, boolean isCurrentlyActive){

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.red))
                    .addActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeLeftLabel("Remove Friend")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(),R.color.white))
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    private void removeFriend(String id, int position) {
        databaseRef.child("users").child(id).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot != null){
                User user = dataSnapshot.getValue(User.class);
                user.getFriends().remove(signedInUser.getUserId());
                databaseRef.child("users").child(id).setValue(user);
            }
        });

        signedInUser.getFriends().remove(id);
        friends.remove(position);
        databaseRef.child("users").child(signedInUser.getUserId()).setValue(signedInUser);
        adapter.notifyItemRemoved(position);
    }


    class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

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
            holder.name.setText(friends.get(position).name);
            holder.playButton.setOnClickListener(view -> {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                new GameInviteSender(homeActivity,
                        Arrays.asList(homeActivity.getSignedInUser().getUserId(), friends.get(position).id),
                        null, null).createGameInvite();
            }
            );
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

    private void showMessageIfNoFriends(List<Friend> friends) {
        if (friends.isEmpty()) {
            noFriendsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}