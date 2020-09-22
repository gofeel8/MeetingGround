package com.mgmg.meetinground;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class RecommendActivity extends AppCompatActivity {
    LinearLayout llLocation;
    LinearLayout llMenu;
    LinearLayout llKeyword;
    LinearLayout now;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnLocation = findViewById(R.id.btnLocation);
        Button btnMenu = findViewById(R.id.btnMenu);
        Button btnKeyword = findViewById(R.id.btnKeyword);
        llLocation = findViewById(R.id.llLocation);
        llMenu = findViewById(R.id.llMenu);
        llKeyword = findViewById(R.id.llKeyword);
        now = llLocation;

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now.setVisibility(View.INVISIBLE);
                now = llLocation;
                llLocation.setVisibility(View.VISIBLE);
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now.setVisibility(View.INVISIBLE);
                now = llMenu;
                llMenu.setVisibility(View.VISIBLE);
            }
        });
        btnKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                now.setVisibility(View.INVISIBLE);
                now = llKeyword;
                llKeyword.setVisibility(View.VISIBLE);
            }
        });
    }
}