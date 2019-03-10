package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.util.ImageUtil;


public class ForumAdapter extends RecyclerView.Adapter<ForumAdapter.ForumAdapterViewHolder> {
    private String[][] mForumData;


    private final ForumAdapter.ForumAdapterOnClickHandler mClickHandler;

    public interface ForumAdapterOnClickHandler {
        void onClick(String data);
    }

    public ForumAdapter(ForumAdapter.ForumAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ForumAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ViewHolder holder;
        public ForumAdapterViewHolder(View convertView) {
            super(convertView);
            holder=new ViewHolder();
            holder.image = (ImageView) convertView
                    .findViewById(R.id.list_item_google_cards_media_image);
            holder.Forumname = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_media_forum_name);
            holder.reply = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_media_reply);
            holder.pdate = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_media_pdate);
            holder.uview = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_media_uview);
            holder.like = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_media_like);
            holder.favorite = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_media_favorite);
            holder.share = (TextView) convertView
                    .findViewById(R.id.list_item_google_cards_media_share);
            holder.like.setOnClickListener(this);
            holder.favorite.setOnClickListener(this);
            holder.share.setOnClickListener(this);
            convertView.setTag(holder);
        }


        @Override
        public void onClick(View v) {
            //Toast.makeText(v.getContext(), "Text " , Toast.LENGTH_SHORT).show();
            int position = (Integer) v.getTag();
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
            }
        }
    }

    private static class ViewHolder {
        public ImageView image;
        public TextView Forumname;
        public TextView reply;
        public TextView pdate;
        public TextView uview;
        public TextView like;
        public TextView favorite;
        public TextView share;
    }

    @Override
    public ForumAdapter.ForumAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_google_cards_tabforum;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ForumAdapter.ForumAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ForumAdapter.ForumAdapterViewHolder ForumAdapterViewHolder, int position) {
        String Forumname = mForumData[position][1];
        String reply = mForumData[position][4];
        String pdate = mForumData[position][2];
        String uview = mForumData[position][3];
        String ipadd = ForumAdapterViewHolder.itemView.getResources().getString(R.string.ipadd);

        //Log.e("Debug",transname+" "+transsname+" "+transdname);
        ForumAdapterViewHolder.holder.Forumname.setText(Forumname);
        ForumAdapterViewHolder.holder.reply.setText(reply);
        ForumAdapterViewHolder.holder.uview.setText(uview);
        ForumAdapterViewHolder.holder.pdate.setText(pdate);
        ForumAdapterViewHolder.holder.like.setTag(position);
        ForumAdapterViewHolder.holder.favorite.setTag(position);
        ForumAdapterViewHolder.holder.share.setTag(position);
        ImageUtil.displayImage(ForumAdapterViewHolder.holder.image, "http://"+ipadd+"/CFGAPI/CFGApp/Forum%20Logo.png", null);
    }


    @Override
    public int getItemCount() {
        if (null == mForumData) return 0;
        return mForumData.length;
    }


    public void setForumData(String[][] ForumData) {
        mForumData = ForumData;
        notifyDataSetChanged();
    }
}
