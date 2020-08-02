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

import android.animation.ValueAnimator;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.view.MotionEventCompat;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.phonk.runner.AppRunnerFragment;
import io.phonk.runner.R;
import io.phonk.runner.apidoc.annotation.PhonkMethod;
import io.phonk.runner.apidoc.annotation.PhonkObject;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.api.common.ReturnInterface;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.widgets.PPopupDialogFragment;
import io.phonk.runner.apprunner.api.widgets.PToolbar;
import io.phonk.runner.apprunner.api.widgets.StylePropertiesProxy;
import io.phonk.runner.apprunner.api.widgets.WidgetHelper;
import io.phonk.runner.apprunner.interpreter.PhonkNativeArray;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;

@PhonkObject(mergeFrom = "ViewsArea")
public class PUI extends PViewsArea {
    public StylePropertiesProxy theme;
    public StylePropertiesProxy rootStyle = new StylePropertiesProxy();

    private boolean isMainLayoutSetup = false;
    public View mainLayout;
    public ArrayList viewTree = new ArrayList<ViewElement>();
    public int screenWidth;
    public int screenHeight;

    class ViewElement {
        String id;
        String type;
        View ref;
    }

    public PUI(AppRunner appRunner) {
        super(appRunner);
    }

    @Override
    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        if (fragment != null) {
            toolbar = new PToolbar(getAppRunner(), getActivity().getSupportActionBar());
        }
        initializeLayout();
    }

    /**
     * This method creates the basic layout where the user created views will lay out
     * It has to be programatically created since it might be used somewhere else without access to the R file
     *   scriptedLayout
     *     [rep]  uiHolderLayout -> common background
     *       scrollView
     *       absolutelayout
     */
    protected void initializeLayout() {
        if (isMainLayoutSetup) return;
        mainLayout = initMainLayout();

        // here we add the layout
        if (getFragment() != null) {
            getFragment().addScriptedLayout(mainLayout);
        } else if (getService() != null) {
            getService().addScriptedLayout(mainLayout);
        }

        isMainLayoutSetup = true;

        setTheme();
        setStyle();
        background((String) theme.get("background"));

        viewTree.add(mainLayout);
    }

    @PhonkMethod
    public PViewsArea newArea(Object x, Object y, Object w, Object h) {
        PViewsArea pViewsArea = new PViewsArea(mAppRunner);
        View v = pViewsArea.initMainLayout();
        addView(v, x, y, w, h);
        return pViewsArea;
    }

    @PhonkMethod
    public PViewsArea newArea() {
        PViewsArea pViewsArea = new PViewsArea(mAppRunner);
        View v = pViewsArea.initMainLayout();
        return pViewsArea;
    }

    private void setTheme() {
        theme = new StylePropertiesProxy();

        theme.put("background", getContext().getResources().getString(R.color.phonk_backgroundColor));
        theme.put("primary", getContext().getResources().getString(R.color.phonk_colorPrimary));
        theme.put("primaryShade", getContext().getResources().getString(R.color.phonk_colorSecondary_shade));
        theme.put("secondary", getContext().getResources().getString(R.color.phonk_colorSecondary));
        theme.put("secondaryShade", getContext().getResources().getString(R.color.phonk_colorSecondary_shade));
        theme.put("textPrimary", getContext().getResources().getString(R.color.phonk_textColor_secondary));
        theme.put("textSecondary", getContext().getResources().getString(R.color.phonk_textColor_secondary));
        theme.put("animationOnViewAdd", false);

        // if the theme is change then we reapply the styles
        theme.onChange((name, value) -> {
            setStyle();
        });
    }

    private void setStyle() {
        String colorPrimary = (String) theme.get("primary");
        String colorPrimaryShade = (String) theme.get("primaryShade");
        String colorSecondary = (String) theme.get("secondary");
        String colorSecondaryShade = (String) theme.get("secondaryShade");
        String colorTextPrimary = (String) theme.get("textPrimary");
        String colorBackground = (String) theme.get("background");
        String colorTransparent = "#00FFFFFF";

        rootStyle.put("x", rootStyle, 0f);
        rootStyle.put("y", rootStyle, 0f);
        rootStyle.put("w", rootStyle, 0f);
        rootStyle.put("h", rootStyle, 0f);

        rootStyle.put("enabled", rootStyle, true);
        rootStyle.put("opacity", rootStyle, 1.0f);
        rootStyle.put("visibility", rootStyle, "visible");
        rootStyle.put("background", rootStyle, colorTransparent);
        rootStyle.put("backgroundHover", rootStyle, "#88000000");
        rootStyle.put("backgroundPressed", rootStyle, "#88FFFFFF");
        rootStyle.put("backgroundSelected", rootStyle, "#88000000");
        rootStyle.put("backgroundChecked", rootStyle, "#88000000");
        rootStyle.put("borderColor", rootStyle, colorTransparent);
        rootStyle.put("borderWidth", rootStyle, 0);
        rootStyle.put("borderRadius", rootStyle, 0);

        rootStyle.put("src", rootStyle, "");
        rootStyle.put("srcPressed", rootStyle, "");

        rootStyle.put("textColor", rootStyle, colorTextPrimary);
        rootStyle.put("textSize", rootStyle, 16);
        rootStyle.put("textFont", rootStyle, "monospace");
        rootStyle.put("textStyle", rootStyle, "normal");
        rootStyle.put("textAlign", rootStyle, "center");
        rootStyle.put("textTransform", rootStyle, "none");
        rootStyle.put("padding", rootStyle, AndroidUtils.dpToPixels(getContext(), 2));
        /*
        rootStyle.put("paddingLeft", rootStyle, AndroidUtils.dpToPixels(getContext(), 2));
        rootStyle.put("paddingTop", rootStyle, AndroidUtils.dpToPixels(getContext(), 2));
        rootStyle.put("paddingRight", rootStyle, AndroidUtils.dpToPixels(getContext(), 2));
        rootStyle.put("paddingBottom", rootStyle, AndroidUtils.dpToPixels(getContext(), 2));
         */
        rootStyle.put("background", rootStyle, colorSecondaryShade);

        // style.put("animInBefore", style, "this.x(0).y(100)");
        // style.put("animIn", style, "this.animate().x(100)");
        // style.put("animOut", style, "this.animate().x(0)");
    }
    public StylePropertiesProxy getStyle() {
        return rootStyle;
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
     * Set a global theme
     *
     * @param properties
     */
    @PhonkMethod
    public void setTheme(Map<String, Object> properties) {
        MLog.d("qqq", "setTheme");

        theme.eventOnChange = false;
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            theme.put(entry.getKey(), theme, entry.getValue());
        }
        setStyle();
        background((String) theme.get("background"));
        theme.eventOnChange = true;
        MLog.d("qqq", "setTheme 2");
    }

    /**
     * Get the current global theme
     *
     * @return theme
     */
    @PhonkMethod
    public Map getTheme() {
        return theme;
    }

    /**
     * Adds an overlay title to the script
     *
     * @param title
     */
    @PhonkMethod
    public void addTitle(String title) {
        getFragment().changeTitle(title, (String) theme.get("primary"));
    }

    /**
     * Adds an overlay subtitle to the script
     *
     * @param subtitle
     */
    @PhonkMethod
    public void addSubtitle(String subtitle) {
        getFragment().changeSubtitle(subtitle);
    }

    /**
     * Creates a popup
     *
     * @return
     * @status TOREVIEW
     */
    @PhonkMethod
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

    public ArrayList getViewTree() {
        return viewTree;
    }

    @Override
    public void __stop() {
    }

}
