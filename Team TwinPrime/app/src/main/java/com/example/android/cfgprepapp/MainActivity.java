package com.example.android.cfgprepapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.adapter.DrawerAdapter;

import com.example.android.cfgprepapp.fragment.ChatFragment;
import com.example.android.cfgprepapp.fragment.ForumFragment_Main;
import com.example.android.cfgprepapp.fragment.ImageCaptureIntentFragment;
import com.example.android.cfgprepapp.fragment.ContentProviderFragment_Contacts;
import com.example.android.cfgprepapp.fragment.SearchFragment;
import com.example.android.cfgprepapp.fragment.SheltersFragment;
import com.example.android.cfgprepapp.fragment.ProfileTabsFragment;

import com.example.android.cfgprepapp.fragment.TrainListFragment;
import com.example.android.cfgprepapp.fragment.TwitterFragment;
import com.example.android.cfgprepapp.fragment.VolunteerFragment;
import com.example.android.cfgprepapp.model.DrawerItem;
import com.example.android.cfgprepapp.sync.CallJobService;
import com.example.android.cfgprepapp.util.ImageUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	public static final int REQUEST_CODE = 1;
	private ListView mDrawerList;
	private List<DrawerItem> mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	private Handler mHandler;
	private static final int SUBSCRIBE_VIEW = 3;


	private boolean mShouldFinish = false;

	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Getting SharedPreference
		pref = getSharedPreferences("LoginSession", MODE_PRIVATE);
		Toast.makeText(this,pref.getString("name","User Name"),Toast.LENGTH_LONG).show();
		//Log.d("Msg",pref.getString("name","Username"));


		//Notification Service Call
		CallJobService.scheduleReminderNotification(this);
		//Code for Universal_Image_Loader_Library Configuration
		ImageLoader imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited()) {
			imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		}

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		//Drawer Toggle Code
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
				R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.addDrawerListener(mDrawerToggle);

		mTitle = mDrawerTitle = getTitle();
		mDrawerList = (ListView) findViewById(R.id.list_view);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		//Add all the Navbar Items in the DrawerItem Array
		prepareNavigationDrawerItems();
		View headerView = getLayoutInflater().inflate(
				R.layout.header_navigation_drawer_social, mDrawerList, false);


		ImageView iv = (ImageView) headerView.findViewById(R.id.image);
		TextView headerName =(TextView) headerView.findViewById(R.id.header_name);
		TextView headerUsername=(TextView)headerView.findViewById(R.id.header_username);

		//Drawer Header Image and Text Settting
		ImageUtil.displayRoundImage(iv,
				"http://pengaja.com/uiapptemplate/newphotos/profileimages/0.jpg", null);
		headerName.setText(pref.getString("name","Joe Smith"));
		headerUsername.setText(pref.getString("username","joesmith100"));

		//Set Header to Drawer
		mDrawerList.addHeaderView(headerView);

		//Use to Display all the DrawerItem Array Elements on the NavBar Drawer
		mDrawerList.setAdapter(new DrawerAdapter(this, mDrawerItems, true));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerLayout.addDrawerListener(mDrawerToggle);

		mHandler = new Handler();

		if (savedInstanceState == null) {
			int position = 0;
			selectItem(position, mDrawerItems.get(position).getTag());
			//mDrawerLayout.openDrawer(mDrawerList);
		}

	}

	//Result of method StartActivityonResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Log.e("Test",String.valueOf(requestCode)+" "+String.valueOf(resultCode));
		try
		{
			super.onActivityResult(requestCode, resultCode, data);

			if (requestCode == REQUEST_CODE   && resultCode  == RESULT_OK)
			{

				String requiredValue = data.getStringExtra("Activity");
				if(requiredValue.equals("ForumAdd"))
				{
					Fragment currentFragment = ForumFragment_Main.newInstance(1);
					commitFragment(currentFragment);
				}
			}
		}
		catch (Exception ex)
		{
			Toast.makeText(this, ex.toString(),
					Toast.LENGTH_SHORT).show();
		}
	}


	@Override
	public void onBackPressed() {
		if (!mShouldFinish && !mDrawerLayout.isDrawerOpen(mDrawerList)) {
			Toast.makeText(getApplicationContext(), R.string.confirm_exit,
					Toast.LENGTH_SHORT).show();
			mShouldFinish = true;
			mDrawerLayout.openDrawer(mDrawerList);
		} else if (!mShouldFinish && mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			super.onBackPressed();
		}
    }


	private void prepareNavigationDrawerItems() {
		mDrawerItems = new ArrayList<>();
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_list_views,
				R.string.drawer_title_list_views,
				DrawerItem.DRAWER_ITEM_TAG_LIST_VIEWS));
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_progress_bars,
				R.string.drawer_title_parallax,
				DrawerItem.DRAWER_ITEM_TAG_PARALLAX));
		/*mDrawerItems.add(new DrawerItem(R.string.drawer_icon_left_menus,
				R.string.drawer_title_left_menus,
				DrawerItem.DRAWER_ITEM_TAG_LEFT_MENUS));*/
		/*mDrawerItems.add(new DrawerItem(R.string.drawer_icon_login_page,
				R.string.drawer_title_login_page,
				DrawerItem.DRAWER_ITEM_TAG_LOGIN_PAGE_AND_LOADERS));*/
		/*mDrawerItems.add(new DrawerItem(R.string.drawer_icon_image_gallery,
				R.string.drawer_title_image_gallery,
				DrawerItem.DRAWER_ITEM_TAG_IMAGE_GALLERY));*/
		/*mDrawerItems.add(new DrawerItem(R.string.drawer_icon_shape_image_views,
				R.string.drawer_title_shape_image_views,
				DrawerItem.DRAWER_ITEM_TAG_SHAPE_IMAGE_VIEWS));*/
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_parallax,
				R.string.drawer_title_progress_bars,
				DrawerItem.DRAWER_ITEM_TAG_PROGRESS_BARS));
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_check_and_radio_buttons,
				R.string.drawer_title_check_and_radio_buttons,
				DrawerItem.DRAWER_ITEM_TAG_CHECK_AND_RADIO_BOXES));
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_splash_screens,
				R.string.drawer_title_splash_screens,
				DrawerItem.DRAWER_ITEM_TAG_SPLASH_SCREENS));
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_search_bars,
				R.string.drawer_title_search_bars,
				DrawerItem.DRAWER_ITEM_TAG_SEARCH_BARS));
		/*mDrawerItems.add(new DrawerItem(R.string.drawer_icon_text_views,
				R.string.drawer_title_text_views,
				DrawerItem.DRAWER_ITEM_TAG_TEXT_VIEWS));*/
		/*mDrawerItems.add(new DrawerItem(R.string.drawer_icon_dialogs,
				R.string.drawer_title_dialogs,
				DrawerItem.DRAWER_ITEM_TAG_DIALOGS));*/
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_tabs,
				R.string.drawer_title_tabs,
				DrawerItem.DRAWER_ITEM_TAG_TABS));
		mDrawerItems.add(new DrawerItem(R.string.drawer_icon_wizards, R.string.drawer_title_wizards,
				DrawerItem.DRAWER_ITEM_TAG_WIZARDS));
	}


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(MainActivity.this,SettingActivity.class);
			startActivity(intent);

		}
		if(id == R.id.action_logout){

		}
		if(id==R.id.notifications)
		{
			Intent intent = new Intent(MainActivity.this,UserListActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}


	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
			selectItem(position-1, mDrawerItems.get(position-1).getTag());
		}
	}

	private void selectItem(int position, int drawerTag) {
		Fragment fragment = getFragmentByDrawerTag(drawerTag);
		commitFragment(fragment);
		mDrawerList.setItemChecked(position+1, true);
		setTitle(mDrawerItems.get(position).getTitle());
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	private Fragment getFragmentByDrawerTag(int drawerTag) {
		Fragment fragment;
		/*if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_SPLASH_SCREENS) {
			fragment = SplashScreensFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_PROGRESS_BARS) {
			fragment = ProgressBarsFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_SHAPE_IMAGE_VIEWS) {
			fragment = ShapeImageViewsFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_TEXT_VIEWS) {
			fragment = TextViewsFragment.newInstance();
		}  else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_LOGIN_PAGE_AND_LOADERS) {
			fragment = LogInPageFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_IMAGE_GALLERY) {
			fragment = ImageGalleryFragment.newInstance();
		}else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_LEFT_MENUS) {
			fragment = LeftMenusFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_LIST_VIEWS) {
			fragment = ListViewsFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_PARALLAX) {
			fragment = ParallaxEffectsFragment.newInstance();
		}
		 else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_CHECK_AND_RADIO_BOXES) {
			fragment = CheckAndRadioBoxesFragment.newInstance();
		} else */if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_LIST_VIEWS) {
			fragment = TrainListFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_PARALLAX) {
			fragment = TwitterFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_PROGRESS_BARS) {
			fragment = VolunteerFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_SPLASH_SCREENS) {
			fragment = SheltersFragment.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_CHECK_AND_RADIO_BOXES) {
			fragment = ChatFragment.newInstance();
		}
		/*else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_TEXT_VIEWS) {
			fragment = ImageCaptureIntentFragment.newInstance();
		}else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_DIALOGS) {
			fragment = ContentProviderFragment_Contacts.newInstance();
		}*/ else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_WIZARDS) {
			fragment = ForumFragment_Main.newInstance();
		} else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_SEARCH_BARS) {
			fragment = SearchFragment.newInstance();
		}else if (drawerTag == DrawerItem.DRAWER_ITEM_TAG_TABS) {
			fragment = ProfileTabsFragment.newInstance();
		} else {
			fragment = new Fragment();
		}
		mShouldFinish = false;
		return fragment;
	}

	private class CommitFragmentRunnable implements Runnable {
		private Fragment fragment;
		public CommitFragmentRunnable(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void run() {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();
		}
	}

	public void commitFragment(Fragment fragment) {
		// Using Handler class to avoid lagging while
		// committing fragment in same time as closing
		// navigation drawer
		mHandler.post(new CommitFragmentRunnable(fragment));
	}

	@Override
	public void setTitle(int titleId) {
		setTitle(getString(titleId));
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}