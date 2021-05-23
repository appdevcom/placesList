package com.dam.placeslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.preference.PreferenceManager;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText login_txt, password_txt;
    Button login_btn;
    Context context;
    TextView register_tv;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = getApplicationContext();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove("userid").apply();

        login_txt = findViewById(R.id.login_txt);
        password_txt = findViewById(R.id.password_txt);
        register_tv = findViewById(R.id.register_tv);
        login_btn = findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user, pass;
                int error;

                user= login_txt.getText().toString();
                pass= password_txt.getText().toString();

                error=0;
                if (user.isEmpty()) {login_txt.setError("required"); error=1;}
                if (pass.isEmpty()) {password_txt.setError("required"); error=1;}

                if (error == 0) {
                    do_login(user, pass);
                }
            }
        });

        register_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        //delete login




    }


    void do_login(String user, String pass){


        String url = getApplicationContext().getResources().getString(R.string.apilogin);
        String urllogin = url + "login/";




        StringRequest stringRequest = new StringRequest(Request.Method.POST, urllogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            int userid = Integer.parseInt(response);
                            if (userid > 0)
                                after_login(userid);
                        }
                        catch (NumberFormatException ex){
                            Toast toast = Toast.makeText(context , response, Toast.LENGTH_LONG);
                            toast.show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast toast = Toast.makeText(context, error.toString(), Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user", user);
                params.put("pass", pass);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }



    void after_login(int userid){
        /*SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("userid", userid);
        editor.commit();*/
        sharedPreferences.edit().putInt("userid", userid).apply();

        // open main window
        Intent intent;
        intent = new Intent(this,MainActivity.class);
        //intent.putExtra("userid", userid);  // if you want to pass to main activity
        startActivity(intent);
    }

}