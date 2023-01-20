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

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Build;

import androidx.annotation.RequiresApi;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.ProtoBase;

@PhonkClass
public class PAudioRecorder extends ProtoBase {
    MediaRecorder recorder;
    ProgressDialog mProgressDialog;
    boolean showProgress = false;

    public PAudioRecorder(AppRunner appRunner) {
        super(appRunner);

        recorder = new MediaRecorder();
    }

    @PhonkMethod(description = "Starts recording", example = "")
    @PhonkMethodParam(params = {"showProgressBoolean"})
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
            mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Stop recording", (dialog, whichButton) -> {
                mProgressDialog.dismiss();
                stop();
            });

            mProgressDialog.setOnCancelListener(p1 -> stop());
            mProgressDialog.show();
        }

        recorder.start();

        return this;
    }

    private void init() {
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // recorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
        recorder.setAudioEncodingBitRate(96000);
        recorder.setAudioSamplingRate(44100);
    }

    @PhonkMethod(description = "Stops recording", example = "")
    @PhonkMethodParam(params = {""})
    public void stop() {
        recorder.stop();
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

    @Override
    public void __stop() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
            }
        } catch (Exception ignored) {

        }

        if (showProgress && getActivity() != null) {
            mProgressDialog.dismiss();
            showProgress = false;
        }
    }
}
