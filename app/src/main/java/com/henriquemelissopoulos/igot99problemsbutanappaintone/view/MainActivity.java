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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.R;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Bus;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.Config;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.network.Service;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.utils.LatLngInterpolator;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.controller.utils.Utils99;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.databinding.ActivityMainBinding;
import com.henriquemelissopoulos.igot99problemsbutanappaintone.model.Taxi;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;

    GoogleMap map;
    ArrayList<Taxi> taxis = new ArrayList<>();
    ActivityMainBinding binding;
    ClusterManager<Taxi> clusterManager;


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
                requestTaxis();
            }
        });

        Timer timer = new Timer();
        timer.schedule(new RequestTaxisTimerTask(), 10, 5000);
    }


    public void requestTaxis() {
        if (map != null) {
            LatLngBounds maplatLngBounds = map.getProjection().getVisibleRegion().latLngBounds;
            String sw = String.valueOf(maplatLngBounds.southwest.latitude) + "," + String.valueOf(maplatLngBounds.southwest.longitude);
            String ne = String.valueOf(maplatLngBounds.northeast.latitude) + "," + String.valueOf(maplatLngBounds.northeast.longitude);
            Service.getInstance().getTaxis(sw, ne);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        map.setMyLocationEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setCompassEnabled(false);

        clusterManager = new ClusterManager<>(this, map);
        map.setOnCameraChangeListener(clusterManager);

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


    public void addTaxiMarkers() {
        for (Taxi taxi : taxis) {
            if (taxi.getMarker() == null)
                taxi.setMarker(map.addMarker(new MarkerOptions()
                        .position(new LatLng(taxi.getLatitude(), taxi.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin_circle_black_24dp))
                        .title(String.valueOf(taxi.getDriverId()))));

            Utils99.animateMarker(taxi.getMarker(), new LatLng(taxi.getLatitude(), taxi.getLongitude()), new LatLngInterpolator.Spherical());
        }

        //TODO clusterManager.addItems(taxis);
    }


    public void onEventMainThread(Bus<ArrayList<Taxi>> bus) {

        if (bus.key == Config.GET_TAXI_LIST) {

            if (bus.error) {
                Toast.makeText(this, R.string.general_error_message, Toast.LENGTH_SHORT).show();
                return;
            }


            Main: for (Taxi taxi : taxis) {
                for (Taxi busTaxi : bus.data) {
                    if (taxi.getDriverId() == busTaxi.getDriverId()) { //if it's the same taxi, update values
                        taxi.setLatitude(busTaxi.getLatitude());
                        taxi.setLongitude(busTaxi.getLongitude());
                        bus.data.remove(busTaxi);
                        continue Main; //Go to next main list's taxi
                    }
                }
            }

            taxis.addAll(bus.data);

            addTaxiMarkers();
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


    class RequestTaxisTimerTask extends TimerTask {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    requestTaxis();
                }
            });
        }
    }

}
