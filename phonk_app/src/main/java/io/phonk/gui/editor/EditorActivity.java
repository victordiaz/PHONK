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

package io.phonk.gui.editor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.gui._components.APIWebviewFragment;
import io.phonk.gui.filemanager.FileManagerFragment;
import io.phonk.gui.filepreviewer.FilePreviewerFragment;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkAppHelper;
import io.phonk.runner.base.BaseActivity;
import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Project;
import io.phonk.server.model.ProtoFile;

public class EditorActivity extends BaseActivity {

    private final String TAG = EditorActivity.class.getSimpleName();

    private static final String FRAGMENT_EDITOR = "11";
    private static final String FRAGMENT_FILE_MANAGER  = "12";
    private static final String FRAGMENT_FILE_PREVIEWER = "21";

    private Menu mMenu;
    private static final int MENU_RUN = 8;
    private static final int MENU_SAVE = 9;
    // private static final int MENU_BACK = 10;
    private static final int MENU_FILES = 11;
    private static final int MENU_API = 12;
    private static final int MENU_INCREASE_FONT = 13;
    private static final int MENU_DECREASE_FONT = 14;

    private boolean isTablet;

    private EditorFragment editorFragment;
    private FileManagerFragment fileFragment;
    private APIWebviewFragment webviewFragment;

    private FilePreviewerFragment filePreviewerFragment;

    // drawers
    private boolean showFilesDrawer = false;
    private boolean showAPIDrawer = false;
    private Project mCurrentProject;

    // here we store the opened files with a flag that indicates if the file has been modified (true) or not (false)
    HashMap<String, Boolean> openedFiles = new HashMap<>();

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editor_activity);
        isTablet = getResources().getBoolean(R.bool.isTablet);

        setupActivity();

        // Get the bundle and pass it to the fragment.
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        if (null != intent) {
            String folder = intent.getStringExtra(Project.FOLDER);
            String name = intent.getStringExtra(Project.NAME);
            mCurrentProject = new Project(folder, name);
            setProjectTitleAndSubtitle(mCurrentProject.getName(), PhonkSettings.MAIN_FILENAME);
        }

        addEditorFragment(savedInstanceState);
        addFilePreviewerFragment(savedInstanceState);

        if (isTablet) addFileManagerDrawer(savedInstanceState, true);

        // showFileManagerDrawer(false);
    }

    /**
     * Load a project
     */
    public void loadProject(String folder, String name) {
        mCurrentProject = new Project(folder, name);
        loadFileInEditor(mCurrentProject.getName(), PhonkSettings.MAIN_FILENAME);
    }

    /**
     * Load a file within the project
     */
    public void loadFileInEditor(String folder, String fileName) {
        setProjectTitleAndSubtitle(mCurrentProject.getName(), fileName);
        editorFragment.loadFile(mCurrentProject.getFullPath(), fileName);
        openedFiles.put(mCurrentProject.getFullPathForFile(fileName), false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void setupActivity() {
        super.setupActivity();
        setSupportActionBar(mToolbar);

        enableBackOnToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.mMenu = menu;
        menu.clear();
        menu.add(1, MENU_RUN, 0, "Run Project").setIcon(R.drawable.ic_play_arrow_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(1, MENU_SAVE, 0, "Save File").setIcon(R.drawable.ic_save_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (!isTablet) menu.add(1, MENU_FILES, 0, "Show Project Files").setIcon(R.drawable.ic_list_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(1, MENU_API, 0, "API").setIcon(R.drawable.ic_chrome_reader_mode_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add(1, MENU_INCREASE_FONT, 0, "Increase font").setIcon(R.drawable.ic_zoom_in_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menu.add(1, MENU_DECREASE_FONT, 0, "Decrease font").setIcon(R.drawable.ic_zoom_out_black_24dp).setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        // onOptionsItemSelected(mMenu.findItem(MENU_FILES));

        return super.onCreateOptionsMenu(menu);
    }

    public void saveAndRun() {
        Toast.makeText(this, "Code saved and launched", Toast.LENGTH_SHORT).show();
        save();
        run();
    }

    public void onlySave() {
        Toast.makeText(this, "Code saved", Toast.LENGTH_SHORT).show();
        save();
    }


    /**
     * Run the current project
     */
    public void run() {
        PhonkAppHelper.launchScript(this, mCurrentProject);
    }

    public void save() {
        editorFragment.saveFile();
        EventBus.getDefault().post(new Events.EditorEvent(Events.EDITOR_ALL_FILE_STATUS, null));
    }

    public void saveAll() {
        // editorFragment;
    }

    public void toggleFilesDrawer() {
        MenuItem menuFiles = mMenu.findItem(MENU_FILES).setChecked(showFilesDrawer);

        showFilesDrawer = !showFilesDrawer;
        addFileManagerDrawer(null, showFilesDrawer);
        // showFileManagerDrawer(showFilesDrawer);
    }

    public void toggleApiDrawer() {
        MenuItem menuApi = mMenu.findItem(MENU_API).setChecked(showFilesDrawer);

        showAPIDrawer = !showAPIDrawer;
        showAPIDrawer(showAPIDrawer);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case MENU_RUN:
                saveAndRun();
                return true;

            case MENU_SAVE:
                onlySave();

                return true;

            case MENU_FILES:
                toggleFilesDrawer();

                return true;

            case MENU_API:
                toggleApiDrawer();

                return true;

            case MENU_INCREASE_FONT:
                editorFragment.increaseFont();

                return true;

            case MENU_DECREASE_FONT:
                editorFragment.decreaseFont();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        if (event.isCtrlPressed()) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_R:
                    saveAndRun();
                    break;

                case KeyEvent.KEYCODE_S:
                    onlySave();
                    break;

                case KeyEvent.KEYCODE_F:
                    toggleApiDrawer();

                    break;
            }
        }
        return super.onKeyShortcut(keyCode, event);
    }

    /**
     *  Toggle API drawer
     */
    public void showAPIDrawer(boolean b) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_out_left,
                R.anim.slide_out_left);

        if (b) {
            webviewFragment = new APIWebviewFragment();
            Bundle bundle = new Bundle();
            bundle.putString("url", "http://localhost:8585/reference.html");
            webviewFragment.setArguments(bundle);
            ft.add(R.id.fragmentWebview, webviewFragment).addToBackStack(null);

            editorFragment.getView().animate().translationX(-50).setDuration(500).start();
        } else {
            editorFragment.getView().animate().translationX(0).setDuration(500).start();
            ft.remove(webviewFragment);
        }

        ft.commit();
    }

    public void addFileManagerDrawer(Bundle savedInstance, boolean b) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left);

        if (b) {

            if (savedInstance == null) {
                fileFragment = FileManagerFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString(FileManagerFragment.ROOT_FOLDER, mCurrentProject.getFullPath());

                // we pass the initial route to hide
                bundle.putString(FileManagerFragment.PATH_HIDE_PATH_FROM, mCurrentProject.getPathPrev());
                fileFragment.setArguments(bundle);

                if (isTablet) {
                    ft.add(R.id.fragmentFileManager, fileFragment, FRAGMENT_FILE_PREVIEWER);
                } else {
                    ft.add(R.id.fragmentFileManager, fileFragment, FRAGMENT_FILE_PREVIEWER).addToBackStack("filemanager");
                }
            } else {
                if (isTablet) {
                    filePreviewerFragment = (FilePreviewerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_FILE_PREVIEWER);
                } else {
                    filePreviewerFragment = (FilePreviewerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_FILE_PREVIEWER);
                }
            }

        } else {
            ft.remove(fileFragment);
        }

        ft.commit();
    }

    private void addEditorFragment(Bundle savedInstance) {
        if (savedInstance == null) {
            Bundle bundle = new Bundle();
            bundle.putString(EditorFragment.FILE_PATH, mCurrentProject.getFullPath());
            bundle.putString(EditorFragment.FILE_NAME, PhonkSettings.MAIN_FILENAME);
            editorFragment = EditorFragment.newInstance(bundle);
            FrameLayout fl = (FrameLayout) findViewById(R.id.editor_container);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(fl.getId(), editorFragment, FRAGMENT_EDITOR);
            ft.commit();
        } else {
            editorFragment = (EditorFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_EDITOR);
        }
    }

    private void addFilePreviewerFragment(Bundle savedInstance) {
        if (savedInstance == null) {
            filePreviewerFragment = FilePreviewerFragment.newInstance(savedInstance);
            FrameLayout fl = (FrameLayout) findViewById(R.id.filepreviewer_container);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(fl.getId(), filePreviewerFragment, FRAGMENT_FILE_PREVIEWER);
            ft.commit();
        } else {
            filePreviewerFragment = (FilePreviewerFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_FILE_PREVIEWER);
        }
    }

    /**
     * Show / hide file manager
     */
    public void showFileManagerDrawer(boolean show) {
        FrameLayout fileManager = (FrameLayout) findViewById(R.id.fragmentFileManager);

        if (show) {
            fileManager.setVisibility(View.VISIBLE);
        } else {
            fileManager.setVisibility(View.GONE);
        }
    }

    public void showFilePreviewerFragment(boolean show) {
        FrameLayout filePreviewerContainer = (FrameLayout) findViewById(R.id.filepreviewer_container);

        if (show) {
            filePreviewerContainer.setVisibility(View.VISIBLE);
        } else {
            filePreviewerContainer.setVisibility(View.GONE);
        }
    }

    public void addAFile() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        // startActivityForResult(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setProjectTitleAndSubtitle(String projectName, String fileName) {
        MLog.d("qq2", "project: " + projectName);

        mToolbar.setTitle(projectName);
        mToolbar.setSubtitle(fileName);
    }

    // load script
    @Subscribe (sticky = true)
    public void onEventMainThread(Events.EditorEvent e) {
        if (e.getAction().equals(Events.EDITOR_FILE_INTENT_LOAD)) {
            ProtoFile f = e.getProtofile();

            // check type
            String type = checkType(f.name);

            // if preview
            if (type != null) {
                EventBus.getDefault().post(new Events.EditorEvent(Events.EDITOR_FILE_PREVIEW, f, type));
                showFilePreviewerFragment(true);
            } else {
                showFilePreviewerFragment(false);
                EventBus.getDefault().post(new Events.EditorEvent(Events.EDITOR_FILE_LOAD, f));
            }
            setProjectTitleAndSubtitle(mCurrentProject.getName(), f.name);
        } else if (e.getAction().equals(Events.EDITOR_FILE_CHANGED)) {
            openedFiles.put(e.getProtofile().getFullPath(), true);
        }
    }


    private String checkType(String name) {
        String extension = FileIO.getFileExtension(name);
        String type = null;

        if (extension.equals("png") || extension.equals("jpg") || extension.equals(".jpeg")) {
            type = "image";
        } else if (extension.equals("avi") || extension.equals("mpg") || extension.equals("mpeg") || extension.equals("mp4")){
            type = "video";
        }

        return type;
    }
}
