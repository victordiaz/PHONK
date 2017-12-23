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

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.phonk.R;

public class FileManagerDialog extends DialogFragment {

    private String TAG = FileManagerDialog.class.getSimpleName();

    public static FileManagerDialog newInstance() {
        FileManagerDialog dialogFragment = new FileManagerDialog();
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folderchooser_dialog, null);

        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        int w = (int) (displayRectangle.width() * 0.95);
        int h = (int) (displayRectangle.height() * 0.9);
        window.setLayout(w, h);

        WindowManager.LayoutParams params = window.getAttributes();
        window.setAttributes(params);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        FileManagerFragment fmf = FileManagerFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString(FileManagerFragment.ROOT_FOLDER, "/sdcard/phonk_io/examples");
        fmf.setArguments(bundle);
        fragmentTransaction.add(R.id.dialogchooserfl, fmf);
        fragmentTransaction.commit();

        super.onActivityCreated(savedInstanceState);
    }

}

