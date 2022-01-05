package com.example.woolky.ui.escaperooms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.woolky.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.User;
import com.example.woolky.domain.escaperooms.EscapeRoom;
import com.example.woolky.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayEscapeRoomFragment extends Fragment implements OnMapReadyCallback {

    private static final int FINE_LOCATION_CODE = 114;
    private EscapeRoom escapeRoom;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng initialPosition;
    private Marker userMarker;
    private User signedInUser;

    public PlayEscapeRoomFragment() {
        // Required empty public constructor
    }

    public PlayEscapeRoomFragment(EscapeRoom chosenEscapeRoom) {
        this.escapeRoom = chosenEscapeRoom;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signedInUser = ((HomeActivity) getActivity()).getSignedInUser();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_escape_room, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.playMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                initialPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 16));

                Drawable myVectorDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_android_24dp).mutate();
                userMarker = mMap.addMarker(new MarkerOptions().position(initialPosition).icon(Utils.BitmapFromVector(myVectorDrawable, signedInUser.getColor())));

                escapeRoom.drawEscapeRoom(initialPosition, mMap);
            }
        });

    }
}