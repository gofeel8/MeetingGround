package com.mgmg.meetinground;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendActivity extends AppCompatActivity {
    StringAdapter keyAdapter;
    ResultAdapter resultAdapter;
    ListView lvKey, lvResult;
    String[] keys = {"존맛","혼밥","갬성","깔끔","백종원","가성비","채식","한식","양식","분식","면류","일식","치킨","피자","중식","카페","고기집","디저트","샌드위치","버거","주점","도시락","뷔페","기타"};
    boolean[] checked;
    List<Restaurant> restaurants;
    double lat, lon;
    String uid;
    JSONArray tmp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        lvKey = findViewById(R.id.lvKey);
        lvResult = findViewById(R.id.lvResult);

        Intent intent = getIntent();
        lat = intent.getDoubleExtra("lat", 0);
        lon = intent.getDoubleExtra("lon", 0);
        uid = intent.getStringExtra("uid");

        checked = new boolean[keys.length];
        keyAdapter = new StringAdapter(getApplicationContext(), keys, checked);
        lvKey.setAdapter(keyAdapter);
        lvKey.setOnItemClickListener((parent, view, position, id) -> {
            checked[position] = !checked[position];
            post();
            keyAdapter.notifyDataSetChanged();
        });

        restaurants = new ArrayList<>();
        resultAdapter = new ResultAdapter(getApplicationContext(), restaurants);
        lvResult.setAdapter(resultAdapter);
        lvResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), RestaurantDetail.class);
                intent.putExtra("obj", restaurants.get(position));
                intent.putExtras(getIntent());
                startActivity(intent);
            }
        });

        // 최초 1회 실행
        post();
    }

    public void post() {
        try {
            // 아이템이 클릭될 때마다 비동기 요청 전송
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://j3b207.p.ssafy.io:8080/api/search";
            JSONObject obj = new JSONObject();
            obj.put("lon", lat);
            obj.put("lat", lon);
            JSONArray arr = new JSONArray(checked);
            obj.put("keys", arr);

            // Add the request to the RequestQueue.
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, obj, response -> {
                try {
                    restaurants.clear();
                    tmp = response.getJSONArray("result");
                    for (int i = 0; i < tmp.length(); i++) {
                        restaurants.add(new Gson().fromJson(tmp.getJSONObject(i).toString(), Restaurant.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                resultAdapter.notifyDataSetChanged();
            }, error -> error.printStackTrace());

            queue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}