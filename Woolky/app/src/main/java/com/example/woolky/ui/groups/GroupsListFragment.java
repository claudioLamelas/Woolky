package com.example.woolky.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.woolky.R;
import com.example.woolky.ui.friends.Friend;
import com.example.woolky.utils.MarginItemDecoration;

import java.util.Arrays;
import java.util.List;

public class GroupsListFragment extends Fragment {
    private RecyclerView recyclerView;
    private GroupsListAdapter adapter;
    private TextView noGroupsMessage;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.fragment_groups_list, container, false);
        recyclerView = view.findViewById(R.id.groups_list);
        noGroupsMessage = view.findViewById(R.id.no_groups_message);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new MarginItemDecoration(getResources().getDimensionPixelSize(R.dimen.friends_li_padding)));
        // TODO: Remove this mock data and get DTOs from somewhere else (firebase, ...)
        List<Group> groups = Arrays.asList(new Group("Group 1"), new Group("Group 2"));
        adapter = new GroupsListAdapter(groups);
        recyclerView.setAdapter(adapter);
        showMessageIfNoGroups(groups);
        return view;
    }

    static class GroupsListAdapter extends RecyclerView.Adapter<GroupsListAdapter.ViewHolder> {
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
    }

    private void showMessageIfNoGroups(List<Group> groups) {
        if (groups.isEmpty()) {
            noGroupsMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }
}