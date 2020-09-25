package com.mgmg.meetinground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

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

    }
}
