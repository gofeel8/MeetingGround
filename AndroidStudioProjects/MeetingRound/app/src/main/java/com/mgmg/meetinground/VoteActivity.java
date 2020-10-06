package com.mgmg.meetinground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class VoteActivity extends AppCompatActivity {
    VoteAdapter adapter;
    private DatabaseReference database;
    String roomId;

    static ValueEventListener listener1;

    @Override
    protected void onDestroy() {
//        Log.d("gps", "onDestroy: 리스너 죽임");
//        database.child("rooms").child(roomId).child("vote").removeEventListener(listener1);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote);

        database = FirebaseDatabase.getInstance().getReference();
        RecyclerView recyclerView = findViewById(R.id.recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager( layoutManager);

        adapter = new VoteAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnRestaurantClickListener() {
            @Override
            public void OnItemClick(VoteAdapter.ViewHolder holder, View view, int position) {
                Restaurant item = adapter.getItem(position);
//                Toast.makeText(VoteActivity.this, "아이템 선택됨 : "+item.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), RestaurantDetail.class);
                intent.putExtra("obj",item);
                intent.putExtras(getIntent());
                startActivity(intent);
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

        roomId = intent.getStringExtra("roomId");
        if(listener1!=null)
                    database.child("rooms").child(roomId).child("vote").removeEventListener(listener1);
            listener1= database.child("rooms").child(roomId).child("vote").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.items.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Restaurant r = new Restaurant();
                    r.setId(ds.getKey());
                    r.setName((String) ds.child("info").child("name").getValue());
                    r.setAddress((String) ds.child("info").child("address").getValue());
                    r.setArea((String) ds.child("info").child("area").getValue());
                    r.setTel((String) ds.child("info").child("tel").getValue());
                    r.setLat((String) ds.child("info").child("lat").getValue());
                    r.setLon((String) ds.child("info").child("lon").getValue());
                    r.setTags((List<String>) ds.child("info").child("tags").getValue());
                    r.setImages((List<String>) ds.child("info").child("images").getValue());
                    int agree=0, disagree=0;
                    for (DataSnapshot d : ds.child("vote").getChildren()) {
                        if ((boolean) d.getValue())
                            agree++;
                        else
                            disagree++;
                    }
                    r.setAgree(agree);
                    r.setDisagree(disagree);
                    adapter.addItem(r);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
