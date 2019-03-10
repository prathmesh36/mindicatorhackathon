package com.example.android.cfgprepapp.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.adapter.ForumAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
//import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileTabsFragment_Forum extends Fragment implements ForumAdapter.ForumAdapterOnClickHandler,OnClickListener{

	private static final String ARG_POSITION = "position";

	private int position;
	private static final int INITIAL_DELAY_MILLIS = 300;
	private TextView mErrorMessageDisplay;
	private ProgressBar mLoadingIndicator;
	private RecyclerView mRecyclerView;
	private ForumAdapter mForumAdapter;

	// URL to get contacts JSON
	private  String url = "/CFGAPI/selectforum.php?uid=1";
	private String headurl = "http://";
	String ipadd;
	ArrayList<HashMap<String, String>> storeList;
	String [][]Forumdet = new String[100][5];
	
	public static ProfileTabsFragment_Forum newInstance(int position) {
		ProfileTabsFragment_Forum f = new ProfileTabsFragment_Forum();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


		View view=inflater.inflate(R.layout.fragment_tab_forum, container, false);
		storeList = new ArrayList<>();
		mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        /* This TextView is used to display errors and will be hidden if there are no errors */
		mErrorMessageDisplay = (TextView) view.findViewById(R.id.error_mess);
		mLoadingIndicator = (ProgressBar) view.findViewById(R.id.loadbar);
		LinearLayoutManager layoutManager
				= new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);

		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.setHasFixedSize(true);
		//mRecyclerView.setBackgroundResource(R.drawable.background_media);

		mForumAdapter = new ForumAdapter(this);
		mRecyclerView.setAdapter(mForumAdapter);
		showForumDataView();
		new GetForum().execute("nostring");
		return view;


	}

	private void showForumDataView() {
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
		/*Intent intentnext = new Intent(getActivity(), ForumportActivity.class);
		intentnext.putExtra("did",data);
		startActivity(intentnext);*/
	}


	private class GetForum extends AsyncTask<String, Void, Void>  {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingIndicator.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(String... strings) {
			ipadd = getActivity().getResources().getString(R.string.ipadd);
			HttpHandler sh = new HttpHandler();

			url = "/CFGAPI/selectforum.php?uid=1";
			headurl = "http://";
			url=headurl+ipadd+url;
			Log.e("url",url);
			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url);

			Log.e("ProfileTabsFragment_Forum", "Response from url: " + jsonStr); //The Output of First Page

			if (jsonStr != null) {
				try {

					// Getting JSON Array node
					JSONArray names = new JSONArray(jsonStr);
					storeList.clear();
					// looping through All Contacts
					for (int i = 0; i < names.length(); i++) {
						JSONObject c = names.getJSONObject(i);

						String name = c.getString("name");
						String id = c.getString("id");
						String date = c.getString("date");
						String reply = c.getString("reply");
						String view=c.getString("view");

						// tmp hash map for single contact
						HashMap<String, String> forumname = new HashMap<>();

						// adding each child node to HashMap key => value\
						forumname.put("name", name);
						//Log.e("Names",contact.get("name").toString());
						// adding contact to contact list

						storeList.add(forumname);
						Forumdet[i][0]=id;
						Forumdet[i][1]=name;
						Forumdet[i][2]=date;
						Forumdet[i][3]=view;
						Forumdet[i][4]=reply;
					}
				} catch (final JSONException e) {
					Log.e("ProfileTabsFragment_Forum", "Json parsing error: " + e.getMessage());
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
				Log.e("ProfileTabsFragment_Forum", "Couldn't get json from server.");
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);


			mLoadingIndicator.setVisibility(View.INVISIBLE);
			showForumDataView();
			if(storeList.size()>0) {
				HashMap<String, String> m = storeList.get(0);
				String strArr[][] = new String[storeList.size() * m.size()][5];
				for (int i=0;i<storeList.size() * m.size();i++) {
					strArr[i][0] = Forumdet[i][0];
					strArr[i][1] = Forumdet[i][1];
					strArr[i][2] = Forumdet[i][2];
					strArr[i][3] = Forumdet[i][3];
					strArr[i][4] = Forumdet[i][4];
				}
				mForumAdapter.setForumData(strArr);
			}else {
				showErrorMessage();
			}
		}

	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.fragment_tab_media_like:
			Toast.makeText(getActivity(), "Like media", Toast.LENGTH_SHORT).show();
			break;
		case R.id.fragment_tab_media_favorite:
			Toast.makeText(getActivity(), "Favorite media", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.fragment_tab_media_share:
			Toast.makeText(getActivity(), "Share media", Toast.LENGTH_SHORT).show();
			break;
		}
	}
	
	
}