package com.example.woolky.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.woolky.ui.HomeActivity;
import com.example.woolky.utils.LatLngCustom;
import com.example.woolky.domain.ShareLocationType;
import com.example.woolky.domain.User;
import com.example.woolky.R;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class VicinityMapFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final int FINE_LOCATION_CODE = 114;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    protected LocationManager locationManager;
    private LatLng currentPosition;
    private Marker userMarker;
    private User signedInUser;

    List<User> users;
    private boolean mayUpdate = true;

    private Handler handler;
    private boolean permissionsGranted;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        users = new ArrayList<>();
        HomeActivity activity = (HomeActivity) getActivity();
        signedInUser = activity.getSignedInUser();

        permissionsGranted = activity.isPermissionsGranted();
        if (!permissionsGranted) {
            permissionsGranted = Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
            if (permissionsGranted)
                activity.setPermissionsGranted(true);
        }

        return inflater.inflate(R.layout.fragment_vicinity_map, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        handler = new Handler();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map) ;
        OnMapReadyCallback cx = this;

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://woolky-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference usersRef = database.getReference();
        usersRef.child("users").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                users = getRecentUsers(dataSnapshot, false);
                assert mapFragment != null;
                mapFragment.getMapAsync(cx);
            }
        });

        view.findViewById(R.id.openChallengesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChallengesDialog challengesDialog = ChallengesDialog.newInstance("retirar", "retirar");
                challengesDialog.show(getChildFragmentManager(), "challenges");
            }
        });

        view.findViewById(R.id.recenterPositionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition != null)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
            }
        });

        view.findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPosition != null) {
                    usersRef.child("users").get().addOnSuccessListener(dataSnapshot -> {
                        users = getRecentUsers(dataSnapshot, true);
                        mMap.clear();
                        drawUsers(currentPosition, getContext());
                    });
                }
            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    }

    @SuppressLint({"MissingPermission", "PotentialBehaviorOverride"})
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        final Context cx = getActivity();
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(cx, R.raw.style_json));

        if (permissionsGranted) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    drawUsers(currentPosition, getActivity());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
                    updateCurrentPositionOnBD(currentPosition);
                }

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        500, 0.5f, this);
            });
        } else
            Toast.makeText(getActivity(), "You need to grant location access if you want to use the maps",
                    Toast.LENGTH_SHORT).show();

        mMap.setOnInfoWindowClickListener(marker -> {
            UserInformationOnMapDialog dialog = UserInformationOnMapDialog.newInstance(marker.getTag());
            dialog.show(getChildFragmentManager(), "userID");
        });
    }

    private void drawUsers(LatLng posicaoInicial, Context cx) {
        Drawable myVectorDrawable = ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp).mutate();
        userMarker = mMap.addMarker(new MarkerOptions().position(posicaoInicial).icon(Utils.BitmapFromVector(myVectorDrawable, signedInUser.getColor())));

        for (User u : users) {
            if (u.getVisibilityType() != ShareLocationType.NOBODY && !u.getUserId().equals(signedInUser.getUserId()) &&
                u.getCurrentPosition() != null) {
                Drawable vectorDrawable = ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp).mutate();
                Marker marker = mMap.addMarker(new MarkerOptions().position(u.getCurrentPosition().getLatLng()).title(u.getUserName())
                        .icon(Utils.BitmapFromVector(vectorDrawable, u.getColor())));

                marker.setTag(u);
            }
        }
    }

    private List<User> getRecentUsers(DataSnapshot dataSnapshot, boolean isForcedUpdate) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        List<User> recentUsers = new ArrayList<>();
        if (homeActivity.getUsers().isEmpty() || isForcedUpdate) {
            for (DataSnapshot d : dataSnapshot.getChildren()) {
                recentUsers.add(d.getValue(User.class));
            }
            homeActivity.setUsers(recentUsers);
        } else {
            recentUsers = homeActivity.getUsers();
        }
        return recentUsers;
    }

    private void updateCurrentPositionOnBD(LatLng currentPosition) {
        if (mayUpdate) {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            DatabaseReference dbRef = homeActivity.getDatabaseRef();
            User signedInUser = homeActivity.getSignedInUser();

            signedInUser.setCurrentPosition(new LatLngCustom(currentPosition.latitude, currentPosition.longitude));
            dbRef.child("users").child(signedInUser.getUserId()).setValue(signedInUser)
                    .addOnSuccessListener(unused -> {
                        this.mayUpdate = false;
                        int secondsDelayed = 2;
                        handler.postDelayed(() -> this.mayUpdate = true, secondsDelayed * 1000);
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager.removeUpdates(this);
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker != null) {
            userMarker.remove();
            userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).
                    icon(Utils.BitmapFromVector(Utils.getUserDrawable(getActivity()), signedInUser.getColor())));
        } else {
            drawUsers(currentPosition, getActivity());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 16));
        }
        updateCurrentPositionOnBD(currentPosition);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(getActivity(), "Turn On the GPS please", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {}
}
