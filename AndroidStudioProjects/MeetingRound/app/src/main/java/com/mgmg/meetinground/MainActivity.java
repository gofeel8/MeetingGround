package com.mgmg.meetinground;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;
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

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

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


        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
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

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("MainActivity", "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_main),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i("MainActivity", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}