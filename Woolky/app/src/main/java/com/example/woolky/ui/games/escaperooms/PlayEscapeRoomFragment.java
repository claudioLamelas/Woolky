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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.woolky.domain.games.escaperooms.EscapeRoomGame;
import com.example.woolky.domain.games.escaperooms.EscapeRoomGameListener;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.R;
import com.example.woolky.domain.User;
import com.example.woolky.domain.games.escaperooms.Quiz;
import com.example.woolky.ui.games.tictactoe.FinishGameDialog;
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
import com.google.maps.android.geometry.Point;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayEscapeRoomFragment extends Fragment implements OnMapReadyCallback, LocationListener,
        ShowQuizDialog.AnswerQuizListener {

    private static final int FINE_LOCATION_CODE = 114;
    private DatabaseReference gameRef;
    private EscapeRoomGameListener escapeRoomGameListener;
    private EscapeRoomGame escapeRoomGame;
    private GoogleMap mMap;
    //private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private LatLng currentPosition;
    private Marker userMarker;
    private User signedInUser;

    public PlayEscapeRoomFragment() {
        // Required empty public constructor
    }

    public PlayEscapeRoomFragment(DatabaseReference gameRef, EscapeRoomGame escapeRoomGame) {
        this.gameRef = gameRef;
        this.escapeRoomGame = escapeRoomGame;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signedInUser = ((HomeActivity) getActivity()).getSignedInUser();
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        escapeRoomGameListener = new EscapeRoomGameListener(this);
        gameRef.addChildEventListener(escapeRoomGameListener);
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
                Random random = new Random();
                Quiz quiz = escapeRoomGame.getEscapeRoom().getQuizzes()
                        .get(random.nextInt(escapeRoomGame.getEscapeRoom().getQuizzes().size()));
                ShowQuizDialog dialog = new ShowQuizDialog(quiz, polyline);
                dialog.show(getChildFragmentManager(), "quiz");
            }
        });
    }

    private boolean admissibleChallengeWall(Polyline polyline, LatLng currentPosition) {
        int index = escapeRoomGame.getEscapeRoom().getPolylines().indexOf(polyline);
        Triple<Integer, Integer, Integer> triple = escapeRoomGame.getEscapeRoom().getLinesCircles().get(index);

        if (polyline.getColor() != Color.RED && polyline.getColor() != Color.BLUE)
            return false;

        //TODO: substituir pela PolyUtils.distanceToLine()
        Circle c1 = escapeRoomGame.getEscapeRoom().getVertex().get(triple.getFirst());
        Circle c2 = escapeRoomGame.getEscapeRoom().getVertex().get(triple.getSecond());
        double hypotenuseMeters = LocationCalculator.distancePointToPoint(c1.getCenter(), c2.getCenter());
        double oppositeMeters = LocationCalculator.distancePointToPoint(c2.getCenter(), currentPosition);
        double adjacentMeters = LocationCalculator.distancePointToPoint(c1.getCenter(), currentPosition);;
        double sin = oppositeMeters / hypotenuseMeters;

        double distanceFromWall = sin * adjacentMeters;


        //TODO: Fazer deste valor uma constante algures
        return distanceFromWall <= 100;
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
        }
        userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).
                icon(Utils.BitmapFromVector(ContextCompat.getDrawable(getActivity(), R.drawable.ic_android_24dp), signedInUser.getColor())));

        Point previous = new Point(previousPosition.latitude, previousPosition.longitude);
        Point current = new Point(currentPosition.latitude, currentPosition.longitude);
        for (Triple<Integer, Integer, Integer> triple : escapeRoomGame.getEscapeRoom().getLinesCircles()) {
            Point p1 = new Point(escapeRoomGame.getEscapeRoom().getVertexPosition().get(triple.getFirst()).latitude,
                    escapeRoomGame.getEscapeRoom().getVertexPosition().get(triple.getFirst()).longitude);

            Point p2 = new Point(escapeRoomGame.getEscapeRoom().getVertexPosition().get(triple.getSecond()).latitude,
                    escapeRoomGame.getEscapeRoom().getVertexPosition().get(triple.getSecond()).longitude);

            if (doLineSegmentsIntersect(previous, current, p1, p2)) {
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
                }
                break;
            }
        }

//        if (!PolyUtil.containsLocation(currentPosition, escapeRoom.getVertexPosition(), false)) {
//            PairCustom<Double, Double> coordinatesDif = LocationCalculator.diferenceBetweenPoints(previousPosition, currentPosition);
//            List<PairCustom<Double, Double>> list = new ArrayList<>();
//            list.add(coordinatesDif);
//            LatLng newEscapeRoomPosition = LocationCalculator.calculatePositions(escapeRoom.getVertex().get(0).getCenter(),
//                    list).get(0);
//            escapeRoom.removeFromMap(mMap);
//            escapeRoom.drawEscapeRoom(newEscapeRoomPosition, mMap);
//        }


    }



    boolean doLineSegmentsIntersect(Point p,Point p2,Point q,Point q2) {
        Point r = subtractPoints(p2, p);
        Point s = subtractPoints(q2, q);

        double uNumerator = crossProduct(subtractPoints(q, p), r);
        double denominator = crossProduct(r, s);

        if (denominator == 0) {
            // lines are paralell
            return false;
        }

        double u = uNumerator / denominator;
        double t = crossProduct(subtractPoints(q, p), s) / denominator;

        return (t >= 0) && (t <= 1) && (u >= 0) && (u <= 1);

    }

    /**
     * Calculate the cross product of the two points.
     *
     * @param point1 point1 point object with x and y coordinates
     * @param point2 point2 point object with x and y coordinates
     *
     * @return the cross product result as a float
     */
    double crossProduct(Point point1, Point point2) {
        return point1.x * point2.y - point1.y * point2.x;
    }

    /**
     * Subtract the second point from the first.
     *
     * @param point1 point1 point object with x and y coordinates
     * @param point2 point2 point object with x and y coordinates
     *
     * @return the subtraction result as a point object
     */
    Point subtractPoints(Point point1,Point point2) {
        return new Point(point1.x - point2.x, point1.y - point2.y);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int chosenAnswer, Quiz quiz, Polyline polyline) {
        if (chosenAnswer == quiz.getIndexOfCorrectAnswer()) {
            polyline.setColor(Color.GREEN);
            int lineIndex = escapeRoomGame.getEscapeRoom().getPolylines().indexOf(polyline);
            Triple<Integer, Integer, Integer> triple = escapeRoomGame.getEscapeRoom().getLinesCircles().get(lineIndex);
            triple.setThird(Color.GREEN);
            Toast.makeText(getActivity(), "Correct Answer", Toast.LENGTH_SHORT).show();

            if (escapeRoomGame.isFinished() == 1) {
                escapeRoomGame.setFinito(true);
                gameRef.setValue(escapeRoomGame);
            }
        } else
            Toast.makeText(getActivity(), "Wrong Answer", Toast.LENGTH_SHORT).show();


        dialog.dismiss();
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
}