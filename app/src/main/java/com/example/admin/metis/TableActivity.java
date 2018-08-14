package com.example.admin.metis;

import android.content.Intent;
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

public class TableActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_table);

        bindUI();
        firebaseAuth = FirebaseAuth.getInstance();

        //Tabs
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),
                SectionsPagerAdapter.AdapterVersion.BAR_TABLE);
        viewPager.setAdapter(sectionsPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

    }


    public void onStart(){
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void bindUI(){
        viewPager = findViewById(R.id.TableActivity_tabPager);
        tabLayout = findViewById(R.id.TableActivity_tabs);
    }

}
