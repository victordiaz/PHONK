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
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.BaseFragment;
import io.phonk.runner.base.utils.FileIO;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.base.utils.TextUtils;
import io.phonk.server.model.ProtoFile;

@SuppressLint("NewApi")
public class EditorFragment extends BaseFragment {

    protected static final String TAG = EditorFragment.class.getSimpleName();
    public static final String FILE_NAME = "file_name";
    public static final String FILE_PATH = "file_path";

    public interface EditorFragmentListener {
        void onLoad();

        void onLineTouched();
    }

    private EditText mEdit;
    private View v;
    private EditorFragmentListener listener;

    // settings
    private final EditorSettings mEditorSettings = new EditorSettings();
    private final HashMap<String, String> openedFiles = new HashMap<>();
    private ProtoFile mCurrentFile;

    public EditorFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.editor_fragment, container, false);

        bindUI();

        // editor settings
        mEditorSettings.fontSize = mEdit.getTextSize();
        TextUtils.changeFont(getActivity(), mEdit, mEditorSettings.font);

        setExtraKeysBar();

        setHasOptionsMenu(true); // Set actionbar override

        /* get the code file */
        Bundle bundle = getArguments();
        if (bundle != null) {
            String folder = bundle.getString(FILE_PATH);
            String name = bundle.getString(FILE_NAME);
            loadFile(folder, name);
        }

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Bind the UI
     */
    private void bindUI() {
        mEdit = v.findViewById(R.id.editText1);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String originalText = openedFiles.get(mCurrentFile.getFullPath());
                String currentText = s.toString();

                // check if text is changed
                if (!originalText.equals(currentText)) {
                    // update current code string
                    openedFiles.put(mCurrentFile.getFullPath(), currentText);

                    // notifiy that a file is changed
                    EventBus.getDefault().post(new Events.EditorEvent(Events.EDITOR_FILE_CHANGED, mCurrentFile));
                }

            }
        });
    }

    public void increaseFont() {
        MLog.d(TAG, "size: " + mEdit.getTextSize() + " " + mEditorSettings.fontSize);
        mEditorSettings.fontSize += 1;
        mEdit.setTextSize(mEditorSettings.fontSize);
    }

    public void decreaseFont() {
        mEditorSettings.fontSize -= 1;
        mEdit.setTextSize(mEditorSettings.fontSize);
    }

    /**
     * Set the extra keys bar
     */
    private void setExtraKeysBar() {
        if (!mEditorSettings.extraKeysBarEnabled) return;

        mEdit.setOnClickListener(v -> {
            MLog.d(TAG, "line touched at " + getCurrentCursorLine(mEdit.getEditableText()));
            if (listener != null) {
                listener.onLineTouched();
            }
        });

        // detect if key board is open or close
        final RelativeLayout rootView = v.findViewById(R.id.rootEditor);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

            if (heightDiff > 300) {
                // mExtraKeyBar.setVisibility(View.VISIBLE);
            } else {
                // mExtraKeyBar.setVisibility(View.GONE);
            }
        });


        // set all the extra buttons
        LinearLayout extraKeyBarLl = v.findViewById(R.id.extraKeyBarLl);
        for (int i = 0; i < extraKeyBarLl.getChildCount(); i++) {
            final Button b = (Button) extraKeyBarLl.getChildAt(i);

            b.setOnClickListener(v -> {
                String t = b.getText().toString();
                mEdit.getText().insert(mEdit.getSelectionStart(), t);
            });

        }
    }

    public static EditorFragment newInstance() {
        return newInstance(null);
    }

    public static EditorFragment newInstance(Bundle bundle) {
        EditorFragment myFragment = new EditorFragment();
        myFragment.setArguments(bundle);

        return myFragment;
    }

    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Toast.makeText(getActivity(), "HIDDEN? " + hidden, Toast.LENGTH_SHORT).show();
    }

    /**
     * Add a listner to know whe the cursor is
     */
    public void addListener(EditorFragmentListener listener) {
        this.listener = listener;
    }


    public void loadFile(String path, String name) {
        mCurrentFile = new ProtoFile(path, name);
        String filePath = mCurrentFile.getFullPath();

        String code = "";
        if (openedFiles.containsKey(filePath)) {
            code = openedFiles.get(filePath);
        } else {
            code = FileIO.loadStringFromFile(filePath);
            openedFiles.put(filePath, code);
        }

        mEdit.setText(code);
    }

    /**
     * Save the current project
     */
    public void saveFile() {
        PhonkScriptHelper.saveCodeFromAbsolutePath(mCurrentFile.getFullPath(), openedFiles.get(mCurrentFile.getFullPath()));
        // Toast.makeText(getActivity(), "Saving " + mCurrentFile.getFullPath() + "...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Toggle edittext editable
     */
    public void enableEdit(boolean b) {
        if (b) {
            mEdit.setFocusableInTouchMode(true);
        } else {
            mEdit.setFocusable(false);
        }
    }


    /**
     * Get current cursor line
     */
    public int getCurrentCursorLine(Editable editable) {
        int selectionStartPos = Selection.getSelectionStart(editable);

        // no selection
        if (selectionStartPos < 0) return -1;

        String preSelectionStartText = editable.toString().substring(0, selectionStartPos);
        return StringUtils.countMatches(preSelectionStartText, "\n");
    }

    /**
     * Get current code text
     */
    public String getCode() {
        return mEdit.getText().toString();
    }

    /**
     * Set the code text
     */
    public void setCode(String text) {
        mEdit.setText(text);
    }

    // load file in editor
    @Subscribe
    public void onEventMainThread(Events.EditorEvent e) {
        if (Events.EDITOR_FILE_LOAD.equals(e.getAction())) {
            ProtoFile f = e.getProtofile();
            loadFile(e.getProtofile().path, e.getProtofile().name);
        }
    }
}
