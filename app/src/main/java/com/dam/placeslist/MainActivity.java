package com.dam.placeslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.SharedPreferences;

import android.os.Bundle;
//import android.preference.PreferenceManager;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView list1;
    private LinearLayoutManager linearLayoutManager;
    private DividerItemDecoration dividerItemDecoration;
    private List<Place> placeList;
    private RecyclerView.Adapter adapter;
    private TextView title_tv;

    private String url;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Intent intent = getIntent();  // if you want to get userid
        //int userid = intent.getIntExtra("userid", 0);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int userid = sharedPreferences.getInt("userid", 0);

        url = getResources().getString(R.string.apiplaces);

        list1 = findViewById(R.id.list1);
        title_tv = findViewById(R.id.title_tv);

        title_tv.setText("Logged in userid: " + String.valueOf(userid));

        placeList = new ArrayList<>();
        adapter = new PlaceAdapter(getApplicationContext(),placeList);

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dividerItemDecoration = new DividerItemDecoration(list1.getContext(),
                linearLayoutManager.getOrientation());

        list1.setHasFixedSize(true);
        list1.setLayoutManager(linearLayoutManager);
        list1.addItemDecoration(dividerItemDecoration);
        list1.setAdapter(adapter);

        title_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        getData();
    }


    private void getData() {


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);

                        Place Place = new Place();
                        Place.setId(jsonObject.getInt("id"));
                        Place.setName(jsonObject.getString("name"));
                        Place.setAddress(jsonObject.getString("address"));

                        String latlon = "LAT:" +  jsonObject.getString("latitude") +
                                " LNG:" + jsonObject.getString("longitude");

                        Place.setDescription(latlon);

                        Place.setImage(jsonObject.getString("image"));

                        placeList.add(Place);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    void logout(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPreferences.edit().remove("userid").apply();
        finish();
    }
    
    
    
}