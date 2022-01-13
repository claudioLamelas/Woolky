package com.example.woolky.ui.friends;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woolky.domain.games.GameInviteSender;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.User;
import com.example.woolky.utils.MarginItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendsListFragment extends Fragment {
    private RecyclerView recyclerView;
    private FriendsListAdapter adapter;
    private TextView noFriendsMessage;
    private User signedInUser;
    private List<User> users; //talvez nao seja boa ideia, mas funciona

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_friends_list, container, false);
        recyclerView = view.findViewById(R.id.friends_list);
        noFriendsMessage = view.findViewById(R.id.no_friends_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MarginItemDecoration(getResources().getDimensionPixelSize(R.dimen.friends_li_padding)));
        // TODO: Remove this mock data and get DTOs from somewhere else (firebase, ...)

        HomeActivity homeActivity = ((HomeActivity) getActivity());
        users = homeActivity.getUsers();
        signedInUser = homeActivity.getSignedInUser();
        List<Friend> friends = new ArrayList<>();
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
        return view;
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
            holder.itemView.setOnClickListener(view ->
                    Toast.makeText(view.getContext(), holder.name.getText() + "'s profile", Toast.LENGTH_SHORT).show()
            );
            //viewHolder.avatar = ...
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