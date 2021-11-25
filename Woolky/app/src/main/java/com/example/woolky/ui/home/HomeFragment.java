package com.example.woolky.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.woolky.R;
import com.example.woolky.ui.friends.FriendsListFragment;
import com.example.woolky.ui.groups.GroupsListFragment;
//import com.example.woolky.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    //private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
       /* homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;*/

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button friendsButton = view.findViewById(R.id.friendsButton);
        Button groupsButton = view.findViewById(R.id.groupsButton);
        friendsButton.setOnClickListener(v -> {
            // getLayoutInflater().inflate(R.id.fragment, new FriendsListFragment(), false);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment, new FriendsListFragment()).addToBackStack(null).commit();
        });
        groupsButton.setOnClickListener(v -> {
            // getLayoutInflater().inflate(R.id.fragment, new FriendsListFragment(), false);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment, new GroupsListFragment()).addToBackStack(null).commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }
}