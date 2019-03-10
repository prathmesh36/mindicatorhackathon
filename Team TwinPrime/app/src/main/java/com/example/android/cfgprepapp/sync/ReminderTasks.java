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
import android.content.Intent;

import com.example.android.cfgprepapp.notifyutilities.NotificationUtils;
import com.example.android.cfgprepapp.notifyutilities.PreferenceUtilities;

public class ReminderTasks {

    public static final String ACTION_INCREMENT_COUNT = "increment-count";
    //  COMPLETED (2) Add a public static constant called ACTION_DISMISS_NOTIFICATION
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
    static final String ACTION_NOTIFICATION_REMINDER = "reminder-notification";
    public static void executeTask(Context context, String action) {
        if (ACTION_INCREMENT_COUNT.equals(action)) {
            incrementCount(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_NOTIFICATION_REMINDER.equals(action)) {
            issueReminderNotification(context,"Andheri");
        }
        //      COMPLETED (3) If the user ignored the reminder, clear the notification
    }

    public static void executeTask(Context context, String action, String status) {
        if (ACTION_NOTIFICATION_REMINDER.equals(action)) {
            issueReminderNotification(context,status);
        }
        //      COMPLETED (3) If the user ignored the reminder, clear the notification
    }

    private static void incrementCount(Context context) {
        NotificationUtils.clearAllNotifications(context);
    }
    private static void issueReminderNotification(Context context,String status) {
        PreferenceUtilities.incrementChargingReminderCount(context);
        NotificationUtils.remindUserNotification(context,status);
    }

}