package com.example.woolky.ui.games.escaperooms;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.example.woolky.domain.games.escaperooms.EscapeRoomGame;
import com.example.woolky.domain.games.escaperooms.EscapeRoomGameListener;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.User;
import com.example.woolky.domain.games.escaperooms.Quiz;
import com.example.woolky.ui.games.FinishGameDialog;
import com.example.woolky.utils.LocationCalculator;
import com.example.woolky.utils.PairCustom;
import com.example.woolky.utils.Triple;
import com.example.woolky.utils.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.DatabaseReference;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayEscapeRoomFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        ShowQuizDialog.AnswerQuizListener, ImitateSequenceDialog.SequenceListener,
        InputDataDialog.OnDataSubmitted {

    private static final int FINE_LOCATION_CODE = 114;
    public static final int MINIMUM_DISTANCE_TO_WALL = 20;

    private DatabaseReference gameRef;
    private EscapeRoomGameListener escapeRoomGameListener;
    private EscapeRoomGame escapeRoomGame;
    private GoogleMap mMap;
    //private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private LatLng currentPosition;
    private Marker userMarker;
    private List<Marker> otherPlayersMarkers;
    private User signedInUser;
    private boolean mapDrawn;
    private int nextCodeDigitIndex;

    public PlayEscapeRoomFragment() {}

    public PlayEscapeRoomFragment(DatabaseReference gameRef, EscapeRoomGame escapeRoomGame) {
        this.gameRef = gameRef;
        this.escapeRoomGame = escapeRoomGame;
        this.nextCodeDigitIndex = 0;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signedInUser = ((HomeActivity) getActivity()).getSignedInUser();
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        escapeRoomGameListener = new EscapeRoomGameListener(this);
        gameRef.addChildEventListener(escapeRoomGameListener);

        otherPlayersMarkers = new ArrayList<>();
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
        ((HomeActivity) getActivity()).isPlaying = false;
        gameRef.removeEventListener(escapeRoomGameListener);
        locationManager.removeUpdates(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
        //Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        //SystemClock.sleep(2000);
//        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//
//
//                currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
//
//                Drawable myVectorDrawable = ContextCompat.getDrawable(getActivity(), R.drawable.ic_android_24dp).mutate();
//                userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).icon(Utils.BitmapFromVector(myVectorDrawable, signedInUser.getColor())));
//
//                escapeRoom.drawEscapeRoom(currentPosition, mMap);
//            }
//        });

        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);

        mMap.setOnPolylineClickListener(polyline -> {
            if (admissibleChallengeWall(polyline, currentPosition)) {
                if (polyline.getColor() == Color.BLUE) {
                    InputDataDialog inputDataDialog = new InputDataDialog("Final Code", "",
                            "", EditorInfo.TYPE_CLASS_NUMBER);
                    inputDataDialog.show(getChildFragmentManager(), "finalCode");
                } else {
                    Random random = new Random();
                    int x = random.nextInt(2);
                    if (x == 0 && !escapeRoomGame.getEscapeRoom().getQuizzes().isEmpty()) {
                        Quiz quiz = escapeRoomGame.getEscapeRoom().getQuizzes()
                                .get(random.nextInt(escapeRoomGame.getEscapeRoom().getQuizzes().size()));
                        ShowQuizDialog dialog = new ShowQuizDialog(quiz, polyline);
                        dialog.show(getChildFragmentManager(), "quiz");
                    } else {
                        ImitateSequenceDialog dialog1 = new ImitateSequenceDialog(polyline);
                        dialog1.show(getChildFragmentManager(), "seq");
                    }
                }
            }
        });
    }

    private boolean admissibleChallengeWall(Polyline polyline, LatLng currentPosition) {
        int index = escapeRoomGame.getEscapeRoom().getPolylines().indexOf(polyline);
        Triple<Integer, Integer, Integer> triple = escapeRoomGame.getEscapeRoom().getLinesCircles().get(index);

        if (polyline.getColor() != Color.RED && polyline.getColor() != Color.BLUE)
            return false;

        Circle c1 = escapeRoomGame.getEscapeRoom().getVertex().get(triple.getFirst());
        Circle c2 = escapeRoomGame.getEscapeRoom().getVertex().get(triple.getSecond());
        return PolyUtil.distanceToLine(currentPosition, c1.getCenter(), c2.getCenter()) <= MINIMUM_DISTANCE_TO_WALL;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng previousPosition = currentPosition != null ? currentPosition :
                new LatLng(location.getLatitude(), location.getLongitude());
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

        if (userMarker != null) {
            userMarker.remove();
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
            LatLng escapeRoomInitialPosition = LocationCalculator.calculatePositions(currentPosition,
                    Collections.singletonList(escapeRoomGame.getEscapeRoom().getUserStartPosition())).get(0);
            escapeRoomGame.getEscapeRoom().drawEscapeRoom(escapeRoomInitialPosition, mMap);

            //Desenha no mapa os markers dos outros players
            for (String id : escapeRoomGame.getPlayersIds()) {
                if (!id.equals(signedInUser.getUserId())) {
                    Marker m = mMap.addMarker(new MarkerOptions().position(currentPosition).
                            icon(Utils.BitmapFromVector(Utils.getUserDrawable(getActivity()), Color.BLACK)));
                    m.setTag(id);
                    otherPlayersMarkers.add(m);
                }
            }
            mapDrawn = true;
        }
        userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).
                icon(Utils.BitmapFromVector(Utils.getUserDrawable(getActivity()), signedInUser.getColor())));

        //Está a verificar se com a movimentação foram cruzadas algumas paredes e se sim movimenta a room conforme
        Point previous = new Point(previousPosition.latitude, previousPosition.longitude);
        Point current = new Point(currentPosition.latitude, currentPosition.longitude);
        keepPlayerInside(previousPosition, previous, current);

        //A calcular a posicao relativa da nossa movimentação em relacao ao ponto inicial da room
        PairCustom<Double, Double> distancesDif =
                LocationCalculator.diferenceBetweenPoints(escapeRoomGame.getEscapeRoom().getVertex().get(0).getCenter(),
                        currentPosition);

        gameRef.child(signedInUser.getUserId()).setValue(distancesDif);
    }

    private void keepPlayerInside(LatLng previousPosition, Point previous, Point current) {
        for (Triple<Integer, Integer, Integer> triple : escapeRoomGame.getEscapeRoom().getLinesCircles()) {
            Point p1 = new Point(escapeRoomGame.getEscapeRoom().getVertexPosition().get(triple.getFirst()).latitude,
                    escapeRoomGame.getEscapeRoom().getVertexPosition().get(triple.getFirst()).longitude);

            Point p2 = new Point(escapeRoomGame.getEscapeRoom().getVertexPosition().get(triple.getSecond()).latitude,
                    escapeRoomGame.getEscapeRoom().getVertexPosition().get(triple.getSecond()).longitude);

            if (LocationCalculator.doLineSegmentsIntersect(previous, current, p1, p2)) {
                if (triple.getThird() != Color.GREEN) {
                    PairCustom<Double, Double> distancesDif =
                            LocationCalculator.diferenceBetweenPoints(previousPosition, escapeRoomGame.getEscapeRoom().getVertex().get(0).getCenter());
                    List<PairCustom<Double, Double>> list = new ArrayList<>();
                    list.add(distancesDif);
                    LatLng newEscapeRoomPosition = LocationCalculator.calculatePositions(currentPosition,
                            list).get(0);
                    escapeRoomGame.getEscapeRoom().removeFromMap(mMap);
                    escapeRoomGame.getEscapeRoom().drawEscapeRoom(newEscapeRoomPosition, mMap);
                    Toast.makeText(getActivity(), "Impossible to reach", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int chosenAnswer, Quiz quiz, Polyline polyline) {
        if (chosenAnswer == quiz.getIndexOfCorrectAnswer()) {
            processCorrectAnswer(polyline);
            escapeRoomGame.getEscapeRoom().getQuizzes().remove(quiz);
        } else
            Toast.makeText(getActivity(), "Wrong Answer", Toast.LENGTH_SHORT).show();

        dialog.dismiss();
    }

    private void processCorrectAnswer(Polyline polyline) {
        polyline.setColor(Color.GREEN);
        int lineIndex = escapeRoomGame.getEscapeRoom().getPolylines().indexOf(polyline);
        Triple<Integer, Integer, Integer> triple = escapeRoomGame.getEscapeRoom().getLinesCircles().get(lineIndex);
        triple.setThird(Color.GREEN);

        Toast.makeText(getActivity(), "Correct Answer", Toast.LENGTH_SHORT).show();

        if (escapeRoomGame.isFinished() == 1) {
            escapeRoomGame.setFinito(true);
            gameRef.setValue(escapeRoomGame);
        } else {
            char c = escapeRoomGame.getFinalCode().charAt(nextCodeDigitIndex);
            CodeNumberDialog dialog = new CodeNumberDialog(c);
            dialog.show(getChildFragmentManager(), "codeNumber");
            nextCodeDigitIndex++;
        }
    }

    public void finishGame(Boolean finishedGame) {
        if (finishedGame && escapeRoomGame.isFinito()) {
            FinishGameDialog finishDialog = FinishGameDialog.newInstance("You've escaped the room :D");
            finishDialog.show(getChildFragmentManager(), "finish");
        } else {
            FinishGameDialog finishDialog = FinishGameDialog.newInstance("Someone escaped first D:");
            finishDialog.show(getChildFragmentManager(), "finish");
        }
    }

    public void updatePlayerPosition(String movedPlayerId, PairCustom<Double, Double> value) {
        if (!movedPlayerId.equals(signedInUser.getUserId()) && mapDrawn) {
            Marker m = null;
            for (Marker marker : otherPlayersMarkers) {
                if (marker.getTag().equals(movedPlayerId)) {
                    m = marker;
                    break;
                }
            }
            int index = otherPlayersMarkers.indexOf(m);
            m.setTag(null);
            m.remove();

            LatLng newPlayerPosition =
                    LocationCalculator.calculatePositions(escapeRoomGame.getEscapeRoom().getVertex().get(0).getCenter(),
                    Collections.singletonList(value)).get(0);

            m = mMap.addMarker(new MarkerOptions()
                    .position(newPlayerPosition)
                    .icon(Utils.BitmapFromVector(Utils.getUserDrawable(getActivity()), Color.BLACK)));
            m.setTag(movedPlayerId);
            otherPlayersMarkers.set(index, m);
        }

    }

    @Override
    public void rightSequenceDone(DialogFragment dialog, Polyline polyline) {
        processCorrectAnswer(polyline);
        dialog.dismiss();
    }

    @Override
    public void processData(DialogFragment dialog, String inputData) {
        dialog.dismiss();
        if (inputData.equals(escapeRoomGame.getFinalCode())) {
            Triple<Integer, Integer, Integer> triple = escapeRoomGame.getEscapeRoom().getBlueLine();
            int bluePolylineIndex = escapeRoomGame.getEscapeRoom().getLinesCircles().indexOf(triple);
            Polyline polyline = escapeRoomGame.getEscapeRoom().getPolylines().get(bluePolylineIndex);
            processCorrectAnswer(polyline);
        } else {
            Toast.makeText(getActivity(), "Wrong code", Toast.LENGTH_LONG).show();
        }
    }
}