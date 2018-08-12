package com.example.admin.metis;

import android.app.AppOpsManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatActivity extends AppCompatActivity {

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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatActivity.this, MenuActivity.class);
        startActivity(intent);
    }

    public void onStart(){
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    private void bindUI(){
        viewPager = findViewById(R.id.chatActivity_tabPager);
        tabLayout = findViewById(R.id.chatActivity_tabs);
    }


}
