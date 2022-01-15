package com.example.woolky.ui.games.escaperooms;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.woolky.domain.games.escaperooms.EscapeRoomGame;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.games.escaperooms.EscapeRoom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class EscapeRoomsFragment extends Fragment {

    private ArrayList<String> escapeRoomIds;
    private ArrayList<String> escapeRoomNames;
    private ArrayList<EscapeRoom> escapeRooms;
    private ArrayAdapter<String> arrayAdapter;

    public EscapeRoomsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        escapeRoomIds = new ArrayList<>();
        escapeRoomNames = new ArrayList<>();
        escapeRooms = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_escape_rooms_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        escapeRooms.clear();
        escapeRoomIds.clear();
        escapeRoomNames.clear();

        view.findViewById(R.id.escapeRoomsBackButton).setOnClickListener(v -> getActivity().onBackPressed());

        HomeActivity homeActivity = (HomeActivity) getActivity();
        DatabaseReference ref = homeActivity.getDatabaseRef();
        ref.child("escapeRooms").child(homeActivity.getSignedInUser().getUserId()).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot != null) {
                for (DataSnapshot er : dataSnapshot.getChildren()) {
                    escapeRoomIds.add(er.getKey());
                    EscapeRoom escapeRoom = er.getValue(EscapeRoom.class);
                    escapeRooms.add(escapeRoom);
                    escapeRoomNames.add(escapeRoom.getName());
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });

        ListView lv = view.findViewById(R.id.escapeRoomsList);
        arrayAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_list_item_1, escapeRoomNames);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener((parent, v, position, id) -> {
            EscapeRoom chosenEscapeRoom = escapeRooms.get(position);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment,
                    new EscapeRoomCreationFragment(chosenEscapeRoom, escapeRoomIds.get(position)))
                    .addToBackStack(null).commit();
        });

        view.findViewById(R.id.newEscapeRoomButton).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment, new EscapeRoomCreationFragment())
                    .addToBackStack(null).commit();
        });
    }
}