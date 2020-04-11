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

package io.phonk.runner.api.media;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

import io.phonk.runner.base.utils.MLog;

/** Utility class for manipulating images.
 *
 * Copy-pasted from TFLite object detection example.
 */
public class ImageUtils {
  private static final String TAG = ImageUtils.class.getSimpleName();

  // This value is 2 ^ 18 - 1, and is used to clamp the RGB values before their ranges
  // are normalized to eight bits.
  static final int MAX_CHANNEL_VALUE = 262143;

  public static void convertYUV420SPToARGB8888(byte[] input, int width, int height, int[] output) {
    final int frameSize = width * height;
    for (int j = 0, yp = 0; j < height; j++) {
      int uvp = frameSize + (j >> 1) * width;
      int u = 0;
      int v = 0;

      for (int i = 0; i < width; i++, yp++) {
        int y = 0xff & input[yp];
        if ((i & 1) == 0) {
          v = 0xff & input[uvp++];
          u = 0xff & input[uvp++];
        }

        output[yp] = yuv2Rgb(y, u, v);
      }
    }
  }

  private static int yuv2Rgb(int y, int u, int v) {
    // Adjust and check YUV values
    y = (y - 16) < 0 ? 0 : (y - 16);
    u -= 128;
    v -= 128;

    // This is the floating point equivalent. We do the conversion in integer
    // because some Android devices do not have floating point in hardware.
    // nR = (int)(1.164 * nY + 2.018 * nU);
    // nG = (int)(1.164 * nY - 0.813 * nV - 0.391 * nU);
    // nB = (int)(1.164 * nY + 1.596 * nV);
    int y1192 = 1192 * y;
    int r = (y1192 + 1634 * v);
    int g = (y1192 - 833 * v - 400 * u);
    int b = (y1192 + 2066 * u);

    // Clipping RGB values to be inside boundaries [ 0 , MAX_CHANNEL_VALUE ]
    r = r > MAX_CHANNEL_VALUE ? MAX_CHANNEL_VALUE : Math.max(r, 0);
    g = g > MAX_CHANNEL_VALUE ? MAX_CHANNEL_VALUE : Math.max(g, 0);
    b = b > MAX_CHANNEL_VALUE ? MAX_CHANNEL_VALUE : Math.max(b, 0);

    return 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
  }

  public static void convertYUV420ToARGB8888(
      byte[] yData,
      byte[] uData,
      byte[] vData,
      int width,
      int height,
      int yRowStride,
      int uvRowStride,
      int uvPixelStride,
      int[] out) {
    int yp = 0;
    for (int j = 0; j < height; j++) {
      int pY = yRowStride * j;
      int pUV = uvRowStride * (j >> 1);

      for (int i = 0; i < width; i++) {
        int uvOffset = pUV + (i >> 1) * uvPixelStride;

        out[yp++] = yuv2Rgb(0xff & yData[pY + i], 0xff & uData[uvOffset], 0xff & vData[uvOffset]);
      }
    }
  }

  public static void saveBitmap(final Bitmap bitmap, final String filename) {
    final String root =
            Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "tensorflow";
    MLog.d(TAG, "Saving " + bitmap.getWidth() + " " + bitmap.getHeight() + " in " + root);
    final File myDir = new File(root);

    if (!myDir.mkdirs()) {
      MLog.d(TAG, "Make dir failed");
    }

    final String fname = filename;
    final File file = new File(myDir, fname);
    if (file.exists()) {
      file.delete();
    }
    try {
      final FileOutputStream out = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 99, out);
      out.flush();
      out.close();
    } catch (final Exception e) {
      MLog.e(TAG, e.toString());
    }
  }
}
