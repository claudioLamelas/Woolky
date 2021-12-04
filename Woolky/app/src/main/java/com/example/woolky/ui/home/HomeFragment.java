package com.example.woolky.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.woolky.R;
import com.example.woolky.ui.friends.FriendsListFragment;
import com.example.woolky.ui.groups.GroupsListFragment;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
//import com.example.woolky.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    //private FragmentHomeBinding binding;


    private FirebaseUser current_user;


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

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        current_user = getArguments().getParcelable("current_user");

        updateUiCurrentUser(view);


        return view;
    }

    private void updateUiCurrentUser(View view) {

        String currentDate = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());

        TextView date = view.findViewById(R.id.dateDaily);
        date.setText(currentDate);


        String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
        Log.d("TIME", currentTime);

        TextView time_of_greeting = view.findViewById(R.id.time_of_day);

        int hours = Integer.parseInt(currentTime);

        String greeting = "";
        if (hours >= 6 && hours < 13) {
            greeting = "Good Morning";
        }
        else if (hours >= 13 && hours < 20) {
            greeting = "Good Afternoon";
        }
        else {
            greeting = "Good Evening";
        }
        time_of_greeting.setText(greeting);



        TextView name = view.findViewById(R.id.username);
        name.setText(current_user.getDisplayName());
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