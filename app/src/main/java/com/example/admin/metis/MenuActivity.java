package com.example.admin.metis;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "Metis-Application: ";
    private static final String BAR_NAME_EXTRA = "bar_name";
    private static final String USERS_NODE = "Users";
    private static final String USER_NAME_EXTRA = "userName";
    private static final String USER_IMAGE_EXTRA = "userImage";
    private static final String TABLE_NODE = "Tables";

    //UI Variables
    private Button menuBtn, tableBtn, chatBtn, logoutBtn;
    private String userName, userId, providerId;
    private Uri userPhotoUrl,barPhotoUrl ;
    private TextView userNameTxt, barNameTxt;
    private CircleImageView userProfilePic;
    private ConstraintLayout layout;

    //Firebase Variables
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    static String BAR_NAME;
    static String TABLE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        firebaseAuth = FirebaseAuth.getInstance();

        getBarName();
        bindUI();
        getUserInfo();
        setUIParams();
        signUserToBar();
        btnEvents();
        getBarImage();
    }

    private void getBarName(){
        if(getIntent().getStringExtra(BAR_NAME_EXTRA) != null)
            BAR_NAME = getIntent().getStringExtra(BAR_NAME_EXTRA);
    }

    private void getUserInfo()
    {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userId = user.getUid();

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {

                // Id of the provider (ex: google.com)
                providerId = profile.getProviderId();

                // Name and profile photo Url
                userName = profile.getDisplayName();
                userPhotoUrl = profile.getPhotoUrl();
            }
        }
    }

    private void getBarImage(){

        switch(BAR_NAME){
            case "Shame-Bar":
                layout.setBackgroundResource(R.drawable.shame_bar);
                break;
            case "The Crazy Rabbit":
                layout.setBackgroundResource(R.drawable.crazy_rabbits_bar);
                break;
        }

    }

    private void setUIParams(){

        barNameTxt.setText(BAR_NAME);

        //user name & profile image from facebook account
        userNameTxt.setText(userName);
        Picasso.with(getApplicationContext()).load(userPhotoUrl).into(userProfilePic);
    }

    private void signUserToBar(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(USERS_NODE).child(userId);

        HashMap<String,String> userMapData = new HashMap<>();
        userMapData.put("name",userName);
        userMapData.put("image", userPhotoUrl.toString());

        databaseReference.setValue(userMapData);

    }

    private void signOutUserFromBar(){
        signOutUserFromTable();
        databaseReference.removeValue();
    }

    private void signOutUserFromTable(){
        if(TABLE == null){
            return;
        }

        DatabaseReference databaseTableReference = firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child(TABLE).child(USERS_NODE).child(userId);
        databaseTableReference.removeValue();

    }

    public void bindUI(){
        menuBtn = findViewById(R.id.MenuBtn);
        tableBtn =  findViewById(R.id.TableBtn);
        chatBtn =  findViewById(R.id.ChatBtn);
        userNameTxt = findViewById(R.id.UserNameTxtView);
        userProfilePic = findViewById(R.id.profile_image);
        logoutBtn = findViewById(R.id.SignOutBtn);
        barNameTxt = findViewById(R.id.barNameTxtView);
        layout =  findViewById(R.id.menuActivityLayout);
    }

    public void btnEvents(){
        menuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, BarMenuActivity.class);
                startActivity(intent);
            }

        });

        tableBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, BarcodeActivity.class);
                intent.putExtra(USER_NAME_EXTRA, userName);
                intent.putExtra(USER_IMAGE_EXTRA, userPhotoUrl);
                startActivity(intent);
            }

        });

        chatBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MenuActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                signOutUserFromBar();
                firebaseAuth.signOut();//log out from firebase
                LoginManager.getInstance().logOut();//log out from facebook
                Intent intent = new Intent(MenuActivity.this,MainActivity.class );
                startActivity(intent);
            }
        });
    }

    public void onBackPressed(){
        signOutUserFromBar();
        finish();
    }

    @Override
    public void onStart(){
        Log.i(TAG, "MenuActivity: onStart()");
        super.onStart();
        Intent intent = new Intent(getBaseContext(), MenuActivityTaskService.class);
       // intent.putExtra(USER_ID,userId);
        this.startService(intent);
    }

    public void onPause(){
        Log.i(TAG,"MenuActivity: onPause");
        super.onPause();
    }

    public void onStop(){
        Log.i(TAG,"MenuActivity: onStop");
        super.onStop();
    }

    public void onDestroy(){
        Log.i(TAG,"MenuActivity: onDestory");
        super.onDestroy();
        signOutUserFromBar();

        //Sign out from facebook
        firebaseAuth.signOut();//log out from firebase
        LoginManager.getInstance().logOut();//log out from facebook
    }

}