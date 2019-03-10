/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.cfgprepapp.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.data.HttpHandler;
import com.example.android.cfgprepapp.location.GPSTracker;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ReminderFirebaseJobService extends JobService {

    private AsyncTask mBackgroundTask;
    private  String url = "/CFGAPI/selectforum.php";
    private String headurl = "http://";
    String ipadd;
    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     *
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        mBackgroundTask = new AsyncTask() {
            String jsonStr;
            GPSTracker gpsTracker;
            @Override
            protected Object doInBackground(Object[] params) {
                Log.e("Background:","Start");
                ipadd = getResources().getString(R.string.ipadd);

                gpsTracker = new GPSTracker(ReminderFirebaseJobService.this);
                String stringLatitude="0";
                String stringLongitude="0";
                if (gpsTracker.getIsGPSTrackingEnabled())
                {
                    //Getting Latitude and Longitude
                    stringLatitude = String.valueOf(gpsTracker.latitude);
                    stringLongitude = String.valueOf(gpsTracker.longitude);


                    //Error Checking and Substring to make it ready to store it in the database
                    if(stringLatitude.length()>5) {
                        stringLatitude = stringLatitude.substring(0, 7);
                        stringLongitude = stringLongitude.substring(0, 7);
                    }
                    else
                    {
                        stringLatitude="19.119510";
                        stringLongitude="72.846862";
                    }

                }
                else
                {
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gpsTracker.showSettingsAlert();
                }
                Log.e("Longitude/Latitude:",stringLatitude+"/"+stringLongitude);
                HttpHandler sh = new HttpHandler();
                url = "/CFGAPI/backgroundtask.php";
                headurl = "http://";
                url=headurl+ipadd+url;

                HashMap<String, String> postparam = new HashMap<>();
                // adding each child node to HashMap key => value\
                postparam.put("lat", stringLatitude);
                postparam.put("long",stringLongitude);

                for (Map.Entry<String,String> entry : postparam.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Log.e(key,value);
                }
                jsonStr = sh.makeServiceCallPost(url,postparam);
                Log.e("doInBackgroundl", jsonStr);

                String status="0";
                //JSON Parsing
                if (jsonStr != null)
                {
                    JSONObject jsonObj = null;
                    String resp="";

                    try {
                        jsonObj = new JSONObject(jsonStr);
                        Log.e("doInBackgroundl", jsonObj.getString("status"));
                        resp=jsonObj.getString("status");

                    } catch (final JSONException e) {
                        Log.e("Error", "Json parsing error: " + e.getMessage());
                        return -1;
                    }
                    if(!resp.equals("0"))
                    {
                        //If Status Returned true then registration is successful
                        Log.e("Success", "Server Updated ");
                        status=resp;
                    }else
                    {
                        Log.e("Error", "Server Error");
                    }

                }
                else
                {
                    Log.e("Error", "Couldn't get json from server.");

                }

                if(!status.equals("0")) {
                    Context context = ReminderFirebaseJobService.this;
                    ReminderTasks.executeTask(context, ReminderTasks.ACTION_NOTIFICATION_REMINDER,status);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                /*
                 * Once the AsyncTask is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the jobParamters that were passed to your
                 * job and a boolean representing whether the job needs to be rescheduled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */

                jobFinished(jobParameters, false);
            }
        };

        mBackgroundTask.execute();
        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.e("Notification:","Stop");
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}