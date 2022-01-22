package com.example.woolky.ui.groups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.woolky.R;
import com.example.woolky.domain.Group;
import com.example.woolky.domain.user.User;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.friends.Friend;
import com.example.woolky.utils.MarginItemDecoration;
import com.example.woolky.utils.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class AddFriendsToGroupDialogFragment extends DialogFragment {

    private Group group;
    private RecyclerView recyclerView;
    private TextView noFriendsMessage;
    private User signedInUser;
    private AddFriendsToGroupDialogFragment.FriendsListGroupAdapter adapter;
    private HomeActivity homeActivity;
    private DatabaseReference databaseRef;

    public AddFriendsToGroupDialogFragment(Group current) {
        group = current;
    }

    public static AddFriendsToGroupDialogFragment newInstance(String title, Group group) {
        AddFriendsToGroupDialogFragment frag = new AddFriendsToGroupDialogFragment(group);
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return (RelativeLayout) inflater.inflate(R.layout.fragment_add_friends_to_group, container, false);
    }

    class FriendsListGroupAdapter extends RecyclerView.Adapter<AddFriendsToGroupDialogFragment.FriendsListGroupAdapter.ViewHolder> {
        private List<Friend> friends;

        public FriendsListGroupAdapter(List<Friend> friends) {
            this.friends = friends;
        }

        @NonNull
        @Override
        public AddFriendsToGroupDialogFragment.FriendsListGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
            return new AddFriendsToGroupDialogFragment.FriendsListGroupAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AddFriendsToGroupDialogFragment.FriendsListGroupAdapter.ViewHolder holder, int position) {
            holder.name.setText(friends.get(position).name);
            holder.playButton.setText("Add");
            holder.playButton.setOnClickListener(view ->{
                   addFriendToGroup(friends.get(position).id);
                Utils.showSuccesSnackBar(getActivity(), view, holder.name.getText() + " added to group!");
            });
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
                if (!group.hasMember(id)) {
                    group.addMember(id);
                    databaseRef.child("groups").child(group.getId()).setValue(group);

                    databaseRef.child("users").child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            User friend = dataSnapshot.getValue(User.class);
                            friend.addNewGroup(group.getId());
                            databaseRef.child("users").child(friend.getUserId()).setValue(friend);
                        }
                    });
                }
    }

    private void showMessageIfNoFriends(List<Friend> friends) {
        if (friends.isEmpty()) {
            noFriendsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_friends_to_group, null);


        recyclerView = view.findViewById(R.id.listFriendsToAdd);
        noFriendsMessage = view.findViewById(R.id.no_friends_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MarginItemDecoration(getResources().getDimensionPixelSize(R.dimen.friends_li_padding)));

        homeActivity = (HomeActivity) getActivity();

        signedInUser = homeActivity.getSignedInUser();


        databaseRef = homeActivity.getDatabaseRef();
        List<User> users = homeActivity.getUsers();
        List<Friend> friends = new ArrayList<>();

        List<String> friendsId = signedInUser.getFriends();
        List<String> members = group.getMembers();


        if (friendsId != null) {
            for (User user : users) {
                if (friendsId.contains(user.getUserId()) && !members.contains(user.getUserId())) {
                    friends.add(new Friend(user.getUserId(), user.getUserName(), user.getPhotoUrl()));
                }
            }
        }

        adapter = new AddFriendsToGroupDialogFragment.FriendsListGroupAdapter(friends);
        recyclerView.setAdapter(adapter);
        builder.setView(view)
                .setTitle("Add friends to Group")
                .setNegativeButton("Close", (dialog, id) -> {
                    Intent i = new Intent();
                    getTargetFragment().onActivityResult(2,0, i);
                    dialog.dismiss();
                });
        return builder.create();

    }




}