package com.example.admin.metis;

import android.app.AppOpsManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "Metis-Application: ";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private TabLayout tabLayout;

    //ViewPager set the content of the tabs
    private ViewPager viewPager;

    //In charge of how the ui of the content of the tabs
    private SectionsPagerAdapter sectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bindUI();
        firebaseAuth = FirebaseAuth.getInstance();

        //Tabs
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                SectionsPagerAdapter.AdapterVersion.BAR_CHAT);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public void onStart(){
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void bindUI(){
        viewPager = findViewById(R.id.chatActivity_tabPager);
        tabLayout = findViewById(R.id.chatActivity_tabs);
    }

    public void onBackPressed(){
        finish();
    }

    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "ReviewActivity : onDestory() callback has been called");
    }

}
