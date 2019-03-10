package com.example.android.cfgprepapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.adapter.ForumansAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.font.RobotoTextView;
import com.example.android.cfgprepapp.util.ImageUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ForumActivity_Detail extends AppCompatActivity implements ForumansAdapter.ForumansAdapterOnClickHandler{

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private ForumansAdapter mForumansAdapter;
    private String TAG = ForumActivity_Detail.class.getSimpleName();
    private ForumActivity_Detail.GetForumans mTask;
    // URL to get contacts JSON
    private  String url = "/CFGAPI/selectforumans.php?qid=24";
    private String headurl = "http://";
    String ipadd;
    String fdata[];
    String forumId;
    ArrayList<HashMap<String, String>> forumansList;
    String [][]forumansdet = new String[10][4];
    // TODO: Rename and change types of parameters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_detail);
        ipadd = getResources().getString(R.string.ipadd);
        Intent getintent = getIntent();
        fdata=getintent.getStringArrayExtra("fdata");
        Log.e("Recieved Data",fdata[1]);
        forumId=fdata[0];
        RobotoTextView header = findViewById(R.id.activity_forum_ans_label);
        header.setText(fdata[1]);
        RobotoTextView det = findViewById(R.id.activity_forum_ans_text);
        det.setText(fdata[6]);
        RobotoTextView uname = findViewById(R.id.activity_forum_ans_uname);
        uname.setText(fdata[5]);

        //Image Intialization
        ImageView uimage=findViewById(R.id.activity_forum_ans_uimage);
        ImageUtil.displayImage(uimage, "http://"+ipadd+"/CFGAPI/CFGApp/user.png", null);
        ImageView urimage=findViewById(R.id.activity_forum_ans_urimage);
        ImageUtil.displayImage(urimage, "http://"+ipadd+"/CFGAPI/CFGApp/user.png", null);
        ImageView coverimage = findViewById(R.id.activity_forum_ans_image);
        ImageUtil.displayImage(coverimage, "http://"+ipadd+"/CFGAPI/CFGApp/forumcover.jpg", null);


        forumansList = new ArrayList<>();
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_mess);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.loadbar);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);


        mForumansAdapter = new ForumansAdapter(this);
        mRecyclerView.setAdapter(mForumansAdapter);
        showForumansDataView();
        new GetForumans().execute("nostring");
        RobotoTextView commentboxEditTextButton= (RobotoTextView) findViewById(R.id.commentEditTextButton);
        commentboxEditTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText comment=(EditText)findViewById(R.id.commentEditText);
                String commentstring=comment.getText().toString();
                new AddCommentTask(commentstring).execute();
                comment.setText("");
            }
        }
        );

    }
    private void showForumansDataView() {
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
    @Override
    public void onClick(String data) {
        /*Intent intentnext = new Intent(ForumansportsActivity.this, com.sih2018.helpinghand.ForumansportActivity.class);
        intentnext.putExtra("did",data);
        startActivity(intentnext);*/
    }


    private class GetForumans extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... strings) {
            ipadd = getResources().getString(R.string.ipadd);
            HttpHandler sh = new HttpHandler();

            url = "/CFGAPI/selectforumans.php?qid="+fdata[0];
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
                    forumansList.clear();
                    // looping through All Contacts
                    for (int i = 0; i < names.length(); i++) {
                        JSONObject c = names.getJSONObject(i);
                        String id =c.getString("id");
                        String name = c.getString("name");
                        String  text = c.getString("text");
                        String pdate = c.getString("date");

                        // tmp hash map for single contact
                        HashMap<String, String> shelname = new HashMap<>();

                        // adding each child node to HashMap key => value\
                        shelname.put("name", name);
                        //Log.e("Names",contact.get("name").toString());
                        // adding contact to contact list

                        forumansList.add(shelname);
                        forumansdet[i][0]=id;
                        forumansdet[i][1]=name;
                        forumansdet[i][2]=text;
                        forumansdet[i][3]=pdate;
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
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
            showForumansDataView();
            /*ListAdapter adapter = new SimpleAdapter(
                    ForumanssActivity.this, forumansList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});

            lv.setAdapter(adapter);*/
            if(forumansList.size()>0) {
                HashMap<String, String> m = forumansList.get(0);
                String strArr[][] = new String[forumansList.size() * m.size()][4];
                for (int i=0;i<forumansList.size() * m.size();i++) {
                    strArr[i][0] = forumansdet[i][0];
                    strArr[i][1] = forumansdet[i][1];
                    strArr[i][2] = forumansdet[i][2];
                    strArr[i][3] = forumansdet[i][3];
                }
                mForumansAdapter.setForumansData(strArr);
            }else {
                showErrorMessage();
            }
        }

    }

    public class AddCommentTask extends AsyncTask<Void, Void, Boolean> {

        private final String mComment;
        String jsonStr;

        AddCommentTask(String comment) {
            mComment = comment;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpHandler sh = new HttpHandler();
            String ipadd = getResources().getString(R.string.ipadd);
            String url = "/CFGAPI/forumansadd.php";
            String headurl = "http://";
            url=headurl+ipadd+url;
            Log.e("url",url);

            //Creating a Hashmap for storing parameters for POST Method.
            HashMap<String, String> postparam = new HashMap<>();

            // adding each child node to HashMap key => value\
            postparam.put("comment", mComment);
            postparam.put("id",forumId);

            for (Map.Entry<String,String> entry : postparam.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Log.e(key,value);
            }

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCallPost(url,postparam);
            Log.e("Debugg(ForumAnsAct)", "Response from url: " + jsonStr); //The Output of First Page

            //JSON Parsing
            if (jsonStr != null) {
                JSONObject jsonObj = null;
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
                    //If Status Returned true then registration is successful
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

            //If Status returned false then registration error
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Toast.makeText(getApplicationContext(),
                        "Comment Added",
                        Toast.LENGTH_LONG)
                        .show();

                new GetForumans().execute("nostring");
                mRecyclerView.scrollToPosition(mForumansAdapter.getItemCount()-1);
            } else {
                Toast.makeText(getApplicationContext(),
                        "Comment Adding Unsuccessful",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }

    }
}
