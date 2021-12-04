package com.example.woolky.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.woolky.utils.Board;
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

public class GameModeFragment extends Fragment implements LocationListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    protected LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng currentPosition;
    private Board board;
    private Marker userMarker;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            final Context cx = getActivity();
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(cx, R.raw.style_json));
            Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, 114);
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LatLng posicaoInicial = new LatLng(location.getLatitude(), location.getLongitude());
                    currentPosition = posicaoInicial;

                    userMarker = mMap.addMarker(new MarkerOptions().position(posicaoInicial).icon(Utils.BitmapFromVector(ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp), R.color.user_default_color)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoInicial, 16));

                    board = new Board(3, 30, currentPosition);
                    board.drawBoard(mMap);
                }
            });
            Utils.checkPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION, 114);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, locationListener);
        }
    };

    public GameModeFragment() {}

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

        view.findViewById(R.id.leaveGameButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, new VicinityMapFragment()).commitNow();
            }
        });

        locationListener = this;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker != null) {
            userMarker.remove();
            userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).icon(Utils.BitmapFromVector(ContextCompat.getDrawable(getActivity(), R.drawable.ic_android_24dp), R.color.user_default_color)));
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