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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MenuActivity extends AppCompatActivity {

    private final String TAG = "Metis-Application: ";
    private final String BAR_NAME = "bar_name";

    static String bar_name;

    private Button menuBtn, tableBtn, chatBtn, logoutBtn;
    private String userName, providerId;
    private Uri userPhotoUrl;
    private TextView userNameTxt;
    private CircleImageView userProfilePic;
    private FirebaseAuth firebaseAuth;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bar_name = getIntent().getStringExtra(BAR_NAME);
        Log.i(TAG,"bar name is " + bar_name);


        bindUI();
        Intent intent = getIntent();
        String bar_name = intent.getStringExtra(BAR_NAME);
        firebaseAuth = FirebaseAuth.getInstance();
        getUserInfo();
        btnEvents();


        //user name & profile image from facebook account
        userNameTxt.setText(userName);
        Picasso.with(getApplicationContext()).load(userPhotoUrl).into(userProfilePic);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                firebaseAuth.signOut();//log out from firebase
                LoginManager.getInstance().logOut();//log out from facebook
                Intent intent = new Intent(MenuActivity.this,MainActivity.class );
                startActivity(intent);
            }
        });

    }

    public void getUserInfo()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                providerId = profile.getProviderId();

                // Name and profile photo Url
                userName = profile.getDisplayName();
                userPhotoUrl = profile.getPhotoUrl();
            }
            ;
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
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
                Intent intent = new Intent(MenuActivity.this, BarMActivity.class);
                startActivity(intent);
            }

        });

        tableBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, TableActivity.class);
                startActivity(intent);
            }

        });

    }




}