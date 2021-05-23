package com.dam.placeslist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
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

public class RegisterActivity extends AppCompatActivity {

    EditText login2_txt, password2_txt, password3_txt, email2_txt;
    Button register_btn;
    Context context;
    TextView login_tv;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        login2_txt = findViewById(R.id.login2_txt);
        email2_txt = findViewById(R.id.email2_txt);
        password2_txt = findViewById(R.id.password2_txt);
        password3_txt = findViewById(R.id.password3_txt);
        login_tv = findViewById(R.id.login_tv);
        register_btn = findViewById(R.id.register_btn);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user, pass2, pass3, email2;
                int error;

                user= login2_txt.getText().toString();
                pass2= password2_txt.getText().toString();
                pass3= password3_txt.getText().toString();
                email2= email2_txt.getText().toString().trim();

                error=0;
                if (user.isEmpty()) {login2_txt.setError("required"); error=1;}
                if (pass2.isEmpty()) {password2_txt.setError("required"); error=1;}
                if (pass3.isEmpty()) {password3_txt.setError("required"); error=1;}
                if (email2.isEmpty()) {email2_txt.setError("required"); error=1;}

                if (!Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
                    email2_txt.setError("Enter Valid Email Address");
                    email2_txt.requestFocus();
                    error = 1;
                }

                if (!pass2.equals(pass3)){
                    password3_txt.setError("passwords don't match!");
                    password3_txt.requestFocus();
                    error=1;
                }

                if (error == 0) {
                    do_register(user, pass2, email2);
                }

            }
        });

        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }


    void do_register(String user, String pass, String email){


        String url = getApplicationContext().getResources().getString(R.string.apilogin);
        String urllogin = url + "register/";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, urllogin,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            int userid = Integer.parseInt(response);
                            if (userid > 0)
                                after_register(userid);
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
                params.put("email", email);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);


    }



    void after_register(int userid){
        sharedPreferences.edit().putInt("userid", userid).apply();

        // open main window
        Intent intent;
        intent = new Intent(this,MainActivity.class);
        //intent.putExtra("userid", userid);  // if you want to pass to main activity
        startActivity(intent);
    }


}