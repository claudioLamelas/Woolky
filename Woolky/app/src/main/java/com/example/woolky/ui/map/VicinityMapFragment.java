package com.example.woolky.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.example.woolky.utils.Board;
import com.example.woolky.ui.dialogs.ChallengesDialog;
import com.example.woolky.utils.MockUsers;
import com.example.woolky.R;
import com.example.woolky.ui.dialogs.UserInformationOnMapDialog;
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


public class VicinityMapFragment extends Fragment implements OnMapReadyCallback, LocationListener {
    private static final int FINE_LOCATION_CODE = 114;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    protected LocationManager locationManager;
    private LatLng currentPosition;

    private Board board;
    private Marker userMarker;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vicinity_map, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map) ;
        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
            }
        });

        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        final Context cx = getActivity();
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(cx, R.raw.style_json));
        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng posicaoInicial = new LatLng(location.getLatitude(), location.getLongitude());
                currentPosition = posicaoInicial;

                userMarker = mMap.addMarker(new MarkerOptions().position(posicaoInicial).icon(Utils.BitmapFromVector(ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp))));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoInicial, 16));

                for (int i = 0; i < MockUsers.usersPositions.size(); i++) {
                    Drawable vectorDrawable = ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp).mutate();
                    vectorDrawable.setTint(MockUsers.usersColors.get(i));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(MockUsers.usersPositions.get(i)).title(MockUsers.usersInformation.get(i))
                    .icon(Utils.BitmapFromVector(vectorDrawable)));

                    //Com isto será possivel armazenar dados de cada user no marker
                    marker.setTag(MockUsers.usersLevels.get(i));
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        UserInformationOnMapDialog dialog = UserInformationOnMapDialog.newInstance(marker.getTitle(), marker.getTag());
                        dialog.show(getChildFragmentManager(), "userID");
                    }
                });
            }
        });
        Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker != null) {
            userMarker.remove();
            userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).icon(Utils.BitmapFromVector(ContextCompat.getDrawable(getActivity(), R.drawable.ic_android_24dp))));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
