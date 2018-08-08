package com.example.admin.metis;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

public class MapActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button logoutBtn;
    private String userName, providerId;
    private Uri userPhotoUrl;
    private TextView userNameTxt;
    private de.hdodenhof.circleimageview.CircleImageView userProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        bindUI();
        getUserInfo();

        //user name & profile image from facebook account
        userNameTxt.setText(userName);
        userProfilePic.setImageURI(userPhotoUrl);

        mAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){
                mAuth.signOut();//log out from firebase
                LoginManager.getInstance().logOut();//log out from facebook
                Intent intent = new Intent(MapActivity.this,MainActivity.class );
                startActivity(intent);
            }
        });



    }

    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
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