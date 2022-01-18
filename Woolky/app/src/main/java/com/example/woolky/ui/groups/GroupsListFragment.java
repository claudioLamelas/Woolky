package com.example.woolky.ui.groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.woolky.R;
import com.example.woolky.domain.user.User;
import com.example.woolky.domain.Group;

import com.example.woolky.ui.HomeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.Collections;
import java.util.List;

public class GroupsListFragment extends Fragment{
    private RecyclerView recyclerView;
    //private GroupsListAdapter adapter;
    private TextView noGroupsMessage;

    private User signedInUser;
    private DatabaseReference databaseRef;

    private int indexLastButton = 1;

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
            // getLayoutInflater().inflate(R.id.fragment, new FriendsListFragment(), false);
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
        // SETS the target fragment for use later when sending results
        addNewGroup.setTargetFragment(GroupsListFragment.this, 300);
        //fm.putFragment();
        addNewGroup.show(fm, "fragment_edit_name");




        //updateUI();

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

    private void updateUIList() {

        List<String> groupsIOwn = signedInUser.getGroupsIOwn();
        List<String> groupsIBelong = signedInUser.getGroupsIBelong();

        LinearLayout layout = (LinearLayout) getView().findViewById(R.id.scrollLayout);

        /*
        ESTA LINHA ELIMINA METADE IFS VERIFICACAO :((

        NAO DEVE SER O MAIS CORRETO APAGAR TO_DO O LAYOUT E FAZER DE NOVO
         */
        layout.removeAllViews();


        TextView noGroupsOwn = new TextView(getContext());
        noGroupsOwn.setText("You don't own any group");
        noGroupsOwn.setTag("tv_no_groups_own");

        TextView noGroupsBelong = new TextView(getContext());
        noGroupsBelong.setText("You don't belong to any group");
        noGroupsBelong.setTag("tv_no_groups_belong");

        TextView groupsOwn = new TextView(getContext());
        groupsOwn.setText("Groups I Own");
        groupsOwn.setTag("tv_own");

        TextView groupsBelong = new TextView(getContext());
        groupsBelong.setText("Groups I Belong");
        groupsBelong.setTag("tv_belong");


        if (!groupsIOwn.isEmpty()) {

            Collections.reverse(groupsIOwn);

/*            TextView testNoGroups = layout.findViewWithTag("tv_no_groups_own");
            if (testNoGroups != null) {
                layout.removeView(noGroupsOwn);
            }*/

            TextView testOwn = layout.findViewWithTag("tv_own");
            if (testOwn == null) {
                layout.addView(groupsOwn);
            }



            // nao eh name eh ID
            for (String id : groupsIOwn) {


                Button btnTag = new Button(getContext());


                databaseRef.child("groups").child(id).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
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
                    getParentFragmentManager().beginTransaction().replace(R.id.fragment, new GroupFragment(id, databaseRef)).addToBackStack(null).commit();


                });

                layout.addView(btnTag);
                //indexLastButton++;
            }
        }
        else {

            layout.addView(noGroupsOwn);

        }




        if (!groupsIBelong.isEmpty()) {

            Collections.reverse(groupsIBelong);

/*            TextView test = layout.findViewWithTag("tv_no_groups_belong");
            if (test != null) {
                layout.removeView(noGroupsBelong);
            }*/


            TextView testBelong= layout.findViewWithTag("tv_belong");
            if (testBelong == null) {
                layout.addView(groupsBelong);
            }


            // ver se tem a mensagem de nenhum grupo
            for (String name : groupsIBelong) {

                Button btnTag = new Button(getContext());


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

                layout.addView(btnTag);
            }
        }
        else {

            layout.addView(noGroupsBelong);

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == 0) {
            updateUI();
            Toast.makeText(getView().getContext(), "New Group created", Toast.LENGTH_SHORT).show();
        }

    }

    private void showMessageIfNoGroups(List<Group> groups) {
        if (groups.isEmpty()) {
            noGroupsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


}