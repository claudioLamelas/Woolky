package com.example.woolky.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.woolky.R;
//import com.example.woolky.databinding.FragmentNotificationsBinding;

public class ProfileFragment extends Fragment {

    private ProfileViewModel notificationsViewModel;
//private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
/*        notificationsViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

    binding = FragmentNotificationsBinding.inflate(inflater, container, false);
    View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;*/


        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }
}