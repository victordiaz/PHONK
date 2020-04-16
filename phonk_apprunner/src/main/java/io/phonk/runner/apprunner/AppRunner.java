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

package io.phonk.runner.apprunner;

import android.content.Context;

import org.mozilla.javascript.Scriptable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.phonk.runner.AppRunnerFragment;
import io.phonk.runner.apprunner.api.PApp;
import io.phonk.runner.apprunner.api.PBoards;
import io.phonk.runner.apprunner.api.PConsole;
import io.phonk.runner.apprunner.api.PDashboard;
import io.phonk.runner.apprunner.api.PDevice;
import io.phonk.runner.apprunner.api.PFileIO;
import io.phonk.runner.apprunner.api.PMedia;
import io.phonk.runner.apprunner.api.PNetwork;
import io.phonk.runner.apprunner.api.PPhonk;
import io.phonk.runner.apprunner.api.PSensors;
import io.phonk.runner.apprunner.api.PUI;
import io.phonk.runner.apprunner.api.PUtil;
import io.phonk.runner.apprunner.api.common.ReturnObject;
import io.phonk.runner.apprunner.api.other.WhatIsRunning;
import io.phonk.runner.apprunner.interpreter.AppRunnerInterpreter;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.network.NetworkUtils;
import io.phonk.runner.base.utils.MLog;

public class AppRunner {

    private static final String TAG = AppRunner.class.getSimpleName();

    private final Context mContext;
    public boolean hasUserInterface = false;
    public WhatIsRunning whatIsRunning;

    //Project properties
    private Project mProject;

    private String mScript;
    public String mIntentPrefixScript = "";
    public String mIntentCode = "";
    public String mIntentPostfixScript = "";

    public boolean mIsProjectLoaded = false;

    //Interpreter
    public AppRunnerInterpreter interp;

    //API Objects for the interpreter
    public PApp pApp;
    public PBoards pBoards;
    public PConsole pConsole;
    public PDashboard pDashboard;
    public PDevice pDevice;
    public PFileIO pFileIO;
    public PMedia pMedia;
    public PNetwork pNetwork;
    public PPhonk pPhonk;
    public PSensors pSensors;
    public PUI pUi;
    public PUtil pUtil;

    public AppRunner(Context context) {
        this.mContext = context;
        whatIsRunning = new WhatIsRunning();
    }

    public AppRunner initDefaultObjects(HashMap<String, Object> settings) {
        initDefaultObjects(null, settings);

        return this;
    }

    public AppRunner initDefaultObjects(AppRunnerFragment appRunnerFragment, Map<String, Object> mScriptSettings) {
        hasUserInterface = true;

        // instantiate the objects that can be accessed from the interpreter

        // the reason to call initForParentFragment is because the class depends on the fragment ui.
        // its not very clean and at some point it will be change to a more elegant solution that will allow to
        // have services
        pApp = new PApp(this);
        pApp.initForParentFragment(appRunnerFragment);
        pApp.settings = mScriptSettings;
        pBoards = new PBoards(this);
        pConsole = new PConsole(this);
        pDashboard = new PDashboard(this);
        pDevice = new PDevice(this);
        pDevice.initForParentFragment(appRunnerFragment);
        pFileIO = new PFileIO(this);
        pMedia = new PMedia(this);
        pMedia.initForParentFragment(appRunnerFragment);
        pNetwork = new PNetwork(this);
        pNetwork.initForParentFragment(appRunnerFragment);
        pPhonk = new PPhonk(this);
        pSensors = new PSensors(this);
        pSensors.initForParentFragment(appRunnerFragment);
        pUi = new PUI(this);
        pUi.initForParentFragment(appRunnerFragment);
        pUtil = new PUtil(this);

        return this;
    }

    public AppRunner initInterpreter() {

        //create mContext new interpreter and add the objects to it
        interp = new AppRunnerInterpreter(this);
        interp.addJavaObjectToJs("app", pApp);
        interp.addJavaObjectToJs("boards", pBoards);
        interp.addJavaObjectToJs("console", pConsole);
        interp.addJavaObjectToJs("dashboard", pDashboard);
        interp.addJavaObjectToJs("device", pDevice);
        interp.addJavaObjectToJs("fileio", pFileIO);
        interp.addJavaObjectToJs("media", pMedia);
        interp.addJavaObjectToJs("network", pNetwork);
        interp.addJavaObjectToJs("protocoder", pPhonk);
        interp.addJavaObjectToJs("sensors", pSensors);
        interp.addJavaObjectToJs("ui", pUi);
        interp.addJavaObjectToJs("util", pUtil);

        return this;
    }

    public void addObject(String name, Object object) {
        interp.addJavaObjectToJs(name, object);
    }

    public AppRunner loadProject(String folder, String name) {
        mProject = new Project(folder, name);
        mScript = AppRunnerHelper.getCode(mProject);

        return this;
    }

    public AppRunner initProject() {

        // preloaded script
        if (!mIntentPrefixScript.isEmpty()) evaluate(mIntentPrefixScript, "");

        // run the script
        if (null != mScript) {
            MLog.d(TAG, "evaluate qq");
            evaluate(mScript, mProject.getName());
        }
        // can accept intent code if no project is loaded
        if (!mIsProjectLoaded) {
            evaluate(mIntentCode, "");
        }

        // script postfix
        if (!mIntentPostfixScript.isEmpty()) evaluate(mIntentPostfixScript, "");

        // call the javascript method setup
        // interp.callJsFunction("setup");

        return this;
    }

    public void evaluate(String script, String projectName) {
        if (interp != null) interp.eval(script, projectName);
    }

    public Context getAppContext() {
        return mContext.getApplicationContext();
    }

    public Scriptable newArray() {
        return interp.newNativeArray();
    }

    public Scriptable newArray(File[] files) {
        return interp.newNativeArrayFrom(files);
    }

    boolean finished = false;
    public void byebye() {
        if (!finished) {
            finished = true;
            whatIsRunning.stopAll();
            // interp.stop();
            // interp = null;
        }
    }

    public Project getProject() {
        return mProject;
    }

    public String getServingUrl() {
        ReturnObject networkInfo = NetworkUtils.getLocalIpAddress(getAppContext());
        String ip = (String) networkInfo.get("ip");
        MLog.d(TAG, ip);
        String url = "http://" + ip + ":" + AppRunnerSettings.SERVER_PORT + File.separator;

        return url;
    }
}
