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

import android.graphics.Bitmap;
import android.hardware.Camera;

import java.util.List;

import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.gui.CameraTexture;
import io.phonk.runner.base.utils.MLog;

public class PCamera extends CameraTexture implements PCameraInterface {

    private final PCamera cam;
    protected AppRunner mAppRunner;

    private LearnImages learnImages = null;
    private DetectImage detectImage = null;

    public PCamera(AppRunner appRunner, int camera) {
        super(appRunner, camera, PCamera.MODE_COLOR_COLOR);
        this.mAppRunner = appRunner;
        cam = this;

        this.addOnReadyCallback(new OnReadyCallback() {
            @Override
            public void event() {
                setFocusMode("auto");
                // setAspectRatio(1, 1);
            }
        });
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
        if (mode.equals("auto")) {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else {
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_FIXED);
        }
        applyParameters();
    }

    @ProtoMethodParam(params = {"function(data)"})
    @ProtoMethod(description = "Gets frames", example = "")
    public void startLearning(LearnImages.Callback callback) {
      learnImages.start();
      learnImages.addCallback(callback);
      cam.addCallbackData(new CallbackData() {
          @Override
          public void event(byte[] data, Camera camera) {
            learnImages.addCameraFrame(data, camera);
          }
        });

        /*
        new CallbackBmp() {
        @Override
        public void event(Bitmap bmp) {
          learnImages.inferenceAnalyzerThread(bmp, 0);
          MLog.d(TAG, "learning...");
        }
      });
         */
    }

    public void startDetecting(DetectImage.Callback callback) {
        cam.addCallbackBmp(new CallbackBmp() {
            @Override
            public void event(Bitmap bmp) {
                detectImage.detect(bmp);
            }
        });
        detectImage.start();
    }

    public LearnImages learnImages() {
      learnImages = new LearnImages(mAppRunner);
      return learnImages;
    }

    public DetectImage detectImage() {
        detectImage = new DetectImage(mAppRunner);
        return detectImage;
    }

    @ProtoMethodParam(params = {"function(data)"})
    @ProtoMethod(description = "Gets data frames in yuv format (bytes)", example = "")
    public void onNewFrame(CallbackData callbackfn) {
      cam.addCallbackData(callbackfn);
    }

    @ProtoMethodParam(params = {"function(bitmap)"})
    @ProtoMethod(description = "Gets bitmap frames ready to use", example = "")
    public void onNewFrameBitmap(final CameraTexture.CallbackBmp callbackfn) {
        cam.addCallbackBmp(callbackfn);
    }

    @ProtoMethodParam(params = {"function(base64Image)"})
    @ProtoMethod(description = "Get the frames ready to stream", example = "")
    public void onNewFrameBase64(CameraTexture.CallbackStream callbackfn) {
        cam.addCallbackStream(callbackfn);
    }

    @ProtoMethodParam(params = {"width", "height"})
    @ProtoMethod(description = "Set the camera preview resolution", example = "")
    public void setPreviewSize(int w, int h) {
        super.setPreviewSize(w, h);
    }

    @ProtoMethodParam(params = {"width", "height"})
    @ProtoMethod(description = "Set the camera picture resolution", example = "")
    public void setPictureResolution(int w, int h) {
        super.setPictureSize(w, h);
    }

    @ProtoMethodParam(params = {"{'none', 'mono', 'sepia', 'negative', 'solarize', 'posterize', 'whiteboard', 'blackboard'}"})
    @ProtoMethod(description = "Set the camera picture effect if supported", example = "")
    public void setColorEffect(String effect) {
        super.setColorEffect(effect);
    }

    @ProtoMethod(description = "Records a video in fileName", example = "")
    @ProtoMethodParam(params = {"fileName"})
    public void recordVideo(String file, final ReturnInterface callbackfn) {
        recordVideo(mAppRunner.getProject().getFullPathForFile(file));
        addListener(new CameraListener() {

            @Override
            public void onVideoRecorded() {
                callbackfn.event(null);
                cam.removeListener(this);
            }

            @Override
            public void onPicTaken() {
            }
        });
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

    @ProtoMethod(description = "Focus", example = "")
    @ProtoMethodParam(params = {""})
    public void focus() {
        super.focus(null);
    }

    @ProtoMethod(description = "Focus with callback on finish", example = "")
    @ProtoMethodParam(params = {""})
    public void focus(ReturnInterface callback) {
        super.focus(callback);
    }
}
