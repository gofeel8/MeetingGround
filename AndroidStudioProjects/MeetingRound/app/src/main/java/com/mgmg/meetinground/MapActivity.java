package com.mgmg.meetinground;
import android.Manifest;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

public class MapActivity  extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener
{
    private static int Investment=0;
    private GpsTracker gpsTracker;
    private boolean GameStart=false;
    private String temploc,roomId,uid;
    private EditText editText;
    private GoogleMap mMap;
    private Button button2;
    private LatLng position;
    private DatabaseReference database;
    private Long meetingTime;
    private String address;
    private String api_key="AIzaSyAMy-qeSOF-mrR6_aLzMDGc9YgsW70UCfQ";
    static boolean first=false;
    private LinearLayout container;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private int circlesize=5000;
    final Handler handler=new Handler();
    Runnable run=null;


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent=getIntent();
        roomId=intent.getStringExtra("roomId");
        uid=intent.getStringExtra("uid");

        container=findViewById(R.id.selection);

        database = FirebaseDatabase.getInstance().getReference();

        if(database.child("rooms").child(roomId).child("info").child("users").child(uid).child("host").getKey()==null){
            container.removeAllViews();
        }

        editText=(EditText)findViewById(R.id.editText);

        Places.initialize(getApplicationContext(),api_key);
        magnetic_field(null,null,null,false);
        editText.setFocusable(false);
        editText.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                List<Place.Field> fieldList=Arrays.asList(Place.Field.ADDRESS
                        ,Place.Field.LAT_LNG,Place.Field.NAME);

                Intent intent=new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY
                        ,fieldList).build(MapActivity.this);
                startActivityForResult(intent,100);
            }
        });

        database.child("rooms").child(roomId).child("info").child("users").child(uid).child("lat").addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LatLng now=new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                if(position==null)
                    return;
                com.mgmg.meetinground.distance Distance=new com.mgmg.meetinground.distance("now",SphericalUtil.computeDistanceBetween(now,position));

                if(Distance.getDistance()>circlesize){  // 원보다 밖에 있으면,
                    NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder builder=null;

                    Investment+=100;
                    database.child("rooms").child(roomId).child("investment").child(uid).setValue(Investment);


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
                            .setSmallIcon(R.mipmap.ic_launcher)
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        database.child("rooms").child(roomId).child("info").addValueEventListener(new ValueEventListener() {
            final int _id = roomId.hashCode();


            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               meetingTime=Long.parseLong(snapshot.child("settings").child("time").getValue().toString());
                //                // 1. 모임장소를 띄운다. + 자기장
                if(snapshot.child("settings").child("address").getValue()==null) { // 모임장소가 정해지지 않았으면 내 위치만 띄워줌. -> 내 위치는 mapload때 띄워져 있음.
                    return;
                }
                String str=snapshot.child("settings").child("address").getValue().toString();
                Geocoder geocoder=new Geocoder(getBaseContext());
                try{
                    List<Address> addresses=geocoder.getFromLocationName(
                            str,
                            10);
                    String []splitStr=addresses.get(0).toString().split(",");
                    String address=splitStr[0].substring(splitStr[0].indexOf("\"")+1,
                            splitStr[0].length()-2); // 주소 parsing
                    String latitude=splitStr[10].substring(splitStr[10].indexOf("=")+1);
                    String longtitude=splitStr[12].substring(splitStr[12].indexOf("=")+1);
                    position = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longtitude));
                    if(snapshot.child("users").child(uid).child("lat").getValue()==null||snapshot.child("users").child(uid).child("lon").getValue()==null){ // 데이터베이스에 위치정보가 갱신 x -> 게임시작 x 목적지와 내위치만 표시해줌.
                        magnetic_field(null,null,null,false);
                        return;
                    }
                    GameStart=true;
                    container.removeAllViews();
                    // 2. 각자의 위치를 띄운다.
//                    List<LatLng> position=new LinkedList<>();
                    List<LatLng> user_pos=new LinkedList<>();
                    List<String> profile=new LinkedList<>();
                    List<String> name=new LinkedList<>();
                    Iterator<DataSnapshot> users=snapshot.child("users").getChildren().iterator();
                    while(users.hasNext()) {
                        DataSnapshot snap = users.next();
                        if (snap.child("lat").getValue() == null || snap.child("lon").getValue() == null)
                            continue;
                        String Lat = snap.child("lat").getValue().toString();
                        String Lng = snap.child("lon").getValue().toString();
                        profile.add(snap.child("profile").getValue().toString());
                        name.add(snap.child("name").getValue().toString());
//                        position.add(new LatLng(Double.parseDouble(Lat),Double.parseDouble(Lng)));
                        user_pos.add(new LatLng(Double.parseDouble(Lat),Double.parseDouble(Lng)));
                    }
                    magnetic_field(user_pos,profile,name,true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });



        button2=(Button)findViewById(R.id.button2);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        }else {
            checkRunTimePermission();
        }
//        final TextView textview_address = (TextView)findViewById(R.id.textView);


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
                        Intent intent=getIntent();
                        String roomId=intent.getStringExtra("roomId");

                        database.child("rooms").child(roomId).child("info").child("settings").child("address").setValue(str);
                    }
                });
                alert.create().show();
            }
        });
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.tab2);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.tab1:
                        Intent intent = getIntent();
                        Intent intent2=new Intent(MapActivity.this,RoomActivity.class);
                        intent2.putExtras(intent);
                        intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);
                        return true;
                    case R.id.tab2:
                        return true;
                    case R.id.tab3:
                        intent = getIntent();
                        Intent intent3 = new Intent(getApplicationContext(), RecommendActivity.class);
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
        bottomNavigation.setSelectedItemId(R.id.tab2);
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
            case 100:
                if(resultCode==RESULT_OK){
                    Place place= Autocomplete.getPlaceFromIntent(data);

                    editText.setText(place.getAddress());
                }else if(resultCode== AutocompleteActivity.RESULT_ERROR){
                    Status status=Autocomplete.getStatusFromIntent(data);
                    Toast.makeText(getApplicationContext(),status.getStatusMessage()
                            ,Toast.LENGTH_SHORT).show();
                }
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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                {
                    if(GameStart)
                        return;
                    mMap.clear();

                    gpsTracker = new GpsTracker(MapActivity.this);
                    String myurl=getIntent().getStringExtra("profile");

                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng mine = (new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
                    markerOptions.position(mine);
                    markerOptions.title("mine");
                    if(!myurl.equals("")) {
                        if (android.os.Build.VERSION.SDK_INT > 9) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                        }
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromLink(myurl)));
                    }else{
                        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.logo);
                        Bitmap b=bitmapDrawable.getBitmap();
                        Bitmap smallMarker=Bitmap.createScaledBitmap(b,200,200,false);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getCircularBitmap(smallMarker)));
                    }
                    mMap.addMarker(markerOptions);
                    // marker title
                    MarkerOptions markerOptions1=new MarkerOptions();
                    markerOptions1.title("click");
                    markerOptions1.position(latLng);
                    mMap.addMarker(markerOptions1);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

                }
            }
        });
        mMap.setOnMarkerClickListener(this);
//
//        gpsTracker = new GpsTracker(MapActivity.this);
//
//        double latitude = gpsTracker.getLatitude();
//        double longitude = gpsTracker.getLongitude();
//
//        LatLng address = new LatLng(latitude, longitude);
//
//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(address);
//        markerOptions.title("현위치");
//        markerOptions.snippet("now");
//        String url=getIntent().getStringExtra("profile");
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromLink(url)));
//
//        mMap.addMarker(markerOptions);

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(address));
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    public Bitmap getBitmapFromLink(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.connect();
            } catch (Exception e) {
                Log.v("asfwqeds", e.getMessage());
            }
            InputStream input = connection.getInputStream();
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inSampleSize=4;
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            myBitmap=getCircularBitmap(myBitmap);
            myBitmap=resizeBitmapImage(myBitmap,100);
            return myBitmap;
        } catch (IOException e) {
            Log.v("asfwqeds", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    protected Bitmap getCircularBitmap(Bitmap srcBitmap) {
        // Calculate the circular bitmap width with border
        int squareBitmapWidth = Math.min(srcBitmap.getWidth(), srcBitmap.getHeight());
        // Initialize a new instance of Bitmap
        Bitmap dstBitmap = Bitmap.createBitmap (
                squareBitmapWidth, // Width
                squareBitmapWidth, // Height
                Bitmap.Config.ARGB_8888 // Config
        );
        Canvas canvas = new Canvas(dstBitmap);
        // Initialize a new Paint instance
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, squareBitmapWidth, squareBitmapWidth);
        RectF rectF = new RectF(rect);
        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // Calculate the left and top of copied bitmap
        float left = (squareBitmapWidth-srcBitmap.getWidth())/2;
        float top = (squareBitmapWidth-srcBitmap.getHeight())/2;
        canvas.drawBitmap(srcBitmap, left, top, paint);
        // Free the native object associated with this bitmap.
        srcBitmap.recycle();
        // Return the circular bitmap
        return dstBitmap;
    }
    public Bitmap resizeBitmapImage(Bitmap source, int maxResolution)
    {
        int width = source.getWidth();
        int height = source.getHeight();
        int newWidth = width;
        int newHeight = height;
        float rate = 0.0f;

        if(width > height)
        {
            if(maxResolution < width)
            {
                rate = maxResolution / (float) width;
                newHeight = (int) (height * rate);
                newWidth = maxResolution;
            }
        }
        else
        {
            if(maxResolution < height)
            {
                rate = maxResolution / (float) height;
                newWidth = (int) (width * rate);
                newHeight = maxResolution;
            }
        }

        return Bitmap.createScaledBitmap(source, newWidth, newHeight, true);
    }

    public void magnetic_field(final List<LatLng> users, final List<String> profile, final List<String> name, final boolean GameStart){
        handler.removeCallbacks(run);
        run=new Runnable() {
            @Override
            public void run() {
                mMap.clear();

                gpsTracker = new GpsTracker(MapActivity.this);

                // 1. 내 위치 2. 목적지 3. 사람들 위치 4. 경고

                // 현재 위치와 비교를 위한 현재 gps 찾기.
                // 멤버들 위치 보기.
                String myurl=getIntent().getStringExtra("profile");
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng mine = (new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
                markerOptions.position(mine);
                markerOptions.title("mine");
                if(!myurl.equals("")) {
                    if (android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                    }
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromLink(myurl)));
                }else{
                    BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.logo);
                    Bitmap b=bitmapDrawable.getBitmap();
                    Bitmap smallMarker=Bitmap.createScaledBitmap(b,100,100,false);
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getCircularBitmap(smallMarker)));
                }
                mMap.addMarker(markerOptions);
                if(!GameStart) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(mine));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                }
                if(position==null)
                    return;
                markerOptions.position(position);
                markerOptions.title("meeting place");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                mMap.addMarker(markerOptions);
                if(!GameStart) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                }
                if(users==null) {
                    return;
                }
                for(int i=0;i<users.size();i++){
                    String url=profile.get(i);
                    if(users.get(i)==mine)
                        continue;
                    markerOptions.position(users.get(i));
                    markerOptions.title(name.get(i));

                    if(!url.equals("")) {
                        if (android.os.Build.VERSION.SDK_INT > 9) {
                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                            StrictMode.setThreadPolicy(policy);
                        }
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromLink(myurl)));
                    }else{
                        BitmapDrawable bitmapDrawable=(BitmapDrawable)getResources().getDrawable(R.drawable.logo);
                        Bitmap b=bitmapDrawable.getBitmap();
                        Bitmap smallMarker=Bitmap.createScaledBitmap(b,100,100,false);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getCircularBitmap(smallMarker)));
                    }
                    mMap.addMarker(markerOptions);
                }
                LatLng now=new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                com.mgmg.meetinground.distance Distance=new com.mgmg.meetinground.distance("now",SphericalUtil.computeDistanceBetween(now,position));

                circlesize=(int)(meetingTime-System.currentTimeMillis())/10;
                if(circlesize<100)
                    circlesize=100;
                mMap.addCircle(new CircleOptions()
                        .center(position)
                        .radius(circlesize)
                        .fillColor(Color.parseColor("#50F08080"))); // in meters
                if(!first){
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                    first=true;
                }
            }
        };
        handler.post(run);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        String temp=marker.getTitle();
        if(temp.equals("click")){
            AlertDialog.Builder alert = new AlertDialog.Builder(MapActivity.this);
            LatLng now=marker.getPosition();
            Geocoder geocoder=new Geocoder(getBaseContext());
            try{
                List<Address> addresses = geocoder.getFromLocation(now.latitude,now.longitude,1);
                String []splitStr=addresses.get(0).toString().split(",");
                 address=splitStr[0].substring(splitStr[0].indexOf("\"")+1,
                        splitStr[0].length()-2); // 주소 parsing
            }catch (IOException e){
                e.printStackTrace();
            }
            alert.setTitle("선택지점");
            alert.setMessage(address+"를 약속지점으로 선택하시겠습니까?");
            alert.setNegativeButton("아니오",null);

            alert.setNeutralButton("맛집추천받기", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(MapActivity.this,"현재위치"+now.toString(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(MapActivity.this,RecommendActivity.class);
                    intent.putExtra("lat",now.latitude);
                    intent.putExtra("lon",now.longitude);
                    startActivity(intent);
                }
            });

            alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent=getIntent();
                    String roomId=intent.getStringExtra("roomId");

                    database.child("rooms").child(roomId).child("info").child("settings").child("address").setValue(address);
                }
            });
            alert.create().show();
        }
        return false;
    }
}
