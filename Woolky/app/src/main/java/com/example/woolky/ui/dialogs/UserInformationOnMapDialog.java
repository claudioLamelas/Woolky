package com.example.woolky.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.woolky.ui.map.GameModeFragment;
import com.example.woolky.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInformationOnMapDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInformationOnMapDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userID";
    private static final String ARG_PARAM2 = "userData";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String userId;
    private int userLevel;

    public UserInformationOnMapDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId The user id
     * @param tag The information associated with the user
     * @return A new instance of fragment UserInformationOnMapDialog.
     */
    public static UserInformationOnMapDialog newInstance(String userId, Object tag) {
        UserInformationOnMapDialog fragment = new UserInformationOnMapDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, userId);
        args.putInt(ARG_PARAM2, (int)tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.userId = getArguments().getString(ARG_PARAM1);
            this.userLevel = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_information_on_map_dialog, container, false);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_user_information_on_map_dialog, null);

        v.findViewById(R.id.inviteToPlayButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToGameMode(v);
            }
        });

        ((TextView) v.findViewById(R.id.userName)).setText(userId);
        ((TextView) v.findViewById(R.id.userLevel)).setText("Level: " + userLevel);
        String[] array = getResources().getStringArray(R.array.gameModes);
        ArrayAdapter<String> gameModesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.game_modes_dropdown_item, array);
        ((Spinner) v.findViewById(R.id.gameModeSpinner)).setAdapter(gameModesAdapter);
        builder.setView(v)
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                    }
                });
        return builder.create();
    }

    private void changeToGameMode(View v) {
        GameModeFragment gameModeFragment = new GameModeFragment();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, gameModeFragment).commitNow();
    }
}
