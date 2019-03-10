package com.example.android.cfgprepapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.cfgprepapp.adapter.SubcategoryAdapter;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends AppCompatActivity {

    public static final String UC = "Upload/Change Profile Photo";
    public static final String Rem = "Remove Profile Photo";
    public static final String EditP = "Edit Profile";
    private ListView mListView;
    private List<String> mTabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mTabs = new ArrayList<String>();
        mTabs.add(UC);
        mTabs.add(Rem);
        mTabs.add(EditP);
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(new SubcategoryAdapter(this, mTabs));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                String leftMenu = mTabs.get(position);
                if(UC.equals(leftMenu)){
                    intent = new Intent(SettingActivity.this,UploadActivity.class);
                    startActivity(intent);
                } /*else if (Rem.equals(leftMenu)) {
                    intent = new Intent(this, TabShopActivity.class);
                }*/

            }
        });
    }
}
