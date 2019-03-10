package com.example.android.cfgprepapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.android.cfgprepapp.adapter.UserListAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserListActivity extends AppCompatActivity implements UserListAdapter.UserListAdapterOnClickHandler{

    String [][] UserListData = new String[1000][4];

    private TextView mErrorMessageDisplay;
    private RecyclerView mRecyclerView;

    private UserListAdapter mUserListAdapter;

    String ipadd,url,headurl;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list_follow2);

        //Actionbar Title Change
        setTitle("User of the Month");

        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_user_list);
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_mess);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //Setting Recycler Views Layout and Adapter
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mUserListAdapter = new UserListAdapter(this);
        mRecyclerView.setAdapter(mUserListAdapter);

        showUserListView();

        new UserListTask().execute();
    }

    @Override
    public void onClick(String UserListID,String UserListName) {
        //Starting a new Activity to show user profile
        Intent intent=new Intent(UserListActivity.this,UserProfileActivity.class);
        intent.putExtra("user_id",UserListID);
        intent.putExtra("user_name",UserListName);
        startActivity(intent);
    }


    private void showUserListView() {
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

    private class UserListTask extends AsyncTask<String, Void, Void> {

        private static final String TAG = "Msg";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {

            ipadd = getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();
            url = "/CFGAPI/userlist.php";
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
                        String name = c.getString("name");
                        String pp_url=c.getString("profile_pic_url");
                        String id=c.getString("u_id");
                        String rating=c.getString("rating");
                        UserListData[i][0]=id;
                        UserListData[i][1]=name;
                        UserListData[i][2]=pp_url;
                        UserListData[i][3]=rating;
                        count++;
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UserListActivity.this,
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

            showUserListView();

            if(count>0) {
                //Call the Recycler View Adapter
                mUserListAdapter.setUserListData(UserListData,count);
            }else {
                showErrorMessage();
            }
        }

    }
}
