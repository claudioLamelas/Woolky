package com.example.woolky.ui.profile;

import android.content.res.ColorStateList;
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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.User;
import com.google.firebase.auth.FirebaseAuth;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ProfileFragment extends Fragment {

    User signedInUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {



        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        v.findViewById(R.id.buttonLogOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut(v);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeActivity homeActivity = ((HomeActivity) getActivity());
        signedInUser = homeActivity.getSignedInUser();

        updateUiProfile(view);
    }

    private void updateUiProfile(View view) {

        TextView name = view.findViewById(R.id.userName_profile);
        name.setText(signedInUser.getUserName());

        ImageView photo = view.findViewById(R.id.image_profile);
        Glide.with(getActivity()).load(Uri.parse(signedInUser.getPhotoUrl())).circleCrop().into(photo);

        changeUserColor(view);

//        ConstraintLayout changeUserColor = view.findViewById(R.id.change_user_color_layout);
//        changeUserColor.setOnClickListener(v -> openColorPicker(signedInUser.getColor()));

        Button colorButton = view.findViewById(R.id.userColorButton);
        colorButton.setOnClickListener(v -> openColorPicker(signedInUser.getColor()));
    }

    private void changeUserColor(View view) {
//        ImageView userIcon = view.findViewById(R.id.user_icon);
//        userIcon.setColorFilter(signedInUser.getColor());

        Button colorButton = view.findViewById(R.id.userColorButton);
        colorButton.setBackgroundTintList(ColorStateList.valueOf(signedInUser.getColor()));
    }

    private void openColorPicker(int lateColor) {

        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this.getActivity(), lateColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
               signedInUser.setColor(color);
               changeUserColor(getView());

            }
        });

        colorPicker.show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //binding = null;
    }


    public void logOut(View v){
        FirebaseAuth.getInstance().signOut();
        HomeActivity home = (HomeActivity) getActivity();
        home.logout();

    }
}