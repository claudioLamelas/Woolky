package com.example.woolky.ui.groups;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woolky.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.ShareLocationType;
import com.example.woolky.domain.User;
import com.example.woolky.domain.Group;

import com.example.woolky.ui.friends.FriendsListFragment;
import com.example.woolky.ui.home.HomeFragment;
import com.example.woolky.utils.MarginItemDecoration;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class GroupsListFragment extends Fragment {
    private RecyclerView recyclerView;
    //private GroupsListAdapter adapter;
    private TextView noGroupsMessage;

    private User signedInUser;
    private DatabaseReference databaseRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ConstraintLayout view = (ConstraintLayout) inflater.inflate(R.layout.fragment_groups_list, container, false);
        //recyclerView = view.findViewById(R.id.groups_list);

        //noGroupsMessage = view.findViewById(R.id.no_groups_message);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.addItemDecoration(new MarginItemDecoration(getResources().getDimensionPixelSize(R.dimen.friends_li_padding)));
        // TODO: Remove this mock data and get DTOs from somewhere else (firebase, ...)




        //List<Group> groups = Arrays.asList(new Group("Group 1"), new Group("Group 2"));
        //adapter = new GroupsListAdapter(groups);
        //recyclerView.setAdapter(adapter);
        //showMessageIfNoGroups(groups);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        HomeActivity homeActivity = ((HomeActivity) getActivity());
        signedInUser = homeActivity.getSignedInUser();
        databaseRef = homeActivity.getDatabaseReference();

        Button newGroupButton = view.findViewById(R.id.newGroup);
        newGroupButton.setOnClickListener(v -> {
            // getLayoutInflater().inflate(R.id.fragment, new FriendsListFragment(), false);
            createNewGroup();

        });
        updateUI();


    }

    private void createNewGroup() {

        String nome = "Grupo1Teste";
        Group newGroup = new Group(nome,signedInUser.getUserId() );
        //update user

        DatabaseReference groupsRef = databaseRef.child("groups");
        DatabaseReference push = groupsRef.push();
        String key = push.getKey();
        groupsRef.child(key).setValue(newGroup);

        signedInUser.createNewGroup(key);
        databaseRef.child("users").child(signedInUser.getUserId()).setValue(signedInUser);

        updateUI();

//        usersRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                boolean newAccount = true;
//                for (DataSnapshot d : dataSnapshot.getChildren()) {
//                    User u = d.getValue(User.class);
//                    if (u.getUserId().equals(user.getUid())) {
//                        newAccount = false;
//                    }
//                }
//                if (newAccount) {
//                    User newUser = new User(user.getUid(), user.getDisplayName(), 0, R.color.user_default_color, ShareLocationType.ALL, user.getPhotoUrl().toString());
//                    usersRef.child(newUser.getUserId()).setValue(newUser).addOnSuccessListener((unused -> updateUI(user)));
//                } else {
//                    updateUI(user);
//                }
//            }
//        });


    }

    private void updateUI() {

        List<String> groups = signedInUser.getGroups();

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.scrollLayout);

        if (groups != null) {
            // ver se tem a mensagem de nenhum grupo
            for (String name : groups) {

                Button btnTag = new Button(getContext());


                databaseRef.child("groups").child(name).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        Group current = dataSnapshot.getValue(Group.class);
                        btnTag.setText(current.getGroupName());
                    }
                });

                //set the properties for button

                //btnTag.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

                //btnTag.setId(some_random_id);

                //add button to the layout
                btnTag.setOnClickListener(v -> {
                    // getLayoutInflater().inflate(R.id.fragment, new FriendsListFragment(), false);
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment, new GroupFragment()).addToBackStack(null).commit();


                });

                layout.addView(btnTag);
            }
        }
        else {
            TextView noGroups = new TextView(getContext());
            noGroups.setText("Não pertence a nenhum grupo");

            layout.addView(noGroups);

        }



    }


    /*static class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {
        private List<Group> groups;

        public GroupsListAdapter(List<Group> groups) {
            this.groups = groups;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.itemView.setOnClickListener(view ->
                    Toast.makeText(view.getContext(), "Clicked on " + holder.name.getText(), Toast.LENGTH_SHORT).show()
            );
            //viewHolder.icon = ...
            holder.name.setText(groups.get(position).name);
            holder.playButton.setOnClickListener(view ->
                    Toast.makeText(view.getContext(), "Play with " + holder.name.getText(), Toast.LENGTH_SHORT).show()
            );
        }

        @Override
        public int getItemCount() {
            return groups.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public View view;
            // public ImageView icon; // TODO: Icon should be specific to group. It is a mock for now
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
    }*/



    private void showMessageIfNoGroups(List<Group> groups) {
        if (groups.isEmpty()) {
            noGroupsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}