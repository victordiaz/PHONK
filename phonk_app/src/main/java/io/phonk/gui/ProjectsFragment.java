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
import io.phonk.helpers.ProtoScriptHelper;
import io.phonk.runner.models.Folder;

import java.util.ArrayList;


public class ProjectsFragment extends Fragment {

    private static final java.lang.String TAG = ProjectsFragment.class.getSimpleName();

    private final String CARD_COLOR = "#ff006f";

    private ResizableRecyclerView mProjectList;

    public ProjectsFragment() {
    }

    public static ProjectsFragment newInstance() {
        ProjectsFragment fragment = new ProjectsFragment();
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
        ArrayList<Folder> adapterData = ProtoScriptHelper.listFoldersOrdered(PhonkSettings.USER_PROJECTS_FOLDER + "/User Projects");

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

        return rootView;
    }

}