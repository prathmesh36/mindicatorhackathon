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

import com.example.android.cfgprepapp.adapter.BusListAdapter;
import com.example.android.cfgprepapp.adapter.BusListAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusListActivity extends AppCompatActivity implements BusListAdapter.BusListAdapterOnClickHandler{

    private RecyclerView mRecyclerView;

    private BusListAdapter mBusListAdapter;

    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        //Actionbar Title Change
        setTitle("Bus Nos");
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_user_list);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        //Setting Recycler Views Layout and Adapter
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mBusListAdapter = new BusListAdapter(this);
        mRecyclerView.setAdapter(mBusListAdapter);
        Intent intent= getIntent();
        String name[] = intent.getStringArrayExtra("busno");
        mBusListAdapter.setBusListData(name,name.length);
    }

    @Override
    public void onClick(String BusListID,String BusListName) {
        //Starting a new Activity to show user profile
        /*Intent intent=new Intent(BusListActivity.this,UserProfileActivity.class);
        intent.putExtra("user_id",BusListID);
        intent.putExtra("user_name",BusListName);
        startActivity(intent);*/
    }



}
