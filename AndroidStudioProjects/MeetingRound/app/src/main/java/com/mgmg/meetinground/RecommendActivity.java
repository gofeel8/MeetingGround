package com.mgmg.meetinground;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RecommendActivity extends AppCompatActivity {
    List<String> navList, locList, menuList, keyList, voteList;
    StringAdapter navAdapter, locAdapter;
    ListView lvNav, lvLoc, lvMenu, lvKey, lvVote;
    ListView[] listViews;
    FrameLayout flNav;
    int idx;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        final int height = lvNav.getHeight(), divider = lvNav.getDividerHeight(), width = flNav.getWidth();

        navAdapter = new StringAdapter(getApplicationContext(), navList, height, divider);
        locAdapter = new StringAdapter(getApplicationContext(), locList, height, divider);

        lvNav.setAdapter(navAdapter);
        lvLoc.setAdapter(locAdapter);



        lvNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listViews[idx].setVisibility(View.INVISIBLE);
                listViews[position].setVisibility(View.VISIBLE);
                idx = position;
                if (idx == 3) {
                    flNav.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                } else {
                    flNav.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        lvNav = findViewById(R.id.lvNav);
        listViews = new ListView[4];
        lvLoc = findViewById(R.id.lvLoc);
        listViews[0] = lvLoc;
        lvMenu = findViewById(R.id.lvMenu);
        listViews[1] = lvMenu;
        lvKey = findViewById(R.id.lvKey);
        listViews[2] = lvKey;
        lvVote = findViewById(R.id.lvVote);
        listViews[3] = lvVote;

        flNav = findViewById(R.id.flNav);

        navList = new ArrayList<>();
        navList.add("지역");
        navList.add("메뉴");
        navList.add("키워드");
        navList.add("투표함");

        locList = new ArrayList<>();
        locList.add("서울특별시");
        locList.add("부산광역시");
        locList.add("대구광역시");
        locList.add("인천광역시");
        locList.add("광주광역시");
        locList.add("대전광역시");
        locList.add("울산광역시");
        locList.add("세종특별자치시");
        locList.add("경기도");
        locList.add("강원도");
        locList.add("충청북도");
        locList.add("충청남도");
        locList.add("전라북도");
        locList.add("전라남도");
        locList.add("경상북도");
        locList.add("경상남도");
        locList.add("제주특별자치도");


    }
}