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

package io.phonk.runner.apprunner.api.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class PCanvas3 extends TextureView implements TextureView.SurfaceTextureListener {

    private RenderingThread mThread;
    String TAG = "myTextView";

    // set of consturctors needed for the TextureView, most of which we then ignore the parameters anyway.
    public PCanvas3(Context context) {
        super(context);
        Log.v(TAG, "constructor");
        setSurfaceTextureListener(this);  //Required or the TextureView never starts up.
    }

    public PCanvas3(Context context, AttributeSet attrs) {
        super(context);
        Log.v(TAG, "constructor2");
        setSurfaceTextureListener(this);  //Required or the TextureView never starts up.
    }

    public PCanvas3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context);
        Log.v(TAG, "constructor3");
        setSurfaceTextureListener(this);  //Required or the TextureView never starts up.
    }

    public PCanvas3(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context);
        Log.v(TAG, "constructor4");
        setSurfaceTextureListener(this);  //Required or the TextureView never starts up.
    }

    /*
     * TextureView.SurfaceTextureListener overrides below, that start up the drawing thread.
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.v(TAG, "onSurfaceTextureAvailable");
        //We can't override the draw(canvas) function, so we need to access the surface
        //via here and pass it to the thread to draw on it.
        mThread = new RenderingThread(this);
        mThread.start();

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored
        Log.v(TAG, "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.v(TAG, "onSurfaceTextureDestroyed");
        if (mThread != null) mThread.stopRendering();
        return true;

    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        //Log.v(TAG,  "onSurfaceTextureUpdated");  //this is called a lot!
        // Ignored
    }

    /*
     * Thread to draw a green square moving around the textureView.
     */
    class RenderingThread extends Thread {
        private final TextureView mSurface;
        private volatile boolean mRunning = true;

        public RenderingThread(TextureView surface) {
            mSurface = surface;
        }

        @Override
        public void run() {
            float x = 0.0f;
            float y = 0.0f;
            float speedX = 5.0f;
            float speedY = 3.0f;

            Paint paint = new Paint();
            paint.setColor(0xff00ff00);

            while (mRunning && !Thread.interrupted()) {
                final Canvas canvas = mSurface.lockCanvas(null);
                try {
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);
                    //canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
                    canvas.drawRect(x, y, x + 20.0f, y + 20.0f, paint);
                } finally {
                    mSurface.unlockCanvasAndPost(canvas);
                }

                if (x + 20.0f + speedX >= mSurface.getWidth() || x + speedX <= 0.0f) {
                    speedX = -speedX;
                }
                if (y + 20.0f + speedY >= mSurface.getHeight() || y + speedY <= 0.0f) {
                    speedY = -speedY;
                }

                x += speedX;
                y += speedY;

                try {
                    Thread.sleep(15);
                } catch (InterruptedException e) {
                    // Interrupted
                }
            }
        }

        void stopRendering() {
            interrupt();
            mRunning = false;
        }
    }
}
