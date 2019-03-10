package com.example.android.cfgprepapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForumActivity_Add extends AppCompatActivity {
    private  String url = "/CFGAPI/selectforum.php";
    private String headurl = "http://";
    String ipadd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_add);
        CircularProgressButton cpb1 = (CircularProgressButton)findViewById(R.id.circular_progress_bar_1);
        cpb1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof CircularProgressButton) {
                    int id = v.getId();
                    if (id == R.id.circular_progress_bar_1) {

                        EditText question=(EditText)findViewById(R.id.question);
                        EditText detail=(EditText)findViewById(R.id.details);
                        String queststring=question.getText().toString();
                        String detstring=detail.getText().toString();
                        new FalseProgress((CircularProgressButton) v,queststring,detstring).execute(100);
                    }
                }
            }
        });
    }
    private class FalseProgress extends AsyncTask<Integer, Integer, Integer> {

        private CircularProgressButton cpb;
        String quest,detail,jsonStr;

        public FalseProgress(CircularProgressButton cpb,String quest, String detail) {
            this.cpb = cpb;
            this.quest=quest;
            this.detail=detail;
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            ipadd = getResources().getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();
            url = "/CFGAPI/forumadd.php";
            headurl = "http://";
            url=headurl+ipadd+url;
            publishProgress(10);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashMap<String, String> postparam = new HashMap<>();
            // adding each child node to HashMap key => value\
            postparam.put("quest", quest);
            postparam.put("detail", detail);
            publishProgress(20);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Map.Entry<String,String> entry : postparam.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.e(key,value);
            }
            jsonStr = sh.makeServiceCallPost(url,postparam);
            publishProgress(60);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("doInBackgroundl", jsonStr);
            //JSON Parsing
            if (jsonStr != null)
            {
                JSONObject jsonObj = null;
                String resp="";
                publishProgress(70);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    jsonObj = new JSONObject(jsonStr);
                    Log.e("doInBackgroundl", jsonObj.getString("status"));
                    resp=jsonObj.getString("status");

                } catch (final JSONException e) {
                    publishProgress(-1);
                    Log.e("Error", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                    return -1;
                }
                if(resp.equals("1"))
                {
                    //If Status Returned true then registration is successful
                    publishProgress(90);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return params[0];
                }else
                {
                    return -1;
                }

            }
            else
            {
                Log.e("Error", "Couldn't get json from server.");
                publishProgress(-1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();

                    }
                });
                return -1;
            }

        }

        @Override
        protected void onPostExecute(Integer result) {
            Log.e("Result of CPB",String.valueOf(result));
            if(result !=-1) {
                cpb.setProgress(result);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = getIntent();
                        intent.putExtra("Activity", "ForumAdd");
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, 1000);

            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            cpb.setProgress(progress);
        }
    }
}
