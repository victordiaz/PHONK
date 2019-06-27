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

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.gui.settings.UserPreferences;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.runner.api.other.PLooper;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

public class ConnectionInfoFragment extends Fragment {

    private final String TAG = ConnectionInfoFragment.class.getSimpleName();

    private static AppRunner mAppRunner;
    private View rootView;

    private ToggleButton mToggleServers;
    private TextView mTxtConnectionMessage;
    private TextView mTxtConnectionIp;
    private LinearLayout mConnectionButtons;
    private ImageView mComputerimage;
    boolean stateBlinkCursor = true;
    private TextView mComputerText;

    public ConnectionInfoFragment() {
    }

    public static ConnectionInfoFragment newInstance(AppRunner appRunner) {
        mAppRunner = appRunner;
        ConnectionInfoFragment fragment = new ConnectionInfoFragment();
        // Bundle args = new Bundle();
        // args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        // fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        rootView = inflater.inflate(R.layout.connection_info_fragment, container, false);

        mTxtConnectionMessage = rootView.findViewById(R.id.connection_message);
        mTxtConnectionIp = rootView.findViewById(R.id.connection_ip);

        mConnectionButtons = rootView.findViewById(R.id.connection_buttons);

        mToggleServers = rootView.findViewById(R.id.webide_connection_toggle);
        Button connectWifi = rootView.findViewById(R.id.connect_to_wifi);
        Button startHotspot = rootView.findViewById(R.id.start_hotspot);
        // Button webide_connection_help = (Button) findViewById(R.id.webide_connection_help);

        connectWifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhonkAppHelper.launchWifiSettings(getActivity());
            }
        });

        startHotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhonkAppHelper.launchHotspotSettings(getActivity());
            }
        });

        mToggleServers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               if (b) EventBus.getDefault().postSticky(new Events.AppUiEvent("startServers", ""));
               else EventBus.getDefault().postSticky(new Events.AppUiEvent("stopServers", ""));
            }
        });

        if ((boolean) UserPreferences.getInstance().get("servers_enabled_on_start")) {
            mToggleServers.performClick(); // startServers();
        }

        mComputerText = rootView.findViewById(R.id.computerText);
        mComputerText.setMovementMethod(new ScrollingMovementMethod());

        mAppRunner.pUtil.loop(1000, new PLooper.LooperCB() {
            @Override
            public void event() {
                // String prevText = (String) mComputerText.getText();
                // if (stateBlinkCursor) mComputerText.setText(prevText + "\n> ");
                // else mComputerText.setText(prevText + "\n >_");
                stateBlinkCursor = !stateBlinkCursor;
            }
        }).start();


        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    // network notification
    @Subscribe
    public void onEventMainThread(Events.Connection e) {
        String type = e.getType();
        String address = e.getAddress();

        if (type == "wifi") {
            mTxtConnectionMessage.setText(getResources().getString(io.phonk.R.string.connection_message_wifi));
            mTxtConnectionIp.setText("http://" + address);
            mTxtConnectionIp.setVisibility(View.VISIBLE);
            // mConnectionButtons.setVisibility(View.GONE);
        } else if (type == "tethering") {
            mTxtConnectionMessage.setText(getResources().getString(io.phonk.R.string.connection_message_tethering));
            mTxtConnectionIp.setText("http://" + address);
            mTxtConnectionIp.setVisibility(View.VISIBLE);
            // mConnectionButtons.setVisibility(View.GONE);
        } else {
            mTxtConnectionMessage.setText(getResources().getString(io.phonk.R.string.connection_message_not_connected));
            mTxtConnectionIp.setVisibility(View.GONE);
            // mConnectionButtons.setVisibility(View.VISIBLE);
        }
    }

    public void addTextToConsole(String text) {
        mComputerText.append(text + "\n> ");

    }


    // folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen e) {
        // MLog.d(TAG, "< Event (folderChosen)");
        addTextToConsole("ls " + e.getFullFolder());
    }


    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, "event -> " + e.getAction());

        String action = e.getAction();
        Project p = e.getProject();

        if (action.equals(Events.PROJECT_RUN)) {
            addTextToConsole("run " + p.getSandboxPath());

        } else if (action.equals(Events.PROJECT_STOP_ALL_AND_RUN)) {
            addTextToConsole("stop " + p.getSandboxPath());
        } else if (action.equals(Events.PROJECT_STOP_ALL)) {
        } else if (action.equals(Events.PROJECT_SAVE)) {
            addTextToConsole("saving... " + p.getSandboxPath());
        } else if (action.equals(Events.PROJECT_NEW)) {
            addTextToConsole("new project");
        } else if (action.equals(Events.PROJECT_UPDATE)) {
            //mProtocoder.protoScripts.listRefresh();
        } else if (action.equals(Events.PROJECT_EDIT)) {
            addTextToConsole("edit project" + p.getSandboxPath());
        }
    }

}
