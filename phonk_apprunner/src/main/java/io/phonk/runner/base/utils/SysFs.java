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

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SysFs {

    static final String TAG = "SysFS";

    public static boolean write(String filename, String data) {
        try {
            File mpuFile = new File(filename);
            if (mpuFile.canWrite()) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
                bw.write(data);
                bw.close();
            } else {
                Process p = Runtime.getRuntime().exec("su");

                DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                dos.writeBytes("echo " + data + " > " + filename + "\n");
                dos.writeBytes("exit");
                dos.flush();
                dos.close();

                if (p.waitFor() != 0) {
                    Log.i(TAG, "Could not write to " + filename + " (exit: " + p.exitValue() + ")");
                    InputStream in = p.getErrorStream();
                    StringBuilder sb = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        sb.append(line);
                        sb.append('\n');
                        line = bufferedReader.readLine();
                    }
                    Log.i(TAG, sb.toString());
                }
            }
        } catch (IOException ex) {
            Log.i(TAG, "Error: " + ex.getMessage());
            return false;
        } catch (InterruptedException e) {
            Log.i(TAG, "Error writing with root permission");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String read(String filename) {
        String value;
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            value = br.readLine();
            br.close();
        } catch (IOException ex) {
            return "-1";
        }
        return value;
    }

}
