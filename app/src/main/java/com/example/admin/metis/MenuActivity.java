package com.example.admin.metis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private final String TAG = "Metis-Application: ";
   private Button menuBtn, tableBtn, chatBtn;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        menuBtn = findViewById(R.id.MenuBtn);
        tableBtn =  findViewById(R.id.TableBtn);
        chatBtn =  findViewById(R.id.ChatBtn);

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