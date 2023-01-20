/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.base.services;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NotificationService extends NotificationListenerService {
    public static final String ACTION = "NotificationCaptured";
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        MLog.d("notification", "received");

        if (AndroidUtils.isVersionKitKat()) {
            String pack = sbn.getPackageName();
            String ticker = getString(sbn.getNotification().tickerText);
            Bundle extras = sbn.getNotification().extras;
            String title = "";
            String text = "";

            if (extras != null) {
                title = extras.getString("android.title");
                text = getString(extras.getCharSequence("android.text"));
            }

            Intent in = new Intent(ACTION);
            in.putExtra("package", pack);
            in.putExtra("ticker", ticker);
            in.putExtra("title", title);
            in.putExtra("text", text);

            context.sendBroadcast(in);
        }
    }

    private String getString(CharSequence c) {
        String ret;

        if (c == null) {
            ret = "";
        } else {
            ret = c.toString();
        }

        return ret;
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg", "Notification Removed");
    }

    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}