package com.example.woolky.ui.escaperooms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.woolky.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.escaperooms.EscapeRoom;
import com.example.woolky.domain.User;
import com.example.woolky.domain.escaperooms.Quiz;
import com.example.woolky.utils.LocationCalculator;
import com.example.woolky.utils.PairCustom;
import com.example.woolky.utils.Triple;
import com.example.woolky.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;


public class EscapeRoomCreationFragment extends Fragment implements OnMapReadyCallback,
        CreateNewQuizDialog.CreateNewQuizListener {
    private static final int FINE_LOCATION_CODE = 114;

    private GoogleMap mMap;
    private LatLng initialPosition;
    private FusedLocationProviderClient fusedLocationClient;

    private Circle activeCircle;
    private Circle previousActiveCircle;
    private Circle userStartPosition;
    private boolean choosingStartPosition;

    private EscapeRoom escapeRoom = null;
    private String escapeRoomId;
    private List<PairCustom<Double, Double>> circlesRelativePositions;


    public EscapeRoomCreationFragment() {
        this.escapeRoomId = "";
    }

    public EscapeRoomCreationFragment(EscapeRoom escapeRoom, String escapeRoomId) {
        this.escapeRoomId = escapeRoomId;
        this.escapeRoom = escapeRoom;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.circlesRelativePositions = new ArrayList<>();
        this.activeCircle = null;
        this.previousActiveCircle = null;
        this.initialPosition = null;
        this.choosingStartPosition = false;
        this.userStartPosition = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_escape_room_creation, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.escapeRoomCreationMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        view.findViewById(R.id.saveEscapeRoomButton).setOnClickListener(v -> saveEscapeRoom());

        view.findViewById(R.id.addQuizButton).setOnClickListener(v -> {
            CreateNewQuizDialog dialog = CreateNewQuizDialog.newInstance("retirar", "retirar");
            dialog.show(getChildFragmentManager(), "quiz");
        });

        Button chooseStartPositionButton = view.findViewById(R.id.chooseStartPositionButton);
        chooseStartPositionButton.setOnClickListener(v -> {
            int color = choosingStartPosition ? R.color.white : R.color.colorPrimaryDark;
            chooseStartPositionButton.setBackgroundColor(getResources().getColor(color, null));
            choosingStartPosition = !choosingStartPosition;
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;

        //TODO: Mudar para o LocationManager.requestLocationUpdates()
        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                SystemClock.sleep(2000);
                initialPosition = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 16));

                if (escapeRoom == null) {
                    escapeRoom = new EscapeRoom();

                    Circle initialCircle = mMap.addCircle(new CircleOptions().fillColor(Color.RED)
                            .strokeColor(Color.RED).radius(6).center(initialPosition).clickable(true));

                    escapeRoom.getVertex().add(initialCircle);
                    activeCircle = initialCircle;
                    previousActiveCircle = initialCircle;
                } else {
                    escapeRoom.drawEscapeRoom(initialPosition, mMap);
                    activeCircle = escapeRoom.getVertex().get(escapeRoom.getVertex().size()-1);
                    changeActiveCircle(activeCircle);
                    userStartPosition = escapeRoom.getStartPositionCircle();
                }
            }
        });

        mMap.setOnMapLongClickListener(latLng -> {

            if (!choosingStartPosition) {
                Circle c = mMap.addCircle(new CircleOptions().fillColor(Color.BLACK).radius(6).center(latLng).clickable(true));
                Polyline p = mMap.addPolyline(new PolylineOptions()
                        .add(activeCircle.getCenter(), c.getCenter()).clickable(true));
                escapeRoom.getVertex().add(c);

                escapeRoom.getLinesCircles().add(new Triple<>(escapeRoom.getVertex().indexOf(activeCircle),
                        escapeRoom.getVertex().indexOf(c), Color.BLACK));
                escapeRoom.getPolylines().add(p);

                changeActiveCircle(c);
            } else {
                Circle c = mMap.addCircle(new CircleOptions().fillColor(Color.GREEN).radius(6).center(latLng).clickable(false));
                if (userStartPosition != null)
                    userStartPosition.remove();

                userStartPosition = c;
                escapeRoom.setUserStartPosition(LocationCalculator.diferenceBetweenPoints(c.getCenter(),
                        escapeRoom.getVertex().get(0).getCenter()));
            }
        });

        mMap.setOnCircleClickListener(circle -> {
            if (circle.equals(activeCircle)) {
                Polyline p = mMap.addPolyline(new PolylineOptions()
                        .add(previousActiveCircle.getCenter(), activeCircle.getCenter())
                        .clickable(true));

                escapeRoom.getLinesCircles().add(new Triple<>(escapeRoom.getVertex().indexOf(previousActiveCircle),
                        escapeRoom.getVertex().indexOf(activeCircle), Color.BLACK));
                escapeRoom.getPolylines().add(p);
            } else {
                changeActiveCircle(circle);
            }
        });

        mMap.setOnPolylineClickListener(polyline -> {
            int index = escapeRoom.getPolylines().indexOf(polyline);
            Triple<Integer, Integer, Integer> triple = escapeRoom.getLinesCircles().get(index);

            if (triple.getThird() == Color.BLACK) {
                polyline.setColor(Color.RED);
                triple.setThird(Color.RED);
            } else if (triple.getThird() == Color.RED) {
                polyline.setColor(Color.BLACK);
                triple.setThird(Color.BLACK);
            }
        });
    }

    private void changeActiveCircle(Circle circle) {
        activeCircle.setFillColor(Color.BLACK);
        activeCircle.setStrokeColor(Color.BLACK);
        previousActiveCircle = activeCircle;
        activeCircle = circle;
        activeCircle.setFillColor(Color.RED);
        activeCircle.setStrokeColor(Color.RED);
    }

    private void saveEscapeRoom() {
        for (int i = 0; i < escapeRoom.getVertex().size(); i++) {
            if (i == 0) {
                circlesRelativePositions.add(new PairCustom<>(0.0, 0.0));
            } else {
                Circle previousCircle = escapeRoom.getVertex().get(i-1);
                Circle currentCircle = escapeRoom.getVertex().get(i);

                circlesRelativePositions.add(LocationCalculator.diferenceBetweenPoints(previousCircle.getCenter(),
                        currentCircle.getCenter()));
            }
        }

        escapeRoom.setCirclesRelativePositions(circlesRelativePositions);
        //EscapeRoom escapeRoomToStore = new EscapeRoom(linesCircles, circlesRelativePositions, quizzes);

        HomeActivity homeActivity = (HomeActivity) getActivity();
        User signedInUser = homeActivity.getSignedInUser();
        DatabaseReference ref = homeActivity.getDatabaseRef();
        ref = ref.child("escapeRooms").child(signedInUser.getUserId());

        String roomID = escapeRoomId.equals("") ? ref.push().getKey() : escapeRoomId;
        escapeRoomId = roomID;

        ref.child(roomID).setValue(escapeRoom).addOnSuccessListener(unused ->
                Toast.makeText(homeActivity, "Escape Room Saved", Toast.LENGTH_SHORT).show());

        circlesRelativePositions.clear();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Quiz quiz) {
        escapeRoom.getQuizzes().add(quiz);
        dialog.dismiss();
        Toast.makeText(getActivity(), "Quiz added", Toast.LENGTH_SHORT).show();
    }
}