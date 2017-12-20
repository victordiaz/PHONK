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

package io.phonk.runner.api.widgets;

import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.View;

import io.phonk.runner.api.other.PLooper;
import io.phonk.runner.apidoc.annotation.ProtoField;
import io.phonk.runner.apidoc.annotation.ProtoMethod;
import io.phonk.runner.apidoc.annotation.ProtoMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.Image;
import io.phonk.runner.base.utils.MLog;

import java.util.ArrayList;
import java.util.Collections;


public class PCanvas extends View {

    private static final String TAG = PCanvas.class.getSimpleName();

    protected final AppRunner mAppRunner;

    @ProtoField(description = "Object that contains the layers", example = "")
    public Layers layers;
    public Canvas mCanvas;

    @ProtoField(description = "Canvas width", example = "")
    public int width;

    @ProtoField(description = "Canvas height", example = "")
    public int height;

    @ProtoField(description = "Time interval between draws", example = "")
    private int drawInterval = 35;


    public boolean MODE_CORNER = true;
    public boolean MODE_CENTER = false;

    private RectF mRectf;
    private Paint mPaintBackground;
    public Paint mPaintFill;
    private Paint mPaintStroke;
    protected boolean mAutoDraw = false;
    private boolean fillOn = true;
    private boolean strokeOn = false;
    private boolean mModeCorner = MODE_CORNER;


    // Autodraw
    private PLooper loop;
    int looperSpeed = 35;
    private boolean absoluteMode = false;

    public interface OnSetupCallback { void event (PCanvas c); }
    public interface OnDrawCallback { void event (PCanvas c); }

    public OnSetupCallback setup;
    public OnDrawCallback draw;


    public PCanvas(AppRunner appRunner, int w, int h) {
        this(appRunner);
        width = w;
        height = h;

        absoluteMode = true;
        post_init();
    }

    public PCanvas(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;

        prepareLooper();
        init();
    }

    private void prepareLooper() {
        loop = mAppRunner.pUtil.loop(drawInterval, new PLooper.LooperCB() {
            @Override
            public void event() {
                if (draw != null) {
                    draw.event(PCanvas.this);
                    invalidate();
                }
            }
        });
        loop.speed(looperSpeed);
    }

    private void startLooper() {
        loop.start();
        mAutoDraw = true;
    }

    public void drawInterval(int ms) {
        loop.speed(ms);
    }

    public void init() {
        mRectf = new RectF();
        mPaintBackground = new Paint();
        mPaintFill = new Paint();
        mPaintStroke = new Paint();
        mPaintFill.setAntiAlias(true);
        mPaintStroke.setAntiAlias(true);
    }

    public void post_init() {
        mCanvas = new Canvas();
        layers = new Layers();

        if (setup != null) setup.event(PCanvas.this);
        // startLooper();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        MLog.d(TAG, "new size --> " + w + " " + h);

        if (!absoluteMode) {
            width = w;
            height = h;
            post_init();
            invalidate();
        }
    }

    public void onDraw(OnDrawCallback callback) {
        draw = callback;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // MLog.d(TAG, "onDraw");

        if (!mAutoDraw && draw != null) draw.event(this);
        layers.drawAll(canvas);
    }

    /**
     * Fill and stroke colors
     */
    @ProtoMethod(description = "Clear the canvas", example = "")
    @ProtoMethodParam(params = {})
    public void clear() {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        refresh();
    }

    @ProtoMethod(description = "Sets the filling color", example = "")
    @ProtoMethodParam(params = {"hex"})
    public PCanvas fill(String hex) {
        fill(Color.parseColor(hex));

        return this;
    }

    @ProtoMethod(description = "Sets the filling color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas fill(int r, int g, int b, int alpha) {
        fill(Color.argb(alpha, r, g, b));

        return this;
    }

    @ProtoMethod(description = "Sets the filling color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b"})
    public PCanvas fill(int r, int g, int b) {
        fill(Color.argb(255, r, g, b));

        return this;
    }

    @ProtoMethod(description = "Sets the filling color", example = "")
    @ProtoMethodParam(params = {"hex"})
    public PCanvas fill(int c) {
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(c);
        fillOn = true;

        return this;
    }

    @ProtoMethod(description = "Removes the filling color", example = "")
    @ProtoMethodParam(params = {})
    public void noFill() {
        fillOn = false;
    }

    @ProtoMethod(description = "Sets the stroke color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas stroke(int r, int g, int b, int alpha) {
        stroke(Color.argb(alpha, r, g, b));

        return this;
    }

    @ProtoMethod(description = "Sets the stroke color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b"})
    public PCanvas stroke(int r, int g, int b) {
        stroke(Color.argb(255, r, g, b));

        return this;
    }

    @ProtoMethod(description = "Sets the stroke color", example = "")
    @ProtoMethodParam(params = {"hex"})
    public PCanvas stroke(String c) {
        stroke(Color.parseColor(c));

        return this;
    }

    @ProtoMethod(description = "Sets the stroke color", example = "")
    @ProtoMethodParam(params = {"hex"})
    public PCanvas stroke(int c) {
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(c);
        strokeOn = true;

        return this;
    }

    @ProtoMethod(description = "Removes the stroke color", example = "")
    @ProtoMethodParam(params = {})
    public PCanvas noStroke() {
        strokeOn = false;
        return this;
    }

    @ProtoMethod(description = "Sets a stroke width", example = "")
    @ProtoMethodParam(params = {"width"})
    public PCanvas strokeWidth(float w) {
        mPaintStroke.setStrokeWidth(w);
        return this;
    }

    @ProtoMethod(description = "Sets a stroke cap", example = "")
    @ProtoMethodParam(params = {"cap"})
    public PCanvas strokeCap(String cap) {

        Paint.Cap c = Paint.Cap.SQUARE;

        switch (cap) {
            case "round":
                c = Paint.Cap.ROUND;
                break;

            case "butt":
                c = Paint.Cap.BUTT;

                break;

            case "square":
                c = Paint.Cap.SQUARE;

                break;
        }

        mPaintStroke.setStrokeCap(c);
        return this;
    }

    @ProtoMethod(description = "Change the background color with alpha value", example = "")
    @ProtoMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas background(int r, int g, int b, int alpha) {
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setARGB(alpha, r, g, b);
        mCanvas.drawRect(0, 0, width, height, mPaintBackground);
        refresh();

        return this;
    }

    /**
     * Drawing mode
     */
    @ProtoMethod(description = "Drawing will be done from a corner if true, otherwise from the center", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas mode(boolean mode) {
        mModeCorner = mode;

        return this;
    }

    /**
     * Drawing thingies
     */
    @ProtoMethod(description = "Change the background color", example = "")
    @ProtoMethodParam(params = {"r", "g", "b"})
    public PCanvas background(int r, int g, int b) {
        background(r, g, b, 255);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Draw a point", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas point(float x, float y) {
        mCanvas.drawPoint(x, y, mPaintStroke);
        refresh();
        return this;
    }

    public PCanvas line(float x1, float y1, float x2, float y2) {
        mCanvas.drawLine(x1, y1, x2, y2, mPaintStroke);
        return this;
    }

    @ProtoMethod(description = "Draws a rectangle", example = "")
    @ProtoMethodParam(params = {"x", "y", "width", "height"})
    public PCanvas rect(float x, float y, float width, float height) {
        if (fillOn) mCanvas.drawRect(place(x, y, width, height), mPaintFill);
        if (strokeOn) mCanvas.drawRect(place(x, y, width, height), mPaintStroke);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Draws a rectangle with a given roundness value", example = "")
    @ProtoMethodParam(params = {"x", "y", "width", "height", "rx", "ry"})
    public PCanvas rect(float x, float y, float width, float height, float rx, float ry) {
        if (fillOn) mCanvas.drawRoundRect(place(x, y, width, height), rx, ry, mPaintFill);
        if (strokeOn) mCanvas.drawRoundRect(place(x, y, width, height), rx, ry, mPaintStroke);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Draws and ellipse", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "width", "height"})
    public PCanvas ellipse(float x, float y, float width, float height) {
        if (fillOn) mCanvas.drawOval(place(x, y, width, height), mPaintFill);
        if (strokeOn) mCanvas.drawOval(place(x, y, width, height), mPaintStroke);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Draws an arc", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2", "initAngle", "sweepAngle", "center"})
    public PCanvas arc(float x1, float y1, float x2, float y2, float initAngle, float sweepAngle, boolean center) {
        if (fillOn) mCanvas.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintFill);
        if (strokeOn) mCanvas.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintStroke);
        refresh();

        return this;
    }

    public void drawPath(Path p1) {
        if (fillOn) mCanvas.drawPath(p1, mPaintFill);
        if (strokeOn) mCanvas.drawPath(p1, mPaintStroke);
    }

    public Path path() {
        return new Path();
    }

    public void path(float[][] p) {
        // MLog.d(TAG, "path length " + p.length);
        Path path = new Path();

        path.moveTo(p[0][0], p[0][1]);

        for (int i = 0; i < p.length; i++) {
            // MLog.d(TAG, "setting point in " + p[i][0] + " " + p[i][1]);
            path.lineTo(p[i][0], p[i][1]);
        }

        if (fillOn) mCanvas.drawPath(path, mPaintFill);
        if (strokeOn) mCanvas.drawPath(path, mPaintStroke);
        refresh();
    }

    Path path;
    boolean firstPathPoint = false;
    public void beginPath() {
        path = new Path();
        firstPathPoint = true;
    }

    public void pointPath(float x, float y) {
        if (firstPathPoint) {
            path.moveTo(x, y);
            firstPathPoint = false;
        }
        path.lineTo(x, y);
    }

    public void closePath() {
        if (fillOn) mCanvas.drawPath(path, mPaintFill);
        if (strokeOn) mCanvas.drawPath(path, mPaintStroke);
        refresh();
    }

    /**
     * Text stuff
     */
    @ProtoMethod(description = "Sets the size of the text", example = "")
    @ProtoMethodParam(params = {"size"})
    public PCanvas textSize(int size) {
        mPaintFill.setTextSize(size);
        mPaintStroke.setTextSize(size);
        return this;
    }

    public PCanvas textAlign(String alignTo) {
        Paint.Align alignment = Paint.Align.LEFT;

        switch (alignTo) {
            case "left":
                alignment = Paint.Align.LEFT;
                break;

            case "center":
                alignment = Paint.Align.CENTER;
                break;

            case "right":
                alignment = Paint.Align.RIGHT;
                break;
        }

        mPaintFill.setTextAlign(alignment);
        mPaintStroke.setTextAlign(alignment);

        return this;
    }

    // TODO this only works on api 21
    //public PCanvas textSpacing(float spacing) {
        //mPaintFill.setLetterSpacing(spacing);
        //mPaintStroke.setLetterSpacing(spacing);
    //    return this;
    //}

    @ProtoMethod(description = "Writes text", example = "")
    @ProtoMethodParam(params = {"text", "x", "y"})
    public PCanvas text(String text, float x, float y) {
        if (fillOn) mCanvas.drawText(text, x, y, mPaintFill);
        if (strokeOn) mCanvas.drawText(text, x, y, mPaintStroke);
        refresh();

        return this;
    }

    @ProtoMethod(description = "Draws a text on a path", example = "")
    @ProtoMethodParam(params = {"text", "path", "initOffset", "outOffsett"})
    public PCanvas text(String text, Path path, float initOffset, float outOffset) {
        if (fillOn) mCanvas.drawTextOnPath(text, path, initOffset, outOffset, mPaintFill);
        if (strokeOn) mCanvas.drawTextOnPath(text, path, initOffset, outOffset, mPaintStroke);
        refresh();

        return this;
    }

    public void drawTextCentered(String text){
        int cx = mCanvas.getWidth() / 2;
        int cy = mCanvas.getHeight() / 2;

        Rect textBounds = new Rect();

        mPaintFill.getTextBounds(text, 0, text.length(), textBounds);
        mCanvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), mPaintFill);
    }

    @ProtoMethod(description = "Load an image", example = "")
    @ProtoMethodParam(params = {"imagePath"})
    public Bitmap loadImage(String imagePath) {
        return Image.loadBitmap(mAppRunner.getProject().getFullPathForFile(imagePath));
    }

    @ProtoMethod(description = "Draws an image", example = "")
    @ProtoMethodParam(params = {"bitmap", "x", "y"})
    public PCanvas image(Bitmap bmp, int x, int y) {
        mCanvas.drawBitmap(bmp, x, y, mPaintBackground);
        refresh();
        return this;
    }

    @ProtoMethod(description = "Draws an image", example = "")
    @ProtoMethodParam(params = {"bitmap", "x", "y", "w", "h"})
    public PCanvas image(Bitmap bmp, int x, int y, int w, int h) {
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        RectF rectDst = new RectF(x, y, x + w, y + h);
        mCanvas.drawBitmap(bmp, rectSrc, rectDst, mPaintStroke);
        refresh();

        return this;
    }


    /**
     *
     */
    @ProtoMethod(description = "push", example = "")
    @ProtoMethodParam(params = {})
    public PCanvas push() {
        mCanvas.save();
        return this;
    }

    @ProtoMethod(description = "Rotate given degrees", example = "")
    @ProtoMethodParam(params = {"degrees"})
    public PCanvas rotate(float degrees) {
        mCanvas.rotate(degrees);
        return this;
    }

    @ProtoMethod(description = "Translate", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas translate(float x, float y) {
        mCanvas.translate(x, y);
        return this;
    }

    @ProtoMethod(description = "Skew values 0 - 1)", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas skew(float x, float y) {
        mCanvas.skew(x, y);
        return this;
    }

    @ProtoMethod(description = "Scale", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas scale(float x, float y) {
        mCanvas.scale(x, y);
        return this;
    }

    @ProtoMethod(description = "Restore", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public PCanvas pop() {
        mCanvas.restore();
        return this;
    }

    public Camera getCamera() {
        Camera camera = new Camera();
        return camera;
    }

    public Matrix getMatrix() {
        Matrix matrix = new Matrix();
        getCamera().getMatrix(matrix);

        return matrix;
    }

    /**
     * Shadows
     */
    @ProtoMethod(description = "Sets the shadow fill", example = "")
    @ProtoMethodParam(params = {"x", "y", "radius", "colorHext"})
    public PCanvas shadow(int x, int y, float radius, String colorHex) {
        int c = Color.parseColor(colorHex);
        mPaintFill.setShadowLayer(radius, x, y, c);
        return this;
    }

    @ProtoMethod(description = "Set the shadow stroke", example = "")
    @ProtoMethodParam(params = {"x", "y", "radius", "colorHex"})
    public PCanvas shadowStroke(int x, int y, float radius, String colorHex) {
        int c = Color.parseColor(colorHex);
        mPaintStroke.setShadowLayer(radius, x, y, c);
        return this;
    }

    /**
     * Shaders
     */
    @ProtoMethod(description = "Sets a shader", example = "")
    @ProtoMethodParam(params = {"shader"})
    public void setShader(Shader shader) {
        mPaintFill.setAntiAlias(true);
        mPaintFill.setShader(shader);
    }

    @ProtoMethod(description = "Create a linear shader", example = "")
    @ProtoMethodParam(params = {"x1", "y1", "x2", "y2", "colorHex1", "colorHex2", "tileMode"})
    public Shader linearShader(float x1, float y1, float x2, float y2, String c1, String c2) {
        Shader.TileMode mode = Shader.TileMode.REPEAT;

        Shader shader = new LinearGradient(x1, y1, x2, y2, Color.parseColor(c1), Color.parseColor(c2), mode);
        return shader;
    }

    /**
     *
     */
    @ProtoMethod(description = "Enable/Disable antialiasing", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PCanvas antiAlias(boolean b) {
        mPaintFill.setAntiAlias(b);
        mPaintStroke.setAntiAlias(b);
        return this;
    }

    @ProtoMethod(description = "For each change in the canvas it will redraw it self. Have in mind that mainly to try out things as is not very fast.", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public PCanvas autoDraw(boolean b) {
        mAutoDraw = b;
        loop.start();
        return this;
    }

    /*
    public PCanvas ref() {
        draw.event(PCanvas.this);
        invalidate();

        return this;
    }
    */

    private RectF place(float x, float y, float w, float h) {
        if (mModeCorner == MODE_CORNER) {
            mRectf.set(x, y, x + w, y + h);
        } else {
            mRectf.set(x - w / 2, y - h / 2, x + w / 2, y + h / 2);
        }

        return mRectf;
    }

    /**
     * refresh the canvas after painting if the flag is true
     */
    private PCanvas refresh() {
        if (mAutoDraw) invalidate();
        return this;
    }


    /**
     * Layer class stuff
     */
    public class Layer {
        public Bitmap bitmap;
        public boolean visibility = true;

        Layer() {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
    }

    public class Layers {

        ArrayList<Layer> layers = new ArrayList<>();

        Layers() {
            create(); //add default layer
        }

        public void create() {
            create(layers.size());
        }

        public void create(int index) {
            Layer layer = new Layer();
            layers.add(index, layer);
            setCurrent(layers.size() - 1);
        }

        public void move(int from, int to) {
            Collections.swap(layers, from, to);
        }

        public void delete(int index) {
            layers.remove(index);
        }

        public void clear() {
            layers.clear();
        }

        public void setCurrent(int index) {
            MLog.d(TAG, index + " " + layers.size());
            mCanvas.setBitmap(layers.get(index).bitmap);
        }

        public void show(int index, boolean b) {
            layers.get(index).visibility = b;
        }

        public int size() {
            return layers.size();
        }

        public void drawAll(Canvas canvas) {
            for (Layer layer : layers) {
                if (layer.visibility) canvas.drawBitmap(layer.bitmap, 0, 0, null);
            }
        }
    }

}
