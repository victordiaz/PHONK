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

package io.phonk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.phonk.R;

import io.phonk.appinterpreter.AppRunnerCustom;
import io.phonk.appinterpreter.ProtocoderApp;
import io.phonk.events.Events;
import io.phonk.gui.CombinedFolderAndProjectFragment;
import io.phonk.gui.ConnectionInfoFragment;
import io.phonk.gui._components.APIWebviewFragment;
import io.phonk.gui._components.NewProjectDialogFragment;
import io.phonk.gui.folderchooser.FolderListFragment;
import io.phonk.gui.projectlist.ProjectListFragment;
import io.phonk.gui.settings.UserPreferences;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.gui.EmptyFragment;
import io.phonk.server.PhonkServerService;
import io.phonk.runner.api.PUtil;
import io.phonk.runner.apprunner.AppRunnerHelper;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.network.NetworkUtils;
import io.phonk.runner.base.utils.AndroidUtils;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;

public class MainActivity extends BaseActivity {

    private static final java.lang.String TAG = MainActivity.class.getSimpleName();

    private AppRunnerCustom mAppRunner;

    private Intent mServerIntent;

    private int mCurrentPagerPosition;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private RelativeLayout mHeader;
    private RelativeLayout mHeader2;

    private ImageButton mToggleConnectionInfo;
    private RelativeLayout mConnectionInfo;

    private EmptyFragment mEmptyFragment;
    private FolderListFragment mFolderListFragment;
    private ProjectListFragment mProjectListFragment;
    private ConnectionInfoFragment mConnectionInfoFragment;
    private CombinedFolderAndProjectFragment mCombinedFolderAndProjectFragment;
    private APIWebviewFragment mWebViewFragment;

    private boolean mIsTablet = true;

    private boolean mUiInit = false;
    private boolean isWebIdeMode = false;
    private boolean isServersEnabledOnStart;
    private boolean mShowProjectsInFolder = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // PhonkAppHelper.launchSchedulerList(this);
        EventBus.getDefault().register(this);

        UserPreferences.getInstance().load();
        isWebIdeMode = (boolean) UserPreferences.getInstance().get("webide_mode");
        isServersEnabledOnStart = (boolean) UserPreferences.getInstance().get("servers_enabled_on_start");

        mAppRunner = new AppRunnerCustom(this);
        mAppRunner.initDefaultObjects(AppRunnerHelper.createSettings()).initInterpreter();
        ProtocoderApp protocoderApp = new ProtocoderApp(mAppRunner);
        // protocoderApp.network.checkVersion();
        mAppRunner.interp.eval("device.vibrate(100);");

        // startServers if conf specifies. In webidemode always have to start it
        MLog.d(TAG, "isWebIdeMode " + isWebIdeMode);
        if (isWebIdeMode) startServers();

        loadUI();

        setScreenAlwaysOn((boolean) UserPreferences.getInstance().get("screen_always_on"));

        // execute onLaunch script
        String script = (String) UserPreferences.getInstance().get("launch_script_on_app_launch");
        if (!script.isEmpty()) {
            Project p = new Project(script);
            PhonkAppHelper.launchScript(this, p);
        }

        mAppRunner.pUtil.delay(2000, new PUtil.delayCB() {
            @Override
            public void event() {
                mViewPager.setCurrentItem(1);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        startBroadCastReceiver();

        // send appIsClosed
        Intent i = new Intent("org.protocoder.intent.CLOSED");
        sendBroadcast(i);
        registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(adbBroadcastReceiver);
        unregisterReceiver(connectivityChangeReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopServers();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        MLog.d(TAG, "changing conf");
        removeFragment(mConnectionInfoFragment);
        removeFragment(mEmptyFragment);
        removeFragment(mProjectListFragment);
        removeFragment(mFolderListFragment);
        if (mCombinedFolderAndProjectFragment != null) removeFragment(mCombinedFolderAndProjectFragment);
        RelativeLayout mainContent = (RelativeLayout) findViewById(R.id.main_content);
        mainContent.removeAllViews();
        super.onConfigurationChanged(newConfig);
        loadUI();
    }

    private void loadUI() {
        // load UI
        setContentView(R.layout.activity_main);

        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        if (!mUiInit) {
            mEmptyFragment = EmptyFragment.newInstance();

            mFolderListFragment = FolderListFragment.newInstance(PhonkSettings.EXAMPLES_FOLDER, true);
            mProjectListFragment = ProjectListFragment.newInstance("", true);
            mConnectionInfoFragment = ConnectionInfoFragment.newInstance(mAppRunner);

            if (mIsTablet) {
                mCombinedFolderAndProjectFragment = CombinedFolderAndProjectFragment.newInstance(mFolderListFragment, mProjectListFragment);
            }
        }


        addFragment(mConnectionInfoFragment, R.id.infoLayout, false);

        boolean isLandscapeBig = getResources().getBoolean(R.bool.isLandscapeBig);

        if (!mUiInit) {
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
            mUiInit = true;
        }

        mHeader = (RelativeLayout) findViewById(R.id.header);
        mHeader2 = (RelativeLayout) findViewById(R.id.textgroup2);

        mConnectionInfo = (RelativeLayout) findViewById(R.id.ip_container);

        mToggleConnectionInfo = (ImageButton) findViewById(R.id.toggleConnectionInfo);
        mToggleConnectionInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mConnectionInfo.getVisibility() == View.GONE) mConnectionInfo.setVisibility(View.VISIBLE);
                else mConnectionInfo.setVisibility(View.GONE);
            }
        });

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    MLog.d(TAG, position + " " + positionOffset + " " + positionOffsetPixels);
                    mHeader2.setAlpha(1 - positionOffset);

                    float scale = positionOffset / 5.0f;
                    mHeader2.setScaleX(1 - scale);
                    mHeader2.setScaleY(1 - scale);

                    mHeader.setAlpha(positionOffset);
                }
            }



            @Override
            public void onPageSelected(int position) {
                mCurrentPagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                MLog.d("selected", "state " + state + " " + mCurrentPagerPosition);
            }
        });

        final ImageButton moreOptionsButton = (ImageButton) findViewById(R.id.more_options);
        moreOptionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu myPopup = new PopupMenu(MainActivity.this, moreOptionsButton);
                myPopup.inflate(R.menu.more_options);
                myPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final MenuItem menuItem) {
                        int itemId = menuItem.getItemId();

                        if (itemId == R.id.more_options_new) {
                            PhonkAppHelper.newProjectDialog(MainActivity.this);
                            return true;
                        } else if (itemId == R.id.more_options_settings) {
                            PhonkAppHelper.launchSettings(MainActivity.this);
                            return true;
                        } else if (itemId == R.id.more_options_help) {
                            PhonkAppHelper.launchHelp(MainActivity.this);
                            return true;
                        }  else if (itemId == R.id.more_options_about) {
                            PhonkAppHelper.launchHelp(MainActivity.this);
                            return true;
                        }

                        return false;
                    }
                });

                myPopup.show();
            }
        });
    }

    public void loadWebIde() {
        MLog.d(TAG, "loadWebIde");

        if (mWebViewFragment != null) return;

        FrameLayout fl = (FrameLayout) findViewById(R.id.fragmentEditor);
        fl.setVisibility(View.VISIBLE);
        MLog.d(TAG, "using webide");
        mWebViewFragment = new APIWebviewFragment();

        Bundle bundle = new Bundle();
        // String url = "http://127.0.0.1:8585";
        String url = "http://10.0.2.2:8080";
        bundle.putString("url", url);
        bundle.putBoolean("isTablet", mIsTablet);
        mWebViewFragment.setArguments(bundle);

        addFragment(mWebViewFragment, R.id.fragmentEditor, "qq");

        // mWebViewFragment.webView.loadUrl(url);
    }

    public void createProjectDialog() {
        FragmentManager fm = getSupportFragmentManager();
        NewProjectDialogFragment newProjectDialog = new NewProjectDialogFragment();
        newProjectDialog.show(fm, "fragment_edit_name");

        String[] templates = PhonkScriptHelper.listTemplates(this);
        for (String template : templates) {
            MLog.d(TAG, "template " + template);
        }

        newProjectDialog.setListener(new NewProjectDialogFragment.NewProjectDialogListener() {
            @Override
            public void onFinishEditDialog(String inputText) {
                String template = "default";
                Toast.makeText(MainActivity.this, "Creating " + inputText, Toast.LENGTH_SHORT).show();
                Project p = PhonkScriptHelper.createNewProject(MainActivity.this, template, "user_projects/User Projects/", inputText);
                EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_NEW, p));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /*
     * This broadcast will receive JS commands if is in debug mode, useful to debug the app through adb
     */
    BroadcastReceiver adbBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String cmd = intent.getStringExtra("cmd");
            MLog.d(TAG, "executing >> " + cmd);
            mAppRunner.interp.eval(cmd);
        }
    };

    private void startBroadCastReceiver() {
        if (PhonkSettings.DEBUG) {
            //execute commands from intents
            //ie: adb shell am broadcast -a org.protocoder.intent.EXECUTE --es cmd "device.vibrate(100)"

            IntentFilter filterSend = new IntentFilter();
            filterSend.addAction("org.protocoder.intent.EXECUTE");
            registerReceiver(adbBroadcastReceiver, filterSend);
        }
    }

    /*
     * Server
     */
    private void startServers() {
        MLog.d(TAG, "starting servers");
        mServerIntent = new Intent(this, PhonkServerService.class);
        //serverIntent.putExtra(Project.FOLDER, folder);
        startService(mServerIntent);
    }

    private void stopServers() {
        stopService(mServerIntent);
    }

    // execute lines
    @Subscribe
    public void onEventMainThread(Events.ExecuteCodeEvent e) {
        String code = e.getCode();
        MLog.d(TAG, "event -> " + code);

        if (PhonkSettings.DEBUG) {
            mAppRunner.interp.eval(code);
        }
    }

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        if (e.getAction() == Events.CLOSE_APP) { }
    }

    // folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen e) {
        MLog.d(TAG, "< Event (folderChosen)");
        mViewPager.setCurrentItem(2, true);
    }

    @Subscribe
    public void onEventMainThread(Events.AppUiEvent e) {
        String action = e.getAction();
        Object value = e.getValue();
        MLog.d(TAG, "got AppUiEvent "  + action);

        switch (action) {
            case "page":
                mViewPager.setCurrentItem((int) value);
                break;
            case "stopServers":
                stopServers();
                break;
            case "startServers":
                startServers();
                break;
            case "serversStarted":
                // show webview
                if (isWebIdeMode) loadWebIde();
                break;
            case "recreate":
                // recreate();
                // finish();
                break;

        }
    }

    /*
   * Network Connectivity listener
   */
    BroadcastReceiver connectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d(TAG, "connectivity changed");
            AndroidUtils.debugIntent("connectivityChangerReceiver", intent);

            // check if there is a WIFI connection or we can connect via USB
            if (NetworkUtils.getLocalIpAddress(MainActivity.this).get("ip").equals("127.0.0.1")) {
                MLog.d(TAG, "No WIFI, still you can hack via USB using the adb command");
                EventBus.getDefault().post(new Events.Connection("none", ""));
            } else {
                MLog.d(TAG, "Hack via your browser @ http://" + NetworkUtils.getLocalIpAddress(MainActivity.this) + ":" + PhonkSettings.HTTP_PORT);
                String ip = NetworkUtils.getLocalIpAddress(MainActivity.this).get("ip") + ":" + PhonkSettings.HTTP_PORT;
                String type = (String) NetworkUtils.getLocalIpAddress(MainActivity.this).get("type");
                EventBus.getDefault().post(new Events.Connection(type, ip));
            }
        }
    };


    public class SectionsPagerAdapter extends PagerAdapter {

        private final FragmentManager mFragmentManager;
        private final SparseArray mFragments;
        private FragmentTransaction mCurTransaction;

        public SectionsPagerAdapter(FragmentManager fm) {
            mFragmentManager = fm;
            mFragments = new SparseArray<>();
            mFragments.put(0, mEmptyFragment);

            if (mIsTablet) {
                mFragments.put(1, mCombinedFolderAndProjectFragment);
            } else {
                mFragments.put(1, mFolderListFragment);
                mFragments.put(2, mProjectListFragment);
            }
        }

        public Fragment getItem(int position) {
            return (Fragment) mFragments.get(position);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = getItem(position);
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }

            mCurTransaction.add(container.getId(), fragment, "fragment:"+position);
            MLog.d("fff", "instantiate" + position + mProjectListFragment);

            return mFragments.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.detach((Fragment) mFragments.get(position));
            // mFragments.remove(position);
            MLog.d("fff", "destroying item" + mProjectListFragment);
        }

        @Override
        public boolean isViewFromObject(View view, Object fragment) {
            return ((Fragment) fragment).getView() == view;
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public int getCount() {
            // MLog.d("fff", "getCount " + mFragments.size());
            return mFragments.size();
        }


        /*
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Intro";
                case 1: return "Folder";
                case 2: return "Projects";
            }
            return null;
        }
        */
    }

    @Override
    public void onBackPressed() {
        if (mCurrentPagerPosition == 2) {
            mViewPager.setCurrentItem(1, true);
        } else {
            super.onBackPressed();
        }
    }
}
