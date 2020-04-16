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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;

import androidx.camera.core.ImageProxy;

import org.tensorflow.lite.examples.transfer.api.TransferLearningModel;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.LoggingBenchmark;
import io.phonk.runner.base.utils.MLog;

public class LearnImages {

    private static String TAG = LearnImages.class.getSimpleName();

    private final AppRunner mAppRunner;
    private TransferLearningModelWrapper mTlModel;
    private boolean mIsAnalyzerRunning = false;
    public Handler mHandler = new Handler(Looper.getMainLooper());

    // When the user presses the "add sample" button for some class,
    // that class will be added to this queue. It is later extracted by
    // InferenceThread and processed.
    private final ConcurrentLinkedQueue<Frame> mLearnFrames = new ConcurrentLinkedQueue<Frame>();
    private final ConcurrentLinkedQueue<Frame> mCameraFrames = new ConcurrentLinkedQueue<Frame>();

    private final LoggingBenchmark mInferenceBenchmark = new LoggingBenchmark("InferenceBench");
    private String mNextFrameCategory = null;

    LearnImages(AppRunner appRunner) {
        mAppRunner = appRunner;
    }

    public void learnFrameAsCategory(String className) {
        mNextFrameCategory = className;
    }

    public void addCameraFrame(byte[] data, Camera camera) {
        Frame f = new Frame(data, camera, "-1");

        // if there is a frame to learn we add it to the queue and return inmediately
        if (mNextFrameCategory != null) {
            f.category = mNextFrameCategory;
            mLearnFrames.add(f);
            mNextFrameCategory = null;
            return;
        }

        MLog.d("qq", "mCameraFramesSize: " + mCameraFrames.size());
        if (mCameraFrames.size() < 10) {
            MLog.d("qq", "add frame");
            mCameraFrames.add(f);
        } else {
            MLog.d("qq", "drop frame");
            mCameraFrames.poll();
        }
    }

    public void start() {
        mTlModel = new TransferLearningModelWrapper(mAppRunner.getAppContext());
        mIsAnalyzerRunning = true;

        Thread t1 = new Thread(() -> {
            while (mIsAnalyzerRunning) {
                inferenceAnalyzer();
            }
        });
        t1.start();
    }

    public void stop() {
        disableTraining();
        mTlModel.close();
        mTlModel = null;

        // stop thread
        mIsAnalyzerRunning = false;
    }

    public void inferenceAnalyzer() {
        MLog.d("qq", "analyzing");

        // Learn frames size
        Frame learnFrame = mLearnFrames.poll();
        if (learnFrame != null) {
            MLog.d(TAG, "learning... " + learnFrame.category + " / " + mLearnFrames.size());

            // mInferenceBenchmark.startStage(imageId, "addSample");
            try {
                mTlModel.addSample(getRGBFromFrame(learnFrame), learnFrame.category).get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Failed to add sample to model", e.getCause());
            } catch (InterruptedException e) {
                // no-op
            }
            // mInferenceBenchmark.endStage(imageId, "addSample");
        }

        // predict
        Frame cameraFrame = mCameraFrames.poll();
        if (cameraFrame != null) {
            MLog.d(TAG, "predicting... " + mCameraFrames.size() + " / " + mTlModel.getTrainBatchSize());
            // We don't perform inference when adding samples, since we should be in capture mode
            // at the time, so the inference results are not actually displayed.
            // mInferenceBenchmark.startStage(imageId, "predict");
            TransferLearningModel.Prediction[] predictions = mTlModel.predict(getRGBFromFrame(cameraFrame));
            if (predictions == null) return;
            // mInferenceBenchmark.endStage(imageId, "predict");

            String p = "";
            for (TransferLearningModel.Prediction prediction : predictions) {
                MLog.d(TAG, prediction.getClassName() + " " + prediction.getConfidence());
                p += prediction.getClassName() + " " + prediction.getConfidence() + '\n';
            }

            String finalP = p;
            mHandler.post(() -> mCallback.event(finalP));
        }

        // mInferenceBenchmark.finish(imageId);
    }

    private float[] getRGBFromFrame(Frame f) {
        final String imageId = UUID.randomUUID().toString();
        // mInferenceBenchmark.startStage(imageId, "preprocess");
        Bitmap bmp = cameraDataToBmp(f.data, f.camera);
        float[] rgbImage = prepareCameraImage(bmp, 0);
        // mInferenceBenchmark.endStage(imageId, "preprocess");
        bmp.recycle();

        return rgbImage;
    }

    public void enableTraining() {
        // dont enable training if not enought samples
        if (mTlModel.getNumberSamples() < mTlModel.getTrainBatchSize()) return;

        mTlModel.enableTraining((epoch, loss) -> MLog.d("qq", "" + epoch + " " + loss));
    }

    public void disableTraining() {
        mTlModel.disableTraining();
    }

    Callback mCallback = null;

    public interface Callback {
        void event(String p);

    }

    public void addCallback(Callback callback) {
        this.mCallback = callback;
    }


    class Frame {
        byte[] data;
        Camera camera;
        String category;

        Frame(byte[] data, Camera camera, String category) {
            this.data = data;
            this.camera = camera;
            this.category = category;
        }
    }


    /**
     * Normalizes a camera image to [0; 1], cropping it
     * to size expected by the model and adjusting for camera rotation.
     */
    private static float[] prepareCameraImage(Bitmap bitmap, int rotationDegrees) {
        final int LOWER_BYTE_MASK = 0xFF;

        int modelImageSize = TransferLearningModelWrapper.IMAGE_SIZE;

        Bitmap paddedBitmap = padToSquare(bitmap);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                paddedBitmap, modelImageSize, modelImageSize, true);

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(rotationDegrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0, modelImageSize, modelImageSize, rotationMatrix, false);

        float[] normalizedRgb = new float[modelImageSize * modelImageSize * 3];
        int nextIdx = 0;
        for (int y = 0; y < modelImageSize; y++) {
            for (int x = 0; x < modelImageSize; x++) {
                int rgb = rotatedBitmap.getPixel(x, y);

                float r = ((rgb >> 16) & LOWER_BYTE_MASK) * (1 / 255.f);
                float g = ((rgb >> 8) & LOWER_BYTE_MASK) * (1 / 255.f);
                float b = (rgb & LOWER_BYTE_MASK) * (1 / 255.f);

                normalizedRgb[nextIdx++] = r;
                normalizedRgb[nextIdx++] = g;
                normalizedRgb[nextIdx++] = b;
            }
        }

        return normalizedRgb;
    }

    private static Bitmap padToSquare(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        int paddingX = width < height ? (height - width) / 2 : 0;
        int paddingY = height < width ? (width - height) / 2 : 0;
        Bitmap paddedBitmap = Bitmap.createBitmap(
                width + 2 * paddingX, height + 2 * paddingY, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);
        canvas.drawBitmap(source, paddingX, paddingY, null);
        return paddedBitmap;
    }


    private static Bitmap yuvCameraImageToBitmap(ImageProxy imageProxy) {
        if (imageProxy.getFormat() != ImageFormat.YUV_420_888) {
            throw new IllegalArgumentException(
                    "Expected a YUV420 image, but got " + imageProxy.getFormat());
        }

        ImageProxy.PlaneProxy yPlane = imageProxy.getPlanes()[0];
        ImageProxy.PlaneProxy uPlane = imageProxy.getPlanes()[1];

        int width = imageProxy.getWidth();
        int height = imageProxy.getHeight();

        byte[][] yuvBytes = new byte[3][];
        int[] argbArray = new int[width * height];
        for (int i = 0; i < imageProxy.getPlanes().length; i++) {
            final ByteBuffer buffer = imageProxy.getPlanes()[i].getBuffer();
            yuvBytes[i] = new byte[buffer.capacity()];
            buffer.get(yuvBytes[i]);
        }

        ImageUtils.convertYUV420ToARGB8888(
                yuvBytes[0],
                yuvBytes[1],
                yuvBytes[2],
                width,
                height,
                yPlane.getRowStride(),
                uPlane.getRowStride(),
                uPlane.getPixelStride(),
                argbArray);

        return Bitmap.createBitmap(argbArray, width, height, Bitmap.Config.ARGB_8888);
    }


    private Bitmap cameraDataToBmp(byte[] data, Camera camera) {
        // transform camera data to bmp
        Camera.Parameters parameters = camera.getParameters();
        int width = parameters.getPreviewSize().width;
        int height = parameters.getPreviewSize().height;

        // get support preview format
        // MLog.d("qq", );
        YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        // maybe pass the output to the callbacks and do each compression there?
        yuv.compressToJpeg(new Rect(0, 0, (int) Math.floor(width * 0.2), (int) Math.floor(height * 0.2)), 100, out);
        byte[] bytes = out.toByteArray();
        BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
        bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;
        final Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, bitmap_options);
        return bmp;
    }

}
