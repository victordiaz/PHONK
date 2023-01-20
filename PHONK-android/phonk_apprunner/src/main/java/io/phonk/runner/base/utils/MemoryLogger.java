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

import android.os.Debug;
import android.util.Log;

public class MemoryLogger {
    static double lastavail;
    static double initavail;
    static boolean first = true;

    public static void showMemoryStats() {
        showMemoryStats("");
    }

    public static void showMemoryStats(String message) {
        Log.i(
                "memory",
                message + "----------------------------------------------------------------------------------------"
        );
        double nativeUsage = Debug.getNativeHeapAllocatedSize();
        Log.i("memory", "nativeUsage: " + (nativeUsage / 1048576d));
        // current heap size
        double heapSize = Runtime.getRuntime().totalMemory();
        // Log.i("memory", "heapSize: " + (heapSize / 1048576d));
        // amount available in heap
        double heapRemaining = Runtime.getRuntime().freeMemory();
        // Log.i("memory", "heapRemaining: " + (heapRemaining / 1048576d));
        double memoryAvailable = Runtime.getRuntime().maxMemory() - (heapSize - heapRemaining) - nativeUsage;
        Log.i("memory", "memoryAvailable: " + (memoryAvailable / 1048576d));

        if (first) {
            initavail = memoryAvailable;
            first = false;
        }
        if (lastavail > 0) {
            Log.i("memory", "consumed since last: " + ((lastavail - memoryAvailable) / 1048576d));
        }
        Log.i("memory", "consumed total: " + ((initavail - memoryAvailable) / 1048576d));

        lastavail = memoryAvailable;

        Log.i(
                "memory",
                "-----------------------------------------------------------------------------------------------"
        );
    }
}
