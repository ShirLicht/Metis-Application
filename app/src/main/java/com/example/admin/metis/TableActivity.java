package com.example.admin.metis;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.admin.metis.MenuActivity.BAR_NAME;

public class TableActivity extends AppCompatActivity {

    private static final String TABLE_NODE = "Tables";
    private static final String USERS_NODE = "Users";
    private static final String ORDERS_NODE = "Orders";
    private static final String IS_TAKEN_NODE = "isTaken";

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private TabLayout tabLayout;
    private String userName, userId, providerId;
    private Uri userPhotoUrl;
    private ArrayList<String> productsNames;

    //ViewPager set the content of the tabs
    private ViewPager viewPager;

    //In charge of how the ui of the content of the tabs
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        bindUI();
        firebaseAuth = FirebaseAuth.getInstance();

        getUserInfo();
        signUserToBarTable();

        //Tabs
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                SectionsPagerAdapter.AdapterVersion.BAR_TABLE);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        productsNames = new ArrayList<>();

    }


    public void onStart(){
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void bindUI(){
        viewPager = findViewById(R.id.TableActivity_tabPager);
        tabLayout = findViewById(R.id.TableActivity_tabs);
    }

    private void signUserToBarTable(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child("1")
                .child(USERS_NODE).child(userId);
        HashMap<String,String> userMapData = new HashMap<>();
        userMapData.put("name",userName);
        userMapData.put("image", userPhotoUrl.toString());
        userMapData.put(ORDERS_NODE, "empty");

        databaseReference.setValue(userMapData);

        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child("1").child(IS_TAKEN_NODE);
        databaseReference.setValue("true");
    }

    private void getUserInfo() {
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

    public DatabaseReference getDatabaseReference(){
        return firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child("1");
    }

    public String getUserId(){
        return userId;
    }

    public ArrayList<String> getProductsNames() {
        return productsNames;
    }

    public void addNameToProductsNames(String name) {
        productsNames.add(name);
    }
}
