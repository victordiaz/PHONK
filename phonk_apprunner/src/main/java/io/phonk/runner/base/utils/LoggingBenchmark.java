/*
 * Part of Phonk http://www.phonk.io
 * A prototyping platform for Android devices
 *
 * Copyright (C) 2013 - 2017 Victor Diaz Barrales @victordiaz (Protocoder)
 * Copyright (C) 2017 - Victor Diaz Barrales @victordiaz (Phonk)
 *
 * Phonk is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Phonk is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Phonk. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package io.phonk.runner.base.utils;

import android.util.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple class for measuring execution time in various contexts.
 */
public class LoggingBenchmark {
    private static final boolean ENABLED = false;

    private final String tag;

    private final Map<String, Long> totalImageTime = new HashMap<>();
    private final Map<String, Map<String, Long>> stageTime = new HashMap<>();

    private final Map<String, Map<String, Long>> stageStartTime = new HashMap<>();

    public LoggingBenchmark(String tag) {
        this.tag = tag;
    }

    public void startStage(String imageId, String stageName) {
        if (!ENABLED) {
            return;
        }

        Map<String, Long> stageStartTimeForImage;
        if (!stageStartTime.containsKey(imageId)) {
            stageStartTimeForImage = new HashMap<>();
            stageStartTime.put(imageId, stageStartTimeForImage);
        } else {
            stageStartTimeForImage = stageStartTime.get(imageId);
        }

        long timeNs = System.nanoTime();
        stageStartTimeForImage.put(stageName, timeNs);
    }

    public void endStage(String imageId, String stageName) {
        if (!ENABLED) {
            return;
        }

        long endTime = System.nanoTime();
        long startTime = stageStartTime.get(imageId).get(stageName);
        long duration = endTime - startTime;

        if (!stageTime.containsKey(imageId)) {
            stageTime.put(imageId, new HashMap<>());
        }
        stageTime.get(imageId).put(stageName, duration);

        if (!totalImageTime.containsKey(imageId)) {
            totalImageTime.put(imageId, 0L);
        }
        totalImageTime.put(imageId, totalImageTime.get(imageId) + duration);
    }

    public void finish(String imageId) {
        if (!ENABLED) {
            return;
        }

        StringBuilder msg = new StringBuilder();
        for (Map.Entry<String, Long> entry : stageTime.get(imageId).entrySet()) {
            msg.append(String.format(Locale.getDefault(),
                    "%s: %.2fms | ", entry.getKey(), entry.getValue() / 1.0e6));
        }

        msg.append(String.format(Locale.getDefault(),
                "TOTAL: %.2fms", totalImageTime.get(imageId) / 1.0e6));
        Log.d(tag, msg.toString());
    }
}
