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
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.apprunner.AppRunnerInterpreter;
import io.phonk.runner.apprunner.AppRunnerSettings;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.events.Events;
import io.phonk.runner.models.Project;

public class AppRunnerService extends Service {

    private final String TAG = AppRunnerService.class.getSimpleName();

    private static final String SERVICE_CLOSE = "service_close";

    private AppRunnerService mContext;
    private AppRunner mAppRunner;

    private WindowManager windowManager;
    private RelativeLayout parentScriptedLayout;
    private RelativeLayout mainLayout;

    private BroadcastReceiver mReceiver;
    private NotificationManager mNotifManager;
    private PendingIntent mRestartPendingIntent;
    private Toast mToast;

    private boolean eventBusRegistered = false;
    private boolean mOverlayIsEnabled = false;
    private NotificationManager mNotificationManager;
    private int mNotificationId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerEventBus();

        mainLayout = initLayout();

        mContext = this;

        AppRunnerSettings.SERVER_PORT = intent.getIntExtra(Project.SERVER_PORT, 0);

        mAppRunner = new AppRunner(mContext);
        mAppRunner.hasUserInterface = false;
        mAppRunner.initDefaultObjects(AppRunnerHelper.createSettings());
        mAppRunner.pApp.folder = intent.getStringExtra(Project.FOLDER);;
        mAppRunner.pApp.name = intent.getStringExtra(Project.NAME);
        mAppRunner.pDevice.deviceId = intent.getStringExtra(Project.DEVICE_ID);

        mAppRunner.loadProject(mAppRunner.pApp.folder, mAppRunner.pApp.name);
        //  mAppRunner.mIntentPrefixScript = intent.getString(Project.PREFIX, "");
        //  mAppRunner.mIntentCode = intent.getString(Project.INTENTCODE, "");
        //  mAppRunner.mIntentPostfixScript = intent.getString(Project.POSTFIX, "");
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

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    touchParam,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);

            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = 0;
            params.y = 0;

            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(mContext)) {
                    Intent intent2 = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    // startActivityForResult(intent, 1234);
                }
            }
            windowManager.addView(mainLayout, params);
        }

        // start / stop service
        startStopActivityBroadcastReceiver();
        executeCodeActivityBroadcastReceiver();

        // notification
        mNotifManager = (NotificationManager) AppRunnerService.this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationId = (int) Math.ceil(100000 * Math.random());
        createNotification(mNotificationId, mAppRunner.getProject().getFolder(), mAppRunner.getProject().getName());

        //just in case it crash
        Intent restartIntent = new Intent("org.protocoder.LauncherActivity"); //getApplicationContext(), AppRunnerActivity.class);
        restartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        restartIntent.putExtra("wasCrash", true);

        mRestartPendingIntent = PendingIntent.getActivity(AppRunnerService.this, 0, restartIntent, 0);
        mToast = Toast.makeText(AppRunnerService.this, "Service crashed :(", Toast.LENGTH_LONG);

        // catch errors and send them to the WebIDE or the app console
        AppRunnerInterpreter.InterpreterInfo appRunnerCb = new AppRunnerInterpreter.InterpreterInfo() {
            @Override
            public void onError(int resultType, Object message) {
                mAppRunner.pConsole.p_error(resultType, message);
            }
        };
        mAppRunner.interp.addListener(appRunnerCb);

        mAppRunner.initProject();
        mAppRunner.interp.callJsFunction("onCreate");

        return Service.START_NOT_STICKY;
    }

    public void registerEventBus() {
        if (!eventBusRegistered) {
            EventBus.getDefault().register(this);
            eventBusRegistered = true;
        }
    }

    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
        eventBusRegistered = false;
    }

    private void createNotification(final int notificationId, String scriptFolder, String scriptName) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SERVICE_CLOSE);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SERVICE_CLOSE)) {
                    AppRunnerService.this.stopSelf();
                    mNotifManager.cancel(notificationId);
                }
            }
        };

        registerReceiver(mReceiver, filter);

        //RemoteViews remoteViews = new RemoteViews(getPackageName(),
        //        R.layout.widget);

        Intent stopIntent = new Intent(SERVICE_CLOSE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, stopIntent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.phonk_icon)
                .setContentTitle(scriptName).setContentText("Running service: " + scriptFolder + " > " + scriptName)
                .setOngoing(false)
                .addAction(R.drawable.phonk_icon, "stop", pendingIntent)
                .setDeleteIntent(pendingIntent);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, AppRunnerActivity.class);

        // The stack builder object will contain an artificial back stack for
        // navigating backward from the Activity leads out your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(AppRunnerActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, mBuilder.build());
        Thread.setDefaultUncaughtExceptionHandler(handler);

    }


    Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {

            MLog.d(TAG, "exception" + ex.toString());

            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(AppRunnerService.this, "lalll", Toast.LENGTH_LONG);
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


    public void addScriptedLayout(RelativeLayout scriptedUILayout) {
        parentScriptedLayout.addView(scriptedUILayout);
    }

    public RelativeLayout initLayout() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // set the parent
        parentScriptedLayout = new RelativeLayout(this);
        parentScriptedLayout.setLayoutParams(layoutParams);
        parentScriptedLayout.setGravity(Gravity.BOTTOM);
        parentScriptedLayout.setBackgroundColor(getResources().getColor(R.color.transparent));

        return parentScriptedLayout;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MLog.d(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "onDestroy");

        mAppRunner.interp.callJsFunction("onDestroy");

        if (mOverlayIsEnabled) windowManager.removeView(mainLayout);

        Intent i = new Intent("org.protocoder.intent.CLOSED");
        sendBroadcast(i);
        unregisterReceiver(stopActivitiyBroadcastReceiver);
        unregisterReceiver(executeCodeActivitiyBroadcastReceiver);
        mNotificationManager.cancel(mNotificationId);
        unregisterReceiver(mReceiver);
        unregisterEventBus();
        mAppRunner.byebye();
        mAppRunner.interp = null;
    }

    /**
     * Activity dependent events
     */
    @Subscribe
    public void onEventMainThread(Events.LogEvent e) {
        Intent i = new Intent("org.protocoder.intent.CONSOLE");

        String action = e.getAction();
        String data = e.getData();

        i.putExtra("action", action);
        i.putExtra("data", data);
        sendBroadcast(i);
    }

    /**
     * Receiving order to close the activity
     */
    public void startStopActivityBroadcastReceiver() {
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("org.protocoderrunner.intent.CLOSE");
        registerReceiver(stopActivitiyBroadcastReceiver, filterSend);
    }

    BroadcastReceiver stopActivitiyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopSelf();
        }
    };

    /**
     * Receiving order to execute line of code
     */
    public void executeCodeActivityBroadcastReceiver() {
        IntentFilter filterSend = new IntentFilter();
        filterSend.addAction("org.protocoderrunner.intent.EXECUTE_CODE");
        registerReceiver(executeCodeActivitiyBroadcastReceiver, filterSend);
    }

    BroadcastReceiver executeCodeActivitiyBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String code = intent.getStringExtra("code");

            mAppRunner.interp.eval(code);
        }
    };


}
