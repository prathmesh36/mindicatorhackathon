package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.location.GPSTracker;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

public class ShelterListAdapter extends RecyclerView.Adapter<ShelterListAdapter.TrainListAdapterViewHolder> {

    private String[][] mTrainListData;
    private int mCount;
    Context contextG;
    String stringLatitude="0";
    String stringLongitude="0";
    String mobileno="8452961300";

    //Interface for onClickListener Functionality
    public interface ShelterListAdapterOnClickHandler {
        void onClick(String TrainListID);
    }

    //Constructor
    public ShelterListAdapter(Context context) {
        contextG=context;
    }

    //View Holder Class
    public class TrainListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTrainListTextView;
        public final TextView mTrainStatus;
        public final ImageView mDirection;
        public final ImageView mCall;
        CircularProgressButton follow_cbp;

        public TrainListAdapterViewHolder(View view) {
            super(view);
            mTrainListTextView = (TextView) view.findViewById(R.id.tv_shelter_data);
            mTrainStatus=(TextView) view.findViewById(R.id.train_status);
            mDirection=(ImageView)view.findViewById(R.id.direction);
            //Set onclick listener to the elements you want to make clickable
            mDirection.setOnClickListener(this);
            mCall=(ImageView)view.findViewById(R.id.contact);
            mCall.setOnClickListener(this);
            }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //Opening the Profile of the user by passing user's id
                case R.id.direction:
                    int adapterPosition = getAdapterPosition();
                    String lati = mTrainListData[adapterPosition][2];
                    String longi = mTrainListData[adapterPosition][3];
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?saddr=" + stringLatitude + "," + stringLongitude + "&daddr=" + lati + "," + longi));
                    contextG.startActivity(intent);
                    break;
                case R.id.contact:
                    Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "Your Phone_number"));
                    contextG.startActivity(intent1);
            }
        }
    }

    @Override
    public TrainListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflating the ListItem Layout to the Parent Layout thus creating ViewHolder
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_view_nearshelter2;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrainListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainListAdapterViewHolder holder, int position) {
        //Bind Data to the ViewHolder
        String name = mTrainListData[position][0];
        String facility=mTrainListData[position][1];
        String lati=mTrainListData[position][2];
        String longi=mTrainListData[position][3];
        String contact=mTrainListData[position][4];
        String typestr;
        Resources res = holder.itemView.getContext().getResources();
        holder.mTrainListTextView.setText(name);
    }

    @Override
    public int getItemCount() {
        if (null == mTrainListData) return 0;
        return mCount;
    }

    public void setTrainListData(String[][] Data,int count,String lati, String longi) {
        mTrainListData = Data;
        mCount=count;
        notifyDataSetChanged();
        stringLatitude=lati;
        stringLongitude=longi;
    }

}
