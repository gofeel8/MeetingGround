package com.mgmg.meetinground;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecommendActivity extends AppCompatActivity {
    List<String> navList, locationList, menuList, keywordList;
    // resultList 추가해야됨

    StringAdapter navAdapter;
    ListView lvNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        lvNav = findViewById(R.id.lvNav);

        navList = new ArrayList<>();
        navList.add("지역");
        navList.add("메뉴");
        navList.add("키워드");
        navList.add("투표함");

        navAdapter = new StringAdapter(getApplicationContext(), navList);
        lvNav.setAdapter(navAdapter);
    }
}