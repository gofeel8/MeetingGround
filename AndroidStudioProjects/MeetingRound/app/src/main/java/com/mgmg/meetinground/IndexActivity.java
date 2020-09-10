package com.mgmg.meetinground;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.Session;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.HashMap;
import java.util.Map;

public class IndexActivity extends AppCompatActivity {

    String id, name, profile, roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        roomId = intent.getStringExtra("roomId");

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        KakaoTalkService.getInstance()
                .requestProfile(new TalkResponseCallback<KakaoTalkProfile>() {
                    @Override
                    public void onNotKakaoTalkUser() {
                        Log.e("KAKAO_API", "카카오톡 사용자가 아님");
                    }

                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "카카오톡 프로필 조회 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(KakaoTalkProfile result) {
                        Log.i("KAKAO_API", "카카오톡 닉네임: " + result.getNickName());
                        Log.i("KAKAO_API", "카카오톡 프로필이미지: " + result.getProfileImageUrl());

                        name = result.getNickName();
                        profile = result.getProfileImageUrl();

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("/users/"+id+"/name", name);
                        updates.put("/users/"+id+"/profile", profile);
                        database.updateChildren(updates);
                        setView();
                    }
                });

        if (roomId!=null) {
            // roomId가 null이 아니라면 초대를 받은 사용자. 바로 방으로 이동시켜야 함.
            Toast.makeText(getApplicationContext(), roomId, Toast.LENGTH_SHORT).show();
        }

        Button btnMake = findViewById(R.id.btnMake);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnLogout = findViewById(R.id.btnLogout);

        btnMake.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Map<String, Object> updates = new HashMap<>();
//                updates.put("/users/"+id+"/rooms", null);
//                database.updateChildren(updates);
                String key = database.child("users").child(id).child("rooms").push().getKey();
                database.child("users").child("id").child("rooms").child(key).setValue(1987);
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
                templateArgs.put("id", "36252");

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

    public void setView() {
        ImageView ivProfile = findViewById(R.id.ivProfile);
        TextView tvId = findViewById(R.id.tvId);
        TextView tvNickname = findViewById(R.id.tvName);

        Glide.with(this).load(profile).into(ivProfile);
        tvId.setText(id);
        tvNickname.setText(name);
    }
}