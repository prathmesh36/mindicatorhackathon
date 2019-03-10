package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.util.ImageUtil;


public class MainForumAdapter extends RecyclerView.Adapter<MainForumAdapter.MainForumAdapterViewHolder> {
    private String[][] mMainForumData;


    private final MainForumAdapter.MainForumAdapterOnClickHandler mClickHandler;

    public interface MainForumAdapterOnClickHandler {
        void onClick(String data[]);
    }

    public MainForumAdapter(MainForumAdapter.MainForumAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class MainForumAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ViewHolder holder;
        public MainForumAdapterViewHolder(View convertView) {
            super(convertView);
            holder=new ViewHolder();
            holder.uimage = (ImageView) convertView
                    .findViewById(R.id.list_item_google_cards_social_uimage);
            holder.image = (ImageView) convertView
                    .findViewById(R.id.list_item_google_cards_social_image);
            holder.forumname = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_social_forum_name);
            holder.reply = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_social_reply);
            holder.pdate = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_social_pdate);
            holder.uview = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_social_uview);
            holder.uname = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_social_uname);
            holder.text = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_social_text);
            holder.forumname.setOnClickListener(this);
            convertView.setTag(holder);
        }


        @Override
        public void onClick(View v) {
            //Toast.makeText(v.getContext(), "Text " , Toast.LENGTH_SHORT).show();
                int adapterPosition = getAdapterPosition();
                String forumid[] = mMainForumData[adapterPosition];
                mClickHandler.onClick(forumid);
            /*int position = (Integer) v.getTag();
            switch (v.getId()) {
                case R.id.list_item_google_cards_media_like:
                    // click on share button
                    Toast.makeText(v.getContext(), "Like " + position, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.list_item_google_cards_media_favorite:
                    // click on share button
                    Toast.makeText(v.getContext(), "Favorite " + position, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.list_item_google_cards_media_share:
                    // click on share button
                    Toast.makeText(v.getContext(), "Share " + position, Toast.LENGTH_SHORT).show();
                    break;
            }*/
        }
    }

    private static class ViewHolder {
        public ImageView image;
        public ImageView uimage;
        public TextView forumname;
        public TextView uname;
        public TextView reply;
        public TextView pdate;
        public TextView uview;
        public TextView text;
    }

    @Override
    public MainForumAdapter.MainForumAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_google_cards_mainforum;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MainForumAdapter.MainForumAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MainForumAdapter.MainForumAdapterViewHolder MainForumAdapterViewHolder, int position) {
        String forumname = mMainForumData[position][1];
        String reply = mMainForumData[position][4];
        String pdate = mMainForumData[position][2];
        String uview = mMainForumData[position][3];
        String uname = mMainForumData[position][5];
        String forumtext = mMainForumData[position][6];
        String ipadd = MainForumAdapterViewHolder.itemView.getResources().getString(R.string.ipadd);

        //Log.e("Debug",transname+" "+transsname+" "+transdname);
        MainForumAdapterViewHolder.holder.forumname.setText(forumname);
        MainForumAdapterViewHolder.holder.reply.setText(reply);
        MainForumAdapterViewHolder.holder.uview.setText(uview);
        MainForumAdapterViewHolder.holder.pdate.setText(pdate);
        MainForumAdapterViewHolder.holder.uname.setText(uname);
        MainForumAdapterViewHolder.holder.text.setText(forumtext);
        //MainForumAdapterViewHolder.holder.like.setTag(position);
        ImageUtil.displayImage(MainForumAdapterViewHolder.holder.image, "http://"+ipadd+"/CFGAPI/CFGApp/Forum%20Logo.png", null);
        ImageUtil.displayImage(MainForumAdapterViewHolder.holder.uimage, "http://"+ipadd+"/CFGAPI/CFGApp/user.png", null);
    }


    @Override
    public int getItemCount() {
        if (null == mMainForumData) return 0;
        return mMainForumData.length;
    }


    public void setMainForumData(String[][] MainForumData) {
        mMainForumData = MainForumData;
        Log.e("NotifyDataSetChanged","Yes");
        notifyDataSetChanged();
    }
}
