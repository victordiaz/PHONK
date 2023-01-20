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

package io.phonk.runner.apprunner.api.widgets;

import static android.view.View.TEXT_ALIGNMENT_TEXT_START;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

public class Styler {
    private static final java.lang.String TAG = Styler.class.getSimpleName();
    public final StylePropertiesProxy mProps;
    final View mView;
    final AppRunner mAppRunner;
    final MyRoundCornerDrawable mBackgroundDrawable = new MyRoundCornerDrawable();
    // MyRoundCornerDrawable mActiveDrawable = new MyRoundCornerDrawable();
    final MyRoundCornerDrawable mPressedDrawable = new MyRoundCornerDrawable();
    final MyRoundCornerDrawable mSelectedDrawable = new MyRoundCornerDrawable();
    final MyRoundCornerDrawable mCheckedDrawable = new MyRoundCornerDrawable();
    final MyRoundCornerDrawable mHoveredDrawable = new MyRoundCornerDrawable();
    final StateListDrawable mStateListDrawable = new StateListDrawable();

    // String animInBefore;
    // String animIn;
    // String animOut;

    // common properties
    final String id;
    final String mViewName;
    boolean enabled;
    String visibility;
    float opacity;
    float x;
    float y;
    float width;
    float height;
    int background;
    int backgroundHover;
    int backgroundPressed;
    int backgroundSelected;
    int backgroundChecked;
    double borderWidth;
    int borderColor;
    double borderRadius;
    float padding;
    int textColor;
    double textSize;
    String textFont;
    String textStyle;
    String textTransform;
    String textAlign;

    private Typeface font;
    private int typeFaceStyle;

    /*
    String src;
    String srcPressed;
    */

    private int mWidth;
    private int mHeight;
    private boolean mViewIsSet = false;

    Styler(AppRunner appRunner, View view, StylePropertiesProxy props) {
        mAppRunner = appRunner;

        id = RandomStringUtils.randomAlphanumeric(8);
        mView = view;
        mViewName = mView.getClass().getSimpleName().substring(1).toLowerCase();
        mProps = props;

        MLog.d(TAG, "Applying style to " + mViewName);

        // mStateListDrawable.addState(new int[]{android.R.attr.state_checked}, mCheckedDrawable);
        mStateListDrawable.addState(new int[]{android.R.attr.state_pressed}, mPressedDrawable);
        // mStateListDrawable.addState(new int[]{}, mActiveDrawable);
        mStateListDrawable.addState(new int[]{android.R.attr.state_enabled}, mBackgroundDrawable);
        mStateListDrawable.addState(new int[]{android.R.attr.state_selected}, mSelectedDrawable);
        mStateListDrawable.addState(new int[]{android.R.attr.state_hovered}, mHoveredDrawable);
        mStateListDrawable.addState(new int[]{}, mCheckedDrawable);
        mStateListDrawable.addState(new int[]{android.R.attr.state_checkable}, mCheckedDrawable);
        mView.setBackground(mStateListDrawable);

        // get default styles
        resetStyle();

        // when property changes then reapply them
        props.onChange((name, value) -> {
            /*
            switch (name) {
                case "x":
                    setX(value);
                    break;
                case "y":
                    setY(value);
                    break;
                case "w":
                    setWidth(value);
                    break;
                case "h":
                    setHeight(value);
                    break;
            }
            */
            apply();
        });
    }

    public void resetStyle() {
        StylePropertiesProxy style = mAppRunner.pUi.getStyle();
        fromTo(style, mProps);
        mProps.put("id", id);
    }

    public void apply() {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();

        if (mViewIsSet) {
            setX(mProps.get("x"));
            setY(mProps.get("y"));
            setWidth(mProps.get("w"));
            setHeight(mProps.get("h"));
        }
        mViewIsSet = true;

        enabled = (boolean) mProps.get("enabled");
        visibility = mProps.get("visibility").toString();
        opacity = toFloat(mProps.get("opacity"));
        background = Color.parseColor(mProps.get("background").toString());

        backgroundHover = Color.parseColor(mProps.get("backgroundHover").toString());
        backgroundPressed = Color.parseColor(mProps.get("backgroundPressed").toString());
        backgroundSelected = Color.parseColor(mProps.get("backgroundSelected").toString());
        backgroundChecked = Color.parseColor(mProps.get("backgroundChecked").toString());
        borderWidth = toFloat(mProps.get("borderWidth"));
        borderColor = Color.parseColor(mProps.get("borderColor").toString());
        borderRadius = toFloat(mProps.get("borderRadius"));

        textColor = Color.parseColor(mProps.get("textColor").toString());
        textSize = toFloat(mProps.get("textSize"));
        textFont = mProps.get("textFont").toString();
        textStyle = mProps.get("textStyle").toString();
        textTransform = mProps.get("textTransform").toString();
        textAlign = mProps.get("textAlign").toString();
        padding = toFloat(mProps.get("padding"));

        // individual paddings have preference over general
        float paddingLeft = padding;
        float paddingTop = padding;
        float paddingRight = padding;
        float paddingBottom = padding;

        try {
            paddingLeft = toFloat(mProps.get("paddingLeft"));
        } catch (Exception ignored) {
        }
        try {
            paddingTop = toFloat(mProps.get("paddingTop"));
        } catch (Exception ignored) {
        }
        try {
            paddingRight = toFloat(mProps.get("paddingRight"));
        } catch (Exception ignored) {
        }
        try {
            paddingBottom = toFloat(mProps.get("paddingBottom"));
        } catch (Exception ignored) {
        }

        /*
        src = props.get("src").toString();
        srcPressed = props.get("srcPressed").toString();


        // animInBefore = props.get("animInBefore").toString();
        // animIn = props.get("animIn").toString();
        // animOut = props.get("animOut").toString();
        */

        // mView.setVisibility(visibility);
        mView.setAlpha(opacity);
        mView.setEnabled(enabled);

        mView.setPadding((int) paddingLeft, (int) paddingTop, (int) paddingRight, (int) paddingBottom);

        // set background
        mBackgroundDrawable.setBackground(background);
        // mActiveDrawable.setBackground(0x0000FF00);
        mPressedDrawable.setBackground(backgroundPressed);
        mSelectedDrawable.setBackground(backgroundSelected);
        mCheckedDrawable.setBackground(backgroundChecked);
        mHoveredDrawable.setBackground(backgroundHover);

        mBackgroundDrawable.setBorderRadius((int) borderRadius);
        // mActiveDrawable.setBorderRadius((int) borderRadius);

        mPressedDrawable.setBorderRadius((int) borderRadius);
        mSelectedDrawable.setBorderRadius((int) borderRadius);
        mCheckedDrawable.setBorderRadius((int) borderRadius);
        mHoveredDrawable.setBorderRadius((int) borderRadius);

        mBackgroundDrawable.setBorderWidth((int) borderWidth);
        mPressedDrawable.setBorderWidth((int) borderWidth);
        mSelectedDrawable.setBorderWidth((int) borderWidth);
        mCheckedDrawable.setBorderWidth((int) borderWidth);
        mHoveredDrawable.setBorderWidth((int) borderWidth);

        mBackgroundDrawable.setBorderColor(borderColor);
        mPressedDrawable.setBorderColor(borderColor);
        mSelectedDrawable.setBorderColor(borderColor);
        mCheckedDrawable.setBorderColor(borderColor);
        mHoveredDrawable.setBorderColor(borderColor);

        if (mView instanceof PTextInterface) {
            PTextInterface v = (PTextInterface) mView;
            // btn.setTextAlignment();
            v.textColor(textColor);
            v.textSize((float) textSize);
            // btn.setFont();

            switch (textFont) {
                case "serif":
                    font = Typeface.SERIF;
                    break;
                case "sansSerif":
                    font = Typeface.SANS_SERIF;
                    break;
                case "monospace":
                    MLog.d(TAG, mViewName + " yep!");
                    font = Typeface.MONOSPACE;
                    break;
            }
            v.textFont(font);

            switch (textStyle) {
                case "normal":
                    typeFaceStyle = Typeface.NORMAL;
                    break;
                case "bold":
                    typeFaceStyle = Typeface.BOLD;
                    break;
                case "boldItalic":
                    typeFaceStyle = Typeface.BOLD_ITALIC;
                    break;
                case "italic":
                    typeFaceStyle = Typeface.ITALIC;
                    break;
            }
            v.textStyle(typeFaceStyle);

            int tAlignment = TEXT_ALIGNMENT_TEXT_START;
            switch (textAlign) {
                case "left":
                    tAlignment = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                    break;
                case "center":
                    tAlignment = Gravity.CENTER;
                    break;

                case "right":
                    tAlignment = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                    break;
            }
            v.textAlign(tAlignment);
        }
    }

    public static void fromTo(Map<String, Object> styleFrom, StylePropertiesProxy styleTo) {
        if (styleFrom == null) return;

        for (Map.Entry<String, Object> entry : styleFrom.entrySet()) {
            styleTo.put(entry.getKey(), styleTo, entry.getValue());
        }
    }

    public void setX(Object value) {
        getScreenSize();
        int val = mAppRunner.pUtil.sizeToPixels(value, mWidth);

        if (mView.getLayoutParams() instanceof FixedLayout.LayoutParams) {
            FixedLayout.LayoutParams lp = (FixedLayout.LayoutParams) mView.getLayoutParams();
            lp.x = val;
        }
    }

    public void setY(Object value) {
        getScreenSize();
        int val = mAppRunner.pUtil.sizeToPixels(value, mHeight);

        if (mView.getLayoutParams() instanceof FixedLayout.LayoutParams) {
            FixedLayout.LayoutParams lp = (FixedLayout.LayoutParams) mView.getLayoutParams();
            lp.y = val;
        }
    }

    private void setWidth(Object value) {
        getScreenSize();
        int val = mAppRunner.pUtil.sizeToPixels(value, mWidth);

        if (mView.getLayoutParams() instanceof FixedLayout.LayoutParams) {
            ViewGroup.LayoutParams lp = mView.getLayoutParams();
            lp.width = val;
            mView.setLayoutParams(lp);
        }
    }

    private void setHeight(Object value) {
        getScreenSize();
        int val = mAppRunner.pUtil.sizeToPixels(value, mHeight);

        if (mView.getLayoutParams() instanceof FixedLayout.LayoutParams) {
            ViewGroup.LayoutParams lp = mView.getLayoutParams();
            lp.height = val;
            mView.setLayoutParams(lp);
        }
    }

    protected float toFloat(Object o) {
        return ((Number) o).floatValue();
    }

    void getScreenSize() {
        mWidth = mAppRunner.pUi.screenWidth;
        mHeight = mAppRunner.pUi.screenHeight;
    }

    public void setProps(Map o) {
        mProps.eventOnChange = false;
        fromTo(o, mProps);
        apply();
        mProps.eventOnChange = true;
    }

    public void setLayoutProps(float x, float y, float width, float height) {
        mProps.eventOnChange = false;
        mProps.put("x", mProps, x);
        mProps.put("y", mProps, y);
        mProps.put("w", mProps, width);
        mProps.put("h", mProps, height);
        mProps.eventOnChange = true;
    }

}