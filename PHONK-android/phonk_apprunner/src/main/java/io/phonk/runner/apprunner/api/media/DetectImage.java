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

package io.phonk.runner.apprunner.api.media;

import android.graphics.Bitmap;

import org.tensorflow.lite.examples.detection.tflite.Classifier;
import org.tensorflow.lite.examples.detection.tflite.TFLiteObjectDetectionAPIModel;

import java.io.IOException;
import java.util.List;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

public class DetectImage {

    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    private final AppRunner mAppRunner;
    LearnImages.Callback mCallback = null;
    private Classifier detector;

    DetectImage(AppRunner appRunner) {
        mAppRunner = appRunner;
    }

    public void start() {
        int cropSize = TF_OD_API_INPUT_SIZE;

        try {
            detector = TFLiteObjectDetectionAPIModel.create(
                    mAppRunner.getAppContext().getAssets(),
                    TF_OD_API_MODEL_FILE,
                    TF_OD_API_LABELS_FILE,
                    TF_OD_API_INPUT_SIZE,
                    TF_OD_API_IS_QUANTIZED
            );
        } catch (final IOException e) {
            e.printStackTrace();
            MLog.e(e.toString(), "Exception initializing classifier!");
        }
    }

    public void detect(Bitmap bitmap) {
        // ImageUtils.saveBitmap(bitmap, "potato.png");

        final List<Classifier.Recognition> results = detector.recognizeImage(bitmap);
        for (Classifier.Recognition result : results) {
            if (result.getConfidence() > 0.5) {
                MLog.d("recognition", result.getId() + " " + " " + result.getTitle() + " " + result.getConfidence());
            }
        }
    }

    public void addCallback(LearnImages.Callback callback) {
        this.mCallback = callback;
    }

    public interface Callback {
        void event(String p);
    }

}
