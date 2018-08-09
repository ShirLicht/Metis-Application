package com.example.admin.metis;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnInfoWindowClickListener {

    private static final float DEFAULT_ZOOM = 17f;
    private final String BAR_NAME = "bar_name";
    private final String[] DB_NODES = {"Location", "Latitude", "Longitude"};
    private final String TAG = "Metis-Application: ";
    private final String DB_Url = "https://metis-application.firebaseio.com";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private Button logoutBtn;
    private static  GoogleMap map;
    private MarkerOptions userMarker;
    private ArrayList<MarkerOptions> barsMarkersList;

    private LocationService locationService;
    private Intent locationServiceIntent;
    private String userName, providerId;
    private Uri userPhotoUrl;
    private TextView userNameTxt;
    private CircleImageView userProfilePic;
    private SupportMapFragment mapFragment;
    private Activity uiActivity;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        uiActivity = this;
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_Url);
        barsMarkersList = new ArrayList<>();


        locationService = new LocationService(this);
        bindLocationService();

        bindUI();
        getUserInfo();

        //user name & profile image from facebook account
        userNameTxt.setText(userName);
        Picasso.with(getApplicationContext()).load(userPhotoUrl).into(userProfilePic);
       // userProfilePic.setImageURI(userPhotoUrl);


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                firebaseAuth.signOut();//log out from firebase
                LoginManager.getInstance().logOut();//log out from facebook
                Intent intent = new Intent(MapActivity.this,MainActivity.class );
                startActivity(intent);
            }
        });
        btnEvents();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        initMap();

    }

    @Override
    public void onStart(){
        super.onStart();

    }

    public void getUserInfo()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                providerId = profile.getProviderId();

                // UID specific to the provider
                //String uid = profile.getUid();

                // Name and profile photo Url
                userName = profile.getDisplayName();
                userPhotoUrl = profile.getPhotoUrl();
            }

        }
    }

    public void bindUI(){
        userNameTxt = (TextView)findViewById(R.id.UserNameTxtView);
        userProfilePic = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);
        logoutBtn = (Button)findViewById(R.id.SignOutBtn);
    }


    public void btnEvents(){
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                firebaseAuth.signOut();//log out from firebase
                LoginManager.getInstance().logOut();//log out from facebook
                Intent intent = new Intent(MapActivity.this,MainActivity.class );
                startActivity(intent);
            }
        });
    }


    public void onResume(){
        super.onResume();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void onBackPressed(){
        firebaseAuth.signOut();//log out from firebase
        LoginManager.getInstance().logOut();//log out from facebook
        Intent intent = new Intent(MapActivity.this,MainActivity.class );
        startActivity(intent);
    }

    public void onDestroy(){
        super.onDestroy();
        firebaseAuth.signOut();//log out from firebase
        LoginManager.getInstance().logOut();//log out from facebook
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        double location_latitude, location_longitude;


        for(MarkerOptions marker : barsMarkersList){
            map.addMarker(marker);
            map.setOnInfoWindowClickListener(this);
        }

        if (locationService.initDeviceLocation() && userMarker == null){

            location_latitude = locationService.getDeviceLocation().getLatitude();
            location_longitude = locationService.getDeviceLocation().getLongitude();

            LatLng currentLocation = new LatLng(location_latitude, location_longitude);
            userMarker = new MarkerOptions().position(currentLocation).title("User")
                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker));

            Marker marker = map.addMarker(userMarker);
            marker.showInfoWindow();
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));


            Log.i(TAG,"In the end of onMapReady callback function");
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker){
        Intent intent = new Intent(MapActivity.this, MenuActivity.class );
        intent.putExtra(BAR_NAME,marker.getTitle());
        startActivity(intent);
    }

    private void initMap() {
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String name = dataSnapshot.getKey();

                double location_latitude, location_longitude;


                Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.getValue();

                for (String key : map.keySet()) {
                    if (key.equals(DB_NODES[0])) {
                        Map<String, Object> infoMap = map.get(key);
                        location_latitude = (Double) (infoMap.get(DB_NODES[1]));
                        location_longitude = (Double) (infoMap.get(DB_NODES[2]));
                        LatLng barLocation = new LatLng(location_latitude, location_longitude);
                        MarkerOptions currentMarker = new MarkerOptions().position(barLocation).title(name);


                        barsMarkersList.add(currentMarker);
                    }
                }

                Log.i(TAG, "In ObtainBarsMarkersList function");
                mapFragment.getMapAsync(MapActivity.this);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
