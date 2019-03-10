package com.example.android.cfgprepapp.fragment;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.util.ImageUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfileTabsFragment_UserDetail extends Fragment implements OnClickListener {

	//ProfileTask Variable
	private ProfileTask mProfileTask = null;

	private static final String ARG_POSITION = "position";

	private TextView mLike;
	private TextView mFavorite;
	private TextView mShare;

	private int position;

	SharedPreferences pref;
	static String profile_user_id;


	public static ProfileTabsFragment_UserDetail newInstance(int position) {

		profile_user_id="0";
		ProfileTabsFragment_UserDetail f = new ProfileTabsFragment_UserDetail();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	public static ProfileTabsFragment_UserDetail newInstance(int position, String user_id) {
		//Setting the profile id passed by the previous fragment
		profile_user_id=user_id;

		ProfileTabsFragment_UserDetail f = new ProfileTabsFragment_UserDetail();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);

		//Getting SharedPreference
		pref = getActivity().getSharedPreferences("LoginSession", MODE_PRIVATE);

	}

	@Override
	public void onStop() {
		super.onStop();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tab_user_detail,
				container, false);
		mLike = (TextView) rootView
				.findViewById(R.id.fragment_tab_media_like);
		mFavorite = (TextView) rootView
				.findViewById(R.id.fragment_tab_media_favorite);
		mShare = (TextView) rootView
				.findViewById(R.id.fragment_tab_media_share);
		ViewCompat.setElevation(rootView, 50);
		TextView mName=(TextView) rootView.findViewById(R.id.fragment_tab_media_artist_name);
		TextView mGender=(TextView) rootView.findViewById(R.id.fragment_tab_media_gender);
		TextView mAge=(TextView) rootView.findViewById(R.id.fragment_tab_media_age);
		TextView mUsername =(TextView) rootView.findViewById(R.id.fragment_tab_media_place);

		mName.setText(pref.getString("name","Joe Smith"));
		mAge.setText(pref.getString("age","20"));
		mGender.setText(pref.getString("gender","Male"));
		mUsername.setText(pref.getString("username","joesmith100"));

		mLike.setOnClickListener(this);
		mFavorite.setOnClickListener(this);
		mShare.setOnClickListener(this);

		if(profile_user_id=="0")
		{
			profile_user_id=pref.getString("userID","1");
		}

		//Starting a Async Task
		mProfileTask = new ProfileTask(profile_user_id);
		mProfileTask.execute((Void) null);

		return rootView;
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

	/**
	 * Represents an asynchronous Data Fetching task used to get data about
	 * the user.
	 */
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
				if(resp.equals("1"))
				{
					//If Status Returned true then data is fetched
					return true;
				}

			} else {
				Log.e("Error", "Couldn't get json from server.");
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(getActivity(),
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
					TextView tvName = getActivity().findViewById(R.id.fragment_tab_media_artist_name);
					tvName.setText(proData.getString("name"));
					TextView tvEmail=getActivity().findViewById(R.id.fragment_tab_media_place);
					tvEmail.setText("Mumbai");
					TextView tvGender=getActivity().findViewById(R.id.fragment_tab_media_gender);
					tvGender.setText("Gender: "+proData.getString("gender"));
					TextView tvAge=getActivity().findViewById(R.id.fragment_tab_media_age);
					tvAge.setText("Age: "+proData.getString("age"));
                    ImageView image_profile_pic = (ImageView) getActivity().findViewById(R.id.fragment_tab_media_image);
                    Log.e("URL",proData.getString("profile_pic_url"));
                    ImageUtil.displayImage(
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