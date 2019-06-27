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

import android.hardware.Camera;

import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.gui.CameraNew;

import java.util.List;

public class PCameraOld extends CameraNew implements PCameraInterface {

    private final PCameraOld cam;
    protected AppRunner mAppRunner;

    public PCameraOld(AppRunner appRunner, int camera) {
        super(appRunner, camera, PCameraOld.MODE_COLOR_COLOR);
        this.mAppRunner = appRunner;
        cam = this;
    }

    @ProtoMethodParam(params = {"fileName", "function()"})
    @ProtoMethod(description = "Takes a picture and saves it to fileName", example = "camera.takePicture();")
    // @APIRequires()
    public void takePicture(String file, final ReturnInterface callbackfn) {

        takePic(mAppRunner.getProject().getFullPathForFile(file));
        addListener(new CameraListener() {

            @Override
            public void onVideoRecorded() {

            }

            @Override
            public void onPicTaken() {
                callbackfn.event(null);
                cam.removeListener(this);
            }
        });
    }


    public List<Camera.Size> getSizes() {
        return mParameters.getSupportedPictureSizes();
    }

    public List<Camera.Size> getPreviewSizes() {
        return mParameters.getSupportedPreviewSizes();
    }
    public List<String> getSceneModes() {
        return mParameters.getSupportedSceneModes();
    }

    public String getColorEffects() {
        return mParameters.getColorEffect();
    }

    public List<String> getFocusModes() {
        return mParameters.getSupportedFocusModes();
    }

    public void setFocusMode(String mode) {
        mParameters.setFocusMode(mode);
        applyParameters();
    }


    @ProtoMethodParam(params = {"function(bitmap)"})
    @ProtoMethod(description = "Gets bitmap frames ready to use", example = "camera.takePicture();")
    public void onNewBitmap(final CameraNew.CallbackBmp callbackfn) {
        cam.addCallbackBmp(callbackfn);
    }

    @ProtoMethodParam(params = {"function(base64Image)"})
    @ProtoMethod(description = "Get the frames ready to stream", example = "camera.takePicture();")
    public void onNewStreamFrame(CameraNew.CallbackStream callbackfn) {
        cam.addCallbackStream(callbackfn);
    }

    @ProtoMethodParam(params = {"width", "height"})
    @ProtoMethod(description = "Set the camera preview resolution", example = "camera.takePicture();")
    public void setPreviewSize(int w, int h) {
        super.setPreviewSize(w, h);
    }

    @ProtoMethodParam(params = {"width", "height"})
    @ProtoMethod(description = "Set the camera picture resolution", example = "camera.takePicture();")
    public void setPictureResolution(int w, int h) {
        super.setPictureSize(w, h);
    }

    @ProtoMethodParam(params = {"{'none', 'mono', 'sepia', 'negative', 'solarize', 'posterize', 'whiteboard', 'blackboard'}"})
    @ProtoMethod(description = "Set the camera picture effect if supported", example = "camera.takePicture();")
    public void setColorEffect(String effect) {
        super.setColorEffect(effect);
    }

    @ProtoMethod(description = "Records a video in fileName", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public void recordVideo(String file) {
        recordVideo(mAppRunner.getProject().getFullPathForFile(file));
    }

    @ProtoMethod(description = "Stops recording the video", example = "")
    @ProtoMethodParam(params = {""})
    public void stopRecordingVideo() {
        stopRecordingVideo();
    }

    @ProtoMethod(description = "Checks if flash is available", example = "")
    @ProtoMethodParam(params = {""})
    public boolean isFlashAvailable() {
        return super.isFlashAvailable();
    }

    @ProtoMethod(description = "Turns on/off the flash", example = "")
    @ProtoMethodParam(params = {""})
    public void turnOnFlash(boolean b) {
        super.turnOnFlash(b);
    }

    @ProtoMethod(description = "Turn the autofocus on/off", example = "")
    @ProtoMethodParam(params = {""})
    public void focus() {
        super.focus(null);
    }

    @ProtoMethod(description = "Turn the autofocus on/off", example = "")
    @ProtoMethodParam(params = {""})
    public void focus(ReturnInterface callback) {
        super.focus(callback);
    }
}
