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

package io.phonk.runner;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import io.phonk.runner.api.other.PLiveCodingFeedback;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.apprunner.AppRunnerInterpreter;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressLint("NewApi")
public class AppRunnerFragment extends Fragment {

    private static final String TAG = AppRunnerFragment.class.getSimpleName();
    private static Map<String, Object> mScriptSettings;

    private AppRunner mAppRunner;
    private AppRunnerActivity mActivity;
    private Context mContext;
    private FileObserver fileObserver;

    // Layout stuff
    public  RelativeLayout mainLayout;
    private RelativeLayout parentScriptedLayout;
    private RelativeLayout infoLayout;

    public  PLiveCodingFeedback liveCoding;
    private View mMainView;
    private TextView txtTitle;
    private TextView txtSubtitle;

    String name = "defaultName";
    String folder = "defaulFolder";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        this.mContext = context;
        mActivity = (AppRunnerActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // init the layout and pass it to the activity
        mMainView = initLayout();

        // create the apprunner
        mAppRunner = new AppRunner(mContext);
        mAppRunner.initDefaultObjects(this, mScriptSettings);

        // get parameters and set them in the AppRunner
        Bundle bundle = getArguments();
        mAppRunner.pApp.intentData = bundle;

        MLog.d(TAG, "bundle " + bundle);

        if (bundle != null) {
            folder = bundle.getString(Project.FOLDER, "");
            name = bundle.getString(Project.NAME, "");
            // String settings = bundle.getString(Project.SETTINGS, "");

            mAppRunner.loadProject(folder, name);
        }

        mAppRunner.mIntentPrefixScript = bundle.getString(Project.PREFIX, "");
        mAppRunner.mIntentCode = bundle.getString(Project.INTENTCODE, "");
        mAppRunner.mIntentPostfixScript = bundle.getString(Project.POSTFIX, "");

        mAppRunner.initInterpreter();

        mAppRunner.pDevice.deviceId = bundle.getString(Project.DEVICE_ID, "");
        // mAppRunner.pUi.screenOrientation("portrait");
        mAppRunner.pUi.updateScreenSizes();
        mAppRunner.pUi.toolbar.title(name);
        mAppRunner.pUi.toolbar.show(false);
        mAppRunner.pApp.folder = folder;
        mAppRunner.pApp.name = name;

        return mMainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // catch errors and send them to the WebIDE or the app console
        AppRunnerInterpreter.InterpreterInfo appRunnerCb = new AppRunnerInterpreter.InterpreterInfo() {
            @Override
            public void onError(int resultType, Object message) {
                mAppRunner.pConsole.p_error(resultType, message);
            }
        };
        mAppRunner.interp.addListener(appRunnerCb);

        MLog.d(TAG, "QQ initProject");
        mAppRunner.initProject();

        // nfc
        mActivity.initializeNFC();

        // file observer will notify project file changes
        startFileObserver();

        // Call the onCreate JavaScript function.
        if (mAppRunner.interp != null) mAppRunner.interp.callJsFunction("onCreate", savedInstanceState);
    }

    public static AppRunnerFragment newInstance(Bundle bundle, Map<String, Object> scriptSettings) {
        mScriptSettings = scriptSettings;
        AppRunnerFragment myFragment = new AppRunnerFragment();
        myFragment.setArguments(bundle);

        return myFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAppRunner.interp != null) mAppRunner.interp.callJsFunction("onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        MLog.d(TAG, "onPause");

        if (mAppRunner.interp != null) mAppRunner.interp.callJsFunction("onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.d(TAG, "onDestroy");
        if (mAppRunner.interp != null) mAppRunner.interp.callJsFunction("onDestroy");
        mAppRunner.byebye();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    public void addScriptedLayout(RelativeLayout scriptedUILayout) {
        parentScriptedLayout.addView(scriptedUILayout);
    }

    public RelativeLayout initLayout() {
        View v = getLayoutInflater(null).inflate(R.layout.apprunner_fragment, null);

        // add main layout
        mainLayout = v.findViewById(R.id.main);

        // set the parent
        parentScriptedLayout = v.findViewById(R.id.scriptedLayout);

        liveCoding = new PLiveCodingFeedback(mContext);
        mainLayout.addView(liveCoding.add());

        infoLayout = v.findViewById(R.id.infoLayout);
        txtTitle = v.findViewById(R.id.txtTitle);
        txtSubtitle = v.findViewById(R.id.txtSubtitle);

        return mainLayout;
    }

    public void startFileObserver() {

        if (mAppRunner.mIsProjectLoaded) {

            MLog.d(TAG, "fileObserver -> ");

            MLog.d(TAG, "qq1: " + mAppRunner);
            MLog.d(TAG, "qq2: " + mAppRunner.getProject());
            MLog.d(TAG, "qq3: " + mAppRunner.getProject().getSandboxPath());

            // set up a file observer to watch this directory on sd card
            fileObserver = new FileObserver(mAppRunner.getProject().getFullPath(), FileObserver.CREATE | FileObserver.DELETE) {

                @Override
                public void onEvent(int event, String file) {
                    JSONObject msg = new JSONObject();
                    String action = null;

                    if ((FileObserver.CREATE & event) != 0) {
                        MLog.d(TAG, "created " + file);
                        action = "new_files_in_project";

                    } else if ((FileObserver.DELETE & event) != 0) {
                        MLog.d(TAG, "deleted file " + file);
                        action = "deleted_files_in_project";
                    }

                    try {
                        msg.put("action", action);
                        msg.put("type", "ide");
                        //TODO change to events
                        //IDEcommunication.getInstance(mActivity).send(msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
        }
    }

    public PLiveCodingFeedback liveCodingFeedback() {
        return liveCoding;
    }

    public AppRunner getAppRunner() {
        return mAppRunner;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermission(String... p) {
        List<String> requiredPermissions = new ArrayList<String>();

        for (int i = 0; i < p.length; i++) {
            requiredPermissions.add(p[i]);
        }

        // check if permission is already granted
        for (int i = 0; i < requiredPermissions.size(); i++) {
            String permissionName = requiredPermissions.get(i);
            int isGranted1 = getActivity().checkSelfPermission(permissionName);
            int isGranted2 = isGranted1 & PackageManager.PERMISSION_GRANTED;

            MLog.d(TAG, permissionName + " " + isGranted1 + " " + isGranted2);
        }

    }

    public TextView changeTitle(String title) {
        txtTitle.setText(title);
        txtTitle.setAlpha(0.0f);
        txtTitle.setX(-100f);
        txtTitle.setVisibility(View.VISIBLE);
        txtTitle.animate().x(50).alpha(1.0f).setStartDelay(300);
        txtTitle.setEnabled(false);

        return txtTitle;
    }

    public TextView changeSubtitle(final String subtitle) {
        txtSubtitle.setText(subtitle);
        txtSubtitle.setX(-100f);
        txtSubtitle.setAlpha(0.0f);
        txtSubtitle.setVisibility(View.VISIBLE);

        // invalidating the text because sometimes when overlaying OpenGL surfaces the view is not fully rendered
        txtSubtitle.animate().x(50).alpha(1.0f).setStartDelay(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                txtSubtitle.setText(subtitle);
                // txtSubtitle.invalidate();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        txtSubtitle.setEnabled(false);

        return txtSubtitle;
    }
}
