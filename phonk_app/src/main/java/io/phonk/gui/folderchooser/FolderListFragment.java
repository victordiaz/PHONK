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

package io.phonk.gui.folderchooser;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import io.phonk.R;
import io.phonk.gui._components.ResizableRecyclerView;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.BaseFragment;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Folder;

@SuppressLint("NewApi")
public class FolderListFragment extends BaseFragment {

    private String TAG = FolderListFragment.class.getSimpleName();

    private ResizableRecyclerView mFolderRecyclerView;
    private String currentParentFolder;
    private String currentFolder;

    private boolean isShown = true;
    private boolean mIsTablet = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.folderchooser_fragment, container, false);

        //this goes to the adapter
        ArrayList<FolderAdapterData> foldersForAdapter = new ArrayList<FolderAdapterData>();

        //get the user folder
        ArrayList<Folder> folders = PhonkScriptHelper.listFolders(PhonkSettings.USER_PROJECTS_FOLDER, true);
        foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_TITLE, PhonkSettings.USER_PROJECTS_FOLDER, "Playground"));

        MLog.d(TAG, "nn1 " + folders);
        for (Folder folder : folders) {
            foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_FOLDER_NAME, PhonkSettings.USER_PROJECTS_FOLDER, folder.getName()));
        }

        //get the examples folder
        ArrayList<Folder> examples = PhonkScriptHelper.listFolders(PhonkSettings.EXAMPLES_FOLDER, true);
        foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_TITLE, PhonkSettings.EXAMPLES_FOLDER, "Examples"));
        for (Folder folder : examples) {
            foldersForAdapter.add(new FolderAdapterData(FolderAdapterData.TYPE_FOLDER_NAME,  PhonkSettings.EXAMPLES_FOLDER, folder.getName()));
        }

        // Attach the adapter with the folders data
        mFolderRecyclerView = v.findViewById(R.id.folderList);
        mFolderRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFolderRecyclerView.setLayoutManager(linearLayoutManager);
        FolderChooserAdapter folderChooserAdapter = new FolderChooserAdapter(foldersForAdapter);
        mFolderRecyclerView.setAdapter(folderChooserAdapter);

        mIsTablet = getResources().getBoolean(R.bool.isTablet);
        LinearLayout llChooseFolder = v.findViewById(R.id.choosefolder);
        if (mIsTablet) llChooseFolder.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public static FolderListFragment newInstance(String folderName, boolean orderByName) {
        FolderListFragment myFragment = new FolderListFragment();

        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        args.putBoolean("orderByName", orderByName);
        myFragment.setArguments(args);

        return myFragment;
    }


}
