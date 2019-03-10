package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.util.ImageUtil;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

import java.util.List;

public class TrainListWholeAdapter extends ArrayAdapter<String[]> {

    private int resourceLayout;
    private Context mContext;
    String[][] data;

    public TrainListWholeAdapter(Context context, int resource, String[][] items) {
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

        String src = data[position][0];
        String dest= data[position][1];
        String time=data[position][2];
        String type=data[position][3];
        String id=data[position][4];
        String rush1=data[position][5];
        String rush2=data[position][6];
        String rush3=data[position][7];
        String rush4=data[position][8];
        String currStation= data[position][9];

        String typestr;

        if(type.equals("0")) {
            typestr="F";
        }else{
            typestr="S";
        }

        TextView tt1 = (TextView) v.findViewById(R.id.train_name);
        tt1.setText(time+" "+src+"-"+dest+" "+typestr);

        TextView tt2=(TextView)v.findViewById(R.id.status);
        ImageView iv1=(ImageView)v.findViewById(R.id.rush);
        if(currStation.equals((""))){
            currStation="N.A";
        }
        tt2.setText("Live Status: "+currStation);

        int max_rush=max(Integer.parseInt(rush1),Integer.parseInt(rush2),Integer.parseInt(rush3),Integer.parseInt(rush4));
        ImageView img_back=(ImageView)v.findViewById(R.id.image_back);
        if(max_rush==1)
        {
            tt2.setTextColor(getContext().getResources().getColor(R.color.cpb_green));
            Drawable drawable=getContext().getResources().getDrawable(R.drawable.red);
            iv1.setBackground(drawable);
        }else if(max_rush==2){
            tt2.setTextColor(getContext().getResources().getColor(R.color.cpb_green));
            Drawable drawable=getContext().getResources().getDrawable(R.drawable.orange);
            iv1.setBackground(drawable);
        }else if(max_rush==3){
            Drawable drawable=getContext().getResources().getDrawable(R.drawable.yellow);
            iv1.setBackground(drawable);
            tt2.setTextColor(getContext().getResources().getColor(R.color.cpb_red));

        }else{
            Drawable drawable=getContext().getResources().getDrawable(R.drawable.green);
            iv1.setBackground(drawable);
            tt2.setTextColor(getContext().getResources().getColor(R.color.cpb_red));

        }
        ImageView logo=(ImageView)v.findViewById(R.id.image);
        return v;
    }

    public static int max(int a, int b, int c, int d) {

        int max = a;
        int pos=1;
        if (b > max) {
            max = b;
            pos=2;
        }
        if (c > max) {
            max = c;
            pos=3;
        }
        if (d > max) {
            max = d;
            pos=4;
        }
        return pos;
    }

}
