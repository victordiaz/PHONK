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

package io.phonk.runner.apprunner.api.media;

import android.os.Build;
import android.os.Bundle;
import android.util.Rational;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraX;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import io.phonk.runner.R;
import io.phonk.runner.apprunner.AppRunner;
import io.phonk.runner.base.utils.MLog;

public class PCameraXOne extends Fragment {

  private static final String TAG = PCameraX.class.getSimpleName();

  private AppRunner mAppRunner;

  public PCameraXOne(AppRunner appRunner) {
    //  super(appRunner.getAppContext());
    this.mAppRunner = appRunner;
    MLog.d(TAG, "constructor");
    // this.setSurfaceTextureListener(this);
  }

  /**
   * Bind the Camera to the lifecycle
   */
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void bindCamera () {
    MLog.d(TAG, "bindCamera");
    MLog.d(TAG, "qq");

    CameraX.unbindAll();

    MLog.d(TAG, "qq1");

    /* start preview */
    int aspRatioW = 500;
    int aspRatioH = 500;
    Rational asp = new Rational (aspRatioW, aspRatioH); //aspect ratio
    Size screen = new Size(aspRatioW, aspRatioH); //size of the screen

    //config obj for preview/viewfinder thingy.
    PreviewConfig pConfig = new PreviewConfig.Builder().setTargetAspectRatio(asp).setTargetResolution(screen).build();

    MLog.d(TAG, "qq11");

    Preview preview = new Preview(pConfig);

    MLog.d(TAG, "qq12");

    // Handles the output data of the camera
    preview.setOnPreviewOutputUpdateListener(previewOutput -> {
      // ViewGroup parent = (ViewGroup) viewFinder.getParent();
      // parent.removeView(viewFinder);
      // parent.addView(viewFinder, 0);
      MLog.d(TAG, "qq2");


      // this.setSurfaceTexture(previewOutput.getSurfaceTexture());
      MLog.d(TAG, "qq3");

      // Integer rotation = getDisplaySurfaceRotation(viewFinder.getDisplay());
      // updateTransform(rotation, previewOutput.getTextureSize(), viewFinderDimens);
    });
    MLog.d(TAG, "qq4");

    /*
    viewFinder.addOnLayoutChangeListener((
      view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
      Size newViewFinderDimens = new Size(right - left, bottom - top);
      Integer rotation = getDisplaySurfaceRotation(viewFinder.getDisplay());
      updateTransform(rotation, bufferDimens, newViewFinderDimens);
    });
     */

    // Bind the camera to the lifecycle
    CameraX.bindToLifecycle((LifecycleOwner) this, preview);
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.camera_fragment, container, false);

    return root;
  }


  @Override
  public void onViewCreated(View view, Bundle bundle) {
    super.onViewCreated(view, bundle);

    TextureView viewFinder = getActivity().findViewById(R.id.view_finder);
    viewFinder.post(this::bindCamera);
    // view.post(this::startCamera);
    MLog.d(TAG, "starting camera");
  }
}
