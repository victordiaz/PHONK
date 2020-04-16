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

import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Map;

import io.phonk.runner.apprunner.AppRunner;

public class PWebView extends WebView implements PViewMethodsInterface {

    private final AppRunner mAppRunner;

    public StyleProperties props = new StyleProperties();
    private Styler styler;

    public PWebView(AppRunner appRunner) {
        super(appRunner.getAppContext());
        mAppRunner = appRunner;
        styler = new Styler(appRunner, this, props);
        // styler.apply();

        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        WebSettings webSettings = this.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

        this.clearCache(false);
        this.setBackgroundColor(0x00000000);

        this.requestFocus(View.FOCUS_DOWN);
        this.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                    if (!v.hasFocus()) {
                        v.requestFocus();
                    }
                    break;
            }
            return false;
        });

        WebViewClient webViewClient = new CustomWebViewClient();
        this.setWebViewClient(webViewClient);
        this.addJavascriptInterface(mAppRunner.pApp, "app");
    }

    public void loadData(String content) {
        this.loadData(content, "text/html", "utf-8");
    }

    public void loadFile(String fileName) {
        String path = mAppRunner.getProject().getFullPathForFile(fileName);
        loadUrl("file://" + path);
    }

    public void loadUrl(String url) {
        super.loadUrl(url);
    }

    // http://stackoverflow.com/questions/13257990/android-webview-inside-scrollview-scrolls-only-scrollview
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestDisallowInterceptTouchEvent(true);
        return super.onTouchEvent(event);
    }

    private class CustomWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //load the url
            loadUrl(url);

            // return true to tell that we handled the url
            return true;
        }
    }

    @Override
    public void set(float x, float y, float w, float h) {
        styler.setLayoutProps(x, y, w, h);
    }

    @Override
    public void setStyle(Map style) {
        styler.setStyle(style);
    }

    @Override
    public Map getStyle() {
        return props;
    }

}
