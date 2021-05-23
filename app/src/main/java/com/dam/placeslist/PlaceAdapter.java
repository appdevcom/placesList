package com.dam.placeslist;


import android.content.Context;
import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private final Context context;
    private final List<Place> list;

    int userid;

    public PlaceAdapter(Context context, List<Place> list) {
        this.context = context;
        this.list = list;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        userid = sharedPreferences.getInt("userid", 0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = list.get(position);

        int idplace= place.getId();



        holder.name_tv.setText(String.format("%d-%s", idplace, place.getName()));
        holder.address_tv.setText(place.getAddress());
        holder.description_tv.setText(place.getDescription());

        Glide.with(holder.image_iv.getContext())
                .load(place.getImage())
                .into(holder.image_iv);

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("iduser", userid);
                    jsonBody.put("idplace", idplace);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                String urlpost = context.getApplicationContext().getResources().getString(R.string.apilike);

                JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, urlpost,jsonBody,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String toshow = jsonBody.toString() + "  \n" +  response.toString();

                                Toast toast = Toast.makeText(context.getApplicationContext(), toshow , Toast.LENGTH_LONG);
                                toast.show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast toast = Toast.makeText(context.getApplicationContext(), error.toString(), Toast.LENGTH_LONG);
                                toast.show();
                            }
                        }
                );

                RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
                requestQueue.add(jsonobj);

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name_tv, address_tv, description_tv;
        public ImageView image_iv;
        public Button like_btn;

        public ViewHolder(View itemView) {
            super(itemView);

            name_tv = itemView.findViewById(R.id.name_tv);
            address_tv = itemView.findViewById(R.id.address_tv);
            description_tv = itemView.findViewById(R.id.description_tv);
            image_iv = itemView.findViewById(R.id.image_iv);
            like_btn = itemView.findViewById(R.id.like_btn);
        }
    }

}
