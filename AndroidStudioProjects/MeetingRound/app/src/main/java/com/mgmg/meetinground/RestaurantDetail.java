package com.mgmg.meetinground;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class RestaurantDetail extends AppCompatActivity implements OnMapReadyCallback {
    private DatabaseReference database;
    private GoogleMap mMap;
    Restaurant restaurant;
    String roomId, uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_restaurant_detail);

        TextView name;
        TextView tag;
        ImageView imgView,like,hate;

        SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        name = findViewById(R.id.tv_name);
        tag = findViewById(R.id.tv_tag);
        imgView = findViewById(R.id.RestIMG);
        like = findViewById(R.id.btn_like);
        hate = findViewById(R.id.btn_hate);


        restaurant = null;
        Intent intent=getIntent();
        restaurant =  (Restaurant) intent.getParcelableExtra("obj");
        roomId = intent.getStringExtra("roomId");
        uid = intent.getStringExtra("uid");


        if(restaurant != null){
//            Toast.makeText(this, restaurant.getName(), Toast.LENGTH_SHORT).show();
            name.setText(restaurant.getName());
            if(restaurant.getTags() !=null){
                StringBuilder sb = new StringBuilder();


                for (String str : restaurant.getTags()) {
                    sb.append("#");
                    sb.append(str);
                    sb.append("  ");
                }
                tag.setText(sb.toString());

            }

            if(restaurant.getImages().get(0) != null){
                Glide.with(getApplicationContext()).load(restaurant.getImages().get(0)).into(imgView);
            }
        }

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RestaurantDetail.this, "좋아요", Toast.LENGTH_SHORT).show();

                database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("vote").child(uid).setValue(true);
                add(restaurant);
            }
        });

        hate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RestaurantDetail.this, "싫어요", Toast.LENGTH_SHORT).show();

                database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("vote").child(uid).setValue(false);
                add(restaurant);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Toast.makeText(this, "Lat :" + Double.parseDouble(restaurant.getLat()), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Lon :" + Double.parseDouble(restaurant.getLon()), Toast.LENGTH_SHORT).show();
        LatLng rest = new LatLng(Double.parseDouble(restaurant.getLat()),Double.parseDouble(restaurant.getLon()));
        mMap.addMarker(new MarkerOptions()
                .position(rest)
                .title(restaurant.getName()));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rest));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
    }

    public void add(Restaurant restaurant) {
        database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("info").child("name").setValue(restaurant.getName());
        database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("info").child("tel").setValue(restaurant.getTel());
        database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("info").child("address").setValue(restaurant.getAddress());
        database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("info").child("area").setValue(restaurant.getArea());
        database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("info").child("lat").setValue(restaurant.getLat());
        database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("info").child("lon").setValue(restaurant.getLon());
        database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("info").child("tags").setValue(restaurant.getTags());
        database.child("rooms").child(roomId).child("vote").child(restaurant.getId()).child("info").child("images").setValue(restaurant.getImages());
    }
}