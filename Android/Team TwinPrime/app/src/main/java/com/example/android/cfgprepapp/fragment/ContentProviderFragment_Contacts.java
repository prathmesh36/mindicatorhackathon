package com.example.android.cfgprepapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.view.cpb.CircularProgressButton;

import me.everything.providers.android.contacts.Contact;
import me.everything.providers.android.contacts.ContactsProvider;
import me.everything.providers.core.Data;


public class ContentProviderFragment_Contacts extends Fragment {

    private ContactsAdapter mAdapter;
    private ListView mListView;

    //For SMS Manager Method
    final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;
    String phoneNo,message;

    public ContentProviderFragment_Contacts() {
        // Required empty public constructor
    }

    public static ContentProviderFragment_Contacts newInstance() {
        ContentProviderFragment_Contacts fragment = new ContentProviderFragment_Contacts();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView= inflater.inflate(R.layout.fragment_provider, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list);
        new GetContactsTask().execute();
        return rootView;
    }

    //SMS Using SMS Manager Method
    protected void sendSMSMessage(String mess,String phone) {

        phoneNo=phone;
        message=mess;
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getActivity(), "SMS sent.",
                Toast.LENGTH_LONG).show();

    }


    private class GetContactsTask extends AsyncTask<Void, Void, Data<Contact>> {

        @Override
        protected Data<Contact> doInBackground(Void... params) {
            ContactsProvider contactsProvider = new ContactsProvider(getActivity());
            Data<Contact> contacts = contactsProvider.getContacts();
            return contacts;
        }

        @Override
        protected void onPostExecute(Data<Contact> data) {
            mAdapter = new ContactsAdapter(getActivity(), data,new ContentProviderFragment_Contacts.BtnClickListener() {

                @Override
                public void onBtnClick(String phoneNo) {
                    composeMmsMessage("Hii Message Sent through CFG PrepApp",phoneNo);
                }

            });
            mListView.setAdapter(mAdapter);
        }

        //SMS Using Intent Method
        public void composeMmsMessage(String message,String phoneNo) {
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(getActivity()); // Need to change the build to API 19
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
            sendIntent.setType("text/plain");
            sendIntent.setData(Uri.parse("smsto:"+phoneNo));
            sendIntent.putExtra("sms_body", "Hello Sent From CFG App");
            if (defaultSmsPackageName != null)
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            startActivity(sendIntent);
        }

    }

    public interface BtnClickListener {
        public abstract void onBtnClick(String phoneNo);
    }

    private final class ContactsAdapter extends CursorAdapter {

        private Data<Contact> mData;
        private ContentProviderFragment_Contacts.BtnClickListener mClickListener = null;


        private class ViewHolder {
            TextView name;
            CircularProgressButton button;
        }

        public ContactsAdapter(Context context, Data<Contact> data, ContentProviderFragment_Contacts.BtnClickListener listener) {
            super(context, data.getCursor(), FLAG_REGISTER_CONTENT_OBSERVER);
            mData = data;
            mClickListener=listener;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item_follow, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.text);
            viewHolder.button=(CircularProgressButton)view.findViewById(R.id.circular_progress_bar);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, final Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            final Contact contact = mData.fromCursor(cursor);
            viewHolder.name.setText(contact.displayName);
            viewHolder.button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(mClickListener != null)
                        mClickListener.onBtnClick(contact.phone);
                }
            });

        }

    }


}
