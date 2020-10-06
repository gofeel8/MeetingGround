package com.mgmg.meetinground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RoomActivity extends AppCompatActivity {

    String uid, profile, name, roomId, roomName;
    DatabaseReference database;
    UserAdapter userAdapter;
    List<UserDto> list;
    ListView lvUsers;
    TextView tvRoomName,tvRoomTime;
    Button btnSend, btnExit, btnBack,btnmap, btnRecommend;
    boolean host;

    Long meetingTime;
    Calendar calendar;

    static ValueEventListener listener1;
    static ValueEventListener listener2;
    static ValueEventListener listener3;

    @Override
    protected void onDestroy() {
//        Log.d("gps", "onDestroy: 리스너 죽임");
//        database.child("rooms").child(roomId).child("info").child("settings").child("time").removeEventListener(listener1);
//        database.child("rooms").child(roomId).child("info").child("users").removeEventListener(listener2);
//        database.child("rooms").child(roomId).child("investment").removeEventListener(listener3);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        final Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        profile = intent.getStringExtra("profile");
        name = intent.getStringExtra("name");
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");


        database = FirebaseDatabase.getInstance().getReference();

        if (intent.getBooleanExtra("isFirst", false)) {
            database.child("users").child(uid).child("rooms").child(roomId).setValue(roomName);
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("rooms/"+roomId+"/info/users/"+uid+"/name", name);
        updates.put("rooms/"+roomId+"/info/users/"+uid+"/profile", profile);
        database.updateChildren(updates);

        lvUsers = findViewById(R.id.lvUsers);
        tvRoomName = findViewById(R.id.tvRoomName);
        tvRoomTime = findViewById(R.id.tvRoomTime);

        btnSend = findViewById(R.id.btnSend);
        btnExit = findViewById(R.id.btnExit);
        btnBack = findViewById(R.id.btnBack);
//        btnmap=findViewById(R.id.btnmap);
//        btnRecommend = findViewById(R.id.btnRecommend);

        list = new ArrayList<>();
        userAdapter = new UserAdapter(getApplicationContext(), list);
        lvUsers.setAdapter(userAdapter);
        tvRoomName.setText(roomName);


         calendar = Calendar.getInstance();


        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        //방에들어오면 알람이 설정된다. 동일한 이름의 PendingIntent는 덮어쓰여지는것으로 알고있음
        if(listener1!=null)
            database.child("rooms").child(roomId).child("info").child("settings").child("time").removeEventListener(listener1);
        listener1=database.child("rooms").child(roomId).child("info").child("settings").child("time").addValueEventListener(new ValueEventListener() {
            final int _id = roomId.hashCode();

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Log.d("GPS","진입");
                meetingTime = (Long) snapshot.getValue();
                if(meetingTime !=null){

                Log.d("GPS",meetingTime.toString());
                calendar.setTimeInMillis(meetingTime);

                SimpleDateFormat format = new SimpleDateFormat("MM월dd일 HH시mm분", Locale.getDefault());
                tvRoomTime.setText(format.format(calendar.getTime()));

                long finishtime = 30; // 모임시간 finishtime분후 종료

                    Calendar EndTime = Calendar.getInstance();
                    EndTime.setTimeInMillis(meetingTime+(finishtime*60*1000));




                    if(EndTime.before(Calendar.getInstance())){
//                    Toast.makeText(RoomActivity.this, "지난 모임방에들어옴", Toast.LENGTH_SHORT).show();
//                    bottomNavigation.setVisibility(View.INVISIBLE);

                    return;
                }
                    btnSend.setVisibility(View.VISIBLE);
                    bottomNavigation.setVisibility(View.VISIBLE);

                //Receiver 설정
                Intent intent = new Intent(RoomActivity.this,AlarmReceiver.class);
                intent.putExtra("uid",uid);
                intent.putExtra("roomId",roomId);
                intent.putExtra("roomName",roomName);
                intent.putExtra("meetingTime",meetingTime);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(RoomActivity.this,_id,intent,PendingIntent.FLAG_UPDATE_CURRENT);

                //.알람 설정
                long startMinutes = 30;
                Calendar startTime = Calendar.getInstance();
                startTime.setTimeInMillis(calendar.getTimeInMillis()-(startMinutes*60*1000));


                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP,startTime.getTimeInMillis(),pendingIntent);

                //Toast보여주기(알람 시간 표시)
//                Toast.makeText(RoomActivity.this, "Alarm : "+format.format(startTime.getTime()), Toast.LENGTH_SHORT).show();


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(listener2!=null)
            database.child("rooms").child(roomId).child("info").child("users").removeEventListener(listener2);
        listener2=database.child("rooms").child(roomId).child("info").child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                boolean hasHost = false;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uId =ds.getKey();
                    String uName = (String) ds.child("name").getValue();
                    String uProfile = (String) ds.child("profile").getValue();
                    boolean isHost = false;
                    if (ds.child("host").getValue() != null)
                        isHost = true;
                    if (isHost) {
                        list.add(0, new UserDto(uName, uProfile,uId));
                        hasHost = true;
                    }
                    else {
                        list.add(new UserDto(uName, uProfile,uId));
                    }

                    if (uId.equals(uid)) {
                        host = isHost;
                    }
                }

                if (list.size() == 0 || !hasHost) {
                    database.child("rooms").child(roomId).setValue(null);
                    if (!hasHost) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            database.child("users").child(ds.getKey()).child("rooms").child(roomId).setValue(null);
                        }
//                        Toast.makeText(getApplicationContext(), "방장이 방을 나갔습니다.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if(listener3!=null)
            database.child("rooms").child(roomId).child("investment").removeEventListener(listener3);
        listener3=database.child("rooms").child(roomId).child("investment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d("gps2","id : "+ds.getKey() +", value : "+ds.getValue());
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).getuId().equals(ds.getKey())){
                            list.get(i).setInvest( (int) (long) ds.getValue());
                            userAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


//        btnRecommend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), RecommendActivity.class);
//                startActivity(intent);
//            }
//        });

//        btnmap.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                Intent intent=new Intent(RoomActivity.this,MapActivity.class);
//                intent.putExtra("name",name);
//                intent.putExtra("profile",profile);
//                intent.putExtra("roomId",roomId);
//                intent.putExtra("roomName",roomName);
//                startActivity(intent);
//            }
//        });

        btnSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 템플릿 ID
                String templateId = "36252";

                // 템플릿에 입력된 Argument에 채워질 값
                Map<String, String> templateArgs = new HashMap<>();
                templateArgs.put("name", name);
                templateArgs.put("roomId", roomId);
                templateArgs.put("roomName", roomName);

                // 사용자 정의 메시지로 카카오링크 보내기
                KakaoLinkService.getInstance()
                        .sendCustom(getApplicationContext(), templateId, templateArgs, null, new ResponseCallback<KakaoLinkResponse>() {
                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                Log.e("KAKAO_API", "카카오링크 보내기 실패: " + errorResult);
                            }

                            @Override
                            public void onSuccess(KakaoLinkResponse result) {
                                Log.i("KAKAO_API", "카카오링크 보내기 성공");

                                // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                                Log.w("KAKAO_API", "warning messages: " + result.getWarningMsg());
                                Log.w("KAKAO_API", "argument messages: " + result.getArgumentMsg());
                            }
                        });
            }
        });

        btnExit.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.child("users").child(uid).child("rooms").child(roomId).setValue(null);
                database.child("rooms").child(roomId).child("info").child("users").child(uid).setValue(null);
                finish();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
//                finish();
            }
        });


        bottomNavigation.setSelectedItemId(R.id.tab1);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab1:
                        return true;
                    case R.id.tab2:

                        Intent intent2=new Intent(RoomActivity.this,MapActivity.class);
                        intent2.putExtras(intent);
                        intent2.putExtra("name",name);
                        intent2.putExtra("profile",profile);
                        intent2.putExtra("roomId",roomId);
                        intent2.putExtra("roomName",roomName);
                        intent2.putExtra("host", host);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent2);

                        return true;
                    case R.id.tab3:
                        Intent intent3 = new Intent(RoomActivity.this, VoteActivity.class);

                        intent3.putExtras(intent);
                        intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent3);

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
        bottomNavigation.setSelectedItemId(R.id.tab1);
    }
}