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

package io.phonk.runner.api;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.NativeObject;
import io.phonk.runner.AppRunnerActivity;
import io.phonk.runner.R;
import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.api.common.ReturnObject;
import io.phonk.runner.api.other.PEvents;
import io.phonk.runner.api.other.PLiveCodingFeedback;
import io.phonk.runner.apidoc.annotation.ProtoField;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.SchedulerManager;
import io.phonk.runner.base.utils.ExecuteCmd;
import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

import java.util.Iterator;
import java.util.Map;

@ProtoObject
public class PApp extends ProtoBase {

    @ProtoField
    public final Notification notification;

    @ProtoField
    public Map<String, Object> settings;

    PEvents pevents;
    public String folder;
    public String name;
    public Bundle intentData;

    public interface onAppStatus {
        public void onStart();
        public void onPause();
        public void onResume();
        public void onStop();
    }

    public PApp(AppRunner appRunner) {
        super(appRunner);
        notification = new Notification();
        pevents = new PEvents(appRunner);
    }

    //
    //@APIMethod(description = "get the script runner context", example = "")
    //public AppRunnerFragment getContext() {
    //	return (AppRunnerFragment) mContext;
    //}

    /**
     * @status TODO
     */
    @ProtoMethod
    public SchedulerManager alarmManager() {
        return new SchedulerManager(getContext(), getAppRunner().getProject());
    }


    /**
     * Gets shared data?
     * @param type
     * @param data
     *
     * @status TODO
     */
    @ProtoMethod
    public void getSharedData(String type, String data) {

    }

    /**
     * Closes the current running script
     *
     * @status OK
     */
    @ProtoMethod
    public void close() {
        getActivity().finish();
    }

    /**
     * Runs a string of Javascript code
     * Don't abuse this!
     *
     * @param code
     * @status TODO_EXAMPLE
     *
     * @exampleLink examples/User Interface/Webview
     * @advanced
     */
    @android.webkit.JavascriptInterface
    @ProtoMethod
    public void eval(final String code) {
        runOnUiThread(new ReturnInterface() {
            @Override
            public void event(ReturnObject r) {
                getAppRunner().interp.eval(code);
            }
        });
    }

    /**
     * Loads and external file containing code
     *
     * @param filename
     *
     * @exampleLink /examples/Advanced/LoadJsFile
     * @status OK
     */
    @ProtoMethod
    public void load(String filename) {
        String code = FileIO.loadStringFromFile(getAppRunner().getProject().getFullPathForFile(filename));

        getAppRunner().interp.eval(code);
    }

    /*
    @ProtoMethod(description = "loads a library stored in the <i>libraries</i>' folder", example = "")
    @ProtoMethodParam(params = {"libraryName"})
    public void loadLibrary(String name) {
        String code = FileIO.loadStringFromFile("../../libraries/" + name + "/main.js");
        getAppRunner().interp.eval(code);
    }
    */


    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "notification cancelled");
        }

    }

    public Bundle getIntentData() {
        return intentData;
    }

    class Notification {
        private NotificationManager mNotificationManager;
        int id;
        NotificationCompat.Builder mBuilder;

        Notification () {
            mNotificationManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);
        }

        public Notification create(Map map) {

            Bitmap iconBmp = null;
            String iconName = (String) map.get("icon");
            if (iconName != null) iconBmp = BitmapFactory.decodeFile(getAppRunner().getProject().getFullPathForFile(iconName));

            String launchOnClick = null;
            launchOnClick = (String) map.get("launchOnClick");
            Project p = new Project(launchOnClick);

            String notificationData = null;
            notificationData = (String) map.get("notificationData");
            this.id = ((Number) map.get("id")).intValue();

            Intent intent = new Intent(getContext(), MyBroadcastReceiver.class);
            intent.putExtra("notificationData", notificationData);
            intent.putExtra(Project.FOLDER, p.getFolder());
            intent.putExtra(Project.NAME, p.getName());
            intent.putExtra("isNotification", true);
            intent.putExtra("notificationId", this.id);

            PendingIntent deletePendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);

            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(getContext(), AppRunnerActivity.class);
            resultIntent.putExtra("notificationData", notificationData);
            resultIntent.putExtra(Project.FOLDER, p.getFolder());
            resultIntent.putExtra(Project.NAME, p.getName());
            resultIntent.putExtra("isNotification", true);
            resultIntent.putExtra("notificationId", this.id);

            // The stack builder object will contain an artificial back stack for navigating backward from the Activity leads out your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
            stackBuilder.addParentStack(AppRunnerActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            this.mBuilder = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle((CharSequence) map.get("title"))
                    .setContentText((CharSequence) map.get("description"))
                    .setLights(Color.parseColor((String) map.get("color")), 1000, 1000)
                    .setLargeIcon(iconBmp)
                    .setAutoCancel((Boolean) map.get("autocancel"))
                    .setTicker((String)map.get("ticker"))
                    .setSubText((CharSequence) map.get("subtext"))
                    .setDeleteIntent(deletePendingIntent)
                    .setContentIntent(resultPendingIntent);


            return this;
        }

        public Notification show() {
            mNotificationManager.notify(id, mBuilder.build());
            return this;
        }

        public Notification cancel(int id) {
            mNotificationManager.cancel(id);

            return this;
        }

        public Notification cancelAll() {
            mNotificationManager.cancelAll();

            return this;
        }

        public Notification onClick(ReturnInterface callback) {

            return this;
        }
    }

    /**
     * Share Image with an app installed in the system
     * @param imagePath
     *
     * @status TODO
     */
    @ProtoMethod
    public void shareImage(String imagePath) {
        ContentValues values = new ContentValues();
        values.put(MediaColumns.MIME_TYPE, "image/png");
        //values.put(MediaColumns.DATA, AppRunnerSettings.get().project.getStoragePath() + "/" + imagePath);
        Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");

        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        getContext().startActivity(shareIntent);
    }


    /**
     * Share text with an app installed in the system
     *
     * @param text
     *
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public void shareText(String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        getContext().startActivity(shareIntent);
    }

    /**
     * Get the current project path
     *
     * @return
     *
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public String path() {
        String url = getAppRunner().getProject().getSandboxPath();
        return url;
    }

    /**
     * Get the current project fullpath
     *
     * @return
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public String fullPath() {
        String url = getAppRunner().getProject().getFullPath();
        return url;
    }

    /**
     * Runs a function in the UI thread. This is normally not needed but might be required when combined with Threads
     *
     * @param callback
     *
     * @advanced
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public void runOnUiThread(final ReturnInterface callback) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.event(null);
            }
        });
    }

    /**
     * Executes a shell command
     *
     * @param cmd
     * @param callbackfn
     * @return
     *
     * @advanced
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public ExecuteCmd executeShellCommand(final String cmd, final ReturnInterface callbackfn) {

        return new ExecuteCmd(cmd, callbackfn);
    }

    /**
     * Shows a feedback overlay with the live-executed code
     *
     * @return PLiveCodingFeedback
     *
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public PLiveCodingFeedback liveCodingOverlay() {
        PLiveCodingFeedback l = getFragment().liveCodingFeedback();
        l.enable = true;

        return l;
    }

    /**
     * Sends a name event with a json object
     *
     * @param name
     * @param obj
     *
     * @exampleLink examples/Advanced/Events
     * @see PApp#listenEvent
     * @see PApp#removeEvent
     *
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public void sendEvent(String name, Object obj) {
        pevents.sendEvent(name, (NativeObject) obj);
    }

    /**
     * Receives a named event with a json object
     *
     * @param name
     * @param callback
     * @return
     *
     * @exampleLink examples/Advanced/Events
     * @see PApp#sendEvent
     * @see PApp#removeEvent
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public String listenEvent(String name, PEvents.EventCB callback) {
        return pevents.add(name, callback);
    }

    /**
     * Receives a named event with a json object
     *
     * @param id
     *
     *
     * @exampleLink examples/Advanced/Events
     * @see PApp#sendEvent
     * @see PApp#listenEvent
     *
     * @status TODO_EXAMPLE
     */
    @ProtoMethod
    public void removeEvent(String id) {
        pevents.remove(id);
    }

    /**
     * Try to open a file with an app installed in the system
     * @param src
     *
     * @status TODO
     */
    @ProtoMethod
    public void openFileWithApp(final String src) {
        /*
        final String projectPath = ProjectManager.getInstance().getCurrentProject().getStoragePath();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(projectPath + "/" + src));

        mContext.startActivity(intent);
        */
    }


    /**
     * Start an activity
     *
     * @param params
     * @throws JSONException
     *
     * @advanced
     * @status TODO
     */
    @ProtoMethod
    public void startActivity(NativeObject params) throws JSONException {
        Intent intent = new Intent();

        JSONObject jsonParams = new JSONObject(params);

        String action = (String) jsonParams.get("action");
        intent.setAction(action);

        JSONObject extras = (JSONObject) jsonParams.get("extras");
        Iterator<String> iterator = extras.keys();
        while(iterator.hasNext()) {
            String key = iterator.next();
            String value = (String) extras.get(key);
            MLog.d("qq", key + " " + value);
            intent.putExtra(key, value);
        }

        String data = (String) jsonParams.get("data");
        intent.setData(Uri.parse(data));

        String type = (String) jsonParams.get("type");
        intent.setType(type);

        MLog.d("qq", action + " " + data + " " + type);



        JSONObject component = (JSONObject) jsonParams.get("component");
        intent.setComponent(new ComponentName("com.example", "com.example.MyExampleActivity"));

        // JSONArray flags = (JSONArray) jsonParams.get("flags");
        // intent.setFlags(flags);

        getContext().startActivity(intent);

        // getActivity().startActivityForResult();
    }

    /**
     * Callback called when script is loaded
     *
     * @param callback
     *
     * @exampleLink /examples/Advanced/App Lifecycle.
     *
     * @see PApp#onDestroy
     * @see PApp#onPause
     * @see PApp#onResume
     *
     * @status OK
     * @advanced
     */
    @ProtoMethod
    public void onCreate(ReturnInterface callback) {
      //  callback.event(null);
    }

    /**
     * Callback called when script is paused. For example when switching to a different task.
     *
     * @param callback
     *
     * @exampleLink /examples/Advanced/App Lifecycle
     *
     * @see PApp#onDestroy
     * @see PApp#onCreate
     * @see PApp#onResume
     *
     * @status OK
     * @advanced
     *
     */
    @ProtoMethod
    public void onPause(ReturnInterface callback) {
      //  callback.event(null);
    }

    /**
     * Callback called when script is resumed. This can be called either when the app loads for the first time or switches back from a different app.
     *
     * @param callback
     *
     * @exampleLink /examples/Advanced/App Lifecycle
     *
     * @see PApp#onDestroy
     * @see PApp#onPause
     * @see PApp#onCreate
     *
     * @status OK
     * @advanced
     *
     */
    @ProtoMethod
    public void onResume(ReturnInterface callback) {
      //  callback.event(null);
    }

    /**
     * Callback called when script is closed
     *
     * @param callback
     *
     * @exampleLink /examples/Advanced/App Lifecycle
     *
     * @see PApp#onCreate
     * @see PApp#onPause
     * @see PApp#onResume
     *
     * @status OK
     * @advanced
     *
     */
    @ProtoMethod
    public void onDestroy(ReturnInterface callback) {
      //  callback.event(null);
    }

    public void breakPoint() {
        getAppRunner().interp.stop();
    }

    public void resumeFromBreakPoint() {

    }

    @Override
    public void __stop() {

    }
}
