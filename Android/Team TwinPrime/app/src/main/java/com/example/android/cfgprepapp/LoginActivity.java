package com.example.android.cfgprepapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.data.HttpHandler;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username;
    EditText password;
    private LoginTask mLoginTask=null;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TextView loginTextView=findViewById(R.id.login);
        TextView registerTextView=findViewById(R.id.register);

        loginTextView.setOnClickListener(this);
        registerTextView.setOnClickListener(this);

        username=findViewById(R.id.login_page_social_login_text);
        password=findViewById(R.id.login_page_social_login_password);

        SharedPreferences pref = getSharedPreferences("LoginSession", MODE_PRIVATE);
        if(pref.contains("userID"))
        {
            Intent intent =new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    //Listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                //Starting a Async Task
                mLoginTask = new LoginTask(username.getText().toString(),password.getText().toString());
                mLoginTask.execute((Void) null);
                break;
            case R.id.register:
                Intent regIntent=new Intent(this,RegisterActivity.class);
                startActivity(regIntent);
                break;
        }
    }

    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        String jsonStr;
        JSONObject jsonObj = null;

        LoginTask(String username,String password) {
            mUsername=username;
            mPassword=password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpHandler sh = new HttpHandler();
            String ipadd = getResources().getString(R.string.ipadd);
            String url = "/CFGAPI/login.php";
            String headurl = "http://";
            url=headurl+ipadd+url;
            Log.d("url",url);

            //Creating a Hashmap for storing parameters for POST Method.
            HashMap<String, String> postparam = new HashMap<>();

            // adding each child node to HashMap key => value
            postparam.put("username", mUsername);
            postparam.put("password",mPassword);
            for (Map.Entry<String,String> entry : postparam.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                //Log.e(key,value);
            }

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCallPost(url,postparam);
            Log.d("Debugg(LoginAct)", "Response from url: " + jsonStr); //The Output of First Page

            //JSON Parsing
            if (jsonStr != null) {

                String resp="";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    //Log.e("doInBackgroundl", jsonObj.getString("status"));
                    resp=jsonObj.getString("status");

                } catch (final JSONException e) {
                    Log.e("Error", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this,
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
                if(resp.equals("1"))
                {
                    //If Status Returned true then login is successful
                    return true;
                }

            } else {
                Log.e("Error", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*Toast.makeText(LoginActivity.this,
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                        */
                    }
                });
            }

            //If Status returned false then login error
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mLoginTask = null;

            if (success) {
                //JSON Data
                JSONObject Data=null;
                try {
                    Data=jsonObj.getJSONObject("0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Setting Shared Preference
                pref = getApplicationContext().getSharedPreferences("LoginSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                try {
                    editor.putString("userID", Data.getString("u_id"));
                    editor.putString("name",Data.getString("name"));
                    editor.putString("age",Data.getString("age"));
                    editor.putString("gender",Data.getString("gender"));
                    editor.putString("username",Data.getString("username"));
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FirebaseMessaging.getInstance().subscribeToTopic("global");
                //Starting new Intent
                Intent intent =new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                //Error no data found
            }
        }

        @Override
        protected void onCancelled() {
            mLoginTask = null;
        }
    }
}
