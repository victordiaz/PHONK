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

package io.phonk.gui.filemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.runner.base.BaseFragment;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.Image;
import io.phonk.server.model.ProtoFile;

@SuppressLint("NewApi")
public class FilePreviewerFragment extends BaseFragment {
    protected static final String TAG = FilePreviewerFragment.class.getSimpleName();

    private View v;
    private RelativeLayout mImageContainer;
    private RelativeLayout mVideoContainer;
    private RelativeLayout mTextContainer;

    public static FilePreviewerFragment newInstance() {
        return newInstance(null);
    }

    public static FilePreviewerFragment newInstance(Bundle bundle) {
        FilePreviewerFragment myFragment = new FilePreviewerFragment();
        myFragment.setArguments(bundle);

        return myFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.filepreviewer_fragment, container, false);

        bindUI();

        /* get the code file */
        Bundle bundle = getArguments();
        if (bundle != null) {
            loadFile(new Project(bundle.getString(Project.FOLDER, ""), bundle.getString(Project.NAME, "")));
        }

        return v;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Bind the UI
     */
    private void bindUI() {
        mImageContainer = v.findViewById(R.id.preview_image);
        mVideoContainer = v.findViewById(R.id.preview_video);
        mTextContainer = v.findViewById(R.id.preview_text);
    }

    private void loadFile(Project project) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // load file in editor
    @Subscribe
    public void onEventMainThread(Events.EditorEvent e) {
        if (e.getAction().equals(Events.EDITOR_FILE_PREVIEW)) {
            ProtoFile f = e.getProtofile();
            String type = e.getPreviewType();

            unloadAll();

            switch (type) {
                case "image":
                    loadImage(f.getFullPath());
                    break;
                case "video":
                    loadVideo(f.path);
                    break;
                case "sound":
                    loadSound(f.path);
                    break;
            }
        }
    }

    private void unloadAll() {
        mVideoContainer.setVisibility(View.GONE);
        mImageContainer.setVisibility(View.GONE);
        mTextContainer.setVisibility(View.GONE);
    }

    private void loadImage(String file) {
        mImageContainer.setVisibility(View.VISIBLE);
        ImageView imageView = v.findViewById(R.id.imageView);

        imageView.setImageBitmap(Image.loadBitmap(file));
    }

    private void loadVideo(String name) {
    }

    private void loadSound(String name) {

    }

}
