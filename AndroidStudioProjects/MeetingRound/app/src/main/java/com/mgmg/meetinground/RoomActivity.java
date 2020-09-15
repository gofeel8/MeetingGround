package com.mgmg.meetinground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomActivity extends AppCompatActivity {

    String uid, profile, name, roomId, roomName;
    DatabaseReference database;
    UserAdapter userAdapter;
    List<UserDto> list;
    ListView lvUsers;
    TextView tvRoomName;
    Button btnSend, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        profile = intent.getStringExtra("profile");
        name = intent.getStringExtra("name");
        roomId = intent.getStringExtra("roomId");
        roomName = intent.getStringExtra("roomName");

        database = FirebaseDatabase.getInstance().getReference();

        if (intent.getBooleanExtra("isFirst", false)) {
            database.child("users").child(uid).child("rooms").child(roomId).setValue(roomName);
        }
        database.child("rooms").child(roomId).child("users").child(uid).setValue(new UserDto(name, profile));

        lvUsers = findViewById(R.id.lvUsers);
        tvRoomName = findViewById(R.id.tvRoomName);
        btnSend = findViewById(R.id.btnSend);
        btnExit = findViewById(R.id.btnExit);

        list = new ArrayList<>();
        userAdapter = new UserAdapter(getApplicationContext(), list);
        lvUsers.setAdapter(userAdapter);
        tvRoomName.setText(roomName);

        database.child("rooms").child(roomId).child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String uName = (String) ds.child("name").getValue();
                    String uProfile = (String) ds.child("profile").getValue();
                    list.add(new UserDto(uName, uProfile));
                }

                if (list.size() == 0){
                    database.child("rooms").child(roomId).setValue(null);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                database.child("rooms").child(roomId).child("users").child(uid).setValue(null);
                finish();
            }
        });
    }
}