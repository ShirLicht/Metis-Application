package com.example.admin.metis;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Map;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float DEFAULT_ZOOM = 14f;
    private final String[] DB_NODES = {"Name", "Location_Latitude", "Location_Longitude"};
    private final String TAG = "Metis-Application: ";
    private final String DB_Url = "https://metis-application.firebaseio.com";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private Button logoutBtn;
    private GoogleMap map;
    private MarkerOptions userMarker;
    private ArrayList<MarkerOptions> barsMarkersList;

    private LocationService locationService;
    private Intent locationServiceIntent;
    private String userName, providerId;
    private Uri userPhotoUrl;
    private TextView userNameTxt;
    private CircleImageView userProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_Url);
        barsMarkersList = new ArrayList<>();

        //When the user is log in already -> there is no a bar marker
        obtainBarsMarkersList();

        locationService = new LocationService(this);
        bindLocationService();

        logoutBtn = findViewById(R.id.SignOutBtn);
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



    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
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
            ;
        }
    }

    public void bindUI(){
        userNameTxt = (TextView)findViewById(R.id.UserNameTxtView);
        userProfilePic = (de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.profile_image);
        logoutBtn = (Button)findViewById(R.id.SignOutBtn);
    }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        double location_latitude, location_longitude;

      //Add known bars markers
        for(MarkerOptions marker : barsMarkersList)
            map.addMarker(marker);

        if (locationService.initDeviceLocation()){

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
    }


    private void obtainBarsMarkersList() {
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String name;
                double location_latitude, location_longitude;

                Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.getValue();

                for (String key : map.keySet()) {
                    if (key.equals("Information")) {
                        Map<String, Object> infoMap = map.get(key);
                        name = (String) infoMap.get(DB_NODES[0]);
                        Log.i(TAG, "name is :" + name);
                        location_latitude = (Double) (infoMap.get(DB_NODES[1]));
                        Log.i(TAG, "lat is :" + location_latitude);
                        location_longitude = (Double) (infoMap.get(DB_NODES[2]));

                        LatLng barLocation = new LatLng(location_latitude, location_longitude);
                        MarkerOptions currentMarker = new MarkerOptions().position(barLocation).title(name);
                        barsMarkersList.add(currentMarker);
                    }
                }

                Log.i(TAG, "In ObtainBarsMarkersList function");

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
