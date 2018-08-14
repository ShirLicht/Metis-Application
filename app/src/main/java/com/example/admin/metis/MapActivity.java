package com.example.admin.metis;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.CameraUpdate;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback , GoogleMap.OnInfoWindowClickListener {

    private static final float DEFAULT_ZOOM = 17f;
    private static final String BAR_NAME_EXTRA = "bar_name";
    private static final String IS_USER_SIGNED = "isUserSigned";
    private static final String[] DB_NODES = {"Location", "Latitude", "Longitude"};
    private static final String TAG = "Metis-Application: ";
    private static final String DB_Url = "https://metis-application.firebaseio.com";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private Button logoutBtn;
    private static  GoogleMap map;
    private MarkerOptions userMarker;
    private Map<String,MarkerOptions> barsContainer;

    private LocationService locationService;
    private Intent locationServiceIntent;
    private String userName, providerId;
    private Uri userPhotoUrl;
    private TextView userNameTxt;
    private TextView barSearch;
    private CircleImageView userProfilePic;
    private SupportMapFragment mapFragment;
    private Activity uiActivity;
    private boolean changeLocationFlag = false;
    // LatLng searchedBarLocation;
    private LatLng searchedBarLocation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        uiActivity = this;
        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(DB_Url);
        barsContainer = new HashMap<>();

        locationService = new LocationService(this);
        bindLocationService();

        bindUI();
        initListeners();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        initMap();

    }

    @Override
    public void onStart(){
        super.onStart();
        getUserInfo();
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

            //user name & profile image from facebook account
            userNameTxt.setText(userName);
            Picasso.with(getApplicationContext()).load(userPhotoUrl).into(userProfilePic);
        }
        else {
            Toast.makeText(this, "No Recognization of the facebook user", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MapActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void bindUI(){
        barSearch = findViewById(R.id.barSearchField);
        userNameTxt = findViewById(R.id.UserNameTxtView);
        userProfilePic = findViewById(R.id.profile_image);
        logoutBtn = findViewById(R.id.SignOutBtn);
    }


    public void initListeners(){
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                firebaseAuth.signOut();//log out from firebase
                LoginManager.getInstance().logOut();//log out from facebook
                Intent intent = new Intent(MapActivity.this,MainActivity.class );
                startActivity(intent);
            }
        });

        barSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(barsContainer.containsKey(charSequence + "")){
                    searchedBarLocation = barsContainer.get(charSequence+ "").getPosition();
                    changeLocationFlag = true;
                    mapFragment.getMapAsync(MapActivity.this);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        double location_latitude, location_longitude;


        for(MarkerOptions marker : barsContainer.values()){
            map.addMarker(marker);
            map.setOnInfoWindowClickListener(this);
        }

        if (locationService.initDeviceLocation() && userMarker == null){

            location_latitude = locationService.getDeviceLocation().getLatitude();
            location_longitude = locationService.getDeviceLocation().getLongitude();

            LatLng currentLocation = new LatLng(location_latitude, location_longitude);
            userMarker = new MarkerOptions().position(currentLocation)
                   .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_marker));

            Marker marker = map.addMarker(userMarker);
            map.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            map.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));


            Log.i(TAG,"In the end of onMapReady callback function");
        }
        else if (locationService.initDeviceLocation() && changeLocationFlag ){
            CameraUpdate searchedBarLoc = CameraUpdateFactory.newLatLngZoom(searchedBarLocation,DEFAULT_ZOOM) ;
            map.animateCamera(searchedBarLoc);
            changeLocationFlag = false;
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker){
        Intent intent = new Intent(MapActivity.this, MenuActivity.class );
        intent.putExtra(BAR_NAME_EXTRA, marker.getTitle());
        startActivity(intent);
    }

    private void initMap() {
        databaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String barName = dataSnapshot.getKey();
                double location_latitude, location_longitude;

                try{
                    Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) dataSnapshot.getValue();

                    for (String key : map.keySet()) {
                        if (key.equals(DB_NODES[0])) {
                            Map<String, Object> infoMap = map.get(key);
                            location_latitude = (Double) (infoMap.get(DB_NODES[1]));
                            location_longitude = (Double) (infoMap.get(DB_NODES[2]));
                            LatLng barLocation = new LatLng(location_latitude, location_longitude);
                            MarkerOptions currentMarker = new MarkerOptions().position(barLocation).title(barName);

                            barsContainer.put(barName, currentMarker);
                        }
                    }

                    mapFragment.getMapAsync(MapActivity.this);
                }catch (Exception ex){
                    Log.e(TAG,"MapActivity: initMap function -> error :" + ex.getMessage());
                }


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


    public void onDestory(){
        super.onDestroy();
        Log.i(TAG,"MapActivity: onDestory callback method");
        getApplicationContext().unbindService(locationServiceConnection);

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
