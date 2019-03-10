package com.example.android.cfgprepapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.UserListActivity;
import com.example.android.cfgprepapp.UserProfileActivity;
import com.example.android.cfgprepapp.adapter.SearchAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFragment extends Fragment implements View.OnClickListener,SearchAdapter.UserListAdapterOnClickHandler, TextWatcher {

    String ipadd,url,headurl;
    int count;
    private SearchAdapter mAdapter;

    String [][] UserListData = new String[1000][3];

    private EditText mSearchField;
    private TextView mXMark;
    private View mMicrofon;
    private ListView mListView;
    private RecyclerView mRecyclerView;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search_bar,
                container, false);

        count=0;
        //Get all the elements of the layout
        mSearchField = (EditText) rootView.findViewById(R.id.search_field);
        mXMark = (TextView) rootView.findViewById(R.id.search_x);
        mMicrofon = rootView.findViewById(R.id.search_microfon);
        mListView = (ListView) rootView.findViewById(R.id.list_view);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_user_list);
        //Run the Async Task
        new UserListTask().execute();

        //Setting Listeners
        mXMark.setOnClickListener(this);
        mMicrofon.setOnClickListener(this);
        mSearchField.addTextChangedListener(this);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        //Setting Recycler Views Layout and Adapter
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter=new SearchAdapter(getActivity(),UserListData,this);
        mRecyclerView.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_x:
                mSearchField.setText(null);
                break;
            case R.id.search_microfon:
                Toast.makeText(getActivity(), "Implement voice search", Toast.LENGTH_LONG)
                        .show();
                break;
        }
    }

    @Override
    public void onClick(String UserListID, String UserListName) {
        //Starting a new Activity to show user profile
        Intent intent=new Intent(getActivity(),UserProfileActivity.class);
        intent.putExtra("user_id",UserListID);
        intent.putExtra("user_name",UserListName);
        startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String searchText = s.toString().trim();
        ArrayList<Integer> searchedArray = new ArrayList<Integer>();
        for (int i=0;i<count;i++) {
            if(searchText.length()<=UserListData[i][1].length()) {
                if (UserListData[i][1].toLowerCase().substring(0, searchText.length())
                        .contains(searchText.toLowerCase())) {
                    searchedArray.add(i);
                    Log.d("Users:", UserListData[i][1]);
                }
            }
        }
        if (searchText.isEmpty()) {
            mXMark.setText(R.string.fontello_x_mark);
        } else {
            Log.d("Msg","Text Changed");
            mAdapter.setUserListData(searchedArray);
            mXMark.setText(R.string.fontello_x_mark_masked);
        }
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
                        UserListData[i][0]=id;
                        UserListData[i][1]=name;
                        UserListData[i][2]=pp_url;
                        count++;
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
            } else {
                Log.e(TAG, "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }

    }
}
