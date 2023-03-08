package com.example.colectivos_interurbanos_aguilar.activities.client;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.colectivos_interurbanos_aguilar.R;
import com.example.colectivos_interurbanos_aguilar.activities.MainActivity;
import com.example.colectivos_interurbanos_aguilar.activities.driver.MapDriverActivity;
import com.example.colectivos_interurbanos_aguilar.includes.MyToolbar;
import com.example.colectivos_interurbanos_aguilar.providers.AuthProvider;
import com.example.colectivos_interurbanos_aguilar.providers.GeofireProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

public class MapClientActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;

    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTINGS_REQUEST_CODE = 2;

    private Marker mMarker;
    private GeofireProvider mGeofireProvider;
    private LatLng mCurrentLatLng;
    private List<Marker> mDriversMarkers = new ArrayList<>();
    private boolean mIsFirstTime = true ;

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    if (mMarker != null){
                        mMarker.remove();
                    }
                    mCurrentLatLng = new LatLng(location.getLatitude(), location.getLongitude());


                    mMarker = mMap.addMarker(new MarkerOptions().position(
                                            new LatLng(location.getLatitude(), location.getLongitude())
                                    )
                                    .title("Posicion")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.position_icon))
                    );

                    //obtener localizacion en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15f)
                                    .build()
                    ));

                    if (mIsFirstTime){
                        mIsFirstTime = false;
                        getActiveDrivers();
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client);

        MyToolbar.show(this, "Pasajero", false);

        mAuthProvider = new AuthProvider();
        mGeofireProvider = new GeofireProvider();
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);


    }


    private void getActiveDrivers(){
        mGeofireProvider.getActiveDrivers(mCurrentLatLng).addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //marcadores de vehiculos que se conectan
                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            return;
                        }
                    }
                }
                LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Vehiculo en Circulacion").icon(BitmapDescriptorFactory.fromResource(R.drawable.autobus_icon)));
                marker.setTag(key);
                mDriversMarkers.add(marker);
            }

            @Override
            public void onKeyExited(String key) {
                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                            marker.remove();
                            mDriversMarkers.remove(marker);
                            return;
                        }
                    }
                }
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

                //actualizar la posicion de cada chofer

                for (Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(key)){
                           marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .build();

        startLocation();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()) {
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }
                    else {
                        showAlertDialogNOGPS();
                    }
                } else {
                    checkLocationPermissions();
                }
            } else {
                checkLocationPermissions();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
        else {
            showAlertDialogNOGPS();
        }

    }

    private void showAlertDialogNOGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Active la Ubicacion para Continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTINGS_REQUEST_CODE);
                    }
                }).create().show();
    }


    private boolean gpsActived(){
        boolean isActive = false;
        LocationManager locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive=true;
        }
        return isActive;
    }


    private void startLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()) {

                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }
                else {
                    showAlertDialogNOGPS();
                }
            }
            else {
                checkLocationPermissions();
            }
        }
        else {
            if (gpsActived()) {

                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }
            else {
                showAlertDialogNOGPS();
            }
        }
    }


    private void checkLocationPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporcione Los Permisos Para Continuar")
                        .setMessage("Esta Aplicacion Requiere de los Permisos de Ubicacion para Utilizarse")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapClientActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(MapClientActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.driver_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    void logout(){
        mAuthProvider.logout();
        Intent intent = new Intent(MapClientActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}