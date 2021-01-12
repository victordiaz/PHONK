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

package io.phonk;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.service.controls.Control;
import android.service.controls.ControlsProviderService;
import android.service.controls.DeviceTypes;
import android.service.controls.actions.BooleanAction;
import android.service.controls.actions.CommandAction;
import android.service.controls.actions.ControlAction;
import android.service.controls.actions.FloatAction;
import android.service.controls.actions.ModeAction;
import android.service.controls.templates.RangeTemplate;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.reactivestreams.FlowAdapters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.function.Consumer;

import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.base.models.Folder;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;
import io.phonk.server.model.ProtoFile;
import io.reactivex.Flowable;
import io.reactivex.processors.ReplayProcessor;

public class MyCustomControlService extends ControlsProviderService {
    private ReplayProcessor updatePublisher;

    @Override
    public Flow.Publisher createPublisherForAllAvailable() {
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        // listProjectsWithControls();

        // PhonkScriptHelper.listProjects()
        List controls = new ArrayList<>();

        ArrayList<ProtoFile> userFolder = PhonkScriptHelper.listProjectsInFolder(PhonkSettings.USER_PROJECTS_FOLDER, 1);
        ArrayList<ProtoFile> examplesFolder = PhonkScriptHelper.listProjectsInFolder(PhonkSettings.EXAMPLES_FOLDER, 1);

        for (ProtoFile protoFile : examplesFolder) {
            for (ProtoFile projectFolder : protoFile.files) {
                Project p = new Project(projectFolder.getPath());
                Map<String, Object> scriptSettings = AppRunnerHelper.readProjectProperties(getApplicationContext(), p);
                boolean isDeviceControl = (boolean) scriptSettings.get("device_control");

                if (isDeviceControl) {
                    // MLog.d(TAG,  "yep: " + p.getFullPath() + " " + isDeviceControl + " : " + p.getFullPath());
                } else {
                    // MLog.d(TAG, "nop: " + p.getFullPath() + " " + isDeviceControl + " : " + p.getFullPath());
                }

                Control control = new Control.StatelessBuilder(p.getSandboxPath(), pi)
                        .setTitle(p.getName())
                        // .setSubtitle("Subtitle")
                        // .setStructure(getBaseContext().getString(R.string.app_name))
                        .setDeviceType(DeviceTypes.TYPE_UNKNOWN) // For example, DeviceTypes.TYPE_THERMOSTAT
                        //.setZone("pag")
                        .build();
                controls.add(control);
            }
        }
        // Create more controls here if needed and add it to the ArrayList

        // Uses the RxJava 2 library
        return FlowAdapters.toFlowPublisher(Flowable.fromIterable(controls));
    }

    @NonNull
    @Override
    public Flow.Publisher<Control> createPublisherFor(@NonNull List<String> controlIds) {
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        updatePublisher = ReplayProcessor.create();

        // For each controlId in controlIds
        for (String controlId : controlIds) {
            MLog.d(TAG, "qqq" + controlId);

            Project p = new Project(controlId);

            Control control = new Control.StatefulBuilder(controlId, pi)
                    .setTitle(p.getName())
                    // .setSubtitle(MY-CONTROL-SUBTITLE)
                    // .setStructure(getBaseContext().getString(R.string.app_name))
                    .setDeviceType(DeviceTypes.TYPE_UNKNOWN) // For example, DeviceTypes.TYPE_THERMOSTAT
                    .setStatus(Control.STATUS_OK) // For example, Control.STATUS_OK
                    // .setControlTemplate(new RangeTemplate(p.getSandboxPath(),0f, 100f, 50f, 1f, "q"))
                    // .setControlTemplate(new )
                    .build();


            updatePublisher.onNext(control);
        }

        // if (controlIds.contains(MY-UNIQUE-DEVICE-ID)) {
        // }

        return FlowAdapters.toFlowPublisher(updatePublisher);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void performControlAction(@NonNull String controlId, @NonNull ControlAction controlAction, @NonNull Consumer<Integer> consumer) {
        Intent i = new Intent();
        PendingIntent pi = PendingIntent.getActivity(getBaseContext(), 1, i, PendingIntent.FLAG_UPDATE_CURRENT);

        /* First, locate the control identified by the controlId. Once it is located, you can
         * interpret the action appropriately for that specific device. For instance, the following
         * assumes that the controlId is associated with a light, and the light can be turned on
         * or off.
         */
        Project p = new Project(controlId);

        MLog.d("qq", "" + controlAction.getActionType());
        MLog.d("qq", "" + controlAction.getActionType());

        if (controlAction instanceof FloatAction) {
            FloatAction floatAction = (FloatAction) controlAction;
            float value = floatAction.getNewValue();
            MLog.d("floatAction", "" + value);
        }

         if (controlAction instanceof CommandAction) {
            CommandAction commandAction = (CommandAction) controlAction;
            float type = commandAction.getActionType();
            MLog.d("commandAction", "" + commandAction.getChallengeValue());
         }

         if (controlAction instanceof ControlAction) {
            ControlAction commandAction = (ControlAction) controlAction;
            String type = commandAction.getChallengeValue();
            MLog.d("commandAction", "" + type);
         }

         if (controlAction instanceof ModeAction) {
            ModeAction commandAction = (ModeAction) controlAction;
            int type = commandAction.getNewMode();
            MLog.d("modeAction", "" + type);
         }

        if (controlAction instanceof BooleanAction) {

            // Inform SystemUI that the action has been received and is being processed
            consumer.accept(ControlAction.RESPONSE_OK);
            BooleanAction action = (BooleanAction) controlAction;
            MLog.d("controlAction", "" + action.getNewState());

            // In this example, action.getNewState() will have the requested action: true for “On”,
            // false for “Off”.

            /* This is where application logic/network requests would be invoked to update the state of
             * the device.
             * After updating, the application should use the publisher to update SystemUI with the new
             * state.
             */
            Control control = new Control.StatefulBuilder(controlId, pi)
                    .setTitle(p.getName())
                    // .setSubtitle(MY-CONTROL-SUBTITLE)
                    // .setStructure(MY-CONTROL-STRUCTURE)
                    // Required: Type of device, i.e., thermostat, light, switch
                    .setDeviceType(DeviceTypes.TYPE_UNKNOWN) // For example, DeviceTypes.TYPE_THERMOSTAT
                    // Required: Current status of the device
                    .setStatus(Control.STATUS_OK) // For example, Control.STATUS_OK
                    .build();

            // This is the publisher the application created during the call to createPublisherFor()
            updatePublisher.onNext(control);
        }

    }
}
