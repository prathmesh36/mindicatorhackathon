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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.adapter.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterFragment extends Fragment{

	List<String> tdata;
	View rootViewG;
	public static TwitterFragment newInstance() {
		TwitterFragment f = new TwitterFragment();
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
		View rootView = inflater.inflate(R.layout.fragment_twitter, container,
				false);

		new GetTwitterTask().execute();
		rootViewG=rootView;
		Button tweetButton=(Button)rootView.findViewById(R.id.tweet);
		tweetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String message=" %23mindicatorRail";
				String url = "http://www.twitter.com/intent/tweet?text="+message;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});
		return rootView;
	}

	private class GetTwitterTask extends AsyncTask<Void, Void, List<Status>> {
		@Override
		protected List<twitter4j.Status> doInBackground(Void... params) {
			//Twitter API
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
					.setOAuthConsumerKey("Put Your Consumer Key")
					.setOAuthConsumerSecret("Put your Auth Consumer Secret")
					.setOAuthAccessToken("Put your Outh Access Token")
					.setOAuthAccessTokenSecret("Put your Outh Access Token Secret");
			TwitterFactory tf = new TwitterFactory(cb.build());
			Twitter twitter = tf.getInstance();

			//Printing Users Feed Tweet
			Twitter twitter1 = TwitterFactory.getSingleton();
			List<twitter4j.Status> statuses = null;
			try {
				statuses = twitter.getHomeTimeline();
			} catch (TwitterException e) {
				e.printStackTrace();
			}

			//Searching Tweets
            Query query = new Query("#mindicatorRail");
			query.setCount(50);
			tdata=new ArrayList<>();
            try {
                QueryResult result = twitter.search(query);
                for (twitter4j.Status status : result.getTweets()) {
                    tdata.add("@" + status.getUser().getScreenName() + ":" + status.getText());

                	//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }


            return statuses;
		}

		@Override
		protected void onPostExecute(List<twitter4j.Status> statuses) {
			/*System.out.println("Showing home timeline.");
			for (twitter4j.Status status : statuses) {
				System.out.println(status.getUser().getName() + ":" +
						status.getText());
			}*/

			ListView yourListView = (ListView) rootViewG.findViewById(R.id.list);
			ListAdapter customAdapter = new ListAdapter(getContext(), R.layout.list_view_item_follow,tdata);
			yourListView .setAdapter(customAdapter);
		}
	}

}