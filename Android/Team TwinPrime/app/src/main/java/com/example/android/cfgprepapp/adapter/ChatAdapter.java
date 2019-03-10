package com.example.android.cfgprepapp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.util.ImageUtil;


public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatAdapterViewHolder> {
    private String[][] mChatData;
    private int mCount;

    private final ChatAdapter.ChatAdapterOnClickHandler mClickHandler;

    public interface ChatAdapterOnClickHandler {
        void onClick(String data[],int adapterpos, ProgressBar lb);
    }

    public ChatAdapter(ChatAdapter.ChatAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ChatAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ViewHolder holder;
        public ChatAdapterViewHolder(View convertView) {
            super(convertView);
            holder=new ViewHolder();
            holder.message = (TextView) convertView
                    .findViewById(R.id.text_message_body);
            holder.time = (TextView) convertView
                    .findViewById(R.id.text_message_time);
            holder.loader = (ProgressBar) convertView
                    .findViewById(R.id.loadbar);
            holder.message.setOnClickListener(this);
            convertView.setTag(holder);
        }


        @Override
        public void onClick(View v) {
                Log.e("Clicked","Yes");
                int adapterPosition = getAdapterPosition();
                String chatDatum[] = mChatData[adapterPosition];
                ProgressBar lb = holder.loader;
                mClickHandler.onClick(chatDatum,adapterPosition,lb);
        }
    }

    private static class ViewHolder {
        public TextView message;
        public TextView time;
        public ProgressBar loader;

    }

    @Override
    public ChatAdapter.ChatAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.item_message_sent;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        //Log.e("Debug","Yes");
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ChatAdapter.ChatAdapterViewHolder(view);

    }


    @Override
    public void onBindViewHolder(ChatAdapter.ChatAdapterViewHolder ChatAdapterViewHolder, int position) {
        String message = mChatData[position][0];
        String time = mChatData[position][1];
        String type = mChatData[position][2];
        //Log.e("Debug",time);
        if(type.equals("0")) {
            Drawable drawable=ChatAdapterViewHolder.holder.message.getContext().getResources().getDrawable(R.drawable.rounded_rectangle_orange);
            ChatAdapterViewHolder.holder.message.setBackground(drawable);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)ChatAdapterViewHolder.holder.message.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.removeRule(RelativeLayout.ALIGN_PARENT_END);
            RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams)ChatAdapterViewHolder.holder.time.getLayoutParams();
            params2.addRule(RelativeLayout.RIGHT_OF,ChatAdapterViewHolder.holder.message.getId());
            params2.removeRule(RelativeLayout.START_OF);
            ChatAdapterViewHolder.holder.time.setLayoutParams(params2);
            RelativeLayout.LayoutParams params3 = (RelativeLayout.LayoutParams)ChatAdapterViewHolder.holder.loader.getLayoutParams();
            params3.addRule(RelativeLayout.RIGHT_OF,ChatAdapterViewHolder.holder.time.getId());
            params3.removeRule(RelativeLayout.START_OF);
            ChatAdapterViewHolder.holder.loader.setLayoutParams(params3);
        }
        ChatAdapterViewHolder.holder.message.setText(message);
        ChatAdapterViewHolder.holder.time.setText(time);

    }


    @Override
    public int getItemCount() {
        return mCount;
    }


    public void setChatData(String[][] ChatData, int count) {
        mChatData = ChatData;
        Log.e("NotifyDataSetChanged","Yes"+mChatData[0][0]);
        mCount=count;
        notifyDataSetChanged();
    }
}
