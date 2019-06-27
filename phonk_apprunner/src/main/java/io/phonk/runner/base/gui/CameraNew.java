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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import io.phonk.runner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.utils.TimeUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CameraNew extends TextureView implements TextureView.SurfaceTextureListener {

    public static final int MODE_CAMERA_FRONT = 0;
    public static final int MODE_CAMERA_BACK = 1;
    public static final int MODE_COLOR_BW = 2;
    public static final int MODE_COLOR_COLOR = 3;
    private static int cameraRotation = 0;

    int modeColor;
    int modeCamera;
    private int cameraId;

    protected String TAG = CameraNew.class.getSimpleName();

    AppRunner mAppRunner;

    // camera
    //TextureView mTextureView;
    protected Camera mCamera;

    // saving info
    private String _rootPath;
    private String _fileName;
    private String _path;
    private View v;

    private Vector<CameraListener> listeners = new Vector<CameraListener>();
    private CallbackData callbackData;
    private CallbackBmp callbackBmp;
    private CallbackStream callbackStream;
    private boolean frameProcessing = false;
    protected Parameters mParameters;

    public interface CameraListener {
        void onPicTaken();
        void onVideoRecorded();
    }

    public CameraNew(AppRunner appRunner, int camera, int colorMode) {
        super(appRunner.getAppContext());
        this.mAppRunner = appRunner;
        this.modeColor = colorMode;
        this.modeCamera = camera;
        this.setSurfaceTextureListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stopCamera();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }


    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //set shadow
        //AndroidUtils.setViewGenericShadow(this, width, height);

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // mCamera.stopPreview();
        // mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

        if (modeCamera == MODE_CAMERA_FRONT) {
            cameraId = getCameraId(CameraInfo.CAMERA_FACING_FRONT);
            MLog.d(TAG, "" + cameraId);
            if (cameraId == -1) {
                MLog.d(TAG, "there is no camera");
            }
            mCamera = Camera.open(cameraId);
        } else {
            cameraId = 0;
            mCamera = Camera.open();
        }

        mParameters = mCamera.getParameters();


        try {
            applyParameters();
            mCamera.setPreviewTexture(surface);

        } catch (IOException exception) {
            mCamera.release();
        }


        setCameraDisplayOrientation(cameraId, mCamera);
        mCamera.startPreview();

    }


    public interface CallbackData {
        void event(byte[] data, Camera camera);
    }

    public void addCallbackData(CallbackData callbackData) {
        MLog.d(TAG, "qq 1");
        this.callbackData = callbackData;
        startOnFrameProcessing();
    }

    public interface CallbackBmp {
        void event(Bitmap bmp);
    }

    public void addCallbackBmp(CallbackBmp callbackBmp) {
        this.callbackBmp = callbackBmp;
        startOnFrameProcessing();
    }

    public interface CallbackStream {
        void event(String out);
    }

    public void addCallbackStream(CallbackStream callbackStream) {
        this.callbackStream = callbackStream;
        startOnFrameProcessing();
    }

    public void applyParameters() {
        mCamera.setParameters(mParameters);
    }

    public void setPreviewSize(int w, int h) {
        mParameters.setPreviewSize(w, h);
        applyParameters();
    }

    public void setPictureSize(int w, int h) {
        mParameters.setPictureSize(320, 240);
        applyParameters();
    }

    public void setColorEffect(String mode) {
        mParameters.setColorEffect(mode);
        applyParameters();
    }


    public void getProperties() {

        Log.i(TAG, "Supported Exposure Modes:" + mParameters.get("exposure-mode-values"));
        Log.i(TAG, "Supported White Balance Modes:" + mParameters.get("whitebalance-values"));
        Log.i(TAG, "Supported White Balance Modes:" + mParameters.get("whitebalance-values"));
        Log.i(TAG, "Supported White Balance Modes:" + mParameters.get("whitebalance"));
        Log.i(TAG, "Supported White Balance Modes:" + mParameters.get("exposure"));
    }


    public void startOnFrameProcessing() {
        if (frameProcessing == false) {
            frameProcessing = true;
            Camera.Parameters parameters = mCamera.getParameters();
            int format = parameters.getPreviewFormat();

            mCamera.setPreviewCallback(new PreviewCallback() {

                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    MLog.d(TAG, "onNewFrame");


                    if (callbackData != null) {
                        callbackData.event(data, camera);
                    }
                    if (callbackBmp != null) {
                        Camera.Parameters parameters = camera.getParameters();
                        int width = parameters.getPreviewSize().width;
                        int height = parameters.getPreviewSize().height;

                        // get support preview format
                        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        // maybe pass the out to the callbacks and do each compression there?
                        yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);
                        byte[] bytes = out.toByteArray();

                        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
                        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;

                        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bitmap_options);
                        callbackBmp.event(bitmap);
                    }
                    if (callbackStream != null) {
                        Camera.Parameters parameters = camera.getParameters();
                        int width = parameters.getPreviewSize().width;
                        int height = parameters.getPreviewSize().height;

                        // get support preview format
                        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);
                        ByteArrayOutputStream out = new ByteArrayOutputStream();

                        String encodedImage = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
                        callbackStream.event(encodedImage);
                    }
                }
            });
        }
    }

    public void addFaceDetector() {

    }

    protected void stopCamera() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

    }


    File dir = null;
    File file = null;
    String fileName;

    public String takePic(final String path) {
        // final CountDownLatch latch = new CountDownLatch(1);

        AudioManager mgr = (AudioManager) mAppRunner.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);

        SoundPool soundPool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 0);
        // final int shutterSound = soundPool.load(this, R.raw.camera_click, 0);

        mCamera.takePicture(null, null, new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);


                // soundPool.play(shutterSound, 1f, 1f, 0, 0, 1);

                FileOutputStream outStream = null;
                try {

                    file = new File(path);

                    outStream = new FileOutputStream(file);
                    outStream.write(data);
                    outStream.flush();
                    outStream.close();
                    MLog.d(TAG, "onPictureTaken - wrote bytes: " + data.length);

                    for (CameraListener l : listeners) {
                        l.onPicTaken();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                }

                MLog.d(TAG, "onPictureTaken - jpeg");

                camera.startPreview();
                // latch.countDown();

            }
        });

		/*
         * try { latch.await(); } catch (InterruptedException e1) { // TODO
		 * Auto-generated catch block e1.printStackTrace(); }
		 */

        return fileName;

    }

    private MediaRecorder recorder;
    private boolean recording = false;

    public void stopRecordingVideo() {
        recorder.stop();
        recorder.release();
        mCamera.lock();
        recording = false;
        MLog.d(TAG, "Recording Stopped");
    }

    public void recordVideo(String file) {

        MLog.d(TAG, "mCamera " + mCamera);
        // Camera.Parameters parameters = mCamera.getParameters();
        // MLog.d(TAG, "parameters " + parameters);
        // parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
        // mCamera.setParameters(parameters);

        recorder = new MediaRecorder();
        MLog.d(TAG, "recorder " + recorder);

        mCamera.unlock();
        recorder.setCamera(mCamera);
        // recorder.setVideoFrameRate(15);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
        // mCamera.getParameters().
        CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(profile);
        MLog.d(TAG, "setting profile ");
        // CamcorderProfile cpHigh = CamcorderProfile.get(cameraId,
        // CamcorderProfile.QUALITY_HIGH);
        // MLog.d(TAG, "profile set " + cpHigh);
        // recorder.setProfile(cpHigh);
        MLog.d(TAG, "profile set 1 " + file);
        // recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setOutputFile(file);
        MLog.d(TAG, "profile set 2");
        // recorder.setMaxDuration(5000 * 1000); // 50 seconds
        // recorder.setMaxFileSize(5000 * 1000000); // Approximately 5 megabytes

        // CamcorderProfile camcorderProfile =
        // CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        // recorder.setProfile(camcorderProfile);

        // recorder.setPreviewDisplay(mTextureView.getSurfaceTexture());
        // recorder.setPreviewDisplay(holder.getSurface());
        // recorder.setP

        try {
            MLog.d(TAG, "preparing ");
            recorder.prepare();
            MLog.d(TAG, "prepare ");

        } catch (IllegalStateException e) {
            e.printStackTrace();
            // finish();
        } catch (IOException e) {
            e.printStackTrace();
            // finish();
        }

        if (recording) {
            stopRecordingVideo();
            // Let's initRecorder so we can record again
            // prepareRecorder();
        } else {
            recording = true;
            recorder.start();
            MLog.d(TAG, "Recording Started");
        }

    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "photo taken");

        _fileName = TimeUtils.getCurrentTime() + ".jpg";
        _path = _rootPath + _fileName;

        new File(_rootPath).mkdirs();
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        // Uri imageFileUri = getContentResolver().insert(
        // Media.EXTERNAL_CONTENT_URI, new ContentValues());

        try {
            OutputStream imageFileOS = mAppRunner.getAppContext().getContentResolver().openOutputStream(outputFileUri);
            imageFileOS.write(data);
            imageFileOS.flush();
            imageFileOS.close();

        } catch (FileNotFoundException e) {
            Toast t = Toast.makeText(mAppRunner.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT);
            t.show();
        } catch (IOException e) {
            Toast t = Toast.makeText(mAppRunner.getAppContext(), e.getMessage(), Toast.LENGTH_SHORT);
            t.show();
        }

        camera.startPreview();
        camera.release();

        AudioManager mgr = (AudioManager) mAppRunner.getAppContext().getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);

        // WindowManager.LayoutParams params = getWindow().getAttributes();
        // params.flags |= LayoutParams.FLAG_KEEP_SCREEN_ON;
        // params.screenBrightness = 0;
        // getWindow().setAttributes(params);

        Log.i(TAG, "photo saved");

        // this.finish();

    }

    @SuppressLint("NewApi")
    private int getCameraId(int cameraId) {
        CameraInfo ci = new CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == cameraId) { //CameraInfo.CAMERA_FACING_FRONT) {
                MLog.d(TAG, "returning " + i);
                return i;
                //} else (ci.facing == CameraInfo.CAMERA_FACING_BACK) {
                //    return i;
            }
        }
        return -1; // No front-facing camera found
    }

    public boolean isFlashAvailable() {
        boolean b = mAppRunner.getAppContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        return b;
    }

    public void turnOnFlash(boolean b) {
        MLog.d(TAG, "qq " + b + " " + isFlashAvailable());
        if (isFlashAvailable()) {
            if (b) mParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            else mParameters.setFlashMode(Parameters.FLASH_MODE_OFF);

            applyParameters();
        }
    }

    public boolean hasAutofocus() {
        return mAppRunner.getAppContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_AUTOFOCUS);
    }


    public void focus(final ReturnInterface callbackfn) {
        if (hasAutofocus()) {
            if (true) {
                mCamera.autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        if (callbackfn != null) callbackfn.event(null);
                    }
                });
            } else {
                mCamera.cancelAutoFocus();
            }
        }
    }

    public void addListener(CameraListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CameraListener listener) {
        listeners.remove(listener);
    }

    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);

        WindowManager windowManager = (WindowManager) mAppRunner.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        cameraRotation = result;
        camera.setDisplayOrientation(result);
    }


    static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & (yuv420sp[yp])) - 16;
                if (y < 0) {
                    y = 0;
                }
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) {
                    r = 0;
                } else if (r > 262143) {
                    r = 262143;
                }
                if (g < 0) {
                    g = 0;
                } else if (g > 262143) {
                    g = 262143;
                }
                if (b < 0) {
                    b = 0;
                } else if (b > 262143) {
                    b = 262143;
                }

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

}