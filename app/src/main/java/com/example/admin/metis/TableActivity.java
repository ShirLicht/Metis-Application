package com.example.admin.metis;


import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.admin.metis.MenuActivity.BAR_NAME;
import static com.example.admin.metis.MenuActivity.TABLE;

public class TableActivity extends AppCompatActivity {

    private final static String TAG = "Metis-Application: ";
    private static final String FIREBASE_URL_PREFIX = "https://metis-application.firebaseio.com/";
    private static final String TABLES_URL_NODE = "/Tables/";
    private static final String FULL_TABLE_URL = "fullTableUrl";
    private static final String TABLE_NODE = "Tables";
    private static final String USERS_NODE = "Users";
    private static final String IS_TAKEN_NODE = "isTaken";
    private static final String PERSONAL_DETAILS_NODE = "Details";

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

        //Obatins the table that match barcode scanned by user
        String tableNumFullUrl = getIntent().getStringExtra(FULL_TABLE_URL);
        String tableNumUrlPrefix = FIREBASE_URL_PREFIX.concat(BAR_NAME).concat(TABLES_URL_NODE);
        TABLE = tableNumFullUrl.split(tableNumUrlPrefix)[1];

        bindUI();

        firebaseAuth = FirebaseAuth.getInstance();
        productsNames = new ArrayList<>();

        obtainUserInfo();
        signUserToTable();

        initTabsView();
    }


    public void onStart(){
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void bindUI(){
        viewPager = findViewById(R.id.TableActivity_tabPager);
        tabLayout = findViewById(R.id.TableActivity_tabs);
    }

    private void initTabsView(){
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                SectionsPagerAdapter.AdapterVersion.BAR_TABLE);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void signUserToTable(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child(TABLE);

        //Update the DB with the current user data
        HashMap<String,String> userMapData = new HashMap<>();
        userMapData.put("name",userName);
        userMapData.put("image", userPhotoUrl.toString());
        databaseReference.child(USERS_NODE).child(userId).child(PERSONAL_DETAILS_NODE).setValue(userMapData);

        //Update the DB that the Table is now taken -> maybe to add check table status(is taken or not)
        databaseReference.child(IS_TAKEN_NODE).setValue("true");
    }

    private void obtainUserInfo() {
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

    public DatabaseReference getTableDatabaseReference(){
        return firebaseDatabase.getReference().child(BAR_NAME).child(TABLE_NODE).child(TABLE);
    }

    public String getUserId(){
        return userId;
    }

    public Uri getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public ArrayList<String> getProductsNames() {
        return productsNames;
    }

    public void addNameToProductsNames(String name) {
        productsNames.add(name);
    }

    public void onBackPressed(){
        Intent intent = new Intent(TableActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

}
