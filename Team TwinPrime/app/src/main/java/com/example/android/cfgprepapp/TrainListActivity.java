package com.example.android.cfgprepapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.adapter.TrainListAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrainListActivity extends AppCompatActivity implements TrainListAdapter.TrainListAdapterOnClickHandler{

    String [][] TrainListData = new String[1000][5];

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;

    private TrainListAdapter mTrainListAdapter;

    String ipadd,url,headurl;
    int count=0;
    String tstation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_follow);

        //Actionbar Title Change
        setTitle("Which Train you are in?");

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_user_list);
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_mess);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //Setting Recycler Views Layout and Adapter
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mTrainListAdapter = new TrainListAdapter(this);
        mRecyclerView.setAdapter(mTrainListAdapter);

        showTrainListView();
        new TrainListTask().execute();
    }

    @Override
    public void onClick(String TrainListID) {
        //Starting a new Activity to show user profile
        Intent intent=new Intent(TrainListActivity.this,TrainQActivity.class);
        intent.putExtra("train_id",TrainListID);
        intent.putExtra("tstation",tstation);
        startActivity(intent);
    }


    private void showTrainListView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
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
            tstation="Matunga";//(String)b.get("station");

            ipadd = getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();
            url = "/CFGAPI/trainlist.php?tstation="+tstation;
            headurl = "http://";
            url=headurl+ipadd+url;
            Log.e("url",url);

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr); //The Output of First Page

            if (jsonStr != null) {
                try {
                    JSONArray names = new JSONArray(jsonStr);
                    for (int i = 0; i < names.length(); i++) {
                        JSONObject c = names.getJSONObject(i);
                        String src = c.getString("src");
                        String dest=c.getString("dest");
                        String time=c.getString("time");
                        String type=c.getString("type");
                        String id=c.getString("id");
                        TrainListData[i][0]=src;
                        TrainListData[i][1]=dest;
                        TrainListData[i][2]=time;
                        TrainListData[i][3]=type;
                        TrainListData[i][4]=id;
                        count++;
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(TrainListActivity.this,
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            showTrainListView();

            if(count>0) {
                //Call the Recycler View Adapter
                mTrainListAdapter.setTrainListData(TrainListData,count);
            }else {
                showErrorMessage();
            }
        }

    }
}
