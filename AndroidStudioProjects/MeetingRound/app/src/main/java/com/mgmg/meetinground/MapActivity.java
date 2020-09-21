package com.mgmg.meetinground;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapActivity  extends AppCompatActivity implements OnMapReadyCallback
{
    private GpsTracker gpsTracker;

    private EditText editText;
    private GoogleMap mMap;
    private Button button2;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private int circlesize=5000;
    final Handler handler=new Handler();
    Runnable run=null;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        editText=(EditText)findViewById(R.id.editText);
        button2=(Button)findViewById(R.id.button2);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
        final TextView textview_address = (TextView)findViewById(R.id.textView);

        button2.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(MapActivity.this);
                String pos=editText.getText().toString();
                alert.setTitle("선택지점");
                alert.setMessage(pos+"를 출발지점으로 선택하시겠습니까?");
                alert.setNegativeButton("아니오",null);

                alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String str=editText.getText().toString();
                        List<Address> addresses=null;
                        Geocoder geocoder=new Geocoder(getBaseContext());
                        try{
                            addresses=geocoder.getFromLocationName(
                                    str,
                                    10);
                            String []splitStr=addresses.get(0).toString().split(",");
                            String address=splitStr[0].substring(splitStr[0].indexOf("\"")+1,
                                    splitStr[0].length()-2); // 주소 parsing.
                            String latitude=splitStr[10].substring(splitStr[10].indexOf("=")+1);
                            String longtitude=splitStr[12].substring(splitStr[12].indexOf("=")+1);
                            final LatLng point = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longtitude));
                            magnetic_field(point);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                alert.create().show();
            }
        });


        Button ShowLocationButton = (Button) findViewById(R.id.button);
        ShowLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(MapActivity.this);
                alert.setTitle("내 위치");
                alert.setMessage("내 위치를 출발지점으로 선택하시겠습니까?");
                alert.setNegativeButton("아니오",null);

                alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gpsTracker = new GpsTracker(MapActivity.this);

                        double latitude = gpsTracker.getLatitude();
                        double longitude = gpsTracker.getLongitude();
                        String address = getCurrentAddress(latitude, longitude);
                        LatLng now=new LatLng(latitude,longitude);
                        magnetic_field(now);
                    }
                });
                alert.create().show();
            }
        });

    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if ( check_result ) {

                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                }else {

                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MapActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        gpsTracker = new GpsTracker(MapActivity.this);

        double latitude = gpsTracker.getLatitude();
        double longitude = gpsTracker.getLongitude();

        LatLng address = new LatLng(latitude, longitude);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(address);
        markerOptions.title("현위치");
        markerOptions.snippet("now");
        mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(address));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    public void magnetic_field(final LatLng position){
        // 여기에 실행 함수.
        handler.removeCallbacks(run);

        // textview_address.setText(address);

//                        Toast.makeText(MapActivity.this, "현재위치 \n위도 " + latitude + "\n경도 " + longitude, Toast.LENGTH_LONG).show();

//                        final LatLng now = new LatLng(latitude,longitude);
//                        final LatLng temp1 = new LatLng(36.335,127.34); // 임시로 찍어본 바깥영역 표시.
//                        final LatLng temp2 = new LatLng(36.33,127.3353);
        run=new Runnable() {
            @Override
            public void run() {
                mMap.clear();

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(position);
                markerOptions.title("spot");
                markerOptions.snippet("약속장소");
                mMap.addMarker(markerOptions);

                gpsTracker = new GpsTracker(MapActivity.this);

                // 현재 위치와 비교를 위한 현재 gps 찾기.
                LatLng now=new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                MarkerOptions markerOptions2=new MarkerOptions();
                markerOptions2.position(now);
                markerOptions2.title("now");
                markerOptions2.snippet("내위치");
                mMap.addMarker(markerOptions2);

                com.mgmg.meetinground.distance Distance=new com.mgmg.meetinground.distance("now",SphericalUtil.computeDistanceBetween(now,position));

//                Distance.add(new com.mgmg.meetinground.distance("temp1", SphericalUtil.computeDistanceBetween(now,temp1)));

//                for(int i=0;i<Distance.size();i++){ 거리밖에 있는것 꺼내서 확인해보기~
//                    if(Distance.get(i).getDistance()>circlesize){
//                        System.out.println(Distance.get(i).getDistance());
//                    }
//                }
                if(Distance.getDistance()>circlesize){  // 원보다 밖에 있으면,
                    NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder=null;


                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        String channelID="channel_01";
                        String channelName="MyChannel01";

                        NotificationChannel channel=new NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);

                        builder=new NotificationCompat.Builder(MapActivity.this,channelID);
                    }else{
                        builder=new NotificationCompat.Builder(MapActivity.this,null);
                    }

                    Intent push=new Intent(getApplicationContext(),MapActivity.class); // intent안에 이동할 class를 적어줌.
                    push.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent fullScreen=PendingIntent.getActivity(MapActivity.this,0,push,PendingIntent.FLAG_UPDATE_CURRENT);

                    Uri defaultSoundUri= RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION));
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),android.R.drawable.ic_dialog_info))
                            .setSmallIcon(R.mipmap.ic_logo)
                            .setContentTitle("MGMG")
                            .setContentText("벌금이 부과됩니다.")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setSound(defaultSoundUri)
                            .setContentIntent(fullScreen)
                            .setFullScreenIntent(fullScreen,true);

                    notificationManager.notify(1,builder.build());
//                        notificationManager.cancel(1);
                }

                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                mMap.addCircle(new CircleOptions()
                        .center(position)
                        .radius(circlesize)
                        .fillColor(Color.parseColor("#50F08080"))); // in meters
                circlesize-=100;
                if(circlesize>50)
                    handler.postDelayed(this,3000);
                else
                    mMap.clear();
            }
        };
        handler.post(run);
    }
}