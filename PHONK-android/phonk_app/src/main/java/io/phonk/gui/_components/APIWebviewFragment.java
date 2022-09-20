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

package io.phonk.gui._components;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import io.phonk.R;
import io.phonk.gui.settings.UserPreferences;
import io.phonk.helpers.PhonkSettingsHelper;
import io.phonk.runner.base.BaseFragment;
import io.phonk.runner.base.utils.MLog;

@SuppressLint({"NewApi", "ValidFragment"})
public class APIWebviewFragment extends BaseFragment {

    public WebView webView;
    final Handler myHandler = new Handler();
    protected View v;
    private String mUrl = null;
    private final String TAG = APIWebviewFragment.class.getSimpleName();
    private boolean mIsTablet;

    public APIWebviewFragment(String file) {
        super();
        mUrl = file;
    }

    public APIWebviewFragment() {
        super();
    }

    /**
     * Called when the activity is first created.
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        MLog.d(TAG, "LOADED BaseWebView");
        v = inflater.inflate(R.layout.webview, container, false);

        return v;
    }

    public WebView getWebview() {
        return webView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {
            this.mUrl = bundle.getString("url", null);
            this.mIsTablet = bundle.getBoolean("isTablet");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        MLog.d(TAG, "onActivityCreated");
        webView = v.findViewById(R.id.webView1);
        MLog.d(TAG, "Loaded WebView");

        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setDomStorageEnabled(true);
        // WebView.setWebContentsDebuggingEnabled(true);

        settings.setLightTouchEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });

        webView.getSettings().setGeolocationDatabasePath("/data/data/customwebview");

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                MLog.e(TAG, "error" + description);
            }

        });

        if (mUrl != null) {
            webView.loadUrl(mUrl);
        }

        SettingsFromAndroid settingsFromAndroid = new SettingsFromAndroid();
        settingsFromAndroid.isTablet = mIsTablet;
        webView.addJavascriptInterface(settingsFromAndroid, "settingsFromAndroid");
    }

    public void setPage(String Url) {
        webView.loadUrl(Url);
    }

    public void loadViewFromFile(String filename) {
        filename = "file://" + filename;
        webView.loadUrl(filename);
    }

    public class SettingsFromAndroid {
        boolean isTablet = false;

        SettingsFromAndroid() {

        }

        @JavascriptInterface
        public boolean isTablet() {
            return isTablet;
        }

        @JavascriptInterface
        public boolean getWebIde() {
            return (boolean) UserPreferences.getInstance().get("webide_mode");
        }

        @JavascriptInterface
        public void setWebIde(boolean b) {
            UserPreferences.getInstance().set("webide_mode", b).save();

            PhonkSettingsHelper.showRestartMessage(getContext(), v);
        }
    }

}
