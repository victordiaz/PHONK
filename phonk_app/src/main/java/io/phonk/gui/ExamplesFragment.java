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

/**
* Created by biquillo on 31/03/17.
*/

package io.phonk.gui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.phonk.R;
import io.phonk.gui._components.ResizableRecyclerView;
import io.phonk.gui.settings.PhonkSettings;
import io.phonk.helpers.PhonkScriptHelper;
import io.phonk.runner.base.utils.MLog;
import io.phonk.runner.models.Folder;

import java.util.ArrayList;


public class ExamplesFragment extends Fragment {

    private static final java.lang.String TAG = ExamplesFragment.class.getSimpleName();
    private final String CARD_COLOR = "#ff9900";

    private ResizableRecyclerView mProjectList;

    public ExamplesFragment() {
    }

    public static ExamplesFragment newInstance() {
        ExamplesFragment fragment = new ExamplesFragment();
        Bundle args = new Bundle();
        // args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.prueba_fragment_main, container, false);

        // get the folders
        // ArrayList<Folder> adapterData = PhonkScriptHelper.listFoldersOrdered(PhonkSettings.USER_PROJECTS_FOLDER + "/User Projects");
        ArrayList<Folder> folders = PhonkScriptHelper.listFoldersOrdered(PhonkSettings.EXAMPLES_FOLDER);
        ArrayList<Folder> adapterData = new ArrayList<>();

        for (Folder folder : folders) {
            MLog.d("1", folder.getFolder());
            MLog.d("1", folder.getName());
            MLog.d("2", folder.getFolderUrl());

            folder.setParent();
            adapterData.add(folder);
            ArrayList<Folder> subfolders = PhonkScriptHelper.listFoldersOrdered(folder.getFolder() + "/" + folder.getName());
            adapterData.addAll(subfolders);
        }
        MLog.d("qq5", "" + adapterData.size());

        CardView cardView = (CardView) rootView.findViewById(R.id.card);
        cardView.setCardBackgroundColor(Color.parseColor(CARD_COLOR));

        // Attach the adapter with the folders data
        mProjectList = (ResizableRecyclerView) rootView.findViewById(R.id.projectList);
        mProjectList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mProjectList.setLayoutManager(linearLayoutManager);
        ProjectsAdapter projectsAdapter = new ProjectsAdapter(getContext(), true);
        projectsAdapter.setArray(adapterData);
        mProjectList.setAdapter(projectsAdapter);

    /*
    TextView textView2 = (TextView) rootView.findViewById(R.id.section_label2);
    textView2.setY(100);
    textView2.animate().yBy(-100).setDuration(300).start();

    TextView textView = (TextView) rootView.findViewById(R.id.section_label);
    textView.setText("patata");
    */

        return rootView;
    }

}