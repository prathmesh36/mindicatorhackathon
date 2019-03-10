package com.example.android.cfgprepapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.BusListActivity;
import com.example.android.cfgprepapp.adapter.NearShelterAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.location.GPSTracker;
import com.example.android.cfgprepapp.sync.ReminderFirebaseJobService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.android.cfgprepapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SheltersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SheltersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SheltersFragment extends Fragment implements  NearShelterAdapter.NearShelterAdapterOnClickHandler {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private NearShelterAdapter mNearShelterAdapter;
    private CardView mResultCard;
    private EditText mSearchField;
    private String TAG = "MapFragment";
    private GetShelter mTask;
    // URL to get contacts JSON
    private  String url = "/HHAPI/select2.php";
    private String headurl = "http://";
    String ipadd;
    ArrayList<HashMap<String, String>> shelterList;
    String [][]shelterdet = new String[6][3];
    private GoogleMap map;
    JSONArray busArray[]= new JSONArray[6];

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public static SheltersFragment newInstance() {
        return new SheltersFragment();
    }
    public SheltersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SheltersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SheltersFragment newInstance(String param1, String param2) {
        SheltersFragment fragment = new SheltersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null, false);
        shelterList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_shelter);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) view.findViewById(R.id.error_mess);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.loadbar);
        mSearchField =(EditText)view.findViewById(R.id.search_field);
        mResultCard=(CardView)view.findViewById(R.id.card_viewresult);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);


        mNearShelterAdapter = new NearShelterAdapter(this);
        mRecyclerView.setAdapter(mNearShelterAdapter);
        showShelterDataView();
        new GetShelter().execute("nostring");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

    private void showShelterDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
        mResultCard.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String ProfileData,String[] busno) {
        Intent intentnext = new Intent(getActivity(), BusListActivity.class);
        intentnext.putExtra("busno",busno);
        startActivity(intentnext);
    }

    private class GetShelter extends AsyncTask<String, Void, Void> implements OnMapReadyCallback {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... strings) {
            GPSTracker gpsTracker;
            gpsTracker = new GPSTracker(getContext());
            String stringLatitude="0";
            String stringLongitude="0";
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

            ipadd = getActivity().getResources().getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();

            url = "/CFGAPI/transit.php?lati="+stringLatitude+"&longi="+stringLongitude;
            headurl = "http://";
            url=headurl+ipadd+url;
            Log.e("url",url);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr); //The Output of First Page

            if (jsonStr != null) {
                try {

                    // Getting JSON Array node
                    JSONArray names = new JSONArray(jsonStr);
                    shelterList.clear();
                    // looping through All Contacts
                    for (int i = 0; i < names.length(); i++) {
                        JSONObject c = names.getJSONObject(i);

                        String name = c.getString("name");
                        Double lati = c.getDouble("lat");
                        Double longi = c.getDouble("long");
                        busArray[i]=c.getJSONArray("busno");

                        // tmp hash map for single contact
                        HashMap<String, String> shelname = new HashMap<>();

                        // adding each child node to HashMap key => value\
                        shelname.put("name", name);
                        //Log.e("Names",contact.get("name").toString());
                        // adding contact to contact list

                        shelterList.add(shelname);
                        shelterdet[i][0]=name;
                        shelterdet[i][1]=lati.toString();
                        shelterdet[i][2]=longi.toString();
                        Log.e("Lati",shelterdet[i][1]);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
                Log.e("set","yes");


            } else {
                Log.e(TAG, "Couldn't get json from server.");
                /*getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();

                    }
                });*/
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            /**
             * Updating parsed JSON data into ListView
             * */

            mLoadingIndicator.setVisibility(View.INVISIBLE);
            showShelterDataView();
            /*ListAdapter adapter = new SimpleAdapter(
                    SheltersActivity.this, shelterList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            lv.setAdapter(adapter);*/
            if(shelterList.size()>0) {
                HashMap<String, String> m = shelterList.get(0);
                String strArr[] = new String[shelterList.size() * m.size()];
                int i = 0;
                for (HashMap<String, String> hash : shelterList) {
                    for (String current : hash.values()) {
                        strArr[i] = current;
                        i++;
                    }
                }
                mNearShelterAdapter.setShelterData(strArr,busArray);
            }else {
                showErrorMessage();
            }
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {

            map = googleMap;
            boolean success = map.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
            LatLng sheltermark= new LatLng(26.2006,92.9376);
            Log.e("call",String.valueOf(shelterdet.length));
            if(shelterdet.length>0) {
                for (int i=0;i<shelterdet.length;i++) {
                    Log.e("name","N-"+shelterdet[i][0]);
                    sheltermark = new LatLng(Double.parseDouble(shelterdet[i][2]),Double.parseDouble(shelterdet[i][1]));
                    map.addMarker(new MarkerOptions().position(sheltermark).title(shelterdet[i][0]));
                }
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(sheltermark));
            map.animateCamera( CameraUpdateFactory.zoomTo( 15.2f ) );
        }
    }
}
