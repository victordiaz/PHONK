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
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import io.phonk.R;
import io.phonk.appinterpreter.AppRunnerCustom;
import io.phonk.events.Events;
import io.phonk.events.EventsProxy;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.gui.settings.UserPreferences;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.network.NetworkUtils;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

public class PhonkServerService extends Service {

    private final String TAG = PhonkServerService.class.getSimpleName();

    private final int NOTIFICATION_ID = 58592;
    private static final String SERVICE_CLOSE = "service_close";

    private NotificationManager mNotificationManager;
    private NotificationChannel mChannel;
    private PendingIntent mRestartPendingIntent;
    private EventsProxy mEventsProxy;

    /*
     * Servers
     */
    private PhonkHttpServer phonkHttpServer;
    private PhonkFtpServer phonkFtpServer;
    private PhonkWebsocketServer phonkWebsockets;
    private ArrayList<String> mConnectedClients = new ArrayList<>();

    private Gson gson = new Gson();
    private int counter = 0;
    private Project mProjectRunning;
    private NotificationCompat.Builder mNotificationBuilder;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.d(TAG, "onStartCommand");

        if (intent != null) {
            AndroidUtils.debugIntent(TAG, intent);
            if (intent.getAction() == SERVICE_CLOSE) {
                EventBus.getDefault().postSticky(new Events.AppUiEvent("stopServers", ""));
                stopSelf();
            }
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
                    Toast.makeText(PhonkServerService.this, "ooops!", Toast.LENGTH_LONG).show();
                    Looper.loop();
                }
            }.start();
            //          handlerToast.post(runnable);

            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mRestartPendingIntent);
            mNotificationManager.cancelAll();

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
         * Init the connect proxy
         */
        mEventsProxy = new EventsProxy();
        EventBus.getDefault().register(this);

        /*
        // go back to app intent
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack mNotificationBuilder object will contain an artificial back stack for
        // navigating backward from the Activity leads out your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        */

        // close server intent
        Intent notificationIntent = new Intent(this, PhonkServerService.class).setAction(SERVICE_CLOSE);
        PendingIntent pendingIntentStopService = PendingIntent.getService(this, (int) System.currentTimeMillis(), notificationIntent, 0);
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        String ip = NetworkUtils.getLocalIpAddress(PhonkServerService.this).get("ip") + ":" + PhonkSettings.HTTP_PORT;
        String msg = this.getString(R.string.notification_description) + " http://" + ip;

        mNotificationBuilder = new NotificationCompat.Builder(this, PhonkSettings.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_phonk_service)
                // .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentTitle(this.getString(R.string.app_name))
                .setContentText(msg)
                .setOngoing(false)
                .setColor(this.getResources().getColor(R.color.phonk_accentColor_primary))
                // .setContentIntent(pendingIntent)
                // .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_action_stop, this.getString(R.string.notification_stop), pendingIntentStopService);
        // .setContentInfo("1 Connection");
        // mNotificationBuilder.build().flags = Notification.FLAG_ONGOING_EVENT;

        // damm annoying android pofkjpodsjf0ewiah
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            mChannel = new NotificationChannel(PhonkSettings.NOTIFICATION_CHANNEL_ID, this.getString(R.string.app_name), importance);
            // mChannel.setDescription("lalalla");
            mChannel.enableLights(false);
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
        }

        startForeground(NOTIFICATION_ID, mNotificationBuilder.build());

        phonkHttpServer = new PhonkHttpServer(this, PhonkSettings.HTTP_PORT);

        // when we get a first request from the editor we can say that the editor is connected
        phonkHttpServer.connectionCallback(ip1 -> {
            if (!mConnectedClients.contains(ip1)) {
                mConnectedClients.add(ip1);
                EventBus.getDefault().postSticky(new Events.UserConnectionEvent(true, ip1, mConnectedClients));
                updateUserSizeNotification(mConnectedClients.size());
                vibrate();
            }
        });

        try {
            phonkWebsockets = new PhonkWebsocketServer(this, PhonkSettings.WEBSOCKET_PORT);
            // when the websocket disconnect we can say that the editor is disconnected
            phonkWebsockets.addConnectionCallback(new PhonkWebsocketServer.ConnectionCallback() {
                @Override
                public void connect(String ip) {
                    if (!mConnectedClients.contains(ip)) {
                        mConnectedClients.add(ip);
                        vibrate();
                        updateUserSizeNotification(mConnectedClients.size());
                        EventBus.getDefault().postSticky(new Events.UserConnectionEvent(true, ip, mConnectedClients));
                    }
                }

                @Override
                public void disconnect(String ip) {
                    if (mConnectedClients.contains(ip)) {
                        mConnectedClients.remove(ip);
                        updateUserSizeNotification(mConnectedClients.size());
                        EventBus.getDefault().postSticky(new Events.UserConnectionEvent(false, ip, mConnectedClients));
                    }
                }
            });
            phonkWebsockets.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // MDNS advertising
        boolean isMDNSAdvertising = (boolean) UserPreferences.getInstance().get("advertise_mdns");
        appRunner.pNetwork.mDNS.register("Phonk WebIDE", "_http._tcp", 8585);

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

        sendUpdatedProjectListToWebIDE();

        //phonkFtpServer = new PhonkFtpServer(this);

        fileObserver.startWatching();
        File file = new File(PhonkSettings.getFolderPath(PhonkSettings.USER_PROJECTS_FOLDER));

        try {
            MLog.d(TAG, "--> " + file.getCanonicalPath() + " " + file.exists());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // register log broadcast
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("io.phonk.intent.CONSOLE");
        registerReceiver(logBroadcastReceiver, filterSend);

        // register webide broadcast
        IntentFilter filterWebEditorSend = new IntentFilter();
        filterWebEditorSend.addAction("io.phonk.intent.WEBEDITOR_SEND");
        registerReceiver(webEditorBroadcastReceiver, filterWebEditorSend);
        MLog.d(TAG, "registering receiver");

        // register a broadcast to receive the notification commands
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_CLOSE);
        registerReceiver(mNotificationReceiver, filter);

        startStopActivityBroadcastReceiver();
        EventBus.getDefault().postSticky(new Events.AppUiEvent("serversStarted", ""));

        viewsUpdate();
    }

    private void updateUserSizeNotification(int size) {
        mNotificationBuilder.setContentTitle("Phonk (" + size + " connections)");
        mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    private void vibrate() {
        int vWait = 20;
        int vTime = 80;
        long[] pattern = new long[]{vWait, vTime, vWait, vTime, vWait, vTime, vWait, vTime, vWait, vTime};
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern, -1);
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
    FileObserver fileObserver = new FileObserver(PhonkSettings.getFolderPath(PhonkSettings.USER_PROJECTS_FOLDER + "/User Projects/"), FileObserver.CREATE | FileObserver.DELETE | FileObserver.DELETE_SELF | FileObserver.MODIFY | FileObserver.MOVED_TO | FileObserver.MOVED_FROM) {
        @Override
        public void onEvent(int event, String file) {
            MLog.d(TAG, "qq -> " + event);
            MLog.d(TAG, "qq2 -> " + file);

            if ((FileObserver.CREATE & event) != 0) {
                MLog.d(TAG, "File created [" + PhonkSettings.getBaseDir() + file + "]");
                EventBus.getDefault().postSticky(new Events.ProjectEvent(Events.PROJECT_REFRESH_LIST, null));
            } else if ((FileObserver.DELETE & event) != 0) {
                MLog.d(TAG, "File deleted [" + PhonkSettings.getBaseDir() + file + "]");
                EventBus.getDefault().postSticky(new Events.ProjectEvent(Events.PROJECT_REFRESH_LIST, null));
            } else if ((FileObserver.MOVED_FROM & event) != 0) {
                MLog.d(TAG, "File moved from [" + PhonkSettings.getBaseDir() + file + "]");
                EventBus.getDefault().postSticky(new Events.ProjectEvent(Events.PROJECT_REFRESH_LIST, null));
            } else if ((FileObserver.MOVED_TO & event) != 0) {
                MLog.d(TAG, "File moved to [" + PhonkSettings.getBaseDir() + file + "]");
                EventBus.getDefault().postSticky(new Events.ProjectEvent(Events.PROJECT_REFRESH_LIST, null));
            }
        }
    };

    private void sendUpdatedProjectListToWebIDE() {
        MLog.d(TAG, "sending");
        MLog.d(TAG, "sending 2");
        HashMap data = new HashMap();
        data.put("module", "project");
        HashMap info = new HashMap();
        data.put("project", info);
        info.put("updatedProjectList", true);
        String jsonObject = gson.toJson(data);
        phonkWebsockets.send(jsonObject);

        /*
        final Handler handler = new Handler();
        Runnable r = new Runnable() {
            @Override
            public void run() {

            }
        };
        handler.post(r);
        */
    }

    /*
     * Notification that show if the server is ON
     */

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
            MLog.d(TAG, "onReceive" + intent.getAction());

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
        MLog.d(TAG, "connect -> " + e.getAction());

        String action = e.getAction();
        if (action.equals(Events.PROJECT_RUN)) {
            PhonkAppHelper.launchScript(getApplicationContext(), e.getProject());
            mProjectRunning = e.getProject();
        } else if (action.equals(Events.PROJECT_STOP_ALL_AND_RUN)) {
            // ProtoScriptHelper.stop_all_scripts();
            Intent i = new Intent("io.phonk.runner.intent.CLOSE");
            sendBroadcast(i);
            PhonkAppHelper.launchScript(getApplicationContext(), e.getProject());
        } else if (action.equals(Events.PROJECT_STOP_ALL)) {
            Intent i = new Intent("io.phonk.runner.intent.CLOSE");
            sendBroadcast(i);
        } else if (action.equals(Events.PROJECT_SAVE)) {
        } else if (action.equals(Events.PROJECT_NEW)) {
            sendUpdatedProjectListToWebIDE();
        } else if (action.equals(Events.PROJECT_DELETE)) {
            sendUpdatedProjectListToWebIDE();
        } else if (action.equals(Events.PROJECT_REFRESH_LIST)) {
            sendUpdatedProjectListToWebIDE();
        } else if (action.equals(Events.PROJECT_UPDATE)) {
            //mProtocoder.protoScripts.listRefresh();
        } else if (action.equals(Events.PROJECT_EDIT)) {
            MLog.d(TAG, "edit " + e.getProject().getName());
            PhonkAppHelper.launchEditor(getApplicationContext(), e.getProject());
        }
    }

    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        Intent i = new Intent("io.phonk.runner.intent.EXECUTE_CODE");
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
        filterSend.addAction("io.phonk.intent.CLOSED");
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
        MLog.d("registerreceiver", "sending connect");

        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("io.phonk.intent.VIEWS_UPDATE");
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
