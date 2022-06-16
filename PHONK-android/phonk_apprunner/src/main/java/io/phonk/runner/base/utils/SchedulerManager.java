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

package io.phonk.runner.base.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.services.AlarmReceiver;

public class SchedulerManager {

    private static final java.lang.String TAG = SchedulerManager.class.getSimpleName();
    private final AlarmManager mAlarmManager;
    private Context c;
    private final Project mProject;

    private SharedPreferences mPreferences;
    private ArrayList<Task> tasks = null; // new ArrayList<SchedulerManager.Task>();
    private static final String ALARM_INTENT = "protocoder_alarm_message";

    public SchedulerManager(Context c, Project p) {
        this.c = c;
        mProject = p;
        mAlarmManager = (AlarmManager) (c).getSystemService(Context.ALARM_SERVICE);
    }

    public void addAlarm(Date date, int id, String data, int interval, boolean repeating, boolean wakeUpScreen) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");

        // intent
        Intent intent = new Intent(c, AlarmReceiver.class);
        intent.putExtra(ALARM_INTENT, data);
        intent.putExtra(Project.SETTINGS_SCREEN_WAKEUP, wakeUpScreen);
        intent.putExtra(Project.NAME, mProject.getName());
        intent.putExtra(Project.FOLDER, mProject.getFolder());
        PendingIntent sender = PendingIntent.getBroadcast(c, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set Alarm
        if (repeating)
            mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, sender);
        else mAlarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);

        // add to a global alarm thingie
        addTask(new Task(id, mProject, Task.TYPE_ALARM, cal, interval, repeating, wakeUpScreen));
    }

    public void removeAlarm(int id) {
        Intent intent = new Intent(c, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(c, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);
        removeTask(id);
    }


    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        loadTasks();
        tasks.add(task);
        saveTasks();
    }

    public void removeTask(int id) {
        loadTasks();
        for (int i = 0; i < tasks.size(); i++) {
            if (tasks.get(i).id == id) tasks.remove(i);
        }
        saveTasks();
    }

    public SchedulerManager loadTasks() {
        mPreferences = c.getSharedPreferences("io.phonk.scheduler", Context.MODE_PRIVATE);
        String tasksString = mPreferences.getString("tasks", "");
        tasks = GSONUtil.getInstance().getGson().fromJson(tasksString, new TypeToken<ArrayList<Task>>() {
        }.getType());

        if (tasks == null) tasks = new ArrayList<>();
        MLog.d(TAG, "ww " + tasksString + " " + tasks);

        return this;
    }

    public SchedulerManager saveTasks() {
        String tasksString = GSONUtil.getInstance().getGson().toJson(tasks);
        boolean isSaved = mPreferences.edit().putString("tasks", tasksString).commit();
        MLog.d(TAG, "ww save " + tasksString + " " + isSaved);

        return this;
    }


    public class Task {
        static final int TYPE_ALARM = 0;
        static final int TYPE_BOOT = 1;
        static final int TYPE_SMS = 1;

        public int id;
        public int type;
        public Project project;
        public Calendar time;
        public int interval;
        public boolean repeating;
        public boolean wakeUpScreen;

        public Task(int id, Project p, int type, Calendar time, int interval, boolean repeating, boolean wakeUpScreen) {
            this.id = id;
            this.project = p;
            this.type = type;
            this.time = time;
            this.interval = interval;
            this.repeating = repeating;
            this.wakeUpScreen = wakeUpScreen;
        }

        public String getTimeString() {
            return time.toString();
        }
    }

}
