package com.example.admin.metis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private final String TAG = "Metis-Application: ";
    private final String BAR_NAME = "bar_name";
    private Button menuBtn, tableBtn, chatBtn;
   // public static  String bar_name;
    static String bar_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bar_name = getIntent().getStringExtra(BAR_NAME);
        Log.i(TAG,"bar name is " + bar_name);


        bindUI();
        Intent intent = getIntent();
        String bar_name = intent.getStringExtra(BAR_NAME);

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

    private void bindUI(){
        menuBtn = findViewById(R.id.MenuBtn);
        tableBtn =  findViewById(R.id.TableBtn);
        chatBtn =  findViewById(R.id.ChatBtn);
    }

}