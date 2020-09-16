package com.mgmg.meetinground;

import androidx.annotation.Nullable;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.kakaotalk.callback.TalkResponseCallback;
import com.kakao.kakaotalk.response.KakaoTalkProfile;
import com.kakao.kakaotalk.v2.KakaoTalkService;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.LoginButton;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

public class MainActivity extends Activity {

    Uri uri;
    String uid, name, profile, roomId, roomName;

    // 세션 콜백 구현
    private ISessionCallback sessionCallback = new ISessionCallback() {
        @Override
        public void onSessionOpened() {
            Log.i("KAKAO_SESSION", "로그인 성공");

            UserManagement.getInstance().me(new MeV2ResponseCallback() {
                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                    Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                }

                @Override
                public void onFailure(ErrorResult errorResult) {
                    Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                }

                @Override
                public void onSuccess(MeV2Response result) {

                    uid = result.getId()+"";
                    if (uri != null) {
                        roomId =  uri.getQueryParameter("roomId");
                        roomName = uri.getQueryParameter("roomName");
                    }

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

                                    Intent intent = new Intent(getApplicationContext(), IndexActivity.class);
                                    intent.putExtra("uid", uid);
                                    intent.putExtra("profile", profile);
                                    intent.putExtra("name", name);
                                    intent.putExtra("roomId", roomId);
                                    intent.putExtra("roomName", roomName);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("KAKAO_SESSION", "로그인 실패", exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        uri = intent.getData();

        LoginButton button = findViewById(R.id.login_button);

        // 세션 콜백 등록
        Session.getCurrentSession().addCallback(sessionCallback);

        if (!Session.getCurrentSession().checkAndImplicitOpen()) {
            button.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}