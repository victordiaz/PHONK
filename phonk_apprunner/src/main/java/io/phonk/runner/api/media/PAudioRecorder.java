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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.RequiresApi;

import io.phonk.runner.api.ProtoBase;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;

public class PAudioRecorder extends ProtoBase {

    MediaRecorder recorder;
    ProgressDialog mProgressDialog;
    boolean showProgress = false;

    public PAudioRecorder(AppRunner appRunner) {
        super(appRunner);

        recorder = new MediaRecorder();
    }

    private void init() {
        // ContentValues values = new ContentValues(3);
        // values.put(MediaStore.MediaColumns.TITLE, fileName);
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // recorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
        recorder.setAudioEncodingBitRate(96000);
        recorder.setAudioSamplingRate(44100);
    }

    @ProtoMethod(description = "Starts recording", example = "")
    @ProtoMethodParam(params = {"showProgressBoolean"})
    public PAudioRecorder record(String fileName) {
        init();

        recorder.setOutputFile(getAppRunner().getProject().getFullPathForFile(fileName));
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (showProgress && getActivity() != null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setTitle("Record!");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Stop recording",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, final int whichButton) {
                            mProgressDialog.dismiss();
                            stop();
                        }
                    });

            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface p1) {
                    stop();
                }
            });
            mProgressDialog.show();
        }

        recorder.start();

        return this;
    }

    public int amplitude() {
        return recorder.getMaxAmplitude();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PAudioRecorder pause() {
        recorder.pause();

        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PAudioRecorder resume() {
        recorder.resume();

        return this;
    }

    @ProtoMethod(description = "Stops recording", example = "")
    @ProtoMethodParam(params = {""})
    public void stop() {
        recorder.stop();
    }

    @Override
    public void __stop() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        } catch (Exception e) {

        }

        if (showProgress && getActivity() != null) {
            mProgressDialog.dismiss();
            showProgress = false;
        }
    }
}
