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

package io.phonk.runner.apprunner.api.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import io.phonk.runner.apprunner.api.widgets.PImage;
import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.Image;
import io.phonk.runner.base.utils.MLog;

public class PImageHelper {

    private static final java.lang.String TAG = PImageHelper.class.getSimpleName();

    /**
     * This class lets us set images from a file asynchronously
     */
    public static class SetImageTask extends AsyncTask<String, Void, Object> {
        private PImage image;
        private String imagePath;
        private String fileExtension;

        public SetImageTask(PImage image) {
            this.image = image;
        }

        @Override
        protected Object doInBackground(String... paths) {
            imagePath = paths[0];
            this.fileExtension = FileIO.getFileExtension(imagePath);
            Object ret = null;

            // download from web
            if (imagePath.startsWith("http")) {
                try {
                    InputStream in = new java.net.URL(imagePath).openStream();
                    ret = BitmapFactory.decodeStream(in);

                } catch (Exception e) {
                    MLog.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                // read from file
            } else {
                File imgFile = new File(imagePath);

                if (imgFile.exists()) {
                    if (fileExtension.equals("svg")) {
                        File file = new File(imagePath);
                        FileInputStream fileInputStream = null;
                        try {
                            fileInputStream = new FileInputStream(file);
                            SVG svg = new SVGBuilder().readFromInputStream(fileInputStream).build();
                            ret = svg.getDrawable();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ret = Image.loadBitmap(imagePath);
                    }
                }
            }
            return ret;
        }


        @Override
        protected void onPostExecute(Object result) {
            MLog.d(TAG, "image" + image);
            image.mode(null);
            // image.setScaleType(ImageView.ScaleType.FIT_XY);

            if (fileExtension.equals("svg")) {
                MLog.d("svg", "is SVG 2 " + result);
                image.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                image.setImageDrawable((Drawable) result);
            } else {
                image.setImageBitmap((Bitmap) result);
            }

        }
    }

}
