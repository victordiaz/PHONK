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

package io.phonk.runner.api.media;

import android.widget.Toast;

import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.CameraException;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.apprunner.AppRunner;

/**
 * Created by biquillo on 9/29/17.
 */

public class ArCore extends ProtoBase {

    private Session mSession;
    private Config mDefaultConfig;

    public ArCore(AppRunner appRunner) {
        super(appRunner);
    }

    public void init () {
        mSession = new Session(getActivity());

        // Create default config, check is supported, create session from that config.
        mDefaultConfig = Config.createDefaultConfig();
        if (!mSession.isSupported(mDefaultConfig)) {
            Toast.makeText(getActivity(), "This device does not support AR", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void update () {
        Frame frame = null;
        try {
            frame = mSession.update();

            // If not tracking, don't draw 3d objects.
            if (frame.getTrackingState() == Frame.TrackingState.NOT_TRACKING) {
                return;
            }

            // Visualize tracked points.
            // frame.getPointCloud().getPoints();
            // frame.getPose().getTranslation(dest, 0);
            // MLog.d(TAG, "");
            // mPointCloud.update(frame.getPointCloud());
            // mPointCloud.draw(frame.getPointCloudPose(), viewmtx, projmtx);
            // mPlaneRenderer.drawPlanes(mSession.getAllPlanes(), frame.getPose(), projmtx);

        } catch (CameraException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void __stop() {

    }
}
