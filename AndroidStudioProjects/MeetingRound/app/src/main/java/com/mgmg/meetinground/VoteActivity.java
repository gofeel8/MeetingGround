package com.mgmg.meetinground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class VoteActivity extends AppCompatActivity {
    VoteAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        RecyclerView recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager( layoutManager);

        adapter = new VoteAdapter();

        adapter.addItem(new Restaurant());
        adapter.addItem(new Restaurant());
        adapter.addItem(new Restaurant());
        adapter.addItem(new Restaurant());
        adapter.addItem(new Restaurant());
        adapter.addItem(new Restaurant());
        adapter.addItem(new Restaurant());
        adapter.addItem(new Restaurant());

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnRestaurantClickListener() {
            @Override
            public void OnItemClick(VoteAdapter.ViewHolder holder, View view, int position) {
                Restaurant item = adapter.getItem(position);
                Toast.makeText(VoteActivity.this, "아이템 선택됨 : "+item.getName(), Toast.LENGTH_SHORT).show();
            }
        });


        final Intent intent = getIntent();


        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.tab3);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab1:
                        Intent intent3=new Intent(VoteActivity.this,RoomActivity.class);
                        intent3.putExtras(intent);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent3);
                        return true;
                    case R.id.tab2:

                        Intent intent2=new Intent(VoteActivity.this,MapActivity.class);
                        intent2.putExtras(intent);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent2);

                        return true;
                    case R.id.tab3:


                        return true;

                }
                return false;
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
