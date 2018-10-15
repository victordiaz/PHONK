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

import android.graphics.Rect;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.phonk.R;
import io.phonk.events.Events;
import io.phonk.runner.base.utils.MLog;

public class FolderChooserDialog extends DialogFragment {

    private String TAG = FolderChooserDialog.class.getSimpleName();

    public static FolderChooserDialog newInstance() {
        FolderChooserDialog dialogFragment = new FolderChooserDialog();
        return dialogFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folderchooser_dialog, null);

        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //window.setGravity(Gravity.TOP | Gravity.LEFT);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        int w = (int) (displayRectangle.width() * 0.95);
        int h = (int) (displayRectangle.height() * 0.9);
        window.setLayout(w, h);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        //params.x = 280;
        //params.y = 280;
        //params.height = 200;
        //params.dimAmount = 0f;


        window.setAttributes(params);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.dialogchooserfl, FolderListFragment.newInstance("", true));
        fragmentTransaction.commit();

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

    //folder choose
    @Subscribe
    public void onEventMainThread(Events.FolderChosen evt) {
        MLog.d(TAG, "< Event (folderChosen)");
        dismiss();
        //MLog.d(TAG, "event -> " + code);
    }

}

