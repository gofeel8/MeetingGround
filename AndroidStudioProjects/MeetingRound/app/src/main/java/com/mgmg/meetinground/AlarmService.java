package com.mgmg.meetinground;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends Service {

    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static int UPDATE_INTERVER = 5000;
    private static int FASTEST_INTERVER = 3000;
    private static int DISTANCE = 10;

    DatabaseReference userRef;

    private Location lastKnownLocation = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.d("gps", "알람이울려야함");
//        Log.d("gps", intent.getStringExtra("uid"));
//        Log.d("gps", intent.getStringExtra("roomId"));
        String roomId = intent.getStringExtra("roomId");
        String uid = intent.getStringExtra("uid");
        userRef = FirebaseDatabase.getInstance().getReference("rooms").child(roomId).child("users").child(uid);


        //노티피케이션 클릭하면 나오는 액티비티 설정
        Intent intent1 = new Intent(this, MapActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);


        //Foreground 에서 실행되면 Notification을 보여줘야 됨
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            RemoteViews remoteVIews = new RemoteViews(getPackageName(), R.layout.notification_foreground);

            //Oreo(26)버전 이후 버전부터는 channel이 필요함
            String channelId = createNotificationChannel();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
            Notification notification = builder.setOngoing(true).setSmallIcon(R.mipmap.ic_launcher).setContent(remoteVIews)
//                    .setCategory(Notification.CATEGORY_SERVICE);
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);
        }

        //내 위치를  파이어베이스에 업데이트 해보자
        final GpsTracker tracker = new GpsTracker(this);

//        Log.d("gps", "3초대기");




        TimerTask task = new TimerTask() {
            @Override
            public void run() {
//                Log.d("gps", "lat=" + tracker.getLatitude());
                userRef.child("lat").setValue(Double.toString(tracker.getLatitude()));
//                Log.d("gps", "lon=" + tracker.getLongitude());
                userRef.child("lon").setValue(Double.toString(tracker.getLongitude()));

            }
        };

        Timer timer = new Timer();
        long delay = 3;
        long inteval = 10*1000;
        timer.scheduleAtFixedRate(task,delay,inteval);


        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d("AlarmService","Alarm종료");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId="Alarm";
        String channelName = getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(channelId,channelName, NotificationManager.IMPORTANCE_NONE);
//        channel.setDescription(channelName);
        channel.setSound(null,null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        return channelId;
    }



}

