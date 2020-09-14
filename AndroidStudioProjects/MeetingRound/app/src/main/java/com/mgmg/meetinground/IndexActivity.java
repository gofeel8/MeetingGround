package com.mgmg.meetinground;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.ArrayList;
import java.util.List;

public class IndexActivity extends AppCompatActivity {

    String uid, name, profile, roomId;
    DatabaseReference database;
    List<String> list;
    RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Intent intent = getIntent();
        uid = intent.getStringExtra("id");
        profile = intent.getStringExtra("profile");
        name = intent.getStringExtra("name");
        roomId = intent.getStringExtra("roomId");

        database = FirebaseDatabase.getInstance().getReference();

        if (roomId!=null) {
            // roomId가 null이 아니라면 초대를 받은 사용자. 바로 방으로 이동시켜야 함.
            Intent directIntent = new Intent(getApplicationContext(), RoomActivity.class);
            intent.putExtra("id", uid);
            intent.putExtra("profile", profile);
            intent.putExtra("name", name);
            intent.putExtra("roomId", roomId);
            intent.putExtra("isFirst", true);
            startActivity(directIntent);
        }

        ListView lvRooms = findViewById(R.id.lvRooms);
        Button btnMake = findViewById(R.id.btnMake);
        Button btnLogout = findViewById(R.id.btnLogout);
        ImageView ivProfile = findViewById(R.id.ivProfile);
        TextView tvId = findViewById(R.id.tvId);
        TextView tvNickname = findViewById(R.id.tvName);

        Glide.with(this).load(profile).into(ivProfile);
        tvId.setText(uid);
        tvNickname.setText(name);

        list = new ArrayList<>();
        roomAdapter = new RoomAdapter(getApplicationContext(), list);
        lvRooms.setAdapter(roomAdapter);

        database.child("users").child(uid).child("rooms").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                list.add(snapshot.getKey());
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                list.remove(snapshot.getKey());
                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                error.toException().printStackTrace();
            }
        });

        lvRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
                intent.putExtra("id", uid);
                intent.putExtra("profile", profile);
                intent.putExtra("name", name);
                intent.putExtra("roomId", list.get(position));

                startActivity(intent);
            }
        });

        btnMake.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                String roomId = database.child("users").child(uid).child("rooms").push().getKey();
                database.child("users").child(uid).child("rooms").child(roomId).setValue(true);
                database.child("rooms").child(roomId).child("users").child(uid).setValue(new MyUser(name, profile));
            }
        });

        btnLogout.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance()
                        .requestLogout(new LogoutResponseCallback() {
                            @Override
                            public void onCompleteLogout() {
                                Log.i("KAKAO_API", "로그아웃 완료");
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
            }
        });
    }
}