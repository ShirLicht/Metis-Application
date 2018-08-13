package com.example.admin.metis;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "Metis-Application: ";
    private static final String BAR_NAME_EXTRA = "bar_name";
    private static final String USERS_NODE = "Users";
    private static final String USER_ID = "userId";

    private Button menuBtn, tableBtn, chatBtn, logoutBtn;
    private String userName, userId, providerId;
    private Uri userPhotoUrl;
    private TextView userNameTxt;
    private CircleImageView userProfilePic;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    static String BAR_NAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG,"MenuActivity: onCreate() callback function");
        setContentView(R.layout.activity_menu);
        BAR_NAME = getIntent().getStringExtra(BAR_NAME_EXTRA);
        Log.i(TAG,"MenuActivity: User choose the bar with the name: " + BAR_NAME);

        bindUI();
        firebaseAuth = FirebaseAuth.getInstance();

        getUserInfo();

        signUserToBar();

        btnEvents();

        //user name & profile image from facebook account
        userNameTxt.setText(userName);
        Picasso.with(getApplicationContext()).load(userPhotoUrl).into(userProfilePic);

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


    private void signUserToBar(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(USERS_NODE).child(userId);

        HashMap<String,String> userMapData = new HashMap<>();
        userMapData.put("name",userName);
        userMapData.put("image", userPhotoUrl.toString());

        databaseReference.setValue(userMapData);
    }

    private void signOutUserFromBar(){
        databaseReference.removeValue();
    }



    public void bindUI(){
        menuBtn = findViewById(R.id.MenuBtn);
        tableBtn =  findViewById(R.id.TableBtn);
        chatBtn =  findViewById(R.id.ChatBtn);
        userNameTxt = findViewById(R.id.UserNameTxtView);
        userProfilePic = findViewById(R.id.profile_image);
        logoutBtn = findViewById(R.id.SignOutBtn);
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
                Intent intent = new Intent(MenuActivity.this, TableActivity.class);
                startActivity(intent);
            }

        });

        chatBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent intent = new Intent(MenuActivity.this, ChatActivity.class);
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
        Intent intent = new Intent(MenuActivity.this,MapActivity.class );
        startActivity(intent);
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
//        firebaseAuth.signOut();//log out from firebase
//        LoginManager.getInstance().logOut();//log out from facebook
    }

}