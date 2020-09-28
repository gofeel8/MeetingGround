package com.mgmg.meetinground;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RestaurantDetail extends AppCompatActivity {
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_restaurant_detail);

        TextView name;
        TextView tag;
        ImageView imgView;
        Button like,hate;

        name = findViewById(R.id.tv_name);
        tag = findViewById(R.id.tv_tag);
        imgView = findViewById(R.id.RestIMG);
        like = findViewById(R.id.btn_like);
        hate = findViewById(R.id.btn_hate);


        Restaurant restaurant = null;
        Intent intent=getIntent();
        restaurant =  (Restaurant) intent.getParcelableExtra("obj");
        Toast.makeText(this, restaurant.getName(), Toast.LENGTH_SHORT).show();









    }
}