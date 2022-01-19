package com.example.woolky.ui.games.tictactoe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.woolky.R;
import com.example.woolky.domain.games.tictactoe.TicTacToeGame;
import com.example.woolky.domain.games.tictactoe.TicTacToeGameListener;
import com.example.woolky.domain.user.User;
import com.example.woolky.ui.HomeActivity;
import com.example.woolky.ui.games.FinishGameDialog;
import com.example.woolky.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class PlayTicTacToeFragment extends Fragment implements LocationListener {

    private TicTacToeGame ticTacToeGame;
    private DatabaseReference gameRef;
    private TicTacToeGameListener ticTacToeGameListener;
    private boolean isReceiver;
    private Button confirmPlayButton;

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    protected LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng currentPosition;
    private Marker userMarker;

    private boolean permissionsGranted;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @SuppressLint("MissingPermission")
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            final Context cx = getActivity();
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(cx, R.raw.style_json));

            if (permissionsGranted) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                   if (location != null) {
                       currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
                       ticTacToeGame.getBoard().setInitialPosition(currentPosition);
                       ticTacToeGame.getBoard().drawBoard(mMap);
                       userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                               .icon(Utils.BitmapFromVector(Utils.getUserDrawable(getActivity()), R.color.user_default_color)));
                   }

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0.5f, locationListener);
                });
            } else
                Utils.showInfoSnackBar(getActivity(), getView(), "You need to grant location access if you want to use the maps");
        }
    };

    public PlayTicTacToeFragment(DatabaseReference gameRef, TicTacToeGame ticTacToeGame) {
        this.gameRef = gameRef;
        this.ticTacToeGame = ticTacToeGame;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isReceiver = getArguments().getBoolean("isReceiver");
        }
        ticTacToeGameListener = new TicTacToeGameListener(this);
        gameRef.addChildEventListener(ticTacToeGameListener);

        HomeActivity activity = (HomeActivity) getActivity();
        permissionsGranted = activity.isPermissionsGranted();
        if (!permissionsGranted) {
            permissionsGranted = Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (permissionsGranted)
                activity.setPermissionsGranted(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_mode, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        view.findViewById(R.id.leaveGameButton).setOnClickListener(v -> {
            ticTacToeGame.setWinner(ticTacToeGame.opponentPiece());
            gameRef.setValue(ticTacToeGame);
            ((HomeActivity) getActivity()).changeToMap();
        });

        view.findViewById(R.id.confirmPlayButton).setOnClickListener(v -> {
            if (currentPosition != null) {
                List<Integer> playedPosition = ticTacToeGame.getBoard().getPositionInBoard(currentPosition);
                if (ticTacToeGame.isPlayValid(playedPosition)) {
                    ticTacToeGame.makePlay(playedPosition, mMap);
                    int finishState = ticTacToeGame.isFinished();
                    if (finishState != -1) {
                        ticTacToeGame.finishGame(finishState);
                    }
                    gameRef.setValue(ticTacToeGame);
                } else {
                    Utils.showWarningSnackBar(getActivity(), getView(), "You can't make this move");
                }
            }
        });

        confirmPlayButton = view.findViewById(R.id.confirmPlayButton);

        locationListener = this;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (isReceiver) {
            gameRef.setValue(ticTacToeGame);
        }

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((HomeActivity) getActivity()).isPlaying = false;
        gameRef.removeEventListener(ticTacToeGameListener);
        locationManager.removeUpdates(locationListener);
    }

    public void finishGame(TicTacToeGame.Piece value) {
        if (value == TicTacToeGame.Piece.Blank) {
            FinishGameDialog dialog = FinishGameDialog.newInstance("It's a TIE");
            dialog.show(getChildFragmentManager(), "dialog");
        } else if (value == this.ticTacToeGame.getMyPiece()) {
            HomeActivity activity = (HomeActivity) getActivity();
            User signedInUser = activity.getSignedInUser();
            signedInUser.getStats().addOneWin();
            activity.getDatabaseRef().child("users").child(signedInUser.getUserId()).setValue(signedInUser);
            FinishGameDialog dialog = FinishGameDialog.newInstance("You've Won :D");
            dialog.show(getChildFragmentManager(), "dialog");
        } else {
            FinishGameDialog dialog = FinishGameDialog.newInstance("You've Lost :(");
            dialog.show(getChildFragmentManager(), "dialog");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker != null) {
            userMarker.remove();
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
            ticTacToeGame.getBoard().setInitialPosition(currentPosition);
            ticTacToeGame.getBoard().drawBoard(mMap);
        }
        userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition)
                .icon(Utils.BitmapFromVector(Utils.getUserDrawable(getActivity()), R.color.user_default_color)));
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Utils.showInfoSnackBar(getActivity(), getView(), "Turn On the GPS please");
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    public TicTacToeGame getTicTacToe() {
        return ticTacToeGame;
    }

    public DatabaseReference getGameRef() {
        return gameRef;
    }

    public Button getConfirmPlayButton() {
        return confirmPlayButton;
    }

    public GoogleMap getMap() {
        return mMap;
    }
}