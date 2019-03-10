package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

public class BusListAdapter extends RecyclerView.Adapter<BusListAdapter.BusListAdapterViewHolder> {

    private String[] mBusListData;
    private int mCount;

    //onClick Listener for each List Item
    private final BusListAdapterOnClickHandler mClickHandler;

    //Interface for onClickListener Functionality
    public interface BusListAdapterOnClickHandler {
        void onClick(String BusListID, String BusListName);
    }

    //Constructor
    public BusListAdapter(BusListAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    //View Holder Class
    public class BusListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mBusListTextView;
        public final TextView mBusDirTextView;
        CircularProgressButton follow_cbp;

        public BusListAdapterViewHolder(View view) {
            super(view);
            mBusListTextView = (TextView) view.findViewById(R.id.list_item_sticky_header_social_text);
            mBusDirTextView = (TextView) view.findViewById(R.id.list_item_sticky_header_social_pdate);
            //Set onclick listener to the elements you want to make clickable
            view.setOnClickListener(this);
            }

        @Override
        public void onClick(View v) {
                //Opening the Profile of the user by passing user's id
                /*int adapterPosition = getAdapterPosition();
                String BusListid = mBusListData[adapterPosition][0];
                String BusListName=mBusListData[adapterPosition][1];
                mClickHandler.onClick(BusListid, BusListName);*/
        }
    }

    @Override
    public BusListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflating the ListItem Layout to the Parent Layout thus creating ViewHolder
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.bus_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new BusListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BusListAdapterViewHolder holder, int position) {
        //Bind Data to the ViewHolder
        String busno[] = mBusListData[position].split("/");
        Log.e("Bus",busno[0]);
        holder.mBusListTextView.setText(busno[0]);
        holder.mBusDirTextView.setText(busno[1]);
    }

    @Override
    public int getItemCount() {
        if (null == mBusListData) return 0;
        return mCount;
    }

    public void setBusListData(String[] Data,int count) {
        mBusListData = Data;
        mCount=count;
        notifyDataSetChanged();
    }

}
