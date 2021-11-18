package com.example.woolky;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int FINE_LOCATION_CODE = 114;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    protected LocationManager locationManager;
    private LatLng currentPosition;

    private Board board;
    private Marker userMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.drawBoardButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                board = new Board(3, 30, currentPosition);
                board.drawBoard(mMap);
            }
        });

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void checkPermission(String permission, int code) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{permission}, code);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final Context cx = this;
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                int dim = 3;
                int ladoQuadrado = 30;
                LatLng posicaoInicial = new LatLng(location.getLatitude(), location.getLongitude());
                currentPosition = posicaoInicial;
                /*board = new Board(dim, ladoQuadrado, posicaoInicial);
                board.drawBoard(mMap);*/

                /*LatLng pontoTeste = MockUsers.usersPositions.get(1);
                int [] coordenadas = board.getPositionInBoard(pontoTeste);

                board.playCircle(coordenadas, mMap);*/

                userMarker = mMap.addMarker(new MarkerOptions().position(posicaoInicial).icon(BitmapFromVector(cx, ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp))));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicaoInicial, 16));

                for (int i = 0; i < MockUsers.usersPositions.size(); i++) {
                    Drawable vectorDrawable = ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp).mutate();
                    vectorDrawable.setTint(MockUsers.usersColors.get(i));
                    mMap.addMarker(new MarkerOptions().position(MockUsers.usersPositions.get(i)).title(MockUsers.usersInformation.get(i))
                    .icon(BitmapFromVector(cx, vectorDrawable)));
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        UserInformationOnMapDialog dialog = UserInformationOnMapDialog.newInstance(marker.getTitle());
                        dialog.show(getSupportFragmentManager(), "userID");
                    }
                });

                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng latLng) {
                        board.remove();
                        board = null;
                    }
                });
            }
        });
        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 3, this);
    }



    private BitmapDescriptor BitmapFromVector(Context context, Drawable vectorDrawable) {
        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker != null) {
            userMarker.remove();
            userMarker = mMap.addMarker(new MarkerOptions().position(currentPosition).icon(BitmapFromVector(this, ContextCompat.getDrawable(this, R.drawable.ic_android_24dp))));
        }
        if (board != null) {
            board.remove();
            board.setInitialPosition(currentPosition);
            board.drawBoard(mMap);
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
