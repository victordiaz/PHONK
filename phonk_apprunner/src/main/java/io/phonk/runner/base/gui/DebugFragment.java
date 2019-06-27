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

package io.phonk.runner.base.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.phonk.runner.R;
import io.phonk.runner.apprunner.AppRunnerInterpreter;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.events.Events;

public class DebugFragment extends Fragment {

    private static final String TAG = DebugFragment.class.getSimpleName();

    private View v;
    private RecyclerView mListView;
    private ArrayList<DebugFragment.LogData> mLogArray = new ArrayList<>();
    private MyAdapter mArrayAdapter;
    private LinearLayoutManager mLayoutManager;
    private boolean isLockPosition = false;
    private boolean eventBusRegistered = false;

    public static DebugFragment newInstance() {
        DebugFragment myFragment = new DebugFragment();
        return myFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        registerEventBus();

        v = inflater.inflate(R.layout.debug_fragment, container, false);

        mListView = v.findViewById(R.id.logwrapper);

        mArrayAdapter = new MyAdapter();
        mListView.setAdapter(mArrayAdapter);
        mListView.setItemAnimator(null);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        ToggleButton toggleLock = v.findViewById(R.id.toogleLockList);
        toggleLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isLockPosition = isChecked;
            }
        });

        Button close = v.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(DebugFragment.this).commit();
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);

        mListView.setLayoutManager(mLayoutManager);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        registerEventBus();
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterEventBus();
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

    public void addText(int actionType, String log) {
        MLog.d(TAG, actionType + " " + log);
        mLogArray.add(new LogData(actionType, log));

        if (isLockPosition == false) {
            mArrayAdapter.notifyItemInserted(mLogArray.size());
            mListView.scrollToPosition(mLogArray.size() - 1);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {

        }
        return true;
    }

    @Subscribe (sticky = true)
    public void onEventMainThread(Events.LogEvent e) {
        String logMsg = e.getData();

        int actionType = AppRunnerInterpreter.RESULT_OK;
        if (e.getAction() == "log_error") actionType = AppRunnerInterpreter.RESULT_ERROR;
        else if (e.getAction() == "log_permission_error") actionType = AppRunnerInterpreter.RESULT_PERMISSION_ERROR;

        addText(actionType, logMsg);
    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        public MyAdapter() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LinearLayout ll = null;
            switch (viewType) {
                case AppRunnerInterpreter.RESULT_OK:
                    ll = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.debug_console_text, parent, false);
                    break;

                case AppRunnerInterpreter.RESULT_ERROR:
                    ll = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.debug_console_error, parent, false);
                    break;

                case AppRunnerInterpreter.RESULT_PERMISSION_ERROR:
                    ll = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.debug_console_permissions, parent, false);
                    break;

            }
            DebugFragment.ViewHolder vh = new DebugFragment.ViewHolder(viewType, ll);

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String txt = mLogArray.get(position).data;
            holder.consoleText.setText(txt);

            if (holder.viewType == AppRunnerInterpreter.RESULT_PERMISSION_ERROR) {
                holder.btnGrantPermissions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MLog.d(TAG, "qq");
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mLogArray.size();
        }

        @Override
        public int getItemViewType(int position) {
            return mLogArray.get(position).type;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final LinearLayout ll;
        private final TextView consoleType = null;
        private final TextView consoleText;
        private final int viewType;
        private Button btnGrantPermissions = null;

        public ViewHolder(int viewType, LinearLayout v) {
            super(v);
            this.viewType = viewType;
            ll = v;
            consoleText = ll.findViewById(R.id.console_text);

            if (viewType == AppRunnerInterpreter.RESULT_PERMISSION_ERROR) {
                btnGrantPermissions = ll.findViewById(R.id.grantPermissionsBtn);
            }
        }
    }

    public class LogData {
        int type;
        String data;

        public LogData(int actionType, String log) {
            this.type = actionType;
            this.data = log;
        }
    }
}
