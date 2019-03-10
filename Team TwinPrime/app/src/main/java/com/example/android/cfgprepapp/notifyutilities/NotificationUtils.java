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
package com.example.android.cfgprepapp.notifyutilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Action;
import android.support.v4.content.ContextCompat;

import com.example.android.cfgprepapp.MainActivity;
import com.example.android.cfgprepapp.R;
import com.example.android.cfgprepapp.TrainListActivity;
import com.example.android.cfgprepapp.sync.ReminderIntentService;
import com.example.android.cfgprepapp.sync.ReminderTasks;


/**
 * Utility class for creating hydration notifications
 */
public class NotificationUtils {

    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private static final int REMINDER_NOTIFICATION_ID = 1138;
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int REMINDER_PENDING_INTENT_ID = 3417;
    /**
     * This notification channel id is used to link notifications to this channel
     */
    private static final String REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel";
    private static final int ACTION_INC_PENDING_INTENT_ID = 1;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 14;

    //  COMPLETED (1) Create a method to clear all notifications
    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindUserNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.main_color_200))
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(context.getString(R.string.reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                // COMPLETED (17) Add the two new actions using the addAction method and your helper methods
                .addAction(incAction(context,"Andheri"))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void remindUserNotification(Context context, String status) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.main_color_200))
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(largeIcon(context))
                .setContentTitle("Are you in train near "+status+" Station?")
                .setContentText("Please help our mumbaikars and get a chance to be on our App")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        "Please help our mumbaikars and get a chance to be on our App"))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                // COMPLETED (17) Add the two new actions using the addAction method and your helper methods
                .addAction(incAction(context,status))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }
        notificationManager.notify(REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    //  COMPLETED (5) Add a static method called ignoreReminderAction
    private static Action ignoreReminderAction(Context context) {
        // COMPLETED (6) Create an Intent to launch ReminderIntentService
        Intent ignoreReminderIntent = new Intent(context, ReminderIntentService.class);
        // COMPLETED (7) Set the action of the intent to designate you want to dismiss the notification
        ignoreReminderIntent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);
        // COMPLETED (8) Create a PendingIntent from the intent to launch ReminderIntentService
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // COMPLETED (9) Create an Action for the user to ignore the notification (and dismiss it)
        Action ignoreReminderAction = new Action(R.drawable.ic_cancel_black_24px,
                "No.",
                ignoreReminderPendingIntent);
        // COMPLETED (10) Return the action
        return ignoreReminderAction;
    }

    //  COMPLETED (11) Add a static method called incAction
    private static Action incAction(Context context,String status) {
        // COMPLETED (12) Create an Intent to launch ReminderIntentService
        Intent incrementCountIntent = new Intent(context, TrainListActivity.class);
        incrementCountIntent.putExtra("station",status);
        // COMPLETED (13) Set the action of the intent to designate you want to increment the  count
        // COMPLETED (14) Create a PendingIntent from the intent to launch ReminderIntentService
        PendingIntent incrementPendingIntent = PendingIntent.getActivity(
                context,
                REMINDER_PENDING_INTENT_ID,
                incrementCountIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // COMPLETED (15) Create an Action for the user to tell us they've had a glass of 
        Action incAction = new Action(R.drawable.background_small,
                "Yes, I'm.",
                incrementPendingIntent);
        // COMPLETED (16) Return the action
        return incAction;
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.notlogo);
        return largeIcon;
    }
}