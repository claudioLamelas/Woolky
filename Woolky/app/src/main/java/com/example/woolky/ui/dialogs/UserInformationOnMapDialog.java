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
import android.widget.Toast;

import com.example.woolky.HomeActivity;
import com.example.woolky.domain.GameInvite;
import com.example.woolky.domain.GameMode;
import com.example.woolky.domain.InviteState;
import com.example.woolky.domain.User;
import com.example.woolky.ui.map.GameModeFragment;
import com.example.woolky.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInformationOnMapDialog#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInformationOnMapDialog extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_PARAM = "user";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private User user;
    private User signedInUser;

    public UserInformationOnMapDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user The information associated with the user
     * @return A new instance of fragment UserInformationOnMapDialog.
     */
    public static UserInformationOnMapDialog newInstance(Object user) {
        UserInformationOnMapDialog fragment = new UserInformationOnMapDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, (User) user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.user = (User) getArguments().getSerializable(ARG_PARAM);
        }
        this.signedInUser = ((HomeActivity) getActivity()).getSignedInUser();
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
                HomeActivity activity = (HomeActivity) getActivity();
                //TODO: Alterar EU para o id/nome do signedInUser
                GameInvite gameInvite = new GameInvite(signedInUser.getUserName(), user.getUserId(), GameMode.TIC_TAC_TOE, InviteState.SENT);
                FirebaseDatabase database = FirebaseDatabase.getInstance("https://woolky-default-rtdb.europe-west1.firebasedatabase.app/");
                DatabaseReference ref = database.getReference().child("gameInvites").child(user.getUserId());
                String id = ref.push().getKey();
                ref.child(id).setValue(gameInvite);
                DatabaseReference inviteStateRef = ref.child(id).child("inviteState");

                activity.setListenerToGameInvite(id, inviteStateRef);
                //changeToGameMode(v);
            }
        });

        ((TextView) v.findViewById(R.id.userName)).setText(user.getUserName());
        ((TextView) v.findViewById(R.id.userLevel)).setText("Level: " + user.getLevel());
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
}
