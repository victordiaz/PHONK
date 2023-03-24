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
import android.widget.TextView;

import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

public class Styler {
    private static final java.lang.String TAG = Styler.class.getSimpleName();
    public final PropertiesProxy mProps;
    final View mView;
    final AppRunner mAppRunner;
    final MyRoundCornerDrawable mBackgroundDrawable = new MyRoundCornerDrawable();
    final MyRoundCornerDrawable mPressedDrawable = new MyRoundCornerDrawable();
    final MyRoundCornerDrawable mSelectedDrawable = new MyRoundCornerDrawable();
    final MyRoundCornerDrawable mCheckedDrawable = new MyRoundCornerDrawable();
    final MyRoundCornerDrawable mHoveredDrawable = new MyRoundCornerDrawable();
    final StateListDrawable mStateListDrawable = new StateListDrawable();

    // common properties
    final String mViewName;
    float opacity;
    int background;
    int backgroundHover;
    int backgroundPressed;
    int backgroundSelected;
    int backgroundChecked;
    float borderWidth;
    int borderColor;
    float borderRadius;
    float padding;
    int textColor;
    float textSize;
    Typeface textFont;
    int textStyle;
    int textAlign;

    Styler(AppRunner appRunner, View view, PropertiesProxy props) {
        mAppRunner = appRunner;

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
    }

    public void resetStyle() {
        String id = (String) mProps.get("id");
        WidgetHelper.fromTo(mAppRunner.pUi.getProps(), mProps);
        mProps.put("id", id);
    }

    public void apply() {
        apply(null, null);
    }

    protected void apply(String name) {
        apply(name, mProps.get(name));
    }

    public void apply(String name, Object value) {
        if (name == null) {
            apply("background");
            apply("backgroundChecked");
            apply("backgroundHover");
            apply("backgroundPressed");
            apply("backgroundSelected");
            apply("borderColor");
            apply("borderRadius");
            apply("borderWidth");
            apply("opacity");
            apply("padding");
            apply("textAlign");
            apply("textColor");
            apply("textFont");
            apply("textSize");
            apply("textStyle");

        } else {
            if (value == null) return;
            switch (name) {
                case "background":
                    background = Color.parseColor(value.toString());
                    mBackgroundDrawable.setBackground(background);
                    break;

                case "backgroundChecked":
                    backgroundChecked = Color.parseColor(value.toString());
                    mCheckedDrawable.setBackground(backgroundChecked);
                    break;

                case "backgroundHover":
                    backgroundHover = Color.parseColor(value.toString());
                    mHoveredDrawable.setBackground(backgroundHover);
                    break;

                case "backgroundPressed":
                    backgroundPressed = Color.parseColor(value.toString());
                    mPressedDrawable.setBackground(backgroundPressed);
                    break;

                case "backgroundSelected":
                    backgroundSelected = Color.parseColor(value.toString());
                    mSelectedDrawable.setBackground(backgroundSelected);
                    break;

                case "borderColor":
                    borderColor = Color.parseColor(value.toString());
                    mBackgroundDrawable.setBorderColor(borderColor);
                    mPressedDrawable.setBorderColor(borderColor);
                    mSelectedDrawable.setBorderColor(borderColor);
                    mCheckedDrawable.setBorderColor(borderColor);
                    mHoveredDrawable.setBorderColor(borderColor);
                    break;

                case "borderRadius":
                    borderRadius = toFloat(value);
                    mBackgroundDrawable.setBorderRadius((int) borderRadius);
                    mPressedDrawable.setBorderRadius((int) borderRadius);
                    mSelectedDrawable.setBorderRadius((int) borderRadius);
                    mCheckedDrawable.setBorderRadius((int) borderRadius);
                    mHoveredDrawable.setBorderRadius((int) borderRadius);
                    break;

                case "borderWidth":
                    borderWidth = toFloat(value);
                    mBackgroundDrawable.setBorderWidth((int) borderWidth);
                    mPressedDrawable.setBorderWidth((int) borderWidth);
                    mSelectedDrawable.setBorderWidth((int) borderWidth);
                    mCheckedDrawable.setBorderWidth((int) borderWidth);
                    mHoveredDrawable.setBorderWidth((int) borderWidth);
                    break;

                case "opacity":
                    opacity = toFloat(value);
                    mView.setAlpha(opacity);
                    break;

                case "padding":
                case "paddingLeft":
                case "paddingTop":
                case "paddingRight":
                case "paddingBottom":
                    padding = toFloat(mProps.get("padding"));

                    float paddingLeft = padding;
                    float paddingTop = padding;
                    float paddingRight = padding;
                    float paddingBottom = padding;

                    // individual paddings have preference over general
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

                    mView.setPadding((int) paddingLeft, (int) paddingTop, (int) paddingRight, (int) paddingBottom);
                    break;

                case "textAlign":
                    switch (value.toString()) {
                        case "left":
                            textAlign = Gravity.CENTER_VERTICAL | Gravity.LEFT;
                            break;
                        case "center":
                            textAlign = Gravity.CENTER;
                            break;
                        case "right":
                            textAlign = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
                            break;
                    }
                    if (mView instanceof TextView) {
                        ((TextView) mView).setGravity(textAlign);
                    }
                    break;

                case "textColor":
                    String colorString = value.toString();
                    if (!colorString.equals("custom")) {
                        textColor = Color.parseColor(colorString);
                    }
                    if (mView instanceof TextView) {
                        ((TextView) mView).setTextColor(textColor);
                    }
                    break;

                case "textFont":
                    switch (value.toString()) {
                        case "default":
                            textFont = Typeface.DEFAULT;
                            break;
                        case "serif":
                            textFont = Typeface.SERIF;
                            break;
                        case "sansSerif":
                            textFont = Typeface.SANS_SERIF;
                            break;
                        case "monospace":
                            textFont = Typeface.MONOSPACE;
                            break;
                    }
                    if (mView instanceof TextView) {
                        ((TextView) mView).setTypeface(textFont, textStyle);
                    }
                    break;

                case "textSize":
                    if (value instanceof Number) {
                        textSize = toFloat(value);
                        if (mView instanceof TextView) {
                            ((TextView) mView).setTextSize(textSize);
                        }
                    }
                    break;

                case "textStyle":
                    switch (value.toString()) {
                        case "normal":
                            textStyle = Typeface.NORMAL;
                            break;
                        case "bold":
                            textStyle = Typeface.BOLD;
                            break;
                        case "boldItalic":
                            textStyle = Typeface.BOLD_ITALIC;
                            break;
                        case "italic":
                            textStyle = Typeface.ITALIC;
                            break;
                    }
                    if (mView instanceof TextView) {
                        ((TextView) mView).setTypeface(textFont, textStyle);
                    }
                    break;
            }
        }
    }

    protected float toFloat(Object o) {
        return ((Number) o).floatValue();
    }

    public void setLayoutProps(float x, float y, float width, float height) {
        mProps.eventOnChange = false;
        mProps.put("x", x);
        mProps.put("y", y);
        mProps.put("w", width);
        mProps.put("h", height);
        mProps.eventOnChange = true;
    }

}
