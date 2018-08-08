package com.example.admin.metis;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 14f;
    private final String TAG = "Metis-Application: ";

    private FirebaseAuth firebaseAuth;
    private Button logoutBtn;
    private GoogleMap map;
    private MarkerOptions userMarker;

    private LocationService locationService;
    private Intent locationServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        firebaseAuth = FirebaseAuth.getInstance();
        locationService = new LocationService(this);
        bindLocationService();
       // locationService.initServiceData(this);

        logoutBtn = findViewById(R.id.SignOutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
               firebaseAuth.signOut();//log out from firebase
                LoginManager.getInstance().logOut();//log out from facebook
                Intent intent = new Intent(MapActivity.this,MainActivity.class );
                startActivity(intent);
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        double location_latitude, location_longitude;
        if (locationService.initDeviceLocation()){

            //tel aviv lat: 32.0853, long: 34.7818
            location_latitude = locationService.getDeviceLocation().getLatitude();
            location_longitude = locationService.getDeviceLocation().getLongitude();

            LatLng currentLocation = new LatLng(location_latitude, location_longitude);
            userMarker = new MarkerOptions().position(currentLocation).title("User")
                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker));

            map.addMarker(userMarker);
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));

            Log.i(TAG,"In the end of onMapReady callback function");
        }
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//
//        Log.i(TAG,"In the end of onMapReady callback function");
    }


    /////////////////////Service functions//////////////////////////////

    private void bindLocationService(){
        locationServiceIntent = new Intent(this, LocationService.class);
        this.bindService(locationServiceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationService.TheBinder binder = (LocationService.TheBinder) service;
            locationService = binder.getService();
            locationService.setBindActivity(MapActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
