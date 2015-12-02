package com.henriquemelissopoulos.igot99problemsbutanappaintone.view;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.R;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Bus;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Config;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.network.Service;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.databinding.ActivityMainBinding;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.model.Taxi;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;

    GoogleMap map;
    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (savedInstanceState == null) {
            mapFragment.setRetainInstance(true);
        }
        mapFragment.getMapAsync(this);

        binding.fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Service.getInstance().getTaxis("-23.612474,-46.702746", "-23.589548,-46.673392"); //TODO hardcoded
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);

        zoomUser();
    }


    public void zoomUser() {
        Location userLocation;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = locationManager.getBestProvider(new Criteria(), true);
            userLocation = locationManager.getLastKnownLocation(provider);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
            return;
        }

        if (map != null && userLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()))
                    .zoom(15)
                    .build();

            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }


    public void onEventMainThread(Bus<ArrayList<Taxi>> bus) {

        if (bus.key == Config.GET_TAXI_LIST) {

            if (bus.error) {
                Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_SHORT).show();
                return;
            }

            for (Taxi taxi : bus.data) {
                Log.d("taxibus", taxi.getDriverId() + ":= lat: " + taxi.getLatitude() + " long: " + taxi.getLongitude());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                zoomUser();
            } else {
                Toast.makeText(this, R.string.general_permission_ask, Toast.LENGTH_SHORT).show();
                zoomUser();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}