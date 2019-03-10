/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.cfgprepapp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.TrainListActivity;
import com.example.android.cfgprepapp.adapter.ListAdapter;
import com.example.android.cfgprepapp.adapter.TrainListWholeAdapter;
import com.example.android.cfgprepapp.data.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;
import java.util.Calendar;

import static com.android.volley.VolleyLog.TAG;

public class TrainListFragment extends Fragment{

	String [][] TrainListData = new String[61][10];
	String ipadd,url,headurl;
	View rootViewG;
	String tstation;
	int count=0;
	int pos;
	public static TrainListFragment newInstance() {
		TrainListFragment f = new TrainListFragment();
		Bundle b = new Bundle();
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_provider, container,
				false);

		new GetTwitterTask().execute();
		rootViewG=rootView;
		return rootView;
	}

	private class GetTwitterTask extends AsyncTask<Void, Void, List<Status>> {
		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
			tstation="Matunga";
			ipadd = getString(R.string.ipadd);
			HttpHandler sh = new HttpHandler();
			url = "/CFGAPI/trainlistwhole.php?tstation="+tstation;
			headurl = "http://";
			url=headurl+ipadd+url;
			Log.e("url",url);

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(url);
			if (jsonStr != null) {
				try {
					JSONArray names = new JSONArray(jsonStr);
					for (int i = 0; i < names.length(); i++) {
						JSONObject c = names.getJSONObject(i);
						String src = c.getString("src");
						String dest=c.getString("dest");
						String time=c.getString("time");
						String type=c.getString("type");
						String id=c.getString("id");
						String rush1=c.getString("rush1");
						String rush2=c.getString("rush2");
						String rush3=c.getString("rush3");
						String rush4=c.getString("rush4");
						String currStation=c.getString("currstation");
						TrainListData[i][0]=src;
						TrainListData[i][1]=dest;
						TrainListData[i][2]=time;
						TrainListData[i][3]=type;
						TrainListData[i][4]=id;
						TrainListData[i][5]=rush1;
						TrainListData[i][6]=rush2;
						TrainListData[i][7]=rush3;
						TrainListData[i][8]=rush4;
						TrainListData[i][9]=currStation;
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
		protected void onPostExecute(List<twitter4j.Status> statuses) {
			ListView yourListView = (ListView) rootViewG.findViewById(R.id.list);
			TrainListWholeAdapter customAdapter = new TrainListWholeAdapter(getContext(), R.layout.activity_expandable_list_view_social,TrainListData);
			yourListView.setAdapter(customAdapter);
		}
	}

}