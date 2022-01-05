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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private List<Circle> vertex;
    private List<Polyline> polylines;
    private Circle activeCircle;

    private EscapeRoom escapeRoom = null;
    private String escapeRoomId;
    private List<Triple<Integer, Integer, Integer>> linesCircles;
    private List<PairCustom<Double, Double>> circlesRelativePositions;
    private List<Quiz> quizzes;


    public EscapeRoomCreationFragment() {
        this.escapeRoomId = "";
        // Required empty public constructor
    }

    public EscapeRoomCreationFragment(EscapeRoom escapeRoom, String escapeRoomId) {
        this.escapeRoomId = escapeRoomId;
        this.escapeRoom = escapeRoom;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.vertex = new ArrayList<>();
        this.linesCircles = new ArrayList<>();
        this.circlesRelativePositions = new ArrayList<>();
        this.activeCircle = null;
        this.initialPosition = null;
        this.polylines = new ArrayList<>();
        this.quizzes = new ArrayList<>();
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

                if (escapeRoom == null) {
                    Circle initialCircle = mMap.addCircle(new CircleOptions().fillColor(Color.RED)
                            .strokeColor(Color.RED).radius(6).center(initialPosition).clickable(true));
                    vertex.add(initialCircle);
                    activeCircle = initialCircle;
                } else {
                    drawEscapeRoom();
                }
            }
        });

        mMap.setOnMapLongClickListener(latLng -> {
            Circle c = mMap.addCircle(new CircleOptions().fillColor(Color.BLACK).radius(6).center(latLng).clickable(true));
            Polyline p = mMap.addPolyline(new PolylineOptions()
                    .add(activeCircle.getCenter(), c.getCenter()).clickable(true));
            vertex.add(c);

            linesCircles.add(new Triple<>(vertex.indexOf(activeCircle), vertex.indexOf(c), Color.BLACK));
            polylines.add(p);

            changeActiveCircle(c);
        });

        mMap.setOnCircleClickListener(circle -> {
            if (circle.equals(activeCircle)) {
                Polyline p = mMap.addPolyline(new PolylineOptions()
                        .add(vertex.get(vertex.size()-1).getCenter(), activeCircle.getCenter())
                        .clickable(true));

                linesCircles.add(new Triple<>(vertex.size()-1, vertex.indexOf(activeCircle), Color.BLACK));
                polylines.add(p);

            } else {
                changeActiveCircle(circle);
            }
        });

        mMap.setOnPolylineClickListener(polyline -> {
            int index = polylines.indexOf(polyline);
            Triple<Integer, Integer, Integer> triple = linesCircles.get(index);

            if (triple.getThird() == Color.BLACK) {
                polyline.setColor(Color.RED);
                triple.setThird(Color.RED);
            } else if (triple.getThird() == Color.RED) {
                polyline.setColor(Color.GREEN);
                triple.setThird(Color.GREEN);
            } else {
                polyline.setColor(Color.BLACK);
                triple.setThird(Color.BLACK);
            }
        });
    }

    private void drawEscapeRoom() {
        List<LatLng> circlePositions = LocationCalculator.calculatePositions(initialPosition,
                escapeRoom.getCirclesRelativePositions());

        for (LatLng p : circlePositions) {
            Circle c = mMap.addCircle(new CircleOptions().center(p).radius(6)
                    .fillColor(Color.BLACK).clickable(true));
            vertex.add(c);
        }

        activeCircle = vertex.get(vertex.size()-1);
        changeActiveCircle(activeCircle);

        for (Triple<Integer, Integer, Integer> t : escapeRoom.getLinesCircles()) {
            Polyline p = mMap.addPolyline(new PolylineOptions().add(vertex.get(t.getFirst()).getCenter(),
                    vertex.get(t.getSecond()).getCenter()).clickable(true).color(t.getThird()));

            linesCircles.add(new Triple<>(t.getFirst(), t.getSecond(), t.getThird()));
            polylines.add(p);
        }

        this.quizzes = escapeRoom.getQuizzes();

    }

    private void changeActiveCircle(Circle circle) {
        activeCircle.setFillColor(Color.BLACK);
        activeCircle.setStrokeColor(Color.BLACK);
        activeCircle = circle;
        activeCircle.setFillColor(Color.RED);
        activeCircle.setStrokeColor(Color.RED);
    }

    private void saveEscapeRoom() {
        for (int i = 0; i < vertex.size(); i++) {
            if (i == 0) {
                circlesRelativePositions.add(new PairCustom<>(0.0, 0.0));
            } else {
                Circle previousCircle = vertex.get(i-1);
                Circle currentCircle = vertex.get(i);
                LatLng onlyLatitude = new LatLng(currentCircle.getCenter().latitude, previousCircle.getCenter().longitude);
                LatLng onlyLongitude = new LatLng(previousCircle.getCenter().latitude, currentCircle.getCenter().longitude);

                //Com isto estou a verificar em que sentido é, se para a esquerda ou direita OU se para baixo ou para cima
                //Uma latDif < 0 quer dizer que está em baixo
                //Uma lonDif < 0 quer dizer que está à esquerda
                double latDif = (currentCircle.getCenter().latitude - previousCircle.getCenter().latitude) < 0 ? 1 : -1;
                double lonDif = (currentCircle.getCenter().longitude - previousCircle.getCenter().longitude) < 0 ? -1 : 1;

                float[] resultsLat = new float[1];
                Location.distanceBetween(previousCircle.getCenter().latitude, previousCircle.getCenter().longitude,
                        onlyLatitude.latitude, onlyLatitude.longitude, resultsLat);

                float[] resultsLon = new float[1];
                Location.distanceBetween(previousCircle.getCenter().latitude, previousCircle.getCenter().longitude,
                        onlyLongitude.latitude, onlyLongitude.longitude, resultsLon);

                circlesRelativePositions.add(new PairCustom<>(resultsLat[0] * latDif, resultsLon[0] * lonDif));
            }
        }

        EscapeRoom escapeRoomToStore = new EscapeRoom(linesCircles, circlesRelativePositions, quizzes);

        HomeActivity homeActivity = (HomeActivity) getActivity();
        User signedInUser = homeActivity.getSignedInUser();
        DatabaseReference ref = homeActivity.getDatabaseRef();
        ref = ref.child("escapeRooms").child(signedInUser.getUserId());

        String roomID = escapeRoomId.equals("") ? ref.push().getKey() : escapeRoomId;
        escapeRoomId = roomID;

        ref.child(roomID).setValue(escapeRoomToStore).addOnSuccessListener(unused ->
                Toast.makeText(homeActivity, "Escape Room Saved", Toast.LENGTH_SHORT).show());

        circlesRelativePositions.clear();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, Quiz quiz) {
        this.quizzes.add(quiz);
        dialog.dismiss();
        Toast.makeText(getActivity(), "Quiz added", Toast.LENGTH_SHORT).show();
    }
}