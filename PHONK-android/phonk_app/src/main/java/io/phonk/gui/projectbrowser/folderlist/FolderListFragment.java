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

package io.phonk.gui.projectbrowser.folderlist;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.gui._components.ResizableRecyclerView;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.BaseFragment;
import io.phonk.runner.base.models.Folder;
import io.phonk.runner.base.models.Project;
import io.phonk.runner.base.utils.MLog;

@SuppressLint("NewApi")
public class FolderListFragment extends BaseFragment {

    final ArrayList<FolderAdapterData> foldersForAdapter = new ArrayList<>();
    private final String TAG = FolderListFragment.class.getSimpleName();
    private final boolean isShown = true;
    private ResizableRecyclerView mFolderRecyclerView;
    private FolderListAdapter folderListAdapter;
    private boolean mIsTablet = false;

    private ActionListener mListener;

    public static FolderListFragment newInstance(String folderName, boolean orderByName) {
        FolderListFragment myFragment = new FolderListFragment();

        Bundle args = new Bundle();
        args.putString("folderName", folderName);
        args.putBoolean("orderByName", orderByName);
        myFragment.setArguments(args);

        return myFragment;
    }

    public void setListener(ActionListener listener) {
        mListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.folderchooser_fragment, container, false);

        // this goes to the adapter
        listFolders(foldersForAdapter);

        // Attach the adapter with the folders data
        mFolderRecyclerView = v.findViewById(R.id.folderList);
        mFolderRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFolderRecyclerView.setLayoutManager(linearLayoutManager);
        folderListAdapter = new FolderListAdapter(foldersForAdapter);
        folderListAdapter.setListener(mListener);
        mFolderRecyclerView.setAdapter(folderListAdapter);

        mIsTablet = getResources().getBoolean(R.bool.isTablet);

        return v;
    }

    public void listFolders(ArrayList<FolderAdapterData> foldersForAdapter) {
        this.foldersForAdapter.clear();

        // get the user folder
        ArrayList<Folder> folders = PhonkScriptHelper.listFolders(PhonkSettings.USER_PROJECTS_FOLDER, true);

        if (!folders.isEmpty()) {
            this.foldersForAdapter.add(new FolderAdapterData(
                    FolderAdapterData.TYPE_TITLE,
                    PhonkSettings.USER_PROJECTS_FOLDER,
                    "Playground"
            ));
            for (Folder folder : folders) {
                this.foldersForAdapter.add(new FolderAdapterData(
                        FolderAdapterData.TYPE_FOLDER_NAME,
                        PhonkSettings.USER_PROJECTS_FOLDER,
                        folder.getName(),
                        folder.getNumSubfolders()
                ));
            }
        }

        // get the examples folder
        ArrayList<Folder> examples = PhonkScriptHelper.listFolders(PhonkSettings.EXAMPLES_FOLDER, true);
        this.foldersForAdapter.add(new FolderAdapterData(
                FolderAdapterData.TYPE_TITLE,
                PhonkSettings.EXAMPLES_FOLDER,
                "Examples"
        ));
        for (Folder folder : examples) {
            this.foldersForAdapter.add(new FolderAdapterData(
                    FolderAdapterData.TYPE_FOLDER_NAME,
                    PhonkSettings.EXAMPLES_FOLDER,
                    folder.getName(),
                    folder.getNumSubfolders()
            ));
        }
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Subscribe
    public void onEventMainThread(Events.ProjectEvent e) {
        MLog.d(TAG, "event -> " + e.getAction());

        String action = e.getAction();
        Project p = e.getProject();

        if (action.equals(Events.PROJECT_NEW)) {
            listFolders(foldersForAdapter);
            folderListAdapter.notifyDataSetChanged();
        } else if (action.equals(Events.PROJECT_DELETE)) {
            listFolders(foldersForAdapter);
            folderListAdapter.notifyDataSetChanged();
        }
    }

    public interface ActionListener {
        void onFolderSelected(String folder, String name);
    }

}
