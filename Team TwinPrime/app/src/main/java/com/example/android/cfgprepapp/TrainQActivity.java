package com.example.android.cfgprepapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.adapter.TrainListAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrainQActivity extends AppCompatActivity {

    String [][] TrainListData = new String[1000][5];

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private TrainListAdapter mTrainListAdapter;

    String ipadd,url,headurl;
    int count=0;

    String status_val;
    int rush_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_q);

        //Actionbar Title Change
        setTitle("Rush Estimation");

        final RadioButton m_one = (RadioButton) findViewById(R.id.first_radio_button);
        final RadioButton m_two = (RadioButton) findViewById(R.id.second_radio_button);
        final RadioButton m_three = (RadioButton) findViewById(R.id.third_radio_button);
        final RadioButton m_four= (RadioButton) findViewById(R.id.fourth_radio_button);
        m_one.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one.setChecked(true);
                m_two.setChecked(false);
                m_three.setChecked(false);
                m_four.setChecked(false);
            }
        });

        m_two.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one.setChecked(false);
                m_two.setChecked(true);
                m_three.setChecked(false);
                m_four.setChecked(false);
            }
        });

        m_three.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one.setChecked(false);
                m_two.setChecked(false);
                m_three.setChecked(true);
                m_four.setChecked(false);
            }
        });

        m_four.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one.setChecked(false);
                m_two.setChecked(false);
                m_three.setChecked(false);
                m_four.setChecked(true);
            }
        });

        if(m_one.isChecked()){
            rush_val=1;
        }else if(m_two.isChecked()){
            rush_val=2;
        }else if(m_three.isChecked()){
            rush_val=3;
        }else{
            rush_val=4;
        }


        final RadioButton m_one2 = (RadioButton) findViewById(R.id.first_radio_button2);
        final RadioButton m_two2 = (RadioButton) findViewById(R.id.second_radio_button2);
        final RadioButton m_three2 = (RadioButton) findViewById(R.id.third_radio_button2);
        m_one2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one2.setChecked(true);
                m_two2.setChecked(false);
                m_three2.setChecked(false);
            }
        });

        m_two2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one2.setChecked(false);
                m_two2.setChecked(true);
                m_three2.setChecked(false);
            }
        });

        m_three2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m_one2.setChecked(false);
                m_two2.setChecked(false);
                m_three2.setChecked(true);
            }
        });

        Bundle b=getIntent().getExtras();
        String tstation=(String)b.get("tstation");

        if(m_one2.isChecked()){
            status_val="Left "+tstation;
        }else if(m_two2.isChecked()) {
            status_val = "At "+tstation;
        }else{
            status_val="Reaching "+tstation;
        }

        Button btn=(Button) findViewById(R.id.q_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TrainListTask().execute();
            }
        });
    }




    private class TrainListTask extends AsyncTask<String, Void, Void> {

        private static final String TAG = "Msg";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            Bundle b=getIntent().getExtras();
            ipadd = getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();

            String tid=(String)b.get("train_id");

            SharedPreferences pref = getSharedPreferences("LoginSession", MODE_PRIVATE);
            String id=pref.getString("userID","1");
            url = "/CFGAPI/trainstatusupdate.php?rush="+rush_val+"&curr_loc_stat="+status_val+"&id="+tid+"&uid="+id;
            headurl = "http://";
            url=headurl+ipadd+url;
            Log.e("url",url);

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr); //The Output of First Page

            if (jsonStr != null) {
                JSONObject jsonObj = null;
                String resp="";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    resp=jsonObj.getString("status");

                } catch (final JSONException e) {
                    Log.e("Error", "Json parsing error: " + e.getMessage());
                }
                if(resp.equals("1"))
                {
                    //If Status Returned true then registration is successful
                    Log.e("Success", "Server Updated ");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TrainQActivity.this,"Thank you", Toast.LENGTH_LONG).show();
                        }
                    });

                }else
                {
                    Log.e("Error", "Server Error");
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Intent intent =new Intent(TrainQActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }
}
