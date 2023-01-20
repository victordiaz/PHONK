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

package io.phonk.runner;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import androidx.core.app.NotificationCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.apprunner.AppRunnerSettings;
import io.phonk.runner.apprunner.interpreter.AppRunnerInterpreter;
import io.phonk.runner.base.events.Events;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;

public class AppRunnerService extends Service {

    private static final String SERVICE_CLOSE = "service_close";
    final BroadcastReceiver stopActivitiyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };
    final BroadcastReceiver executeCodeActivitiyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra("code");

            // mAppRunner.interp.eval(code);
        }
    };
    private final String TAG = AppRunnerService.class.getSimpleName();
    private final boolean mOverlayIsEnabled = false;
    private final String mNotificationChannelId = "phonk_script";
    private AppRunner mAppRunner;
    private WindowManager windowManager;
    private RelativeLayout parentScriptedLayout;
    private RelativeLayout mainLayout;
    private NotificationManager mNotifManager;
    private PendingIntent mRestartPendingIntent;
    final Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            MLog.d(TAG, "exception" + ex.toString());

            AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mRestartPendingIntent);

            mNotifManager.cancelAll();

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);

            throw new RuntimeException(ex);
        }
    };
    private boolean eventBusRegistered = false;
    private NotificationManager mNotificationManager;
    private int mNotificationId;
    private NotificationCompat.Builder mNotificationBuilder;
    private NotificationChannel mChannel;

    @Override
    public void onCreate() {
        super.onCreate();
        MLog.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.d(TAG, "onStartCommand " + intent);

        if (intent.getAction() != null) {
            if (intent.getAction().equals(SERVICE_CLOSE)) {
                stopSelf();
                return START_STICKY;
            }
        }

        registerEventBus();
        mainLayout = initLayout();

        AppRunnerSettings.SERVER_PORT = intent.getIntExtra(Project.SERVER_PORT, 0);

        mAppRunner = new AppRunner(this);
        mAppRunner.hasUserInterface = false;
        mAppRunner.initDefaultObjects(AppRunnerHelper.createSettings());
        mAppRunner.pApp.folder = intent.getStringExtra(Project.FOLDER);
        mAppRunner.pApp.name = intent.getStringExtra(Project.NAME);
        mAppRunner.pDevice.deviceId = intent.getStringExtra(Project.DEVICE_ID);

        mAppRunner.loadProject(mAppRunner.pApp.folder, mAppRunner.pApp.name);
        mAppRunner.initInterpreter();

        if (mOverlayIsEnabled) {
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

            boolean isTouchable = true;
            int touchParam;
            if (isTouchable) {
                touchParam = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                touchParam = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            }

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    touchParam,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
            );

            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 0;
            params.y = 0;

            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent2 = new Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName())
                    );
                    // startActivityForResult(intent, 1234);
                }
            }
            windowManager.addView(mainLayout, params);
        }

        // start / stop service
        startStopActivityBroadcastReceiver();
        executeCodeActivityBroadcastReceiver();

        // catch errors and send them to the WebIDE or the app console
        AppRunnerInterpreter.InterpreterInfo appRunnerCb = (resultType, message) -> mAppRunner.pConsole.p_error(
                resultType,
                message
        );
        mAppRunner.initProject();

        // notification
        mNotifManager = (NotificationManager) AppRunnerService.this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationId = (int) Math.ceil(100000 * Math.random());
        createNotification(mNotificationId, mAppRunner.getProject().getFolder(), mAppRunner.getProject().getName());

        // just in case it crash
        Intent restartIntent = new Intent("io.phonk.LauncherActivity"); //getApplicationContext(), AppRunnerActivity
        // .class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        restartIntent.putExtra("wasCrash", true);

        mRestartPendingIntent = PendingIntent.getActivity(AppRunnerService.this, 0, restartIntent, 0);

        startForeground(mNotificationId, mNotificationBuilder.build());

        return Service.START_NOT_STICKY;
    }

    public void registerEventBus() {
        if (!eventBusRegistered) {
            EventBus.getDefault().register(this);
            eventBusRegistered = true;
        }
    }

    public RelativeLayout initLayout() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        // set the parent
        parentScriptedLayout = new RelativeLayout(this);
        parentScriptedLayout.setLayoutParams(layoutParams);
        parentScriptedLayout.setGravity(Gravity.BOTTOM);
        parentScriptedLayout.setBackgroundColor(getResources().getColor(R.color.transparent));

        return parentScriptedLayout;
    }

    /**
     * Receiving order to close the activity
     */
    public void startStopActivityBroadcastReceiver() {
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("io.phonk.runner.intent.CLOSE");
        registerReceiver(stopActivitiyBroadcastReceiver, filterSend);
    }

    /**
     * Receiving order to execute line of code
     */
    public void executeCodeActivityBroadcastReceiver() {
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("io.phonk.runner.intent.EXECUTE_CODE");
        registerReceiver(executeCodeActivitiyBroadcastReceiver, filterSend);
    }

    private void createNotification(final int notificationId, String scriptFolder, String scriptName) {


        // close server intent
        Intent notificationIntent = new Intent(this, AppRunnerService.class).setAction(SERVICE_CLOSE);
        PendingIntent pendingIntentStopService = PendingIntent.getService(
                this,
                (int) System.currentTimeMillis(),
                notificationIntent,
                0
        );
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationBuilder = new NotificationCompat.Builder(
                this,
                mNotificationChannelId
        ).setSmallIcon(R.drawable.dotted)
                // .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentTitle(this.getString(R.string.app_name)).setContentText("Running " + scriptName).setOngoing(
                        false).setColor(this.getResources().getColor(R.color.phonk_colorPrimary))
                // .setContentIntent(pendingIntent)
                // .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_action_stop, "Stop script", pendingIntentStopService);
        // .setContentInfo("1 Connection");
        // mNotificationBuilder.build().flags = Notification.FLAG_ONGOING_EVENT;

        // damm annoying android pofkjpodsjf0ewiah
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            mChannel = new NotificationChannel(mNotificationChannelId, this.getString(R.string.app_name), importance);
            // mChannel.setDescription("lalalla");
            mChannel.enableLights(false);
            mNotificationManager.createNotificationChannel(mChannel);
        } else {
        }

        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "onDestroy");
        if (mOverlayIsEnabled) windowManager.removeView(mainLayout);

        Intent i = new Intent("io.phonk.intent.CLOSED");
        sendBroadcast(i);
        unregisterReceiver(stopActivitiyBroadcastReceiver);
        unregisterReceiver(executeCodeActivitiyBroadcastReceiver);
        mNotificationManager.cancel(mNotificationId);
        unregisterEventBus();
        mAppRunner.byebye();
        // mAppRunner.interp = null;
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
        eventBusRegistered = false;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    public void addScriptedLayout(View scriptedUILayout) {
        parentScriptedLayout.addView(scriptedUILayout);
    }

    /**
     * Activity dependent events
     */
    @Subscribe
    public void onEventMainThread(Events.LogEvent e) {
        Intent i = new Intent("io.phonk.intent.CONSOLE");

        String action = e.getAction();
        String data = e.getData();

        i.putExtra("action", action);
        i.putExtra("data", data);
        sendBroadcast(i);
    }


}
