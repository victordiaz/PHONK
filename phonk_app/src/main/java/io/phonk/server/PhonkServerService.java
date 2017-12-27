/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.server;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.phonk.R;

import io.phonk.appinterpreter.AppRunnerCustom;
import io.phonk.events.Events;
import io.phonk.events.EventsProxy;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

import java.net.UnknownHostException;
import java.util.HashMap;

public class PhonkServerService extends Service {

    private final String TAG = PhonkServerService.class.getSimpleName();

    private final int NOTIFICATION_ID = 58592;
    private static final String SERVICE_CLOSE = "service_close";

    private NotificationManager mNotifManager;
    private PendingIntent mRestartPendingIntent;
    private Toast mToast;
    private EventsProxy mEventsProxy;

    /*
     * Servers
     */
    private PhonkHttpServer phonkHttpServer;
    private PhonkFtpServer phonkFtpServer;
    private PhonkWebsocketServer phonkWebsockets;

    private Gson gson = new Gson();
    private int counter = 0;
    private Project mProjectRunning;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.d(TAG, "onStartCommand");

        if (intent != null) {
            AndroidUtils.debugIntent(TAG, intent);
            if (intent.getAction() == SERVICE_CLOSE) stopSelf();
        }

        return Service.START_STICKY;
    }

    BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "received action: " + intent.getAction());
            if (intent.getAction().equals(SERVICE_CLOSE)) {
                //PhonkServerService.this.stopSelf();
                //mNotifManager.cancel(NOTIFICATION_SERVER_ID);
            }
        }
    };

    Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(PhonkServerService.this, "lalll", Toast.LENGTH_LONG);
                    Looper.loop();
                }
            }.start();
            //          handlerToast.post(runnable);

            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mRestartPendingIntent);
            mNotifManager.cancelAll();

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);

            throw new RuntimeException(ex);
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MLog.d(TAG, "network service created");

        final AppRunnerCustom appRunner = new AppRunnerCustom(this).initDefaultObjects(AppRunnerHelper.createSettings());

        /*
         * Init the event proxy
         */
        mEventsProxy = new EventsProxy();

        EventBus.getDefault().register(this);

        Intent notificationIntent = new Intent(this, PhonkServerService.class).setAction(SERVICE_CLOSE);
        PendingIntent pendingIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(), notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.phonk_icon)
                .setContentTitle("Phonk").setContentText("Web Editor access is enabled")
                .setOngoing(false)
                .addAction(R.drawable.ic_action_stop, "stop", pendingIntent)
                //.setDeleteIntent(pendingIntent)
                .setContentInfo("1 Connection");

        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);

        phonkHttpServer = new PhonkHttpServer(this, PhonkSettings.HTTP_PORT);

        try {
            phonkWebsockets = new PhonkWebsocketServer(this, PhonkSettings.WEBSOCKET_PORT);
            phonkWebsockets.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {

                HashMap data = new HashMap();
                data.put("module", "device");
                HashMap info = new HashMap();
                data.put("info", info);

                // device
                HashMap device = new HashMap();
                ReturnObject deviceInfo = appRunner.pDevice.info();
                device.put("type", deviceInfo.get("type"));
                device.put("model name", deviceInfo.get("model"));
                device.put("manufacturer", deviceInfo.get("manufacturer"));

                // screen
                HashMap screen = new HashMap();
                screen.put("orientation", appRunner.pDevice.orientation());
                screen.put("screen", appRunner.pDevice.isScreenOn());
                screen.put("screen resolution", deviceInfo.get("screenWidth") + " x " + deviceInfo.get("screenHeight"));
                screen.put("screen dpi", deviceInfo.get("screenDpi"));
                screen.put("brightness", appRunner.pDevice.brightness());

                // others
                HashMap other = new HashMap();
                other.put("battery level", appRunner.pDevice.battery());
                other.put("used memory", deviceInfo.get("usedMem"));
                other.put("total memory", deviceInfo.get("totalMem"));

                // network
                HashMap network = new HashMap();
                network.put("network available", appRunner.pNetwork.isNetworkAvailable());
                network.put("wifi enabled", appRunner.pNetwork.isWifiEnabled());
                network.put("cellular network type", appRunner.pNetwork.getNetworkType());
                network.put("ip", appRunner.pNetwork.networkInfo().get("ip"));
                network.put("wifi type", appRunner.pNetwork.networkInfo().get("type"));
                network.put("rssi", appRunner.pNetwork.wifiInfo().getRssi());
                network.put("ssid", appRunner.pNetwork.wifiInfo().getSSID());

                // scripts info (name of the current running project)
                HashMap script = new HashMap();

                String name = "none";
                if (mProjectRunning != null) name = mProjectRunning.getName();
                script.put("running script", name);

                info.put("device", device);
                info.put("screen", screen);
                info.put("other", other);
                info.put("network", network);
                info.put("script", script);

                String jsonObject = gson.toJson(data);

                phonkWebsockets.send(jsonObject);
                handler.postDelayed(this, 2500);
            }
        };
        handler.postDelayed(r, 0);

        //phonkFtpServer = new PhonkFtpServer(this);

        fileObserver.startWatching();

        // register log broadcast
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("org.protocoder.intent.CONSOLE");
        registerReceiver(logBroadcastReceiver, filterSend);

        // register webide broadcast
        IntentFilter filterWebEditorSend = new IntentFilter();
        filterWebEditorSend.addAction("org.protocoder.intent.WEBEDITOR_SEND");
        registerReceiver(webEditorBroadcastReceiver, filterWebEditorSend);
        MLog.d("qq22", "registering receiver");

        // register a broadcast to receive the notification commands
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_CLOSE);
        registerReceiver(mNotificationReceiver, filter);

        startStopActivityBroadcastReceiver();


        EventBus.getDefault().postSticky(new Events.AppUiEvent("serversStarted", ""));


        viewsUpdate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "service destroyed");
        mEventsProxy.stop();
        phonkHttpServer.stop();
        phonkWebsockets.stop();

        // unregisterReceiver(mNotificationReceiver);

        unregisterReceiver(mNotificationReceiver);
        unregisterReceiver(logBroadcastReceiver);
        unregisterReceiver(webEditorBroadcastReceiver);
        unregisterReceiver(stopActivitiyBroadcastReceiver);
        unregisterReceiver(viewsUpdateBroadcastReceiver);

        fileObserver.stopWatching();

        EventBus.getDefault().unregister(this);
    }



    /*
     * FileObserver to notify when projects are added or removed
     */
    FileObserver fileObserver = new FileObserver(PhonkSettings.getBaseDir(), FileObserver.CREATE| FileObserver.DELETE) {

        @Override
        public void onEvent(int event, String file) {
            if ((FileObserver.CREATE & event) != 0) {
                MLog.d(TAG, "File created [" + PhonkSettings.getBaseDir() + "/" + file + "]");
            } else if ((FileObserver.DELETE & event) != 0) {
                MLog.d(TAG, "File deleted [" + PhonkSettings.getBaseDir() + "/" + file + "]");
            }
        }
    };

    /*
     * Notification that show if the server is ON
     */
    private void createNotification() {
        /*
        //create pending intent that will be triggered if the notification is clicked
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_CLOSE);
        // registerReceiver(mNotificationReceiver, filter);

        Intent stopIntent = new Intent(SERVICE_CLOSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(org.protocoderrunner.R.drawable.phonk_icon)
                .setContentTitle("Protocoder").setContentText("Running service ")
                .setOngoing(false)
                .addAction(org.protocoderrunner.R.drawable.ic_action_stop, "stop", pendingIntent)
                .setDeleteIntent(pendingIntent);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for
        // navigating backward from the Activity leads out your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AppRunnerActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION_SERVER_ID, mBuilder.build());

        Thread.setDefaultUncaughtExceptionHandler(handler);
        */
    }

    /**
     * Events
     *
     * - Start app
     * - Stop service
     *
     */

    /**
     * send logs to WEBIDE
     */
    BroadcastReceiver logBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, intent.getAction());

            HashMap hashMap = new HashMap();
            hashMap.put("module", "console");
            hashMap.put("action", intent.getStringExtra("action"));
            hashMap.put("time", intent.getStringExtra("time"));
            hashMap.put("data", intent.getStringExtra("data"));
            String jsonObject = gson.toJson(hashMap);

            phonkWebsockets.send(jsonObject);
        }
    };


    BroadcastReceiver webEditorBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, intent.getAction());
            MLog.d("qq22", "onReceive");

            HashMap hashMap = new HashMap();
            hashMap.put("module", "webeditor");
            hashMap.put("action", intent.getStringExtra("action"));
            hashMap.put("type", intent.getStringExtra("type"));
            hashMap.put("folder", intent.getStringExtra("folder"));
            hashMap.put("name", intent.getStringExtra("name"));
            String jsonObject = gson.toJson(hashMap);

            phonkWebsockets.send(jsonObject);
        }
    };



    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, "event -> " + e.getAction());

        String action = e.getAction();
        if (action.equals(Events.PROJECT_RUN)) {
            PhonkAppHelper.launchScript(getApplicationContext(), e.getProject());
            mProjectRunning = e.getProject();
        } else if (action.equals(Events.PROJECT_STOP_ALL_AND_RUN)) {
            // ProtoScriptHelper.stop_all_scripts();
            Intent i = new Intent("org.protocoderrunner.intent.CLOSE");
            sendBroadcast(i);
            PhonkAppHelper.launchScript(getApplicationContext(), e.getProject());

        } else if (action.equals(Events.PROJECT_STOP_ALL)) {
            Intent i = new Intent("org.protocoderrunner.intent.CLOSE");
            sendBroadcast(i);
        } else if (action.equals(Events.PROJECT_SAVE)) {
            //Project p = evt.getProject();
            //mProtocoder.protoScripts.refresh(p.getFolder(), p.getName());
        } else if (action.equals(Events.PROJECT_NEW)) {
            //MLog.d(TAG, "creating new project " + evt.getProject().getName());
            //mProtocoder.protoScripts.createProject("projects", evt.getProject().getName());
        } else if (action.equals(Events.PROJECT_UPDATE)) {
            //mProtocoder.protoScripts.listRefresh();
        } else if (action.equals(Events.PROJECT_EDIT)) {
            MLog.d(TAG, "edit " + e.getProject().getName());

            PhonkAppHelper.launchEditor(getApplicationContext(), e.getProject());
        }
    }

    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        Intent i = new Intent("org.protocoderrunner.intent.EXECUTE_CODE");
        i.putExtra("code", e.getCode());
        sendBroadcast(i);
    }

    //stop service
    @Subscribe
    public void onEventMainThread(Events.SelectedProjectEvent e) {
        // stopSelf();
    }

    /**
     * Receiving order to close the apprunneractivity
     */
    public void startStopActivityBroadcastReceiver() {
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("org.protocoder.intent.CLOSED");
        registerReceiver(stopActivitiyBroadcastReceiver, filterSend);
    }

    BroadcastReceiver stopActivitiyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "stop_all 2");
            mProjectRunning = null;
        }
    };


    /**
     * Receiving order to execute line of code
     */
    public void viewsUpdate() {
        MLog.d("registerreceiver", "sending event");

        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("org.protocoder.intent.VIEWS_UPDATE");
        registerReceiver(viewsUpdateBroadcastReceiver, filterSend);
    }

    BroadcastReceiver viewsUpdateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String views = intent.getStringExtra("views");
            MLog.d(TAG, "views" + views);
            phonkHttpServer.setViews(views);
        }
    };

}