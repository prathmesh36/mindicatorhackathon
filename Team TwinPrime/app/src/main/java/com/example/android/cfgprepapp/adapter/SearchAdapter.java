package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.util.ImageUtil;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.UserListAdapterViewHolder>{
	
	private LayoutInflater mInflater;
	private ArrayList<Integer> mSearchedUser;

	String [][] mUserListData = new String[1000][3];

	//onClick Listener for each List Item
	public UserListAdapterOnClickHandler mClickHandler;

	//Interface for onClickListener Functionality
	public interface UserListAdapterOnClickHandler {
		void onClick(String UserListID, String UserListName);
	}

	public SearchAdapter(Context context,String userlist[][],UserListAdapterOnClickHandler clickHandler) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mUserListData=userlist;
		mClickHandler=clickHandler;
	}


	//View Holder Class
	public class UserListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		public final TextView mUserListTextView;
		public final ImageView mProfilePicImageView;
		public CircularProgressButton follow_cbp;

		public UserListAdapterViewHolder(View view) {
			super(view);
			mUserListTextView = (TextView) view.findViewById(R.id.text);
			mProfilePicImageView=(ImageView)view.findViewById(R.id.image);
			follow_cbp=(CircularProgressButton)view.findViewById(R.id.circular_progress_bar);

			//Set onclick listener to the elements you want to make clickable
			view.setOnClickListener(this);
			//follow_cbp.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
				//Opening the Profile of the user by passing user's id
			    Log.d("Msg","Clicked");
				int adapterPosition = getAdapterPosition();
				final int searchedUser = mSearchedUser.get(adapterPosition);
				mClickHandler.onClick(mUserListData[searchedUser][0],mUserListData[searchedUser][1]);
		}
	}

	@Override
	public UserListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		//Inflating the ListItem Layout to the Parent Layout thus creating ViewHolder
		Context context = parent.getContext();
		int layoutIdForListItem = R.layout.list_view_item_follow;
		LayoutInflater inflater = LayoutInflater.from(context);
		boolean shouldAttachToParentImmediately = false;
		View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

		return new SearchAdapter.UserListAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(SearchAdapter.UserListAdapterViewHolder holder, int position) {
		final int searchedUser = mSearchedUser.get(position);
		ImageUtil.displayRoundImage(
				holder.mProfilePicImageView,mUserListData[searchedUser][2],
				null);
		holder.mUserListTextView.setText(mUserListData[searchedUser][1]);
	}

	@Override
	public int getItemCount() {
		if (null == mSearchedUser) return 0;
		return mSearchedUser.size();
	}

	public void setUserListData( ArrayList<Integer> searcheduser) {
		mSearchedUser = searcheduser;
		notifyDataSetChanged();
	}
}
