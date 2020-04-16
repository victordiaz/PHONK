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

package io.phonk.runner.base.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;

public class ExecuteCmd {

    private static final String TAG = "ExecuteCmd";
    private final String cmd;
    private final ReturnInterface callbackfn;

    private Handler mHandler;
    private Thread mThread;

    public ExecuteCmd(final String cmd, final ReturnInterface callbackfn) {
        this.cmd = cmd;
        this.callbackfn = callbackfn;
    }

    private void initThread() {
        mThread = new Thread(() -> {
        Looper.prepare();

        int count = 0;
        String str = "";
        try {
            //execute the command
            final Process process = Runtime.getRuntime().exec(cmd);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));

            //handler that can stop the thread
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                process.destroy();

                mThread.interrupt();
                mThread = null;

                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                }
            };

            //read the lines
            int i;
            final char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();

            while ((i = reader.read(buffer)) > 0) {
                output.append(buffer, 0, i);

                Handler h = new Handler(Looper.getMainLooper());
                final int finalI = i;
                h.post(() -> {
                    ReturnObject o = new ReturnObject();
                    o.put("value", finalI + " " + String.valueOf(buffer));
                    callbackfn.event(o);
                });

            }
            reader.close();

            str = output.toString();
        } catch (IOException e) {
            // Log.d(TAG, "Error");
            e.printStackTrace();
        }
        Looper.loop();
        });
    }

    public ExecuteCmd start() {
        initThread();
        mThread.start();

        return this;
    }

    @PhonkMethod(description = "stop the running command", example = "")
    public ExecuteCmd stop() {
        Message msg = mHandler.obtainMessage();
        msg.arg1 = 0;
        mHandler.dispatchMessage(msg);
        // mHandler.

        return this;
    }
}