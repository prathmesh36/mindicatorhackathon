package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.util.ImageUtil;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

public class TrainListAdapter extends RecyclerView.Adapter<TrainListAdapter.TrainListAdapterViewHolder> {

    private String[][] mTrainListData;
    private int mCount;

    //onClick Listener for each List Item
    private final TrainListAdapterOnClickHandler mClickHandler;

    //Interface for onClickListener Functionality
    public interface TrainListAdapterOnClickHandler {
        void onClick(String TrainListID);
    }

    //Constructor
    public TrainListAdapter(TrainListAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    //View Holder Class
    public class TrainListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTrainListTextView;
        public final TextView mTrainTimeTextView;
        public final TextView mTrainStatus;
        CircularProgressButton follow_cbp;

        public TrainListAdapterViewHolder(View view) {
            super(view);
            mTrainListTextView = (TextView) view.findViewById(R.id.list_item_sticky_header_social_text);
            mTrainTimeTextView=(TextView) view.findViewById(R.id.list_item_sticky_header_social_pdate);
            mTrainStatus=(TextView) view.findViewById(R.id.train_status);
            //Set onclick listener to the elements you want to make clickable
            view.setOnClickListener(this);
            }

        @Override
        public void onClick(View v) {
                //Opening the Profile of the user by passing user's id
                int adapterPosition = getAdapterPosition();
                String TrainListid = mTrainListData[adapterPosition][4];
                mClickHandler.onClick(TrainListid);
        }
    }

    @Override
    public TrainListAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflating the ListItem Layout to the Parent Layout thus creating ViewHolder
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.train_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrainListAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainListAdapterViewHolder holder, int position) {
        //Bind Data to the ViewHolder
        String src = mTrainListData[position][0];
        String dest=mTrainListData[position][1];
        String type=mTrainListData[position][3];
        String time=mTrainListData[position][2];
        String typestr;
        Resources res = holder.itemView.getContext().getResources();
        if(type.equals("0")) {
            typestr="F";
            holder.mTrainStatus.setBackgroundColor(res.getColor(R.color.material_red_700));
        }else{
            typestr="S";
        }

        holder.mTrainListTextView.setText(src+"-"+dest+" "+typestr);
        holder.mTrainTimeTextView.setText(time);


    }

    @Override
    public int getItemCount() {
        if (null == mTrainListData) return 0;
        return mCount;
    }

    public void setTrainListData(String[][] Data,int count) {
        mTrainListData = Data;
        mCount=count;
        notifyDataSetChanged();
    }

}
