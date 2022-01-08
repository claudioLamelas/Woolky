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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EscapeRoomsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EscapeRoomsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<String> escapeRoomIds;
    private ArrayList<EscapeRoom> escapeRooms;
    private ArrayAdapter<String> arrayAdapter;

    public EscapeRoomsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EscapeRoomsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EscapeRoomsFragment newInstance(String param1, String param2) {
        EscapeRoomsFragment fragment = new EscapeRoomsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        escapeRoomIds = new ArrayList<>();
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

        HomeActivity homeActivity = (HomeActivity) getActivity();
        DatabaseReference ref = homeActivity.getDatabaseRef();
        ref.child("escapeRooms").child(homeActivity.getSignedInUser().getUserId()).get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot != null) {
                for (DataSnapshot er : dataSnapshot.getChildren()) {
                    escapeRoomIds.add(er.getKey());
                    EscapeRoom escapeRoom = er.getValue(EscapeRoom.class);
                    escapeRooms.add(escapeRoom);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        });

        ListView lv = view.findViewById(R.id.escapeRoomsList);
        arrayAdapter = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_list_item_1, escapeRoomIds);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener((parent, v, position, id) -> {
            EscapeRoom chosenEscapeRoom = escapeRooms.get(position);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment,
                    new EscapeRoomCreationFragment(chosenEscapeRoom, (String) parent.getItemAtPosition(position)))
                    .addToBackStack(null).commit();
        });

        lv.setOnItemLongClickListener((parent, v2, position, id) -> {
            EscapeRoom chosenEscapeRoom = escapeRooms.get(position);
            getParentFragmentManager().beginTransaction().replace(R.id.fragment,
                    new PlayEscapeRoomFragment(new EscapeRoomGame(chosenEscapeRoom))).addToBackStack(null).commit();
            return false;
        });

        view.findViewById(R.id.newEscapeRoomButton).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment, new EscapeRoomCreationFragment())
                    .addToBackStack(null).commit();
        });
    }
}