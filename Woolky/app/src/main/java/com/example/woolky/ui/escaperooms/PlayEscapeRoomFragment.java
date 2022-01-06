package com.example.woolky.ui.escaperooms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.example.woolky.domain.escaperooms.Quiz;
import com.example.woolky.utils.LocationCalculator;
import com.example.woolky.utils.Triple;
import com.example.woolky.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayEscapeRoomFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final int FINE_LOCATION_CODE = 114;
    private EscapeRoom escapeRoom;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private LatLng currentPosition;
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
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager.removeUpdates(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));

                Drawable myVectorDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_android_24dp).mutate();
                userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).icon(Utils.BitmapFromVector(myVectorDrawable, signedInUser.getColor())));

                escapeRoom.drawEscapeRoom(currentPosition, mMap);
            }
        });

        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);

        mMap.setOnPolylineClickListener(polyline -> {
            if (admissibleChallengeWall(polyline, currentPosition)) {
                Random random = new Random();
                Quiz quiz = escapeRoom.getQuizzes().get(random.nextInt(escapeRoom.getQuizzes().size()));
                ShowQuizDialog dialog = new ShowQuizDialog(quiz, polyline);
                dialog.show(getChildFragmentManager(), "quiz");
            }
        });
    }

    private boolean admissibleChallengeWall(Polyline polyline, LatLng currentPosition) {
        int index = escapeRoom.getPolylines().indexOf(polyline);
        Triple<Integer, Integer, Integer> triple = escapeRoom.getLinesCircles().get(index);

        //Talvez terei de usar o triple.getThird() inves da polyline. Para isso é usar a interface do dialog
        if (polyline.getColor() != Color.RED)
            return false;

        Circle c1 = escapeRoom.getVertex().get(triple.getFirst());
        Circle c2 = escapeRoom.getVertex().get(triple.getSecond());
        double hypotenuseMeters = LocationCalculator.distancePointToPoint(c1.getCenter(), c2.getCenter());
        double oppositeMeters = LocationCalculator.distancePointToPoint(c2.getCenter(), currentPosition);
        double adjacentMeters = LocationCalculator.distancePointToPoint(c1.getCenter(), currentPosition);;
        double sin = oppositeMeters / hypotenuseMeters;

        double distanceFromWall = sin * adjacentMeters;

        return distanceFromWall <= 100;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker != null) {
            userMarker.remove();
            userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).
                    icon(Utils.BitmapFromVector(ContextCompat.getDrawable(getActivity(), R.drawable.ic_android_24dp), signedInUser.getColor())));
        }
    }
}