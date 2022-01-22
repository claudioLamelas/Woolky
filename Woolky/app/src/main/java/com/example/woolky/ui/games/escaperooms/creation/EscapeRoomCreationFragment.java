package com.example.woolky.ui.games.escaperooms.creation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.example.woolky.R;
import com.example.woolky.domain.games.escaperooms.EscapeRoom;
import com.example.woolky.domain.user.User;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.games.escaperooms.challenges.InputDataDialog;
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
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class EscapeRoomCreationFragment extends Fragment implements OnMapReadyCallback, LocationListener, InputDataDialog.OnDataSubmitted {
    public static final int VERTEX_RADIUS = 8;

    private GoogleMap mMap;
    private LatLng initialPosition;
    private LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;

    private Circle activeCircle;
    private Circle previousActiveCircle;
    private Circle userStartPosition;
    private boolean choosingStartPosition;
    private boolean erasingWalls;
    private Triple<Integer, Integer, Integer> blueLine;

    private EscapeRoom escapeRoom = null;
    private String escapeRoomId;
    private List<PairCustom<Double, Double>> circlesRelativePositions;

    private boolean permissionsGranted;
    private boolean isMapDrawn;


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
        this.erasingWalls = false;
        this.userStartPosition = null;
        this.blueLine = null;

        HomeActivity activity = (HomeActivity) getActivity();
        permissionsGranted = activity.isPermissionsGranted();
        if (!permissionsGranted) {
            permissionsGranted = Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionsGranted)
                activity.setPermissionsGranted(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_escape_room_creation, container, false);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.escapeRoomCreationMap);
        assert mapFragment != null;
        if (!isMapDrawn)
            mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        view.findViewById(R.id.roomCreationBackButton).setOnClickListener(v -> getActivity().onBackPressed());

        view.findViewById(R.id.saveEscapeRoomButton).setOnClickListener(v -> {
            if (escapeRoom.getBlueLine() == null)
                Utils.showWarningSnackBar(getActivity(), getView(), "You need to define a finishing door (BLUE)");
            else if (escapeRoom.getUserStartPosition() == null)
                Utils.showWarningSnackBar(getActivity(), getView(), "You need to specify a starting position for the players");
            else {
                InputDataDialog dialog = new InputDataDialog("Give your escape room a name",
                        escapeRoom.getName(),"Hardest Room Ever", EditorInfo.TYPE_CLASS_TEXT);
                dialog.show(getChildFragmentManager(), "roomName");
            }
        });

        view.findViewById(R.id.addQuizButton).setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.fragment,
                    new QuizzesMenuFragment(escapeRoom)).addToBackStack("quizzes").commit();
        });

        view.findViewById(R.id.helpButton).setOnClickListener(v -> {
            CreationTutorialDialog dialog = new CreationTutorialDialog();
            dialog.show(getChildFragmentManager(), "tutorial");
        });

        Button chooseStartPositionButton = view.findViewById(R.id.chooseStartPositionButton);
        int buttonColor = choosingStartPosition ? R.color.colorPrimary : R.color.white;
        chooseStartPositionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(buttonColor, null)));
        chooseStartPositionButton.setOnClickListener(v -> {
            int color = choosingStartPosition ? R.color.white : R.color.colorPrimary;
            chooseStartPositionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color, null)));
            choosingStartPosition = !choosingStartPosition;
        });

        ImageButton eraseWallButton = view.findViewById(R.id.deleteWallButton);
        int eraseButtonColor = erasingWalls ? R.color.colorPrimary : R.color.white;
        eraseWallButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(eraseButtonColor, null)));
        eraseWallButton.setOnClickListener(v ->  {
            int color = erasingWalls ? R.color.white : R.color.colorPrimary;
            eraseWallButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color, null)));
            erasingWalls = !erasingWalls;
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;

        if (permissionsGranted) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null)
                    setupCreationMap(location);
                else
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0.1f, this);
            });
        } else
            Utils.showInfoSnackBar(getActivity(), getView(), "You need to grant location access if you want to use the maps");

        mMap.setOnMapLongClickListener(latLng -> {

            if (!choosingStartPosition) {
                Circle c = mMap.addCircle(new CircleOptions().fillColor(Color.BLACK).radius(VERTEX_RADIUS).center(latLng).clickable(true));
                Polyline p = mMap.addPolyline(new PolylineOptions()
                        .add(activeCircle.getCenter(), c.getCenter()).clickable(true));
                escapeRoom.getVertex().add(c);

                escapeRoom.getLinesCircles().add(new Triple<>(escapeRoom.getVertex().indexOf(activeCircle),
                        escapeRoom.getVertex().indexOf(c), Color.BLACK));
                escapeRoom.getPolylines().add(p);

                changeActiveCircle(c);
            } else {
                Circle c = mMap.addCircle(new CircleOptions().fillColor(Color.GREEN).radius(VERTEX_RADIUS - 3).center(latLng).clickable(false));
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
            if (!erasingWalls) {
                int index = escapeRoom.getPolylines().indexOf(polyline);
                Triple<Integer, Integer, Integer> triple = escapeRoom.getLinesCircles().get(index);

                if (triple.getThird() == Color.BLACK) {
                    polyline.setColor(Color.RED);
                    triple.setThird(Color.RED);
                } else if (triple.getThird() == Color.RED) {
                    polyline.setColor(Color.BLUE);
                    triple.setThird(Color.BLUE);
                    if (blueLine == null)
                        blueLine = triple;
                    else {
                        int index2 = escapeRoom.getLinesCircles().indexOf(blueLine);
                        escapeRoom.getPolylines().get(index2).setColor(Color.BLACK);
                        blueLine.setThird(Color.BLACK);
                        blueLine = triple;
                    }
                } else {
                    blueLine = null;
                    polyline.setColor(Color.BLACK);
                    triple.setThird(Color.BLACK);
                }
            } else {
                int index = escapeRoom.getPolylines().indexOf(polyline);
                Triple<Integer, Integer, Integer> triple = escapeRoom.getLinesCircles().remove(index);
                escapeRoom.getPolylines().remove(polyline);
                polyline.remove();
                blueLine = escapeRoom.getBlueLine();
                List<Circle> lonelyPoints = escapeRoom.getLonelyPoints(triple.getFirst(), triple.getSecond());
                for (Circle c : lonelyPoints) {
                    if (escapeRoom.getVertex().size() > 1) {
                        int i = escapeRoom.getVertex().indexOf(c);
                        escapeRoom.getVertex().remove(c);
                        escapeRoom.updateTriples(i);
                        if (activeCircle.equals(c)) {
                            changeActiveCircle(escapeRoom.getVertexDifferentFrom(c));
                            previousActiveCircle = activeCircle;
                        }
                        c.remove();
                    }
                }
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
        saveRelativePositions();

        escapeRoom.setUserStartPosition(LocationCalculator.diferenceBetweenPoints(userStartPosition.getCenter(),
                escapeRoom.getVertex().get(0).getCenter()));

        HomeActivity homeActivity = (HomeActivity) getActivity();
        User signedInUser = homeActivity.getSignedInUser();
        DatabaseReference ref = homeActivity.getDatabaseRef();
        ref = ref.child("escapeRooms").child(signedInUser.getUserId());

        String roomID = escapeRoomId.equals("") ? ref.push().getKey() : escapeRoomId;
        escapeRoomId = roomID;

        ref.child(roomID).setValue(escapeRoom)
                .addOnSuccessListener(unused -> Utils.showSuccesSnackBar(getActivity(), getView(), "Escape Room Saved"));
    }

    private void saveRelativePositions() {
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

        escapeRoom.setCirclesRelativePositions(new ArrayList<>(circlesRelativePositions));
        circlesRelativePositions.clear();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        setupCreationMap(location);
        locationManager.removeUpdates(this);
    }

    private void setupCreationMap(@NonNull Location location) {
        initialPosition = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialPosition, 16));

        if (escapeRoom == null) {
            escapeRoom = new EscapeRoom();

            Circle initialCircle = mMap.addCircle(new CircleOptions().fillColor(Color.RED)
                    .strokeColor(Color.RED).radius(VERTEX_RADIUS).center(initialPosition).clickable(true));

            escapeRoom.getVertex().add(initialCircle);
            activeCircle = initialCircle;
            previousActiveCircle = initialCircle;
        } else {
            blueLine = escapeRoom.getBlueLine();
            LatLng escapeRoomInitialPosition = LocationCalculator.calculatePositions(initialPosition,
                    Collections.singletonList(escapeRoom.getUserStartPosition())).get(0);

            escapeRoom.drawEscapeRoom(escapeRoomInitialPosition, mMap, VERTEX_RADIUS);
            activeCircle = escapeRoom.getVertex().get(escapeRoom.getVertex().size()-1);
            changeActiveCircle(activeCircle);
            userStartPosition = mMap.addCircle(new CircleOptions().center(initialPosition).radius(VERTEX_RADIUS - 3)
                    .fillColor(Color.GREEN).clickable(false));
        }
        isMapDrawn = true;
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        if (initialPosition == null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            getActivity().getSupportFragmentManager().popBackStack();
            Utils.showInfoSnackBar(getActivity(), getView(), "Turn On the GPS please");
        }
    }

    @Override
    public void processData(DialogFragment dialogFragment, String inputData) {
        if (inputData.trim().length() > 0) {
            dialogFragment.dismiss();
            escapeRoom.setName(inputData);
            saveEscapeRoom();
        } else {
            Utils.showWarningSnackBar(getActivity(), getView(), "You need to choose a name");
        }
    }
}