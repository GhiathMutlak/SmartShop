package com.applefish.smartshop.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.applefish.smartshop.R;
import com.applefish.smartshop.classes.ConnectChecked;
import com.applefish.smartshop.fcm.SharedPrefManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.applefish.smartshop.fcm.EndPoints.URL_REGISTER_DEVICE;
import static com.applefish.smartshop.fcm.EndPoints.URL_UNREGISTER_DEVICE;

public class SettingActivity extends AppCompatActivity {
    //defining views
   // private Button buttonRegister;
   // private EditText editTextEmail;
    private Switch  switchNotification;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //getting views from xml
       // editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        switchNotification=(Switch) findViewById(R.id.switch1);
        //buttonRegister = (Button) findViewById(R.id.buttonRegister);

        String favoriteOffer=readSharedPreference();
        if(favoriteOffer.equals("") || favoriteOffer.equals("off"))
        { switchNotification.setChecked(false);}
        else
        { switchNotification.setChecked(true);}

        switchNotification.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                String favoriteOffer=readSharedPreference();
                 if(switchNotification.isChecked())
                {
                     if(ConnectChecked.isNetworkAvailable(getBaseContext())&& ConnectChecked.isOnline())
                     {
                             if(favoriteOffer.equals("") || favoriteOffer.equals("off"))
                        {
                            sendTokenToServer();
                            writeSharedPreference("on");
                            //Toast.makeText(getBaseContext(), "Switch on", Toast.LENGTH_LONG).show();
                        }
                     }
                    else
                     {
                         switchNotification.setChecked(false);
                         Snackbar.make(buttonView, "No Internet Connection", Snackbar.LENGTH_LONG)
                                 .setAction("Action", null).show();
                     }

                }
                else
                {
                    if(ConnectChecked.isNetworkAvailable(getBaseContext())&& ConnectChecked.isOnline())
                    {
                        if(favoriteOffer.equals("on"))
                        {
                           deleteTokenToServer();
                            writeSharedPreference("off");
                          //  Toast.makeText(getBaseContext(), "Switch off", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        switchNotification.setChecked(true);
                        Snackbar.make(buttonView, "No Internet Connection", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }



                }


            }


        });
    }
    //storing token to mysql server
    private void sendTokenToServer() {
        progressDialog = new ProgressDialog(this);
       // progressDialog.setMessage("Registering Device...");
        progressDialog.setMessage("Notification  Switch...");
        progressDialog.show();

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        //final String email = editTextEmail.getText().toString();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(SettingActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", token);
                params.put("token", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //delete token from mysql server
    private void deleteTokenToServer() {
        progressDialog = new ProgressDialog(this);
        // progressDialog.setMessage("Registering Device...");
        progressDialog.setMessage("Notification  Switch...");
        progressDialog.show();

        final String token = SharedPrefManager.getInstance(this).getDeviceToken();
        //final String email = editTextEmail.getText().toString();

        if (token == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Token not generated", Toast.LENGTH_LONG).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UNREGISTER_DEVICE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            Toast.makeText(SettingActivity.this, obj.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", token);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

//    public void Click(View view) {
//        if (view == buttonRegister) {
//            sendTokenToServer();
//        }
//    }

    public String readSharedPreference()
    {
        SharedPreferences sharedPref =getBaseContext().getSharedPreferences("com.applefish.smartshop.SETTING_KEY",MODE_PRIVATE);
        //0 is default_value if no vaule
        String savedSetting = sharedPref .getString(getString(R.string.saved_setting), "");

        return savedSetting;
    }
    public  void  writeSharedPreference(String savedSetting)
    {
        SharedPreferences sharedPref =getBaseContext().getSharedPreferences("com.applefish.smartshop.SETTING_KEY",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_setting), savedSetting);
        editor.commit();
    }


}
