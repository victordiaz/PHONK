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
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.mozilla.javascript.EvaluatorException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.media.AutoFitTextureView;
import io.phonk.runner.apprunner.interpreter.AppRunnerInterpreter;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.utils.TimeUtils;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CameraTexture extends AutoFitTextureView implements TextureView.SurfaceTextureListener {

    public static final int MODE_COLOR_COLOR = 3;
    private static int mCameraRotation = 0;

    String modeColor;
    String modeCamera;
    private int cameraId;

    protected String TAG = CameraTexture.class.getSimpleName();

    AppRunner mAppRunner;

    // camera
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
    private OnReadyCallback mOnReadyCallback;

    public interface CameraListener {
        void onPicTaken();
        void onVideoRecorded();
    }

    public CameraTexture(AppRunner appRunner, String camera, String colorMode) {
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
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        // mCamera.stopPreview();
        // mCamera.release();
        return true;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (modeCamera.equals("front")) {
            cameraId = getCameraId(CameraInfo.CAMERA_FACING_FRONT);
            MLog.d(TAG, "" + cameraId);
            if (cameraId == -1) {
                MLog.d(TAG, "there is no camera");
            }
            mCamera = Camera.open(cameraId);
        } else if (modeCamera.equals("back")){
            cameraId = 0;
            mCamera = Camera.open();
        }
        mParameters = mCamera.getParameters();

        // SizePair siz = generateValidPreviewSize(camera, 480, 640);
        // width = siz.mPicture.getWidth();
        // height = siz.mPicture.getHeight();
        // mParameters.setPreviewSize(640, 480);

        try {
            applyParameters();
            mCamera.setPreviewTexture(surface);
        } catch (IOException exception) {
            mCamera.release();
        }

        setCameraDisplayOrientation(cameraId, mCamera);
        mCamera.startPreview();
        mOnReadyCallback.event();
    }

    public interface CallbackData {
        void event(byte[] data, Camera camera);
    }

    public void addCallbackData(CallbackData callbackData) {
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
        mParameters.setPictureSize(w, h);
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

            mCamera.setPreviewCallback((data, camera) -> {
                if (callbackData != null) {
                    callbackData.event(data, camera);
                }
                if (callbackBmp != null) {
                    Parameters parameters1 = camera.getParameters();

                    List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
                    for (Camera.Size size : sizes) {
                        MLog.d("wewe", "sizes " + size.width + " " + size.height);
                    }

                    List<Integer> formats = camera.getParameters().getSupportedPreviewFormats();
                    for (Integer integer : formats) {
                        MLog.d(TAG, "formats " + integer);
                    }

                    int width = parameters1.getPreviewSize().width;
                    int height = parameters1.getPreviewSize().height;
                    // MLog.d("wewe", "-> " + width + " " + height);

                    // get support preview format
                    YuvImage yuv = new YuvImage(data, parameters1.getPreviewFormat(), width, height, null);
                    if (yuv == null) return;

                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    // maybe pass the output to the callbacks and do each compression there?
                    yuv.compressToJpeg(new Rect(0, 0, width, height), 100, out);

                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    matrix.postScale((float) 0.5, (float) 0.5);
                    byte[] bytes = out.toByteArray();

                    BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
                    bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bitmap_options);
                    Bitmap fbitmap = Bitmap.createBitmap(bitmap, 0, 0, 300, 300, matrix, true);

                    // MLog.d("qq", "img " + bitmap.getWidth() + " " + bitmap.getHeight());
                    // MLog.d("qq", "resized img " + fbitmap.getWidth() + " " + fbitmap.getHeight());
                    callbackBmp.event(fbitmap);
                }
                if (callbackStream != null) {
                    Parameters parameters1 = camera.getParameters();
                    int width = parameters1.getPreviewSize().width;
                    int height = parameters1.getPreviewSize().height;

                    // CameraTexture.this.setAspectRatio(height, width);


                    // get support preview format
                    YuvImage yuv = new YuvImage(data, parameters1.getPreviewFormat(), width, height, null);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    String encodedImage = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
                    callbackStream.event(encodedImage);
                }
            });
        }
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

        mCamera.takePicture(null, null, (data, camera) -> {

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
        });

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

    public void flash(boolean b) {
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
                mCamera.autoFocus((success, camera) -> {
                    if (callbackfn != null) callbackfn.event(null);
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
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
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

        mCameraRotation = result;
        MLog.d("wewe", "" + mCameraRotation);
        camera.setDisplayOrientation(mCameraRotation);
    }


    public void addOnReadyCallback(OnReadyCallback callback) {
        mOnReadyCallback = callback;
    }

    public interface OnReadyCallback {
        void event();
    }


    private static final double MAX_ASPECT_DISTORTION = 0.15;
    private static final float ASPECT_RATIO_TOLERANCE = 0.01f;

    //desiredWidth and desiredHeight can be the screen size of mobile device
    private static SizePair generateValidPreviewSize(Camera camera, int desiredWidth,
                                                     int desiredHeight) {
        Camera.Parameters parameters = camera.getParameters();
        double screenAspectRatio = desiredWidth / (double) desiredHeight;
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        SizePair bestPair = null;
        double currentMinDistortion = MAX_ASPECT_DISTORTION;
        for (Camera.Size previewSize : supportedPreviewSizes) {
            float previewAspectRatio = (float) previewSize.width / (float) previewSize.height;
            for (Camera.Size pictureSize : supportedPictureSizes) {
                float pictureAspectRatio = (float) pictureSize.width / (float) pictureSize.height;
                if (Math.abs(previewAspectRatio - pictureAspectRatio) < ASPECT_RATIO_TOLERANCE) {
                    SizePair sizePair = new SizePair(previewSize, pictureSize);

                    boolean isCandidatePortrait = previewSize.width < previewSize.height;
                    int maybeFlippedWidth = isCandidatePortrait ? previewSize.width : previewSize.height;
                    int maybeFlippedHeight = isCandidatePortrait ? previewSize.height : previewSize.width;
                    double aspectRatio = maybeFlippedWidth / (double) maybeFlippedHeight;
                    double distortion = Math.abs(aspectRatio - screenAspectRatio);
                    if (distortion < currentMinDistortion) {
                        currentMinDistortion = distortion;
                        bestPair = sizePair;
                    }
                    break;
                }
            }
        }

        return bestPair;
    }


    private static class SizePair {
        private Size mPreview;
        private Size mPicture;

        public SizePair(Camera.Size previewSize, Camera.Size pictureSize) {
            mPreview = new Size(previewSize.width, previewSize.height);
            if (pictureSize != null) {
                mPicture = new Size(pictureSize.width, pictureSize.height);
            }
        }

        public Size previewSize() {
            return mPreview;
        }

        @SuppressWarnings("unused")
        public Size pictureSize() {
            return mPicture;
        }
    }
}
