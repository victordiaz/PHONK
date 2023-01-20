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

package io.phonk.runner.apprunner.api.media;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.base.gui.CameraTexture;
import io.phonk.runner.base.gui.CameraTexture2;

public class PCamera2 extends CameraTexture2 implements PCameraInterface {
    protected final AppRunner mAppRunner;

    public PCamera2(AppRunner appRunner) {
        super(appRunner);
        this.mAppRunner = appRunner;

        mAppRunner.whatIsRunning.add(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void startFlash(Context c, boolean b) {
        try {
            CameraManager mCamManager = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            if (mCamManager != null) {
                cameraId = mCamManager.getCameraIdList()[0];
                mCamManager.setTorchMode(cameraId, b);
            }
        } catch (CameraAccessException e) {
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void takePicture() {

    }

    @Override
    public void recordVideo(String file) {

    }

    @Override
    public void stopRecordingVideo() {

    }

    @Override
    public void focus(ReturnInterface callback) {

    }

    @Override
    public void previewSize(int w, int h) {

    }

    @Override
    public void pictureResolution(int w, int h) {

    }

    @Override
    public boolean isFlashAvailable() {
        return false;
    }

    @Override
    public void flash(boolean b) {

    }

    @Override
    public void colorEffect(String effect) {

    }

    @Override
    public void onNewFrame(CameraTexture.CallbackData callbackfn) {

    }

    @Override
    public void onNewFrameBitmap(CameraTexture.CallbackBmp callbackfn) {

    }

    @Override
    public void onNewFrameBase64(CameraTexture.CallbackStream callbackfn) {

    }

    public void __close() {
        //    cam.closeCamera();
    }


}
