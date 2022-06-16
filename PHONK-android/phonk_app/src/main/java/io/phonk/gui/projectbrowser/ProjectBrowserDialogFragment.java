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

package io.phonk.gui.projectbrowser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import io.phonk.R;
import io.phonk.gui.projectbrowser.folderlist.FolderListFragment;
import io.phonk.gui.projectbrowser.projectlist.ProjectItem;
import io.phonk.gui.projectbrowser.projectlist.ProjectListFragment;
import io.phonk.gui.settings.PhonkSettings;

public class ProjectBrowserDialogFragment extends DialogFragment {

    private ProjectListFragment.ProjectSelectedListener mListener;
    private int mMode;

    public static ProjectBrowserDialogFragment newInstance(int mode) {
        ProjectBrowserDialogFragment f = new ProjectBrowserDialogFragment();
        Bundle args = new Bundle();
        args.putInt("mode", mode);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) mMode = getArguments().getInt("mode");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.project_browser_dialog_fragment, container);

        ProjectBrowserFragment projectBrowserFragment = ProjectBrowserFragment.newInstance(mMode);
        projectBrowserFragment.setProjectClickListener(mListener);

        TextView txtTitle = rootView.findViewById(R.id.txtTitleDialog);
        Button btnOk = rootView.findViewById(R.id.btnOk);
        Button btnClear = rootView.findViewById(R.id.btnClear);

        btnOk.setOnClickListener(view -> {
            mListener.onActionClicked("ok");
            dismiss();
        });

        btnClear.setOnClickListener(view -> {
            mListener.onActionClicked("clear");
            dismiss();
        });

        if (mMode == ProjectItem.MODE_SINGLE_PICK) {
            btnOk.setVisibility(View.GONE);
        } else if (mMode == ProjectItem.MODE_SINGLE_PICK_CLEAR) {
            btnOk.setVisibility(View.GONE);
            btnClear.setVisibility(View.VISIBLE);
        } else if (mMode == ProjectItem.MODE_MULTIPLE_PICK) {
            txtTitle.setText("Select one or more projects");
        }

        if (savedInstanceState == null) {
            FolderListFragment folderListFragment = FolderListFragment.newInstance(PhonkSettings.EXAMPLES_FOLDER, true);
            getChildFragmentManager().beginTransaction().add(R.id.project_browser_dialog_fragment_content, projectBrowserFragment).commit();
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view

        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
    }

    public void setListener(ProjectListFragment.ProjectSelectedListener listener) {
        mListener = listener;
    }
}