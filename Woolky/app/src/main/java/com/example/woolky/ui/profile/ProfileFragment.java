package com.example.woolky.ui.profile;

import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.woolky.domain.ShareLocationType;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import yuku.ambilwarna.AmbilWarnaDialog;

public class ProfileFragment extends Fragment implements AdapterView.OnItemSelectedListener {

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


        Spinner spin = view.findViewById(R.id.visibility_spinner);
        spin.setOnItemSelectedListener(this);
        ArrayList<String> options = new ArrayList<>();
        options.add( "For all users");
        options.add( "For friends");
        options.add( "Not visible");
        //ArrayAdapter aa = new ArrayAdapter();
        ArrayAdapter aa = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item,options);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(aa);

        ShareLocationType chosen = signedInUser.getVisibilityType();
        switch (chosen) {
            case ALL:
                spin.setSelection(0);

            case FRIENDS_ONLY:
                spin.setSelection(1);

            case NOBODY:
                spin.setSelection(2);

        }




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
               updateUser();

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Log.d("pos", String.valueOf(position));

        //switch NãO FUNCIONA!!!

        switch (position) {
            case 0 :
                signedInUser.setVisibilityType(ShareLocationType.ALL);
                break;

            case 1 :
                signedInUser.setVisibilityType(ShareLocationType.FRIENDS_ONLY);
                break;

            case 2 :
                signedInUser.setVisibilityType(ShareLocationType.NOBODY);
                break;

        }

        //signedInUser.setVisibilityType(ShareLocationType.ALL);


        Log.d("ENUM", String.valueOf(signedInUser.getVisibilityType()));

        updateUser();

       // Toast.makeText(getContext(),"country[position]" , Toast.LENGTH_LONG).show();


    }

    private void updateUser() {
        HomeActivity home = (HomeActivity) getActivity();
        home.getDatabaseRef().child("users").child(signedInUser.getUserId()).setValue(signedInUser);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }
}