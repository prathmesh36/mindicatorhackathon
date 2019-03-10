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


public class ForumansAdapter extends RecyclerView.Adapter<ForumansAdapter.ForumansAdapterViewHolder> {
    private String[][] mForumansData;


    private final ForumansAdapter.ForumansAdapterOnClickHandler mClickHandler;

    public interface ForumansAdapterOnClickHandler {
        void onClick(String data);
    }

    public ForumansAdapter(ForumansAdapter.ForumansAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ForumansAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ViewHolder holder;
        public ForumansAdapterViewHolder(View convertView) {
            super(convertView);
            holder=new ViewHolder();
            holder.image = (ImageView) convertView
                    .findViewById(R.id.list_item_sticky_header_social_image);
            holder.name = (TextView) convertView
                    .findViewById(R.id.list_item_sticky_header_social_name);
            holder.text = (TextView) convertView
                    .findViewById(R.id.list_item_sticky_header_social_text);
            holder.pdate = (TextView) convertView
                    .findViewById(R.id.list_item_sticky_header_social_pdate);
            holder.like = (TextView) convertView
                    .findViewById(R.id.list_item_sticky_header_social_icon_like);
            holder.bookmark = (TextView) convertView
                    .findViewById(R.id.list_item_sticky_header_social_icon_bookmark);
            holder.share = (TextView) convertView
                    .findViewById(R.id.list_item_sticky_header_social_icon_share);
            holder.like.setOnClickListener(this);
            holder.bookmark.setOnClickListener(this);
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
                case R.id.list_item_sticky_header_social_icon_bookmark:
                    // click on share button
                    Toast.makeText(v.getContext(), "Bookmark " + position, Toast.LENGTH_SHORT).show();
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
        public TextView name;
        public TextView text;
        public TextView pdate;
        public TextView like;
        public TextView bookmark;
        public TextView share;
    }

    @Override
    public ForumansAdapter.ForumansAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_sticky_header_forumans;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ForumansAdapter.ForumansAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ForumansAdapter.ForumansAdapterViewHolder ForumansAdapterViewHolder, int position) {
        String name = mForumansData[position][1];
        String text = mForumansData[position][2];
        String pdate = mForumansData[position][3];
        String ipadd = ForumansAdapterViewHolder.itemView.getResources().getString(R.string.ipadd);

        //Log.e("Debug",transname+" "+transsname+" "+transdname);
        ForumansAdapterViewHolder.holder.name.setText(name);
        ForumansAdapterViewHolder.holder.text.setText(text);
        ForumansAdapterViewHolder.holder.pdate.setText(pdate);
        ForumansAdapterViewHolder.holder.like.setTag(position);
        ForumansAdapterViewHolder.holder.bookmark.setTag(position);
        ForumansAdapterViewHolder.holder.share.setTag(position);
        ImageUtil.displayImage(ForumansAdapterViewHolder.holder.image, "http://"+ipadd+"/CFGAPI/CFGApp/user.png", null);
    }


    @Override
    public int getItemCount() {
        if (null == mForumansData) return 0;
        return mForumansData.length;
    }


    public void setForumansData(String[][] ForumansData) {
        mForumansData = ForumansData;
        notifyDataSetChanged();
    }
}
