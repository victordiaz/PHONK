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
import android.graphics.Typeface;

import io.phonk.runner.apidoc.annotation.PhonkClass;
import io.phonk.runner.apidoc.annotation.PhonkField;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.Image;

@PhonkClass(mergeFrom = "CustomView")
public class PCanvas {
    private static final String TAG = PCustomView.class.getSimpleName();
    private final AppRunner mAppRunner;

    // @ProtoField(description = "Object that contains the layers", example = "")
    // public Layers layers;
    private Canvas mCanvasBuffer;
    public Canvas mCanvas;

    @PhonkField(description = "Canvas width", example = "")
    public int width;

    @PhonkField(description = "Canvas height", example = "")
    public int height;

    private RectF mRectf;
    private Bitmap mTransparentBmp;
    private Paint mPaintBackground;
    public Paint mPaintFill;
    private Paint mPaintStroke;
    private boolean fillOn = true;
    private boolean strokeOn = false;
    private boolean mModeCorner = true;

    private boolean absoluteMode = false;

    PCanvas(AppRunner appRunner) {
        this.mAppRunner = appRunner;

        init();
    }

    public void init() {
        mRectf = new RectF();
        mPaintBackground = new Paint();
        mPaintFill = new Paint();
        mPaintStroke = new Paint();
        mPaintFill.setAntiAlias(true);
        mPaintStroke.setAntiAlias(true);
        // layers = new Layers();
    }

    /**
     * Fill and stroke colors
     */
    @PhonkMethod(description = "Clear the canvas", example = "")
    @PhonkMethodParam(params = {})
    public void clear() {
        mCanvasBuffer.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        refresh();
    }

    @PhonkMethod(description = "Sets the filling color", example = "")
    @PhonkMethodParam(params = {"hex"})
    public PCanvas fill(String hex) {
        fill(Color.parseColor(hex));

        return this;
    }

    @PhonkMethod(description = "Sets the filling color", example = "")
    @PhonkMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas fill(int r, int g, int b, int alpha) {
        fill(Color.argb(alpha, r, g, b));

        return this;
    }

    @PhonkMethod(description = "Sets the filling color", example = "")
    @PhonkMethodParam(params = {"r", "g", "b"})
    public PCanvas fill(int r, int g, int b) {
        fill(Color.argb(255, r, g, b));

        return this;
    }

    @PhonkMethod(description = "Sets the filling color", example = "")
    @PhonkMethodParam(params = {"hex"})
    public PCanvas fill(int c) {
        mPaintFill.setStyle(Paint.Style.FILL);
        mPaintFill.setColor(c);
        fillOn = true;

        return this;
    }

    @PhonkMethod(description = "Removes the filling color", example = "")
    @PhonkMethodParam(params = {})
    public void noFill() {
        fillOn = false;
    }

    @PhonkMethod(description = "Sets the stroke color", example = "")
    @PhonkMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas stroke(int r, int g, int b, int alpha) {
        stroke(Color.argb(alpha, r, g, b));

        return this;
    }

    @PhonkMethod(description = "Sets the stroke color", example = "")
    @PhonkMethodParam(params = {"r", "g", "b"})
    public PCanvas stroke(int r, int g, int b) {
        stroke(Color.argb(255, r, g, b));

        return this;
    }

    @PhonkMethod(description = "Sets the stroke color", example = "")
    @PhonkMethodParam(params = {"hex"})
    public PCanvas stroke(String c) {
        stroke(Color.parseColor(c));

        return this;
    }

    @PhonkMethod(description = "Sets the stroke color", example = "")
    @PhonkMethodParam(params = {"hex"})
    public PCanvas stroke(int c) {
        mPaintStroke.setStyle(Paint.Style.STROKE);
        mPaintStroke.setColor(c);
        strokeOn = true;

        return this;
    }

    @PhonkMethod(description = "Removes the stroke color", example = "")
    @PhonkMethodParam(params = {})
    public PCanvas noStroke() {
        strokeOn = false;
        return this;
    }

    @PhonkMethod(description = "Sets a stroke width", example = "")
    @PhonkMethodParam(params = {"width"})
    public PCanvas strokeWidth(float w) {
        mPaintStroke.setStrokeWidth(w);
        return this;
    }

    @PhonkMethod(description = "Sets a stroke cap", example = "")
    @PhonkMethodParam(params = {"cap"})
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

    @PhonkMethod(description = "Change the background color with alpha value", example = "")
    @PhonkMethodParam(params = {"r", "g", "b", "alpha"})
    public PCanvas background(int r, int g, int b, int alpha) {
        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setARGB(alpha, r, g, b);
        mCanvasBuffer.drawRect(0, 0, width, height, mPaintBackground);
        refresh();

        return this;
    }

    /**
     * Drawing cornerMode
     */
    @PhonkMethod(description = "Drawing will be done from a corner if true, otherwise from the center", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PCanvas cornerMode(boolean mode) {
        mModeCorner = mode;

        return this;
    }

    /**
     * Drawing thingies
     */
    @PhonkMethod(description = "Change the background color", example = "")
    @PhonkMethodParam(params = {"r", "g", "b"})
    public PCanvas background(int r, int g, int b) {
        background(r, g, b, 255);
        refresh();

        return this;
    }

    @PhonkMethod(description = "Draw a point", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PCanvas point(float x, float y) {
        mCanvasBuffer.drawPoint(x, y, mPaintStroke);
        refresh();
        return this;
    }

    public PCanvas line(float x1, float y1, float x2, float y2) {
        mCanvasBuffer.drawLine(x1, y1, x2, y2, mPaintStroke);
        return this;
    }

    @PhonkMethod(description = "Draws a rectangle", example = "")
    @PhonkMethodParam(params = {"x", "y", "width", "height"})
    public PCanvas rect(float x, float y, float width, float height) {
        if (fillOn) mCanvasBuffer.drawRect(place(x, y, width, height), mPaintFill);
        if (strokeOn) mCanvasBuffer.drawRect(place(x, y, width, height), mPaintStroke);
        refresh();

        return this;
    }

    @PhonkMethod(description = "Draws a rectangle with a given roundness value", example = "")
    @PhonkMethodParam(params = {"x", "y", "width", "height", "rx", "ry"})
    public PCanvas rect(float x, float y, float width, float height, float rx, float ry) {
        if (fillOn) mCanvasBuffer.drawRoundRect(place(x, y, width, height), rx, ry, mPaintFill);
        if (strokeOn) mCanvasBuffer.drawRoundRect(place(x, y, width, height), rx, ry, mPaintStroke);
        refresh();

        return this;
    }

    @PhonkMethod(description = "Draws and ellipse", example = "")
    @PhonkMethodParam(params = {"x1", "y1", "width", "height"})
    public PCanvas ellipse(float x, float y, float width, float height) {
        if (fillOn) mCanvasBuffer.drawOval(place(x, y, width, height), mPaintFill);
        if (strokeOn) mCanvasBuffer.drawOval(place(x, y, width, height), mPaintStroke);
        refresh();

        return this;
    }

    @PhonkMethod(description = "Draws an arc", example = "")
    @PhonkMethodParam(params = {"x1", "y1", "x2", "y2", "initAngle", "sweepAngle", "center"})
    public PCanvas arc(float x1, float y1, float x2, float y2, float initAngle, float sweepAngle, boolean center) {
        if (fillOn)
            mCanvasBuffer.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintFill);
        if (strokeOn)
            mCanvasBuffer.drawArc(place(x1, y1, x2, y2), initAngle, sweepAngle, center, mPaintStroke);
        refresh();

        return this;
    }

    public void drawPath(Path p1) {
        if (fillOn) mCanvasBuffer.drawPath(p1, mPaintFill);
        if (strokeOn) mCanvasBuffer.drawPath(p1, mPaintStroke);
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

        if (fillOn) mCanvasBuffer.drawPath(path, mPaintFill);
        if (strokeOn) mCanvasBuffer.drawPath(path, mPaintStroke);
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
        if (fillOn) mCanvasBuffer.drawPath(path, mPaintFill);
        if (strokeOn) mCanvasBuffer.drawPath(path, mPaintStroke);
        refresh();
    }

    /**
     * Text stuff
     */
    @PhonkMethod(description = "Sets the size of the text", example = "")
    @PhonkMethodParam(params = {"size"})
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

    @PhonkMethod(description = "Writes text", example = "")
    @PhonkMethodParam(params = {"text", "x", "y"})
    public PCanvas text(String text, float x, float y) {
        if (fillOn) mCanvasBuffer.drawText(text, x, y, mPaintFill);
        if (strokeOn) mCanvasBuffer.drawText(text, x, y, mPaintStroke);
        refresh();

        return this;
    }

    @PhonkMethod(description = "Draws a text on a path", example = "")
    @PhonkMethodParam(params = {"text", "path", "initOffset", "outOffsett"})
    public PCanvas text(String text, Path path, float initOffset, float outOffset) {
        if (fillOn) mCanvasBuffer.drawTextOnPath(text, path, initOffset, outOffset, mPaintFill);
        if (strokeOn) mCanvasBuffer.drawTextOnPath(text, path, initOffset, outOffset, mPaintStroke);
        refresh();

        return this;
    }

    public void drawTextCentered(String text) {
        int cx = mCanvasBuffer.getWidth() / 2;
        int cy = mCanvasBuffer.getHeight() / 2;

        Rect textBounds = new Rect();

        mPaintFill.getTextBounds(text, 0, text.length(), textBounds);
        mCanvasBuffer.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), mPaintFill);
    }


    public void typeface(String type) {
        Typeface selectedType;

        switch (type) {
            case "monospace":
                selectedType = Typeface.MONOSPACE;
                break;
            case "sans":
                selectedType = Typeface.SANS_SERIF;
                break;
            case "serif":
                selectedType = Typeface.SERIF;
                break;
            default:
                selectedType = Typeface.DEFAULT;

        }
        mPaintFill.setTypeface(selectedType);
    }

    @PhonkMethod(description = "Load an image", example = "")
    @PhonkMethodParam(params = {"imagePath"})
    public Bitmap loadImage(String imagePath) {
        return Image.loadBitmap(mAppRunner.getProject().getFullPathForFile(imagePath));
    }

    @PhonkMethod(description = "Draws an image", example = "")
    @PhonkMethodParam(params = {"bitmap", "x", "y"})
    public PCanvas image(Bitmap bmp, int x, int y) {
        mCanvasBuffer.drawBitmap(bmp, x, y, mPaintBackground);
        refresh();
        return this;
    }

    @PhonkMethod(description = "Draws an image", example = "")
    @PhonkMethodParam(params = {"bitmap", "x", "y", "w", "h"})
    public PCanvas image(Bitmap bmp, int x, int y, int w, int h) {
        Rect rectSrc = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        RectF rectDst = new RectF(x, y, x + w, y + h);
        mCanvasBuffer.drawBitmap(bmp, rectSrc, rectDst, mPaintStroke);
        refresh();

        return this;
    }


    /**
     *
     */
    @PhonkMethod(description = "push", example = "")
    @PhonkMethodParam(params = {})
    public PCanvas push() {
        mCanvasBuffer.save();
        return this;
    }

    @PhonkMethod(description = "Rotate given degrees", example = "")
    @PhonkMethodParam(params = {"degrees"})
    public PCanvas rotate(float degrees) {
        mCanvasBuffer.rotate(degrees);
        return this;
    }

    @PhonkMethod(description = "Translate", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PCanvas translate(float x, float y) {
        mCanvasBuffer.translate(x, y);
        return this;
    }

    @PhonkMethod(description = "Skew values 0 - 1)", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PCanvas skew(float x, float y) {
        mCanvasBuffer.skew(x, y);
        return this;
    }

    @PhonkMethod(description = "Scale", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PCanvas scale(float x, float y) {
        mCanvasBuffer.scale(x, y);
        return this;
    }

    @PhonkMethod(description = "Restore", example = "")
    @PhonkMethodParam(params = {"x", "y"})
    public PCanvas pop() {
        mCanvasBuffer.restore();
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
    @PhonkMethod(description = "Sets the shadow fill", example = "")
    @PhonkMethodParam(params = {"x", "y", "radius", "colorHext"})
    public PCanvas shadow(int x, int y, float radius, String colorHex) {
        int c = Color.parseColor(colorHex);
        mPaintFill.setShadowLayer(radius, x, y, c);
        return this;
    }

    @PhonkMethod(description = "Set the shadow stroke", example = "")
    @PhonkMethodParam(params = {"x", "y", "radius", "colorHex"})
    public PCanvas shadowStroke(int x, int y, float radius, String colorHex) {
        int c = Color.parseColor(colorHex);
        mPaintStroke.setShadowLayer(radius, x, y, c);
        return this;
    }

    /**
     * Shaders
     */
    @PhonkMethod(description = "Sets a shader", example = "")
    @PhonkMethodParam(params = {"shader"})
    public void setShader(Shader shader) {
        mPaintFill.setAntiAlias(true);
        mPaintFill.setShader(shader);
    }

    @PhonkMethod(description = "Create a linear shader", example = "")
    @PhonkMethodParam(params = {"x1", "y1", "x2", "y2", "colorHex1", "colorHex2", "tileMode"})
    public Shader linearShader(float x1, float y1, float x2, float y2, String c1, String c2) {
        Shader.TileMode mode = Shader.TileMode.REPEAT;

        Shader shader = new LinearGradient(x1, y1, x2, y2, Color.parseColor(c1), Color.parseColor(c2), mode);
        return shader;
    }

    /**
     *
     */
    @PhonkMethod(description = "Enable/Disable antialiasing", example = "")
    @PhonkMethodParam(params = {"boolean"})
    public PCanvas antiAlias(boolean b) {
        mPaintFill.setAntiAlias(b);
        mPaintStroke.setAntiAlias(b);
        return this;
    }

    private RectF place(float x, float y, float w, float h) {
        if (mModeCorner) {
            mRectf.set(x, y, x + w, y + h);
        } else {
            mRectf.set(x - w / 2, y - h / 2, x + w / 2, y + h / 2);
        }

        return mRectf;
    }

    public void setCanvas(Canvas canvas) {
        this.width = canvas.getWidth();
        this.height = canvas.getHeight();
        this.mCanvas = canvas;
    }

    /**
     * Layer class stuff
     */
    /*
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
            mCanvasBuffer.setBitmap(layers.get(index).bitmap);
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
    */
    public void refresh() {

    }

    public void prepare(int w, int h) {
        if (mTransparentBmp != null) {
            mTransparentBmp.recycle();
        }
        // mCanvasBuffer = new Canvas();

        mCanvasBuffer = new Canvas();
        mTransparentBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvasBuffer.setBitmap(mTransparentBmp);

        // mPCanvas.setCanvas(canvas);

    }

    public void drawAll() {
        mCanvas.drawBitmap(mTransparentBmp, 0, 0, null);
    }
}
