package com.mgmg.meetinground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class RecommendActivity extends AppCompatActivity {
    List<String> navList, locList, menuList, keyList, voteList;
//    List<String> resultList;
    StringAdapter navAdapter, locAdapter;
//    StringAdapter resultAdapter;
    ListView lvNav, lvLoc, lvMenu, lvKey, lvVote, lvResult;
    ListView[] listViews;
    FrameLayout flNav;
    HashSet<String> navSet, locSet;
    int height, divider, width, idx;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        height = lvNav.getHeight();
        divider = lvNav.getDividerHeight();
        width = 315;
        idx = -1;

        navAdapter = new StringAdapter(getApplicationContext(), navList, height, divider, navSet);
        locAdapter = new StringAdapter(getApplicationContext(), locList, height, divider, locSet);

        lvNav.setAdapter(navAdapter);
        lvLoc.setAdapter(locAdapter);




        lvNav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == idx) {
                    listViews[idx].setVisibility(View.INVISIBLE);
                    idx = -1;
                    flNav.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
                    navSet.clear();
                    navAdapter.notifyDataSetChanged();

                } else {
                    if (idx != -1) {
                        listViews[idx].setVisibility(View.INVISIBLE);
                    }
                    listViews[position].setVisibility(View.VISIBLE);
                    idx = position;

                    if (idx == 3) {
                        flNav.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    } else {
                        flNav.setLayoutParams(new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT));
                    }

                    navSet.clear();
                    navSet.add(navList.get(position));
                    navAdapter.notifyDataSetChanged();
                }
            }
        });

        lvLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = locList.get(position);
                if (locSet.contains(item)) {
                    locSet.remove(item);
                } else {
                    locSet.add(item);
                }
                locAdapter.notifyDataSetChanged();
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
        lvResult = findViewById(R.id.lvResult);

        navSet = new HashSet<>();
        locSet = new HashSet<>();

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

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.tab3);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = getIntent();
                switch (item.getItemId()){
                    case R.id.tab1:
                        intent = getIntent();
                        Intent intent2=new Intent(getApplicationContext(),RoomActivity.class);
                        intent2.putExtras(intent);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        return true;
                    case R.id.tab2:
                        intent = getIntent();
                        Intent intent3=new Intent(getApplicationContext(),MapActivity.class);
                        intent3.putExtras(intent);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        return true;
                    case R.id.tab3:
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.tab3);
    }
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}