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

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.StateListDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedHashMap;
import java.util.Map;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.StyleProperties;
import io.phonk.runner.base.utils.MLog;

import static android.view.View.TEXT_ALIGNMENT_TEXT_START;

public class Styler {
    private static final java.lang.String TAG = Styler.class.getSimpleName();

    View mView;
    AppRunner mAppRunner;
    public StyleProperties props;

    MyRoundCornerDrawable mBackgroundDrawable = new MyRoundCornerDrawable();
    MyRoundCornerDrawable mPressedDrawable = new MyRoundCornerDrawable();
    MyRoundCornerDrawable mSelectedDrawable = new MyRoundCornerDrawable();
    MyRoundCornerDrawable mCheckedDrawable = new MyRoundCornerDrawable();
    MyRoundCornerDrawable mHoveredDrawable = new MyRoundCornerDrawable();
    StateListDrawable mStateListDrawable = new StateListDrawable();

    String visibility;
    float opacity;
    boolean enabled;
    int background;
    int backgroundHover;
    int backgroundPressed;
    int backgroundSelected;
    private int backgroundChecked;
    double borderWidth;
    int borderColor;
    double borderRadius;
    int textColor;
    double textSize;
    String src;
    String srcPressed;
    int srcTintPressed;
    String textFont;
    String textStyle;
    String textTransform;
    private String textAlign;
    public float padding;
    public int hintColor;
    String animInBefore;
    String animIn;
    String animOut;
    int slider;
    int sliderPressed;
    float sliderHeight;
    float sliderBorderSize;
    int sliderBorderColor;
    float padSize;
    int padColor;
    int padBorderColor;
    float padBorderSize;
    public int knobProgressWidth;
    public float knobProgressSeparation;
    public float knobBorderWidth;
    public int knobBorderColor;
    public int knobProgressColor;
    public int matrixCellColor;
    public int matrixCellSelectedColor;
    public float matrixCellBorderSize;
    public int matrixCellBorderColor;
    public float matrixCellBorderRadius;

    private boolean mViewIsSet;
    public int plotBackground = Color.parseColor("#22000000");
    public int plotColor = Color.parseColor("#222222");
    public float plotWidth = 2;
    String viewName;
    private Typeface font;
    private int tStyle;

    Styler(AppRunner appRunner, View view, StyleProperties props) {
        mAppRunner = appRunner;
        mView = view;

        viewName = mView.getClass().getSimpleName().substring(1).toLowerCase();
        MLog.d(TAG, "Applying style to " + viewName);
        this.props = props;

        mStateListDrawable.addState(new int[] { android.R.attr.state_checked }, mCheckedDrawable);
        mStateListDrawable.addState(new int[] { android.R.attr.state_pressed }, mPressedDrawable);
        mStateListDrawable.addState(new int[] { android.R.attr.state_enabled }, mBackgroundDrawable);


        mStateListDrawable.addState(new int[] { android.R.attr.state_selected }, mSelectedDrawable);
        mStateListDrawable.addState(new int[] { android.R.attr.state_hovered }, mHoveredDrawable);

        mStateListDrawable.addState(new int[] { }, mCheckedDrawable);
        mStateListDrawable.addState(new int[] { android.R.attr.state_checkable }, mCheckedDrawable);

        mView.setBackground(mStateListDrawable);

        // get default styles
        resetStyle();

        // when property changes then reapply them
        props.onChange(new StyleProperties.OnChangeListener() {
            @Override
            public void event(String name, Object value) {
                switch (name) {
                    case "x":
                        setX(value);
                        break;
                    case "y":
                        setY(value);
                        break;
                    case "width":
                        setWidth(value);
                        break;
                    case "height":
                        setHeight(value);
                        break;
                }

                apply();
            }
        });
    }

    public void resetStyle() {
        LinkedHashMap<String, StyleProperties> styles = mAppRunner.pUi.getStyles();

        fromTo(styles.get("*"), props);

        StyleProperties p = styles.get(viewName);
        if (p != null) {
            MLog.d(TAG, "" + p.size());
            MLog.d(TAG, "applying view specific style for " + viewName);
            fromTo(p, props);
        }
    }

    public void setStyle(Map o) {
        props.eventOnChange = false;
        fromTo(o, props);
        apply();
        props.eventOnChange = true;
    }

    private void fromTo(Map<String, Object> styleFrom, StyleProperties styleTo) {
        for (Map.Entry<String, Object> entry : styleFrom.entrySet()) {
            MLog.d(TAG, "" + entry.getKey() + " " + entry.getValue());

            styleTo.put(entry.getKey(), styleTo, entry.getValue());
        }
    }

    public void apply() {
        /*
        for (Map.Entry<String, Object> entry : mComputedStyle.entrySet()) {
            MLog.d(TAG, entry.getKey() + " " + entry.getValue());
        }
        */

        /*
        if (props.containsKey("x")) setX(props.get("x"));
        if (props.containsKey("y")) setY(props.get("y"));
        if (props.containsKey("width")) setWidth(props.get("width"));
        if (props.containsKey("height")) setHeight(props.get("height"));
        */

        visibility = props.get("visibility").toString();
        opacity = toFloat(props.get("opacity"));
        enabled = (boolean) props.get("enabled");
        background = Color.parseColor(props.get("background").toString());
        backgroundHover = Color.parseColor(props.get("backgroundHover").toString());
        backgroundPressed = Color.parseColor(props.get("backgroundPressed").toString());
        backgroundSelected = Color.parseColor(props.get("backgroundSelected").toString());
        backgroundChecked = Color.parseColor(props.get("backgroundChecked").toString());
        borderWidth = toFloat(props.get("borderWidth"));
        borderColor = Color.parseColor(props.get("borderColor").toString());
        borderRadius = toFloat(props.get("borderRadius"));
        textColor = Color.parseColor(props.get("textColor").toString());
        textSize = toFloat(props.get("textSize"));
        textFont = props.get("textFont").toString();
        textStyle = props.get("textStyle").toString();
        textTransform = props.get("textTransform").toString();
        textAlign = props.get("textAlign").toString();
        padding = toFloat(props.get("padding"));

        hintColor = Color.parseColor(props.get("hintColor").toString());

        src = props.get("src").toString();
        srcPressed = props.get("srcPressed").toString();
        srcTintPressed = Color.parseColor(props.get("srcTintPressed").toString());

        animInBefore = props.get("animInBefore").toString();
        animIn = props.get("animIn").toString();
        animOut = props.get("animOut").toString();
        slider = Color.parseColor(props.get("slider").toString());
        sliderPressed = Color.parseColor(props.get("sliderPressed").toString());
        sliderHeight = toFloat(props.get("sliderHeight"));
        sliderBorderSize = toFloat(props.get("sliderBorderSize"));
        sliderBorderColor = Color.parseColor(props.get("sliderBorderColor").toString());

        padSize = toFloat(props.get("padSize"));
        padColor = Color.parseColor(props.get("padColor").toString());
        padBorderColor = Color.parseColor(props.get("padBorderColor").toString());
        padBorderSize = toFloat(props.get("padBorderSize"));

        knobProgressSeparation = toFloat(props.get("knobProgressSeparation"));
        knobBorderWidth = toFloat(props.get("knobBorderWidth"));
        knobBorderColor = Color.parseColor(props.get("knobBorderColor").toString());
        knobProgressColor = Color.parseColor(props.get("knobProgressColor").toString());

        matrixCellColor = Color.parseColor(props.get("matrixCellColor").toString());
        matrixCellSelectedColor = Color.parseColor(props.get("matrixCellSelectedColor").toString());
        matrixCellBorderSize = toFloat(props.get("matrixCellBorderSize"));
        matrixCellBorderColor = Color.parseColor(props.get("matrixCellBorderColor").toString());
        matrixCellBorderRadius = toFloat(props.get("matrixCellBorderRadius"));

        plotColor = Color.parseColor(props.get("plotColor").toString());
        plotWidth = toFloat(props.get("plotWidth"));

        // mView.setVisibility(visibility);
        mView.setAlpha(opacity);
        mView.setEnabled(enabled);

        mView.setPadding((int) padding, (int) padding, (int) padding, (int) padding);

        // set background
        mBackgroundDrawable.setBackground(background);
        mPressedDrawable.setBackground(backgroundPressed);
        mSelectedDrawable.setBackground(backgroundSelected);
        mCheckedDrawable.setBackground(backgroundChecked);
        mHoveredDrawable.setBackground(backgroundHover);

        mBackgroundDrawable.setBorderRadius((int) borderRadius);
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
                    MLog.d(TAG, viewName + " yep!");
                    font = Typeface.MONOSPACE;
                    break;
            }
            v.font(font);

            switch (textStyle) {
                case "normal":
                    tStyle = Typeface.NORMAL;
                    break;
                case "bold":
                    tStyle = Typeface.BOLD;
                    break;
                case "boldItalic":
                    tStyle = Typeface.BOLD_ITALIC;
                    break;
                case "italic":
                    tStyle = Typeface.ITALIC;
                    break;
            }
            v.textStyle(tStyle);

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

    private float toFloat(Object o) {
        return ((Number) o).floatValue();
    }

    void setLayoutProps(float x, float y, float width, float height ) {
        props.eventOnChange = false;
        props.put("x", props, x);
        props.put("y", props, y);
        props.put("width", props, width);
        props.put("height", props, height);
        props.eventOnChange = true;
        mViewIsSet = true;
    }

    public void setX(Object value) {
        mView.setX(toFloat(value));
    }

    public void setY(Object value) {
        mView.setY(toFloat(value));
    }

    private void setWidth(Object value) {
        ViewGroup.LayoutParams lp = mView.getLayoutParams();

        if (mViewIsSet) {
            int w = ((Number) value).intValue();
            lp.width = w;
            mView.setLayoutParams(lp);
        }
    }

    private void setHeight(Object value) {
        ViewGroup.LayoutParams lp = mView.getLayoutParams();

        if (mViewIsSet) {
            int h = ((Number) value).intValue();
            lp.height = h;
            mView.setLayoutParams(lp);
        }
    }

}