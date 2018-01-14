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

package io.phonk.gui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import io.phonk.R;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.SchedulerManager;
import io.phonk.runner.base.utils.MLog;

import java.util.ArrayList;

public class SchedulerActivity extends BaseActivity {

    private static final String TAG = SchedulerActivity.class.getSimpleName();
    private ArrayList<SchedulerManager.Task> tasks;
    private ListView mScheduleTasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduler_activity);

        SchedulerManager schedulerManager = new SchedulerManager(this, null);
        tasks = schedulerManager.loadTasks().getTasks();

        mScheduleTasks = (ListView) findViewById(R.id.task_list);
        final MyAdapter myAdapter = new MyAdapter(this, tasks);
        mScheduleTasks.setAdapter(myAdapter);

        setupActivity();
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();

        enableBackOnToolbar();
    }

    private class MyAdapter extends ArrayAdapter<SchedulerManager.Task> {

        public MyAdapter(Context context, ArrayList<SchedulerManager.Task> task) {
            super(context, -1, -1, task);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.scheduler_task_item, parent, false);
            }

            TextView txtTaskProjectName = (TextView) convertView.findViewById(R.id.txtTaskProjectName);
            TextView txtTaskId = (TextView) convertView.findViewById(R.id.txtTaskId);
            TextView txtTaskType = (TextView) convertView.findViewById(R.id.txtTaskType);
            TextView txtTaskWhen = (TextView) convertView.findViewById(R.id.txtTaskWhen);
            TextView txtTaskInterval = (TextView) convertView.findViewById(R.id.txtTaskInterval);
            CheckBox bRepeating = (CheckBox) convertView.findViewById(R.id.bTaskRepeating);
            CheckBox bWakeUpScreen = (CheckBox) convertView.findViewById(R.id.bTaskWakeUpScreen);

            SchedulerManager.Task task = getItem(position);

            MLog.d(TAG, "ww task loaded " + task.id + " " + txtTaskId);

            txtTaskProjectName.setText(task.project.getSandboxPath());

            txtTaskId.setText("" + task.id);
            txtTaskType.setText("" + task.type);
            txtTaskWhen.setText("" + task.time.getTime());
            txtTaskInterval.setText("" + task.interval);
            bRepeating.setChecked(task.repeating);
            bWakeUpScreen.setChecked(task.wakeUpScreen);

            return convertView;
        }
    }

}
