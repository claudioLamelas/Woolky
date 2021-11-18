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
import android.location.LocationManager;
import android.os.Bundle;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int FINE_LOCATION_CODE = 114;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    protected LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_CODE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void checkPermission(String permission, int code) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{permission}, code);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final Context cx = this;
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());

                //--------------- CÓDIGO PARA WOOLKY ----------------//
                int dim = 3;
                int ladoQuadrado = 30;
                LatLng posicaoInicial = new LatLng(location.getLatitude(), location.getLongitude());
                //faz com que o utilizador comece no meio do tabuleiro
                posicaoInicial = LocationCalculator.getPositionXMetersRight(posicaoInicial, 15, -3);
                posicaoInicial = LocationCalculator.getPositionXMetersBelow(posicaoInicial, 15, -3);
                Board board = new Board(dim, ladoQuadrado, posicaoInicial);
                board.drawBoard(mMap);

                LatLng pontoTeste = LocationCalculator.getPositionXMetersRight(posicaoInicial, 15, 5);
                pontoTeste = LocationCalculator.getPositionXMetersBelow(pontoTeste, 15, 5);
                int [] coordenadas = board.getPositionInBoard(pontoTeste);

                if (coordenadas[0] > -1 && coordenadas[1] > -1) {
                    LatLng centerOfCell = LocationCalculator.getPositionXMetersBelow(posicaoInicial, ladoQuadrado/2, coordenadas[0] * 2 + 1);
                    centerOfCell = LocationCalculator.getPositionXMetersRight(centerOfCell, ladoQuadrado/2, coordenadas[1] * 2 + 1);
                    mMap.addCircle(new CircleOptions().center(centerOfCell).radius(10.0).strokeColor(Color.RED));
                }
                //--------------- CÓDIGO PARA WOOLKY ----------------//

                //lastLocation = newPosition;
                mMap.addMarker(new MarkerOptions().position(newPosition).icon(BitmapFromVector(cx, ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp))).title("EU"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newPosition, 16));

                for (int i = 0; i < MockUsers.usersPositions.size(); i++) {
                    Drawable vectorDrawable = ContextCompat.getDrawable(cx, R.drawable.ic_android_24dp).mutate();
                    vectorDrawable.setTint(MockUsers.usersColors.get(i));
                    mMap.addMarker(new MarkerOptions().position(MockUsers.usersPositions.get(i)).title(MockUsers.usersInformation.get(i))
                    .icon(BitmapFromVector(cx, vectorDrawable)));
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(@NonNull Marker marker) {
                        Toast.makeText(cx, marker.getTitle(), Toast.LENGTH_LONG).show();
                    }
                });

                /*mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng latLng) {
                        mMap.clear();
                    }
                });*/
            }
        });
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
}
