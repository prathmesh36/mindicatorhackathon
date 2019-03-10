package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.util.ImageUtil;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends ArrayAdapter<String> {

    private int resourceLayout;
    private Context mContext;
    List<String> data;

    public ListAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        data=items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            v = vi.inflate(resourceLayout, null);
        }

        String p = data.get(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.text);
            if (tt1 != null) {
                tt1.setText(p);
            }
        }
        ImageView logo=(ImageView)v.findViewById(R.id.image);
        ImageUtil.displayImage(
                 logo,"https://lh6.googleusercontent.com/proxy/Q5Lnw1wPJMXpZnrpDYD4325b_yZy-3PEK-upvQEqkuMIdD1w8g5r8n1Y90lgCxE7DmUlYPZKMl5pyN-YvRXxVM3BagXl_G9p03dNnSMRtDL78kyNkBWkYA=w384-h384-nc",
                null);
        CircularProgressButton button=(CircularProgressButton)v.findViewById(R.id.circular_progress_bar);
        button.setText("Read More");
        button.setBackgroundColor(getContext().getResources().getColor(R.color.cpb_blue));
        return v;
    }

}
