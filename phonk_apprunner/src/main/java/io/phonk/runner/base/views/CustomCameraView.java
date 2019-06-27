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

package io.phonk.runner.base.views;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.TextureView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import io.phonk.runner.base.utils.MLog;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
@SuppressLint("NewApi")
public class CustomCameraView extends TextureView {

    public static final int MODE_COLOR_BW = 0;
    public static final int MODE_COLOR_COLOR = 1;
    public static final int MODE_CAMERA_FRONT = 2;
    public static final int MODE_CAMERA_BACK = 3;
    int modeColor;
    int modeCamera;

    protected String TAG = "Camera";

    // camera
    protected Camera mCamera;

    // saving info
    private String _rootPath;
    private String _fileName;
    private String _path;

    private final Vector<CameraListener> listeners;
    private final Context c;

    public interface CameraListener {

        void onPicTaken();

        void onVideoRecorded();

    }

    public CustomCameraView(Context context, int id) {
        super(context);
        c = context;
        modeCamera = id;
        listeners = new Vector<CameraListener>();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // takePic();
            }
        });

        this.setSurfaceTextureListener(new SurfaceTextureListener() {

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

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
                    int cameraId = getFrontCameraId();
                    MLog.d(TAG, "" + cameraId);
                    if (cameraId == -1) {
                        MLog.d(TAG, "there is no camera");
                    }
                    mCamera = Camera.open(cameraId);

                } else {
                    mCamera = Camera.open();
                }

                try {

                    Camera.Parameters parameters = mCamera.getParameters();

                    if (modeColor == MODE_COLOR_BW && parameters.getSupportedColorEffects() != null) {
                        // parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
                    }

                    if (c.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                        // parameters.set("orientation", "PORTRAIT"); // For
                        // Android Version 2.2 and above
                        mCamera.setDisplayOrientation(90);
                        // For Android Version 2.0 and above
                        parameters.setRotation(90);
                    } else if (modeCamera == MODE_CAMERA_FRONT) {

                    }

                    List<Size> supportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
                    parameters.setPreviewSize(supportedPreviewSizes.get(0).width, supportedPreviewSizes.get(0).height);

                    mCamera.setParameters(parameters);
                    mCamera.setPreviewTexture(surface);
                } catch (IOException exception) {
                    mCamera.release();
                }

                mCamera.startPreview();

            }
        });

    }

    protected void stopCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopCamera();
    }

    File dir = null;
    File file = null;
    String fileName;

    public String takePic(final String path) {
        // final CountDownLatch latch = new CountDownLatch(1);

        AudioManager mgr = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        mgr.setStreamMute(AudioManager.STREAM_SYSTEM, true);

        // SoundPool soundPool = new SoundPool(1,
        // AudioManager.STREAM_NOTIFICATION, 0);
        // final int shutterSound = soundPool.load(this, R.raw.camera_click, 0);


        // System.gc();
        mCamera.setPreviewCallback(null);
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

    public void recordVideo(String file) {

        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
        mCamera.setParameters(parameters);

        recorder = new MediaRecorder();

        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        recorder.setProfile(cpHigh);
        recorder.setOutputFile(file);
        recorder.setMaxDuration(5000 * 1000); // 50 seconds
        recorder.setMaxFileSize(5000 * 1000000); // Approximately 5 megabytes

        // CamcorderProfile camcorderProfile =
        // CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        // recorder.setProfile(camcorderProfile);

        // recorder.setPreviewDisplay(mTextureView.getSurfaceTexture());
        // recorder.setPreviewDisplay(holder.getSurface());

        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            // finish();
        } catch (IOException e) {
            e.printStackTrace();
            // finish();
        }

        if (recording) {
            recorder.stop();
            recorder.release();
            recording = false;
            MLog.d(TAG, "Recording Stopped");
            // Let's initRecorder so we can record again
            // prepareRecorder();
        } else {
            recording = true;
            recorder.start();
            MLog.d(TAG, "Recording Started");
        }

    }

    // @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    // public void onPictureTaken(byte[] data, Camera camera) {
    // Log.i(TAG, "photo taken");
    //
    // _fileName = TimeUtils.getCurrentTime() + ".jpg";
    // _path = _rootPath + _fileName;
    //
    // new File(_rootPath).mkdirs();
    // File file = new File(_path);
    // Uri outputFileUri = Uri.fromFile(file);
    //
    // // Uri imageFileUri = getContentResolver().insert(
    // // Media.EXTERNAL_CONTENT_URI, new ContentValues());
    //
    // try {
    // OutputStream imageFileOS =
    // c.getContentResolver().openOutputStream(outputFileUri);
    // imageFileOS.write(data);
    // imageFileOS.flush();
    // imageFileOS.close();
    //
    // } catch (FileNotFoundException e) {
    // Toast t = Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT);
    // t.show();
    // } catch (IOException e) {
    // Toast t = Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT);
    // t.show();
    // }
    //
    // camera.startPreview();
    // camera.release();
    //
    // AudioManager mgr = (AudioManager)
    // c.getSystemService(Context.AUDIO_SERVICE);
    // mgr.setStreamMute(AudioManager.STREAM_SYSTEM, false);
    //
    // Log.i(TAG, "photo saved");
    // }

    @SuppressLint("NewApi")
    private int getFrontCameraId() {
        CameraInfo ci = new CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) {
                return i;
            }
        }
        return -1; // No front-facing camera found
    }

    public void addListener(CameraListener listener) {
        listeners.add(listener);
    }

    public void removeListener(CameraListener listener) {
        listeners.remove(listener);
    }

}
