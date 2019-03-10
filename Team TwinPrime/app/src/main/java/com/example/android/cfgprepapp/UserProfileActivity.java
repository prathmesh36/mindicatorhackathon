package com.example.android.cfgprepapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.fragment.ProfileTabsFragment_Shop;
import com.example.android.cfgprepapp.fragment.ProfileTabsFragment_Social;
import com.example.android.cfgprepapp.fragment.ProfileTabsFragment_UserDetail;
import com.example.android.cfgprepapp.util.ImageUtil;
import com.example.android.cfgprepapp.view.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TABS_TRAVEL = "Travel tabs";
    public static final String TABS_MEDIA = "Media tabs";
    public static final String TABS_SHOP = "Shop tabs";
    public static final String TABS_SOCIAL = "Social tabs";
    public static final String TABS_NEWS = "News tabs";
    public static final String TABS_UNIVERSAL = "Universal tabs";

    private ListView mListView;
    private List<String> mTabs;
    private UserProfileActivity.MyPagerAdapter adapter;
    private LinearLayout toolbar;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private TextView toolbarLike;
    private TextView toolbarFavorite;
    private TextView toolbarShare;

    UserProfileActivity.ProfileTask mProfileTask=null;
    String user_id="-1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Getting user_id from Intent
        Intent intent=new Intent();
        Bundle b=getIntent().getExtras();
        user_id=(String)b.get("user_id");
        String user_name=(String)b.get("user_name");

        //Actionbar Name
        setTitle("User Profile");
        getSupportActionBar().setIcon(R.drawable.ic_user_profile);

        //Tabs
        mTabs = new ArrayList<String>();
        mTabs.add(TABS_MEDIA);
        mTabs.add(TABS_SHOP);
        mTabs.add(TABS_SOCIAL);
        mTabs.add(TABS_TRAVEL);
        mTabs.add(TABS_UNIVERSAL);

        //Get the tab layout and pager in the variable below
        tabs = (PagerSlidingTabStrip) findViewById(R.id.activity_tab_social_tabs);
        pager = (ViewPager) findViewById(R.id.activity_tab_social_pager);

        //create a adapter and set it to pager
        adapter = new UserProfileActivity.MyPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        //connect the tab and the pager
        tabs.setViewPager(pager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
                .getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        pager.setCurrentItem(2);


        tabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(getApplicationContext(), "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        //Starting a Async Task
        mProfileTask = new UserProfileActivity.ProfileTask(user_id);
        mProfileTask.execute((Void) null);

    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

        //Add Different pages here
        private final ArrayList<String> tabNames = new ArrayList<String>() {{
            add("Media");
            add("Shop");
            add("Social");
        }};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames.get(position);
        }

        @Override
        public int getCount() {
            return tabNames.size();
        }

        //Return Fragment for each page according to the position
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            if(position == 0) {
                return ProfileTabsFragment_UserDetail.newInstance(position,user_id);
            }
            else if(position == 1) {
                return ProfileTabsFragment_Shop.newInstance(position);
            }
            else{
                return ProfileTabsFragment_Social.newInstance(position);
            }
        }
    }

    //Listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_tab_social_like:
                Toast.makeText(this, "Like", Toast.LENGTH_SHORT).show();
                break;
            case R.id.toolbar_tab_social_favorite:
                Toast.makeText(this, "Favorite", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.toolbar_tab_social_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public class ProfileTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUId;
        String jsonStr;
        JSONObject jsonObj = null;

        ProfileTask(String uid) {
            mUId=uid;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpHandler sh = new HttpHandler();
            String ipadd = getResources().getString(R.string.ipadd);
            String url = "/CFGAPI/userdetail.php";
            String headurl = "http://";
            url=headurl+ipadd+url;
            Log.e("url",url);

            //Creating a Hashmap for storing parameters for POST Method.
            HashMap<String, String> postparam = new HashMap<>();

            // adding each child node to HashMap key => value
            postparam.put("id", mUId);
            for (Map.Entry<String,String> entry : postparam.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.e(key,value);
            }

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCallPost(url,postparam);
            Log.e("Debugg(LoginAct)", "Response from url: " + jsonStr); //The Output of First Page

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
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
                if(resp.equals("1"))
                {
                    //If Status Returned true then data is fetched
                    return true;
                }

            } else {
                Log.e("Error", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();

                    }
                });
            }

            //If Status returned false then Data is not fetched
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mProfileTask = null;

            if (success) {
                //Getting JSON Data
                JSONObject proData = null;
                try {
                    proData = jsonObj.getJSONObject("0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Displaying data
                try {
                    TextView tvName =findViewById(R.id.toolbar_tab_social_name);
                    tvName.setText(proData.getString("name"));
                    TextView tvEmail=findViewById(R.id.toolbar_tab_social_place);
                    tvEmail.setText("Mumbai");
                    ImageView image_profile_pic = (ImageView) findViewById(R.id.toolbar_tab_social_image);
                    Log.e("URL",proData.getString("profile_pic_url"));
                    ImageUtil.displayRoundImage(
                            image_profile_pic,proData.getString("profile_pic_url"),
                            null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            } else {
                //Error no data found
            }
        }

        @Override
        protected void onCancelled() {
            mProfileTask = null;
        }
    }


}
