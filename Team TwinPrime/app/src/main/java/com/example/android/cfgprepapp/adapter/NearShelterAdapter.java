package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.cfgprepapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prathmeshmhapsekar on 20/03/18.
 */

public class NearShelterAdapter extends RecyclerView.Adapter<NearShelterAdapter.NearShelterAdapterViewHolder> {

    private String[] mShelterData;
    private JSONArray mBusData[];


    private final NearShelterAdapter.NearShelterAdapterOnClickHandler mClickHandler;

    public interface NearShelterAdapterOnClickHandler {
        void onClick(String shelterdata,String[] busno);
    }

    public NearShelterAdapter(NearShelterAdapter.NearShelterAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class NearShelterAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mShelterTextView;
        public final ImageView mShelterImageView;

        public NearShelterAdapterViewHolder(View view) {
            super(view);
            mShelterTextView = (TextView) view.findViewById(R.id.tv_shelter_data);
            mShelterImageView = (ImageView) view.findViewById(R.id.direction);

            mShelterImageView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String shelterdata = mShelterData[adapterPosition];
            JSONArray buses = mBusData[adapterPosition];
            String name[] = new String[buses.length()];
            for (int i = 0; i < buses.length(); i++) {
                JSONObject c = null;
                try {
                    c = buses.getJSONObject(i);
                    name[i] = c.getString("name")+"/"+c.getString("dir");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            mClickHandler.onClick(shelterdata,name);
        }
    }


    @Override
    public NearShelterAdapter.NearShelterAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_view_nearshelter;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new NearShelterAdapter.NearShelterAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(NearShelterAdapter.NearShelterAdapterViewHolder nearShelterAdapterViewHolder, int position) {
        String shelterdata = mShelterData[position];
        nearShelterAdapterViewHolder.mShelterTextView.setText(shelterdata);
    }


    @Override
    public int getItemCount() {
        if (null == mShelterData) return 0;
        return mShelterData.length;
    }


    public void setShelterData(String[] shelterData, JSONArray busData[]) {
        mShelterData = shelterData;
        mBusData=busData;
        notifyDataSetChanged();
    }
}
