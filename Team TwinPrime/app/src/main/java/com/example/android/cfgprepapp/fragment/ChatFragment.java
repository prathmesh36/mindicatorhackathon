package com.example.android.cfgprepapp.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.adapter.ChatAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

//import android.support.v7.widget.CardView;

public class ChatFragment extends Fragment implements ChatAdapter.ChatAdapterOnClickHandler,OnClickListener{

    public static final int REQUEST_CODE = 1;
    private static int SCROLL_STAT = 0;
    private RecyclerView mRecyclerView;
    int gpos;
    private ChatAdapter mChatAdapter;
    private ProgressBar mLoadingIndicator;
    final String strArr[][]={{ "All Trains after andheri are delayed","11.00","1"},{ "Overhead Wire Issue","11.02","0"},{ "Any Updates about Andheri?","11.02","1"},{ "Hackathon at VJTI","11.00","0"},{ "Awesome!!","11.00","1"}};
    private boolean allowRefresh=true;
    // URL to get contacts JSON
    private  String url = "/CFGAPI/selectforum.php";
    private String headurl = "http://";
    String ipadd;
    ArrayList<HashMap<String, String>> chatList;
    String [][]chatDet = new String[5][3];

    public static ChatFragment newInstance() {
        ChatFragment f = new ChatFragment();
        return f;
    }

    public static ChatFragment newInstance(int scrollpos) {
        ChatFragment f = new ChatFragment();
        SCROLL_STAT=scrollpos;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view=inflater.inflate(R.layout.activity_message_list, container, false);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        chatList = new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.reyclerview_message_list);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        //mRecyclerView.setBackgroundResource(R.drawable.background_media);

        mChatAdapter = new ChatAdapter(this);
        mRecyclerView.setAdapter(mChatAdapter);
        //mChatAdapter.notifyDataSetChanged();
        mChatAdapter.setChatData(strArr,5);

        return view;


    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onClick(String[] data,int pos,ProgressBar lb) {
        Log.e("Data ",data[0]);
        gpos=pos;
        mLoadingIndicator=lb;
        new GetTranslate().execute(data[0]);
    }

    private class GetTranslate extends AsyncTask<String, Void, String>  {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            ipadd = getActivity().getResources().getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();
            String text="";
            url = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=trnsl.1.1.20181228T173001Z.af864d6422151745.6f7e0fb362469e981024303bba7718c4e4e05cf1&text="+strings[0]+"&lang=en-mr";
            headurl = "http://";
            Log.e("url",url);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e("ForumFragment_Main", "Response from url: " + jsonStr); //The Output of First Page

            if (jsonStr != null) {
                try {
                    JSONObject jsonObject= new JSONObject(jsonStr);
                    JSONArray jsonArray=jsonObject.getJSONArray("text");
                    text = jsonArray.getString(0);
                } catch (final JSONException e) {
                    Log.e("ForumFragment_Main", "Json parsing error: " + e.getMessage());
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
                Log.e("ForumFragment_Main", "Couldn't get json from server.");
            }

            return text;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            Log.e("Result",result);
            strArr[gpos][0]=result;
            mChatAdapter.setChatData(strArr,5);
        }

    }


}