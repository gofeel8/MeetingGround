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
import com.google.firebase.database.ValueEventListener;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class IndexActivity extends AppCompatActivity {

    String uid, name, profile, roomId, roomName;
    DatabaseReference database;
    List<RoomDto> list;
    RoomAdapter roomAdapter;
    ListView lvRooms;
    Button btnMake, btnLogout;
    ImageView ivProfile;
    TextView tvId ,tvNickname;
    ImageView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        profile = intent.getStringExtra("profile");
        name = intent.getStringExtra("name");
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");

        database = FirebaseDatabase.getInstance().getReference();

        if (roomId != null) {
            // roomId가 null이 아니라면 초대를 받은 사용자. 바로 방으로 이동시켜야 함.
            Intent directIntent = new Intent(getApplicationContext(), RoomActivity.class);
            directIntent.putExtra("uid", uid);
            directIntent.putExtra("profile", profile);
            directIntent.putExtra("name", name);
            directIntent.putExtra("roomId", roomId);
            directIntent.putExtra("roomName", roomName);
            directIntent.putExtra("isFirst", true);
            startActivity(directIntent);
        }

        lvRooms = findViewById(R.id.lvRooms);
        btnMake = findViewById(R.id.btnMake);
//        btnLogout = findViewById(R.id.btnLogout);
//        ivProfile = findViewById(R.id.ivProfile);
//        tvId = findViewById(R.id.tvId);
//        tvNickname = findViewById(R.id.tvName);

//        Glide.with(this).load(profile).into(ivProfile);
//        tvId.setText(uid);
//        tvNickname.setText(name);

        test = findViewById(R.id.test);

        list = new ArrayList<>();
        roomAdapter = new RoomAdapter(getApplicationContext(), list);
        lvRooms.setAdapter(roomAdapter);

        database.child("users").child(uid).child("rooms").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                final String roomId = snapshot.getKey();
                final String roomName = snapshot.getValue().toString();

                database.child("rooms").child(snapshot.getKey()).child("info").child("settings").child("time").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                snapshot.getValue();
                        Calendar calendar;
                        String time;
                        String date;
                        calendar = Calendar.getInstance();
                        calendar.setTimeInMillis((Long) snapshot.getValue());
                        list.add(new RoomDto(roomId, roomName,calendar));
//                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
//                        SimpleDateFormat timeFormat = new SimpleDateFormat(" HH:mm", Locale.getDefault());
//                        date=dateFormat.format(calendar.getTime());
//                        time=timeFormat.format(calendar.getTime());
//                        Log.d("GPS","모임날짜  : "+date);
//                        Log.d("GPS","모임시간  : "+time);
//
//                        list.add(new RoomDto(roomId, roomName,date,time));


                        Comparator<RoomDto> comp = new Comparator<RoomDto>() {
                            @Override
                            public int compare(RoomDto o1, RoomDto o2) {
                                int rt = 0;
                                if (o1.getCalendar().before(o2.getCalendar()))
                                    rt = 1 ;
                                else
                                    rt = -1 ;

                                return rt ;
                            }
                        };
                        test.setVisibility(View.INVISIBLE);

                        Collections.sort(list, comp) ;
                        roomAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });








//                list.add(new RoomDto(snapshot.getKey(), snapshot.getValue().toString()));
//                roomAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getRoomId().equals(snapshot.getKey())) {
                        list.remove(i);
                    }
                }
                Comparator<RoomDto> comp = new Comparator<RoomDto>() {
                    @Override
                    public int compare(RoomDto o1, RoomDto o2) {
                        int rt = 0;
                        if (o1.getCalendar().before(o2.getCalendar()))
                            rt = 1 ;
                        else
                            rt = -1 ;

                        return rt ;
                    }
                };

                if(list.size()==0){
                    test.setVisibility(View.VISIBLE);
                }else{
                    Collections.sort(list, comp) ;
                }
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
                Log.d("gps","리스트뷰클릭 : "+position);

                enter(list.get(position).getRoomId(), list.get(position).getRoomName());
            }
        });

        btnMake.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MakeActivity.class);
                intent.putExtra("name", name);
                startActivityForResult(intent, 1);
            }
        });

//        btnLogout.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                UserManagement.getInstance()
//                        .requestLogout(new LogoutResponseCallback() {
//                            @Override
//                            public void onCompleteLogout() {
//                                Log.i("KAKAO_API", "로그아웃 완료");
//                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//            }
//        });
    }

    public void enter(String roomId, String roomName) {
        Intent intent = new Intent(getApplicationContext(), RoomActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("profile", profile);
        intent.putExtra("name", name);
        intent.putExtra("roomId", roomId);
        intent.putExtra("roomName", roomName);

        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String roomId = database.child("users").child(uid).child("rooms").push().getKey();
            String roomName = data.getStringExtra("roomName");
            long timestamp = data.getLongExtra("timestamp", 0);

            database.child("users").child(uid).child("rooms").child(roomId).setValue(roomName);
            database.child("rooms").child(roomId).child("info").child("users").child(uid).setValue(new UserDto(name, profile,uid));
            database.child("rooms").child(roomId).child("info").child("users").child(uid).child("host").setValue(true);
            database.child("rooms").child(roomId).child("info").child("settings").child("title").setValue(roomName);
            database.child("rooms").child(roomId).child("info").child("settings").child("time").setValue(timestamp);
            enter(roomId, roomName);
        }
    }
}