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

package io.phonk.runner.apprunner.api;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.mozilla.javascript.NativeArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import io.phonk.runner.AppRunnerFragment;
import io.phonk.runner.R;
import io.phonk.runner.apidoc.annotation.PhonkField;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkMethodParam;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnInterfaceWithReturn;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.media.PCamera;
import io.phonk.runner.apprunner.api.media.PCamera2;
import io.phonk.runner.apprunner.api.media.PCameraXOne;
import io.phonk.runner.apprunner.api.other.PLooper;
import io.phonk.runner.apprunner.api.other.PProcessing;
import io.phonk.runner.apprunner.api.widgets.PAbsoluteLayout;
import io.phonk.runner.apprunner.api.widgets.PButton;
import io.phonk.runner.apprunner.api.widgets.PCheckBox;
import io.phonk.runner.apprunner.api.widgets.PCustomView;
import io.phonk.runner.apprunner.api.widgets.PImage;
import io.phonk.runner.apprunner.api.widgets.PImageButton;
import io.phonk.runner.apprunner.api.widgets.PInput;
import io.phonk.runner.apprunner.api.widgets.PKnob;
import io.phonk.runner.apprunner.api.widgets.PLinearLayout;
import io.phonk.runner.apprunner.api.widgets.PList;
import io.phonk.runner.apprunner.api.widgets.PMap;
import io.phonk.runner.apprunner.api.widgets.PMatrix;
import io.phonk.runner.apprunner.api.widgets.PNumberPicker;
import io.phonk.runner.apprunner.api.widgets.PPlot;
import io.phonk.runner.apprunner.api.widgets.PPopupDialogFragment;
import io.phonk.runner.apprunner.api.widgets.PProgressBar;
import io.phonk.runner.apprunner.api.widgets.PRadioButtonGroup;
import io.phonk.runner.apprunner.api.widgets.PScrollView;
import io.phonk.runner.apprunner.api.widgets.PSlider;
import io.phonk.runner.apprunner.api.widgets.PSpinner;
import io.phonk.runner.apprunner.api.widgets.PSwitch;
import io.phonk.runner.apprunner.api.widgets.PText;
import io.phonk.runner.apprunner.api.widgets.PTextInterface;
import io.phonk.runner.apprunner.api.widgets.PToggle;
import io.phonk.runner.apprunner.api.widgets.PToolbar;
import io.phonk.runner.apprunner.api.widgets.PTouchPad;
import io.phonk.runner.apprunner.api.widgets.PViewMethodsInterface;
import io.phonk.runner.apprunner.api.widgets.PViewPager;
import io.phonk.runner.apprunner.api.widgets.PWebView;
import io.phonk.runner.apprunner.api.widgets.StyleProperties;
import io.phonk.runner.apprunner.api.widgets.WidgetHelper;
import io.phonk.runner.apprunner.interpreter.PhonkNativeArray;
import io.phonk.runner.apprunner.permissions.FeatureNotAvailableException;
import io.phonk.runner.apprunner.permissions.PermissionNotGrantedException;
import io.phonk.runner.base.gui.CameraTexture;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

/**
 * Hola
 *
 * @author Victor Diaz
 */
@PhonkObject
public class PUI extends ProtoBase {

    private final AppRunner mAppRunner;
    private Context mContext;

    // contains a reference of all views added to the absolute layout
    private ArrayList<View> viewArray = new ArrayList<>();

    // UI
    private boolean isMainLayoutSetup = false;
    private boolean isScrollEnabled = false;
    protected PAbsoluteLayout uiAbsoluteLayout;
    private RelativeLayout uiHolderLayout;
    private PScrollView uiScrollView;

    private int screenWidth;
    private int screenHeight;

    LinkedHashMap<String, StyleProperties> styles = new LinkedHashMap<>();

    public StyleProperties style;
    public StyleProperties theme;

    public PUI(AppRunner appRunner) {
        super(appRunner);
        mAppRunner = appRunner;
        mContext = appRunner.getAppContext();
    }

    @Override
    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        if (fragment != null) {
            toolbar = new PToolbar(getAppRunner(), getActivity().getSupportActionBar());
        }
        initializeLayout();
    }

    public PAbsoluteLayout newAbsoluteLayout() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        PAbsoluteLayout pAbsoluteLayout = new PAbsoluteLayout(getAppRunner());
        pAbsoluteLayout.setLayoutParams(layoutParams);

        return pAbsoluteLayout;
    }

    /**
     * This method creates the basic layout where the user created views will lay out
     * It has to be programatically created since it might be used somewhere else without access to the R file
     */
    protected void initializeLayout() {
        if (!isMainLayoutSetup) {
            MLog.d(TAG, "" + getAppRunner());
            // MLog.d(TAG, "" + getAppRunner().interp);

            // View v = getActivity().getLayoutInflater().inflate(R.layout.apprunner_user_layout, null);

            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // uiHolderLayout = (RelativeLayout) v.findViewById(R.id.holder);
            // uiScrollView = (PScrollView) v.findViewById(R.id.scroll);
            // uiAbsoluteLayout = (PAbsoluteLayout) v.findViewById(R.id.absolute);


            // this is the structure of the layout
            // uiHolderLayout (background color)
            // [scrollview] if (isScrollEnabled)
            // [uiAbsoluteLayout] if (!isScrollEnabled)

            // set the holder
            uiHolderLayout = new RelativeLayout(getContext());
            uiHolderLayout.setLayoutParams(layoutParams);

            // We need to let the view scroll, so we're creating a scrollview
            uiScrollView = new PScrollView(getContext(), false);
            uiScrollView.setLayoutParams(layoutParams);
            // uiScrollView.setFillViewport(true);
            allowScroll(isScrollEnabled);

            // Create the main layout. This is where all the items actually go
            uiAbsoluteLayout = new PAbsoluteLayout(getAppRunner());
            uiAbsoluteLayout.setLayoutParams(layoutParams);
            uiScrollView.addView(uiAbsoluteLayout);

            if (getFragment() != null) {
                getFragment().addScriptedLayout(uiHolderLayout);
            } else if (getService() != null) {
                getService().addScriptedLayout(uiHolderLayout);
            }
            uiHolderLayout.addView(uiScrollView);

            isMainLayoutSetup = true;

            // style.put("x", style, 0);
            // style.put("y", style, 0);
            // nativeObject.put("Awidth", nativeObject, 100);
            // nativeObject.put("height", nativeObject, 100);
            setTheme();
            setStyle();
            background((String) theme.get("secondaryColor"));
        }
    }


    private void setTheme() {
        theme = new StyleProperties();


        theme.put("accentColor", getContext().getResources().getString(R.color.phonk_accentColor_primary));
        theme.put("primaryColor", "#efefef");
        theme.put("secondaryColor", "#2c2c2c");
    }

    private void setStyle() {
        String accentColor = (String) theme.get("accentColor");
        String primaryColor = (String) theme.get("primaryColor");
        String secondaryColor = (String) theme.get("secondaryColor");
        String transparentColor = "#00FFFFFF";

        style = new StyleProperties();
        style.put("enabled", style, true);
        style.put("opacity", style, 1.0f);
        style.put("visibility", style, "visible");
        style.put("background", style, primaryColor);
        style.put("backgroundHover", style, "#88000000");
        style.put("backgroundPressed", style, "#d3d3d3");
        style.put("backgroundSelected", style, "#88000000");
        style.put("backgroundChecked", style, "#88000000");
        style.put("borderColor", style, transparentColor);
        style.put("borderWidth", style, 0);
        style.put("borderRadius", style, 0);

        style.put("src", style, "");
        style.put("srcPressed", style, "");

        style.put("textColor", style, secondaryColor);
        style.put("textSize", style, 18);
        style.put("textFont", style, "sansSerif");
        style.put("textStyle", style, "normal");
        style.put("textAlign", style, "center");
        style.put("textTransform", style, "none");
        style.put("padding", style, 10);

        style.put("hintColor", style, "#88FFFFFF");

        style.put("animInBefore", style, "this.x(0).y(100)");
        style.put("animIn", style, "this.animate().x(100)");
        style.put("animOut", style, "this.animate().x(0)");

        style.put("slider", style, accentColor);
        style.put("sliderPressed", style, accentColor);
        style.put("sliderHeight", style, 20);
        style.put("sliderBorderSize", style, 0);
        style.put("sliderBorderColor", style, transparentColor);
        style.put("padSize", style, AndroidUtils.dpToPixels(mContext, 50));
        style.put("padColor", style, accentColor);
        style.put("padBorderColor", style, accentColor);
        style.put("padBorderSize", style, AndroidUtils.dpToPixels(mContext, 2));

        style.put("knobBorderWidth", style, 5);
        style.put("knobProgressSeparation", style, 50);
        style.put("knobBorderColor", style, accentColor);
        style.put("knobProgressColor", style, accentColor);

        style.put("matrixCellColor", style, "#00000000");
        style.put("matrixCellSelectedColor", style, accentColor);
        style.put("matrixCellBorderSize", style, 3);
        style.put("matrixCellBorderColor", style, secondaryColor);
        style.put("matrixCellBorderRadius", style, 2);

        style.put("plotColor", style, "#000000");
        style.put("plotWidth", style, 5);

        styles.put("*", style);

        StyleProperties buttonStyle = new StyleProperties();
        buttonStyle.put("textStyle", buttonStyle, "bold");
        buttonStyle.put("textAlign", buttonStyle, "center");
        buttonStyle.put("backgroundPressed", buttonStyle, "#d3d3d3");

        style.put("srcTintPressed", buttonStyle, primaryColor);
        styles.put("button", buttonStyle);

        StyleProperties imageStyle = new StyleProperties();
        imageStyle.put("background", imageStyle, transparentColor);
        imageStyle.put("srcMode", imageStyle, "fit");
        styles.put("image", imageStyle);

        StyleProperties imageButtonStyle = new StyleProperties();
        imageButtonStyle.put("srcMode", imageButtonStyle, "resize");
        style.put("srcTintPressed", imageButtonStyle, primaryColor);
        styles.put("imagebutton", imageButtonStyle);

        StyleProperties knobStyle = new StyleProperties();
        knobStyle.put("background", knobStyle, transparentColor);
        knobStyle.put("textColor", knobStyle, accentColor);
        knobStyle.put("textFont", knobStyle, "monospace");
        knobStyle.put("textSize", knobStyle, 10);
        styles.put("knob", knobStyle);

        StyleProperties textStyle = new StyleProperties();
        textStyle.put("background", textStyle, transparentColor);
        textStyle.put("textColor", textStyle, "#ffffff");
        textStyle.put("textSize", textStyle, 25);
        textStyle.put("textAlign", textStyle, "left");
        styles.put("text", textStyle);

        StyleProperties toggleStyle = new StyleProperties();
        toggleStyle.put("textColor", toggleStyle, primaryColor);
        toggleStyle.put("background", toggleStyle, secondaryColor);
        toggleStyle.put("backgroundChecked", toggleStyle, accentColor);
        toggleStyle.put("backgroundPressed", toggleStyle, transparentColor);
        toggleStyle.put("borderColor", toggleStyle, "#ffffff");
        toggleStyle.put("borderWidth", toggleStyle, 5);
        styles.put("toggle", toggleStyle);

        StyleProperties inputStyle = new StyleProperties();
        inputStyle.put("textAlign", inputStyle, "left");
        inputStyle.put("background", inputStyle, transparentColor);
        inputStyle.put("backgroundPressed", inputStyle, transparentColor);
        inputStyle.put("borderColor", inputStyle, "#ffffff");
        inputStyle.put("borderWidth", inputStyle, 5);
        // inputStyle.put("textAlign", inputStyle, "center");
        inputStyle.put("textColor", inputStyle, "#ffffff");
        styles.put("input", inputStyle);


        StyleProperties matrixStyle = new StyleProperties();
        textStyle.put("background", matrixStyle, transparentColor);
        textStyle.put("backgroundPressed", matrixStyle, transparentColor);
        styles.put("matrix", matrixStyle);

        StyleProperties plotStyle = new StyleProperties();
        plotStyle.put("textColor", plotStyle, "#55000000");

        styles.put("plot", plotStyle);
    }

    public void registerStyle(String name, StyleProperties widgetProp) {
        if (styles.containsKey(name) == false) {
            styles.put(name, widgetProp);
        }
    }


    public LinkedHashMap<String, StyleProperties> getStyles() {
        return styles;
    }

    public void startEditor() {
        PLooper loop = mAppRunner.pUtil.loop(1000, () -> {
            ArrayList<HashMap> arrayList = new ArrayList<HashMap>();
            for (int i = 0; i < viewArray.size(); i++) {
                PViewMethodsInterface view = (PViewMethodsInterface) viewArray.get(i);
                Map style = view.getStyle();
                HashMap<String, Object> o = new HashMap<String, Object>();
                o.put("name", view.getClass().getSimpleName());
                o.put("type", view.getClass().getSimpleName());
                // MLog.d(TAG, "-->" + view);
                Bitmap bmpView = takeViewScreenshot((View) view);
                String base64ImgString = mAppRunner.pUtil.bitmapToBase64String(bmpView);
                // MLog.d(TAG, base64ImgString);
                o.put("image", "data:image/png;base64," + base64ImgString);
                o.put("osc", "/osc");
                o.put("x", ((View) view).getX());
                o.put("y", ((View) view).getY() + 27); // adding status bar
                o.put("width", ((View) view).getWidth());
                o.put("height", ((View) view).getHeight());
                // o.put("properties", getStyles.props);
                arrayList.add(o);
            }
            Gson gson = new GsonBuilder().create();
            String json = gson.toJson(arrayList);
            // MLog.d(TAG, json.toString());

            // send event
            MLog.d("views", "sending event");
            Intent i = new Intent("io.phonk.intent.VIEWS_UPDATE");
            i.putExtra("views", json);
            mAppRunner.getAppContext().sendBroadcast(i);

        });
        loop.start();
    }

    /**
     * Changes the position cornerMode.
     * By default, Phonk places the widgets using normalized coordinates (0 to 1)
     * Sometimes we need a bit more control on how to create layouts so we can specify that the
     * views will lay out using pixel or dp (density independent pixels) units
     *
     * @param type pixels/dp/normalized
     * @exampleLink /examples/User Interface/Absolute Positioning
     * @advanced
     * @status OK
     */
    @PhonkMethod
    public void positionMode(String type) {
        uiAbsoluteLayout.mode(type);
    }

    public void screenMode(String mode) {

        switch (mode) {
            case "fullscreen":
                getActivity().setFullScreen();
                break;

            case "lightsout":
                getActivity().lightsOutMode();
                break;

            case "immersive":
                getActivity().setImmersive();
                break;

            default:
                getActivity().setNormal();
        }

        updateScreenSizes();
    }

    public void updateScreenSizes() {
        screenWidth = uiAbsoluteLayout.width();
        screenHeight = uiAbsoluteLayout.height();
    }

    public void screenOrientation(String mode) {
        if (mode.equals("landscape")) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (mode.equals("portrait")) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }

        updateScreenSizes();
    }

    /**
     * Adds a view created with new (newButton, newSlider, newText, etc) to the layout
     *
     * @param v view
     * @param x
     * @param y
     * @param w
     * @param h
     * @status TODO_EXAMPLE
     * @advanced
     */
    @PhonkMethod
    protected void addViewAbsolute(View v, Object x, Object y, Object w, Object h) {

        //TODO
        // if (v instanceof PViewMethodsInterface) ((PViewMethodsInterface) v).set(x, y, w, h);

        v.setAlpha(0);
        viewArray.add(v);

        uiAbsoluteLayout.addView(v, x, y, w, h);
        v.animate().alpha(1).setDuration(300).setStartDelay(100 * (1 + viewArray.size())).start();
    }

    /**
     * Remove all views in the layout
     *
     * @advanced
     * @status TODO_EXAMPLE
     */
    @PhonkMethod
    public void removeAllViews() {
        uiAbsoluteLayout.removeAllViews();
        viewArray.clear();
    }


    /**
     * Allows the main interface to scroll up and down.
     * It's quite handy to lay out many widgets that cannot fit in a screen.
     *
     * @param scroll
     * @staus TODO_EXAMPLE
     */
    @PhonkMethod
    public void allowScroll(boolean scroll) {
        uiScrollView.setScrollingEnabled(scroll);
        isScrollEnabled = scroll;
    }

    /**
     * blablba
     *
     * @status TODO
     */
    @PhonkField
    public PToolbar toolbar;

    /**
     * Changes the background color using grayscale
     *
     * @param gray
     * @status TODO_EXAMPLE
     */
    @PhonkMethod
    public void background(int gray) {
        uiHolderLayout.setBackgroundColor(Color.rgb(gray, gray, gray));
    }

    /**
     * Changes the background color using RGB
     *
     * @param red
     * @param green
     * @param blue
     * @status TODO_EXAMPLE
     */
    @PhonkMethod
    public void background(int red, int green, int blue) {
        uiHolderLayout.setBackgroundColor(Color.rgb(red, green, blue));
    }

    /**
     * Changes the background color using RGB
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha
     * @status TODO_EXAMPLE
     */
    @PhonkMethod
    public void background(int red, int green, int blue, int alpha) {
        uiHolderLayout.setBackgroundColor(Color.argb(alpha, red, green, blue));
    }

    /**
     * Changes the background color using Hexadecimal color
     *
     * @param c
     * @status TODO_EXAMPLE
     */
    @PhonkMethod
    public void background(String c) {
        uiHolderLayout.setBackgroundColor(Color.parseColor(c));
    }

    /**
     * Adds an overlay title to the script
     *
     * @param title
     */
    public void addTitle(String title) {

        /*
        PText t = newText(title);

        t.setX(-100f);
        t.setAlpha(0.0f);
        t.animate().x(50).alpha(1.0f);
        t.props.put("background", t.props, "#000000");
        t.props.put("textFont", t.props, "monospace");
        t.props.put("textStyle", t.props, "bold");
        t.props.put("textColor", t.props, "#f5d328");
        t.props.put("textSize", t.props, 20);
        t.props.put("borderRadius", t.props, 0);
        t.setPadding(20, 10, 20, 10);
        t.setAllCaps(true);

        addViewAbsolute(t, 0.0f, 0.05f, -1, -1);
        */


        getFragment().changeTitle(title);

    }

    /**
     * Adds an overlay subtitle to the script
     *
     * @param subtitle
     */
    public void addSubtitle(String subtitle) {
        getFragment().changeSubtitle(subtitle);
    }

    /**
     * Creates a new button
     *
     * @param label
     * @return
     * @advanced
     * @status OK
     */
    @PhonkMethod
    public PButton newButton(String label) {
        PButton b = new PButton(mAppRunner);
        b.setText(label);
        return b;
    }

    /**
     * Adds a button to the main screen
     *
     * @param label Text that appears in the button
     * @param x     Horizontal position
     * @param y     Vertical position
     * @param w     Width
     * @param h     Height
     * @return
     * @exampleLink /examples/User Interface/Basic Views
     * @status OK
     */
    @PhonkMethod
    public PButton addButton(String label, Object x, Object y, Object w, Object h) {
        PButton b = newButton(label);
        addViewAbsolute(b, x, y, w, h);

        return b;
    }

    @PhonkMethod
    public PButton addButton(String label, Object x, Object y) {
        PButton b = newButton(label);
        addViewAbsolute(b, x, y, -1, -1);
        return b;
    }

    private float toFloat(Object o) {
        return ((Number) o).floatValue();
    }

    /**
     * Adds a button to the main screen
     *
     * @param style
     * @return
     * @status TODO
     */
    public PButton addButtonWithProperties(Map style) {
        PButton b = newButton("hi");
        b.setStyle(style);
        addViewAbsolute(b, toFloat(style.get("x")), toFloat(style.get("y")), toFloat(style.get("width")), toFloat(style.get("height")));

        return b;
    }

    /**
     * Creates a new image button
     *
     * @param imagePath
     * @return
     * @advanced
     * @status OK
     */
    @PhonkMethod
    public PImageButton newImageButton(String imagePath) {
        final PImageButton ib = new PImageButton(getAppRunner());
        ib.load(imagePath);

        return ib;
    }

    /**
     * Adds an imageButton
     *
     * @param imagePath
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @exampleLink /examples/User Interface/Basic Views
     * @status OK
     */
    @PhonkMethod
    public PImageButton addImageButton(String imagePath, Object x, Object y, Object w, Object h) {
        PImageButton pImageButton = newImageButton(imagePath);
        addViewAbsolute(pImageButton, x, y, w, h);
        return pImageButton;
    }

    /**
     * @param imagePath
     * @param x
     * @param y
     * @return
     * @exampleLink /examples/User Interface/Basic Views
     * @status OK
     */
    @PhonkMethod
    public PImageButton addImageButton(String imagePath, Object x, Object y) {
        PImageButton pImageButton = newImageButton(imagePath);
        addViewAbsolute(pImageButton, x, y, -1, -1);
        return pImageButton;
    }

    /**
     * Creates a new text
     *
     * @param text
     * @return
     * @status OK
     * @advanced
     */
    @PhonkMethod
    public PText newText(String text) {
        // int defaultTextSize = AndroidUtils.pixelsToSp(getContext(), 16);
        PText tv = new PText(mAppRunner);
        // tv.setTextSize((float) defaultTextSize);
        tv.setTextSize(22);
        tv.setText(text);
        tv.setTextColor(Color.argb(255, 255, 255, 255));
        return tv;
    }

    /**
     * Adds a text box.
     * If only x and y are provided, the bounding box will be as large as the text.
     * If x, y, w, h are provided, the text will accommodate to the bounding box defined.
     *
     * @param x
     * @param y
     * @return
     * @exampleLink /examples/User Interface/Basic Views
     * @status OK
     */
    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {"label", "x", "y"})
    public PText addText(Object x, Object y) {
        PText tv = newText("");
        addViewAbsolute(tv, x, y, -1, -1);
        return tv;
    }

    @PhonkMethod
    public PText addText(Object x, Object y, Object w, Object h) {
        PText tv = newText("");
        addViewAbsolute(tv, x, y, w, h);
        return tv;
    }

    @PhonkMethod
    public PText addText(String text, Object x, Object y) {
        PText tv = newText(text);
        addViewAbsolute(tv, x, y, -1, -1);
        return tv;
    }

    @PhonkMethod
    public PText addText(String label, Object x, Object y, Object w, Object h) {
        PText tv = newText(label);
        addViewAbsolute(tv, x, y, w, h);
        return tv;
    }

    /**
     * Creates a new input
     *
     * @param label
     * @return
     * @exampleLink /examples/User Interface/Basic Views
     * @status OK
     * @advanced
     */
    @PhonkMethod
    public PInput newInput(String label) {
        final PInput et = new PInput(mAppRunner);
        et.setHint(label);
        return et;
    }

    /**
     * Adds a text input
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @exampleLink /examples/User Interface/Basic Views
     * @status OK
     */
    @PhonkMethod
    public PInput addInput(Object x, Object y, Object w, Object h) {
        PInput et = newInput("");
        addViewAbsolute(et, x, y, w, h);
        return et;
    }

    @PhonkMethod
    public PInput addInput(String label, Object x, Object y, Object w, Object h) {
        PInput et = newInput(label);
        addViewAbsolute(et, x, y, w, h);
        return et;
    }

    /**
     * New Checkbox
     *
     * @param label
     * @return
     */
    @PhonkMethod
    public PCheckBox newCheckbox(String label) {
        PCheckBox cb = new PCheckBox(mAppRunner);
        cb.setText(label);
        return cb;
    }

    /**
     * Checkbox
     *
     * @param label
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @exampleLink /examples/User Interface/Basic Views
     * @status OK
     */
    @PhonkMethod
    public PCheckBox addCheckbox(String label, Object x, Object y, Object w, Object h) {
        PCheckBox cb = newCheckbox(label);
        addViewAbsolute(cb, x, y, w, h);
        return cb;
    }

    /**
     * New Toggle
     *
     * @param label
     * @return
     * @status OK
     * @advanced
     */
    @PhonkMethod
    public PToggle newToggle(final String label) {
        PToggle tb = new PToggle(mAppRunner);
        tb.checked(false);
        tb.text(label);
        return tb;
    }

    /**
     * Adds a toggle
     *
     * @param text
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @exampleLink /examples/User Interface/Basic Views
     * @status OK
     */
    @PhonkMethod
    public PToggle addToggle(final String text, Object x, Object y, Object w, Object h) {
        PToggle tb = newToggle(text);
        addViewAbsolute(tb, x, y, w, h);
        return tb;
    }

    /**
     * New switch
     *
     * @param text
     * @return
     * @status OK
     * @advanced
     */
    @PhonkMethod
    public PSwitch newSwitch(String text) {
        PSwitch s = new PSwitch(mAppRunner);
        s.setText(text);
        s.color("#FFFFFF");
        s.background("#00000000");
        return s;
    }

    /**
     * Add a switch
     *
     * @param text
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TODO
     */
    @PhonkMethod
    public PSwitch addSwitch(String text, Object x, Object y, Object w, Object h) {
        PSwitch s = newSwitch(text);
        addViewAbsolute(s, x, y, w, h);
        return s;
    }

    /**
     * Creates a pager that contain slidable views
     *
     * @return
     * @status TODO
     * @advanced
     */
    @PhonkMethod(description = "Creates a new pager", example = "")
    public PViewPager newViewPager() {
        PViewPager pViewPager = new PViewPager(getContext());
        return pViewPager;
    }


    /**
     * Creates a new slider
     *
     * @return
     * @advanced
     */
    @PhonkMethod
    public PSlider newSlider() {
        // final PSlider slider = new PSlider(mAppRunner).range(0, 1);
        final PSlider slider = new PSlider(mAppRunner);
        return slider;
    }

    /**
     * Adds a slider. By default the range is [0, 1].
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PSlider addSlider(Object x, Object y, Object w, Object h) {
        PSlider slider = newSlider();
        addViewAbsolute(slider, x, y, w, h);
        return slider;
    }

    /**
     * New knob
     *
     * @return
     * @advanced
     */
    @PhonkMethod
    public PKnob newKnob() {
        final PKnob slider = new PKnob(mAppRunner);
        return slider;
    }

    /**
     * Adds a knob
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PKnob addKnob(Object x, Object y, Object w, Object h) {
        PKnob slider = newKnob();
        addViewAbsolute(slider, x, y, w, h);
        return slider;
    }

    /**
     * New matrix.
     *
     * @param m
     * @param n
     * @return
     * @status OK
     * @advanced
     */
    @PhonkMethod
    public PMatrix newMatrix(int m, int n) {
        final PMatrix matrix = new PMatrix(mAppRunner, m, n);
        return matrix;
    }

    /**
     * Adds a matrix with M and N size. Useful when creating step sequencers and similar.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param m
     * @param n
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PMatrix addMatrix(Object x, Object y, Object w, Object h, int m, int n) {
        PMatrix matrix = newMatrix(m, n);
        addViewAbsolute(matrix, x, y, w, h);
        return matrix;
    }

    /**
     * Creates a progress bar of n units
     *
     * @return
     * @advanced
     * @status TOREVIEW
     */
    @PhonkMethod
    public PProgressBar newProgress() {
        PProgressBar pb = new PProgressBar(getContext(), android.R.attr.progressBarStyleHorizontal);
        return pb;
    }

    /**
     * Add a progress bar
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PProgressBar addProgressBar(Object x, Object y, Object w, Object h) {
        PProgressBar pb = newProgress();
        addViewAbsolute(pb, x, y, w, -1);
        return pb;
    }


    /**
     * New RadioButtonGroup
     *
     * @return
     * @advanced
     * @status TOREVIEW
     */
    public PRadioButtonGroup newPRadioButtonGroup() {
        PRadioButtonGroup pRadioButtonGroup = new PRadioButtonGroup(getContext());
        return pRadioButtonGroup;
    }

    /**
     * Adds a radio button group
     *
     * @param x
     * @param y
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PRadioButtonGroup addRadioButtonGroup(Object x, Object y) {
        PRadioButtonGroup rbg = newPRadioButtonGroup();
        addViewAbsolute(rbg, x, y, -1, -1);
        return rbg;
    }

    @PhonkMethod
    public PRadioButtonGroup addRadioButtonGroup(Object x, Object y, Object w, Object h) {
        PRadioButtonGroup rbg = newPRadioButtonGroup();
        addViewAbsolute(rbg, x, y, w, h);
        return rbg;
    }

    /**
     * Creates a new image view
     *
     * @param imagePath
     * @return
     * @advanced
     * @status TOREVIEW
     */
    @PhonkMethod
    public PImage newImage(String imagePath) {
        final PImage iv = new PImage(mAppRunner);
        if (imagePath != null) iv.load(imagePath);

        return iv;
    }

    /**
     * Adds an image
     *
     * @param x
     * @param y
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PImage addImage(Object x, Object y) {
        final PImage iv = newImage(null);
        addViewAbsolute(iv, x, y, -1, -1);
        return iv;
    }


    @PhonkMethod
    public PImage addImage(Object x, Object y, Object w, Object h) {
        final PImage iv = newImage(null);
        addViewAbsolute(iv, x, y, w, h);
        return iv;
    }

    @PhonkMethod
    public PImage addImage(String imagePath, Object x, Object y) {
        final PImage iv = newImage(imagePath);
        addViewAbsolute(iv, x, y, -1, -1);

        return iv;
    }

    @PhonkMethod
    public PImage addImage(String imagePath, Object x, Object y, Object w, Object h) {
        final PImage iv = newImage(imagePath);
        addViewAbsolute(iv, x, y, w, h);
        return iv;
    }

    /**
     * Creates a new choice box
     *
     * @param array
     * @return
     * @advanced
     * @status TOREVIEW
     */
    @PhonkMethod
    public PSpinner newChoiceBox(final String[] array) {
        PSpinner pSpinner = new PSpinner(getContext());
        pSpinner.setData(array);
        pSpinner.setPrompt("qq");

        return pSpinner;
    }

    /**
     * Add a choice box
     *
     * @param array
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PSpinner addChoiceBox(final String[] array, Object x, Object y, Object w, Object h) {
        PSpinner pSpinner = newChoiceBox(array);
        addViewAbsolute(pSpinner, x, y, w, h);
        return pSpinner;
    }

    /**
     * New NumberPicker
     *
     * @param from
     * @param to
     * @return
     * @status TOREVIEW
     * @advanced
     */
    @PhonkMethod
    public PNumberPicker newNumberPicker(int from, int to) {
        PNumberPicker pNumberPicker = new PNumberPicker(getContext());
        pNumberPicker.setMinValue(from);
        pNumberPicker.setMaxValue(to);

        return pNumberPicker;
    }

    /**
     * Adds a numberpicker
     *
     * @param from
     * @param to
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PNumberPicker addNumberPicker(int from, int to, Object x, Object y, Object w, Object h) {
        PNumberPicker pNumberPicker = newNumberPicker(from, to);
        addViewAbsolute(pNumberPicker, x, y, w, h);

        return pNumberPicker;
    }

    boolean enableCamera2 = false;

    /**
     * Creates a new camera view
     *
     * @param type "front" or "back"
     * @return
     * @advanced
     */
    @PhonkMethod
    public Object newCameraView(String type) {
        int camNum = -1;
        switch (type) {
            case "front":
                camNum = CameraTexture.MODE_CAMERA_FRONT;
                break;
            case "back":
                camNum = CameraTexture.MODE_CAMERA_BACK;
                break;
        }

        Object pCamera = null;
        if (AndroidUtils.isVersionMarshmallow() && enableCamera2) {
            pCamera = new PCamera2(getAppRunner());
        } else {
            if (check("camera", PackageManager.FEATURE_CAMERA, Manifest.permission.CAMERA)) {
                pCamera = new PCamera(getAppRunner(), camNum);
            }
        }

        return pCamera;
    }

    private boolean check(String what, String feature, String permission) {
        boolean ret = false;

        PackageManager pm = getContext().getPackageManager();

        // check if available
        if (!pm.hasSystemFeature(feature)) throw new FeatureNotAvailableException(what);
        if (!getActivity().checkPermission(permission))
            throw new PermissionNotGrantedException(what);
        ret = true;

        return ret;
    }

    /**
     * Add camera view
     *
     * @param type "front" or "back
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public Object addCameraView(String type, Object x, Object y, Object w, Object h) {
        Object pCamera = newCameraView(type);
        addViewAbsolute((View) pCamera, x, y, w, h);
        return pCamera;
    }

    public Object addCam(Object x, Object y, Object w, Object h) {
        PCameraXOne cameraX = new PCameraXOne(getAppRunner());
        // cameraX.start();
        // addViewAbsolute((View) cameraX, x, y, w, h);
        // return cameraX;

        FrameLayout fl = new FrameLayout(getContext());
        fl.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        fl.setId(200 + (int) (200 * Math.random()));

        // Add the view
        addViewAbsolute(fl, x, y, w, h);

        // PCameraX p = new PCameraX();

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), cameraX, String.valueOf(fl.getId()));
        ft.commit();

        // return p;
        return null;
    }

    /**
     * Adds a view created with new (newButton, newToggle, etc)
     *
     * @param v
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @advanced
     */
    @PhonkMethod
    public View add(View v, Object x, Object y, Object w, Object h) {
        addViewAbsolute(v, x, y, w, h);
        return v;
    }

    /**
     * Creates a new web view
     *
     * @return
     * @advanced
     */
    @PhonkMethod
    public PWebView newWebView() {
        PWebView webView = new PWebView(getAppRunner());
        return webView;
    }

    /**
     * Adds a webview
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PWebView addWebView(Object x, Object y, Object w, Object h) {
        PWebView webView = newWebView();
        addViewAbsolute(webView, x, y, w, h);
        return webView;
    }

    /**
     * Creates a new plot
     *
     * @return
     * @status TOREVIEW
     * @advanced
     */
    @PhonkMethod
    public PPlot newPlot() {
        PPlot pPlot = new PPlot(mAppRunner);
        // pPlotView.range(0, 1);
        return pPlot;
    }

    /**
     * Adds a plot, by default the range is [0, 1]
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PPlot addPlot(Object x, Object y, Object w, Object h) {
        PPlot pPlot = newPlot();
        addViewAbsolute(pPlot, x, y, w, h);
        return pPlot;
    }

    /**
     * Creates a new touch pad
     *
     * @return
     * @status TOREVIEW
     * @advanced
     */
    @PhonkMethod
    public PTouchPad newTouchPad() {
        PTouchPad taV = new PTouchPad(mAppRunner);

        return taV;
    }

    /**
     * Creates a new touch pad
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PTouchPad addTouchPad(Object x, Object y, Object w, Object h) {
        PTouchPad taV = newTouchPad();
        addViewAbsolute(taV, x, y, w, h);

        return taV;
    }

    /**
     * Creates a new drawing canvas
     *
     * @return
     * @advanced
     * @status OK
     */
    @PhonkMethod
    public PCustomView newCanvas() {
        PCustomView customview = new PCustomView(mAppRunner);
        return customview;
    }

    /*
    private PCanvas newCanvas(int w, int h) {
        PCanvas canvasView = new PCanvas(mAppRunner);
        return canvasView;
    }
     */

    /**
     * Adds a canvas view
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PCustomView addCanvas(Object x, Object y, Object w, Object h) {
        final PCustomView canvasView = newCanvas(); // (int) w, (int) h);
        addViewAbsolute(canvasView, x, y, w, h);

        return canvasView;
    }

    /**
     * New map
     *
     * @return
     * @advanced
     * @status OK
     */
    @PhonkMethod
    public PMap newMap() {
        PMap mapView = new PMap(getAppRunner());
        return mapView;
    }

    /**
     * Adds a map widget using OpenStreetMap
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {"x", "y", "w", "h"})
    public PMap addMap(Object x, Object y, Object w, Object h) {
        PMap mapView = newMap();
        addViewAbsolute(mapView, x, y, w, h);
        return mapView;
    }

    /**
     * New List
     *
     * @param numCols
     * @param data
     * @param creating
     * @param binding
     * @return
     * @status TOREVIEW
     * @advanced
     */
    @PhonkMethod
    public PList newList(int numCols, NativeArray data, ReturnInterfaceWithReturn creating, ReturnInterfaceWithReturn binding) {
        PList pList = new PList(getAppRunner());
        pList.init(data, numCols, creating, binding);

        return pList;
    }


    /**
     * @param x
     * @param y
     * @param w
     * @param h
     * @param numCols
     * @param data
     * @param creating
     * @param binding
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PList addGrid(Object x, Object y, Object w, Object h, int numCols, NativeArray data, ReturnInterfaceWithReturn creating, ReturnInterfaceWithReturn binding) {
        PList list = newList(numCols, data, creating, binding);
        addViewAbsolute(list, x, y, w, h);

        return list;
    }

    /**
     * Adds a list of views
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param data
     * @param creating
     * @param binding
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
    public PList addList(Object x, Object y, Object w, Object h, NativeArray data, ReturnInterfaceWithReturn creating, ReturnInterfaceWithReturn binding) {
        PList list = newList(1, data, creating, binding);
        addViewAbsolute(list, x, y, w, h);

        return list;
    }

    public PTextList addTextList(Object x, Object y, Object w, Object h) {
        PTextList pTextList = new PTextList();
        addViewAbsolute(pTextList, x, y, w, h);

        return pTextList;
    }

    public class PTextList extends PList implements PTextInterface {

        int numCol = 1;
        private boolean mIsAutoScroll = false;
        PList list;
        NativeArray data = new NativeArray(0);
        private ReturnInterfaceWithReturn mCreateCallback;
        private ReturnInterfaceWithReturn mUpdateCallback;

        private float mTextSize = -1;
        private int mTextColor = -1;
        private Typeface mTextfont;

        PTextList() {
            super(getAppRunner());

            init();
            // props.put("background", "#000000");
            // styler.apply();
            super.init(data, numCol, mCreateCallback, mUpdateCallback);
        }

        private void init() {
            mCreateCallback = r -> {
                PText t = newText("");
                if (mTextColor != -1)
                    t.textColor(mTextColor); //.props.put("textColor", t.props, mTextColor);
                if (mTextSize != -1)
                    t.textSize(mTextSize); // t.props.put("textSize", t.props, mTextSize);

                styler.apply();
                return t;
            };

            mUpdateCallback = r -> {
                PText t = (PText) r.get("view");
                int position = (int) r.get("position");

                t.text((String) data.get(position));
                return null;
            };
        }

        public PTextList add(String text) {
            data.put(data.size(), data, text);
            notifyDataChanged();
            if (mIsAutoScroll) scrollToPosition(data.size() - 1);

            return this;
        }

        public PTextList autoScroll(boolean b) {
            mIsAutoScroll = b;

            return this;
        }

        public PTextList columns(int num) {
            list.setNumCols(num);

            return this;
        }

        @Override
        public View font(Typeface font) {
            mTextfont = font;
            return this;
        }

        @Override
        public View textSize(int size) {
            mTextSize = size;

            return this;
        }

        @Override
        public View textColor(String textColor) {
            mTextColor = Color.parseColor(textColor);

            return this;
        }

        @Override
        public View textColor(int textColor) {
            mTextColor = textColor;

            return this;
        }

        @Override
        public View textSize(float textSize) {
            mTextSize = textSize;

            return this;
        }

        @Override
        public View textStyle(int textStyle) {
            return null;
        }

        @Override
        public View textAlign(int alignment) {
            return null;
        }
    }


    /**
     * Adds a processing view.
     * This Processing view acts quite similar to Processing.org for Android. In fact is the same, although
     * some things work a bit differently to play well with the rest of the framework.
     * <p>
     * - All the Processing functions prepend the Processing (p) object.
     * p.fill(255); p.stroke(); p.rect();
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @return
     * @exampleLink /examples/Others/Processing
     */
    @PhonkMethod(description = "", example = "")
    @PhonkMethodParam(params = {"x", "y", "w", "h"})
    public PProcessing addProcessingCanvas(Object x, Object y, Object w, Object h) {
        // Create the main layout. This is where all the items actually go
        FrameLayout fl = new FrameLayout(getContext());
        fl.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        fl.setId(200 + (int) (200 * Math.random()));

        // Add the view
        addViewAbsolute(fl, x, y, w, h);

        PProcessing p = new PProcessing(getAppRunner());

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.add(fl.getId(), p, String.valueOf(fl.getId()));
        ft.commit();

        return p;
    }

    /**
     * Create a new linear layout
     *
     * @return
     * @advanced
     */
    @PhonkMethod
    public PLinearLayout newLinearLayout() {
        PLinearLayout pLinearLayout = new PLinearLayout(getAppRunner());
        pLinearLayout.orientation("vertical");

        return pLinearLayout;
    }

    /**
     * Adds a linear layout
     *
     * @param x
     * @param y
     * @return
     */
    @PhonkMethod
    public PLinearLayout addLinearLayout(Object x, Object y) {
        PLinearLayout pLinearLayout = newLinearLayout();
        addViewAbsolute(pLinearLayout, x, y, -1, -1);

        return pLinearLayout;
    }

    @PhonkMethod
    public PLinearLayout addLinearLayout(Object x, Object y, Object w, Object h) {
        PLinearLayout pLinearLayout = newLinearLayout();
        addViewAbsolute(pLinearLayout, x, y, w, h);

        return pLinearLayout;
    }

    /**
     * Creates a popup
     *
     * @return
     * @status TOREVIEW
     */
    public PPopupDialogFragment popup() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        PPopupDialogFragment pPopupCustomFragment = PPopupDialogFragment.newInstance(fm);

        return pPopupCustomFragment;
    }

    /**
     * Shows a web in a different screen.
     * Once the web is opened, we loose the control of the script
     *
     * @param url
     * @status TOREVIEW
     * @advanced
     */
    @PhonkMethod
    public void showWeb(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(Color.BLUE);
        builder.addDefaultShareMenuItem();
        builder.setInstantAppsEnabled(true);

        // builder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
        // builder.setExitAnimations(this, R.anim.slide_in_left, R.anim.slide_out_right);

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
    }

    /**
     * Shows a little popup with a given text during t time
     *
     * @param text
     */
    @PhonkMethod
    public void toast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @PhonkMethod
    public void toast(String text, boolean length) {
        int duration = Toast.LENGTH_SHORT;
        if (length) {
            duration = Toast.LENGTH_LONG;
        }
        Toast.makeText(getContext(), text, duration).show();
    }

    /**
     * Clip a view and add a shadow. This only works on Android > 5
     *
     * @param v
     * @param type 0 for rect, 1 for round
     * @param r    roundness
     * @status TOREVIEW
     * @advanced
     */
    @PhonkMethod
    public void clipAndShadow(View v, int type, int r) {
        AndroidUtils.setViewGenericShadow(v, type, 0, 0, v.getWidth(), v.getHeight(), r);
        // v.setElevation();
        // v.setZ();
        // v.animate().
    }

    @PhonkMethod
    public void clipAndShadow(View v, int type, int x, int y, int w, int h, int r) {
        AndroidUtils.setViewGenericShadow(v, type, x, y, w, h, r);
    }


    /*
     * Utilities
     */


    /**
     * Resize a view to a given width and height. If a parameter is -1 then that dimension is not changed
     *
     * @param v
     * @param w
     * @param h
     * @param animated
     * @advanced
     * @status TOREVIEW
     */
    @PhonkMethod
    public void resize(final View v, int w, int h, boolean animated) {
        if (!animated) {
            if (h != -1) {
                v.getLayoutParams().height = h;
            }
            if (w != -1) {
                v.getLayoutParams().width = w;
            }
            v.setLayoutParams(v.getLayoutParams());
        } else {
            int initHeight = v.getLayoutParams().height;
            int initWidth = v.getLayoutParams().width;
            // v.setLayoutParams(v.getLayoutParams());

            ValueAnimator animH = ValueAnimator.ofInt(initHeight, h);
            animH.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                v.getLayoutParams().height = val;
                v.setLayoutParams(v.getLayoutParams());
            });
            animH.setDuration(200);
            animH.start();

            ValueAnimator animW = ValueAnimator.ofInt(initWidth, w);
            animW.addUpdateListener(valueAnimator -> {
                int val = (Integer) valueAnimator.getAnimatedValue();
                v.getLayoutParams().width = val;
                v.setLayoutParams(v.getLayoutParams());
            });
            animW.setDuration(200);
            animW.start();
        }
    }

    /**
     * Move view
     *
     * @param viewHandler
     * @param viewContainer
     * @param callback
     * @status TOREVIEW
     * @advanced
     */
    @PhonkMethod
    public void movable(View viewHandler, View viewContainer, ReturnInterface callback) {
        WidgetHelper.setMovable(viewHandler, viewContainer, callback);
    }

    /**
     * Remove movable
     *
     * @param viewHandler
     * @status TOREVIEW
     * @advanced
     */
    @PhonkMethod
    public void removeMovable(View viewHandler) {
        WidgetHelper.removeMovable(viewHandler);
    }


    /*
    public void onTouch(View view, final ReturnInterface callback) {
    }
    */

    public void onClick(View view, final ReturnInterface callback) {
        view.setOnClickListener(view1 -> callback.event(null));
    }

    class Touch {
        int id;
        Object x, y;
        String action;

        Touch() {

        }
    }

    /**
     * @param view
     * @param callback
     * @status TOREVIEW
     */
    @PhonkMethod
    public void onTouches(View view, final ReturnInterface callback) {
        final PhonkNativeArray ar = new PhonkNativeArray(20);
        final HashMap<Integer, Touch> touches = new HashMap<Integer, Touch>();

        view.setOnTouchListener((view1, motionEvent) -> {

            int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
            int pointerId = motionEvent.getPointerId(pointerIndex);

            boolean ret = false;

            switch (MotionEventCompat.getActionMasked(motionEvent)) {
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_DOWN: {
                    MLog.d(TAG, "down: " + pointerId);

                    Touch touch = touches.get(pointerId);
                    if (touch == null) {
                        Touch t = new Touch();
                        t.id = pointerId;
                        t.x = motionEvent.getX();
                        t.y = motionEvent.getY();
                        t.action = "down";
                        touches.put(pointerId, t);
                    }
                    ret = true;
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    for (int i = 0; i < motionEvent.getPointerCount(); i++) {
                        int id = motionEvent.getPointerId(i);
                        MLog.d(TAG, "move: " + id);

                        Touch t = touches.get(id);
                        t.x = motionEvent.getX(i);
                        t.y = motionEvent.getY(i);
                        t.action = "move";
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_UP:
                    MLog.d(TAG, "up: " + pointerId);
                    Touch t = touches.get(pointerId);
                    t.action = "up";
                    break;
            }


            PhonkNativeArray ar1 = new PhonkNativeArray(touches.size());
            int toRemove = -1;
            int i = 0;

            MLog.d(TAG, ">>>>>>>>>>>>>>>>>>>>> ");

            for (Integer key : touches.keySet()) {
                Touch touch = touches.get(key);

                if (touch.action.equals("up")) {
                    MLog.d(TAG, "to remove");
                    toRemove = key;
                }

                ReturnObject t = new ReturnObject();
                t.put("x", touch.x);
                t.put("y", touch.y);
                t.put("id", touch.id);
                t.put("action", touch.action);
                MLog.d(TAG, "" + i + " " + touch.id + " action " + touch.action);

                ar1.addPE(i, t);
                i++;
            }

            if (toRemove != -1) {
                MLog.d(TAG, "removing " + toRemove + " of " + touches.size());
                touches.remove(toRemove);
            }

            ReturnObject returnObject = new ReturnObject();
            returnObject.put("touches", ar1);
            returnObject.put("count", motionEvent.getPointerCount());

            callback.event(returnObject);

            return ret;
        });
    }

    /**
     * Takes a screenshot of a view in a Bitmap form
     *
     * @param v View
     * @return
     * @status TODO_EXAMPLE
     */
    @PhonkMethod
    public Bitmap takeViewScreenshot(View v) {
        return AndroidUtils.takeScreenshotView(v);
    }

    /*
     * Load thingies
     */


    @Override
    public void __stop() {

    }

}
