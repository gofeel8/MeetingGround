package com.example.kakaomsg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.CommerceDetailObject;
import com.kakao.message.template.CommerceTemplate;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.TemplateParams;
import com.kakao.message.template.TextTemplate;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.share_lit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnClick(view);
            }
        });
    }


    public void btnClick(View view){
        ContentObject contentObject = ContentObject.newBuilder(
                "Ivory long dress (4 Color)",
                "http://mud-kage.kakao.co.kr/dn/RY8ZN/btqgOGzITp3/uCM1x2xu7GNfr7NS9QvEs0/kakaolink40_original.png",
                LinkObject.newBuilder()
                        .setWebUrl("https://style.kakao.com/main/women/contentId=100")
                        .setMobileWebUrl("https://m.style.kakao.com/main/women/contentId=100")
                        .build())
                .build();

        CommerceDetailObject commerceDetailObject = CommerceDetailObject.newBuilder(208800)
                .setDiscountPrice(146160)
                .setDiscountRate(30)
                .build();

        ButtonObject firstButtonObject = new ButtonObject(
                "구매하기",
                LinkObject.newBuilder()
                        .setWebUrl("https://style.kakao.com/main/women/contentId=100/buy")
                        .setMobileWebUrl("https://m.style.kakao.com/main/women/contentId=100/buy")
                        .build());

        ButtonObject secondButtobObject = new ButtonObject(
                "공유하기",
                LinkObject.newBuilder()
                        .setAndroidExecutionParams("contentId=100&share=true")
                        .setIosExecutionParams("contentId=100&share=true")
                        .build());

        TemplateParams params =  CommerceTemplate.newBuilder(contentObject, commerceDetailObject)
                .addButton(firstButtonObject)
                .addButton(secondButtobObject)
                .build();

        KakaoLinkService.getInstance()
                .sendDefault(this, params, new ResponseCallback<KakaoLinkResponse>() {
                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Toast.makeText(getApplicationContext(), "카카오링크 공유 실패", Toast.LENGTH_LONG);
//                        Log.e("KAKAO_API", "카카오링크 공유 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(KakaoLinkResponse result) {
                        Toast.makeText(getApplicationContext(), "카카오링크 공유 성공", Toast.LENGTH_LONG);
//                        Log.i("KAKAO_API", "카카오링크 공유 성공");

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
//                        Log.w("KAKAO_API", "warning messages: " + result.getWarningMsg());
//                        Log.w("KAKAO_API", "argument messages: " + result.getArgumentMsg());
                    }
                });

    }
}