package com.mgmg.meetinground;

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
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import java.util.HashMap;
import java.util.Map;

public class IndexActivity extends AppCompatActivity {

    String nickname, profile;
    long id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        ImageView ivProfile = findViewById(R.id.ivProfile);
        TextView tvId = findViewById(R.id.tvId);
        TextView tvNickname = findViewById(R.id.tvNickname);
        Button btnMake = findViewById(R.id.btnMake);
        Button btnSend = findViewById(R.id.btnSend);
        Button btnLogout = findViewById(R.id.btnLogout);
        Intent intent = getIntent();
        profile = intent.getStringExtra("profile");
        id = intent.getLongExtra("id", 0);
        nickname = intent.getStringExtra("name");

        Glide.with(this).load(profile).into(ivProfile); //프로필 사진 url을 사진으로 보여줌
        tvNickname.setText(nickname);
        tvId.setText(id+"");

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

        btnSend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 템플릿 ID
                String templateId = "36252";

                // 템플릿에 입력된 Argument에 채워질 값
                Map<String, String> templateArgs = new HashMap<>();
                templateArgs.put("name", nickname);

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

    }
}