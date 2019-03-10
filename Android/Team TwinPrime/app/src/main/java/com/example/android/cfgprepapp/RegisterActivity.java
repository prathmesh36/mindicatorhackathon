package com.example.android.cfgprepapp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.font.RobotoTextView;
import com.example.android.cfgprepapp.view.FloatLabeledEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    RegTask mRegTask=null;
    SharedPreferences pref;

    FloatLabeledEditText mUsername;
    FloatLabeledEditText mPassword;
    FloatLabeledEditText mEmail;
    FloatLabeledEditText mName;
    String age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mUsername= findViewById(R.id.username);
        mPassword= findViewById(R.id.password);
        mEmail= findViewById(R.id.email);
        mName= findViewById(R.id.name);
        RobotoTextView regButton= findViewById(R.id.register);

        //Set Listener
        regButton.setOnClickListener(this);

        //Initialize DatePicker
        MyEditTextDatePicker datePicker=new MyEditTextDatePicker(this,R.id.dob);

        //DropDown Spinner Code
        Spinner dropdown = findViewById(R.id.spinner1);
        String[] items = new String[]{"20","21", "22", "23"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        age=dropdown.getSelectedItem().toString();
    }

    //Listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                //Starting Async Task
                mRegTask = new RegTask(mUsername.getText().toString(),mPassword.getText().toString(),mEmail.getText().toString(),mName.getText().toString(),age);
                mRegTask.execute((Void) null);
                break;
        }
    }

    public class RegTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUsername;
        private final String mPassword;
        private final String mEmail;
        private final String mName;
        private final String mAge;
        String jsonStr;
        JSONObject jsonObj = null;

        RegTask(String username,String password,String email,String name,String age) {
            mUsername=username;
            mPassword=password;
            mEmail=email;
            mName=name;
            mAge=age;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            HttpHandler sh = new HttpHandler();
            String ipadd = getResources().getString(R.string.ipadd);
            String url = "/CFGAPI/register.php";
            String headurl = "http://";
            url=headurl+ipadd+url;
            Log.d("url",url);

            //Creating a Hashmap for storing parameters for POST Method.
            HashMap<String, String> postparam = new HashMap<>();

            // adding each child node to HashMap key => value
            postparam.put("username", mUsername);
            postparam.put("password",mPassword);
            postparam.put("email",mEmail);
            postparam.put("name",mName);
            postparam.put("age",mAge);
            for (Map.Entry<String,String> entry : postparam.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                //Log.e(key,value);
            }

            // Making a request to url and getting response
            jsonStr = sh.makeServiceCallPost(url,postparam);
            Log.d("Debugg(LoginAct)", "Response from url: " + jsonStr); //The Output of First Page

            //JSON Parsing
            if (jsonStr != null) {

                String resp="";
                try {
                    jsonObj = new JSONObject(jsonStr);
                    //Log.e("doInBackgroundl", jsonObj.getString("status"));
                    resp=jsonObj.getString("status");

                } catch (final JSONException e) {
                    Log.e("Error", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegisterActivity.this,
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
                if(resp.equals("1"))
                {
                    //If Status Returned true then reg is successful
                    return true;
                }

            } else {
                Log.e("Error", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this,
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();

                    }
                });
            }

            //If Status returned false then reg error
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegTask = null;

            if (success) {
                //JSON Data
                JSONObject Data=null;
                try {
                    Data=jsonObj.getJSONObject("0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                //Setting Shared Preference
                pref = getApplicationContext().getSharedPreferences("LoginSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                try {
                    editor.putString("userID", Data.getString("u_id"));
                    editor.putString("name",Data.getString("name"));
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Starting new Intent
                Intent intent =new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                //Error no data found
            }
        }

        @Override
        protected void onCancelled() {
            mRegTask = null;
        }
    }

}



//DatePicker Class Code
class MyEditTextDatePicker  implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    EditText _editText;
    private int _day;
    private int _month;
    private int _birthYear;
    private Context _context;

    public MyEditTextDatePicker(Context context, int editTextViewID)
    {
        Activity act = (Activity)context;
        this._editText = (EditText)act.findViewById(editTextViewID);
        this._editText.setOnClickListener(this);
        this._context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        _birthYear = year;
        _month = monthOfYear;
        _day = dayOfMonth;
        updateDisplay();
    }
    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(_context, this,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();

    }

    // updates the date in the birth date EditText
    private void updateDisplay() {

        _editText.setText(new StringBuilder()
                // Month is 0 based so add 1
                .append(_day).append("/").append(_month + 1).append("/").append(_birthYear).append(" "));
    }
}
