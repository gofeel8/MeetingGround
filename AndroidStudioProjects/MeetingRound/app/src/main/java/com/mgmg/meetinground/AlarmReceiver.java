package com.mgmg.meetinground;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent sIntent = new Intent(context, AlarmService.class);
        sIntent.putExtras(intent);
        //Oreo(26) 버전 이후부터는 Background 에서 실행을 금지하기 때문에 Foreground에서 실행해야함
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            context.startForegroundService(sIntent);
        }else{
            context.startService(sIntent);
        }
    }
}
