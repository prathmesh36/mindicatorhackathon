package com.example.android.cfgprepapp.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.notifyutilities.NotificationUtils;
import com.example.android.cfgprepapp.util.ImageUtil;
import com.example.android.cfgprepapp.view.PagerSlidingTabStrip;


import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ProfileTabsFragment extends Fragment implements View.OnClickListener {

	public static final String TABS_MEDIA = "Media tabs";
	public static final String TABS_SHOP = "Shop tabs";
	public static final String TABS_SOCIAL = "Social tabs";

	private ListView mListView;
	private List<String> mTabs;
	private MyPagerAdapter adapter;
	private LinearLayout toolbar;
	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private TextView toolbarLike;
	private TextView toolbarFavorite;
	private TextView toolbarShare;
	private TextView toolbarName;
	private TextView toolbarUsername;

	SharedPreferences pref;

	public static ProfileTabsFragment newInstance() {
		return new ProfileTabsFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Getting SharedPreference
		pref = getActivity().getSharedPreferences("LoginSession", MODE_PRIVATE);
		mTabs = new ArrayList<String>();
		mTabs.add(TABS_MEDIA);
		mTabs.add(TABS_SOCIAL);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		final View rootView = inflater.inflate(R.layout.fragment_tabs,
				container, false);

		//Get Every element of the layout in the variable
		toolbar = (LinearLayout) rootView.findViewById(R.id.toolbar_tab_social);
		ImageView toolbarImage = (ImageView) toolbar.findViewById(R.id.toolbar_tab_social_image);
		toolbarLike = (TextView) toolbar.findViewById(R.id.toolbar_tab_social_like);
		toolbarFavorite = (TextView) toolbar.findViewById(R.id.toolbar_tab_social_favorite);
		toolbarShare = (TextView) toolbar.findViewById(R.id.toolbar_tab_social_share);
		toolbarName =(TextView) toolbar.findViewById(R.id.toolbar_tab_social_name);
		toolbarUsername=(TextView)toolbar.findViewById(R.id.toolbar_tab_social_place);

		//Setting variable
		toolbarName.setText(pref.getString("name","Joe Smith"));
		toolbarUsername.setText(pref.getString("username","joesmith100"));

		//Setting Image
		ImageUtil.displayRoundImage(toolbarImage, "http://pengaja.com/uiapptemplate/newphotos/profileimages/0.jpg", null);

		//Setting Listener
		toolbarLike.setOnClickListener(this);
		toolbarFavorite.setOnClickListener(this);
		toolbarShare.setOnClickListener(this);

		//Get the tab layout and pager in the variable below
		tabs = (PagerSlidingTabStrip) rootView.findViewById(R.id.activity_tab_social_tabs);
		pager = (ViewPager) rootView.findViewById(R.id.activity_tab_social_pager);

		//create a adapter and set it to pager
		adapter = new ProfileTabsFragment.MyPagerAdapter(getActivity().getSupportFragmentManager());
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
				Toast.makeText(getActivity(), "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
			}
		});

		return rootView;
	}


	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final ArrayList<String> tabNames = new ArrayList<String>() {{
			add("Forum");
			add("Details");
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

		@Override
		public Fragment getItem(int position) {
			if(position == 0) {
				return ProfileTabsFragment_Forum.newInstance(position);
			}
			else{
				return ProfileTabsFragment_UserDetail.newInstance(position);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.toolbar_tab_social_like:
				Toast.makeText(getActivity(), "Notification Like", Toast.LENGTH_SHORT).show();
				testNotification(v);
				break;
			case R.id.toolbar_tab_social_favorite:
				Toast.makeText(getActivity(), "Favorite", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.toolbar_tab_social_share:
				Toast.makeText(getActivity(), "Share", Toast.LENGTH_SHORT).show();
				//Code for Social Media Sharing
				String message="Hii Sharing via twitter intent";
				String url = "http://www.twitter.com/intent/tweet?text="+message;
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				break;
		}
	}
	public void testNotification(View view) {
		NotificationUtils.remindUserNotification(getActivity());
	}
}
