package com.example.android.cfgprepapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.MainActivity;
import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.TrainListActivity;
import com.example.android.cfgprepapp.adapter.ShelterListAdapter;
import com.example.android.cfgprepapp.adapter.TrainListAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.location.GPSTracker;
import com.example.android.cfgprepapp.sync.ReminderFirebaseJobService;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.everything.providers.android.contacts.Contact;
import me.everything.providers.core.Data;

public class VolunteerFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    String stringLatitude="0";
    String stringLongitude="0";
    public static final String MyPREFERENCES = "VolunteerPref" ;
    SharedPreferences sharedpreferences;

    private VolunteerFragment.UserLoginTask mAuthTask = null;
    private VolunteerFragment.TrainListTask mTrainTask = null;

    String shelName,facilities="";
    EditText editText,editText2;
    CheckBox food,water,cloth;
    String mobile="1234567890";
    Button mShelterBtn;
    AlertDialog dialog;
    String [][] ShelterListData = new String[1000][5];

    private RecyclerView mRecyclerView;
    private ShelterListAdapter mShelterListAdapter;
    int count=0;

    public VolunteerFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static VolunteerFragment newInstance() {
        VolunteerFragment fragment = new VolunteerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view= inflater.inflate(R.layout.fragment_volunteer, container, false);

        //Creating Switch variable
        final Switch mSwitch=view.findViewById(R.id.switchButton);


        //Shared Preferences
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(!sharedpreferences.contains("volunteer"))
        {
            mSwitch.setChecked(false);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("volunteer", false);
            editor.commit();
            //Craeting Temp Linear Layout Variable
            LinearLayout linLayouttemp=view.findViewById(R.id.volunteerBtns);
            linLayouttemp.setVisibility(View.INVISIBLE);

        }
        else
        {
            mSwitch.setChecked(sharedpreferences.getBoolean("volunteer",false));
            //Craeting Temp Linear Layout Variable
            LinearLayout linLayouttemp=view.findViewById(R.id.volunteerBtns);
            if(sharedpreferences.getBoolean("volunteer",false)) {
                linLayouttemp.setVisibility(View.VISIBLE);
            }else{
                linLayouttemp.setVisibility(View.INVISIBLE);
            }
        }


        //Switch Change Listener
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mSwitch.isChecked())
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("volunteer",true);
                    editor.commit();
                    LinearLayout linLayout=view.findViewById(R.id.volunteerBtns);
                    linLayout.setVisibility(View.VISIBLE);
                }else
                {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putBoolean("volunteer",false);
                    editor.commit();
                    LinearLayout linLayout=view.findViewById(R.id.volunteerBtns);
                    linLayout.setVisibility(View.INVISIBLE);
                    SharedPreferences.Editor editor1 = sharedpreferences.edit();
                    editor1.putBoolean("shelter", false);
                    editor1.commit();
                }
            }
        });

        mShelterBtn=view.findViewById(R.id.provideShelter);
        if(sharedpreferences.contains("shelter") && sharedpreferences.getBoolean("shelter",false)){
            mShelterBtn.setVisibility(View.GONE);
        }

        mShelterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Create a dialog box
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                View mView = getLayoutInflater().inflate(R.layout.dialog_shelter_reg, null);
                Button mDone = (Button) mView.findViewById(R.id.btnDone);
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                if(!sharedpreferences.contains("shelter") || !sharedpreferences.getBoolean("shelter",false))
                {
                    dialog.show();
                }
                editText=mView.findViewById(R.id.shelter);
                editText2=mView.findViewById(R.id.contact);
                water=mView.findViewById(R.id.water);
                food=mView.findViewById(R.id.food);
                cloth=mView.findViewById(R.id.cloth);

                mDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shelName=editText.getText().toString();
                        mobile=editText2.getText().toString();
                        if(water.isChecked())
                        {
                            facilities=facilities+"Water,";
                        }
                        if(food.isChecked()){
                            facilities=facilities+"Food,";
                        }
                        if(cloth.isChecked())
                        {
                            facilities=facilities+"Cloth";
                        }


                        mAuthTask = new UserLoginTask();
                        mAuthTask.execute((Void) null);
                    }
                });

            }
        });
        mTrainTask=new TrainListTask();
        mTrainTask.execute();
        mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_user_list);
        //Setting Recycler Views Layout and Adapter
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mShelterListAdapter = new ShelterListAdapter(getActivity());
        mRecyclerView.setAdapter(mShelterListAdapter);
        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Represents an asynchronous login/registration task used to Register
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        String jsonStr;
        UserLoginTask() {
        }

        @Override
        protected Boolean doInBackground(Void... params) {
                    //If Status Returned true then user is registered already
                    HttpHandler sh1 = new HttpHandler();
                    String ipadd1 = getResources().getString(R.string.ipadd);
                    String url1 = "/CFGAPI/shelter_reg.php";
                    String headurl1 = "http://";
                    url1=headurl1+ipadd1+url1;
                    Log.e("url",url1);

                    //Creating a Hashmap for storing parameters for POST Method.
                    HashMap<String, String> postparam1 = new HashMap<>();
                    JSONObject jsonObj1 = null;
                    // adding each child node to HashMap key => value\

                    GPSTracker gpsTracker = new GPSTracker(getContext());

                    if (gpsTracker.getIsGPSTrackingEnabled())
                    {
                        //Getting Latitude and Longitude
                        stringLatitude = String.valueOf(gpsTracker.latitude);
                        stringLongitude = String.valueOf(gpsTracker.longitude);


                        //Error Checking and Substring to make it ready to store it in the database
                        if(stringLatitude.length()>5) {
                            stringLatitude = stringLatitude.substring(0, 7);
                            stringLongitude = stringLongitude.substring(0, 7);
                        }
                        else
                        {
                            stringLatitude="19.119510";
                            stringLongitude="72.846862";
                        }
                    }
                    else
                    {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings
                        gpsTracker.showSettingsAlert();
                    }

                    postparam1.put("name",shelName);
                    postparam1.put("lati",stringLatitude);
                    postparam1.put("longi",stringLongitude);
                    postparam1.put("mobile",mobile);
                    postparam1.put("facility",facilities);

                    for (Map.Entry<String,String> entry : postparam1.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        Log.e(key,value);
                    }

                    // Making a request to url and getting response
                    jsonStr = sh1.makeServiceCallPost(url1,postparam1);
                    Log.e("Debugg(LoginAct)", "Response from url: " + jsonStr);
                    if (jsonStr != null) {
                        JSONObject jsonObj2 = null;
                        String resp2 = "";
                        try {
                            jsonObj1 = new JSONObject(jsonStr);
                            //Log.e("doInBackgroundl", jsonObj.getString("status"));
                            resp2 = jsonObj1.getString("status");

                        } catch (final JSONException e) {
                            Log.e("Error", "Json parsing error: " + e.getMessage());
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Json parsing error: " + e.getMessage(),
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                        if(resp2.equals("1")) {
                            return true;
                        }
                        else
                        {
                            Log.e("Error", "Couldn't get json from server.");
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Couldn't get json from server. Check LogCat for possible errors!",
                                            Toast.LENGTH_LONG)
                                            .show();

                                }
                            });

                        }
                    }
            //If Status returned false then registration error
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean("shelter", true);
                editor.commit();
                mShelterBtn.setVisibility(View.GONE);
                //Create a dialog box
                dialog.dismiss();

            } else {
                //Registeration Needed
                Toast.makeText(getActivity().getApplicationContext(),
                        "Registration Failed",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }


    private class TrainListTask extends AsyncTask<String, Void, Void> {

        private static final String TAG = "Msg";
        String ipadd,url,headurl;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {

            GPSTracker gpsTracker = new GPSTracker(getContext());
            if (gpsTracker.getIsGPSTrackingEnabled())
            {
                //Getting Latitude and Longitude
                stringLatitude = String.valueOf(gpsTracker.latitude);
                stringLongitude = String.valueOf(gpsTracker.longitude);


                //Error Checking and Substring to make it ready to store it in the database
                if(stringLatitude.length()>5) {
                    stringLatitude = stringLatitude.substring(0, 7);
                    stringLongitude = stringLongitude.substring(0, 7);
                }
                else
                {
                    stringLatitude="19.119510";
                    stringLongitude="72.846862";
                }
            }
            else
            {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showSettingsAlert();
            }

            ipadd = getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();
            url = "/CFGAPI/shelterlist.php";
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
                        String facility=c.getString("facility");
                        String lati=c.getString("lati");
                        String longi=c.getString("longi");
                        String contact=c.getString("contact");
                        ShelterListData[i][0]=name;
                        ShelterListData[i][1]=facility;
                        ShelterListData[i][2]=lati;
                        ShelterListData[i][3]=longi;
                        ShelterListData[i][4]=contact;
                        System.out.println(contact);
                        count++;
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mShelterListAdapter.setTrainListData(ShelterListData,count,stringLatitude,stringLongitude);
        }

    }



}


